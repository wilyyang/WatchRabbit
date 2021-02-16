package wily.apps.watchrabbit.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wily.apps.watchrabbit.HabbitModifyActivity;
import wily.apps.watchrabbit.R;
import wily.apps.watchrabbit.data.database.HabbitDatabase;

public class HabbitFragment extends Fragment {
    private TextView tempText = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_habbit, container, false);

        Button btnHabbitAdd = view.findViewById(R.id.btn_habbit_add);
        btnHabbitAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HabbitModifyActivity.class);
                startActivity(intent);
            }
        });

        tempText = view.findViewById(R.id.temp_text);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllHabbit();
    }

    private void getAllHabbit(){
        HabbitDatabase db = HabbitDatabase.getAppDatabase(getContext());
        db.habbitDao().getAll().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    tempText.setText(item.toString());
                });
    }
}
