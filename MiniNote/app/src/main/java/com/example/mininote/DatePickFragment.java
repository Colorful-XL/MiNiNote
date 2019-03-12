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
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

//将AlertDialog封装在Fragment中可以灵活显示视图,并且在设备旋转后如果直接单独使用Dialog,Dialog会消失,但封装后旋转设备DialogFragment会重建
public class DatePickFragment extends DialogFragment {
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_ALARM = 3;
    public static final String EXTRA_DATE = "com.example.mininote.date";

    private static final String ARG_DATE = "date";

    int pickhour,pickmin,pickyear,pickmonth,pickday;

    private DatePicker mDatePicker;

     public static DatePickFragment newInstance(Date date){
         Bundle args = new Bundle();
         args.putSerializable(ARG_DATE,date);

         DatePickFragment fragment = new DatePickFragment();
         fragment.setArguments(args);
         return fragment;
     }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final  Date date = (Date)getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();//通过Calendar来解析Date类
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_date_pick,null);

        mDatePicker = v.findViewById(R.id.id_datepicker);
        mDatePicker.init(year,month,day,null);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.select_date)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pickyear = mDatePicker.getYear();
                        pickmonth = mDatePicker.getMonth();
                        pickday = mDatePicker.getDayOfMonth();


                        //打开TimePicker
                        FragmentManager fragmentManager = getFragmentManager();
                        TimePickerFragment Timedialog = TimePickerFragment.newInstance(date);
                        Timedialog.setTargetFragment(DatePickFragment.this,REQUEST_ALARM);
                        Timedialog.show(fragmentManager,DIALOG_TIME);

                        //Date date = new GregorianCalendar(pickyear,pickmonth,pickday,pickhour,pickmin).getTime();
                       // sendResult(Activity.RESULT_OK,date);
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
    private void sendResult(int resultCode, Date date) {
         if (getTargetFragment() == null){//fragment间传递信息
             return;
         }
        Intent intent = new Intent();
         intent.putExtra(EXTRA_DATE,date);

         getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_ALARM){
            int [] resultTime = (int[]) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            pickhour = resultTime[0];
            pickmin = resultTime[1];
            Log.d("DatePickFragment","pick hour minute"+pickhour+" "+pickmin);
            Date date = new GregorianCalendar(pickyear,pickmonth,pickday,pickhour,pickmin).getTime();
            sendResult(Activity.RESULT_OK,date);
        }

        }
}
