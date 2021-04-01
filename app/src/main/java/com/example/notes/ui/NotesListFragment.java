package com.example.notes.ui;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

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
import com.example.notes.Data.CardSourceFirebaseImplementation;
import com.example.notes.Data.CardSourceImplementation;
import com.example.notes.Data.CardsSourceResponse;
import com.example.notes.Data.Note;
import com.example.notes.MainActivity;
import com.example.notes.R;
import com.example.notes.observe.Observer;
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
    private EditNoteFragment editNoteFragment;
//    private boolean moveToLastPosition;
    private boolean moveToFirstPosition;

    public static NotesListFragment newInstance() {
        return new NotesListFragment();
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (notes == null) notes = new CardSourceImplementation(getResources()).init();
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notes_list_fragment_layout, container, false);
        initView(view);
        setHasOptionsMenu(true);
        notes = new CardSourceFirebaseImplementation().init(cardData ->
                adapter.notifyDataSetChanged());
        adapter.setCardSource(notes);
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
//        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new NotesAdapter(this);
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

//        if (moveToLastPosition) {
//            recyclerView.smoothScrollToPosition(notes.size() - 1);
//            moveToLastPosition = false;
//        }

        if (moveToFirstPosition && notes.size() > 0){
            recyclerView.scrollToPosition(0);
            moveToFirstPosition = false;
        }

        adapter.SetOnItemClickListener((view, position) -> {
            showNote(notes.getNoteData(position));
            publisher.subscribe(noteData -> {
                notes.updateNoteData(position, noteData);
                adapter.notifyItemChanged(position);
                moveToFirstPosition = false;
            });

        });
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
            else if (notes != null && adapter.getItemCount() > 0) selectedNote = notes.getNoteData(0);

        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (isLandscape) showNoteInLandscape(selectedNote);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.toolbar_menu_add_note:
//                EditNoteFragment editNoteFragment = EditNoteFragment.newInstance();
//                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                boolean isPortrait = getResources().getConfiguration().orientation ==
//                        Configuration.ORIENTATION_PORTRAIT;
//                if (isPortrait) {
//                    fragmentTransaction.addToBackStack("note_fragment");
//                    fragmentTransaction.replace(R.id.notes_list_container, editNoteFragment).
//                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
//                } else {
//                    fragmentTransaction.addToBackStack("note_layout");
//                    fragmentTransaction.replace(R.id.note_layout, editNoteFragment).
//                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
//                }
//                publisher.subscribe(noteData -> {
//                    notes.addNoteData(noteData);
//                    adapter.notifyItemInserted(notes.size() - 1);
//                    moveToLastPosition = true;
//                });
//                adapter.notifyItemInserted(adapter.getItemCount() - 1);
//                return true;
//            default:
//                Toast.makeText(getActivity(), item.getTitle().toString(), Toast.LENGTH_SHORT).show();
//        }

        return onItemSelected(item.getItemId()) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = requireActivity().getMenuInflater();
        menuInflater.inflate(R.menu.note_context_menu, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        final int position = adapter.getMenuPosition();
//        switch (item.getItemId()) {
//            case R.id.edit_note:
//                EditNoteFragment editNoteFragment = EditNoteFragment.newInstance(notes.getNoteData(position));
//                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                boolean isPortrait = getResources().getConfiguration().orientation ==
//                        Configuration.ORIENTATION_PORTRAIT;
//                if (isPortrait) {
//                    fragmentTransaction.addToBackStack("note_fragment");
//                    fragmentTransaction.replace(R.id.notes_list_container, editNoteFragment).
//                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
//                } else {
//                    fragmentTransaction.addToBackStack("note_layout");
//                    fragmentTransaction.add(R.id.note_layout, editNoteFragment).
//                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
//                }
//                publisher.subscribe(noteData -> {
//                    notes.updateNoteData(position, noteData);
//                    adapter.notifyItemChanged(position);
//                    moveToLastPosition = false;
//                });
//
//                return true;
//            case R.id.delete_note:
//                notes.deleteNoteData(position);
//                adapter.notifyItemRemoved(position);
//                return true;
//            default:
//                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
//        }
        return onItemSelected(item.getItemId()) || super.onContextItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    private boolean onItemSelected(int menuItemId) {
        final int position = adapter.getMenuPosition();
        switch (menuItemId){
            case R.id.toolbar_menu_add_note:
                editNoteFragment = EditNoteFragment.newInstance();
                createEditNoteFragment();

                publisher.subscribe(noteData -> {
                    notes.addNoteData(noteData);
                    adapter.notifyItemInserted(notes.size() - 1);
                    moveToFirstPosition = true;
                });
                adapter.notifyItemInserted(adapter.getItemCount() - 1);
                return true;

            case R.id.edit_note:
                editNoteFragment = EditNoteFragment.newInstance(notes.getNoteData(position));
                createEditNoteFragment();
                publisher.subscribe(noteData -> {
                    notes.updateNoteData(position, noteData);
                    adapter.notifyItemChanged(position);
                    moveToFirstPosition = false;
                });
                return true;

            case R.id.delete_note:
                notes.deleteNoteData(position);
                adapter.notifyItemRemoved(position);
                return true;

            default:
                Toast.makeText(getActivity(), "Boop. No reply, it's just a button.", Toast.LENGTH_SHORT).show();
            }

        return false;
    }

    private void createEditNoteFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        boolean isPortrait = getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT;
        if (isPortrait) {
            fragmentTransaction.addToBackStack("note_fragment");
            fragmentTransaction.replace(R.id.notes_list_container, editNoteFragment).
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        } else {
            fragmentTransaction.addToBackStack("note_layout");
            fragmentTransaction.add(R.id.note_layout, editNoteFragment).
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        }
    }

    private void showNote(Note selectedNote) {
        if (isLandscape) showNoteInLandscape(selectedNote);
            else showNoteInPortrait(selectedNote);
    }

    private void showNoteInLandscape(Note selectedNoten) {
        if (selectedNote == null) return;;
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
