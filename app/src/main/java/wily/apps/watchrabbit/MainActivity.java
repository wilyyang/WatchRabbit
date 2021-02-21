package wily.apps.watchrabbit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.data.database.HabbitDatabase;
import wily.apps.watchrabbit.data.entity.Habbit;
import wily.apps.watchrabbit.fragment.HabbitFragment;
import wily.apps.watchrabbit.fragment.TodayFragment;

public class MainActivity extends AppCompatActivity {
    private TodayFragment todayFragment;
    private HabbitFragment habbitFragment;
    private BottomNavigationView bottomNavigation;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentInit();
    }

    private void fragmentInit(){
        frameLayout = findViewById(R.id.container);
        todayFragment = new TodayFragment();
        habbitFragment = new HabbitFragment();

        bottomNavigation = findViewById(R.id.bottom_menu);
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
    public void setVisibleNavigation(boolean flag){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, flag ? 24f:26f);
        frameLayout.setLayoutParams(params);
    }
}