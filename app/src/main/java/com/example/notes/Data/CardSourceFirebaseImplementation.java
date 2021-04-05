package com.example.notes.Data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardSourceFirebaseImplementation implements CardSource{

    private static final String CARDS = "cards";
    private static final String TAG = "CardSourceFirebaseImplementation";

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection(CARDS);
    private List<Note> notes = new ArrayList<>();

    @Override
    public CardSource init(CardsSourceResponse cardsSourceResponse) {
        collectionReference.orderBy(CardDataMapping.NoteFields.CREATION_DATE,
                Query.Direction.DESCENDING).get().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        notes = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Map<String, Object> document = queryDocumentSnapshot.getData();
                            String id = queryDocumentSnapshot.getId();
                            Note note = CardDataMapping.convertToNote(id, document);
                            notes.add(note);
                        }
                        Log.d(TAG, "Successfully loaded " + notes.size() + " notes");
                        cardsSourceResponse.initialized(CardSourceFirebaseImplementation.this);
                    } else {
                        Log.d(TAG, "Failed to load. Reason: " + task.getException());
                    }
                }).addOnFailureListener(e -> Log.d(TAG, "Exception: " + e));
        return this;
    }

    @Override
    public Note getNoteData(int position) {
        return notes.get(position);
    }

    @Override
    public int getSize() {
//        if (notes == null) return 0;
//        return notes.size();
        return notes.size();
    }

    @Override
    public void deleteNoteData(int position) {
        collectionReference.document(notes.get(position).getId()).delete();
        notes.remove(position);
    }

    @Override
    public void updateNoteData(int position, Note noteData) {
        String id = noteData.getId();
        collectionReference.document(id).set(CardDataMapping.convertToDocument(noteData));
    }

    @Override
    public void addNoteData(Note noteData) {
        collectionReference.add(CardDataMapping.convertToDocument(noteData)).
                addOnSuccessListener(documentReference -> noteData.setId(documentReference.getId()));
    }

    @Override
    public void clearNoteData() {
        for (Note note : notes) collectionReference.document(note.getId()).delete();
        notes = new ArrayList<>();
    }
}
