package wily.apps.watchrabbit;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.data.database.RecordDatabase;
import wily.apps.watchrabbit.data.entity.Record;
import wily.apps.watchrabbit.util.DateUtil;

public class RecordDialog extends Dialog {
    private RecordDialog dialog;
    private Context mContext;

    private ImageView recordType = null;
    private TextView recordTitle = null;
    private DatePicker datePickerRecord = null;
    private TimePicker timePickerRecord = null;

    private TextView recordDueTimeLabel = null;
    private NumberPicker numberPickerRecord = null;

    private Button btnCancel = null;
    private Button btnSave = null;

    private int mType = 0;

    private long MINUTE = 60 * 1000;

    public RecordDialog(@NonNull Context context, long id, boolean isAdd, int type, long time, long duration) {
        super(context);
        setContentView(R.layout.dialog_record_info);

        this.dialog = this;
        this.mContext = context;
        this.mType = type;
        initUIComponent(type);

        if(isAdd){
            btnSave.setText(R.string.base_btn_add);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RecordDatabase db = RecordDatabase.getAppDatabase(mContext);
                    long time = DateUtil.getDateLong(datePickerRecord.getYear(), datePickerRecord.getMonth(), datePickerRecord.getDayOfMonth(),
                            timePickerRecord.getHour(), timePickerRecord.getMinute(), 0);
                    if(mType == DataConst.TYPE_HABBIT_CHECK){


                        db.recordDao().insert(new Record(-1, mType, time, DataConst.HABBIT_STATE_CHECK, -1)).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(item2 ->{dialog.dismiss();});
                    }else if(mType == DataConst.TYPE_HABBIT_TIMER){

                        db.recordDao().insert(new Record(-1, mType, time, DataConst.HABBIT_STATE_TIMER_START, -1)).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(item -> {
                                    long stopTime = time+(numberPickerRecord.getValue()*MINUTE);
                                    db.recordDao().insert(new Record(-1, mType, stopTime, DataConst.HABBIT_STATE_TIMER_STOP, item)).subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(item2 ->{dialog.dismiss();});
                                });
                    }
                }
            });
            setRecord(type, System.currentTimeMillis(), 0);
        }else{

            if(duration == -1){
                return;
            }else{
                btnSave.setText(R.string.base_btn_update);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RecordDatabase db = RecordDatabase.getAppDatabase(mContext);
                        long time = DateUtil.getDateLong(datePickerRecord.getYear(), datePickerRecord.getMonth(), datePickerRecord.getDayOfMonth(),
                                timePickerRecord.getHour(), timePickerRecord.getMinute(), 0);

                        if(mType == DataConst.TYPE_HABBIT_CHECK){
                            db.recordDao().updateTime(id, time);
                        }else if(mType == DataConst.TYPE_HABBIT_TIMER){
                            long stopTime = time+(numberPickerRecord.getValue()*MINUTE);
                            db.recordDao().updateTime(id, time);
                            db.recordDao().updateTimePair(id, stopTime);
                        }
                    }
                });
                setRecord(type, time, duration);
            }

        }
    }

    private void initUIComponent(int type){
        // Parent Layout
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width  = (int)(size.x * 0.95f);
        int height = (int)(size.y * 0.7f);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        layoutParams.width = width;
        layoutParams.height = height;

        Window window = getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes(layoutParams);

        // Child UI
        recordType = findViewById(R.id.record_dialog_type);
        recordTitle = findViewById(R.id.record_dialog_title);

        datePickerRecord = findViewById(R.id.datePicker_record);

        timePickerRecord = findViewById(R.id.timePicker_record);
        timePickerRecord.setIs24HourView(true);

        recordDueTimeLabel = findViewById(R.id.record_due_time_label);
        numberPickerRecord = findViewById(R.id.record_due_time);
        numberPickerInit(numberPickerRecord);

        btnSave = findViewById(R.id.btn_record_dialog_add);
        btnCancel = findViewById(R.id.btn_record_dialog_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        setIcon(recordType, type);
        if(type == DataConst.TYPE_HABBIT_CHECK){
            recordDueTimeLabel.setVisibility(View.INVISIBLE);
            numberPickerRecord.setVisibility(View.INVISIBLE);
        }else if(type == DataConst.TYPE_HABBIT_TIMER){
            recordDueTimeLabel.setVisibility(View.VISIBLE);
            numberPickerRecord.setVisibility(View.VISIBLE);
        }
    }

    private void setRecord(int type, long time, long duration){

        int hour = DateUtil.getDateNum(time, Calendar.HOUR);
        int minute = DateUtil.getDateNum(time, Calendar.MINUTE);
        int second = DateUtil.getDateNum(time, Calendar.SECOND);

        timePickerRecord.setHour(hour);
        timePickerRecord.setMinute(minute);

        if(type == DataConst.TYPE_HABBIT_TIMER){
            numberPickerRecord.setValue((int)duration);
        }
    }

    private void setIcon(ImageView image, int type){
        switch (type){
            case DataConst.TYPE_HABBIT_CHECK:
                image.setImageResource(R.drawable.ic_type_check);
                break;
            case DataConst.TYPE_HABBIT_TIMER:
                image.setImageResource(R.drawable.ic_type_timer);
                break;
        }
    }

    private final int maxPickerValue = 1440;
    private final int minPickerValue = 1;
    private void numberPickerInit(NumberPicker numberPicker){
        numberPicker.setMinValue(minPickerValue);
        numberPicker.setMaxValue(maxPickerValue);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(10);
    }
}
