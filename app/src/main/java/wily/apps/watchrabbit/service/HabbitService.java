package wily.apps.watchrabbit.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.adapter.HabbitAdapter;
import wily.apps.watchrabbit.data.DataConst;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.entity.Habbit;

public class HabbitService extends Service {
    private Context mContext = null;

    public static final String HABBIT_SERVICE_CREATE    = "HABBIT_SERVICE_CREATE";
    public static final String HABBIT_SERVICE_INIT      = "HABBIT_SERVICE_INIT";
    public static final String HABBIT_SERVICE_EXIT      = "HABBIT_SERVICE_EXIT";
    public static final String HABBIT_SERVICE_ADD       = "HABBIT_SERVICE_ADD";
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
                case HABBIT_SERVICE_ADD:
                    int id = intent.getIntExtra(DataConst.HABBIT_ID, -1);
                    String title = intent.getStringExtra(DataConst.HABBIT_TITLE);
                    int type = intent.getIntExtra(DataConst.HABBIT_TYPE, -1);
                    addNotification(id, title, type);
                    break;
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
            mainNoti = new HabbitNotification(HabbitService.this, -1, "Main Notifiation", HabbitNotification.TYPE_MAIN_NOTI);
            startForeground(mainNoti.getId(), mainNoti.build());
            initService();
        }
    }

    private void initService(){
        HabbitDatabase db = HabbitDatabase.getAppDatabase(HabbitService.this);
        db.habbitDao().getHabbitActive(true).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    List<Habbit> habbitList = item;
                    if(notiList != null){
                        for(HabbitNotification noti : notiList){
                            noti.cancel();
                        }
                    }
                    notiList = new ArrayList<HabbitNotification>();

                    for(Habbit habbit : habbitList){
                        notiList.add(new HabbitNotification(HabbitService.this, habbit.getId(), habbit.getTitle(), habbit.getType()));
                    }

                    for(HabbitNotification noti : notiList){
                        noti.sendNotification("Notification init");
                    }
                    mainNoti.sendNotification("HabbitService init complete : "+notiList.size());
                });
    }

    private void exitService(){
        isCreate = false;
        if(notiList != null){
            for(HabbitNotification noti : notiList){
                noti.cancel();
            }
        }
        mainNoti.sendNotification("HabbitService exit");
        mainNoti.cancel();
        stopForeground(true);
        stopSelf();
    }

    private void addNotification(int id, String title, int type){
        if(id != -1){
            HabbitNotification noti = new HabbitNotification(HabbitService.this, id, title, type);
            notiList.add(noti);
            noti.sendNotification("Notification init");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
