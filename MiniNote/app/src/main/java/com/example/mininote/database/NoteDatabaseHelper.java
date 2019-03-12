package com.example.mininote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mininote.database.NoteDbSchema.NoteTable;

public class NoteDatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final  String DATABASE_NAME = "NotesDatabase.db";
    public NoteDatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + NoteTable.Name +"("+
                "_id integer primary key autoincrement, "+
                        NoteTable.Cols.UUID+","+
                        NoteTable.Cols.DATE+","+
                        NoteTable.Cols.NOTIDATE+","+
                        NoteTable.Cols.SECONDLINE+","+
                        NoteTable.Cols.TITLE+","+
                        NoteTable.Cols.SETNOTI+","+
                        NoteTable.Cols.CONTENT+
                ")"
                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
