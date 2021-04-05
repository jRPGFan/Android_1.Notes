package com.example.notes.Data;

public interface CardSource {
    CardSource init(CardsSourceResponse cardsSourceResponse);
    Note getNoteData(int position);
    int getSize();
    void deleteNoteData(int position);
    void updateNoteData(int position, Note noteData);
    void addNoteData(Note noteData);
    void clearNoteData();
}
