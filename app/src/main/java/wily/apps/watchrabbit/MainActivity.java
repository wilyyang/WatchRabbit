package wily.apps.watchrabbit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;

import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.data.dao.HabbitDao;
import wily.apps.watchrabbit.data.dao.RecordDao;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.database.RecordDatabase;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.data.entity.Record;
import wily.apps.watchrabbit.fragment.HabbitFragment;
import wily.apps.watchrabbit.fragment.EvaluationFragment;
import wily.apps.watchrabbit.service.HabbitService;
import wily.apps.watchrabbit.util.DateUtil;
import wily.apps.watchrabbit.util.DialogGetter;

public class MainActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigation;

    private EvaluationFragment evaluationFragment;
    private HabbitFragment habbitFragment;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentInit();
        createHabbitService();

        dialog = DialogGetter.getProgressDialog(MainActivity.this, getString(R.string.base_dialog_database_inprogress));
        long sTime = DateUtil.getDateLong(2021, Calendar.APRIL, 4, 0, 0, 0);
        long cTime = System.currentTimeMillis();


        if(init == false){
            init = true;
        }else{
            return;
        }

        addSamples(2, 10, sTime, cTime, Habbit.TYPE_HABBIT_CHECK);
        addSamples(2, 10, sTime, cTime, Habbit.TYPE_HABBIT_TIMER);
    }
    boolean init = false;

    @Override
    protected void onResume() {
        super.onResume();
        evaluateHabbitService();
    }

    private void fragmentInit(){
        frameLayout = findViewById(R.id.container_main);
        evaluationFragment = new EvaluationFragment();
        habbitFragment = new HabbitFragment();

        bottomNavigation = findViewById(R.id.bottom_menu_main);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.today_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_main, evaluationFragment).commit();
                        return true;

                    case R.id.habbit_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_main, habbitFragment).commit();
                        return true;
                }
                return false;
            }
        });
        bottomNavigation.setSelectedItemId(R.id.today_tab);
    }

    private void createHabbitService() {
        Intent intent = new Intent(MainActivity.this, HabbitService.class);
        intent.setAction(HabbitService.HABBIT_SERVICE_CREATE);
        startService(intent);
    }

    private void evaluateHabbitService() {
        Intent intent = new Intent(getApplicationContext(), HabbitService.class);
        intent.setAction(HabbitService.HABBIT_SERVICE_EVALUATE);
        startService(intent);
    }

    public void setVisibleNavigation(boolean flag){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, flag ? 24f:26f);
        frameLayout.setLayoutParams(params);
    }

    // TEMPO
    public void addSamples(int habbitNum, final int recordNum, final long start, long end, int type){
        // 1) init
        HabbitDao habbitDao = HabbitDatabase.getAppDatabase(MainActivity.this).habbitDao();
        RecordDao recordDao = RecordDatabase.getAppDatabase(MainActivity.this).recordDao();
        int count = 0;

        String typeStr = (type==Habbit.TYPE_HABBIT_CHECK)?"체크":"타임";
        int per = (type==Habbit.TYPE_HABBIT_CHECK)?10:1;
        int state= (type==Habbit.TYPE_HABBIT_CHECK)?Habbit.STATE_CHECK:Habbit.STATE_TIMER_WAIT;
        long totalTerm  = end - start;
        ArrayList<Habbit> habbits = new ArrayList<Habbit>();

        // 2) habbit sample
        for(int i = 0; i< habbitNum; ++i){
            habbits.add(new Habbit(type, start, typeStr+" "+count, 1, true, 100, 0, per, state, -1));
            ++count;
        }

        // ** Dialog
        dialog.show();

        // 3) insert habbits
        habbitDao.insertAllSingle(habbits).subscribeOn(Schedulers.io()).doOnSuccess(idList->{

            for(long hhid : idList){
                // 3.1) Record sample
                ArrayList<Record> records = new ArrayList<Record>();
                long divTerm = totalTerm/recordNum;
                for(int j = 0; j< recordNum; ++j){
                    records.add(new Record((int)hhid, type,start+(divTerm*j), 10 * DateUtil.MILLISECOND_TO_MINUTE));
                }
                // 3.2) insert records
                recordDao.insertAll(records);
            }
        }).subscribe(res -> afterAddSample());
    }

    private void afterAddSample(){

        dialog.dismiss();
    }
}