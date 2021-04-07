package wily.apps.watchrabbit.adapter;

import android.content.Context;
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

import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.data.EvaluationHabbit;
import wily.apps.watchrabbit.data.entity.Habbit;

public class EvaluationHabbitAdapter extends RecyclerView.Adapter<EvaluationHabbitAdapter.EvaluationHabbitViewHolder>{
    private ArrayList<EvaluationHabbit> mList;
    private Context mContext;
    private OnEvaluationHabbitItemClickListener mListener = null;

    // Listener
    public interface OnEvaluationHabbitItemClickListener{
        void onItemClick(int id);
        void onItemLongClick(int pos);
    }

    // Base
    public EvaluationHabbitAdapter(Context context, ArrayList<EvaluationHabbit> evaluationHabbitList) {
        this.mContext = context;
        this.mList = evaluationHabbitList;
    }

    public void setOnItemClickListener(OnEvaluationHabbitItemClickListener listener){
        this.mListener = listener;
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

    protected void setContent(EvaluationHabbitViewHolder holder, EvaluationHabbit pEvalHabbit){
        holder.evaluationHabbit = pEvalHabbit;
        holder.txId.setText(""+pEvalHabbit.getHabbit().getId());
        setIcon(holder.imageType, pEvalHabbit.getHabbit().getType());
        holder.txTitle.setText(pEvalHabbit.getHabbit().getTitle());


        holder.txDay30Result.setText(""+pEvalHabbit.getDay30Result());
        holder.txDay30Achive.setText(""+pEvalHabbit.getDay30Achive());
        holder.txDay7Result.setText(""+pEvalHabbit.getDay7Result());
        holder.txDay7Achive.setText(""+pEvalHabbit.getDay7Achive());
        holder.txTodayResult.setText(""+pEvalHabbit.getTodayResult());
        holder.txTodayAchive.setText(""+pEvalHabbit.getTodayResult());
    }

    private void setIcon(ImageView image, int type){
        switch (type){
            case Habbit.TYPE_HABBIT_CHECK:
                image.setImageResource(R.drawable.ic_type_check);
                break;
            case Habbit.TYPE_HABBIT_TIMER:
                image.setImageResource(R.drawable.ic_type_timer);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

    // ViewHolder
    public class EvaluationHabbitViewHolder extends RecyclerView.ViewHolder  {
        private EvaluationHabbit evaluationHabbit;

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
                        mListener.onItemClick(Integer.parseInt(txId.getText().toString()));
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mListener != null) {
                        mListener.onItemLongClick(Integer.parseInt(txId.getText().toString()));
                    }
                    return true;
                }
            });
        }
    }
}
