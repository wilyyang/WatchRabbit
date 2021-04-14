package wily.apps.watchrabbit.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.HabbitModifyActivity;
import wily.apps.watchrabbit.MainActivity;
import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.adapter.HabbitAdapter;
import wily.apps.watchrabbit.AppConst;
import wily.apps.watchrabbit.data.database.EvaluationDatabase;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.database.RecordDatabase;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.service.HabbitService;
import wily.apps.watchrabbit.util.DialogGetter;
import wily.apps.watchrabbit.util.Utils;

public class HabbitFragment extends Fragment {
    private LinearLayout habbitLayoutParent;

    private ArrayList<Habbit> habbitList;
    private HabbitAdapter habbitAdapter;
    private RecyclerView habbitRecyclerView;

    private Button btnHabbitAdd;
    private Button btnSelectMode;
    private CheckBox checkboxHabbitAll;

    private LinearLayout layoutDeleteBtn;
    private Button btnHabbitDelete;
    private Button btnDeleteCancel;

    private AlertDialog dialog;

    // UI
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habbit, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        dialog = DialogGetter.getProgressDialog(getContext(), getString(R.string.base_dialog_database_inprogress));

        habbitLayoutParent = view.findViewById(R.id.layout_fragment_habbit_parent);

        LinearLayoutManager layoutMgr = new LinearLayoutManager(getContext());
        habbitRecyclerView = view.findViewById(R.id.recycler_view_habbit);
        habbitRecyclerView.setLayoutManager(layoutMgr);

        btnHabbitAdd = view.findViewById(R.id.btn_habbit_add);
        btnHabbitAdd.setOnClickListener(onClickListener);

        btnSelectMode = view.findViewById(R.id.btn_habbit_select_mode);
        btnSelectMode.setOnClickListener(onClickListener);

        checkboxHabbitAll = view.findViewById(R.id.check_box_habbit_all);
        checkboxHabbitAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isPressed()){
                    habbitAdapter.setAllChecked(b);
                }
            }
        });

        layoutDeleteBtn = view.findViewById(R.id.layout_habbit_btn_delete);

        btnHabbitDelete = view.findViewById(R.id.btn_habbit_delete);
        btnHabbitDelete.setOnClickListener(onClickListener);

        btnDeleteCancel = view.findViewById(R.id.btn_habbit_delete_cancel);
        btnDeleteCancel.setOnClickListener(onClickListener);
    }

    private void setSelectableMode(boolean mode){
        if(mode){
            habbitLayoutParent.setWeightSum(26);

            checkboxHabbitAll.setVisibility(View.VISIBLE);
            btnHabbitAdd.setVisibility(View.INVISIBLE);

            layoutDeleteBtn.setVisibility(View.VISIBLE);
            ((MainActivity)getActivity()).setVisibleNavigation(false);
        }else{
            habbitLayoutParent.setWeightSum(24);

            checkboxHabbitAll.setChecked(false);
            checkboxHabbitAll.setVisibility(View.INVISIBLE);
            btnHabbitAdd.setVisibility(View.VISIBLE);

            layoutDeleteBtn.setVisibility(View.GONE);
            ((MainActivity)getActivity()).setVisibleNavigation(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(habbitAdapter == null || !habbitAdapter.isSelectableMode()){
            loadHabbits();
        }
    }

    // Access Data
    private void loadHabbits(){
        dialog.show();
        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(getContext());
        habbitDB.habbitDao().getAllSingle()
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> afterGetHabbit((ArrayList<Habbit>) item));
    }

    private void afterGetHabbit(ArrayList<Habbit> list){
        habbitList = list;
        habbitAdapter = new HabbitAdapter(getContext(), habbitList);
        habbitAdapter.setOnItemClickListener(onItemClickListener);

        habbitRecyclerView.setAdapter(habbitAdapter);
        habbitAdapter.notifyDataSetChanged();
        dialog.dismiss();
    }

    private void deleteSelectHabbit() {
        dialog.show();

        List<Integer> list = habbitAdapter.getCheckedIds();
        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(getContext());
        RecordDatabase recordDB = RecordDatabase.getAppDatabase(getContext());
        EvaluationDatabase evalDB = EvaluationDatabase.getAppDatabase(getContext());

        Completable.create(subscriber -> {
            habbitDB.habbitDao().deleteHabbitByIds(list);
            recordDB.recordDao().deleteRecordByHids(list);
            evalDB.evaluationDao().deleteEvaluationByHids(list);
            subscriber.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> afterDeleteHabbit(list));
    }

    private void afterDeleteHabbit(List<Integer> list){
        if(Utils.isServiceRunning(getContext(), HabbitService.class.getName())){
            Intent intent = new Intent(getActivity(), HabbitService.class);
            intent.setAction(HabbitService.HABBIT_SERVICE_DELETE);
            intent.putIntegerArrayListExtra(AppConst.INTENT_SERVICE_DELETE_LIST, (ArrayList<Integer>) list);
            getActivity().startService(intent);
        }
        setSelectableMode(false);
        habbitAdapter.setSelectableMode(false);
        dialog.dismiss();
        onResume();
    }

    // Listener
    private Button.OnClickListener onClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_habbit_add:
                    Intent intent = new Intent(getContext(), HabbitModifyActivity.class);

                    intent.putExtra(AppConst.INTENT_HABBIT_FRAG_ID, -1);
                    intent.putExtra(AppConst.INTENT_HABBIT_FRAG_UPDATE, false);

                    startActivity(intent);
                    break;
                case R.id.btn_habbit_select_mode:
                    habbitAdapter.setSelectableMode(!habbitAdapter.isSelectableMode());
                    setSelectableMode(habbitAdapter.isSelectableMode());
                    break;

                case R.id.btn_habbit_delete:
                    deleteSelectHabbit();
                    break;

                case R.id.btn_habbit_delete_cancel:
                    habbitAdapter.setSelectableMode(false);
                    setSelectableMode(false);
                    break;
            }
        }
    };

    private HabbitAdapter.OnHabbitItemClickListener onItemClickListener = new HabbitAdapter.OnHabbitItemClickListener() {
        @Override
        public void onItemCheckChanged(boolean flag) {
            if(flag == false){
                checkboxHabbitAll.setChecked(false);
            }
        }

        @Override
        public void onItemClick(int id) {
            Intent intent = new Intent(getContext(), HabbitModifyActivity.class);

            intent.putExtra(AppConst.INTENT_HABBIT_FRAG_ID, id);
            intent.putExtra(AppConst.INTENT_HABBIT_FRAG_UPDATE, true);

            startActivity(intent);
        }

        @Override
        public void onItemLongClick(int id) {
            setSelectableMode(true);
        }
    };
}
