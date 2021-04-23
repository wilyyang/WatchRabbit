package wily.apps.watchrabbit;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import java.util.Calendar;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.data.database.RecordDatabase;
import wily.apps.watchrabbit.data.entity.Alarm;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.data.entity.Record;
import wily.apps.watchrabbit.util.DateUtil;
import wily.apps.watchrabbit.util.Utils;

public class AlarmModifyDialog extends Dialog {
    private AlarmModifyDialog mDialog;

    private Context mContext;

    private Alarm mAlarm;

    private boolean isAdd;

    private EditText editTextTile = null;
    private TimePicker timePickerAlarm = null;
    private NumberPicker numberPickerRange = null;
    private NumberPicker numberPickerCost = null;

    private Button btnCancel = null;
    private Button btnAdd = null;

    private final int maxPickerMinuteValue = 1440;
    private final int minPickerMinuteValue = 1;

    private NoticeDialogCallback noticeDialogCallback;

    public AlarmModifyDialog(@NonNull Context context, Alarm alarm, boolean isAdd) {
        super(context);
        setContentView(R.layout.dialog_alarm_modify);
        this.mDialog = this;

        this.mContext = context;
        this.mAlarm = alarm;

        this.isAdd = isAdd;

        initUIComponent();
    }

    // UI
    private void initUIComponent(){
        // Parent Layout
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = (int) (size.x * 0.95f);
        int height = (int) (size.y * 0.85f);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        layoutParams.width = width;
        layoutParams.height = height;

        Window window = getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes(layoutParams);

        // Child UI
        editTextTile = findViewById(R.id.edit_text_alarm_dialog_title);

        timePickerAlarm = findViewById(R.id.time_picker_alarm_dialog);

        numberPickerRange = findViewById(R.id.number_picker_alarm_dialog_range);
        Utils.numberPickerInit(numberPickerRange, minPickerMinuteValue, maxPickerMinuteValue);

        numberPickerCost = findViewById(R.id.number_picker_alarm_dialog_cost);
        Utils.numberPickerInitMinus50(numberPickerCost);

        btnCancel = findViewById(R.id.btn_alarm_dialog_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        btnAdd = findViewById(R.id.btn_alarm_dialog_add);
        if(isAdd){
            btnAdd.setText(R.string.base_btn_add);
            setRecord("", System.currentTimeMillis(), 1, 0);
        }
        else {
            btnAdd.setText(R.string.base_btn_update);
            setRecord(mAlarm.getTitle(), mAlarm.getTime(), mAlarm.getRange(), mAlarm.getCost());
        }
        btnAdd.setOnClickListener(addUpdateClickListener);
    }

    private void setRecord(String title, long time, long range, int cost){
        editTextTile.setText(title);

        int hour = DateUtil.getDateNum(time, Calendar.HOUR_OF_DAY);
        int minute = DateUtil.getDateNum(time, Calendar.MINUTE);

        timePickerAlarm.setHour(hour);
        timePickerAlarm.setMinute(minute);

        numberPickerRange.setValue((int)range);
        numberPickerCost.setValue(cost+100);
    }

    // Listener
    private View.OnClickListener addUpdateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String getTitle = editTextTile.getText().toString();

            int hour=timePickerAlarm.getHour();
            int minute=timePickerAlarm.getMinute();

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            long getTime = calendar.getTimeInMillis();
            long getRange = numberPickerRange.getValue();
            int getCost = numberPickerCost.getValue() - 100;

            mAlarm.setTitle(getTitle);
            mAlarm.setTime(getTime);
            mAlarm.setRange(getRange);
            mAlarm.setCost(getCost);
            if(isAdd){
                noticeDialogCallback.onDialogAddClick(mAlarm);

            }else{
                noticeDialogCallback.onDialogUpdateClick(mAlarm);
            }
            mDialog.dismiss();
        }
    };

    public void setNoticeDialogCallback(NoticeDialogCallback dialogCallback){
        noticeDialogCallback = dialogCallback;
    }

    public interface NoticeDialogCallback {
        public void onDialogAddClick(Alarm alarm);
        public void onDialogUpdateClick(Alarm alarm);
    }
}
