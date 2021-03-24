package com.example.notes.ui;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.Data.CardSource;
import com.example.notes.Data.CardSourceImplementation;
import com.example.notes.Data.Note;
import com.example.notes.MainActivity;
import com.example.notes.R;
import com.example.notes.observe.Publisher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class NotesListFragment extends Fragment {
    private boolean isLandscape;
    private CardSource notes;
    private NotesAdapter adapter;
    private RecyclerView recyclerView;
    private Publisher publisher;
    private Note selectedNote;
    private SimpleDateFormat simpleDateFormat;
    private boolean moveToLastPosition;

    public static NotesListFragment newInstance() {
        return new NotesListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notes = new CardSourceImplementation(getResources()).init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notes_list_fragment_layout, container, false);
        initView(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity)context;
        publisher = activity.getPublisher();
    }

    @Override
    public void onDetach() {
        publisher = null;
        super.onDetach();
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.notes_list_recycler_view);
        initRecyclerView(recyclerView);
    }

    @SuppressLint("RtlHardcoded")
    private void initRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new NotesAdapter(notes, this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                Objects.requireNonNull(getContext()), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(
                ContextCompat.getDrawable(getContext(), R.drawable.card_separator)));
        recyclerView.addItemDecoration(dividerItemDecoration);

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(100);
        animator.setRemoveDuration(100);
        recyclerView.setItemAnimator(animator);

        if (moveToLastPosition) {
            recyclerView.smoothScrollToPosition(notes.size() - 1);
            moveToLastPosition = false;
        }

        adapter.SetOnItemClickListener((view, position) -> showNote(notes.getNoteData(position)));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(NoteFragment.SELECTED_NOTE, selectedNote);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null)
            selectedNote = savedInstanceState.getParcelable(NoteFragment.SELECTED_NOTE);
            else selectedNote = notes.getNoteData(0);

        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (isLandscape) showNoteInLandscape(selectedNote);
    }

    private void showNote(Note selectedNote) {
        if (isLandscape) showNoteInLandscape(selectedNote);
            else showNoteInPortrait(selectedNote);
    }

    private void showNoteInLandscape(Note selectedNote) {
        NoteFragment noteFragment = NoteFragment.newInstance(selectedNote);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.note_layout, noteFragment).
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    private void showNoteInPortrait(Note selectedNote) {
        NoteFragment noteFragment = NoteFragment.newInstance(selectedNote);
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack("notes_list_fragment");
        fragmentTransaction.replace(R.id.notes_list_container, noteFragment).
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }
}
