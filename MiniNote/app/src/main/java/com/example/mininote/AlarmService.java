package com.example.mininote;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AlarmService extends Service {

    private static final String TAG = "test";
    private static final String Broad_Action = "com.example.mininote.RING" ;
    private Date mDate;
    private Calendar mCalendar;
    private AlarmManager mAlarmManger;
    private UUID noteId;
    private boolean cancel = false;
    public AlarmService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("AlarmService","ServiceStart");
        mAlarmManger = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mDate = (Date) intent.getSerializableExtra("date");
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(mDate);
        noteId = (UUID) intent.getSerializableExtra("noteId");
        cancel = intent.getIntExtra("cancel",0)==1;

        //设置/取消广播
        Intent broadIntent = new Intent();
        broadIntent.setAction("com.example.mininote.RING");
        broadIntent.putExtra("noteId",noteId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,broadIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (cancel){
            mAlarmManger.cancel(pendingIntent);Log.d("AlarService","delete");
            return super.onStartCommand(intent,flags,startId);

        }else{
        mAlarmManger.set(AlarmManager.RTC_WAKEUP,mCalendar.getTimeInMillis(),pendingIntent);
            //sendBroadcast(broadIntent);
            cancel = false;
        Log.d("AlarService","send");
        return super.onStartCommand(intent,flags,startId);}
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
