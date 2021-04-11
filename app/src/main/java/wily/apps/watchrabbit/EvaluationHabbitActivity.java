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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.adapter.EvaluationAdapter;
import wily.apps.watchrabbit.data.database.EvaluationDatabase;
import wily.apps.watchrabbit.data.entity.Evaluation;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.util.DateUtil;
import wily.apps.watchrabbit.util.DialogGetter;

import static wily.apps.watchrabbit.AppConst.INTENT_EVAL_HABBIT;

public class EvaluationHabbitActivity extends AppCompatActivity {

    private EvaluationAdapter evaluationAdapter;
    private RecyclerView evaluationRecyclerView;

    private AlertDialog dialog;

    private Habbit evaluationHabbit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_habbit);

        Intent intent = getIntent();
        evaluationHabbit = (Habbit) intent.getSerializableExtra(AppConst.INTENT_EVAL_FRAG_EVALUATION_HABBIT);
        initView();
    }

    private void initView() {
        initTopEvaluationHabbit();
        dialog = DialogGetter.getProgressDialog(EvaluationHabbitActivity.this, getString(R.string.base_dialog_database_inprogress));

        LinearLayoutManager layoutMgr = new LinearLayoutManager(EvaluationHabbitActivity.this);
        evaluationRecyclerView = findViewById(R.id.recycler_view_eval_habbit);
        evaluationRecyclerView.setLayoutManager(layoutMgr);
    }

    private void initTopEvaluationHabbit(){
        View view = findViewById(R.id.include_eval_habbit_top);
        ((TextView)view.findViewById(R.id.text_view_evaluation_habbit_title)).setText(""+evaluationHabbit.getTitle());

        ((TextView)view.findViewById(R.id.text_view_evaluation_habbit_day_30_result)).setText(""+evaluationHabbit.getDay30ResultCost());
        ((TextView)view.findViewById(R.id.text_view_evaluation_habbit_day_30_achive)).setText(""+evaluationHabbit.getDay30AchiveRate());
        ((TextView)view.findViewById(R.id.text_view_evaluation_habbit_day_7_result)).setText(""+evaluationHabbit.getDay7ResultCost());
        ((TextView)view.findViewById(R.id.text_view_evaluation_habbit_day_7_achive)).setText(""+evaluationHabbit.getDay7AchiveRate());
        ((TextView)view.findViewById(R.id.text_view_evaluation_habbit_today_result)).setText(""+evaluationHabbit.getCurrentResultCost());
        ((TextView)view.findViewById(R.id.text_view_evaluation_habbit_today_achive)).setText(""+evaluationHabbit.getCurrentAchiveRate());
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

        EvaluationDatabase evalDB = EvaluationDatabase.getAppDatabase(EvaluationHabbitActivity.this);
        evalDB.evaluationDao().getEvaluationByHidAndTimeSingle(evaluationHabbit.getId(), startDate, endDate).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(item -> afterEvaluationGet(item));
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
        public void onItemClick(Evaluation eval) {
            Intent intent = new Intent(EvaluationHabbitActivity.this, EvaluationRecordActivity.class);
            intent.putExtra(AppConst.INTENT_EVAL_EVALUATION, eval);
            intent.putExtra(INTENT_EVAL_HABBIT, evaluationHabbit);
            startActivity(intent);
        }

        @Override
        public void onItemLongClick(int pos) {

        }
    };
}