package com.example.mininote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class TimePickerFragment extends DialogFragment {
    public static final String EXTRA_TIME = "com.example.mininote.time";

    private static final String ARG_TIME = "date";

    private TimePicker mTimePicker;
    int year,month,day;

    public static TimePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME,date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Date date = (Date)getArguments().getSerializable(ARG_TIME);
        Calendar calendar = Calendar.getInstance();//通过Calendar来解析Date类
        calendar.setTime(date);
         year = calendar.get(Calendar.YEAR);
         month = calendar.get(Calendar.MONTH);
         day = calendar.get(Calendar.DAY_OF_MONTH);


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_time_picker,null);

        mTimePicker = v.findViewById(R.id.id_timepicker);
        mTimePicker.setIs24HourView(true);
        //mTimePicker.setHour(hour);
       // mTimePicker.setMinute(min);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.select_time)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hour = mTimePicker.getHour();
                        int minute = mTimePicker.getMinute();
                        int[] mTime = new int[]{hour , minute};
                        sendResult(Activity.RESULT_OK,mTime);
                    }
                }).create();

    }
    /*
    如果是activity之间传递数据,调用startActivityForResult()方法,ActivityManager负责管理activity父子关系,
    回传数据后子activity被销毁,但ActivityManager知道接收数据的是哪个Activity
    对于同一Activity托管的fragment来说,跳转时使用newInstance方法将信息保存在argument中然后创建视图,
    并给要跳转的fragment设置当前fragment为targetFragment
    result数据回传时就要使用Fragment.onActivityResult,注意是fragment的方法
     */
    private void sendResult(int resultCode, int[] time) {
        if (getTargetFragment() == null){//fragment间传递信息
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME,time);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}


