package wily.apps.watchrabbit.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
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
                    int add_state = intent.getIntExtra(AppConst.INTENT_SERVICE_STATE, -1);
                    addNotification(add_id, add_type, add_title, add_priority, add_state);
                    break;
                case HABBIT_SERVICE_UPDATE:
                    int up_id = intent.getIntExtra(AppConst.INTENT_SERVICE_HABBIT_ID, -1);
                    int up_type = intent.getIntExtra(AppConst.INTENT_SERVICE_TYPE, -1);
                    String up_title = intent.getStringExtra(AppConst.INTENT_SERVICE_TITLE);
                    int up_priority = intent.getIntExtra(AppConst.INTENT_SERVICE_PRIORITY, -1);
                    boolean up_active = intent.getBooleanExtra(AppConst.INTENT_SERVICE_ACTIVE, false);
                    int up_state = intent.getIntExtra(AppConst.INTENT_SERVICE_STATE, -1);
                    updateNotification(up_id, up_type, up_title, up_priority, up_active, up_state);
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
            mainNoti = new HabbitNotification(HabbitService.this, HabbitNotification.TYPE_MAIN_NOTI, HabbitNotification.TYPE_MAIN_NOTI,"Main Notifiation",  11, HabbitNotification.MAIN_NOTI_STATE);
            initService();
            startForeground(mainNoti.getHid(), mainNoti.build());
        }
    }

    // 2. HABBIT_SERVICE_INIT
    private void initService(){
        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(HabbitService.this);
        habbitDB.habbitDao().getHabbitActive(true).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> afterGetHabbitActive(item));
    }

    private void afterGetHabbitActive(List<Habbit> habbitList){
        if(notiList != null){
            for(HabbitNotification noti : notiList){
                noti.cancel();
            }
        }
        notiList = new ArrayList<HabbitNotification>();
        for(Habbit habbit : habbitList){
            notiList.add(new HabbitNotification(HabbitService.this, habbit.getId(), habbit.getType(), habbit.getTitle(), habbit.getPriority(), habbit.getState()));
        }

        for(HabbitNotification noti : notiList){
            noti.sendNotification("Notification init");
        }
        mainNoti.sendNotification("HabbitService init complete : "+notiList.size());
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
    private void addNotification(int id, int type, String title, int priority, int state){
        if(id != -1){
            HabbitNotification noti = new HabbitNotification(HabbitService.this, id, type, title, priority, state);
            notiList.add(noti);
            noti.sendNotification("Notification init");
            mainNoti.sendNotification("#"+id+" "+title+" noti added");
        }
    }

    // 5. HABBIT_SERVICE_UPDATE
    private void updateNotification(int id, int type, String title, int priority, boolean active, int state){
        if(id != -1){
            int idx = notiList.indexOf(HabbitNotification.getDummy(id));
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
                    HabbitNotification newNoti = new HabbitNotification(HabbitService.this, id, type, title, priority, state);
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
    private void recordAction(int hid, int type){
        long now = System.currentTimeMillis();

        HabbitNotification noti = notiList.get(notiList.indexOf(HabbitNotification.getDummy(hid)));

        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(this);
        RecordDatabase recordDB = RecordDatabase.getAppDatabase(this);
        habbitDB.habbitDao().getHabbitState(hid).subscribeOn(Schedulers.io())
                .subscribe(state -> {
                    switch(state){
                        case Habbit.STATE_CHECK:
                            recordDB.recordDao().insert(new Record(hid, type, now, -1)).observeOn(AndroidSchedulers.mainThread()).subscribe(res ->
                            {
                                String message = "#"+hid+" "+ DateUtil.getDateString(now)+" checked";
                                noti.sendNotification(message);
                            });
                            break;
                        case Habbit.STATE_TIMER_WAIT:
                            recordDB.recordDao().insert(new Record(hid, type, now, -1)).subscribe(id->{
                                habbitDB.habbitDao().updateHabbitState(hid, Habbit.STATE_TIMER_INPROGRESS).subscribe();
                                habbitDB.habbitDao().updateCurRecordId(hid, id).observeOn(AndroidSchedulers.mainThread()).subscribe(res ->
                                {
                                    noti.setStatus(Habbit.STATE_TIMER_INPROGRESS);
                                    noti.getBuilder().mActions.get(0).title = "STOP";
                                    String message = "#"+hid+" "+ DateUtil.getDateString(now)+" inprogress";
                                    noti.sendNotification(message);
                                });
                            });
                            break;
                        case Habbit.STATE_TIMER_INPROGRESS:
                            habbitDB.habbitDao().getCurRecordId(hid).subscribe(id-> recordDB.recordDao().getRecord(id).subscribe(
                                    list -> {
                                        if(list.size()>0){
                                            Record record = list.get(0);
                                            long term = now - record.getTime();
                                            recordDB.recordDao().updateTerm(record.getId(), term).subscribe();
                                        }

                                        habbitDB.habbitDao().updateHabbitState(hid, Habbit.STATE_TIMER_WAIT).subscribe();
                                        habbitDB.habbitDao().updateCurRecordId(hid, -1).observeOn(AndroidSchedulers.mainThread()).subscribe(res ->
                                        {
                                            noti.setStatus(Habbit.STATE_TIMER_WAIT);
                                            noti.getBuilder().mActions.get(0).title = "START";
                                            String message = "#"+hid+" "+ DateUtil.getDateString(now)+" complete";
                                            noti.sendNotification(message);
                                        });
                                    }
                            ));
                            break;
                    }});
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
