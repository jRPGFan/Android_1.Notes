package com.example.notes.Data;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CardDataMapping {
    public static class NoteFields{
        public final static String TITLE = "title";
        public final static String CONTENTS = "contents";
        public final static String CREATION_DATE = "creation_date";
        public final static String HEADER_COLOR = "header_color";
    }

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.getDefault());

    public static Note convertToNote (String id, Map<String, Object> document) {
        Note note = new Note((String) document.get(NoteFields.TITLE),
                (String) document.get(NoteFields.CONTENTS),
                ((Timestamp) document.get(NoteFields.CREATION_DATE)).toDate(),
                (String) document.get(NoteFields.HEADER_COLOR));
        note.setId(id);
        return note;
    }
    public static Map<String, Object> convertToDocument (Note note){
        Map<String, Object> document = new HashMap<>();
        document.put(NoteFields.TITLE, note.getTitle());
        document.put(NoteFields.CONTENTS, note.getContents());
        document.put(NoteFields.CREATION_DATE, note.getCreationDate());
        document.put(NoteFields.HEADER_COLOR, note.getHeaderColor());
        return document;
    }
}
