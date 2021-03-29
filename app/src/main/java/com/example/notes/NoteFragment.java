package com.example.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class NoteFragment extends Fragment {
    static final String SELECTED_NOTE = "Selected_Note";
    private Note note;

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
        TextView tvNoteTitle = view.findViewById(R.id.note_title);
        TextView tvNoteContents = view.findViewById(R.id.note_contents);
        TextView tvNoteCreationDate = view.findViewById(R.id.note_creation_date);

        tvNoteTitle.setText(note.getTitle());
        tvNoteTitle.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Edit Title", Toast.LENGTH_SHORT).show();
        });

        tvNoteContents.setText(note.getContents());
        tvNoteContents.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Edit Items", Toast.LENGTH_SHORT).show();
        });

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        tvNoteCreationDate.setText(String.format("%s", simpleDateFormat.format(note.getCreationDate().getTime())));
        tvNoteCreationDate.setOnClickListener(v -> {
            changeNoteDate(note);
        });

        return view;
    }

    public static NoteFragment newInstance(Note note) {
        NoteFragment noteFragment = new NoteFragment();
        Bundle argsBundle = new Bundle();
        argsBundle.putParcelable(SELECTED_NOTE, note);
        noteFragment.setArguments(argsBundle);
        return noteFragment;
    }

    private void changeNoteDate(Note selectedNote) {
        Toast.makeText(getActivity(), "Change Date", Toast.LENGTH_SHORT).show();
    }
}
