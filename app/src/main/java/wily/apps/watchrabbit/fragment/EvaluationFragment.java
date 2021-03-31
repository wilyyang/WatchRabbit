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
            testAdd();
        }
        evalAdd();


        View view = inflater.inflate(R.layout.fragment_evaluation, container, false);
        return view;
    }


    // <tempo>
    private void testAdd(){
        long sTime = DateUtil.getDateLong(2021, 3, 28, 0, 0, 0);
        long cTime = System.currentTimeMillis();
        long totalTerm  = cTime - sTime;
        ArrayList<Habbit> habbits = new ArrayList<Habbit>();
        habbits.add(new Habbit(Habbit.TYPE_HABBIT_CHECK, sTime, "체크1", 1, true, 30, 0, 10, Habbit.STATE_CHECK, -1));
        habbits.add(new Habbit(Habbit.TYPE_HABBIT_CHECK, sTime, "체크2", 1, true, 30, 60, -10, Habbit.STATE_CHECK, -1));

        habbits.add(new Habbit(Habbit.TYPE_HABBIT_TIMER, sTime, "타임1", 1, true, 30, 0, 1, Habbit.STATE_TIMER_WAIT, -1));
        habbits.add(new Habbit(Habbit.TYPE_HABBIT_TIMER, sTime, "타임2", 1, true, 30, 60, -1, Habbit.STATE_TIMER_WAIT, -1));
        habbits.add(new Habbit(Habbit.TYPE_HABBIT_TIMER, sTime, "타임 진행", 1, true, 30, 0, 1, Habbit.STATE_TIMER_WAIT, -1));

        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(getContext());
        RecordDatabase recordDB = RecordDatabase.getAppDatabase(getContext());

        for(Habbit habbit : habbits){
            habbitDB.habbitDao().insert(habbit).subscribeOn(Schedulers.io()).subscribe(hid->{
                long hhid = hid;
                ArrayList<Record> records = new ArrayList<Record>();
                int div = 5;
                long divTerm = totalTerm/div;
                for(int i = 0; i< div; ++i){
                    records.add(new Record((int)hhid, habbit.getType(), sTime+(divTerm*i), 10 * DateUtil.MILLISECOND_TO_MINUTE));
                }
                recordDB.recordDao().insertAll(records).subscribe();

                if(habbit.getTitle().equals("타임 진행")){
                    recordDB.recordDao().insert(new Record((int)hhid, habbit.getType(), sTime+(divTerm*div)+30, -1)).subscribe(

                            rid ->{
                                habbitDB.habbitDao().updateHabbitState((int)hhid, Habbit.STATE_TIMER_INPROGRESS).subscribe();
                                habbitDB.habbitDao().updateCurRecordId((int)hhid, rid).observeOn(AndroidSchedulers.mainThread()).subscribe();
                            }

                    );
                }
            });
        }
    }

    private void evalAdd(){


    }
}
