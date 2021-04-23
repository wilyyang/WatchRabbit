package wily.apps.watchrabbit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.adapter.RecordAdapter;
import wily.apps.watchrabbit.data.database.EvaluationDatabase;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.database.RecordDatabase;
import wily.apps.watchrabbit.data.entity.Evaluation;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.data.entity.Record;
import wily.apps.watchrabbit.util.DateUtil;
import wily.apps.watchrabbit.util.DialogGetter;

public class EvaluationRecordActivity extends AppCompatActivity {
    private LinearLayout recordLayoutRecycler;

    private RecordAdapter recordAdapter;
    private RecyclerView recordRecyclerView;

    private Button btnRecordAdd;
    private Button btnSelectMode;
    private CheckBox checkboxRecordAll;

    private LinearLayout layoutDeleteBtn;
    private Button btnRecordDelete;
    private Button btnDeleteCancel;

    private AlertDialog dialog;

    private Evaluation mEvaluation;
    private Habbit mHabbit;
    private long date;
    private int hid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_record);

        Intent intent = getIntent();
        date = intent.getLongExtra(AppConst.INTENT_EVAL_EVALUATION_DATE, -1);
        hid = intent.getIntExtra(AppConst.INTENT_EVAL_HABBIT_ID, -1);

        initView();
    }

    private void initView() {
        dialog = DialogGetter.getProgressDialog(EvaluationRecordActivity.this, getString(R.string.base_dialog_database_inprogress));

        recordLayoutRecycler = findViewById(R.id.layout_eval_record_recycler);

        LinearLayoutManager layoutMgr = new LinearLayoutManager(EvaluationRecordActivity.this);
        recordRecyclerView = findViewById(R.id.recycler_view_eval_record);
        recordRecyclerView.setLayoutManager(layoutMgr);
        recordAdapter = new RecordAdapter(EvaluationRecordActivity.this, new ArrayList<Record>());
        recordAdapter.setOnItemClickListener(onItemClickListener);
        recordRecyclerView.setAdapter(recordAdapter);

        btnRecordAdd = findViewById(R.id.btn_eval_record_add);
        btnRecordAdd.setOnClickListener(onClickListener);

        btnSelectMode = findViewById(R.id.btn_eval_record_select_mode);
        btnSelectMode.setOnClickListener(onClickListener);

        layoutDeleteBtn = findViewById(R.id.layout_eval_record_btn_delete);

        btnRecordDelete = findViewById(R.id.btn_eval_record_delete);
        btnRecordDelete.setOnClickListener(onClickListener);

        btnDeleteCancel = findViewById(R.id.btn_eval_record_delete_cancel);
        btnDeleteCancel.setOnClickListener(onClickListener);

        checkboxRecordAll = findViewById(R.id.check_box_eval_record_all);
        checkboxRecordAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isPressed()){
                    recordAdapter.setAllChecked(b);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // if Selectable mode, not load data
        if( !(recordAdapter != null && recordAdapter.isSelectableMode()) ){
            loadRecords();
        }
    }

    // Access Data
    private void loadRecords(){
        dialog.show();

        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(EvaluationRecordActivity.this);
        EvaluationDatabase evalDB = EvaluationDatabase.getAppDatabase(EvaluationRecordActivity.this);
        RecordDatabase recordDB = RecordDatabase.getAppDatabase(EvaluationRecordActivity.this);

        Single.create(subscriber -> {
            List<Habbit> habbits = habbitDB.habbitDao().getHabbit(hid);
            if(!habbits.isEmpty()){
                mHabbit = habbits.get(0);
            }
            List<Evaluation> evaluations = evalDB.evaluationDao().getEvaluationByTime(hid, date);
            if(!evaluations.isEmpty()){
                mEvaluation = evaluations.get(0);
            }

            List<Record> recordList = recordDB.recordDao().getRecordByHidAndTerm(mHabbit.getId(), mEvaluation.getTime(), (mEvaluation.getTime()+ DateUtil.ONEDAY_TO_MILLISECOND-1));
            subscriber.onSuccess(recordList);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recordList -> {
                    initTopEvaluation(mEvaluation);
                    afterRecordGetAll((List<Record>) recordList);
                    dialog.dismiss();
                });
        }

    private void initTopEvaluation(Evaluation eval){
        ((TextView)findViewById(R.id.text_view_eval_record_title)).setText("일일 기록 : "+mHabbit.getTitle());

        View view = findViewById(R.id.include_eval_record_top);
        ((TextView)view.findViewById(R.id.text_view_evaluation_date_id)).setText(""+eval.getId());
        ((TextView)view.findViewById(R.id.text_view_evaluation_date_date)).setText(DateUtil.getDateStringDayLimit(mEvaluation.getTime()));
        ((TextView)view.findViewById(R.id.text_view_evaluation_date_result)).setText(""+mEvaluation.getResultCost());
        ((TextView)view.findViewById(R.id.text_view_evaluation_date_achive)).setText(""+mEvaluation.getAchiveRate());
    }

    private void afterRecordGetAll(List<Record> recordList){

        recordAdapter.setRecordList((ArrayList<Record>) recordList);
        recordAdapter.notifyDataSetChanged();
        dialog.dismiss();
    }


    // Record Item
    private void setSelectableMode(boolean mode){
        if(mode){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 16f);
            recordLayoutRecycler.setLayoutParams(params);

            checkboxRecordAll.setVisibility(View.VISIBLE);
            btnRecordAdd.setVisibility(View.INVISIBLE);

            layoutDeleteBtn.setVisibility(View.VISIBLE);
        }else{
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 18f);
            recordLayoutRecycler.setLayoutParams(params);

            checkboxRecordAll.setChecked(false);
            checkboxRecordAll.setVisibility(View.INVISIBLE);
            btnRecordAdd.setVisibility(View.VISIBLE);

            layoutDeleteBtn.setVisibility(View.GONE);
        }
    }

    private void deleteSelectRecord() {
        dialog.show();

        List<Long> list = recordAdapter.getCheckedIds();
        RecordDatabase recordDB = RecordDatabase.getAppDatabase(EvaluationRecordActivity.this);

        Completable.create(subscriber -> {
            recordDB.recordDao().deleteRecordByIds(list);
            EvaluateWork work = new EvaluateWork(EvaluationRecordActivity.this);
            work.work(EvaluateWork.WORK_TYPE_REPLACE_EVALUATION, mHabbit.getId(), mEvaluation.getTime());
            subscriber.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    setSelectableMode(false);
                    recordAdapter.setSelectableMode(false);
                    dialog.dismiss();
                    onResume();
                });
    }

    // Listener
    private Button.OnClickListener onClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_eval_record_add:
                    RecordModifyDialog recordModifyDialog = new RecordModifyDialog(EvaluationRecordActivity.this, mHabbit.getId(), mHabbit.getType(), mHabbit.getTitle()+" ("+DateUtil.getDateStringDayLimit(mEvaluation.getTime())+")", -1, mEvaluation.getTime(), -1, true);
                    recordModifyDialog.show();
                    break;
                case R.id.btn_eval_record_select_mode:
                    recordAdapter.setSelectableMode(!recordAdapter.isSelectableMode());
                    setSelectableMode(recordAdapter.isSelectableMode());
                    break;

                case R.id.btn_eval_record_delete:
                    deleteSelectRecord();
                    break;

                case R.id.btn_eval_record_delete_cancel:
                    recordAdapter.setSelectableMode(false);
                    setSelectableMode(false);
                    break;
            }
        }
    };

    private RecordAdapter.OnRecordItemClickListener onItemClickListener = new RecordAdapter.OnRecordItemClickListener() {
        @Override
        public void onItemCheckChanged(boolean flag) {
            if(flag == false){
                checkboxRecordAll.setChecked(false);
            }
        }

        @Override
        public void onItemClick(long id, int type, long time, long term){
            if(type == Habbit.TYPE_HABBIT_TIMER && term == -1){
                return;
            }
            RecordModifyDialog recordModifyDialog = new RecordModifyDialog(EvaluationRecordActivity.this, mHabbit.getId(), mHabbit.getType(),  mHabbit.getTitle()+" ("+DateUtil.getDateStringDayLimit(mEvaluation.getTime())+")", id, time, term, false);
            recordModifyDialog.show();
        }

        @Override
        public void onItemLongClick(int id) {
            setSelectableMode(true);
        }
    };
}