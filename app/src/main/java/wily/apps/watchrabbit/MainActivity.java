package wily.apps.watchrabbit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

import wily.apps.watchrabbit.data.entity.Habbit;
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

    private WatchRabbitApplication.OnProcessFinsishedListener listener = null;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentInit();
        createHabbitService();



        listener = new WatchRabbitApplication.OnProcessFinsishedListener(){
            @Override
            public void onStart() {
                if(dialog == null){
                    dialog = DialogGetter.getProgressDialog(MainActivity.this, getString(R.string.base_dialog_database_inprogress));
                }
                dialog.show();
            }

            @Override
            public void onFinish() {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        };
        ((WatchRabbitApplication)getApplication()).addListner(listener);


        long sTime = DateUtil.getDateLong(2021, Calendar.APRIL, 2, 0, 0, 0);
        long cTime = System.currentTimeMillis();
        ((WatchRabbitApplication)getApplication()).addSamples(1, 30, sTime, cTime, Habbit.TYPE_HABBIT_CHECK);
        ((WatchRabbitApplication)getApplication()).addSamples(1, 30, sTime, cTime, Habbit.TYPE_HABBIT_TIMER);
        ((WatchRabbitApplication)getApplication()).updateTotal(50, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((WatchRabbitApplication)getApplication()).unRegisterListner(listener);
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
                        Intent intent = new Intent(MainActivity.this, EvaluationHabbitActivity.class);
                        startActivity(intent);
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

    public void setVisibleNavigation(boolean flag){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, flag ? 24f:26f);
        frameLayout.setLayoutParams(params);
    }
}