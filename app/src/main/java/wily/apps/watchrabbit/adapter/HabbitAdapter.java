package wily.apps.watchrabbit.adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.data.DataConst;
import wily.apps.watchrabbit.data.entity.Habbit;

public class HabbitAdapter extends BaseAdapter {
    private List<Habbit> mList;
    private Context mContext;
    private OnItemClickListener mListener = null;

    private boolean selectableMode = false;
    private boolean allCheck = false;

    // Base
    public HabbitAdapter(Context context, List<Habbit> habbitList) {
        this.mContext = context;
        this.mList = habbitList;
    }

    @Override
    public int getCount() {
        return (null != mList ? mList.size() : 0);
    }

    @Override
    public Object getItem(int pos) {
        return mList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return mList.get(pos).getId();
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        HabbitViewHolder viewHolder = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_habbit, viewGroup, false);
            viewHolder = new HabbitViewHolder(view, pos);
            view.setTag(viewHolder);
        } else {
            viewHolder = (HabbitViewHolder) view.getTag();
        }
        viewHolder.setContent(mList.get(pos));
        return view;
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


    // CheckMode
    public boolean isSelectableMode(){
        return selectableMode;
    }

    public void setSelectableMode(boolean flag){
        selectableMode = flag;
//        notifyDataSetChanged();
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
    public class HabbitViewHolder {
        private Habbit habbit;
        private int pos;

        protected View itemView;
        protected CheckBox checkBoxDelete;
        protected TextView txId;
        protected ImageView imageType;
        protected TextView txTitle;
        protected TextView txGoalCost;
        protected TextView txInitCost;
        protected TextView txPerCost;

        public HabbitViewHolder(View view, int position) {
            this.pos = position;

            this.itemView = view;
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
                    if(habbit != null){
                        habbit.setCheck(b);
                    }

                    if(compoundButton.isPressed()){
                        mListener.onItemCheckChanged(b);
                        if(!b){
                            allCheck = false;
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
                        mListener.onItemClick(Integer.parseInt(txId.getText().toString()));
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
                        mListener.onItemLongClick(Integer.parseInt(txId.getText().toString()));
                    }
                    return true;
                }
            });
        }

        protected void setContent(Habbit phabbit){
            this.habbit = phabbit;

            setIcon(imageType, habbit.getType());
            txId.setText(""+habbit.getId());
            txTitle.setText(habbit.getTitle());
            txGoalCost.setText(""+habbit.getGoalCost());
            txInitCost.setText(""+habbit.getInitCost());
            txPerCost.setText(""+habbit.getPerCost());
            if(!habbit.isActive()){
                itemView.setBackground(mContext.getDrawable(R.drawable.bg_layout_round_disabled));
            }

            if (!selectableMode) {
                checkBoxDelete.setVisibility(View.GONE);
            } else {
                checkBoxDelete.setVisibility(View.VISIBLE);
            }

            if (habbit.isCheck()) {
                checkBoxDelete.setChecked(true);
            } else {
                checkBoxDelete.setChecked(false);
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
    }
}
