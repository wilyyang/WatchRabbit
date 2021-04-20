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
import wily.apps.watchrabbit.data.entity.Alarm;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.data.entity.Record;
import wily.apps.watchrabbit.util.DateUtil;
import wily.apps.watchrabbit.util.Utils;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>{
    private ArrayList<Alarm> mList;
    private Context mContext;
    private OnAlarmItemClickListener mListener = null;
    private boolean selectableMode = false;

    // Listener
    public interface OnAlarmItemClickListener{
        void onItemClick(long id, long time, long range, int cost);
        void onItemLongClick(long id);
        void onItemCheckChanged(boolean flag);
    }

    // Base
    public AlarmAdapter(Context context, ArrayList<Alarm> alarmList) {
        this.mContext = context;
        this.mList = alarmList;
    }

    public void setOnItemClickListener(OnAlarmItemClickListener listener){
        this.mListener = listener;
    }

    // Item Init
    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_alarm, viewGroup, false);
        AlarmViewHolder viewHolder = new AlarmViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        setContent(holder, mList.get(position));
    }

    protected void setContent(AlarmViewHolder holder, Alarm pAlarm){
        holder.alarm = pAlarm;

        holder.txTime.setText(DateUtil.getTimeString(pAlarm.getTime()));
        holder.txRange.setText(""+pAlarm.getRange());
        holder.txCost.setText(""+pAlarm.getCost());

        if (!selectableMode) {
            holder.checkBoxSelect.setVisibility(View.GONE);
        } else {
            holder.checkBoxSelect.setVisibility(View.VISIBLE);
        }

        if (pAlarm.isCheck()) {
            holder.checkBoxSelect.setChecked(true);
        } else {
            holder.checkBoxSelect.setChecked(false);
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
        for (Alarm a : mList) {
            a.setCheck(false);
        }
        notifyDataSetChanged();
    }

    public void setAllChecked(boolean flag) {
        for (Alarm a : mList) {
            a.setCheck(flag);
        }
        notifyDataSetChanged();
    }

    public List<Long> getCheckedIds() {
        ArrayList<Long> list = new ArrayList<>();
        for (Alarm a : mList) {
            if (a.isCheck()) {
                list.add(a.getId());
            }
        }
        return list;
    }

    // ViewHolder
    public class AlarmViewHolder extends RecyclerView.ViewHolder  {
        private Alarm alarm;

        protected View itemView;
        protected CheckBox checkBoxSelect;

        protected TextView txTime;
        protected TextView txRange;
        protected TextView txCost;

        public AlarmViewHolder(View view) {
            super(view);
            this.itemView = view;
            this.checkBoxSelect = view.findViewById(R.id.check_box_item_alarm_select);

            this.txTime = view.findViewById(R.id.text_view_alarm_time);
            this.txRange = view.findViewById(R.id.text_view_alarm_range);
            this.txCost = view.findViewById(R.id.text_view_alarm_cost);


            checkBoxSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (alarm != null) {
                        alarm.setCheck(b);
                    }

                    if (compoundButton.isPressed()) {
                        if (mListener != null) {
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
                            mListener.onItemClick(alarm.getId(), alarm.getTime(), alarm.getRange(), alarm.getCost());
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
                            mListener.onItemLongClick(alarm.getId());
                        }
                    }
                    return true;
                }
            });
        }
    }
}
