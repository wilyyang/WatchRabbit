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
import wily.apps.watchrabbit.data.entity.Habbit;

import static wily.apps.watchrabbit.AppConst.INTENT_SERVICE_HABBIT_ID;
import static wily.apps.watchrabbit.AppConst.INTENT_SERVICE_TYPE;

public class HabbitNotification {
    private Context mContext;

    private int mHid;
    private int mType;
    private String mTitle;
    private int mPriority;
    private int mStatus;

    private NotificationChannel mChannel;
    private NotificationCompat.Builder mBuilder;

    public static final int TYPE_MAIN_NOTI = -1;
    public static final String GROUP_HABBIT_NOTI_KEY = "wily.apps.watchrabbit.WatchRabbit";

    public static final int MAIN_NOTI_STATE = 9999;

    // Create
    private HabbitNotification() {}

    public HabbitNotification(Context context, int hId, int type, String title, int priority, int state) {
        // Member init
        this.mContext = context;
        this.mHid = hId;
        this.mType = type;
        this.mTitle = title;
        this.mPriority = priority;

        mStatus = state;
        mChannel = new NotificationChannel(""+mHid, mTitle, NotificationManager.IMPORTANCE_HIGH);
        mBuilder = new NotificationCompat.Builder(mContext, ""+mHid)
                .setContentTitle(mTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .setOngoing(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setGroup(GROUP_HABBIT_NOTI_KEY)
                .setSortKey(""+priority);

        // Button intent
        if(mType == TYPE_MAIN_NOTI){
            Intent exitIntent = new Intent(mContext, HabbitService.class);
            exitIntent.setAction(HabbitService.HABBIT_SERVICE_EXIT);
            PendingIntent exitPending = PendingIntent.getService(mContext, 0, exitIntent, 0);
            mBuilder.addAction(android.R.drawable.btn_default, "Exit", exitPending);
            mBuilder.setSmallIcon(R.drawable.ic_service_top);
            mBuilder.setGroupSummary(true);
        }else{
            Intent recordIntent = new Intent(mContext, HabbitService.class);
            recordIntent.setAction(HabbitService.HABBIT_SERVICE_RECORDING);
            recordIntent.putExtra(INTENT_SERVICE_HABBIT_ID, mHid);
            recordIntent.putExtra(INTENT_SERVICE_TYPE, mType);
            PendingIntent recordPending = PendingIntent.getService(mContext, mHid, recordIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            switch(mStatus){
                case Habbit.STATE_CHECK:
                    mBuilder.setSmallIcon(R.drawable.ic_type_check)
                            .addAction(android.R.drawable.btn_default, "CHECK", recordPending);
                    break;
                case Habbit.STATE_TIMER_WAIT:
                    mBuilder.setSmallIcon(R.drawable.ic_type_timer)
                            .addAction(android.R.drawable.btn_default, "START", recordPending);
                    break;
                case Habbit.STATE_TIMER_INPROGRESS:
                    mBuilder.setSmallIcon(R.drawable.ic_type_timer)
                            .addAction(android.R.drawable.btn_default, "STOP", recordPending);
                    break;
            }
        }

        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMgr.createNotificationChannel(mChannel);
    }

    // Action
    public void changeNotiInfo(String title, int priority) {
        this.mTitle = title;
        this.mBuilder.setContentTitle(mTitle);
        this.mPriority = priority;
        this.mBuilder.setSortKey(""+priority);

        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMgr.notify(mHid, mBuilder.build());
    }

    public void sendNotification(String msg) {
        mBuilder.setContentText(msg);
        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMgr.notify(mHid, mBuilder.build());
    }

    public void cancel(){
        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMgr.cancel(mHid);
    }

    // Object
    public static HabbitNotification getDummy(int hid){
        HabbitNotification noti = new HabbitNotification();
        noti.mHid = hid;
        return noti;
    }

    @Override
    public int hashCode() {
        return mHid % 11;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        HabbitNotification noti = (HabbitNotification) obj;
        return noti.getHid() == mHid;
    }

    // Property
    public NotificationCompat.Builder getBuilder(){
        return mBuilder;
    }
    public Notification build(){
        return mBuilder.build();
    }

    public int getHid(){
        return mHid;
    }
    public int getType(){
        return mType;
    }
    public int getStatus(){
        return mStatus;
    }
    public void setStatus(int status){
        this.mStatus = status;
    }
}
