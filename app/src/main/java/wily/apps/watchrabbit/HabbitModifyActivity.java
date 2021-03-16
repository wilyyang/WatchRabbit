package wily.apps.watchrabbit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.service.HabbitService;

public class HabbitModifyActivity extends AppCompatActivity {

    private LinearLayout layoutChild_check = null;
    private LinearLayout layoutChild_timer = null;

    private EditText etTitleHabbit = null;
    private Switch switchHabbit = null;

    private RadioGroup radioGroupHabbit = null;
    private RadioButton radioBtn_check = null;
    private RadioButton radioBtn_timer = null;

    private NumberPicker numberPickerPrio = null;
    private NumberPicker numberPickerInit = null;
    private NumberPicker numberPickerGoal = null;

    private NumberPicker numberPickerPer_check = null;
    private NumberPicker numberPickerPer_timer = null;

    private Button btnSave = null;
    private Button btnCancel = null;

    private final int maxPickerValue = 100;
    private final int minPickerValue = -100;

    private int type = AppConst.TYPE_HABBIT_CHECK;

    private int mode = AppConst.HABBIT_MODIFY_MODE_ADD;
    private int id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habbit_modify);

        Intent intent = getIntent();
        id = intent.getExtras().getInt("id");
        mode = (intent.getExtras().getBoolean("update") ? AppConst.HABBIT_MODIFY_MODE_UPDATE : AppConst.HABBIT_MODIFY_MODE_ADD);

        initView();
    }

    private void initView(){
        etTitleHabbit = findViewById(R.id.et_title_habbit);
        switchHabbit = findViewById(R.id.switch_habbit);

        numberPickerPrio = findViewById(R.id.number_picker_habbit_prio);
        numberPickerPrio.setMinValue(1);
        numberPickerPrio.setMaxValue(9);
        numberPickerPrio.setWrapSelectorWheel(false);

        numberPickerInit = findViewById(R.id.number_picker_habbit_init);
        numberPickerInit(numberPickerInit);
        numberPickerGoal = findViewById(R.id.number_picker_habbit_goal);
        numberPickerInit(numberPickerGoal);

        numberPickerPer_check = findViewById(R.id.number_picker_check_per);
        numberPickerInit(numberPickerPer_check);
        numberPickerPer_timer = findViewById(R.id.number_picker_timer_per);
        numberPickerInit(numberPickerPer_timer);

        layoutChild_check = findViewById(R.id.include_child_habbit_modify_check);
        layoutChild_timer = findViewById(R.id.include_child_habbit_modify_timer);

        radioBtn_check = findViewById(R.id.radio_habbit_check);
        radioBtn_timer = findViewById(R.id.radio_habbit_timer);
        radioGroupHabbit = findViewById(R.id.radio_group_habbit);
        radioGroupHabbit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio_habbit_check:
                        changeViewAtType(AppConst.TYPE_HABBIT_CHECK);
                        break;
                    case R.id.radio_habbit_timer:
                        changeViewAtType(AppConst.TYPE_HABBIT_TIMER);
                        break;
                }
            }
        });
        radioBtn_check.setChecked(true);
        radioBtn_timer.setChecked(false);
        changeViewAtType(AppConst.TYPE_HABBIT_CHECK);

        btnSave = findViewById(R.id.btn_habbit_modify_save);
        btnSave.setOnClickListener(onClickListener);
        btnCancel = findViewById(R.id.btn_habbit_modify_cancel);
        btnCancel.setOnClickListener(onClickListener);

        if(mode == AppConst.HABBIT_MODIFY_MODE_UPDATE){
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

                        numberPickerPrio.setValue(habbit.getPriority());
                        numberPickerInit.setValue(habbit.getInitCost()- minPickerValue);
                        numberPickerGoal.setValue(habbit.getGoalCost()- minPickerValue);

                        int type = habbit.getType();
                        switch(type){
                            case AppConst.TYPE_HABBIT_CHECK:
                                radioBtn_check.setChecked(true);
                                numberPickerPer_check.setValue(habbit.getPerCost()- minPickerValue);
                                break;

                            case AppConst.TYPE_HABBIT_TIMER:
                                radioBtn_timer.setChecked(true);
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
                    if(mode == AppConst.HABBIT_MODIFY_MODE_ADD){
                        addHabbit();
                    }else if(mode == AppConst.HABBIT_MODIFY_MODE_UPDATE){
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

        int priority  = numberPickerPrio.getValue();
        int goalCost  = numberPickerGoal.getValue()+ minPickerValue;
        int initCost  = numberPickerInit.getValue()+ minPickerValue;
        int perCost = 0;

        switch (type){
            case AppConst.TYPE_HABBIT_CHECK:
                perCost = numberPickerPer_check.getValue()+ minPickerValue;
                break;
            case AppConst.TYPE_HABBIT_TIMER:
                perCost = numberPickerPer_timer.getValue()+ minPickerValue;
                break;
        }

        HabbitDatabase db = HabbitDatabase.getAppDatabase(HabbitModifyActivity.this);
        db.habbitDao().updateHabbit(id, type, priority, title, active, goalCost, initCost, perCost).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    long habbitId = id;
                    Intent intent = new Intent(getApplicationContext(), HabbitService.class);
                    intent.setAction(HabbitService.HABBIT_SERVICE_UPDATE);
                    intent.putExtra(AppConst.INTENT_SERVICE_HABBIT_ID, (int)habbitId);
                    intent.putExtra(AppConst.INTENT_SERVICE_TITLE, title);
                    intent.putExtra(AppConst.INTENT_SERVICE_TYPE, type);
                    intent.putExtra(AppConst.INTENT_SERVICE_PRIORITY, priority);
                    intent.putExtra(AppConst.INTENT_SERVICE_ACTIVE, active);
                    startService(intent);
                    finish();
                });
    }

    private void addHabbit(){
        int type = this.type;
        String temp = etTitleHabbit.getText().toString();
        String title = (!temp.equals("") ? etTitleHabbit.getText().toString() : "Unknown");
        boolean active = switchHabbit.isChecked();

        int priority  = numberPickerPrio.getValue();
        int goalCost  = numberPickerGoal.getValue()+ minPickerValue;
        int initCost  = numberPickerInit.getValue()+ minPickerValue;
        int perCost = 0;

        switch (type){
            case AppConst.TYPE_HABBIT_CHECK:
                perCost = numberPickerPer_check.getValue()+ minPickerValue;
                break;
            case AppConst.TYPE_HABBIT_TIMER:
                perCost = numberPickerPer_timer.getValue()+ minPickerValue;
                break;
        }
        long currentTime = System.currentTimeMillis();

        HabbitDatabase db = HabbitDatabase.getAppDatabase(HabbitModifyActivity.this);
        db.habbitDao().insert(new Habbit(type, currentTime, priority, title, active, goalCost, initCost, perCost)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    if(active == true){
                        long habbitId = item;
                        Intent intent = new Intent(getApplicationContext(), HabbitService.class);
                        intent.setAction(HabbitService.HABBIT_SERVICE_ADD);
                        intent.putExtra(AppConst.INTENT_SERVICE_HABBIT_ID, (int)habbitId);
                        intent.putExtra(AppConst.INTENT_SERVICE_TITLE, title);
                        intent.putExtra(AppConst.INTENT_SERVICE_TYPE, type);
                        intent.putExtra(AppConst.INTENT_SERVICE_PRIORITY, priority);
                        startService(intent);
                    }

                    finish();
                });
    }

    private void changeViewAtType(int type){
        switch (type){
            case AppConst.TYPE_HABBIT_CHECK:
                this.type = AppConst.TYPE_HABBIT_CHECK;
                layoutChild_check.setVisibility(View.VISIBLE);
                layoutChild_timer.setVisibility(View.GONE);
                numberPickerPer_check.setValue(0 - minPickerValue);
                break;
            case AppConst.TYPE_HABBIT_TIMER:
                this.type = AppConst.TYPE_HABBIT_TIMER;
                layoutChild_check.setVisibility(View.GONE);
                layoutChild_timer.setVisibility(View.VISIBLE);
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
        numberPicker.setValue((maxPickerValue - minPickerValue)/2);
    }
}