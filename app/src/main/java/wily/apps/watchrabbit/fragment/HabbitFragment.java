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
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.HabbitModifyActivity;
import wily.apps.watchrabbit.MainActivity;
import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.adapter.HabbitAdapter;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.util.DialogGetter;

public class HabbitFragment extends Fragment {
    private List<Habbit> habbitList;
    private HabbitAdapter habbitAdapter;
    private RecyclerView habbitRecyclerView;

    private Button btnHabbitAdd;
    private Button btnDeleteSelect;

    private Button btnHabbitDelete;
    private Button btnDeleteCancel;

    private CheckBox checkboxHabbitAll;

    private LinearLayout layoutRecycler;
    private LinearLayout layoutDeleteBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habbit, container, false);
        initView(view);
        loadHabbits();
        return view;
    }

    private void initView(View view) {
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

        layoutRecycler = view.findViewById(R.id.layout_recycler);
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
                    habbitAdapter.setSelectableMode(!habbitAdapter.isSelectableMode(), -1);
                    setSelectableMode(habbitAdapter.isSelectableMode());
                    break;

                case R.id.btn_habbit_delete:
                    deleteSelectHabbit();
                    break;

                case R.id.btn_habbit_delete_cancel:
                    habbitAdapter.setSelectableMode(false, -1);
                    setSelectableMode(false);
                    break;
            }
        }
    };

    private void setSelectableMode(boolean mode){
        if(mode){
            checkboxHabbitAll.setVisibility(View.VISIBLE);
            btnHabbitAdd.setVisibility(View.INVISIBLE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 19f);
            layoutRecycler.setLayoutParams(params);

            layoutDeleteBtn.setVisibility(View.VISIBLE);
            ((MainActivity)getActivity()).setVisibleNavigation(false);
        }else{
            checkboxHabbitAll.setChecked(false);
            checkboxHabbitAll.setVisibility(View.INVISIBLE);
            btnHabbitAdd.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 21f);
            layoutRecycler.setLayoutParams(params);

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
                    habbitList = item;
                    habbitAdapter = new HabbitAdapter(habbitList, getContext());
                    habbitAdapter.setOnItemClickListener(onItemClickListener);

                    habbitRecyclerView.setAdapter(habbitAdapter);
                    habbitAdapter.notifyDataSetChanged();

                    dialog.dismiss();
                });
    }

    private void deleteSelectHabbit() {
        List<Integer> list = habbitAdapter.getSelectIds();

        AlertDialog dialog = DialogGetter.getProgressDialog(getContext());
        dialog.show();
        HabbitDatabase db = HabbitDatabase.getAppDatabase(getContext());
        db.habbitDao().deleteItemByIds(list).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                            dialog.dismiss();
                            setSelectableMode(false);
                            onResume();
                        }
                );
    }

    private HabbitAdapter.OnItemClickListener onItemClickListener = new HabbitAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int id) {
            Intent intent = new Intent(getContext(), HabbitModifyActivity.class);

            intent.putExtra("id", id);
            intent.putExtra("update", true);

            startActivity(intent);
        }

        @Override
        public void onItemLongClick(int pos) {
            setSelectableMode(true);
        }

        @Override
        public void onItemCheckChanged(boolean flag) {
            if(flag == false){
                checkboxHabbitAll.setChecked(false);
            }
        }
    };
}
