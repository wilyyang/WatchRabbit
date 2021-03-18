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
import wily.apps.watchrabbit.data.entity.Evaluation;
import wily.apps.watchrabbit.data.entity.Habbit;

public class EvaluationAdapter extends RecyclerView.Adapter<EvaluationAdapter.EvaluationViewHolder>{
    private ArrayList<Evaluation> mList;
    private Context mContext;
    private OnEvaluationItemClickListener mListener = null;
    private boolean selectableMode = false;

    // Listener
    public interface OnEvaluationItemClickListener{
        void onItemClick(int id);
        void onItemLongClick(int pos);
        void onItemCheckChanged(boolean flag);
    }

    // Base
    public EvaluationAdapter(Context context, ArrayList<Evaluation> EvaluationList) {
        this.mContext = context;
        this.mList = EvaluationList;
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
        holder.Evaluation = pEvaluation;

        holder.txId.setText(""+pEvaluation.getId());
        setIcon(holder.imageType, pEvaluation.getType());
        holder.txTitle.setText(pEvaluation.getTitle());
        holder.txPriority.setText(""+pEvaluation.getPriority());
        holder.txGoalCost.setText(""+pEvaluation.getGoalCost());
        holder.txInitCost.setText(""+pEvaluation.getInitCost());
        holder.txPerCost.setText(""+pEvaluation.getPerCost());

        if(pEvaluation.isActive()){
            holder.itemView.setBackground(mContext.getDrawable(R.drawable.bg_layout_round_enabled));
        }else{
            holder.itemView.setBackground(mContext.getDrawable(R.drawable.bg_layout_round_disabled));
        }

        if (!selectableMode) {
            holder.checkBoxSelect.setVisibility(View.GONE);
        } else {
            holder.checkBoxSelect.setVisibility(View.VISIBLE);
        }

        if (pEvaluation.isCheck()) {
            holder.checkBoxSelect.setChecked(true);
        } else {
            holder.checkBoxSelect.setChecked(false);
        }
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

    // Select Mode
    public boolean isSelectableMode() {
        return selectableMode;
    }

    public void setSelectableMode(boolean flag) {
        selectableMode = flag;
        for (Evaluation h : mList) {
            h.setCheck(false);
        }
        notifyDataSetChanged();
    }

    public void setAllChecked(boolean flag) {
        for (Evaluation h : mList) {
            h.setCheck(flag);
        }
        notifyDataSetChanged();
    }

    public List<Integer> getCheckedIds() {
        ArrayList<Integer> list = new ArrayList<>();
        for (Evaluation h : mList) {
            if (h.isCheck()) {
                list.add(h.getId());
            }
        }
        return list;
    }

    // ViewHolder
    public class EvaluationViewHolder extends RecyclerView.ViewHolder  {
        private Evaluation Evaluation;

        protected View itemView;
        protected CheckBox checkBoxSelect;
        protected TextView txId;
        protected ImageView imageType;
        protected TextView txTitle;
        protected TextView txPriority;

        protected TextView txGoalCost;
        protected TextView txInitCost;
        protected TextView txPerCost;


        public EvaluationViewHolder(View view) {
            super(view);
            this.itemView = view;
            this.checkBoxSelect = view.findViewById(R.id.check_box_item_Evaluation_select);
            this.txId = view.findViewById(R.id.text_view_Evaluation_id);
            this.imageType = view.findViewById(R.id.image_view_Evaluation_type);
            this.txTitle = view.findViewById(R.id.text_view_Evaluation_title);
            this.txPriority = view.findViewById(R.id.text_view_Evaluation_priority);

            this.txGoalCost = view.findViewById(R.id.text_view_Evaluation_goal_cost);
            this.txInitCost = view.findViewById(R.id.text_view_Evaluation_init_cost);
            this.txPerCost = view.findViewById(R.id.text_view_Evaluation_per_cost);

            checkBoxSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(Evaluation != null){
                        Evaluation.setCheck(b);
                    }

                    if(compoundButton.isPressed()){
                        if(mListener != null) {
                            mListener.onItemCheckChanged(b);
                        }
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectableMode) {
                        checkBoxSelect.setPressed(true);
                        checkBoxSelect.setChecked(!checkBoxSelect.isChecked());
                    } else {
                        if (mListener != null) {
                            mListener.onItemClick(Integer.parseInt(txId.getText().toString()));
                        }
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (selectableMode) {
                        checkBoxSelect.setPressed(true);
                        checkBoxSelect.setChecked(!checkBoxSelect.isChecked());
                    } else {
                        setSelectableMode(true);
                        checkBoxSelect.setChecked(!checkBoxSelect.isChecked());
                        if (mListener != null) {
                            mListener.onItemLongClick(Integer.parseInt(txId.getText().toString()));
                        }

                    }
                    return true;
                }
            });
        }
    }
}
