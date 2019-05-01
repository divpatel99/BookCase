package edu.temple.lab7;

import java.io.Serializable;

public class Book implements Serializable {

    public static final String TABLE_NAME = "books";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_Author = "author";
    public static final String COLUMN_PUBLISHER = "publisher";
    public static final String COLUMN_IMAGE = "coverurl";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_Author + " TEXT,"
                    + COLUMN_PUBLISHER + " TEXT,"
                    + COLUMN_DURATION + " TEXT,"
                    + COLUMN_IMAGE + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    int id;
    String title;
    String author;
    int published;
    String coverURL;
    int duration;

    public Book() {
    }

    public Book(int id, String title, String author, int published, String coverURL, int duration) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.published = published;
        this.coverURL = coverURL;
        this.duration =duration;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPublished() {
        return published;
    }

    public void setPublished(int published) {
        this.published = published;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
