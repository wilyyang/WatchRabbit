package wily.apps.watchrabbit.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.AppConst;
import wily.apps.watchrabbit.EvaluateWork;
import wily.apps.watchrabbit.data.dao.HabbitDao;
import wily.apps.watchrabbit.data.dao.RecordDao;
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

    private static boolean isCreate = false;

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
                    Habbit habbitAdd = (Habbit) intent.getSerializableExtra(AppConst.INTENT_SERVICE_HABBIT);
                    addNotification(habbitAdd);
                    break;
                case HABBIT_SERVICE_UPDATE:
                    Habbit habbitUpdate = (Habbit) intent.getSerializableExtra(AppConst.INTENT_SERVICE_HABBIT);
                    updateNotification(habbitUpdate);
                    break;
                case HABBIT_SERVICE_DELETE:
                    ArrayList<Integer> delete_list = intent.getIntegerArrayListExtra(AppConst.INTENT_SERVICE_DELETE_LIST);
                    deleteNotification(delete_list);
                    break;
                case HABBIT_SERVICE_RECORDING:
                    int habbit_id = intent.getIntExtra(AppConst.INTENT_SERVICE_HABBIT_ID, -1);
                    int type = intent.getIntExtra(AppConst.INTENT_SERVICE_TYPE, -1);
                    recordAction(habbit_id, type);
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
            startForeground(mainNoti.getId(), mainNoti.build());
        }
    }

    // 2. HABBIT_SERVICE_INIT
    private void initService(){
        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(HabbitService.this);
        habbitDB.habbitDao().getHabbitActiveSingle(true).subscribeOn(Schedulers.io())
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
            HabbitNotification newNoti = new HabbitNotification(HabbitService.this, habbit.getId(), habbit.getType(), habbit.getTitle(), habbit.getPriority(), habbit.getState());
            notiList.add(newNoti);
            newNoti.updateNotiText("Init : "+habbit.getTitle());
        }
        mainNoti.updateNotiText("Init complete : "+ notiList.size());
    }

    // 3. HABBIT_SERVICE_EXIT
    private void exitService(){
        isCreate = false;
        if(notiList != null){
            for(HabbitNotification noti : notiList){
                noti.cancel();
            }
        }
        mainNoti.updateNotiText("HabbitService exit");
        mainNoti.cancel();
        stopForeground(true);
        stopSelf();
    }

    // 4. HABBIT_SERVICE_ADD
    private void addNotification(Habbit habbit){
        if(habbit.getId() != -1 && habbit.isActive()){
            HabbitNotification noti = new HabbitNotification(HabbitService.this, habbit.getId(), habbit.getType(), habbit.getTitle(), habbit.getPriority(), habbit.getState());
            notiList.add(noti);
            noti.updateNotiText("Init : "+habbit.getTitle());
            mainNoti.updateNotiText("#"+habbit.getId()+" "+habbit.getTitle()+" noti added");
        }
    }

    // 5. HABBIT_SERVICE_UPDATE
    private void updateNotification(Habbit habbit){
        if(habbit.getId() != -1){
            int idx = notiList.indexOf(HabbitNotification.getDummy(habbit.getId()));

            if(idx > -1){
                HabbitNotification noti = notiList.get(idx);
                if(habbit.isActive()){  // 1.1) noti exist, habbit active
                    noti.updateNotiInfo(habbit.getTitle(), habbit.getPriority());
                }else{ // 1.2) noti exist, habbit canceled
                    noti.cancel();
                    notiList.remove(idx);
                    mainNoti.updateNotiText("#"+habbit.getId()+" "+habbit.getTitle()+" noti canceled");
                }
            }else if(habbit.isActive()){ // 2) noti not exist, habbit active
                HabbitNotification newNoti = new HabbitNotification(HabbitService.this, habbit.getId(), habbit.getType(), habbit.getTitle(), habbit.getPriority(), habbit.getState());
                notiList.add(newNoti);
                newNoti.updateNotiText("Init : "+habbit.getTitle());
                mainNoti.updateNotiText("#"+habbit.getId()+" "+habbit.getTitle()+" noti actived");
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
        mainNoti.updateNotiText("Noti removed : "+count);
    }

    // 7. HABBIT_SERVICE_RECORDING
    @SuppressLint("RestrictedApi")
    private void recordAction(int hid, int type){
        long now = System.currentTimeMillis();

        HabbitDao habbitDao = HabbitDatabase.getAppDatabase(this).habbitDao();
        RecordDao recordDao = RecordDatabase.getAppDatabase(this).recordDao();
        Single.create(subscriber -> {
                    int state = habbitDao.getHabbitState(hid);
                    long recordId = -1;
                    switch (state) {
                        case Habbit.STATE_CHECK:
                            recordId = recordDao.insert(new Record(hid, type, now, -1));
                            break;
                        case Habbit.STATE_TIMER_WAIT:
                            recordId = recordDao.insert(new Record(hid, type, now, -1));
                            habbitDao.updateHabbitState(hid, Habbit.STATE_TIMER_INPROGRESS);
                            habbitDao.updateCurRecordId(hid, recordId);
                            break;
                        case Habbit.STATE_TIMER_INPROGRESS:
                            recordId = habbitDao.getCurRecordId(hid);
                            List<Record> recordList = recordDao.getRecord(recordId);

                            if (recordList.size() > 0) {
                                Record record = recordList.get(0);
                                long term = now - record.getTime();
                                recordDao.updateTerm(record.getId(), term);
                            }
                            habbitDao.updateHabbitState(hid, Habbit.STATE_TIMER_WAIT);
                            habbitDao.updateCurRecordId(hid, -1);

                            break;
                    }

                    List<Record> recordList = recordDao.getRecord(recordId);
                    if(recordList.size() >0){
                        Record record = recordList.get(0);
                        EvaluateWork work = new EvaluateWork(HabbitService.this);
                        work.work(EvaluateWork.WORK_TYPE_REPLACE_EVALUATION, hid, DateUtil.convertDate(record.getTime()));
                        subscriber.onSuccess(state);
                    }
                }
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(state -> {
                    afterRecordAction(hid, (Integer) state);
                });
    }

    private void afterRecordAction(int hid, int state){
        HabbitNotification noti = notiList.get(notiList.indexOf(HabbitNotification.getDummy(hid)));
        String message = "";
        switch(state){
            case Habbit.STATE_CHECK:
                message = "#" + hid + " " + DateUtil.getDateString(System.currentTimeMillis()) + " checked";
                break;
            case Habbit.STATE_TIMER_WAIT:
                message = "#" + hid + " " + DateUtil.getDateString(System.currentTimeMillis()) + " inprogress";
                noti.setButtonTitle(0, "STOP");
                break;
            case Habbit.STATE_TIMER_INPROGRESS:
                message = "#" + hid + " " + DateUtil.getDateString(System.currentTimeMillis()) + " timer finished";
                noti.setButtonTitle(0, "START");
                break;
        }
        noti.updateNotiText(message);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
