package wily.apps.watchrabbit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.entity.Habbit;

public class HabbitModifyActivity extends AppCompatActivity {

    private LinearLayout layoutChild_check = null;
    private LinearLayout layoutChild_timer = null;

    private EditText etTitleHabbit = null;
    private Switch switchHabbit = null;

    private RadioGroup radioGroupHabbit = null;
    private RadioButton radioBtn_check = null;
    private RadioButton radioBtn_timer = null;

    private NumberPicker numberPickerGoal_check = null;
    private NumberPicker numberPickerInit_check = null;
    private NumberPicker numberPickerPer_check = null;

    private NumberPicker numberPickerGoal_timer = null;
    private NumberPicker numberPickerInit_timer = null;
    private NumberPicker numberPickerPer_timer = null;

    private Button btnSave = null;
    private Button btnCancel = null;

    private final int maxPickerValue = 100;
    private final int minPickerValue = -100;

    private final int TYPE_CHECK = 1;
    private final int TYPE_TIMER = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habbit_modify);

        initView();
    }

    private void initView(){
        etTitleHabbit = findViewById(R.id.et_title_habbit);
        switchHabbit = findViewById(R.id.switch_habbit);

        numberPickerGoal_check = findViewById(R.id.check_picker_goal);
        numberPickerInit(numberPickerGoal_check);
        numberPickerInit_check = findViewById(R.id.check_picker_init);
        numberPickerInit(numberPickerInit_check);
        numberPickerPer_check = findViewById(R.id.check_picker_per);
        numberPickerInit(numberPickerPer_check);
        numberPickerGoal_timer = findViewById(R.id.timer_picker_goal);
        numberPickerInit(numberPickerGoal_timer);
        numberPickerInit_timer = findViewById(R.id.timer_picker_init);
        numberPickerInit(numberPickerInit_timer);
        numberPickerPer_timer = findViewById(R.id.timer_picker_per);
        numberPickerInit(numberPickerPer_timer);

        layoutChild_check = findViewById(R.id.include_habbit_modify_check);
        layoutChild_timer = findViewById(R.id.include_habbit_modify_timer);

        radioBtn_check = findViewById(R.id.radio_habbit_check);
        radioBtn_timer = findViewById(R.id.radio_habbit_timer);
        radioGroupHabbit = findViewById(R.id.radio_group_habbit);
        radioGroupHabbit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio_habbit_check:
                        changeViewAtType(TYPE_CHECK);
                        break;
                    case R.id.radio_habbit_timer:
                        changeViewAtType(TYPE_TIMER);
                        break;
                }
            }
        });
        radioBtn_check.setChecked(true);
        radioBtn_timer.setChecked(false);
        changeViewAtType(TYPE_CHECK);

        btnSave = findViewById(R.id.btn_habbit_modify_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                HabbitDatabase db = HabbitDatabase.getAppDatabase(HabbitModifyActivity.this);
//                db.habbitDao().insert(new Habbit(type, title, active, goalCost, initCost, perCost)).subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe();
                finish();
            }
        });
        btnCancel = findViewById(R.id.btn_habbit_modify_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void changeViewAtType(int type){
        switch (type){
            case TYPE_CHECK:
                layoutChild_check.setVisibility(View.VISIBLE);
                layoutChild_timer.setVisibility(View.GONE);
                numberPickerGoal_check.setValue(0 - minPickerValue);
                numberPickerInit_check.setValue(0 - minPickerValue);
                numberPickerPer_check.setValue(0 - minPickerValue);
                break;
            case TYPE_TIMER:
                layoutChild_check.setVisibility(View.GONE);
                layoutChild_timer.setVisibility(View.VISIBLE);
                numberPickerGoal_timer.setValue(0 - minPickerValue);
                numberPickerInit_timer.setValue(0 - minPickerValue);
                numberPickerPer_timer.setValue(0 - minPickerValue);
                break;
        }
    }

    private void numberPickerInit(NumberPicker numberPicker){
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(maxPickerValue - minPickerValue);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int index) {
                return Integer.toString(index + minPickerValue);
            }
        });
    }
}