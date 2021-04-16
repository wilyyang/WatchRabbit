package wily.apps.watchrabbit.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.EvaluationHabbitActivity;
import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.adapter.EvaluationHabbitAdapter;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.util.DateUtil;
import wily.apps.watchrabbit.util.DialogGetter;

import static wily.apps.watchrabbit.AppConst.INTENT_EVAL_FRAG_ID;

public class EvaluationFragment extends Fragment {
    private EvaluationHabbitAdapter evaluationHabbitAdapter;
    private RecyclerView evaluationHabbitRecyclerView;

    private AlertDialog dialog;
    private View includeEvalTotalTop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evaluation, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        dialog = DialogGetter.getProgressDialog(getActivity(), getString(R.string.base_dialog_database_inprogress));

        includeEvalTotalTop = view.findViewById(R.id.include_eval_total_top);

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
        evaluationHabbitAdapter = new EvaluationHabbitAdapter(getActivity(), (ArrayList<Habbit>) habbitList);
        evaluationHabbitAdapter.setOnItemClickListener(onItemClickListener);
        evaluationHabbitRecyclerView.setAdapter(evaluationHabbitAdapter);
        evaluationHabbitAdapter.notifyDataSetChanged();

        initTopEvaluationTotal(habbitList);
        dialog.dismiss();
    }

    private void initTopEvaluationTotal(List<Habbit> habbitList){
        ((TextView)includeEvalTotalTop.findViewById(R.id.text_view_evaluation_total_date)).setText(DateUtil.getDateStringDayLimit(System.currentTimeMillis()));

        int day30Result = 0;
        int day30Rate  = 0;
        int day7Result = 0;
        int day7Rate  = 0;
        int todayResult = 0;
        int todayRate = 0;

        if(habbitList.size() > 0){
            day30Result = habbitList.stream().mapToInt(o -> o.getDay30ResultCost()).sum() / habbitList.size();
            day30Rate  = habbitList.stream().mapToInt(o -> o.getDay30AchiveRate()).sum() / habbitList.size();
            day7Result = habbitList.stream().mapToInt(o -> o.getDay7ResultCost()).sum() / habbitList.size();
            day7Rate  = habbitList.stream().mapToInt(o -> o.getDay7AchiveRate()).sum() / habbitList.size();
            todayResult = habbitList.stream().mapToInt(o -> o.getCurrentResultCost()).sum() / habbitList.size();
            todayRate = habbitList.stream().mapToInt(o -> o.getCurrentAchiveRate()).sum() / habbitList.size();
        }

        ((TextView)includeEvalTotalTop.findViewById(R.id.text_view_evaluation_total_day_30_result_cost)).setText(""+day30Result);
        ((TextView)includeEvalTotalTop.findViewById(R.id.text_view_evaluation_total_day_30_achive_rate)).setText(""+day30Rate);
        ((TextView)includeEvalTotalTop.findViewById(R.id.text_view_evaluation_total_day_7_result_cost)).setText(""+day7Result);
        ((TextView)includeEvalTotalTop.findViewById(R.id.text_view_evaluation_total_day_7_achive_rate)).setText(""+day7Rate);
        ((TextView)includeEvalTotalTop.findViewById(R.id.text_view_evaluation_total_today_result_cost)).setText(""+todayResult);
        ((TextView)includeEvalTotalTop.findViewById(R.id.text_view_evaluation_total_today_achive_rate)).setText(""+todayRate);

        ((TextView)includeEvalTotalTop.findViewById(R.id.text_view_evaluation_total_rate)).setText(""+todayRate+"%");
    }

    private EvaluationHabbitAdapter.OnEvaluationHabbitItemClickListener onItemClickListener = new EvaluationHabbitAdapter.OnEvaluationHabbitItemClickListener() {

        @Override
        public void onItemClick(int hid) {
            Intent intent = new Intent(getActivity(), EvaluationHabbitActivity.class);
            intent.putExtra(INTENT_EVAL_FRAG_ID, hid);
            startActivity(intent);
        }

        @Override
        public void onItemLongClick(int pos) {

        }
    };
}
