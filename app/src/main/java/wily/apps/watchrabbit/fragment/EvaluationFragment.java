package wily.apps.watchrabbit.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.AppConst;
import wily.apps.watchrabbit.HabbitModifyActivity;
import wily.apps.watchrabbit.MainActivity;
import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.adapter.HabbitAdapter;
import wily.apps.watchrabbit.adapter.RecordAdapter;
import wily.apps.watchrabbit.data.database.EvaluationDatabase;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.database.RecordDatabase;
import wily.apps.watchrabbit.data.entity.Evaluation;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.data.entity.Record;
import wily.apps.watchrabbit.util.DateUtil;
import wily.apps.watchrabbit.util.DialogGetter;
import wily.apps.watchrabbit.RecordModifyDialog;

public class EvaluationFragment extends Fragment {
    public boolean init = false;

    // UI
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(init == false){
            init = true;
            addDummyData();
        }
        evaluateRecords();

        View view = inflater.inflate(R.layout.fragment_evaluation, container, false);
        return view;
    }

    // Dummy
    private void evaluateRecords()
    {

        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(getContext());
        EvaluationDatabase evalDB = EvaluationDatabase.getAppDatabase(getContext());
        RecordDatabase recordDB = RecordDatabase.getAppDatabase(getContext());
        final Calendar cal = Calendar.getInstance();
        // HABBIT
        habbitDB.habbitDao().getAll().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(habbitItem -> {
                    ArrayList<Habbit> habbitList = (ArrayList<Habbit>)habbitItem;
                    for(Habbit habbit : habbitList){
                        final long habbitStart = habbit.getTime();
                        final int hid = habbit.getId();
                        // EVAL
                        evalDB.evaluationDao().getEvaluationByHid(hid).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(evalItem -> {

                                    long nextTime = habbitStart;
                                    int nextYear = DateUtil.getDateNum(nextTime, Calendar.YEAR);
                                    int nextMonth = DateUtil.getDateNum(nextTime, Calendar.DAY_OF_MONTH);
                                    int nextDay = DateUtil.getDateNum(nextTime, Calendar.MONTH);

                                    ArrayList<Evaluation> evalList = (ArrayList<Evaluation>)evalItem;
                                    for(Evaluation eval : evalList){
                                        long evalTime = eval.getTime();
                                        int evalYear = DateUtil.getDateNum(evalTime, Calendar.YEAR);
                                        int evalMonth = DateUtil.getDateNum(evalTime, Calendar.DAY_OF_MONTH);
                                        int evalDay = DateUtil.getDateNum(evalTime, Calendar.MONTH);

                                        if(nextTime > evalTime){
                                            continue;
                                        }else if(nextYear == evalYear && nextMonth == evalMonth && nextDay == evalDay){

                                            cal.setTime(new Date(nextTime));
                                            cal.add(Calendar.DAY_OF_MONTH, 1);

                                            nextTime = cal.getTime().getTime();
                                            nextYear = DateUtil.getDateNum(nextTime, Calendar.YEAR);
                                            nextMonth = DateUtil.getDateNum(nextTime, Calendar.DAY_OF_MONTH);
                                            nextDay = DateUtil.getDateNum(nextTime, Calendar.MONTH);
                                            continue;
                                        }else{
                                            cal.setTime(new Date(nextTime));
                                            cal.add(Calendar.DAY_OF_MONTH, 1);

                                            long nextnextTime = cal.getTime().getTime();
                                            // RECORD_NOT_STOP
                                            long finalNextTime = nextTime;
                                            recordDB.recordDao().getRecordByHidDateNotStop(hid, nextTime, nextnextTime).subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(notStopItem -> {

                                                        long total = 0;
                                                        ArrayList<Record> recordList = (ArrayList<Record>)notStopItem;
                                                        if(habbit.getType()== Habbit.TYPE_HABBIT_CHECK){
                                                            total += recordList.size();
                                                            //int hid, int type, long time, int goalCost, int initCost, int sumCost, int resultCost, int achive
                                                            int evalCost = (int) (total * habbit.getPerCost());
                                                            int result = habbit.getInitCost()+evalCost;
                                                            int achive = (int) ((long)((double)result / habbit.getGoalCost())*100);
                                                            evalDB.evaluationDao().insert(new Evaluation(hid, Habbit.TYPE_HABBIT_CHECK, finalNextTime, habbit.getGoalCost(), habbit.getInitCost(), evalCost, result, achive)).subscribeOn(Schedulers.io())
                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                    .subscribe(eval2->{


                                                                    });
                                                        }
                                            });
                                            // RECORD_NOT_STOP
                                            cal.setTime(new Date(nextTime));
                                            cal.add(Calendar.DAY_OF_MONTH, 1);

                                            nextTime = cal.getTime().getTime();
                                            nextYear = DateUtil.getDateNum(nextTime, Calendar.YEAR);
                                            nextMonth = DateUtil.getDateNum(nextTime, Calendar.DAY_OF_MONTH);
                                            nextDay = DateUtil.getDateNum(nextTime, Calendar.MONTH);
                                        }
                                    }
                                });
                        // EVAL
                    }
                });
        // HABBIT
        evalDB.evaluationDao().getAll().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(eval->{
                ArrayList<Evaluation> evalList = (ArrayList<Evaluation>)eval;
                for(Evaluation e : evalList){
                    Log.d(AppConst.TAG, "Eval : "+e.getTime()+" "+e.getSumCost()+ " "+e.getAchive());
                }
            });

    }

    private void addDummyData()
    {
        final long startTime = DateUtil.getDateLong(2021, 3, 19, 0, 0, 0);
        final long stopTime  = DateUtil.getDateLong(2021, 3, 22, 20, 10, 23);
        final long total = stopTime - startTime;

        RecordDatabase recordDB = RecordDatabase.getAppDatabase(getContext());
        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(getContext());

        habbitDB.habbitDao().insert(new Habbit(Habbit.TYPE_HABBIT_CHECK, startTime, "매일 3회 기도하기", 2, true, 90, 0, 30)).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(item -> {
                final int hid = (int)((long)item);
                final int num = 10;
                final long term = total / num;

                for(int i = 0; i<num; ++i) {
                    recordDB.recordDao().insert(new Record(hid, Habbit.TYPE_HABBIT_CHECK, startTime + (i * term), Record.RECORD_STATE_CHECK, -1)).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe();
                }
            });
        habbitDB.habbitDao().insert(new Habbit(Habbit.TYPE_HABBIT_TIMER, startTime, "명상 하기 25분", 3, true, 30, -20, 2)).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(item -> {
                final int hid = (int)((long)item);
                final int num = 15;
                final long term = total / num;
                final long due = 15 * DateUtil.MILLISECOND_TO_MINUTE;

                for(int i = 0; i<num; ++i){
                    int finalI = i;
                    recordDB.recordDao().insert(new Record(hid, Habbit.TYPE_HABBIT_TIMER, startTime +(finalI*term), Record.RECORD_STATE_TIMER_START, -1)).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(item2 -> {
                                recordDB.recordDao().insert(new Record(-1, Habbit.TYPE_HABBIT_TIMER, startTime +(finalI *term) + due, Record.RECORD_STATE_TIMER_STOP, item2)).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe();
                            });
                }
            });
        habbitDB.habbitDao().insert(new Habbit(Habbit.TYPE_HABBIT_CHECK, startTime, "술은 매일 한잔", 4, true, 0, 30, -30)).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(item -> {
                final int hid = (int)((long)item);
                final int num = 20;
                final long term = total / num;

                for(int i = 0; i<num; ++i) {
                    recordDB.recordDao().insert(new Record(hid, Habbit.TYPE_HABBIT_CHECK, startTime + (i * term), Record.RECORD_STATE_CHECK, -1)).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe();
                }
            });

        /*
                habbitDB.habbitDao().insert(new Habbit(Habbit.TYPE_HABBIT_CHECK, startTime, "더미 체크 1", 1, false, 100, 0, 30)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    final int hid = (int)((long)item);
                    final int num = 1;
                    final long term = total / num;

                    for(int i = 0; i<num; ++i){
                        recordDB.recordDao().insert(new Record(hid, Habbit.TYPE_HABBIT_CHECK, startTime +(i*term), Record.RECORD_STATE_CHECK, -1)).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe();
                    }
                });
        habbitDB.habbitDao().insert(new Habbit(Habbit.TYPE_HABBIT_TIMER, startTime, "더미 타임 1", 2, false, 90, 15, 2)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    final int hid = (int)((long)item);
                    final int num = 1;
                    final long term = total / num;
                    final long due = 15 * DateUtil.MILLISECOND_TO_MINUTE;

                    for(int i = 0; i<num; ++i){
                        int finalI = i;
                        recordDB.recordDao().insert(new Record(hid, Habbit.TYPE_HABBIT_TIMER, startTime +(finalI*term), Record.RECORD_STATE_TIMER_START, -1)).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(item2 -> {
                                    recordDB.recordDao().insert(new Record(-1, Habbit.TYPE_HABBIT_TIMER, startTime +(finalI *term) + due, Record.RECORD_STATE_TIMER_STOP, item2)).subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe();
                                });
                    }
                });
        habbitDB.habbitDao().insert(new Habbit(Habbit.TYPE_HABBIT_CHECK, startTime, "더미 체크 2", 3, false, 30, -10, 20)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {

                });
        habbitDB.habbitDao().insert(new Habbit(Habbit.TYPE_HABBIT_TIMER, startTime, "더미 타임 2", 1, true, 100, 30, 1)).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(item -> {
                final int hid = (int)((long)item);
                final int num = 5;
                final long term = total / num;
                final long due = 15 * DateUtil.MILLISECOND_TO_MINUTE;

                for(int i = 0; i<num; ++i){
                    int finalI = i;
                    recordDB.recordDao().insert(new Record(hid, Habbit.TYPE_HABBIT_TIMER, startTime +(finalI*term), Record.RECORD_STATE_TIMER_START, -1)).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(item2 -> {
                                recordDB.recordDao().insert(new Record(-1, Habbit.TYPE_HABBIT_TIMER, startTime +(finalI *term) + due, Record.RECORD_STATE_TIMER_STOP, item2)).subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe();
                            });
                }
            });

         */
    }
}
