package com.example.notes.Data;

import com.example.notes.Data.Note;

public interface CardSource {
    Note getNoteData(int position);
    int size();
    void deleteNoteData(int position);
    void updateNoteData(int position, Note noteData);
    void addNoteData(Note noteData);
    void clearNoteData();
}
