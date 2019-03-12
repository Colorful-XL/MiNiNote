package com.example.mininote.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import com.example.mininote.Note;

import java.util.Date;
import java.util.UUID;

public class NoteCursorWarpper extends CursorWrapper {
    private static final String TAG = "CursorWrapper";
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public NoteCursorWarpper(Cursor cursor) {
        super(cursor);
    }
    public Note getNote(){
        String uuidString = getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.UUID));
        String title = getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.TITLE));
        long date = getLong(getColumnIndex(NoteDbSchema.NoteTable.Cols.DATE));
        long notidate = getLong(getColumnIndex(NoteDbSchema.NoteTable.Cols.NOTIDATE));
        String secondLine = getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.SECONDLINE));
        String content = getString(getColumnIndex(NoteDbSchema.NoteTable.Cols.CONTENT));
        int setNoti = getInt(getColumnIndex(NoteDbSchema.NoteTable.Cols.SETNOTI));
        Note note = new Note(UUID.fromString(uuidString));
        note.setSecondLine(secondLine);
        note.setDate(new Date(date));
        note.setNotiDate(new Date(notidate));
        note.setmTitle(title);
        note.setContent(content);
        note.setSetNoti(setNoti);
        Log.d(TAG, "getNote():Noti--"+setNoti);
        return note;
    }
}
