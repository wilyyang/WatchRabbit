package wily.apps.watchrabbit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import wily.apps.watchrabbit.fragment.HabbitFragment;
import wily.apps.watchrabbit.fragment.TodayFragment;

public class MainActivity extends AppCompatActivity {
    private TodayFragment todayFragment;
    private HabbitFragment habbitFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentInit();
    }

    private void fragmentInit(){
        todayFragment = new TodayFragment();
        habbitFragment = new HabbitFragment();

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_menu);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.today_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, todayFragment).commit();
                        return true;

                    case R.id.habbit_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, habbitFragment).commit();
                        return true;
                }
                return false;
            }
        });
        bottomNavigation.setSelectedItemId(R.id.habbit_tab);
    }
}