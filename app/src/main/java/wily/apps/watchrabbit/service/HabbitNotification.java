package wily.apps.watchrabbit.service;

import android.annotation.SuppressLint;
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

    private int notiId;
    private int notiType;
    private String notiTitle;
    private int notiPriority;

    private NotificationChannel mChannel;
    private NotificationCompat.Builder mBuilder;

    public static final int TYPE_MAIN_NOTI = -1;
    public static final String GROUP_HABBIT_NOTI_KEY = "wily.apps.watchrabbit.WatchRabbit";

    public static final int MAIN_NOTI_STATE = 9999;

    // Create
    private HabbitNotification() {}

    public HabbitNotification(Context context, int notiId, int notiType, String notiTitle, int notiPriority, int notiStatus) {
        // Member init
        this.mContext = context;
        this.notiId = notiId;
        this.notiType = notiType;
        this.notiTitle = notiTitle;
        this.notiPriority = notiPriority;

        mChannel = new NotificationChannel(""+notiId, notiTitle, NotificationManager.IMPORTANCE_HIGH);
        mBuilder = new NotificationCompat.Builder(mContext, ""+notiId)
                .setContentTitle(notiTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .setOngoing(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setGroup(GROUP_HABBIT_NOTI_KEY)
                .setSortKey(""+notiPriority);

        // Button intent
        if(notiType == TYPE_MAIN_NOTI){
            Intent exitIntent = new Intent(mContext, HabbitService.class);
            exitIntent.setAction(HabbitService.HABBIT_SERVICE_EXIT);
            PendingIntent exitPending = PendingIntent.getService(mContext, 0, exitIntent, 0);
            mBuilder.addAction(android.R.drawable.btn_default, "Exit", exitPending);
            mBuilder.setSmallIcon(R.drawable.ic_service_top);
            mBuilder.setGroupSummary(true);
        }else{
            Intent recordIntent = new Intent(mContext, HabbitService.class);
            recordIntent.setAction(HabbitService.HABBIT_SERVICE_RECORDING);
            recordIntent.putExtra(INTENT_SERVICE_HABBIT_ID, notiId);
            recordIntent.putExtra(INTENT_SERVICE_TYPE, notiType);
            PendingIntent recordPending = PendingIntent.getService(mContext, notiId, recordIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            switch(notiStatus){
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

    public Notification build(){
        return mBuilder.build();
    }

    // Dummy
    public static HabbitNotification getDummy(int id){
        HabbitNotification noti = new HabbitNotification();
        noti.notiId = id;
        return noti;
    }

    public int getId(){
        return notiId;
    }

    @Override
    public int hashCode() {
        return notiId % 11;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        HabbitNotification noti = (HabbitNotification) obj;
        return noti.getId() == notiId;
    }

    // Action
    public void updateNotiInfo(String title, int priority) {
        this.notiTitle = title;
        this.notiPriority = priority;
        this.mBuilder.setContentTitle(notiTitle);
        this.mBuilder.setSortKey(""+notiPriority);

        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMgr.notify(notiId, mBuilder.build());
    }

    public void updateNotiText(String msg) {
        mBuilder.setContentText(msg);

        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMgr.notify(notiId, mBuilder.build());
    }

    public void cancel(){
        NotificationManager notifiMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifiMgr.cancel(notiId);
    }

    // Property
    @SuppressLint("RestrictedApi")
    public void setButtonTitle(int idx, String pTitle){
        mBuilder.mActions.get(idx).title = pTitle;
    }
}
