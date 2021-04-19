package wily.apps.watchrabbit.util;

import android.app.ActivityManager;
import android.content.Context;
import android.widget.ImageView;
import android.widget.NumberPicker;

import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.data.entity.Habbit;

public class Utils {
    public static final int maxPickerValue = 100;
    public static final int minPickerValue = -100;

    public static boolean isServiceRunning(Context context, String className) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (className.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void setIcon(ImageView image, int type){
        switch (type){
            case Habbit.TYPE_HABBIT_CHECK:
                image.setImageResource(R.drawable.ic_type_check);
                break;
            case Habbit.TYPE_HABBIT_TIMER:
                image.setImageResource(R.drawable.ic_type_timer);
                break;
        }
    }

    public static void numberPickerInitMinus50(NumberPicker numberPicker){
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(maxPickerValue - minPickerValue);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int index) {
                return Integer.toString(index + minPickerValue);
            }
        });
        numberPicker.setValue((maxPickerValue - minPickerValue)/2);
    }

    public static void numberPickerInit(NumberPicker numberPicker, int min, int max){
        numberPicker.setMaxValue(max);
        numberPicker.setMinValue(min);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(0);
    }
}
