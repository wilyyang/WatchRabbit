package wily.apps.watchrabbit.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import wily.apps.watchrabbit.R;

public class RecordDialog extends Dialog {
    RecordDialog dialog;
    public RecordDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_record_info);

        dialog = this;

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


        getWindow().setAttributes(layoutParams);

        TimePicker picker=(TimePicker)findViewById(R.id.timePicker_record);
        picker.setIs24HourView(true);

        NumberPicker numberPicker=(NumberPicker)findViewById(R.id.record_due_time);
        numberPickerInit(numberPicker);

        Button buttonCancel = findViewById(R.id.btn_record_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private final int maxPickerValue = 1000;
    private final int minPickerValue = 1;
    private void numberPickerInit(NumberPicker numberPicker){
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(maxPickerValue - minPickerValue);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(10);
    }
}
