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
import wily.apps.watchrabbit.data.entity.Record;

import static wily.apps.watchrabbit.AppConst.INTENT_SERVICE_HABBIT_ID;
import static wily.apps.watchrabbit.AppConst.INTENT_SERVICE_TYPE;

public class HabbitNotification {
    private Context mContext;

    private int mId;
    private int mType;
    private String mTitle;
    private int mPriority;
    private long mPair;
    private int mStatus;

    private NotificationChannel mChannel;
    private NotificationCompat.Builder mBuilder;

    public static final int TYPE_MAIN_NOTI = -1;
    public static final String GROUP_HABBIT_NOTI_KEY = "wily.apps.watchrabbit.WatchRabbit";

    // Create
    private HabbitNotification() {}

    public HabbitNotification(Context context, int id, int type, String title, int priority) {
        // Member init
        this.mContext = context;
        this.mId = id;
        this.mType = type;
        this.mTitle = title;
        this.mPriority = priority;
        this.mPair = -1;

        if(type == Habbit.TYPE_HABBIT_CHECK){
            mStatus = Record.RECORD_STATE_CHECK;
        }else if(type == Habbit.TYPE_HABBIT_TIMER){
            mStatus = Record.RECORD_STATE_TIMER_START;
        }

        mChannel = new NotificationChannel(""+id, mTitle, NotificationManager.IMPORTANCE_HIGH);
        mBuilder = new NotificationCompat.Builder(mContext, ""+id)
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
            recordIntent.putExtra(INTENT_SERVICE_HABBIT_ID, mId);
            recordIntent.putExtra(INTENT_SERVICE_TYPE, mType);
            PendingIntent recordPending = PendingIntent.getService(mContext, mId, recordIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            switch(mType){
                case Habbit.TYPE_HABBIT_CHECK:
                    mBuilder.setSmallIcon(R.drawable.ic_type_check)
                            .addAction(android.R.drawable.btn_default, "CHECK", recordPending);
                    break;
                case Habbit.TYPE_HABBIT_TIMER:
                    mBuilder.setSmallIcon(R.drawable.ic_type_timer)
                            .addAction(android.R.drawable.btn_default, "START", recordPending);
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
        notifiMgr.notify(mId, mBuilder.build());
    }

    public void sendNotification(String msg) {
        mBuilder.setContentText(msg);
        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMgr.notify(mId, mBuilder.build());
    }

    public void cancel(){
        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMgr.cancel(mId);
    }

    // Object
    public static HabbitNotification getDummy(int id){
        HabbitNotification noti = new HabbitNotification();
        noti.mId = id;
        return noti;
    }

    @Override
    public int hashCode() {
        return mId % 11;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        HabbitNotification noti = (HabbitNotification) obj;
        return noti.getId() == mId;
    }

    // Property
    public NotificationCompat.Builder getBuilder(){
        return mBuilder;
    }
    public Notification build(){
        return mBuilder.build();
    }

    public int getId(){
        return mId;
    }
    public int getType(){
        return mType;
    }

    public long getPair() {
        return mPair;
    }
    public void setPair(long mPair) {
        this.mPair = mPair;
    }

    public int getStatus(){
        return mStatus;
    }
    public void setStatus(int status){
        this.mStatus = status;
    }
}
