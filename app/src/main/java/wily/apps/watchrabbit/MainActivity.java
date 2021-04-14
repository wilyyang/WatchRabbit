package wily.apps.watchrabbit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import wily.apps.watchrabbit.fragment.HabbitFragment;
import wily.apps.watchrabbit.fragment.EvaluationFragment;

public class MainActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private BottomNavigationView bottomNavigation;

    private EvaluationFragment evaluationFragment;
    private HabbitFragment habbitFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentInit();
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

    public void setVisibleNavigation(boolean flag){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, flag ? 24f:26f);
        frameLayout.setLayoutParams(params);
    }
}