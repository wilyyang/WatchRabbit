package wily.apps.watchrabbit.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.AppConst;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.database.RecordDatabase;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.data.entity.Record;
import wily.apps.watchrabbit.util.DateUtil;

public class HabbitService extends Service {
    public static final String HABBIT_SERVICE_CREATE    = "HABBIT_SERVICE_CREATE";
    public static final String HABBIT_SERVICE_INIT      = "HABBIT_SERVICE_INIT";
    public static final String HABBIT_SERVICE_EXIT      = "HABBIT_SERVICE_EXIT";
    public static final String HABBIT_SERVICE_ADD       = "HABBIT_SERVICE_ADD";
    public static final String HABBIT_SERVICE_UPDATE    = "HABBIT_SERVICE_UPDATE";
    public static final String HABBIT_SERVICE_DELETE    = "HABBIT_SERVICE_DELETE";
    public static final String HABBIT_SERVICE_RECORDING = "HABBIT_SERVICE_RECORDING";

    private boolean isCreate = false;

    private HabbitNotification mainNoti = null;
    private ArrayList<HabbitNotification> notiList = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
                    int add_id = intent.getIntExtra(AppConst.INTENT_SERVICE_HABBIT_ID, -1);
                    int add_type = intent.getIntExtra(AppConst.INTENT_SERVICE_TYPE, -1);
                    String add_title = intent.getStringExtra(AppConst.INTENT_SERVICE_TITLE);
                    int add_priority = intent.getIntExtra(AppConst.INTENT_SERVICE_PRIORITY, -1);
                    addNotification(add_id, add_type, add_title, add_priority);
                    break;
                case HABBIT_SERVICE_UPDATE:
                    int up_id = intent.getIntExtra(AppConst.INTENT_SERVICE_HABBIT_ID, -1);
                    int up_type = intent.getIntExtra(AppConst.INTENT_SERVICE_TYPE, -1);
                    String up_title = intent.getStringExtra(AppConst.INTENT_SERVICE_TITLE);
                    int up_priority = intent.getIntExtra(AppConst.INTENT_SERVICE_PRIORITY, -1);
                    boolean up_active = intent.getBooleanExtra(AppConst.INTENT_SERVICE_ACTIVE, false);
                    updateNotification(up_id, up_type, up_title, up_priority, up_active);
                    break;
                case HABBIT_SERVICE_DELETE:
                    ArrayList<Integer> delete_list = intent.getIntegerArrayListExtra(AppConst.INTENT_SERVICE_DELETE_LIST);
                    deleteNotification(delete_list);
                    break;
                case HABBIT_SERVICE_RECORDING:
                    int record_id = intent.getIntExtra(AppConst.INTENT_SERVICE_HABBIT_ID, -1);
                    int record_type = intent.getIntExtra(AppConst.INTENT_SERVICE_TYPE, -1);
                    recordAction(record_id, record_type);
                    break;
            }
        }
        return START_STICKY;
    }

    // 1. HABBIT_SERVICE_CREATE
    private void createService(){
        if(isCreate == false){
            isCreate = true;
            mainNoti = new HabbitNotification(HabbitService.this, -1, HabbitNotification.TYPE_MAIN_NOTI,"Main Notifiation",  11);
            initService();
            startForeground(mainNoti.getId(), mainNoti.build());
        }
    }

    // 2. HABBIT_SERVICE_INIT
    private void initService(){
        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(HabbitService.this);
        habbitDB.habbitDao().getHabbitActive(true).subscribeOn(Schedulers.io())
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
                        notiList.add(new HabbitNotification(HabbitService.this, habbit.getId(), habbit.getType(), habbit.getTitle(), habbit.getPriority()));
                    }

                    for(HabbitNotification noti : notiList){
                        noti.sendNotification("Notification init");
                    }
                    mainNoti.sendNotification("HabbitService init complete : "+notiList.size());
                });
    }

    // 3. HABBIT_SERVICE_EXIT
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

    // 4. HABBIT_SERVICE_ADD
    private void addNotification(int id, int type, String title, int priority){
        if(id != -1){
            HabbitNotification noti = new HabbitNotification(HabbitService.this, id, type, title, priority);
            notiList.add(noti);
            noti.sendNotification("Notification init");
            mainNoti.sendNotification("#"+id+" "+title+" noti added");
        }
    }

    // 5. HABBIT_SERVICE_UPDATE
    private void updateNotification(int id, int type, String title, int priority, boolean active){
        if(id != -1){
            int idx = notiList.indexOf(HabbitNotification.getDummy(id));
            Log.d(AppConst.TAG, "idx : "+idx+" , "+id+" "+active);
            HabbitNotification noti = notiList.get(idx);
            if(noti != null){
                if(active){
                    noti.changeNotiInfo(title, priority);
                }else{
                    noti.cancel();
                    notiList.remove(idx);
                    mainNoti.sendNotification("#"+id+" "+title+" noti disabled");
                }
            }else{
                if(active){
                    HabbitNotification newNoti = new HabbitNotification(HabbitService.this, id, type, title, priority);
                    notiList.add(newNoti);
                    noti.sendNotification("Notification init");
                    mainNoti.sendNotification("#"+id+" "+title+" noti actived");
                }
            }
        }
    }

    // 6. HABBIT_SERVICE_DELETE
    private void deleteNotification(ArrayList<Integer> list){
        int count = 0;
        for(Integer id : list){
            int idx = notiList.indexOf(HabbitNotification.getDummy(id));
            Log.d(AppConst.TAG, "idx : "+idx+" , "+id+" ");
            if(idx > -1) {
                HabbitNotification noti = notiList.get(idx);
                noti.cancel();
                notiList.remove(idx);
                ++count;
            }
        }

        mainNoti.sendNotification("Noti removed : "+count);
    }

    // 7. HABBIT_SERVICE_RECORDING
    @SuppressLint("RestrictedApi")
    private void recordAction(int id, int type){
        long now = System.currentTimeMillis();
        HabbitNotification noti = notiList.get(notiList.indexOf(HabbitNotification.getDummy(id)));
        long pair = noti.getPair();
        int state = noti.getStatus();
        RecordDatabase db = RecordDatabase.getAppDatabase(this);
        db.recordDao().insert(new Record(id, type, now, state, pair)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    String message = "#"+id+" "+ DateUtil.getDateString(now)+" ";
                    if(state == Record.RECORD_STATE_TIMER_START){
                        noti.setPair(item);
                        noti.setStatus(Record.RECORD_STATE_TIMER_STOP);
                        noti.getBuilder().mActions.get(0).title = "STOP";
                        message+="start";
                    }else if(state == Record.RECORD_STATE_TIMER_STOP){
                        noti.setPair(-1);
                        noti.setStatus(Record.RECORD_STATE_TIMER_START);
                        noti.getBuilder().mActions.get(0).title = "START";
                        message+="stop";
                    }else{
                        noti.setPair(-1);
                        message+="checked";
                    }
                    noti.sendNotification(message);
                });
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
