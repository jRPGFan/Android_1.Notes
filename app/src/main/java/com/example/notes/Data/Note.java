package com.example.notes.Data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Note implements Parcelable {
    private String id;
    private String title;
    private String contents;
    private Date creationDate;
    private String headerColor;

    public Note(){

    }
    public Note (String title, String contents, Date creationDate, String headerColor){
        this.title = title;
        this.contents = contents;
        this.creationDate = creationDate;
        this.headerColor = headerColor;
    }

    protected Note(Parcel in) {
        title = in.readString();
        contents = in.readString();
        creationDate = new Date(in.readLong());
        headerColor = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(contents);
        out.writeLong(creationDate.getTime());
        out.writeString(headerColor);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getHeaderColor() { return headerColor; }

    public void setHeaderColor(String headerColor) { this.headerColor = headerColor; }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }
}
