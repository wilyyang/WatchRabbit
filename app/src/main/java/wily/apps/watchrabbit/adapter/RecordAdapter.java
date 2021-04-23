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
import wily.apps.watchrabbit.data.entity.Record;
import wily.apps.watchrabbit.util.DateUtil;
import wily.apps.watchrabbit.util.Utils;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder>{
    private ArrayList<Record> mList;
    private Context mContext;
    private OnRecordItemClickListener mListener = null;
    private boolean selectableMode = false;

    // Listener
    public interface OnRecordItemClickListener{
        void onItemClick(long id, int type, long time, long term);
        void onItemLongClick(int pos);
        void onItemCheckChanged(boolean flag);
    }

    // Base
    public RecordAdapter(Context context, ArrayList<Record> recordList) {
        this.mContext = context;
        this.mList = recordList;
    }

    public void setOnItemClickListener(OnRecordItemClickListener listener){
        this.mListener = listener;
    }

    public void setRecordList(ArrayList<Record> recordList){
        mList = recordList;
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

    protected void setContent(RecordViewHolder holder, Record pRecord){
        holder.record = pRecord;

        holder.txId.setText(""+pRecord.getId());
        Utils.setIcon(holder.imageType, pRecord.getType());
        holder.txHid.setText(""+pRecord.getHid());
        holder.txTime.setText(DateUtil.getDateString(pRecord.getTime()));

        if(pRecord.getType()==Habbit.TYPE_HABBIT_TIMER){
            holder.txTermLabel.setVisibility(View.VISIBLE);
            holder.txTerm.setVisibility(View.VISIBLE);

            if(pRecord.getTerm() != -1){
                holder.txTerm.setText(""+ (pRecord.getTerm()/DateUtil.MILLISECOND_TO_MINUTE));
            }else{
                holder.txTerm.setText("진행중");
            }

        }else{
            holder.txTermLabel.setVisibility(View.INVISIBLE);
            holder.txTerm.setVisibility(View.INVISIBLE);
        }

        if (!selectableMode) {
            holder.checkBoxSelect.setVisibility(View.GONE);
        } else {
            holder.checkBoxSelect.setVisibility(View.VISIBLE);
        }

        if (pRecord.isCheck()) {
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
        for (Record r : mList) {
            r.setCheck(false);
        }
        notifyDataSetChanged();
    }

    public void setAllChecked(boolean flag) {
        for (Record r : mList) {
            r.setCheck(flag);
        }
        notifyDataSetChanged();
    }

    public List<Long> getCheckedIds() {
        ArrayList<Long> list = new ArrayList<>();
        for (Record r : mList) {
            if (r.isCheck()) {
                list.add(r.getId());
            }
        }
        return list;
    }

    // ViewHolder
    public class RecordViewHolder extends RecyclerView.ViewHolder  {
        private Record record;

        protected View itemView;
        protected CheckBox checkBoxSelect;
        protected TextView txId;
        protected ImageView imageType;
        protected TextView txHid;

        protected TextView txTime;
        protected TextView txTermLabel;
        protected TextView txTerm;

        public RecordViewHolder(View view) {
            super(view);
            this.itemView = view;
            this.checkBoxSelect = view.findViewById(R.id.check_box_item_record_select);
            this.txId = view.findViewById(R.id.text_view_record_id);
            this.imageType = view.findViewById(R.id.image_view_record_type);
            this.txHid = view.findViewById(R.id.text_view_record_hid);

            this.txTime = view.findViewById(R.id.text_view_record_time);
            this.txTermLabel = view.findViewById(R.id.text_view_record_term_label);
            this.txTerm = view.findViewById(R.id.text_view_record_term);

            checkBoxSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (record != null) {
                        record.setCheck(b);
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
                            mListener.onItemClick(record.getId(), record.getType(), record.getTime(), record.getTerm());
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
