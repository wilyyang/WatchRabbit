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
import wily.apps.watchrabbit.data.entity.Habbit;

public class HabbitAdapter extends RecyclerView.Adapter<HabbitAdapter.HabbitViewHolder>{
    private ArrayList<Habbit> mList;
    private Context mContext;
    private OnHabbitItemClickListener mListener = null;
    private boolean selectableMode = false;

    // Listener
    public interface OnHabbitItemClickListener{
        void onItemClick(int id);
        void onItemLongClick(int pos);
        void onItemCheckChanged(boolean flag);
    }

    // Base
    public HabbitAdapter(Context context, ArrayList<Habbit> habbitList) {
        this.mContext = context;
        this.mList = habbitList;
    }

    public void setOnItemClickListener(OnHabbitItemClickListener listener){
        this.mListener = listener;
    }

    // Item Init
    @NonNull
    @Override
    public HabbitViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_habbit, viewGroup, false);
        HabbitViewHolder viewHolder = new HabbitViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HabbitViewHolder holder, int position) {
        setContent(holder, mList.get(position));
    }

    protected void setContent(HabbitViewHolder holder, Habbit pHabbit){
        holder.habbit = pHabbit;

        holder.txId.setText(""+pHabbit.getId());
        setIcon(holder.imageType, pHabbit.getType());
        holder.txTitle.setText(pHabbit.getTitle());
        holder.txPriority.setText(""+pHabbit.getPriority());
        holder.txGoalCost.setText(""+pHabbit.getGoalCost());
        holder.txInitCost.setText(""+pHabbit.getInitCost());
        holder.txPerCost.setText(""+pHabbit.getPerCost());

        if(pHabbit.isActive()){
            holder.itemView.setBackground(mContext.getDrawable(R.drawable.bg_layout_round_enabled));
            if(pHabbit.getState() == Habbit.STATE_TIMER_INPROGRESS){
                holder.itemView.setBackground(mContext.getDrawable(R.drawable.bg_layout_round_inprogress));
            }
        }else{
            holder.itemView.setBackground(mContext.getDrawable(R.drawable.bg_layout_round_disabled));
        }

        if (!selectableMode) {
            holder.checkBoxSelect.setVisibility(View.GONE);
        } else {
            holder.checkBoxSelect.setVisibility(View.VISIBLE);
        }

        if (pHabbit.isCheck()) {
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
        for (Habbit h : mList) {
            h.setCheck(false);
        }
        notifyDataSetChanged();
    }

    public void setAllChecked(boolean flag) {
        for (Habbit h : mList) {
            h.setCheck(flag);
        }
        notifyDataSetChanged();
    }

    public List<Integer> getCheckedIds() {
        ArrayList<Integer> list = new ArrayList<>();
        for (Habbit h : mList) {
            if (h.isCheck()) {
                list.add(h.getId());
            }
        }
        return list;
    }

    // ViewHolder
    public class HabbitViewHolder extends RecyclerView.ViewHolder  {
        private Habbit habbit;

        protected View itemView;
        protected CheckBox checkBoxSelect;
        protected TextView txId;
        protected ImageView imageType;
        protected TextView txTitle;
        protected TextView txPriority;

        protected TextView txGoalCost;
        protected TextView txInitCost;
        protected TextView txPerCost;


        public HabbitViewHolder(View view) {
            super(view);
            this.itemView = view;
            this.checkBoxSelect = view.findViewById(R.id.check_box_item_habbit_select);
            this.txId = view.findViewById(R.id.text_view_habbit_id);
            this.imageType = view.findViewById(R.id.image_view_habbit_type);
            this.txTitle = view.findViewById(R.id.text_view_habbit_title);
            this.txPriority = view.findViewById(R.id.text_view_habbit_priority);

            this.txGoalCost = view.findViewById(R.id.text_view_habbit_goal_cost);
            this.txInitCost = view.findViewById(R.id.text_view_habbit_init_cost);
            this.txPerCost = view.findViewById(R.id.text_view_habbit_per_cost);

            checkBoxSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(habbit != null){
                        habbit.setCheck(b);
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
