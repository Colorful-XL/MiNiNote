package com.example.mininote;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class NoteFragment extends Fragment {
    private static final String ARG_NOTE_ID = "note_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_PHOTO = "DialogPhoto";
    private static final int REQUEST_DATE = 0;
    private static final int PHOTO_DETIAL = 1;
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_ALARM = 3;

    private Note mNote;
    private File mPhotoFile;
    private EditText mContentFile;
    private FloatingActionButton mPhotoButton;
    private TextView mTimeText;
    private TextView mAlarmTime;
    private FloatingActionButton mAlarmButton;
    private ImageView mPhoto;
    private ImageView mAlarmIcon;
    private ImageButton mBackIcon;
    private ImageButton mDeleteIcon;
    private ImageButton mShareIcon;
    private boolean edited = false;
    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm" );

    public static NoteFragment newInstance(UUID noteId){//通过静态newInstance方法给fragment设置argument用以通信
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE_ID,noteId);

        NoteFragment fragment = new NoteFragment();//只申请了空间,onCreate方法没有回调
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {//获取argument信息
        super.onCreate(savedInstanceState);
        UUID noteId =(UUID) getArguments().getSerializable(ARG_NOTE_ID);//获取需要创建的fragment的crime的id
        //通过Arguments摆脱对Activity的依赖,如果使用intent传递信息,fragment就需要从托管它的Activity中获得
        //intent并从中获取信息,破坏了fragment的封装,这个类如果被其他Activity托管就要进行改变
        mNote = NotesLab.getLab(getActivity()).getNote(noteId);//从数据库中获取Note实例
        mPhotoFile = NotesLab.getLab(getActivity()).getPhotoFile(mNote);
        Log.d("OnCreate: SetNoti:" , "Value"+mNote.getSetNoti());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("OnPause: SetNoti:" , "Value"+mNote.getSetNoti());
        Log.d("OnPause: NotiDate:" , "Value"+mNote.getNotiDate());
        if (mNote.getmTitle()==null && !mPhotoFile.exists()){
            NotesLab.getLab(getActivity()).deleteNote(mNote);
        }else
        NotesLab.getLab(getActivity()).updateNote(mNote);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("OnResume: SetNoti:" , "Value"+mNote.getSetNoti());
        if (mNote.getSetNoti()==0)
        {
         mAlarmIcon.setVisibility(View.GONE);}
         else{
             setfloat();
             setAlarm();
       }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note,container,false);
        Log.d("OnCreateView: SetNoti:" , "Value"+mNote.getSetNoti());
        mContentFile = v.findViewById(R.id.id_note_content);
        mTimeText = v.findViewById(R.id.id_note_time);
        mPhotoButton = v.findViewById(R.id.id_floatPic);
        mAlarmButton = v.findViewById(R.id.id_floatAlarm);
        mPhoto = v.findViewById(R.id.id_imageview);
        mAlarmIcon = v.findViewById(R.id.id_alarmIcon);
        mBackIcon = v.findViewById(R.id.id_icon_back);
        mShareIcon = v.findViewById(R.id.id_icon_share);
        mDeleteIcon = v.findViewById(R.id.id_icon_delete);
        mAlarmTime  = v.findViewById(R.id.id_alarmTime);
        PackageManager packageManager = Objects.requireNonNull(getActivity()).getPackageManager();
        //向上返回按钮
        mBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),NoteListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });
        //分享笔记
        mShareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plan");
                intent.putExtra(Intent.EXTRA_TEXT,GetNoteShareText());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent.createChooser(intent,"选择一个应用:"));
            }
        });

        //删除笔记
        mDeleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotesLab.getLab(getActivity()).deleteNote(mNote);
                Intent intent = new Intent(getActivity(),NoteListActivity.class);
                cancelAlarm();
                mPhotoFile.delete();
                startActivity(intent);//删除后注意更新list视图,和闹钟的删除
            }
        });

        //文本编辑
        mContentFile.setText(mNote.getContent());
        if (!mContentFile.hasFocus()){
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(mContentFile.getWindowToken(),0);
        }

        mContentFile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                StringBuffer stringBufferTitle = new StringBuffer();
                int i = 0;
                for (i = 0; i <s.length()&&i<13&&!Character.isSpaceChar(s.charAt(i)); i++) {
                    stringBufferTitle.append(s.charAt(i));
                }
                mNote.setmTitle(stringBufferTitle.toString());
                StringBuffer stringBufferSecondLine = new StringBuffer();
                for (int j = i; j <s.length()&&j<i+27 ; j++) {
                    stringBufferSecondLine.append(s.charAt(j));
                }
                mNote.setSecondLine(stringBufferSecondLine.toString());
                mNote.setContent(s.toString());
                mTimeText.setText(updateTime());
            }
        });
        //显示创建时间
        mTimeText.setText(updateTime());
        //拍照按钮
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile!=null &&
                captureImage.resolveActivity(packageManager)!=null;         //判断是否有应用可以执行拍照操作
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.example.mininote.fileprovider",mPhotoFile);//第二个参数的值为Provider中auth的值,标识唯一provider
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);//为输出照片的应用提供输出地址

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);//将所有可以执行拍照操作的应用选出来

                for (ResolveInfo activity:cameraActivities){//给所有可用应用授权可以操作指定uri的文件,通过intent中的常量具体指定读写
                    getActivity().grantUriPermission(activity.activityInfo.packageName,uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });
        //提醒
        if (mNote.getSetNoti()!=0){
            Date date = new Date(System.currentTimeMillis());
            if(mNote.getNotiDate().compareTo(date) >= 0)
            setAlarm();
            else cancelAlarm();
        }

        mAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNote.getSetNoti()==0){
                FragmentManager fragmentManager = getFragmentManager();
                DatePickFragment dialog = DatePickFragment.newInstance(mNote.getDate());
                dialog.setTargetFragment(NoteFragment.this,REQUEST_DATE);
                dialog.show(fragmentManager,DIALOG_DATE);}

                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("提示:")
                            .setMessage("您已经设置了提醒,是否取消?")
                            .setCancelable(true)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//取消提醒
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mNote.setSetNoti(0);
                                    Log.d("悬浮按钮取消闹钟","第253行");
                                     cancelAlarm();
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(),"将会在指定时间提醒您",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create().show();

                }
            }
        });
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPhotoFile == null || !mPhotoFile.exists()) {
                    mPhoto.setImageDrawable(null);
                } else {

                    FragmentManager manager = getFragmentManager();
                    PhotoDetailFragment dialog = PhotoDetailFragment.newInstance(mPhotoFile);
                    dialog.setTargetFragment(NoteFragment.this, REQUEST_PHOTO);
                    dialog.show(manager, DIALOG_PHOTO);
                }
            }
        });
        setfloat();
        updatePhotoView();
        return v;
    }

    private void setfloat(){
        if (mNote.getSetNoti()==0){
            mAlarmButton.setImageResource(R.drawable.ic_action_alarm);
        }else {
            mAlarmButton.setImageResource(R.drawable.ic_action_cancelnoti);
        }
    }
    private String GetNoteShareText() {
        StringBuffer share = new StringBuffer();
        share.append("我在MiNiNote上创建了新的便签,现在和你分享!   ");
        share.append(simpleDateFormat.format(mNote.getDate()));
        share.append("  ");
        share.append(mContentFile.getText().toString());
        return share.toString();
    }


    private String updateTime(){
        if(edited == false){
            return simpleDateFormat.format(mNote.getDate());
        }
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        mNote.setDate(date);
       return  simpleDateFormat.format(date);

    }

    private void updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile.exists()){
            mPhoto.setImageDrawable(null);
        }else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),700,350);
            mPhoto.setImageBitmap(bitmap);
            mTimeText.setText(updateTime());
        }
    }
    private void cancelAlarm(){
        Intent intent = new Intent(getActivity(),AlarmService.class);
        intent.putExtra("date",mNote.getNotiDate() );
        intent.putExtra("noteId",mNote.getId());
        intent.putExtra("cancel",1);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startService(intent);
        mNote.setSetNoti(0);
        setfloat();
        mAlarmIcon.setVisibility(View.GONE);
        mAlarmTime.setText(null);

    }
    private void setAlarm(){
        Intent intent = new Intent(getActivity(),AlarmService.class);
        intent.putExtra("date",mNote.getNotiDate() );
        intent.putExtra("noteId",mNote.getId());
        intent.putExtra("cancel",0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        getActivity().startService(intent);Log.d("AlarmNoteFragment","request start Service");
        mNote.setSetNoti(1);
        setfloat();
        mAlarmIcon.setVisibility(View.VISIBLE);
        mAlarmTime.setText("|"+simpleDateFormat.format(mNote.getNotiDate()));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_DATE){
            Date date = (Date)data.getSerializableExtra(DatePickFragment.EXTRA_DATE);
            mNote.setNotiDate(date);
            mNote.setSetNoti(1);
            setAlarm();
        }
       /* else if (requestCode ==  REQUEST_ALARM ){
            Date date = (Date)data.getSerializableExtra(DatePickFragment.EXTRA_DATE);
            mNote.setNotiDate(date);

        }*/
        else if(requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.example.mininote.fileprovider",mPhotoFile);
            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }
}
