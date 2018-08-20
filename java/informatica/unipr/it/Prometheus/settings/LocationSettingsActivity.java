package informatica.unipr.it.Prometheus.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import informatica.unipr.it.Prometheus.R;

public class LocationSettingsActivity extends AppCompatActivity {

    private SharedPreferences locationSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);
        Drawable upArrow;

        locationSharedPreferences = getSharedPreferences("Location", MODE_PRIVATE);


        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException ignored) {
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                upArrow = getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            } else {
                upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            }
        } catch (NullPointerException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                upArrow = getDrawable(R.drawable.androidhappy);
            } else {
                upArrow = getResources().getDrawable(R.drawable.androidhappy);
            }
        }

        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setTitle(getString(R.string.location_settings_activity_title));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        CheckBox checkBoxNavBar = (CheckBox) findViewById(R.id.checkBoxSilencePlace);

        if (locationSharedPreferences.getBoolean("isLocationActive", true)) {
            checkBoxNavBar.setChecked(true);
        }
        checkBoxNavBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                locationSharedPreferences.edit().putBoolean("isLocationActive", isChecked).apply();
            }
        });
    }


/*    @Override
    public void onBackPressed() {
        Intent settingActivity = new Intent(this, SettingsActivity.class);
        startActivity(settingActivity);
        finish();
    }*/
}
