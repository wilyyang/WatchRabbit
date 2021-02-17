package wily.apps.watchrabbit.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.data.DataConst;
import wily.apps.watchrabbit.data.entity.Habbit;

public class HabbitAdapter extends RecyclerView.Adapter<HabbitAdapter.HabbitViewHolder> {
    private List<Habbit> mList;
    public HabbitAdapter(List<Habbit> list) {
        this.mList = list;
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
        viewholder.swtichActive.setGravity(Gravity.CENTER);
        viewholder.txGoalCost.setGravity(Gravity.CENTER);
        viewholder.txInitCost.setGravity(Gravity.CENTER);
        viewholder.txPerCost.setGravity(Gravity.CENTER);

        viewholder.txId.setText(""+mList.get(position).getId());

        setIcon(viewholder.imageType, mList.get(position).getType());

        viewholder.txTitle.setText(""+mList.get(position).getTitle());
        viewholder.swtichActive.setChecked(mList.get(position).isActive());
        viewholder.txGoalCost.setText(""+mList.get(position).getGoalCost());
        viewholder.txInitCost.setText(""+mList.get(position).getInitCost());
        viewholder.txPerCost.setText(""+mList.get(position).getPerCost());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
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

    public class HabbitViewHolder extends RecyclerView.ViewHolder {
        protected TextView txId;
        protected ImageView imageType;
        protected TextView txTitle;
        protected Switch swtichActive;
        protected TextView txGoalCost;
        protected TextView txInitCost;
        protected TextView txPerCost;

        public HabbitViewHolder(View view) {
            super(view);
            this.txId = view.findViewById(R.id.habbit_id);
            this.imageType = view.findViewById(R.id.habbit_type);
            this.txTitle = view.findViewById(R.id.habbit_title);
            this.swtichActive = view.findViewById(R.id.habbit_active);
            this.txGoalCost = view.findViewById(R.id.habbit_goal_cost);
            this.txInitCost = view.findViewById(R.id.habbit_init_cost);
            this.txPerCost = view.findViewById(R.id.habbit_per_cost);
        }
    }
}
