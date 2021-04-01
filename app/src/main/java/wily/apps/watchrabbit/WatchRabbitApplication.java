package wily.apps.watchrabbit;

import android.app.AlertDialog;
import android.app.Application;
import android.util.Log;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.data.dao.EvaluationDao;
import wily.apps.watchrabbit.data.dao.HabbitDao;
import wily.apps.watchrabbit.data.dao.RecordDao;
import wily.apps.watchrabbit.data.database.EvaluationDatabase;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.database.RecordDatabase;
import wily.apps.watchrabbit.data.entity.Evaluation;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.data.entity.Record;
import wily.apps.watchrabbit.util.DateUtil;
import wily.apps.watchrabbit.util.DialogGetter;

public class WatchRabbitApplication extends Application {

    private AlertDialog dialog;
    private static int inprogress = 0;
    private static int count = 0;

    private HabbitDao habbitDao = null;
    private EvaluationDao evalDao = null;
    private RecordDao recordDao = null;

    @Override
    public void onCreate() {
        super.onCreate();
        inprogress = 0;
        dialog = DialogGetter.getProgressDialog(WatchRabbitApplication.this, getString(R.string.base_dialog_database_inprogress));

        habbitDao = HabbitDatabase.getAppDatabase(WatchRabbitApplication.this).habbitDao();
        evalDao = EvaluationDatabase.getAppDatabase(WatchRabbitApplication.this).evaluationDao();
        recordDao = RecordDatabase.getAppDatabase(WatchRabbitApplication.this).recordDao();
    }

    private void checkWork(boolean work){
        if(work== true){
            ++inprogress;
            if(dialog.isShowing() == false){
                dialog.show();
            }
        }else{
            --inprogress;
            if(dialog.isShowing() == true && inprogress == 0){
                dialog.dismiss();
            }
        }
    }


    public void updateTotal(final int num, final boolean replace){
        checkWork(true);
        habbitDao.getAll().subscribeOn(Schedulers.io()).subscribe(list->{
            for(Habbit habbit : list){
                updateHabbit(habbit, num, replace);
            }
        });
    }

    private void updateHabbit(final Habbit habbit){
        long habbitDate = DateUtil.convertDate(habbit.getTime());
        long endDate = DateUtil.convertDate(System.currentTimeMillis());
        long before30Date = DateUtil.getDateLongBefore(endDate, 30);
        long startDate = (habbitDate > before30Date) ? before30Date : habbitDate;


        evalDao.getEvaluationByHidAndTime(habbit.getId(), startDate, endDate).subscribe(evalList ->{

            int idx = 0;
            int size = evalList.size();
            Evaluation temp = null;
            for(long cur = startDate; cur <= endDate; cur += DateUtil.ONEDAY_TO_MILLISECOND){
                if(temp == null && idx < size){
                    temp = evalList.get(idx);
                }



                if(temp != null && temp.getTime() == cur){
                    Log.d(AppConst.TAG, "Eval O : "+temp.getId()+ " "+temp.getResultCost()+" "+temp.getAchiveRate());
                    // 있다.
                    temp = null;
                    ++idx;
                }else{
                    Log.d(AppConst.TAG, "Eval X : ");
                    // 없다.
                    updateEvaluation(habbit, cur);
                }
            }
        });
    }


    // <tempo>
    public void addCheckSample(int habbitNum, final int recordNum, final long start, long end, int type){
        checkWork(true);

        String typeStr = (type==Habbit.TYPE_HABBIT_CHECK)?"체크":"타임";
        int per = (type==Habbit.TYPE_HABBIT_CHECK)?10:1;
        int state= (type==Habbit.TYPE_HABBIT_CHECK)?Habbit.STATE_CHECK:Habbit.STATE_TIMER_WAIT;
        long totalTerm  = end - start;
        ArrayList<Habbit> habbits = new ArrayList<Habbit>();


        for(int i = 0; i< habbitNum; ++i){
            habbits.add(new Habbit(type, start, typeStr+" "+count, 1, true, 100, 0, per, state, -1));
            ++count;
        }

        for(Habbit habbit : habbits){
            habbitDao.insert(habbit).subscribeOn(Schedulers.io()).subscribe(hid->{
                long hhid = hid;
                ArrayList<Record> records = new ArrayList<Record>();
                long divTerm = totalTerm/recordNum;
                for(int j = 0; j< recordNum; ++j){
                    records.add(new Record((int)hhid, habbit.getType(),start+(divTerm*j), 10 * DateUtil.MILLISECOND_TO_MINUTE));
                }
                recordDao.insertAll(records).observeOn(AndroidSchedulers.mainThread()).subscribe(res -> checkWork(false));
            });
        }
    }

    // dummy
    public void addDummyHabbit(Habbit habbit){
        checkWork(true);
        habbitDao.insert(habbit).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(res -> checkWork(false));
    }

    public void addDummyRecord(Record record){
        checkWork(true);
        recordDao.insert(record).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(res -> checkWork(false));
    }

    public void addDummyEvaluation(Evaluation eval){
        checkWork(true);
        evalDao.insert(eval).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(res -> checkWork(false));
    }
}
