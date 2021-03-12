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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.DataConst;
import wily.apps.watchrabbit.MainActivity;
import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.adapter.RecordAdapter;
import wily.apps.watchrabbit.data.database.RecordDatabase;
import wily.apps.watchrabbit.data.entity.Record;
import wily.apps.watchrabbit.util.DialogGetter;
import wily.apps.watchrabbit.util.RecordDialog;

public class TodayFragment extends Fragment {
    private LinearLayout recordLayoutParent;

    private ArrayList<Record> recordList;
    private RecordAdapter recordAdapter;
    private RecyclerView recordRecyclerView;

    private Button btnRecordAdd;
    private Button btnDeleteSelect;

    private Button btnRecordDelete;
    private Button btnDeleteCancel;

    private CheckBox checkboxRecordAll;

    private LinearLayout layoutDeleteBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recordLayoutParent = view.findViewById(R.id.layout_fragment_record_parent);

        LinearLayoutManager layoutMgr = new LinearLayoutManager(getContext());
        recordRecyclerView = view.findViewById(R.id.list_record);
        recordRecyclerView.setLayoutManager(layoutMgr);

        btnRecordAdd = view.findViewById(R.id.btn_record_add);
        btnRecordAdd.setOnClickListener(onClickListener);

        btnDeleteSelect = view.findViewById(R.id.btn_record_delete_select);
        btnDeleteSelect.setOnClickListener(onClickListener);

        btnRecordDelete = view.findViewById(R.id.btn_record_delete);
        btnRecordDelete.setOnClickListener(onClickListener);

        btnDeleteCancel = view.findViewById(R.id.btn_record_delete_cancel);
        btnDeleteCancel.setOnClickListener(onClickListener);

        checkboxRecordAll = view.findViewById(R.id.checkbox_record_all);
        checkboxRecordAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("WILY", "CHECK CHANGE : "+b);
                if(compoundButton.isPressed()){
                    recordAdapter.setAllChecked(b);
                }
            }
        });

        layoutDeleteBtn = view.findViewById(R.id.layout_record_btn_delete);
    }

    private Button.OnClickListener onClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_record_add:
                    RecordDialog recordDialog = new RecordDialog(getContext(), true, DataConst.TYPE_HABBIT_TIMER, -1, -1);
                    recordDialog.show();
                    break;
                case R.id.btn_record_delete_select:
                    recordAdapter.setSelectableMode(!recordAdapter.isSelectableMode());
                    setSelectableMode(recordAdapter.isSelectableMode());
                    break;

                case R.id.btn_record_delete:
                    deleteSelectRecord();
                    break;

                case R.id.btn_record_delete_cancel:
                    recordAdapter.setSelectableMode(false);
                    setSelectableMode(false);
                    break;
            }
        }
    };

    private void setSelectableMode(boolean mode){
        if(mode){
            recordLayoutParent.setWeightSum(26);

            checkboxRecordAll.setVisibility(View.VISIBLE);
            btnRecordAdd.setVisibility(View.INVISIBLE);

            layoutDeleteBtn.setVisibility(View.VISIBLE);
            ((MainActivity)getActivity()).setVisibleNavigation(false);
        }else{
            recordLayoutParent.setWeightSum(24);

            checkboxRecordAll.setChecked(false);
            checkboxRecordAll.setVisibility(View.INVISIBLE);
            btnRecordAdd.setVisibility(View.VISIBLE);

            layoutDeleteBtn.setVisibility(View.GONE);
            ((MainActivity)getActivity()).setVisibleNavigation(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(recordAdapter == null || !recordAdapter.isSelectableMode()){
            loadRecords();
        }
    }

    private void loadRecords(){
        AlertDialog dialog = DialogGetter.getProgressDialog(getContext());
        dialog.show();
        RecordDatabase db = RecordDatabase.getAppDatabase(getContext());
        db.recordDao().getAllNotStop().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    recordList = (ArrayList) item;

                    db.recordDao().getAllStop().subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(stops -> {
                                HashMap<Long, Long> stopMap = new HashMap<>();
                                for(Record r : stops){
                                    stopMap.put(r.getPair(), r.getTime());
                                }

                                recordAdapter = new RecordAdapter(getContext(), recordList, stopMap);
                                recordAdapter.setOnItemClickListener(onItemClickListener);
                                recordRecyclerView.setAdapter(recordAdapter);
                                recordAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            });
                });
    }

    private void deleteSelectRecord() {

        List<Long> list = recordAdapter.getCheckedIds();

        AlertDialog dialog = DialogGetter.getProgressDialog(getContext());
        dialog.show();
        RecordDatabase db = RecordDatabase.getAppDatabase(getContext());
        db.recordDao().deleteItemByIds(list).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                            setSelectableMode(false);
                            recordAdapter.setSelectableMode(false);
                            dialog.dismiss();
                            onResume();
                        }
                );
    }

    private RecordAdapter.OnItemClickListener onItemClickListener = new RecordAdapter.OnItemClickListener() {
        @Override
        public void onItemCheckChanged(boolean flag) {
            if(flag == false){
                checkboxRecordAll.setChecked(false);
            }
        }

        @Override
        public void onItemClick(int type, long time, long duration) {
            if(duration != -1){
                RecordDialog recordDialog = new RecordDialog(getContext(), false, type, time, duration);
                recordDialog.show();
            }

//            Intent intent = new Intent(getContext(), HabbitModifyActivity.class);
//
//            intent.putExtra("id", id);
//            intent.putExtra("update", true);
//
//            startActivity(intent);
        }

        @Override
        public void onItemLongClick(int id) {
            setSelectableMode(true);
        }
    };
}
