package wily.apps.watchrabbit.fragment;

import android.app.AlertDialog;
import android.content.Intent;
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
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.HabbitModifyActivity;
import wily.apps.watchrabbit.MainActivity;
import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.adapter.HabbitAdapter;
import wily.apps.watchrabbit.DataConst;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.database.RecordDatabase;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.service.HabbitService;
import wily.apps.watchrabbit.util.DialogGetter;

public class HabbitFragment extends Fragment {
    private LinearLayout habbitLayoutParent;

    private ArrayList<Habbit> habbitList;
    private HabbitAdapter habbitAdapter;
    private RecyclerView habbitRecyclerView;

    private Button btnHabbitAdd;
    private Button btnDeleteSelect;

    private Button btnHabbitDelete;
    private Button btnDeleteCancel;

    private CheckBox checkboxHabbitAll;

    private LinearLayout layoutDeleteBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habbit, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        habbitLayoutParent = view.findViewById(R.id.layout_fragment_habbit_parent);

        LinearLayoutManager layoutMgr = new LinearLayoutManager(getContext());
        habbitRecyclerView = view.findViewById(R.id.list_habbit);
        habbitRecyclerView.setLayoutManager(layoutMgr);

        btnHabbitAdd = view.findViewById(R.id.btn_habbit_add);
        btnHabbitAdd.setOnClickListener(onClickListener);

        btnDeleteSelect = view.findViewById(R.id.btn_delete_select);
        btnDeleteSelect.setOnClickListener(onClickListener);

        btnHabbitDelete = view.findViewById(R.id.btn_habbit_delete);
        btnHabbitDelete.setOnClickListener(onClickListener);

        btnDeleteCancel = view.findViewById(R.id.btn_habbit_delete_cancel);
        btnDeleteCancel.setOnClickListener(onClickListener);

        checkboxHabbitAll = view.findViewById(R.id.checkbox_habbit_all);
        checkboxHabbitAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d("WILY", "CHECK CHANGE : "+b);
                if(compoundButton.isPressed()){
                    habbitAdapter.setAllChecked(b);
                }
            }
        });

        layoutDeleteBtn = view.findViewById(R.id.layout_btn_delete);
    }

    private Button.OnClickListener onClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_habbit_add:
                    Intent intent = new Intent(getContext(), HabbitModifyActivity.class);

                    intent.putExtra("id", -1);
                    intent.putExtra("update", false);

                    startActivity(intent);
                    break;
                case R.id.btn_delete_select:
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

    private void loadHabbits(){
        AlertDialog dialog = DialogGetter.getProgressDialog(getContext());
        dialog.show();
        HabbitDatabase db = HabbitDatabase.getAppDatabase(getContext());
        db.habbitDao().getAll().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    habbitList = (ArrayList)item;
                    habbitAdapter = new HabbitAdapter(getContext(), habbitList);
                    habbitAdapter.setOnItemClickListener(onItemClickListener);

                    for(Habbit h : habbitList){
                        Log.d("WILY", ""+h);
                    }

                    habbitRecyclerView.setAdapter(habbitAdapter);
                    habbitAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                });
    }

    private void deleteSelectHabbit() {

        List<Integer> list = habbitAdapter.getCheckedIds();

        AlertDialog dialog = DialogGetter.getProgressDialog(getContext());
        dialog.show();
        HabbitDatabase h_db = HabbitDatabase.getAppDatabase(getContext());
        h_db.habbitDao().deleteItemByIds(list).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        RecordDatabase r_db = RecordDatabase.getAppDatabase(getContext());
        r_db.recordDao().deleteItemByHids(list).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    Intent intent = new Intent(getActivity(), HabbitService.class);
                    intent.setAction(HabbitService.HABBIT_SERVICE_REMOVE);
                    intent.putIntegerArrayListExtra(DataConst.HABBIT_DELETE_LIST, (ArrayList<Integer>) list);
                    getActivity().startService(intent);
                    setSelectableMode(false);
                    habbitAdapter.setSelectableMode(false);
                    dialog.dismiss();
                    onResume();
        });

    }

    private HabbitAdapter.OnItemClickListener onItemClickListener = new HabbitAdapter.OnItemClickListener() {
        @Override
        public void onItemCheckChanged(boolean flag) {
            if(flag == false){
                checkboxHabbitAll.setChecked(false);
            }
        }

        @Override
        public void onItemClick(int id) {
            Intent intent = new Intent(getContext(), HabbitModifyActivity.class);

            intent.putExtra("id", id);
            intent.putExtra("update", true);

            startActivity(intent);
        }

        @Override
        public void onItemLongClick(int id) {
            setSelectableMode(true);
        }
    };
}
