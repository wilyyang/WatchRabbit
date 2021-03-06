package wily.apps.watchrabbit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.data.entity.Evaluation;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.util.Utils;

public class EvaluationHabbitAdapter extends RecyclerView.Adapter<EvaluationHabbitAdapter.EvaluationHabbitViewHolder>{
    private ArrayList<Habbit> mList;
    private Context mContext;
    private OnEvaluationHabbitItemClickListener mListener = null;

    // Listener
    public interface OnEvaluationHabbitItemClickListener{
        void onItemClick(int hid);
        void onItemLongClick(int pos);
    }

    // Base
    public EvaluationHabbitAdapter(Context context, ArrayList<Habbit> evaluationHabbitList) {
        this.mContext = context;
        this.mList = evaluationHabbitList;
    }

    public void setOnItemClickListener(OnEvaluationHabbitItemClickListener listener){
        this.mListener = listener;
    }

    public void setEvaluationHabbitList(ArrayList<Habbit> habbitList){
        mList = habbitList;
    }

    // Item Init
    @NonNull
    @Override
    public EvaluationHabbitViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_evaluation_habbit, viewGroup, false);
        EvaluationHabbitViewHolder viewHolder = new EvaluationHabbitViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EvaluationHabbitViewHolder holder, int position) {
        setContent(holder, mList.get(position));
    }

    protected void setContent(EvaluationHabbitViewHolder holder, Habbit pEvalHabbit){
        holder.evaluationHabbit = pEvalHabbit;
        holder.txId.setText(""+pEvalHabbit.getId());
        Utils.setIcon(holder.imageType, pEvalHabbit.getType());
        holder.txTitle.setText(pEvalHabbit.getTitle());

        holder.txDay30Result.setText(""+pEvalHabbit.getDay30ResultCost());
        holder.txDay30Achive.setText(""+pEvalHabbit.getDay30AchiveRate());
        holder.txDay7Result.setText(""+pEvalHabbit.getDay7ResultCost());
        holder.txDay7Achive.setText(""+pEvalHabbit.getDay7AchiveRate());
        holder.txTodayResult.setText(""+pEvalHabbit.getCurrentResultCost());
        holder.txTodayAchive.setText(""+pEvalHabbit.getCurrentAchiveRate());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

    // ViewHolder
    public class EvaluationHabbitViewHolder extends RecyclerView.ViewHolder  {
        private Habbit evaluationHabbit;

        protected View itemView;
        protected TextView txId;
        protected ImageView imageType;
        protected TextView txTitle;

        protected TextView txDay30Result;
        protected TextView txDay30Achive;
        protected TextView txDay7Result;
        protected TextView txDay7Achive;
        protected TextView txTodayResult;
        protected TextView txTodayAchive;

        public EvaluationHabbitViewHolder(View view) {
            super(view);
            this.itemView = view;
            this.txId = view.findViewById(R.id.text_view_evaluation_habbit_id);
            this.imageType = view.findViewById(R.id.image_view_evaluation_habbit_type);
            this.txTitle = view.findViewById(R.id.text_view_evaluation_habbit_title);

            this.txDay30Result = view.findViewById(R.id.text_view_evaluation_habbit_day_30_result);
            this.txDay30Achive = view.findViewById(R.id.text_view_evaluation_habbit_day_30_achive);

            this.txDay7Result = view.findViewById(R.id.text_view_evaluation_habbit_day_7_result);
            this.txDay7Achive = view.findViewById(R.id.text_view_evaluation_habbit_day_7_achive);

            this.txTodayResult = view.findViewById(R.id.text_view_evaluation_habbit_today_result);
            this.txTodayAchive = view.findViewById(R.id.text_view_evaluation_habbit_today_achive);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemClick(evaluationHabbit.getId());
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mListener != null) {
                        mListener.onItemLongClick(evaluationHabbit.getId());
                    }
                    return true;
                }
            });
        }
    }
}
