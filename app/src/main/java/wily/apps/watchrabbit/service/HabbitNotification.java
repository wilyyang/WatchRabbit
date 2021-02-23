package wily.apps.watchrabbit.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import wily.apps.watchrabbit.R;

public class HabbitNotification {
    private Context mContext;

    private NotificationChannel mChannel;
    private NotificationCompat.Builder mBuilder;

    private int mId;
    private String mTitle;
    private boolean isMain;

    public HabbitNotification(Context context, int id, String title, boolean main) {
        this.mContext = context;
        this.mId = id;
        this.mTitle = title;
        this.isMain = main;

        mChannel = new NotificationChannel(""+id, title, NotificationManager.IMPORTANCE_HIGH);

        Intent checkIntent = new Intent(mContext, HabbitService.class);
        checkIntent.setAction(HabbitService.HABBIT_SERVICE_CHECK);
        PendingIntent checkPending = PendingIntent.getService(mContext, 0, checkIntent, 0);

        mBuilder = new NotificationCompat.Builder(mContext, ""+id)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_alarm)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .addAction(android.R.drawable.btn_default, "Check", checkPending);

        if(isMain){
            Intent exitIntent = new Intent(mContext, HabbitService.class);
            exitIntent.setAction(HabbitService.HABBIT_SERVICE_EXIT);
            PendingIntent exitPending = PendingIntent.getService(mContext, 0, exitIntent, 0);
            mBuilder.addAction(android.R.drawable.btn_default, "Exit", exitPending);
        }
    }

    public void sendNotification( String msg) {
        mBuilder.setContentText(msg);
        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMgr.createNotificationChannel(mChannel);
        notifiMgr.notify(mId, mBuilder.build());
    }

    public Notification build(){
        return mBuilder.build();
    }

    public int getId(){
        return mId;
    }

    public void cancel(){
        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMgr.cancel(mId);
    }
}
