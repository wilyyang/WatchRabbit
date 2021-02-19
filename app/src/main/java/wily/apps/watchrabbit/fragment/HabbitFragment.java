package wily.apps.watchrabbit.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.HabbitModifyActivity;
import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.adapter.HabbitAdapter;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.util.DialogGetter;

public class HabbitFragment extends Fragment {
    private List<Habbit> habbitList;
    private HabbitAdapter habbitAdapter;
    private RecyclerView habbitRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habbit, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        LinearLayoutManager layoutMgr = new LinearLayoutManager(getContext());
        habbitRecyclerView = view.findViewById(R.id.list_habbit);
        habbitRecyclerView.setLayoutManager(layoutMgr);

        Button btnHabbitAdd = view.findViewById(R.id.btn_habbit_add);
        btnHabbitAdd.setOnClickListener(onClickListener);
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
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        loadHabbits();
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
            for (int i = 0; i < habbitRecyclerView.getChildCount(); ++i) {
                HabbitAdapter.HabbitViewHolder viewHolder = (HabbitAdapter.HabbitViewHolder) habbitRecyclerView.findViewHolderForAdapterPosition(i);
                viewHolder.visibleCheckBox(true, (pos==i));
            }
        }
    };


}
