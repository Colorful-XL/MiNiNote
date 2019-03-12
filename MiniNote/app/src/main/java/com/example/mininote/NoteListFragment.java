package com.example.mininote;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class NoteListFragment extends Fragment {
private RecyclerView mRecyclerView;
private NoteAdapter mAdapter;
private FloatingActionButton addfloatbutton;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstRun();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list,container,false);

        mRecyclerView = view.findViewById(R.id.note_recycleView);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        /*没有LayoutManager的支持不仅RecycleView无法工作,还会导致应用崩溃,RecycleView视图创建完成后就立马转交给
        LayoutManager对象. RecycleView类不会亲自摆放屏幕上的列表项,实际上,摆放的任务被委托给了LayoutManger.
        除了在屏幕上摆放列表项,LayoutManager还负责定义屏幕滚动行为,LinearLayoutManager是竖直列表式排列,
        也可以使用Grid等以其他方式显示列表项
         */
        addfloatbutton = view.findViewById(R.id.id_icon_add);
        addfloatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = new Note();
                UUID noteId = note.getId();
                Date date = new Date(System.currentTimeMillis());
                note.setDate(date);
                NotesLab.getLab(getActivity()).addNote(note);
                Intent intent = NotePagerActivity.newIntent(getContext(), noteId);
                startActivity(intent);
            }
        });
        upDateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        upDateUI();
    }

    private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener{//在Holder类中实现组件绑定并放入构造方法,因为holder会循环使用
        private TextView mTitleText;
        private TextView mContentText;
        private TextView mTimeText;
        private ImageView mImageView;

        private Note mNote;
        private File mPhotoFile;
        public NoteHolder(LayoutInflater inflater , ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_note , parent , false));
            //首先实例化布局,然后将其传入到super也就是ViewHolder的构造方法中,也就是VIewHolder的构造方法
            //基类ViewHolder因此实际引用这个视图
            itemView.setOnClickListener(this);
            mTitleText = itemView.findViewById(R.id.list_title);
            mContentText = itemView.findViewById(R.id.list_content);
            mTimeText = itemView.findViewById(R.id.list_time);
            mImageView = itemView.findViewById(R.id.list_photo);

            //itemView作为ViewHolder的View
        }
        public void bind(Note note){//
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm" );
            mNote = note;
            mTitleText.setText(mNote.getmTitle());
            mContentText.setText(mNote.getSecondLine());
            mTimeText.setText(simpleDateFormat.format(mNote.getDate()));
            mPhotoFile = NotesLab.getLab(getActivity()).getPhotoFile(mNote);
            updatePhotoView();
        }
        private void updatePhotoView(){
            if (mPhotoFile == null || !mPhotoFile.exists()){
                mImageView.setImageDrawable(null);
            }else {
                Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),70,70);
                mImageView.setImageBitmap(bitmap);
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = NotePagerActivity.newIntent(getContext(),mNote.getId());
            startActivity(intent);
        }
    }
    /*
        RecycleView需要新的ViewHolder来显示列表项时 ,会调用onCreateViewHolder方法,在这个方法内部,
        创建一个LayoutInflater,然后创建NoteHolder
        NoteAdapter必须覆盖onBindViewHolder方法,
        最后将Adapter和RecycleView关联起来,实现一个设置CrimeListFragment用户界面的updateUI方法,该方法创建
        CrimeAdapter,然后设置给RecycleView

     */

    private class NoteAdapter extends RecyclerView.Adapter<NoteHolder>{
        private List<Note> mNotes;
        public NoteAdapter(List<Note> notes){
            mNotes = notes;
        }

        @NonNull
        @Override
        public NoteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return  new NoteHolder(layoutInflater , viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull NoteHolder noteHolder, int i) {
            Note mNote = mNotes.get(i);
            noteHolder.bind(mNote);
        }



        @Override
        public int getItemCount() {
            return mNotes.size();
        }
        public void setNotes(List<Note> notes){
            mNotes = notes;
        }
    }
    private void upDateUI(){
        NotesLab notesLab = NotesLab.getLab(getActivity());
        List<Note> notes = notesLab.getNotes();
        if (mAdapter == null){
        mAdapter = new NoteAdapter(notes);
        mRecyclerView.setAdapter(mAdapter);}
        else {
            mAdapter.setNotes(notes);
            mAdapter.notifyDataSetChanged();
        }
    }
    private void firstRun() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FirstRun",0);
        Boolean first_run = sharedPreferences.getBoolean("First",true);
        if (first_run){
            sharedPreferences.edit().putBoolean("First",false).commit();
            Note note = new Note();
            Date date = new Date(System.currentTimeMillis());
            note.setDate(date);
            note.setmTitle("欢迎使用MiNiNote！");
            note.setContent("您可以使用MiNiNote来记录美好的生活瞬间，也可以记录一些重要的事件。MiNiNote可以创建提醒，可以在你的便签" +
                    "中拍摄照片并且显示在便签中，可以在便签详细页通过左右滑动来更换页面。在便签详情页通过右上角的按钮分享文字内容或删除便签" +
                    ",点击图片可以显示大图哦^^ 感谢您的支持!谢谢!"
            );
            note.setSecondLine("您可以使用MiNiNote来记录美好的生活瞬间，也可以记录一些重要的事件。");
            NotesLab.getLab(getActivity()).addNote(note);
        }

    }

}
