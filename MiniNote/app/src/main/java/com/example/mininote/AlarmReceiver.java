package com.example.mininote;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.UUID;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction()=="com.example.mininote.RING"){
            Log.d("AlarmRe","onRec");
            UUID noteId = (UUID) intent.getSerializableExtra("noteId");
            Note mNote = NotesLab.getLab(context).getNote(noteId);

            //提醒
            Intent intent1 = new Intent(context,NotePagerActivity.class);
            intent1.putExtra("noteId",noteId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent1,0);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(context,null)
                    .setContentTitle("MiniNote任务!")
                    .setContentText(mNote.getmTitle())
                    .setSmallIcon(R.drawable.ic_action_notifacation)
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .build();
            manager.notify(1,notification);
        }

    }
}
