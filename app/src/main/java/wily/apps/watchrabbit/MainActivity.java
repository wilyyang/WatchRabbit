package wily.apps.watchrabbit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import wily.apps.watchrabbit.fragment.HabbitFragment;
import wily.apps.watchrabbit.fragment.EvaluationFragment;
import wily.apps.watchrabbit.service.HabbitService;

public class MainActivity extends AppCompatActivity {
    private EvaluationFragment evaluationFragment;
    private HabbitFragment habbitFragment;
    private BottomNavigationView bottomNavigation;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentInit();
        createHabbitService();
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

    public void setVisibleNavigation(boolean flag){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, flag ? 24f:26f);
        frameLayout.setLayoutParams(params);
    }
}