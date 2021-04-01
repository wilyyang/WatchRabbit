package wily.apps.watchrabbit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.adapter.EvaluationAdapter;
import wily.apps.watchrabbit.adapter.RecordAdapter;
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

public class EvaluationHabbitActivity extends AppCompatActivity {
    private AlertDialog dialog;

    private EvaluationAdapter evaluationAdapter;
    private RecyclerView evaluationRecyclerView;

    public boolean init = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_habbit);

        dialog = DialogGetter.getProgressDialog(EvaluationHabbitActivity.this, getString(R.string.base_dialog_database_inprogress));

        LinearLayoutManager layoutMgr = new LinearLayoutManager(EvaluationHabbitActivity.this);
        evaluationRecyclerView = findViewById(R.id.recycler_view_eval_habbit);
        evaluationRecyclerView.setLayoutManager(layoutMgr);

        if (init == false) {
            init = true;
            testAdd();
        }
        updateTotal();
        loadEvaluations();
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

        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(EvaluationHabbitActivity.this);
        RecordDatabase recordDB = RecordDatabase.getAppDatabase(EvaluationHabbitActivity.this);

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

    private HabbitDao habbitDao = null;
    private EvaluationDao evalDao = null;
    private RecordDao recordDao = null;
    private void updateTotal(){
        habbitDao = HabbitDatabase.getAppDatabase(EvaluationHabbitActivity.this).habbitDao();
        evalDao = EvaluationDatabase.getAppDatabase(EvaluationHabbitActivity.this).evaluationDao();
        recordDao = RecordDatabase.getAppDatabase(EvaluationHabbitActivity.this).recordDao();

        habbitDao.getAll().subscribeOn(Schedulers.io()).subscribe(list->{

            for(Habbit habbit : list){
                Log.d(AppConst.TAG, "Habbit : "+habbit.getId()+ " "+ habbit.getTitle());
                updateHabbit(habbit);
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

    private void updateEvaluation(final Habbit habbit, final long day){

        recordDao.getRecordByHidAndTime(habbit.getId(), day, day+DateUtil.ONEDAY_TO_MILLISECOND-1).subscribe(list ->{
            int sum = 0;
            if(habbit.getType() == Habbit.TYPE_HABBIT_CHECK){
                sum = list.size();

            }else if(habbit.getType() == Habbit.TYPE_HABBIT_TIMER){
                for(Record record : list){
                    sum += record.getTerm();
                }
            }

            int result = habbit.getInitCost() + (sum * habbit.getPerCost());
            int rate = (int) (( result / (double)habbit.getGoalCost() ) * 100 );

            Log.d(AppConst.TAG, "Eval Make : "+day+" "+result+" "+rate);
            evalDao.insert(new Evaluation(habbit.getId(), day, result, rate)).subscribe();
        });

    }

    private void loadEvaluations(){
        dialog.show();
        EvaluationDatabase evalDB = EvaluationDatabase.getAppDatabase(EvaluationHabbitActivity.this);
        evalDB.evaluationDao().getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(item -> afterRecordGetAll(item));
    }

    private void afterRecordGetAll(List<Evaluation> evalList){
        evaluationAdapter = new EvaluationAdapter(EvaluationHabbitActivity.this, (ArrayList<Evaluation>)evalList);
        //evaluationAdapter.setOnItemClickListener(onItemClickListener);
        evaluationRecyclerView.setAdapter(evaluationAdapter);
        evaluationAdapter.notifyDataSetChanged();
        dialog.dismiss();
    }
}