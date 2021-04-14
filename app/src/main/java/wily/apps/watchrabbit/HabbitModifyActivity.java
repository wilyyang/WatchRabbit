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

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.service.HabbitService;
import wily.apps.watchrabbit.util.Utils;

public class HabbitModifyActivity extends AppCompatActivity {
    private EditText etTitleHabbit = null;
    private Switch switchHabbit = null;

    private RadioGroup radioGroupHabbit = null;
    private RadioButton radioBtn_check = null;
    private RadioButton radioBtn_timer = null;

    private NumberPicker numberPickerPrio = null;
    private NumberPicker numberPickerInit = null;
    private NumberPicker numberPickerGoal = null;

    private LinearLayout layoutChild_check = null;
    private LinearLayout layoutChild_timer = null;
    private NumberPicker numberPickerPer_check = null;
    private NumberPicker numberPickerPer_timer = null;

    private Button btnSave = null;
    private Button btnCancel = null;

    private int type = Habbit.TYPE_HABBIT_CHECK;
    private int mode = AppConst.HABBIT_MODIFY_MODE_ADD;
    private int id = -1;

    private final int maxPickerValue = 100;
    private final int minPickerValue = -100;

    // UI
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

        radioBtn_check = findViewById(R.id.radio_habbit_check);
        radioBtn_timer = findViewById(R.id.radio_habbit_timer);
        radioGroupHabbit = findViewById(R.id.radio_group_habbit);
        radioGroupHabbit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio_habbit_check:
                        changeViewAtType(Habbit.TYPE_HABBIT_CHECK);
                        break;
                    case R.id.radio_habbit_timer:
                        changeViewAtType(Habbit.TYPE_HABBIT_TIMER);
                        break;
                }
            }
        });

        numberPickerPrio = findViewById(R.id.number_picker_habbit_prio);
        numberPickerPrio.setMinValue(1);
        numberPickerPrio.setMaxValue(9);
        numberPickerPrio.setWrapSelectorWheel(false);

        numberPickerInit = findViewById(R.id.number_picker_habbit_init);
        numberPickerInit(numberPickerInit);
        numberPickerGoal = findViewById(R.id.number_picker_habbit_goal);
        numberPickerInit(numberPickerGoal);

        layoutChild_check = findViewById(R.id.include_child_habbit_modify_check);
        layoutChild_timer = findViewById(R.id.include_child_habbit_modify_timer);

        numberPickerPer_check = findViewById(R.id.number_picker_check_per);
        numberPickerInit(numberPickerPer_check);
        numberPickerPer_timer = findViewById(R.id.number_picker_timer_per);
        numberPickerInit(numberPickerPer_timer);

        btnSave = findViewById(R.id.btn_habbit_modify_save);
        btnSave.setOnClickListener(onClickListener);
        btnCancel = findViewById(R.id.btn_habbit_modify_cancel);
        btnCancel.setOnClickListener(onClickListener);

        radioBtn_check.setChecked(true);
        radioBtn_timer.setChecked(false);
        if(mode == AppConst.HABBIT_MODIFY_MODE_UPDATE){
            setUIData(id);
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

    private void changeViewAtType(int type){
        switch (type){
            case Habbit.TYPE_HABBIT_CHECK:
                this.type = Habbit.TYPE_HABBIT_CHECK;
                layoutChild_check.setVisibility(View.VISIBLE);
                layoutChild_timer.setVisibility(View.GONE);
                numberPickerPer_check.setValue(0 - minPickerValue);
                break;
            case Habbit.TYPE_HABBIT_TIMER:
                this.type = Habbit.TYPE_HABBIT_TIMER;
                layoutChild_check.setVisibility(View.GONE);
                layoutChild_timer.setVisibility(View.VISIBLE);
                numberPickerPer_timer.setValue(0 - minPickerValue);
                break;
        }
    }

    @SuppressLint("ResourceType")
    private void setUIData(int id){
        btnSave.setText(getString(R.string.base_btn_update));
        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(this);
        habbitDB.habbitDao().getHabbitSingle(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> afterGetHabbit(item));
    }

    private void afterGetHabbit(List<Habbit> item){
        if(!item.isEmpty()){
            Habbit habbit = item.get(0);

            etTitleHabbit.setText(habbit.getTitle());
            switchHabbit.setChecked(habbit.isActive());

            numberPickerPrio.setValue(habbit.getPriority());
            numberPickerInit.setValue(habbit.getInitCost()- minPickerValue);
            numberPickerGoal.setValue(habbit.getGoalCost()- minPickerValue);

            int type = habbit.getType();
            switch(type){
                case Habbit.TYPE_HABBIT_CHECK:
                    radioBtn_check.setChecked(true);
                    numberPickerPer_check.setValue(habbit.getPerCost()- minPickerValue);
                    break;

                case Habbit.TYPE_HABBIT_TIMER:
                    radioBtn_timer.setChecked(true);
                    numberPickerPer_timer.setValue(habbit.getPerCost()- minPickerValue);
                    break;
            }
            radioBtn_check.setEnabled(false);
            radioBtn_timer.setEnabled(false);
        }
    }

    // Listener
    private Button.OnClickListener onClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.btn_habbit_modify_save:
                    addOrUpdateHabbit();
                    break;
                case R.id.btn_habbit_modify_cancel:
                    finish();
                    break;
            }
        }
    };

    private void addOrUpdateHabbit(){

        // 1) attribute set
        int type = this.type;
        String title = (!etTitleHabbit.getText().toString().equals("") ? etTitleHabbit.getText().toString() : "Unknown");
        boolean active = switchHabbit.isChecked();

        int priority  = numberPickerPrio.getValue();
        int goalCost  = numberPickerGoal.getValue()+ minPickerValue;
        int initCost  = numberPickerInit.getValue()+ minPickerValue;
        int perCost = 0;

        switch (type){
            case Habbit.TYPE_HABBIT_CHECK:
                perCost = numberPickerPer_check.getValue()+ minPickerValue;
                break;
            case Habbit.TYPE_HABBIT_TIMER:
                perCost = numberPickerPer_timer.getValue()+ minPickerValue;
                break;
        }
        final int finalPerCost = perCost;

        // 2) process DB
        HabbitDatabase habbitDB = HabbitDatabase.getAppDatabase(HabbitModifyActivity.this);
        Single.create(subscriber -> {
            if (mode == AppConst.HABBIT_MODIFY_MODE_UPDATE) {
                habbitDB.habbitDao().updateHabbit(id, type, title, priority, active, goalCost, initCost, finalPerCost);

                EvaluateWork work = new EvaluateWork(HabbitModifyActivity.this);
                work.work(EvaluateWork.WORK_TYPE_REPLACE_HABBIT, id, -1);

            }else if(mode == AppConst.HABBIT_MODIFY_MODE_ADD){
                long currentTime = System.currentTimeMillis();
                int state = (type == Habbit.TYPE_HABBIT_CHECK) ? Habbit.STATE_CHECK : Habbit.STATE_TIMER_WAIT;
                id = (int) habbitDB.habbitDao().insert(new Habbit(type, currentTime, title, priority, active, goalCost, initCost, finalPerCost, state, -1));
            }

            List<Habbit> habbits = habbitDB.habbitDao().getHabbit(id);
            if(!habbits.isEmpty()){
                subscriber.onSuccess(habbits.get(0));
            }else{
                subscriber.onError(new Throwable());
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(habbit -> afterUpdateHabbit((Habbit) habbit));
    }

    private void afterUpdateHabbit(Habbit habbit){
        if(Utils.isServiceRunning(HabbitModifyActivity.this, HabbitService.class.getName())) {
            Intent intent = new Intent(HabbitModifyActivity.this, HabbitService.class);
            intent.putExtra(AppConst.INTENT_SERVICE_HABBIT, habbit);

            if(mode == AppConst.HABBIT_MODIFY_MODE_ADD){
                intent.setAction(HabbitService.HABBIT_SERVICE_ADD);
            }else if(mode == AppConst.HABBIT_MODIFY_MODE_UPDATE){
                intent.setAction(HabbitService.HABBIT_SERVICE_UPDATE);
            }
            startService(intent);
        }
        finish();
    }
}