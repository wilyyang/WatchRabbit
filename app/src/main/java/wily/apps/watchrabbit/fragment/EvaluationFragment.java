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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.AppConst;
import wily.apps.watchrabbit.HabbitModifyActivity;
import wily.apps.watchrabbit.MainActivity;
import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.adapter.RecordAdapter;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.database.RecordDatabase;
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
    private void addDummyData()
    {
        final long startTime = DateUtil.getDateLong(2021, 3, 19, 0, 0, 0);
        final long stopTime  = DateUtil.getDateLong(2021, 3, 22, 20, 10, 23);
        final long total = stopTime - startTime;

        RecordDatabase recordDB = RecordDatabase.getAppDatabase(getContext());
        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(getContext());

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
    }

    private void evaluateRecords()
    {


    }
}
