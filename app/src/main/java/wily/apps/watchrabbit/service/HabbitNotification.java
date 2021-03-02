package wily.apps.watchrabbit.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.DataConst;

public class HabbitNotification {
    private Context mContext;

    private NotificationChannel mChannel;
    private NotificationCompat.Builder mBuilder;

    private int mId;
    private String mTitle;
    private int mType;
    private int mStatus;

    public static final int TYPE_MAIN_NOTI = -1;

    public static final int STATUS_INIT = -1;
    public static final int STATUS_START_CHECK = -1;
    public static final int STATUS_END_CHECK = -1;

    public HabbitNotification(Context context, int id, String title, int type) {
        this.mContext = context;
        this.mId = id;
        this.mTitle = title;
        this.mType = type;
        this.mStatus = STATUS_INIT;

        mChannel = new NotificationChannel(""+id, mTitle, NotificationManager.IMPORTANCE_HIGH);
        mBuilder = new NotificationCompat.Builder(mContext, ""+id)
                .setContentTitle(mTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .setOngoing(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        if(mType == TYPE_MAIN_NOTI){
            Intent exitIntent = new Intent(mContext, HabbitService.class);
            exitIntent.setAction(HabbitService.HABBIT_SERVICE_EXIT);
            PendingIntent exitPending = PendingIntent.getService(mContext, 0, exitIntent, 0);
            mBuilder.setSmallIcon(R.drawable.ic_alarm).addAction(android.R.drawable.btn_default, "Exit", exitPending);
        }else{
            Intent checkIntent = new Intent(mContext, HabbitService.class);
            checkIntent.setAction(HabbitService.HABBIT_SERVICE_CHECK);
            PendingIntent checkPending = PendingIntent.getService(mContext, 0, checkIntent, 0);

            switch(mType){
                case DataConst.TYPE_HABBIT_CHECK:
                    mBuilder.setSmallIcon(R.drawable.ic_check_circle).addAction(android.R.drawable.btn_default, "CHECK", checkPending);
                    break;
                case DataConst.TYPE_HABBIT_TIMER:
                    mBuilder.setSmallIcon(R.drawable.ic_snooze).addAction(android.R.drawable.btn_default, "START", checkPending);
                    break;
            }
        }
    }

    public void sendNotification(String msg) {
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

    public int getStatus(){
        return mStatus;
    }

    public int getType(){
        return mType;
    }

    public void cancel(){
        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMgr.cancel(mId);
    }

    // temp
    private HabbitNotification() {}
    public static HabbitNotification getDummy(int id){
        HabbitNotification noti = new HabbitNotification();
        noti.mId = id;
        return noti;
    }

    @Override
    public int hashCode() {
        return mId%11;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        HabbitNotification noti = (HabbitNotification) obj;
        return noti.getId() == mId;
    }
}
