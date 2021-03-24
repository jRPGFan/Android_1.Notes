package com.example.notes.Data;

import android.content.res.Resources;
import android.content.res.TypedArray;

import com.example.notes.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CardSourceImplementation implements CardSource {
    private List<Note> dataSource;
    private Resources resources;

    public CardSourceImplementation(Resources resources){
        dataSource = new ArrayList<>();
        this.resources = resources;
    }

    public CardSourceImplementation init(){
        String[] titles = resources.getStringArray(R.array.note_titles);
        String[] contents = resources.getStringArray(R.array.note_contents);
        String[] creationDates = resources.getStringArray(R.array.note_creation_dates);
        //String[] headerColors = resources.obtainTypedArray(R.array.note_header_colors);
        String[] headerColors = getColors();


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        for (int i = 0; i < titles.length; i++) {
            dataSource.add(new Note(titles[i], contents[i],
                    getDateFromString(creationDates[i], simpleDateFormat), headerColors[i]));
        }

        return this;
    }

    private String[] getColors() {
        TypedArray headerColorsArray = resources.obtainTypedArray(R.array.note_header_colors);
        String[] colors = new String[headerColorsArray.length()];
        for (int i = 0; i<headerColorsArray.length(); i++)
            colors[i] = headerColorsArray.getString(i);
        headerColorsArray.recycle();
        return colors;
    }

    @Override
    public Note getNoteData(int position) {
        return dataSource.get(position);
    }

    @Override
    public int size() {
        return dataSource.size();
    }

    @Override
    public void deleteNoteData(int position) {
        dataSource.remove(position);
    }

    @Override
    public void updateNoteData(int position, Note noteData) {
        dataSource.set(position, noteData);
    }

    @Override
    public void addNoteData(Note noteData) {
        dataSource.add(noteData);
    }

    @Override
    public void clearNoteData() {
        dataSource.clear();
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
