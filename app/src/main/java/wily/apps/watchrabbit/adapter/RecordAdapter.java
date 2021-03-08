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
import java.util.HashMap;
import java.util.List;

import wily.apps.watchrabbit.DataConst;
import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.data.entity.Record;
import wily.apps.watchrabbit.util.DateUtil;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder>{
    private ArrayList<Record> mList;
    private Context mContext;
    private OnItemClickListener mListener = null;

    private boolean selectableMode = false;
    private HashMap<Long, Long> stopHash = null;

    // Base
    public RecordAdapter(Context context, ArrayList<Record> recordList, HashMap<Long, Long> stopTime) {
        this.mContext = context;
        this.mList = recordList;
        this.stopHash = stopTime;
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
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_record, viewGroup, false);
        RecordViewHolder viewHolder = new RecordViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        setContent(holder, mList.get(position));
    }

    protected void setContent(RecordViewHolder holder, Record precord){
        holder.record = precord;
        setIcon(holder.imageType, precord.getType());
        holder.txId.setText(""+precord.getId());

        holder.txTime.setText(DateUtil.getDateString(precord.getTime()));
        holder.txHid.setText(""+precord.getHid());
        holder.txState.setText(getStateText(precord.getState()));

        //
        if(precord.getState()==DataConst.HABBIT_STATE_TIMER_START){
            Long stop = stopHash.get(precord.getId());
            if(stop != null){
                Long due = stop - precord.getTime();
                holder.txPairId.setText(""+ (due / 1000));
            }else{
                holder.txPairId.setText("진행중");
            }

        }else{
            holder.txPairId.setText(""+precord.getPair());
        }
        //

        if(precord.getState()==DataConst.HABBIT_STATE_TIMER_STOP){
            holder.itemView.setBackground(mContext.getDrawable(R.drawable.bg_layout_round_disabled));
        }else{
            holder.itemView.setBackground(mContext.getDrawable(R.drawable.bg_layout_round));
        }

        if (!selectableMode) {
            holder.checkBoxDelete.setVisibility(View.GONE);
        } else {
            holder.checkBoxDelete.setVisibility(View.VISIBLE);
        }

        if (precord.isCheck()) {
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

    private String getStateText(int state){
        switch (state){
            case DataConst.HABBIT_STATE_CHECK:
                return mContext.getString(R.string.record_state_check);
            case DataConst.HABBIT_STATE_TIMER_START:
                return mContext.getString(R.string.record_state_timer_start);
            case DataConst.HABBIT_STATE_TIMER_STOP:
                return mContext.getString(R.string.record_state_timer_stop);
            default:
                return "Unknown";
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
        for(Record r : mList){
            r.setCheck(false);
        }
        notifyDataSetChanged();
    }

    public void setAllChecked(boolean flag){
        for(Record r : mList){
            r.setCheck(flag);
        }
        notifyDataSetChanged();
    }

    public List<Long> getCheckedIds(){
        ArrayList<Long> list = new ArrayList<>();
        for(Record r : mList){
            if(r.isCheck()){
                list.add(r.getId());
            }
        }
        return list;
    }

    // ViewHolder
    public class RecordViewHolder extends RecyclerView.ViewHolder  {
        private Record record;

        protected View itemView;
        protected CheckBox checkBoxDelete;
        protected TextView txId;
        protected ImageView imageType;

        protected TextView txTime;
        protected TextView txHid;
        protected TextView txState;
        protected TextView txPairId;


        public RecordViewHolder(View view) {
            super(view);
            this.itemView = view;
            this.checkBoxDelete = view.findViewById(R.id.record_delete_check);
            this.txId = view.findViewById(R.id.record_id);
            this.imageType = view.findViewById(R.id.record_type);
            this.txTime = view.findViewById(R.id.record_time);
            this.txHid = view.findViewById(R.id.record_hid);
            this.txState = view.findViewById(R.id.record_state);
            this.txPairId = view.findViewById(R.id.record_pair);

            checkBoxDelete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(record != null){
                        record.setCheck(b);
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