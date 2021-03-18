package com.example.notes;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotesListFragment extends Fragment {
    private boolean isLandscape;
    private Note[] notes;
    private Note selectedNote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notes_list_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initNoteList(view);
    }

//    final Calendar myCalendar = Calendar.getInstance();
//    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
//
//        @Override
//        public void onDateSet(DatePicker view, int year, int monthOfYear,
//                              int dayOfMonth) {
//            // TODO Auto-generated method stub
//            myCalendar.set(Calendar.YEAR, year);
//            myCalendar.set(Calendar.MONTH, monthOfYear);
//            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//        }
//
//    };

    @SuppressLint({"ResourceType", "RtlHardcoded"})
    public void initNoteList(View view) {
        TypedArray titlesArray = getResources().obtainTypedArray(R.array.note_titles);
        TypedArray contentsArray = getResources().obtainTypedArray(R.array.note_contents);
        TypedArray datesArray = getResources().obtainTypedArray(R.array.note_creation_dates);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        notes = new Note[] {
                    new Note(titlesArray.getString(0),
                            contentsArray.getString(0),
                            getDateFromString(datesArray.getString(0), simpleDateFormat)),

                    new Note(titlesArray.getString(1),
                            contentsArray.getString(1),
                            getDateFromString(datesArray.getString(1), simpleDateFormat)),

                    new Note(titlesArray.getString(2),
                            contentsArray.getString(2),
                            getDateFromString(datesArray.getString(2), simpleDateFormat))
        };

        for (Note note : notes) {
            Context context = getContext();
            if (context != null) {
                LinearLayout linearLayout = (LinearLayout) view;
                TextView tvNoteTitle = new TextView(context);
                TextView tvNoteShortContents = new TextView(context);
                TextView tvNoteCreationDate = new TextView(context);

                tvNoteTitle.setText(note.getTitle());
                tvNoteTitle.setTextSize(18);

                String noteContents = note.getContents();
                tvNoteShortContents.setText(noteContents.length() > 30 ?
                        noteContents.substring(0, 27) + "..." : noteContents);
                tvNoteShortContents.setTextSize(14);

                tvNoteCreationDate.setText(simpleDateFormat.format(note.getCreationDate().getTime()));
                tvNoteCreationDate.setTextSize(12);

                linearLayout.addView(tvNoteTitle);
                linearLayout.addView(tvNoteShortContents);
                linearLayout.addView(tvNoteCreationDate);

                tvNoteTitle.setPadding(0, 30, 0, 0);
                tvNoteShortContents.setPadding(0,5,0,0);
                tvNoteShortContents.setOnClickListener(v -> {
                    selectedNote = note;
                    showNote(selectedNote);
                });
                tvNoteCreationDate.setGravity(Gravity.RIGHT);
                tvNoteCreationDate.setOnClickListener(v -> {
                    selectedNote = note;
                    changeNoteDate(selectedNote);
                });
                linearLayout.setPaddingRelative(0,20,0,0);
            }
        }
        titlesArray.recycle();
        contentsArray.recycle();
        datesArray.recycle();
    }

    private void changeNoteDate(Note selectedNote) {
        //new DatePickerDialog()
    }


    private void showNote(Note selectedNote) {
        if (isLandscape) {
            showNoteInLandscape(selectedNote);
        } else {
            showNoteInPortrait(selectedNote);
        }
    }

    private void showNoteInLandscape(Note selectedNote) {
        NoteFragment noteFragment = NoteFragment.newInstance(selectedNote);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.note_layout, noteFragment).
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    private void showNoteInPortrait(Note selectedNote) {
        Intent intent = new Intent(getActivity(), NoteActivity.class);
        intent.putExtra(NoteFragment.SELECTED_NOTE, selectedNote);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(NoteFragment.SELECTED_NOTE, selectedNote);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            selectedNote = savedInstanceState.getParcelable(NoteFragment.SELECTED_NOTE);
        }

            else {
                selectedNote = notes[0];
            }

        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (isLandscape) {
            showNoteInLandscape(selectedNote);
        }
    }

    public Date getDateFromString(String dateString, SimpleDateFormat simpleDateFormat){
        Date date = new Date();
        try{
            date = simpleDateFormat.parse(dateString);
            return date;
        } catch (ParseException ex){
            ex.printStackTrace();
        }
        return date;
    }
}
