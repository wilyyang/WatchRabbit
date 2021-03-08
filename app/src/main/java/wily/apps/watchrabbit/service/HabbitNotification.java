package wily.apps.watchrabbit.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.DataConst;

import static wily.apps.watchrabbit.DataConst.HABBIT_ID;
import static wily.apps.watchrabbit.DataConst.HABBIT_TYPE;
import static wily.apps.watchrabbit.DataConst.HABBIT_STATE;

public class HabbitNotification {
    private Context mContext;

    private NotificationChannel mChannel;
    private NotificationCompat.Builder mBuilder;

    private int mId;
    private String mTitle;
    private int mType;
    private int mStatus;
    private int mPriority;

    private long mPair;

    public static final int TYPE_MAIN_NOTI = -1;

    public static final int STATUS_INIT = -1;

    public HabbitNotification(Context context, int id, String title, int type, int priority) {
        this.mContext = context;
        this.mId = id;
        this.mTitle = title;
        this.mType = type;
        this.mPair = -1;

        if(type==DataConst.TYPE_HABBIT_CHECK){
            mStatus = DataConst.HABBIT_STATE_CHECK;
        }else{
            mStatus = DataConst.HABBIT_STATE_TIMER_START;
        }
        this.mPriority = priority;

        mChannel = new NotificationChannel(""+id, mTitle, NotificationManager.IMPORTANCE_HIGH);
        mBuilder = new NotificationCompat.Builder(mContext, ""+id)
                .setContentTitle(mTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .setOngoing(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setGroup(DataConst.GROUP_KEY_HABBIT_NOTI)
                .setSortKey(""+priority);

        if(mType == TYPE_MAIN_NOTI){
//            Intent exitIntent = new Intent(mContext, HabbitService.class);
//            exitIntent.setAction(HabbitService.HABBIT_SERVICE_EXIT);
//            PendingIntent exitPending = PendingIntent.getService(mContext, 0, exitIntent, 0);
//            mBuilder.addAction(android.R.drawable.btn_default, "Exit", exitPending);
            mBuilder.setSmallIcon(R.drawable.ic_alarm);
            mBuilder.setGroupSummary(true);
        }else{
            Intent checkIntent = new Intent(mContext, HabbitService.class);
            checkIntent.setAction(HabbitService.HABBIT_SERVICE_CHECK);
            checkIntent.putExtra(HABBIT_ID, mId);
            checkIntent.putExtra(HABBIT_TYPE, mType);
//            Log.d(TAG, "id "+mId+" "+mTitle);
            PendingIntent checkPending = PendingIntent.getService(mContext, mId, checkIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            switch(mType){
                case DataConst.TYPE_HABBIT_CHECK:
                    mBuilder.setSmallIcon(R.drawable.ic_check_circle)
                            .addAction(android.R.drawable.btn_default, "CHECK", checkPending);
                    break;
                case DataConst.TYPE_HABBIT_TIMER:
                    mBuilder.setSmallIcon(R.drawable.ic_snooze).addAction(android.R.drawable.btn_default, "START", checkPending);
                    break;
            }
        }

        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMgr.createNotificationChannel(mChannel);
    }

    public void changeNotiInfo(String title, int priority) {
        if(!mTitle.equals(title)){
            this.mTitle = title;
            this.mBuilder.setContentTitle(mTitle);
        }

        if(mPriority != priority){
            this.mPriority = priority;
            this.mBuilder.setSortKey(""+priority);
        }

        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMgr.notify(mId, mBuilder.build());
    }

    public void sendNotification(String msg) {
        mBuilder.setContentText(msg);
        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//        notifiMgr.createNotificationChannel(mChannel);

        Notification noti = mBuilder.build();
        Log.d(DataConst.TAG, ""+noti.getGroup());
        notifiMgr.notify(mId, noti);
    }

    public NotificationCompat.Builder getBuilder(){
        return mBuilder;
    }

    public Notification build(){
        return mBuilder.build();
    }

    public int getPriority(){
        return mPriority;
    }

    public String getTitle(){
        return mTitle;
    }

    public int getId(){
        return mId;
    }

    public int getStatus(){
        return mStatus;
    }

    public void setStatus(int status){
        this.mStatus = status;
    }


    public int getType(){
        return mType;
    }

    public void cancel(){
        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMgr.cancel(mId);
    }

    public long getPair() {
        return mPair;
    }

    public void setPair(long mPair) {
        this.mPair = mPair;
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
