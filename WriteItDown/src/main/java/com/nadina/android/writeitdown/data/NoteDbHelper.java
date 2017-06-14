package com.nadina.android.writeitdown.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nadina.android.writeitdown.data.NoteContract.NoteEntry;

/**
 * Created by Nadina on 15.03.2017.
 */

public class NoteDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "note.db";
    private static final int DATABASE_VERSION = 1;

    public NoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_NOTE_TABLE = " CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
                NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NoteEntry.NOTE + " TEXT NOT NULL, " +
                NoteEntry.DATE + " DATE NOT NULL "
                + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_NOTE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
