package edu.temple.lab7.dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import edu.temple.lab7.Book;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "book_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(Book.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Book.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertNote(int ids, String COLUMN_Author, String COLUMN_DURATION, String COLUMN_PUBLISHER, String image, String COLUMN_TITLE) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Book.COLUMN_Author, COLUMN_Author);
        values.put(Book.COLUMN_ID, ids);
        values.put(Book.COLUMN_DURATION, COLUMN_DURATION);
        values.put(Book.COLUMN_PUBLISHER, COLUMN_PUBLISHER);
        values.put(Book.COLUMN_IMAGE, image);
        values.put(Book.COLUMN_TITLE, COLUMN_TITLE);

        // insert row
        long id = db.insert(Book.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Book getNote(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Book.TABLE_NAME,
                new String[]{Book.COLUMN_ID, Book.COLUMN_Author, Book.COLUMN_DURATION, Book.COLUMN_PUBLISHER, Book.COLUMN_TITLE, Book.COLUMN_IMAGE, Book.COLUMN_TIMESTAMP},
                Book.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Book note = new Book(
                cursor.getInt(cursor.getColumnIndex(Book.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Book.COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(Book.COLUMN_Author)),
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(Book.COLUMN_PUBLISHER))),
                cursor.getString(cursor.getColumnIndex(Book.COLUMN_IMAGE)),
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(Book.COLUMN_DURATION))));

        // close the db connection
        cursor.close();

        return note;
    }

    public List<Book> getAllNotes() {
        List<Book> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Book.TABLE_NAME + " ORDER BY " +
                Book.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Book note = new Book();
                note.setId(cursor.getInt(cursor.getColumnIndex(Book.COLUMN_ID)));
                note.setAuthor(cursor.getString(cursor.getColumnIndex(Book.COLUMN_Author)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(Book.COLUMN_TITLE)));
                note.setPublished(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Book.COLUMN_PUBLISHER))));
                note.setDuration(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Book.COLUMN_DURATION))));
                note.setCoverURL(cursor.getString(cursor.getColumnIndex(Book.COLUMN_IMAGE)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + Book.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateNote(Book note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Book.COLUMN_ID, note.getId());
        values.put(Book.COLUMN_TITLE, note.getTitle());
        values.put(Book.COLUMN_DURATION, note.getDuration());
        values.put(Book.COLUMN_Author, note.getAuthor());
        values.put(Book.COLUMN_PUBLISHER, note.getPublished());
        values.put(Book.COLUMN_IMAGE, note.getCoverURL());

        // updating row
        return db.update(Book.TABLE_NAME, values, Book.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Book.TABLE_NAME, Book.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }
}