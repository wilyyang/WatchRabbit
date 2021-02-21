package wily.apps.watchrabbit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
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
import wily.apps.watchrabbit.adapter.HabbitAdapter;
import wily.apps.watchrabbit.data.DataConst;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.util.DialogGetter;

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

    private int type = DataConst.TYPE_HABBIT_CHECK;

    private int mode = DataConst.MODE_MODIFY_ADD;
    private int id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habbit_modify);

        Intent intent = getIntent();
        id = intent.getExtras().getInt("id");
        mode = (intent.getExtras().getBoolean("update") ? DataConst.MODE_MODIFY_UPDATE : DataConst.MODE_MODIFY_ADD);

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
                        changeViewAtType(DataConst.TYPE_HABBIT_CHECK);
                        break;
                    case R.id.radio_habbit_timer:
                        changeViewAtType(DataConst.TYPE_HABBIT_TIMER);
                        break;
                }
            }
        });
        radioBtn_check.setChecked(true);
        radioBtn_timer.setChecked(false);
        changeViewAtType(DataConst.TYPE_HABBIT_CHECK);

        btnSave = findViewById(R.id.btn_habbit_modify_save);
        btnSave.setOnClickListener(onClickListener);
        btnCancel = findViewById(R.id.btn_habbit_modify_cancel);
        btnCancel.setOnClickListener(onClickListener);

        if(mode == DataConst.MODE_MODIFY_UPDATE){
            setUIData(id);
        }
    }

    @SuppressLint("ResourceType")
    private void setUIData(int id){
        btnSave.setText("업데이트");
        HabbitDatabase db = HabbitDatabase.getAppDatabase(this);
        db.habbitDao().getHabbit(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    if(!item.isEmpty()){
                        Habbit habbit = item.get(0);

                        etTitleHabbit.setText(habbit.getTitle());
                        switchHabbit.setChecked(habbit.isActive());

                        int type = habbit.getType();
                        switch(type){
                            case DataConst.TYPE_HABBIT_CHECK:
                                radioBtn_check.setChecked(true);
                                numberPickerGoal_check.setValue(habbit.getGoalCost()- minPickerValue);
                                numberPickerInit_check.setValue(habbit.getInitCost()- minPickerValue);
                                numberPickerPer_check.setValue(habbit.getPerCost()- minPickerValue);
                                break;

                            case DataConst.TYPE_HABBIT_TIMER:
                                radioBtn_timer.setChecked(true);
                                numberPickerGoal_timer.setValue(habbit.getGoalCost()- minPickerValue);
                                numberPickerInit_timer.setValue(habbit.getInitCost()- minPickerValue);
                                numberPickerPer_timer.setValue(habbit.getPerCost()- minPickerValue);
                                break;
                        }
                        radioBtn_check.setEnabled(false);
                        radioBtn_timer.setEnabled(false);
                    }
                });
    }

    private Button.OnClickListener onClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.btn_habbit_modify_save:
                    if(mode == DataConst.MODE_MODIFY_ADD){
                        addHabbit();
                    }else if(mode == DataConst.MODE_MODIFY_UPDATE){
                        updateHabbit(id);
                    }
                    break;
                case R.id.btn_habbit_modify_cancel:
                    finish();
                    break;
            }
        }
    };

    private void updateHabbit(int id){
        int type = this.type;
        String title = (!etTitleHabbit.equals("") ? etTitleHabbit.getText().toString() : "Unknown");
        boolean active = switchHabbit.isChecked();

        int goalCost = 0;
        int initCost = 0;
        int perCost = 0;

        switch (type){
            case DataConst.TYPE_HABBIT_CHECK:
                goalCost = numberPickerGoal_check.getValue()+ minPickerValue;
                initCost = numberPickerInit_check.getValue()+ minPickerValue;
                perCost = numberPickerPer_check.getValue()+ minPickerValue;
                break;
            case DataConst.TYPE_HABBIT_TIMER:
                goalCost = numberPickerGoal_timer.getValue()+ minPickerValue;
                initCost = numberPickerInit_timer.getValue()+ minPickerValue;
                perCost = numberPickerPer_timer.getValue()+ minPickerValue;
                break;
        }

        HabbitDatabase db = HabbitDatabase.getAppDatabase(HabbitModifyActivity.this);
        db.habbitDao().updateHabbit(id, type, title, active, goalCost, initCost, perCost).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    finish();
                });
    }

    private void addHabbit(){
        int type = this.type;
        String title = (!etTitleHabbit.equals("") ? etTitleHabbit.getText().toString() : "Unknown");
        boolean active = switchHabbit.isChecked();

        int goalCost = 0;
        int initCost = 0;
        int perCost = 0;

        switch (type){
            case DataConst.TYPE_HABBIT_CHECK:
                goalCost = numberPickerGoal_check.getValue()+ minPickerValue;
                initCost = numberPickerInit_check.getValue()+ minPickerValue;
                perCost = numberPickerPer_check.getValue()+ minPickerValue;
                break;
            case DataConst.TYPE_HABBIT_TIMER:
                goalCost = numberPickerGoal_timer.getValue()+ minPickerValue;
                initCost = numberPickerInit_timer.getValue()+ minPickerValue;
                perCost = numberPickerPer_timer.getValue()+ minPickerValue;
                break;
        }

        HabbitDatabase db = HabbitDatabase.getAppDatabase(HabbitModifyActivity.this);
        db.habbitDao().insert(new Habbit(type, title, active, goalCost, initCost, perCost)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                            finish();
                        });
    }

    private void changeViewAtType(int type){
        switch (type){
            case DataConst.TYPE_HABBIT_CHECK:
                this.type = DataConst.TYPE_HABBIT_CHECK;
                layoutChild_check.setVisibility(View.VISIBLE);
                layoutChild_timer.setVisibility(View.GONE);
                numberPickerGoal_check.setValue(0 - minPickerValue);
                numberPickerInit_check.setValue(0 - minPickerValue);
                numberPickerPer_check.setValue(0 - minPickerValue);
                break;
            case DataConst.TYPE_HABBIT_TIMER:
                this.type = DataConst.TYPE_HABBIT_TIMER;
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