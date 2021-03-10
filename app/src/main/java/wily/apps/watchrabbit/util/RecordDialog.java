package wily.apps.watchrabbit.util;

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

import wily.apps.watchrabbit.DataConst;
import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.data.entity.Record;

public class RecordDialog extends Dialog {
    private RecordDialog dialog;
    private Context context;

    private ImageView recordType = null;
    private TextView recordTitle = null;
    private DatePicker datePickerRecord = null;
    private TimePicker timePickerRecord = null;
    private NumberPicker numberPickerRecord = null;
    private Button btnCancel = null;
    private Button btnSave = null;

    public RecordDialog(@NonNull Context context, int type, boolean isAdd, Record record) {
        super(context);
        setContentView(R.layout.dialog_timer_info);

        this.dialog = this;
        this.context = context;
        initUIComponent(type, isAdd);
    }

    private void initUIComponent(int type, boolean isAdd){
        // Parent Layout
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
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

        numberPickerRecord = findViewById(R.id.record_due_time);
        numberPickerInit(numberPickerRecord);

        btnCancel = findViewById(R.id.btn_record_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        setIcon(recordType, type);
        if(isAdd){
            findViewById(R.id.layout_due_time).setVisibility(View.GONE);
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

    private final int maxPickerValue = 1440;
    private final int minPickerValue = 1;
    private void numberPickerInit(NumberPicker numberPicker){
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(maxPickerValue - minPickerValue);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(10);
    }
}
