package wily.apps.watchrabbit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.data.dao.AlarmDao;
import wily.apps.watchrabbit.data.dao.HabbitDao;
import wily.apps.watchrabbit.data.dao.RecordDao;
import wily.apps.watchrabbit.data.database.AlarmDatabase;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.database.RecordDatabase;
import wily.apps.watchrabbit.data.entity.Alarm;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.data.entity.Record;
import wily.apps.watchrabbit.service.HabbitService;
import wily.apps.watchrabbit.util.DateUtil;
import wily.apps.watchrabbit.util.DialogGetter;
import wily.apps.watchrabbit.util.Utils;

public class SplashActivity extends AppCompatActivity {
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        dialog = DialogGetter.getProgressDialog(SplashActivity.this, getString(R.string.base_dialog_database_inprogress));
        dialog.show();

        createHabbitService();

        Completable.create(subscriber -> {
            onBackgroundWork();
            subscriber.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> afterBackgroundWork());
    }

    private void createHabbitService() {
        if(!Utils.isServiceRunning(SplashActivity.this, HabbitService.class.getName())){
            Intent intent = new Intent(SplashActivity.this, HabbitService.class);
            intent.setAction(HabbitService.HABBIT_SERVICE_CREATE);
            startService(intent);
        }
    }

    private void onBackgroundWork(){
        long sTime = DateUtil.getDateLong(2021, Calendar.APRIL, 23, 0, 0, 0);
        long cTime = System.currentTimeMillis();
        addSamples(2, 10, sTime, cTime, Habbit.TYPE_HABBIT_CHECK);
        addSamples(2, 10, sTime, cTime, Habbit.TYPE_HABBIT_TIMER);

        EvaluateWork work = new EvaluateWork(SplashActivity.this);
        work.work(EvaluateWork.WORK_TYPE_UPDATE_ALL, -1, -1);
    }

    private void afterBackgroundWork(){
        dialog.dismiss();
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // TEMPO
    private void addSamples(int habbitNum, final int recordNum, final long start, long end, int type){
        // 1) init
        HabbitDao habbitDao = HabbitDatabase.getAppDatabase(SplashActivity.this).habbitDao();
        RecordDao recordDao = RecordDatabase.getAppDatabase(SplashActivity.this).recordDao();
        AlarmDao alarmDao = AlarmDatabase.getAppDatabase(SplashActivity.this).alarmDao();
        int count = 0;

        String typeStr = (type== Habbit.TYPE_HABBIT_CHECK)?"체크":"타임";
        int per = (type==Habbit.TYPE_HABBIT_CHECK)?10:1;
        int state= (type==Habbit.TYPE_HABBIT_CHECK)?Habbit.STATE_CHECK:Habbit.STATE_TIMER_WAIT;
        long totalTerm  = end - start;
        ArrayList<Habbit> habbits = new ArrayList<Habbit>();

        // 2) habbit sample
        for(int i = 0; i< habbitNum; ++i){
            habbits.add(new Habbit(type, start, typeStr+" "+count, 1, true, 100, 0, per, state, -1));
            ++count;
        }

        // 3) insert habbits
        List<Long> idList = habbitDao.insertAll(habbits);
        long[] sampleAlarm = {DateUtil.getDateLong(2020, 0, 8, 8, 30, 10),
                DateUtil.getDateLong(2020, 0, 8, 11, 45, 14),
                DateUtil.getDateLong(2020, 0, 8, 13, 25, 22),
                DateUtil.getDateLong(2020, 0, 8, 16, 0, 10),
                DateUtil.getDateLong(2020, 0, 8, 20, 11, 10)};

        for(long hhid : idList){
            // 3.1) Record sample
            ArrayList<Record> records = new ArrayList<Record>();
            long divTerm = totalTerm/recordNum;
            for(int j = 0; j< recordNum; ++j){
                records.add(new Record((int)hhid, type,start+(divTerm*j), 10 * DateUtil.MILLISECOND_TO_MINUTE));
            }
            // insert records
            recordDao.insertAll(records);

            // 3.2) Alarm sample
            ArrayList<Alarm> alarms = new ArrayList<Alarm>();
            for(int j = 0; j< 1; ++j){
                alarms.add(new Alarm((int)hhid, "아침"+j, sampleAlarm[j],30, 20));
            }

            // insert alarms
            alarmDao.insertAll(alarms);
        }

        if(Utils.isServiceRunning(SplashActivity.this, HabbitService.class.getName())){
            Intent intent = new Intent(SplashActivity.this, HabbitService.class);
            intent.setAction(HabbitService.HABBIT_SERVICE_INIT);
            startService(intent);
        }
    }
}