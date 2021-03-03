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
import wily.apps.watchrabbit.DataConst;
import wily.apps.watchrabbit.data.entity.Habbit;

public class HabbitAdapter extends RecyclerView.Adapter<HabbitAdapter.HabbitViewHolder>{
    private ArrayList<Habbit> mList;
    private Context mContext;
    private OnItemClickListener mListener = null;

    private boolean selectableMode = false;

    // Base
    public HabbitAdapter(Context context, ArrayList<Habbit> habbitList) {
        this.mContext = context;
        this.mList = habbitList;
    }

    // Listener
    public interface OnItemClickListener{
        void onItemClick(int id);
        void onItemLongClick(int pos);
        void onItemCheckChanged(boolean flag);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
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

    protected void setContent(HabbitViewHolder holder, Habbit phabbit){
        holder.habbit = phabbit;
        setIcon(holder.imageType, phabbit.getType());
        holder.txId.setText(""+phabbit.getId());
        holder.txTitle.setText(phabbit.getTitle());
        holder.txPriority.setText(""+phabbit.getPriority());
        holder.txGoalCost.setText(""+phabbit.getGoalCost());
        holder.txInitCost.setText(""+phabbit.getInitCost());
        holder.txPerCost.setText(""+phabbit.getPerCost());
        if(phabbit.isActive()){
            holder.itemView.setBackground(mContext.getDrawable(R.drawable.bg_layout_round));
        }else{
            holder.itemView.setBackground(mContext.getDrawable(R.drawable.bg_layout_round_disabled));
        }

        if (!selectableMode) {
            holder.checkBoxDelete.setVisibility(View.GONE);
        } else {
            holder.checkBoxDelete.setVisibility(View.VISIBLE);
        }

        if (phabbit.isCheck()) {
            holder.checkBoxDelete.setChecked(true);
        } else {
            holder.checkBoxDelete.setChecked(false);
        }
    }

    private void setIcon(ImageView image, int type){
        switch (type){
            case DataConst.TYPE_HABBIT_CHECK:
                image.setImageResource(R.drawable.ic_check_circle);
                break;
            case DataConst.TYPE_HABBIT_TIMER:
                image.setImageResource(R.drawable.ic_snooze);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

    // CheckMode
    public boolean isSelectableMode(){
        return selectableMode;
    }

    public void setSelectableMode(boolean flag){
        selectableMode = flag;
        for(Habbit h : mList){
            h.setCheck(false);
        }
        notifyDataSetChanged();
    }

    public void setAllChecked(boolean flag){
        for(Habbit h : mList){
            h.setCheck(flag);
        }
        notifyDataSetChanged();
    }

    public List<Integer> getCheckedIds(){
        ArrayList<Integer> list = new ArrayList<>();
        for(Habbit h : mList){
            if(h.isCheck()){
                list.add(h.getId());
            }
        }
        return list;
    }



    // ViewHolder
    public class HabbitViewHolder extends RecyclerView.ViewHolder  {
        private Habbit habbit;

        protected View itemView;
        protected CheckBox checkBoxDelete;
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
            this.checkBoxDelete = view.findViewById(R.id.habbit_delete_check);
            this.txId = view.findViewById(R.id.habbit_id);
            this.imageType = view.findViewById(R.id.habbit_type);
            this.txTitle = view.findViewById(R.id.habbit_title);
            this.txPriority = view.findViewById(R.id.habbit_priority);
            this.txGoalCost = view.findViewById(R.id.habbit_goal_cost);
            this.txInitCost = view.findViewById(R.id.habbit_init_cost);
            this.txPerCost = view.findViewById(R.id.habbit_per_cost);

            checkBoxDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
                    if(selectableMode){
                        checkBoxDelete.setPressed(true);
                        checkBoxDelete.setChecked(!checkBoxDelete.isChecked());
                    }else{
                        if(mListener != null) {
                            mListener.onItemClick(Integer.parseInt(txId.getText().toString()));
                        }
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(selectableMode){
                        checkBoxDelete.setPressed(true);
                        checkBoxDelete.setChecked(!checkBoxDelete.isChecked());
                    }else{
                        setSelectableMode(true);
                        checkBoxDelete.setChecked(!checkBoxDelete.isChecked());
                        if(mListener != null){
                            mListener.onItemLongClick(Integer.parseInt(txId.getText().toString()));
                        }

                    }
                    return true;
                }
            });
        }
    }
}
