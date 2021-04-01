package wily.apps.watchrabbit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_habbit);

        findViewById(R.id.btn_eval_habbit_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EvaluationHabbitActivity.this, EvaluationRecordActivity.class);
                startActivity(intent);
            }
        });

        dialog = DialogGetter.getProgressDialog(EvaluationHabbitActivity.this, getString(R.string.base_dialog_database_inprogress));

        LinearLayoutManager layoutMgr = new LinearLayoutManager(EvaluationHabbitActivity.this);
        evaluationRecyclerView = findViewById(R.id.recycler_view_eval_habbit);
        evaluationRecyclerView.setLayoutManager(layoutMgr);

        loadEvaluations();
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