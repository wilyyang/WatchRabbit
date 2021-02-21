package wily.apps.watchrabbit.adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.data.DataConst;
import wily.apps.watchrabbit.data.entity.Habbit;

public class HabbitAdapter extends RecyclerView.Adapter<HabbitAdapter.HabbitViewHolder> {
    private List<Habbit> mList;
    private Context mContext;
    private boolean selectableMode = false;
    private boolean allCheck = false;
    private OnItemClickListener mListener = null;
    public int select = -1;

    public ArrayList<HabbitViewHolder> checkedViewHolders = new ArrayList<>();

    public HabbitAdapter(List<Habbit> list, Context context) {
        this.checkedViewHolders = new ArrayList<>();
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public HabbitViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_habbit, viewGroup, false);
        HabbitViewHolder viewHolder = new HabbitViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HabbitViewHolder viewholder, int position) {
        viewholder.txId.setGravity(Gravity.CENTER);
        viewholder.txTitle.setGravity(Gravity.CENTER);
        viewholder.txGoalCost.setGravity(Gravity.CENTER);
        viewholder.txInitCost.setGravity(Gravity.CENTER);
        viewholder.txPerCost.setGravity(Gravity.CENTER);

        viewholder.txId.setText(""+mList.get(position).getId());

        setIcon(viewholder.imageType, mList.get(position).getType());

        viewholder.txTitle.setText(""+mList.get(position).getTitle());
        viewholder.txGoalCost.setText(""+mList.get(position).getGoalCost());
        viewholder.txInitCost.setText(""+mList.get(position).getInitCost());
        viewholder.txPerCost.setText(""+mList.get(position).getPerCost());

        if(!mList.get(position).isActive()){
            viewholder.itemView.setBackground(mContext.getDrawable(R.drawable.bg_layout_round_disabled));
        }

        if (!selectableMode) { viewholder.checkBoxDelete.setVisibility(View.GONE); }
        else { viewholder.checkBoxDelete.setVisibility(View.VISIBLE); }
        viewholder.checkBoxDelete.setChecked(false);

        if(allCheck || position == select){
            viewholder.checkBoxDelete.setChecked(true);
        }
        checkedViewHolders.add(viewholder);
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

    public void setSelectableMode(boolean flag, int p_select){
        selectableMode=flag;
        select = p_select;
        allCheck = false;
        this.checkedViewHolders = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setAllChecked(boolean flag){
        select = -1;
        allCheck=flag;
        this.checkedViewHolders = new ArrayList<>();
        notifyDataSetChanged();
    }

    public boolean isSelectableMode(){
        return selectableMode;
    }

    public ArrayList<Integer> getSelectIds(){
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for(HabbitViewHolder item : checkedViewHolders){
            if(item.checkBoxDelete.isChecked()){
                ids.add(Integer.parseInt(item.txId.getText().toString()));
            }
        }
        return ids;
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

    public interface OnItemClickListener{
        void onItemClick(int id);
        void onItemLongClick(int pos);
        void onItemCheckChanged(boolean flag);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public class HabbitViewHolder extends RecyclerView.ViewHolder {
        protected CheckBox checkBoxDelete;
        protected TextView txId;
        protected ImageView imageType;
        protected TextView txTitle;
        protected TextView txGoalCost;
        protected TextView txInitCost;
        protected TextView txPerCost;

        public HabbitViewHolder(View view) {
            super(view);
            this.checkBoxDelete = view.findViewById(R.id.habbit_delete_check);
            this.txId = view.findViewById(R.id.habbit_id);
            this.imageType = view.findViewById(R.id.habbit_type);
            this.txTitle = view.findViewById(R.id.habbit_title);
            this.txGoalCost = view.findViewById(R.id.habbit_goal_cost);
            this.txInitCost = view.findViewById(R.id.habbit_init_cost);
            this.txPerCost = view.findViewById(R.id.habbit_per_cost);

            checkBoxDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(getAdapterPosition() != RecyclerView.NO_POSITION && mListener != null){
                        if(compoundButton.isPressed()){
                            mListener.onItemCheckChanged(b);
                        }
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition() != RecyclerView.NO_POSITION && mListener != null){
                        if(!selectableMode){
                            mListener.onItemClick(Integer.parseInt(txId.getText().toString()));
                        }else{
                            checkBoxDelete.setPressed(true);
                            checkBoxDelete.setChecked(!checkBoxDelete.isChecked());
                            checkBoxDelete.setPressed(false);
                        }
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION && mListener != null){
                        if(!selectableMode){
                            setSelectableMode(true, pos);
                            mListener.onItemLongClick(pos);
                        }else{
                            checkBoxDelete.setPressed(true);
                            checkBoxDelete.setChecked(!checkBoxDelete.isChecked());
                            checkBoxDelete.setPressed(false);
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
