package wily.apps.watchrabbit;

import android.content.Context;
import android.content.SharedPreferences;

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

import static android.content.Context.MODE_PRIVATE;

public class EvaluateWork{

    private Context mContext = null;
    private HabbitDao habbitDao = null;
    private EvaluationDao evalDao = null;
    private RecordDao recordDao = null;

    public static final int WORK_TYPE_REPLACE_ALL = 1;

    public static final int WORK_TYPE_UPDATE_ALL  = 2;
    public static final int WORK_TYPE_REPLACE_HABBIT = 3;
    public static final int WORK_TYPE_REPLACE_EVALUATION = 4;

    public EvaluateWork(Context context){
        mContext = context;

        habbitDao = HabbitDatabase.getAppDatabase(mContext).habbitDao();
        evalDao = EvaluationDatabase.getAppDatabase(mContext).evaluationDao();
        recordDao = RecordDatabase.getAppDatabase(mContext).recordDao();
    }

    public void work(int workType, int hid, long date) {
        int numOfDay = 30;
        List<Habbit> habbits = null;
        Evaluation evaluation = null;
        switch(workType){
            case WORK_TYPE_REPLACE_ALL:
                habbits = habbitDao.getAll();
                for(Habbit habbit : habbits) {
                    replaceEvaluationByHabbit(habbit, numOfDay);
                    updateHabbitResult(habbit, numOfDay);
                }
                setPrefCurrentDate();
                break;
            case WORK_TYPE_UPDATE_ALL:
                updateEvaluationAll(numOfDay);
                setPrefCurrentDate();
                break;
            case WORK_TYPE_REPLACE_HABBIT:
                habbits = habbitDao.getHabbit(hid);
                if(habbits.size() > 0){
                    replaceEvaluationByHabbit(habbits.get(0), numOfDay);
                    updateHabbitResult(habbits.get(0), numOfDay);
                }
                break;
            case WORK_TYPE_REPLACE_EVALUATION:
                evalDao.deleteEvaluationByTerm(hid, date);
                habbits = habbitDao.getHabbit(hid);
                if(habbits.size() > 0){
                    evaluation = makeEvaluationByDate(habbits.get(0), date);
                    evalDao.insert(evaluation);

                    updateHabbitResult(habbits.get(0), numOfDay);
                }
                break;
        }
    }

    private void updateEvaluationAll(int numOfDay) {
        // 1. Get recent update date
        SharedPreferences pref = mContext.getSharedPreferences(AppConst.WORK_PREF, MODE_PRIVATE);
        long recentUpdate = pref.getLong(AppConst.WORK_PREF_RECENT_UPDATE_KEY, -1);

        // 2. Update evaluation per Habbit
        List<Habbit> habbits = habbitDao.getAll();
        for(Habbit habbit : habbits) {

            // 2.1 Update and Replace
            evalDao.deleteEvaluationByTerm(habbits.get(0).getId(), recentUpdate);
            updateEvaluationByHabbit(habbit, numOfDay);
            replaceEvaluationByHabbit(habbit, 2);

            // 2.2 Update old evaluation
            if(recentUpdate != -1){
//                Log.d(AppConst.TAG, "Recent Update Date : "+DateUtil.getDateString(recentUpdate));
                Evaluation evaluation = makeEvaluationByDate(habbits.get(0), recentUpdate);
                evalDao.insert(evaluation);
            }

            // 2.3 Update habbit result
            updateHabbitResult(habbit, numOfDay);
        }
    }

