package wily.apps.watchrabbit.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

import wily.apps.watchrabbit.data.entity.Habbit;

public class HabbitService extends Service {
    private Context mContext = null;

    public static final String HABBIT_SERVICE_CREATE    = "HABBIT_SERVICE_CREATE";
    public static final String HABBIT_SERVICE_INIT      = "HABBIT_SERVICE_INIT";
    public static final String HABBIT_SERVICE_EXIT      = "HABBIT_SERVICE_EXIT";
//    public static final String HABBIT_SERVICE_ADD       = "HABBIT_SERVICE_ADD";
//    public static final String HABBIT_SERVICE_REMOVE    = "HABBIT_SERVICE_REMOVE";
//    public static final String HABBIT_SERVICE_UPDATE    = "HABBIT_SERVICE_UPDATE";
    public static final String HABBIT_SERVICE_CHECK     = "HABBIT_SERVICE_CHECK";
//    public static final String HABBIT_SERVICE_SAVE      = "HABBIT_SERVICE_SAVE";

    private boolean isCreate = false;

    private HabbitNotification mainNoti = null;
    private ArrayList<HabbitNotification> notiList = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = HabbitService.this;
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case HABBIT_SERVICE_CREATE:
                    createService();
                    break;
                case HABBIT_SERVICE_INIT:
                    initService();
                    break;
                case HABBIT_SERVICE_EXIT:
                    exitService();
                    break;
//                case HABBIT_SERVICE_ADD:
//                    break;
//                case HABBIT_SERVICE_REMOVE:
//                    break;
//                case HABBIT_SERVICE_UPDATE:
//                    break;
                case HABBIT_SERVICE_CHECK:
                    Log.d("WILY", "HABBIT_SERVICE_CHECK");
                    break;
//                case HABBIT_SERVICE_SAVE:
//                    break;
            }
        }
        return START_STICKY;
    }

    private void createService(){
        if(isCreate == false){
            isCreate = true;
            mainNoti = new HabbitNotification(HabbitService.this, -1, "Main Notifiation", true);
            startForeground(mainNoti.getId(), mainNoti.build());
            mainNoti.sendNotification("HabbitService create");

            notiList = new ArrayList<HabbitNotification>();
        }
    }

    private void initService(){

    }

    private void exitService(){
        for(HabbitNotification noti : notiList){
            noti.cancel();
        }
        mainNoti.sendNotification("HabbitService exit");
        mainNoti.cancel();
        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
