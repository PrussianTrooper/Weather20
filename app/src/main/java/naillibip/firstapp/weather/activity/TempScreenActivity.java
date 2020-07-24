package naillibip.firstapp.weather.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import naillibip.firstapp.weather.fragments.TempScreenFragment;
import naillibip.firstapp.weather.R;

public class TempScreenActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_screen);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }

        if (savedInstanceState == null) {
            TempScreenFragment details = new TempScreenFragment();
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, details)
                    .commit();
        }
    }
}
