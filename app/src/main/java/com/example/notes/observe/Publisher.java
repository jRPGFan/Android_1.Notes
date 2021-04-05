package com.example.notes.observe;

import com.example.notes.Data.Note;

import java.util.ArrayList;
import java.util.List;

public class Publisher {
    private List<Observer> observers;

    public Publisher(){
        observers = new ArrayList<>();
    }

    public void subscribe(Observer observer){
        observers.add(observer);
    }

    public void unsubscribe(Observer observer){
        observers.remove(observer);
    }

    public void unsubscribeAll(){
        for (Observer observer : observers)
            unsubscribe(observer);
    }

    public void notify(Note noteData){
        for (Observer observer : observers){
            observer.updateNoteData(noteData);
            unsubscribe(observer);
        }
    }
}
