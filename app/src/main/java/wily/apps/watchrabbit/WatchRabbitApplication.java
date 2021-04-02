package wily.apps.watchrabbit;

import android.app.AlertDialog;
import android.app.Application;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

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

    private static int count = 0;

    private HabbitDao habbitDao = null;
    private EvaluationDao evalDao = null;
    private RecordDao recordDao = null;

    @Override
    public void onCreate() {
        super.onCreate();
        habbitDao = HabbitDatabase.getAppDatabase(WatchRabbitApplication.this).habbitDao();
        evalDao = EvaluationDatabase.getAppDatabase(WatchRabbitApplication.this).evaluationDao();
        recordDao = RecordDatabase.getAppDatabase(WatchRabbitApplication.this).recordDao();

        listnerList = new ArrayList<>();
    }

    public void addListner(OnProcessFinsishedListener listener){
        listnerList.add(listener);
    }

    public void unRegisterListner(OnProcessFinsishedListener listener){
        listnerList.remove(listener);
    }

    private ArrayList<OnProcessFinsishedListener> listnerList = null;
    public interface OnProcessFinsishedListener{
        public void onStart();
        public void onFinish();
    }

    private void checkWork(boolean work){
        if(work== true){
            for(OnProcessFinsishedListener listener : listnerList){
                listener.onStart();
            }
        }else{
            for(OnProcessFinsishedListener listener : listnerList){
                listener.onFinish();
            }
        }
    }


    public void updateTotal(final int numOfDay, final boolean replace){
        //checkWork(true);
        habbitDao.getAllSingle().subscribeOn(Schedulers.io()).doOnSuccess(list->{
            Log.d(AppConst.TAG, "updateTotal Habbit size >>> "+list.size());
            int count = 0;
            for(Habbit habbit : list){
                if(replace){
                    ++count;
                    Log.d(AppConst.TAG, "updateTotal Habbit "+habbit+" "+count);
                    long endDate = DateUtil.convertDate(System.currentTimeMillis());
                    long beforeDate = DateUtil.getDateLongBefore(endDate, numOfDay);
                    long habbitDate = DateUtil.convertDate(habbit.getTime());
                    long startDate = Math.max(habbitDate, beforeDate);
//                    evalDao.deleteEvaluationByTime(habbit.getId(), startDate, endDate);

                    ArrayList<Evaluation> evalList = new ArrayList<>();
                    evalList.add(makeEval(habbit, new ArrayList<Record>(), startDate));
//                    for(long cur = startDate; cur <= endDate; cur += DateUtil.ONEDAY_TO_MILLISECOND){
//                        List<Record> recordList = recordDao.getRecordByHidAndTime(habbit.getId(), cur, cur+DateUtil.ONEDAY_TO_MILLISECOND-1);
//                        Evaluation evaluation = makeEval(habbit, recordList, cur);
//                        Log.d(AppConst.TAG, ""+evaluation);
//                        evalList.add(evaluation);
//                    }
                    evalDao.insertAll(evalList);
                }else{
                    updateAllEvaluation(habbit, numOfDay);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(res -> checkWork(false));
    }

    private void replaceAllEvaluation(Habbit habbit, final int numOfDay){

    }

    private void updateAllEvaluation(Habbit habbit, final int numOfDay){
        long endDate = DateUtil.convertDate(System.currentTimeMillis());
        long beforeDate = DateUtil.getDateLongBefore(endDate, numOfDay);
        long habbitDate = DateUtil.convertDate(habbit.getTime());
        long startDate = Math.max(habbitDate, beforeDate);

        ArrayList<Evaluation> addList = new ArrayList<>();

        List<Evaluation> curList = evalDao.getEvaluationByHidAndTime(habbit.getId(), startDate, endDate);
        int idx = 0;
        int size = curList.size();
        Evaluation temp = null;

        for(long cur = startDate; cur <= endDate; cur += DateUtil.ONEDAY_TO_MILLISECOND){

            if(temp == null && idx < size){
                temp = curList.get(idx);
            }

            if(temp != null && temp.getTime() == cur){
                temp = null;
                ++idx;
            }else{
                List<Record> list = recordDao.getRecordByHidAndTime(habbit.getId(), cur, cur+DateUtil.ONEDAY_TO_MILLISECOND-1);
                Evaluation eval = makeEval(habbit, list, cur);
                Log.d(AppConst.TAG, ""+eval);
                addList.add(eval);
            }
        }
        evalDao.insertAll(addList);
    }

    private Evaluation makeEval(final Habbit habbit, List<Record> list, long day) {
        // sum
        int sum = 0;
        if (habbit.getType() == Habbit.TYPE_HABBIT_CHECK) {
            sum = list.size();
        } else if (habbit.getType() == Habbit.TYPE_HABBIT_TIMER) {
            for (Record record : list) {
                sum += record.getTerm();
            }
            sum = (int)(sum/DateUtil.MILLISECOND_TO_MINUTE);
        }

        // process
        int result = habbit.getInitCost() + (sum * habbit.getPerCost());
        int rate = (int) ((result / (double) habbit.getGoalCost()) * 100);
        return new Evaluation(habbit.getId(), day, result, rate);
    }


    // <tempo>
    public void addSamples2(int habbitNum, final int recordNum, final long start, long end, int type){
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

        habbitDao.insertAllSingle(habbits).subscribeOn(Schedulers.io()).doOnSuccess(idList->{
            for(long hhid : idList){
                ArrayList<Record> records = new ArrayList<Record>();
                long divTerm = totalTerm/recordNum;
                for(int j = 0; j< recordNum; ++j){
                    records.add(new Record((int)hhid, type,start+(divTerm*j), 10 * DateUtil.MILLISECOND_TO_MINUTE));
                }
                recordDao.insertAllSingle(records);
                Log.d(AppConst.TAG, ">>>"+hhid+" "+records.size());
            }
        }).subscribe(res -> updateTotal(20, true));
    }
    /////////////

    public void addSamples(int habbitNum, final int recordNum, final long start, long end, int type){
        checkWork(true);

        new Thread(){
            @Override
            public void run() {
                super.run();
                String typeStr = (type==Habbit.TYPE_HABBIT_CHECK)?"체크":"타임";
                int per = (type==Habbit.TYPE_HABBIT_CHECK)?10:1;
                int state= (type==Habbit.TYPE_HABBIT_CHECK)?Habbit.STATE_CHECK:Habbit.STATE_TIMER_WAIT;
                long totalTerm  = end - start;
                ArrayList<Habbit> habbits = new ArrayList<Habbit>();

                for(int i = 0; i< habbitNum; ++i){
                    habbits.add(new Habbit(type, start, typeStr+" "+count, 1, true, 100, 0, per, state, -1));
                    ++count;
                }


                List<Long> list = habbitDao.insertAll(habbits);

                for(long hhid : list){
                    ArrayList<Record> records = new ArrayList<Record>();
                    long divTerm = totalTerm/recordNum;
                    for(int j = 0; j< recordNum; ++j){
                        records.add(new Record((int)hhid, type,start+(divTerm*j), 10 * DateUtil.MILLISECOND_TO_MINUTE));
                    }
                    recordDao.insertAll(records);
                    Log.d(AppConst.TAG, ">>>"+hhid+" "+records.size());
                }

            }
        }.start();
    }
}
