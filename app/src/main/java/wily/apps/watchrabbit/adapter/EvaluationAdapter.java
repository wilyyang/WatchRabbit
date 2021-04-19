package wily.apps.watchrabbit.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wily.apps.watchrabbit.AppConst;
import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.data.entity.Evaluation;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.util.DateUtil;

public class EvaluationAdapter extends RecyclerView.Adapter<EvaluationAdapter.EvaluationViewHolder>{
    private ArrayList<Evaluation> mList;
    private Context mContext;
    private OnEvaluationItemClickListener mListener = null;

    // Listener
    public interface OnEvaluationItemClickListener{
        void onItemClick(long date);
        void onItemLongClick(long id);
    }

    // Base
    public EvaluationAdapter(Context context, ArrayList<Evaluation> evaluationList) {
        this.mContext = context;
        this.mList = evaluationList;
    }

    public void setOnItemClickListener(OnEvaluationItemClickListener listener){
        this.mListener = listener;
    }

    // Item Init
    @NonNull
    @Override
    public EvaluationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_evaluation_date, viewGroup, false);
        EvaluationViewHolder viewHolder = new EvaluationViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EvaluationViewHolder holder, int position) {
        setContent(holder, mList.get(position));
    }

    protected void setContent(EvaluationViewHolder holder, Evaluation pEvaluation){
        holder.evaluation = pEvaluation;
        holder.txId.setText(""+pEvaluation.getId());

        holder.txHid.setText(""+pEvaluation.getHid());
        holder.txDate.setText(""+ DateUtil.getDateStringDayLimit(pEvaluation.getTime()));
        holder.txInitCost.setText(""+pEvaluation.getResultCost());
        holder.txAchive.setText(""+pEvaluation.getAchiveRate());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }


    // ViewHolder
    public class EvaluationViewHolder extends RecyclerView.ViewHolder  {
        private Evaluation evaluation;

        protected View itemView;
        protected TextView txId;
        protected TextView txHid;
        protected TextView txDate;

        protected TextView txInitCost;
        protected TextView txAchive;

        public EvaluationViewHolder(View view) {
            super(view);
            this.itemView = view;
            this.txId = view.findViewById(R.id.text_view_evaluation_date_id);
            this.txHid = view.findViewById(R.id.text_view_evaluation_date_hid);
            this.txDate = view.findViewById(R.id.text_view_evaluation_date_date);

            this.txInitCost = view.findViewById(R.id.text_view_evaluation_date_result);
            this.txAchive = view.findViewById(R.id.text_view_evaluation_date_achive);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemClick(evaluation.getTime());
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mListener != null) {
                        mListener.onItemLongClick(evaluation.getId());
                    }
                    return true;
                }
            });
        }
    }
}
