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
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.data.entity.Record;
import wily.apps.watchrabbit.util.DateUtil;
import wily.apps.watchrabbit.util.Utils;

public class RecordModifyDialog extends Dialog {
    private RecordModifyDialog mDialog;

    private Context mContext;
    private int habbitId;
    private int mType = 0;
    private String habbitTitle;
    private long recordId;
    private long recordTime;
    private long recordTerm;

    private boolean isAdd;

    private ImageView recordType = null;
    private TextView recordTitle = null;
    private TimePicker timePickerRecord = null;

    private TextView txTermLabel = null;
    private NumberPicker numberPickerRecord = null;

    private Button btnCancel = null;
    private Button btnAdd = null;

    private final int maxPickerMinuteValue = 1440;
    private final int minPickerMinuteValue = 1;

    public RecordModifyDialog(@NonNull Context context, int habbitId, int type, String habbitTitle, long recordId, long time, long miliSecondTerm, boolean isAdd) {
        super(context);
        setContentView(R.layout.dialog_record_modify);
        this.mDialog = this;

        this.mContext = context;
        this.habbitId = habbitId;
        this.mType = type;
        this.habbitTitle = habbitTitle;
        this.recordId = recordId;
        this.recordTime = time;
        this.recordTerm = miliSecondTerm;

        this.isAdd = isAdd;

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Intent intent = new Intent(mContext, EvaluationRecordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mContext.startActivity(intent);
            }
        });

        initUIComponent();
    }

    // UI
    private void initUIComponent(){
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
        Utils.setIcon(recordType, mType);

        recordTitle = findViewById(R.id.text_view_record_dialog_title);
        recordTitle.setText(habbitTitle);

        timePickerRecord = findViewById(R.id.time_picker_record_dialog);

        txTermLabel = findViewById(R.id.text_view_record_dialog_term_label);
        numberPickerRecord = findViewById(R.id.number_picker_record_dialog_term);
        Utils.numberPickerInit(numberPickerRecord, minPickerMinuteValue, maxPickerMinuteValue);

        if(mType == Habbit.TYPE_HABBIT_CHECK){
            txTermLabel.setVisibility(View.GONE);
            numberPickerRecord.setVisibility(View.GONE);
        }else if(mType == Habbit.TYPE_HABBIT_TIMER){
            txTermLabel.setVisibility(View.VISIBLE);
            numberPickerRecord.setVisibility(View.VISIBLE);
        }


        btnCancel = findViewById(R.id.btn_record_dialog_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        btnAdd = findViewById(R.id.btn_record_dialog_add);
        if(isAdd){
            btnAdd.setText(R.string.base_btn_add);
            setRecord(mType, System.currentTimeMillis(), 1);
        }
        else {
            btnAdd.setText(R.string.base_btn_update);
            setRecord(mType, recordTime, (recordTerm/DateUtil.MILLISECOND_TO_MINUTE) );
        }
        btnAdd.setOnClickListener(addUpdateClickListener);
    }

    private void setRecord(int type, long time, long minuteTerm){
        int hour = DateUtil.getDateNum(time, Calendar.HOUR_OF_DAY);
        int minute = DateUtil.getDateNum(time, Calendar.MINUTE);

        timePickerRecord.setHour(hour);
        timePickerRecord.setMinute(minute);

        if(type == Habbit.TYPE_HABBIT_TIMER){
            numberPickerRecord.setValue((int)minuteTerm);
        }
    }

    // Listener
    private View.OnClickListener addUpdateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecordDatabase recordDB = RecordDatabase.getAppDatabase(mContext);
            long time = DateUtil.getDateLong(DateUtil.getDateNum(recordTime, Calendar.YEAR), DateUtil.getDateNum(recordTime, Calendar.MONTH), DateUtil.getDateNum(recordTime, Calendar.DATE),
                    timePickerRecord.getHour(), timePickerRecord.getMinute(), 0);

            Completable.create(subscriber -> {

                if(isAdd){
                    if(mType == Habbit.TYPE_HABBIT_CHECK){
                        recordDB.recordDao().insert(new Record(habbitId, mType, time, -1));
                    }else if(mType == Habbit.TYPE_HABBIT_TIMER){
                        recordDB.recordDao().insert(new Record(habbitId, mType, time, (numberPickerRecord.getValue()*DateUtil.MILLISECOND_TO_MINUTE)));
                    }
                }else{
                    if(mType == Habbit.TYPE_HABBIT_CHECK){
                        recordDB.recordDao().updateTime(recordId, time);
                    }else if(mType == Habbit.TYPE_HABBIT_TIMER){
                        recordDB.recordDao().updateTimeAndTerm(recordId, time, (numberPickerRecord.getValue()*DateUtil.MILLISECOND_TO_MINUTE));
                    }
                }

                EvaluateWork work = new EvaluateWork(mContext);
                work.work(EvaluateWork.WORK_TYPE_REPLACE_EVALUATION, habbitId, DateUtil.convertDate(recordTime));
                subscriber.onComplete();
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        mDialog.dismiss();
                    });
        }
    };
}
