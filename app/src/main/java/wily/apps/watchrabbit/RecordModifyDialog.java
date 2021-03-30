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
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.data.entity.Record;
import wily.apps.watchrabbit.util.DateUtil;

public class RecordModifyDialog extends Dialog {
    private RecordModifyDialog mDialog;

    private Context mContext;
    private int mType = 0;

    private ImageView recordType = null;
    private TextView recordTitle = null;
    private DatePicker datePickerRecord = null;
    private TimePicker timePickerRecord = null;

    private TextView txTermLabel = null;
    private NumberPicker numberPickerRecord = null;

    private Button btnCancel = null;
    private Button btnAdd = null;

    private final int maxPickerMinuteValue = 1440;
    private final int minPickerMinuteValue = 1;

    public RecordModifyDialog(@NonNull Context context, long id, int type, long time, long term, boolean isAdd) {
        super(context);
        setContentView(R.layout.dialog_record_modify);

        this.mDialog = this;
        this.mContext = context;
        this.mType = type;

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Intent intent = new Intent(mContext, EvaluationRecordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mContext.startActivity(intent);
            }
        });

        initUIComponent(type);

        if(isAdd){
            btnAdd.setText(R.string.base_btn_add);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RecordDatabase recordDB = RecordDatabase.getAppDatabase(mContext);
                    long time = DateUtil.getDateLong(datePickerRecord.getYear(), datePickerRecord.getMonth(), datePickerRecord.getDayOfMonth(),
                            timePickerRecord.getHour(), timePickerRecord.getMinute(), 0);
                    if(mType == Habbit.TYPE_HABBIT_CHECK){

                        recordDB.recordDao().insert(new Record(-1, mType, time, Habbit.STATE_CHECK, -1)).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(item ->{
                                    mDialog.dismiss();});
                    }else if(mType == Habbit.TYPE_HABBIT_TIMER){
                        long stopTime = time+(numberPickerRecord.getValue()*DateUtil.MILLISECOND_TO_MINUTE);
                        recordDB.recordDao().insert(new Record(-1, mType, time, Habbit.STATE_TIMER_WAIT, -1)).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(item -> {
                                    recordDB.recordDao().insert(new Record(-1, mType, stopTime, Habbit.STATE_TIMER_INPROGRESS, item)).subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(subItem ->{
                                                mDialog.dismiss();});
                                });
                    }
                }
            });
            setRecord(type, System.currentTimeMillis(), 1);
        }
        else {
            if(term == -1){
                return;
            }else{
                btnAdd.setText(R.string.base_btn_update);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RecordDatabase recordDB = RecordDatabase.getAppDatabase(mContext);
                        long time = DateUtil.getDateLong(datePickerRecord.getYear(), datePickerRecord.getMonth(), datePickerRecord.getDayOfMonth(),
                                timePickerRecord.getHour(), timePickerRecord.getMinute(), 0);

                        if(mType == Habbit.TYPE_HABBIT_CHECK){
                            recordDB.recordDao().updateTime(id, time).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(item ->{
                                        mDialog.dismiss();});
                        }else if(mType == Habbit.TYPE_HABBIT_TIMER){
                            long stopTime = time+(numberPickerRecord.getValue()*DateUtil.MILLISECOND_TO_MINUTE);
                            recordDB.recordDao().updateTime(id, time).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(item ->{
                                        recordDB.recordDao().updateTimePair(id, stopTime).subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(subItem ->{
                                                    mDialog.dismiss();
                                                });
                                    });
                        }
                    }
                });
                setRecord(type, time, term);
            }
        }
    }

    private void setRecord(int type, long time, long term){
        int hour = DateUtil.getDateNum(time, Calendar.HOUR);
        int minute = DateUtil.getDateNum(time, Calendar.MINUTE);
        int second = DateUtil.getDateNum(time, Calendar.SECOND);

        timePickerRecord.setHour(hour);
        timePickerRecord.setMinute(minute);

        if(type == Habbit.TYPE_HABBIT_TIMER){
            numberPickerRecord.setValue((int)term);
        }
    }

    // UI
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
        recordType = findViewById(R.id.image_view_record_dialog_type);
        setIcon(recordType, type);

        recordTitle = findViewById(R.id.text_view_record_dialog_title);

        datePickerRecord = findViewById(R.id.date_picker_record_dialog);

        timePickerRecord = findViewById(R.id.time_picker_record_dialog);
        timePickerRecord.setIs24HourView(true);

        txTermLabel = findViewById(R.id.text_view_record_dialog_term_label);
        numberPickerRecord = findViewById(R.id.number_picker_record_dialog_term);
        numberPickerInit(numberPickerRecord);

        if(type == Habbit.TYPE_HABBIT_CHECK){
            txTermLabel.setVisibility(View.INVISIBLE);
            numberPickerRecord.setVisibility(View.INVISIBLE);
        }else if(type == Habbit.TYPE_HABBIT_TIMER){
            txTermLabel.setVisibility(View.VISIBLE);
            numberPickerRecord.setVisibility(View.VISIBLE);
        }

        btnAdd = findViewById(R.id.btn_record_dialog_add);
        btnCancel = findViewById(R.id.btn_record_dialog_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
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

    private void numberPickerInit(NumberPicker numberPicker){
        numberPicker.setMaxValue(maxPickerMinuteValue);
        numberPicker.setMinValue(minPickerMinuteValue);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(10);
    }
}
