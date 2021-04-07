package wily.apps.watchrabbit.service;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import wily.apps.watchrabbit.AppConst;
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

public class EvaluateThread extends Thread{

    private Context mContext = null;
    private HabbitDao habbitDao = null;
    private EvaluationDao evalDao = null;
    private RecordDao recordDao = null;

    private boolean replace = false;

    public EvaluateThread(Context context, boolean replace){
        mContext = context;

        habbitDao = HabbitDatabase.getAppDatabase(mContext).habbitDao();
        evalDao = EvaluationDatabase.getAppDatabase(mContext).evaluationDao();
        recordDao = RecordDatabase.getAppDatabase(mContext).recordDao();
        this.replace = replace;
    }

    @Override
    public void run() {
        super.run();

        if(replace){
            replaceTotal(30);
        }else{
            updateTotal(30);
//            replaceTotal(3);
        }
    }

    public void updateTotal(int numOfDay){
        // 1) Get habbit all
        List<Habbit> habbits = habbitDao.getAll();
        long endDate = DateUtil.convertDate(System.currentTimeMillis());
        long beforeDate = DateUtil.getDateLongBefore(endDate, numOfDay);

        // 2) Habbit Replace
        for(Habbit habbit : habbits){
            long habbitDate = DateUtil.convertDate(habbit.getTime());
            long startDate = Math.max(habbitDate, beforeDate);

            // 2.1) Get evaluation by term all
            int idx = 0;
            List<Evaluation> oldList = evalDao.getEvaluationByHidAndTime(habbit.getId(), startDate, endDate);
            int size = oldList.size();
            Evaluation temp = null;

            // 2.2) if null, Make evaluation

            ArrayList<Evaluation> evalList = new ArrayList<>();
            for(long cur = startDate; cur < (endDate + DateUtil.ONEDAY_TO_MILLISECOND); cur += DateUtil.ONEDAY_TO_MILLISECOND) {
//                Log.d(AppConst.TAG, ">"+DateUtil.getDateString(startDate)+" ~ "+DateUtil.getDateString(endDate) +" = "+DateUtil.getDateString(cur));
                if(temp == null && idx < size){
                    temp = oldList.get(idx);
                }


                if(temp != null && temp.getTime() == cur){
                    temp = null;
                    ++idx;
                    continue;
                }else{
                    Evaluation evaluation = makeEvaluation(habbit, cur);
                    evalList.add(evaluation);
                }
            }
            // 2.3) Insert evaluation
            evalDao.insertAll(evalList);
        }
    }

    public void replaceTotal(int numOfDay){
        // 1) Get habbit all
        List<Habbit> habbits = habbitDao.getAll();
        long endDate = DateUtil.convertDate(System.currentTimeMillis());
        long beforeDate = DateUtil.getDateLongBefore(endDate, numOfDay);

        // 2) Habbit Replace
        for(Habbit habbit : habbits){
            long habbitDate = DateUtil.convertDate(habbit.getTime());
            long startDate = Math.max(habbitDate, beforeDate);

            // 2.1) Delete evaluation by term all
            evalDao.deleteEvaluationByTime(habbit.getId(), startDate, endDate);

            // 2.2) Make evaluation by term all
            ArrayList<Evaluation> evalList = new ArrayList<>();
            for(long cur = startDate; cur <= endDate; cur += DateUtil.ONEDAY_TO_MILLISECOND) {
                Evaluation evaluation = makeEvaluation(habbit, cur);
                evalList.add(evaluation);
            }
            // 2.3) Insert evaluation
            evalDao.insertAll(evalList);
        }
    }

    private Evaluation makeEvaluation(final Habbit habbit, long day) {
        // - 1 record list
        List<Record> recordList = recordDao.getRecordByHidAndTime(habbit.getId(), day, day + DateUtil.ONEDAY_TO_MILLISECOND-1);

        // - 2 sum
        int sum = 0;
        switch(habbit.getType()){
            case Habbit.TYPE_HABBIT_CHECK:
                sum = recordList.size();
                break;
            case Habbit.TYPE_HABBIT_TIMER:
                for (Record record : recordList) {
                    if(record.getTerm() >0) {
                        sum += record.getTerm();
                    }
                }
                sum = (int)(sum/DateUtil.MILLISECOND_TO_MINUTE);
                break;
        }

        // - 3 calculate
        int result = habbit.getInitCost() + (sum * habbit.getPerCost());
        int rate = (int) ((result / (double) habbit.getGoalCost()) * 100);
        return new Evaluation(habbit.getId(), day, result, rate);
    }
}
