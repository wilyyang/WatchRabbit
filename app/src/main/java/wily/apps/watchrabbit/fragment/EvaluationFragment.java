package wily.apps.watchrabbit.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.EvaluationHabbitActivity;
import wily.apps.watchrabbit.EvaluationRecordActivity;
import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.adapter.EvaluationAdapter;
import wily.apps.watchrabbit.adapter.EvaluationHabbitAdapter;
import wily.apps.watchrabbit.data.EvaluationHabbit;
import wily.apps.watchrabbit.data.database.EvaluationDatabase;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.entity.Evaluation;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.util.DialogGetter;

import static wily.apps.watchrabbit.AppConst.INTENT_EVAL_FRAG_ID;

public class EvaluationFragment extends Fragment {
    private EvaluationHabbitAdapter evaluationHabbitAdapter;
    private RecyclerView evaluationHabbitRecyclerView;

    private AlertDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evaluation, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        dialog = DialogGetter.getProgressDialog(getActivity(), getString(R.string.base_dialog_database_inprogress));

        LinearLayoutManager layoutMgr = new LinearLayoutManager(getActivity());
        evaluationHabbitRecyclerView = view.findViewById(R.id.recycler_view_eval_total);
        evaluationHabbitRecyclerView.setLayoutManager(layoutMgr);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadEvaluationHabbits();
    }

    private void loadEvaluationHabbits(){
        dialog.show();
        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(getContext());
        habbitDB.habbitDao().getAllSingle()
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> afterGetHabbit(item));
    }

    private void afterGetHabbit(List<Habbit> habbitList){

        ArrayList<EvaluationHabbit> evalHabbitList = new ArrayList<EvaluationHabbit>();
        for(Habbit habbit : habbitList){
            evalHabbitList.add(new EvaluationHabbit(habbit, 0, 0, 0, 0, 0, 0));
        }

        evaluationHabbitAdapter = new EvaluationHabbitAdapter(getActivity(), evalHabbitList);
        evaluationHabbitAdapter.setOnItemClickListener(onItemClickListener);
        evaluationHabbitRecyclerView.setAdapter(evaluationHabbitAdapter);
        evaluationHabbitAdapter.notifyDataSetChanged();

        dialog.dismiss();
    }

    private EvaluationHabbitAdapter.OnEvaluationHabbitItemClickListener onItemClickListener = new EvaluationHabbitAdapter.OnEvaluationHabbitItemClickListener() {

        @Override
        public void onItemClick(int id) {
            Intent intent = new Intent(getActivity(), EvaluationHabbitActivity.class);
            intent.putExtra(INTENT_EVAL_FRAG_ID, id);
            startActivity(intent);
        }

        @Override
        public void onItemLongClick(int pos) {

        }
    };
}