    private void setPrefCurrentDate() {
        long currentTime = System.currentTimeMillis();
        SharedPreferences pref = mContext.getSharedPreferences(AppConst.WORK_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putLong(AppConst.WORK_PREF_RECENT_UPDATE_KEY, DateUtil.convertDate(currentTime));
        editor.commit();
    }

    private void updateEvaluationByHabbit(final Habbit habbit, int numOfDay) {
        long endDate = DateUtil.convertDate(System.currentTimeMillis());
        long beforeDate = DateUtil.getDateLongBefore(endDate, numOfDay);
        long habbitDate = DateUtil.convertDate(habbit.getTime());
        long startDate = Math.max(habbitDate, beforeDate);

        // 1) Get evaluation by term all
        int idx = 0;
        List<Evaluation> oldList = evalDao.getEvaluationByHidAndTerm(habbit.getId(), startDate, endDate);

        int size = oldList.size();
        Evaluation temp = null;

        // 2) Per Evaluation at date
        ArrayList<Evaluation> evalList = new ArrayList<>();
        long limitedTime = (endDate + DateUtil.ONEDAY_TO_MILLISECOND);
        for(long cur = startDate; cur < limitedTime; cur += DateUtil.ONEDAY_TO_MILLISECOND) {
            if(temp == null && idx < size){
                temp = oldList.get(idx);
            }

            // 2.1) Evaluation exist
            if(temp != null && temp.getTime() == cur){
                temp = null;
                ++idx;
                continue;
            }else{  // 2.2) Evaluation not exist, make Evaluation
                Evaluation evaluation = makeEvaluationByDate(habbit, cur);
                evalList.add(evaluation);
            }
        }
        evalDao.insertAll(evalList);
    }

    private void replaceEvaluationByHabbit(final Habbit habbit, int numOfDay) {
        long endDate = DateUtil.convertDate(System.currentTimeMillis());
        long beforeDate = DateUtil.getDateLongBefore(endDate, numOfDay);
        long habbitDate = DateUtil.convertDate(habbit.getTime());
        long startDate = Math.max(habbitDate, beforeDate);

        // 1) Delete evaluation by term all
        evalDao.deleteEvaluationByTerm(habbit.getId(), startDate, endDate);

        // 2) Make evaluation by term all
        ArrayList<Evaluation> evalList = new ArrayList<>();
        long limitedTime = (endDate + DateUtil.ONEDAY_TO_MILLISECOND);
        for(long cur = startDate; cur < limitedTime; cur += DateUtil.ONEDAY_TO_MILLISECOND) {
            Evaluation evaluation = makeEvaluationByDate(habbit, cur);
            evalList.add(evaluation);
        }
        // 3) Insert evaluation
        evalDao.insertAll(evalList);
    }

    private Evaluation makeEvaluationByDate(final Habbit habbit, long date) {
        // - 1 record list
        List<Record> recordList = recordDao.getRecordByHidAndTerm(habbit.getId(), date, date + DateUtil.ONEDAY_TO_MILLISECOND-1);

        // - 2 sum
        int sum = 0;
        switch(habbit.getType()){
            case Habbit.TYPE_HABBIT_CHECK:
                sum = recordList.size();
                break;
            case Habbit.TYPE_HABBIT_TIMER:
                for (Record record : recordList) {
                    if(record.getTerm() > 0) {
                        sum += record.getTerm();
                    }
                }
                sum = (int)(sum/DateUtil.MILLISECOND_TO_MINUTE);
                break;
        }

        // - 3 calculate
        int result = habbit.getInitCost() + (sum * habbit.getPerCost());
        int rate = (int) ((result / (double) habbit.getGoalCost()) * 100);
        return new Evaluation(habbit.getId(), date, result, rate);
    }

    private void updateHabbitResult(final Habbit habbit, int numOfDay) {
        long endDate = DateUtil.convertDate(System.currentTimeMillis());
        long beforeDate = DateUtil.getDateLongBefore(endDate, numOfDay);
        long habbitDate = DateUtil.convertDate(habbit.getTime());
        long startDate = Math.max(habbitDate, beforeDate);

        // 1) Get evaluation (numOfDay)
        List<Evaluation> evalList = evalDao.getEvaluationByHidAndTermDESC(habbit.getId(), startDate, endDate);

        long p_currentResultCost = 0;
        long p_currentAchiveRate = 0;
        long p_day7ResultCost = 0;
        long p_day7AchiveRate = 0;
        long p_day30ResultCost = 0;
        long p_day30AchiveRate = 0;

        // 2.1) today
        int currentSize = (int) IntStream.range(0, evalList.size()).filter(i -> i < 1).count();
        p_currentResultCost = IntStream.range(0, evalList.size()).filter(i -> i < 1).mapToObj(i -> evalList.get(i)).mapToInt(o -> o.getResultCost()).sum() / currentSize;
        p_currentAchiveRate = IntStream.range(0, evalList.size()).filter(i -> i < 1).mapToObj(i -> evalList.get(i)).mapToInt(o -> o.getAchiveRate()).sum() / currentSize;

        // 2.2) 7 day
        int day7size = (int) IntStream.range(0, evalList.size()).filter(i -> i < 7).count();
        p_day7ResultCost = IntStream.range(0, evalList.size()).filter(i -> i < 7).mapToObj(i -> evalList.get(i)).mapToInt(o -> o.getResultCost()).sum() / day7size;
        p_day7AchiveRate = IntStream.range(0, evalList.size()).filter(i -> i < 7).mapToObj(i -> evalList.get(i)).mapToInt(o -> o.getAchiveRate()).sum() / day7size;

        // 2.3) 30 day
        int day30size = (int) IntStream.range(0, evalList.size()).filter(i -> i < 30).count();
        p_day30ResultCost = IntStream.range(0, evalList.size()).filter(i -> i < 30).mapToObj(i -> evalList.get(i)).mapToInt(o -> o.getResultCost()).sum() / day30size;
        p_day30AchiveRate = IntStream.range(0, evalList.size()).filter(i -> i < 30).mapToObj(i -> evalList.get(i)).mapToInt(o -> o.getAchiveRate()).sum() / day30size;

        // 3) Update Habbit
        habbitDao.updateHabbitEvaluation(habbit.getId(), p_currentResultCost, p_currentAchiveRate, p_day7ResultCost, p_day7AchiveRate, p_day30ResultCost, p_day30AchiveRate);
    }
}
