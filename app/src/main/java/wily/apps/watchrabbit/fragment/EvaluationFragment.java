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
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.AppConst;
import wily.apps.watchrabbit.MainActivity;
import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.adapter.RecordAdapter;
import wily.apps.watchrabbit.data.database.RecordDatabase;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.data.entity.Record;
import wily.apps.watchrabbit.util.DialogGetter;
import wily.apps.watchrabbit.RecordModifyDialog;

public class EvaluationFragment extends Fragment {
    private LinearLayout recordLayoutParent;

    private ArrayList<Record> recordList;
    private RecordAdapter recordAdapter;
    private RecyclerView recordRecyclerView;

    private Button btnRecordAdd;
    private Button btnSelectMode;
    private CheckBox checkboxRecordAll;

    private LinearLayout layoutDeleteBtn;
    private Button btnRecordDelete;
    private Button btnDeleteCancel;


    // UI
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evaluation, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recordLayoutParent = view.findViewById(R.id.layout_fragment_evaluation_parent);

        LinearLayoutManager layoutMgr = new LinearLayoutManager(getContext());
        recordRecyclerView = view.findViewById(R.id.recycler_view_record);
        recordRecyclerView.setLayoutManager(layoutMgr);

        btnRecordAdd = view.findViewById(R.id.btn_record_add);
        btnRecordAdd.setOnClickListener(onClickListener);

        btnSelectMode = view.findViewById(R.id.btn_record_select_mode);
        btnSelectMode.setOnClickListener(onClickListener);

        layoutDeleteBtn = view.findViewById(R.id.layout_record_btn_delete);

        btnRecordDelete = view.findViewById(R.id.btn_record_delete);
        btnRecordDelete.setOnClickListener(onClickListener);

        btnDeleteCancel = view.findViewById(R.id.btn_record_delete_cancel);
        btnDeleteCancel.setOnClickListener(onClickListener);

        checkboxRecordAll = view.findViewById(R.id.check_box_record_all);
        checkboxRecordAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isPressed()){
                    recordAdapter.setAllChecked(b);
                }
            }
        });
    }

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

    // Access Data
    private void loadRecords(){
        AlertDialog dialog = DialogGetter.getProgressDialog(getContext(), getString(R.string.base_dialog_database_inprogress));
        dialog.show();
        RecordDatabase recordDB = RecordDatabase.getAppDatabase(getContext());
        recordDB.recordDao().getStartRecords().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    recordList = (ArrayList) item;

                    recordDB.recordDao().getStopRecords().subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(stops -> {
                                Log.d(AppConst.TAG, "LAST");
                                HashMap<Long, Long> completeHash = new HashMap<>();
                                for(Record r : stops){
                                    completeHash.put(r.getPair(), r.getTime());
                                    Log.d(AppConst.TAG, " "+r.getPair()+" "+r.getTime());
                                }

                                recordAdapter = new RecordAdapter(getContext(), recordList, completeHash);
                                recordAdapter.setOnItemClickListener(onItemClickListener);
                                recordRecyclerView.setAdapter(recordAdapter);
                                recordAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            });
                });
    }

    private void deleteSelectRecord() {
        AlertDialog dialog = DialogGetter.getProgressDialog(getContext(), getString(R.string.base_dialog_database_inprogress));
        dialog.show();

        List<Long> list = recordAdapter.getCheckedIds();
        RecordDatabase recordDB = RecordDatabase.getAppDatabase(getContext());
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
                case R.id.btn_record_add:
                    RecordModifyDialog recordModifyDialog = new RecordModifyDialog(getContext(), -1, Habbit.TYPE_HABBIT_TIMER, -1, -1, true);
                    recordModifyDialog.show();
                    break;
                case R.id.btn_record_select_mode:
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

    private RecordAdapter.OnRecordItemClickListener onItemClickListener = new RecordAdapter.OnRecordItemClickListener() {
        @Override
        public void onItemCheckChanged(boolean flag) {
            if(flag == false){
                checkboxRecordAll.setChecked(false);
            }
        }

        @Override
        public void onItemClick(long id, int type, long time, long term){
            if(term != -1){
                RecordModifyDialog recordModifyDialog = new RecordModifyDialog(getContext(), id, type, time, term, false);
                recordModifyDialog.show();
            }
        }

        @Override
        public void onItemLongClick(int id) {
            setSelectableMode(true);
        }
    };
}
