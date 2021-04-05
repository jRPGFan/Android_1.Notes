package com.example.notes.ui;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.notes.Data.Note;
import com.example.notes.R;
import com.example.notes.observe.OnCreateEditNoteDialogListener;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class NoteFragment extends Fragment {
    static final String SELECTED_NOTE = "Selected_Note";
    private Note note;

    private TextView tvNoteTitle;
    private TextView tvNoteContents;
    private TextView tvNoteCreationDate;
    private ImageButton editNote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            note = getArguments().getParcelable(SELECTED_NOTE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_fragment_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tvNoteTitle = view.findViewById(R.id.note_title);
        tvNoteContents = view.findViewById(R.id.note_contents);
        tvNoteCreationDate = view.findViewById(R.id.note_creation_date);
        editNote = view.findViewById(R.id.edit_note);

        tvNoteTitle.setText(note.getTitle());
        tvNoteTitle.setBackgroundColor(Color.parseColor(note.getHeaderColor()));
        tvNoteContents.setText(note.getContents());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        tvNoteCreationDate.setText(String.format("%s", simpleDateFormat.format(
                note.getCreationDate().getTime())));
        editNote.setOnClickListener(v -> showNoteEdit(note));
    }


    private void showNoteEdit(Note selectedNote) {
        EditNoteDialog editNoteDialog = EditNoteDialog.newInstance(selectedNote);
        editNoteDialog.setOnDialogListener(new OnCreateEditNoteDialogListener() {
            @Override
            public void onDialogSave() {
                Fragment currentFragment = getActivity().getSupportFragmentManager().
                        findFragmentById(R.id.notes_list_container);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.detach(currentFragment);
                fragmentTransaction.attach(currentFragment);
                fragmentTransaction.commit();

                Toast.makeText(getActivity(), "Note Edited",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDialogCancel() {
                Toast.makeText(getActivity(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        });
        editNoteDialog.show(Objects.requireNonNull(getActivity()).
                        getSupportFragmentManager(),
                "create_edit_note_dialog_fragment");

//        EditNoteFragment editNoteFragment = EditNoteFragment.newInstance(selectedNote);
//        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        boolean isPortrait = getResources().getConfiguration().orientation ==
//                Configuration.ORIENTATION_PORTRAIT;
//        if (isPortrait) {
//            fragmentTransaction.addToBackStack("note_fragment");
//            fragmentTransaction.replace(R.id.notes_list_container, editNoteFragment).
//                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
//        } else {
//            fragmentTransaction.addToBackStack("note_layout");
//            fragmentTransaction.replace(R.id.note_layout, editNoteFragment).
//                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
//        }
    }

    public static NoteFragment newInstance(Note note) {
        NoteFragment noteFragment = new NoteFragment();
        Bundle argsBundle = new Bundle();
        argsBundle.putParcelable(SELECTED_NOTE, note);
        noteFragment.setArguments(argsBundle);
        return noteFragment;
    }
}
