package com.example.mininote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mininote.database.NoteCursorWarpper;
import com.example.mininote.database.NoteDatabaseHelper;
import com.example.mininote.database.NoteDbSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Date;

import static com.example.mininote.database.NoteDbSchema.NoteTable.Cols.*;

public class NotesLab {//负责数据持久化
    private static NotesLab sNotesLab;  //单例模式(应用能存活多久,单例就能存活多久// )
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private static final String TAG = "NoteLab";

    public static NotesLab getLab(Context context){//单例方法
        if (sNotesLab == null){
            sNotesLab = new NotesLab(context);
        }
        return sNotesLab;
    }

    private NotesLab(Context mContext){
        this.mContext = mContext.getApplicationContext();//此处上下文Context使用application
        //因为考虑到NotesLab的生命周期要贯穿整个app的生命周期,用activity作为上下文会导致用户在Activity间
        //来回切换时Activity的生命周期可能结束,但是使用Application作为context只要App还存活,Application就会存在.
        mDatabase = new NoteDatabaseHelper(mContext).getWritableDatabase();
    }
    public void addNote(Note note){
        ContentValues values = getContentValues(note);//负责处理数据库写入和更新操作的辅助类,键值储存类
        mDatabase.insert(NoteDbSchema.NoteTable.Name,null,values);
    }

    public void deleteNote(Note note){
        UUID id = note.getId();
        mDatabase.delete(NoteDbSchema.NoteTable.Name,NoteDbSchema.NoteTable.Cols.UUID+" = ?",new String[]{id.toString()});
    }
    public List<Note> getNotes(){
        List<Note> notes = new ArrayList<>();
        NoteCursorWarpper cursorWarpper = queryNotes(null,null);
        try{cursorWarpper.moveToFirst();
            while (!cursorWarpper.isAfterLast()){
                notes.add(cursorWarpper.getNote());
                cursorWarpper.moveToNext();
            }
        }finally{
            cursorWarpper.close();
        }
        return notes;
    }

    public Note getNote(UUID uuid){
        NoteCursorWarpper cursorWarpper = queryNotes(NoteDbSchema.NoteTable.Cols.UUID+" = ?",
                new String[]{uuid.toString()});
        try{
            if (cursorWarpper.getCount() == 0){
                return null;
            }
            cursorWarpper.moveToFirst();
            return cursorWarpper.getNote();
        }finally {
            cursorWarpper.close();
        }
    }

    public void updateNote(Note note){
        String uuidString = note.getId().toString();
        ContentValues values = getContentValues(note);
        mDatabase.update(NoteDbSchema.NoteTable.Name,values, NoteDbSchema.NoteTable.Cols.UUID+" = ?",new String[]{uuidString});
        //用String[]解决String本身就会带有SQL代码的问题
    }

    private NoteCursorWarpper queryNotes(String whereCalues , String[] whereArgs){//封装查询代码,第一个参数为列名,第二个是需要查询的值
        Cursor cursor = mDatabase.query(NoteDbSchema.NoteTable.Name,
                null,
                whereCalues,
                whereArgs,
                null,
                null,
                null
                );
        return new NoteCursorWarpper(cursor);
    }

    private ContentValues getContentValues(Note note) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UUID,note.getId().toString());
        contentValues.put(TITLE,note.getmTitle());
        contentValues.put(SECONDLINE,note.getSecondLine());
        contentValues.put(DATE,note.getDate().getTime());
        contentValues.put(NOTIDATE,note.getNotiDate().getTime());
        contentValues.put(CONTENT,note.getContent());
        contentValues.put(SETNOTI,note.getSetNoti());
        return contentValues;
    }

    public File getPhotoFile(Note note){//定位照片文件
        File fileDir = mContext.getFilesDir();//打开data/data/包名/files目录
        return new File(fileDir , note.getPicFileName());//不会创建文件,返回一个指向具体位置的File对象
    }


}
