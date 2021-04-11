package wily.apps.watchrabbit.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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

    private int hid = -1;
    private long date = -1;
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
            replaceTotal(3);
            updateHabbitEvaluation();
        }
    }

    public void updateHabbitEvaluation(){
        int numOfDay = 30;
        // 1) Get habbit all
        List<Habbit> habbits = habbitDao.getAll();

        // 2) Per Habbit
        long endDate = DateUtil.convertDate(System.currentTimeMillis());
        long beforeDate = DateUtil.getDateLongBefore(endDate, numOfDay);
        for(Habbit habbit : habbits) {
            long habbitDate = DateUtil.convertDate(habbit.getTime());
            long startDate = Math.max(habbitDate, beforeDate);

            // 2.1) Get evaluation by 30 day
            List<Evaluation> evalList = evalDao.getEvaluationByHidAndTimeDESC(habbit.getId(), startDate, endDate);

            long p_currentResultCost = 0;
            long p_currentAchiveRate = 0;
            long p_day7ResultCost = 0;
            long p_day7AchiveRate = 0;
            long p_day30ResultCost = 0;
            long p_day30AchiveRate = 0;

            // 2.1.1) today
            int currentSize = (int) IntStream.range(0, evalList.size()).filter(i -> i < 1).count();
            p_currentResultCost = IntStream.range(0, evalList.size()).filter(i -> i < 1).mapToObj(i -> evalList.get(i)).mapToInt(o -> o.getResultCost()).sum() / currentSize;
            p_currentAchiveRate = IntStream.range(0, evalList.size()).filter(i -> i < 1).mapToObj(i -> evalList.get(i)).mapToInt(o -> o.getAchiveRate()).sum() / currentSize;

            // 2.1.2) 7 day
            int day7size = (int) IntStream.range(0, evalList.size()).filter(i -> i < 7).count();
            p_day7ResultCost = IntStream.range(0, evalList.size()).filter(i -> i < 7).mapToObj(i -> evalList.get(i)).mapToInt(o -> o.getResultCost()).sum() / day7size;
            p_day7AchiveRate = IntStream.range(0, evalList.size()).filter(i -> i < 7).mapToObj(i -> evalList.get(i)).mapToInt(o -> o.getAchiveRate()).sum() / day7size;

            // 2.1.3) 30 day
            int day30size = (int) IntStream.range(0, evalList.size()).filter(i -> i < 30).count();
            p_day30ResultCost = IntStream.range(0, evalList.size()).filter(i -> i < 30).mapToObj(i -> evalList.get(i)).mapToInt(o -> o.getResultCost()).sum() / day30size;
            p_day30AchiveRate = IntStream.range(0, evalList.size()).filter(i -> i < 30).mapToObj(i -> evalList.get(i)).mapToInt(o -> o.getAchiveRate()).sum() / day30size;

            // 2.2) Update Habbit
            habbitDao.updateHabbitEvaluation(habbit.getId(), p_currentResultCost, p_currentAchiveRate, p_day7ResultCost, p_day7AchiveRate, p_day30ResultCost, p_day30AchiveRate);
        }
    }

    public void updateTotal(int numOfDay){
        // 1) Get habbit all
        List<Habbit> habbits = habbitDao.getAll();

        // 2) Per Habbit
        long endDate = DateUtil.convertDate(System.currentTimeMillis());
        long beforeDate = DateUtil.getDateLongBefore(endDate, numOfDay);
        for(Habbit habbit : habbits){
            long habbitDate = DateUtil.convertDate(habbit.getTime());
            long startDate = Math.max(habbitDate, beforeDate);

            // 2.1) Get evaluation by term all
            int idx = 0;
            List<Evaluation> oldList = evalDao.getEvaluationByHidAndTime(habbit.getId(), startDate, endDate);

            int size = oldList.size();
            Evaluation temp = null;
            ArrayList<Evaluation> evalList = new ArrayList<>();
            // 2.2) Per Evaluation at date
            for(long cur = startDate; cur < (endDate + DateUtil.ONEDAY_TO_MILLISECOND); cur += DateUtil.ONEDAY_TO_MILLISECOND) {
                if(temp == null && idx < size){
                    temp = oldList.get(idx);
                }

                // 2.2.1) Evaluation exist
                if(temp != null && temp.getTime() == cur){
                    temp = null;
                    ++idx;
                    continue;
                }else{  // 2.2.2) Evaluation not exist, make Evaluation
                    Evaluation evaluation = makeEvaluation(habbit, cur);
                    evalList.add(evaluation);
                }
            }
            // 2.3) Insert evaluations (not exist)
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
