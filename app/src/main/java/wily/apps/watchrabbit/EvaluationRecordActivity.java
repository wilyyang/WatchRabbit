package wily.apps.watchrabbit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.adapter.RecordAdapter;
import wily.apps.watchrabbit.data.database.RecordDatabase;
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

    private int hid;
    private int hType;
    private String hTitle;
    private long date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_record);

        Intent intent = getIntent();
        hid = intent.getExtras().getInt(AppConst.INTENT_EVAL_HABBIT_ID);
        hType = intent.getExtras().getInt(AppConst.INTENT_EVAL_HABBIT_TYPE);
        hTitle = intent.getExtras().getString(AppConst.INTENT_EVAL_HABBIT_TITLE);
        date = intent.getExtras().getLong(AppConst.INTENT_EVAL_HABBIT_DATE);

        initView();
    }

    private void initView() {
        initTopEvaluation();

        dialog = DialogGetter.getProgressDialog(EvaluationRecordActivity.this, getString(R.string.base_dialog_database_inprogress));

        recordLayoutRecycler = findViewById(R.id.layout_eval_record_recycler);

        LinearLayoutManager layoutMgr = new LinearLayoutManager(EvaluationRecordActivity.this);
        recordRecyclerView = findViewById(R.id.recycler_view_eval_record);
        recordRecyclerView.setLayoutManager(layoutMgr);

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

    private void initTopEvaluation(){
        View view = findViewById(R.id.include_eval_record_top);

//        view.findViewById(R.id.)

    }

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

    @Override
    public void onResume() {
        super.onResume();
        if( !(recordAdapter != null && recordAdapter.isSelectableMode()) ){
            loadRecords();
        }
    }

    // Access Data
    private void loadRecords(){
        dialog.show();
        RecordDatabase recordDB = RecordDatabase.getAppDatabase(EvaluationRecordActivity.this);
        recordDB.recordDao().getRecordByHidAndTimeSingle(hid, date, (date+ DateUtil.ONEDAY_TO_MILLISECOND-1) ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(item -> afterRecordGetAll(item));
    }

    private void afterRecordGetAll(List<Record> recordList){
        recordAdapter = new RecordAdapter(EvaluationRecordActivity.this, (ArrayList<Record>)recordList);
        recordAdapter.setOnItemClickListener(onItemClickListener);
        recordRecyclerView.setAdapter(recordAdapter);
        recordAdapter.notifyDataSetChanged();
        dialog.dismiss();
    }

    private void deleteSelectRecord() {
        dialog.show();

        List<Long> list = recordAdapter.getCheckedIds();
        RecordDatabase recordDB = RecordDatabase.getAppDatabase(EvaluationRecordActivity.this);
        recordDB.recordDao().deleteRecordByIds(list).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                            setSelectableMode(false);
                            recordAdapter.setSelectableMode(false);
                            dialog.dismiss();
                            onResume();
                        }
                );
    }

    // Listener
    private Button.OnClickListener onClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_eval_record_add:
                    RecordModifyDialog recordModifyDialog = new RecordModifyDialog(EvaluationRecordActivity.this, hid, hType, hTitle+" ("+DateUtil.getDateStringDayLimit(date)+")", -1, date, -1, true);
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
            RecordModifyDialog recordModifyDialog = new RecordModifyDialog(EvaluationRecordActivity.this, hid, hType, hTitle+" ("+DateUtil.getDateStringDayLimit(date)+")", id, time, term, false);
            recordModifyDialog.show();
        }

        @Override
        public void onItemLongClick(int id) {
            setSelectableMode(true);
        }
    };
}