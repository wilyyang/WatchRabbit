package wily.apps.watchrabbit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.adapter.EvaluationAdapter;
import wily.apps.watchrabbit.data.database.EvaluationDatabase;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.entity.Evaluation;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.util.DateUtil;
import wily.apps.watchrabbit.util.DialogGetter;

public class EvaluationHabbitActivity extends AppCompatActivity {

    private EvaluationAdapter evaluationAdapter;
    private RecyclerView evaluationRecyclerView;

    private AlertDialog dialog;

    private int hid;
    private Habbit evaluationHabbit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_habbit);

        Intent intent = getIntent();
        hid = intent.getIntExtra(AppConst.INTENT_EVAL_FRAG_ID, -1);
        initView();
    }

    private void initView() {
        dialog = DialogGetter.getProgressDialog(EvaluationHabbitActivity.this, getString(R.string.base_dialog_database_inprogress));

        LinearLayoutManager layoutMgr = new LinearLayoutManager(EvaluationHabbitActivity.this);
        evaluationRecyclerView = findViewById(R.id.recycler_view_eval_habbit);
        evaluationRecyclerView.setLayoutManager(layoutMgr);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEvaluations();
    }

    private void loadEvaluations(){
        dialog.show();

        long endDate = DateUtil.convertDate(System.currentTimeMillis());
        long startDate = DateUtil.getDateLongBefore(endDate, AppConst.EVALUATION_30_DAYS);

        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(EvaluationHabbitActivity.this);
        EvaluationDatabase evalDB = EvaluationDatabase.getAppDatabase(EvaluationHabbitActivity.this);

        Single.create(subscriber -> {
            List<Habbit> habbits = habbitDB.habbitDao().getHabbit(hid);
            if(!habbits.isEmpty()){
                evaluationHabbit = habbits.get(0);
            }
            List<Evaluation> evalList = evalDB.evaluationDao().getEvaluationByHidAndTerm(evaluationHabbit.getId(), startDate, endDate);
            subscriber.onSuccess(evalList);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(evalList -> {
                    initTopEvaluationHabbit(evaluationHabbit);
                    afterEvaluationGet((List<Evaluation>) evalList);
                    dialog.dismiss();
                });
    }

    private void initTopEvaluationHabbit(Habbit habbit){
        View view = findViewById(R.id.include_eval_habbit_top);
        ((TextView)view.findViewById(R.id.text_view_evaluation_habbit_title)).setText(""+habbit.getTitle());

        ((TextView)view.findViewById(R.id.text_view_evaluation_habbit_day_30_result)).setText(""+habbit.getDay30ResultCost());
        ((TextView)view.findViewById(R.id.text_view_evaluation_habbit_day_30_achive)).setText(""+habbit.getDay30AchiveRate());
        ((TextView)view.findViewById(R.id.text_view_evaluation_habbit_day_7_result)).setText(""+habbit.getDay7ResultCost());
        ((TextView)view.findViewById(R.id.text_view_evaluation_habbit_day_7_achive)).setText(""+habbit.getDay7AchiveRate());
        ((TextView)view.findViewById(R.id.text_view_evaluation_habbit_today_result)).setText(""+habbit.getCurrentResultCost());
        ((TextView)view.findViewById(R.id.text_view_evaluation_habbit_today_achive)).setText(""+habbit.getCurrentAchiveRate());
    }

    private void afterEvaluationGet(List<Evaluation> evalList){
        evaluationAdapter = new EvaluationAdapter(EvaluationHabbitActivity.this, (ArrayList<Evaluation>)evalList);
        evaluationAdapter.setOnItemClickListener(onItemClickListener);
        evaluationRecyclerView.setAdapter(evaluationAdapter);
        evaluationAdapter.notifyDataSetChanged();
        dialog.dismiss();
    }

    // Listener
    private EvaluationAdapter.OnEvaluationItemClickListener onItemClickListener = new EvaluationAdapter.OnEvaluationItemClickListener() {

        @Override
        public void onItemClick(long date) {
            Intent intent = new Intent(EvaluationHabbitActivity.this, EvaluationRecordActivity.class);
            intent.putExtra(AppConst.INTENT_EVAL_EVALUATION_DATE, date);
            intent.putExtra(AppConst.INTENT_EVAL_HABBIT_ID, evaluationHabbit.getId());
            startActivity(intent);
        }

        @Override
        public void onItemLongClick(long id) {

        }
    };
}