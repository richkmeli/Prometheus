package informatica.unipr.it.Prometheus.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import informatica.unipr.it.Prometheus.MainActivity;
import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.theme.ThemesManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context context = this;
        ThemesManager themesManager = new ThemesManager(this);
        Drawable upArrow;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);
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

        getSupportActionBar().setTitle(getString(R.string.setting_title));


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        View generalSettings = findViewById(R.id.general_settings);
        generalSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent generalSet = new Intent(context, GeneralSettingsActivity.class);
                startActivity(generalSet);
                finish();
            }
        });

        View themeSettings = findViewById(R.id.theme_settings);
        themeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent themeSet = new Intent(context, ThemeSettingsActivity.class);
                startActivity(themeSet);
                finish();
            }
        });

        View permissionsSettings = findViewById(R.id.permissions);
        permissionsSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent permissionsSet = new Intent(context, PermissionsSettingsActivity.class);
                startActivity(permissionsSet);
                finish();
            }
        });

        View locationSettings = findViewById(R.id.location_settings);
        locationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locSet = new Intent(context, LocationSettingsActivity.class);
                startActivity(locSet);
                finish();
            }
        });

        View advancedSettings = findViewById(R.id.advanced_settings);
        advancedSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent advancedSet = new Intent(context, AdvancedSettingsActivity.class);
                startActivity(advancedSet);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }


    String setTime(int time) {
        String timeString = "";
        if (((time) / 60) != 0) {
            if ((((time) / 60) / 60) == 1)
                timeString = timeString + "1 " + getResources().getString(R.string.hour);
            else if ((((time) / 60) / 60) > 1)
                timeString = timeString + (((time) / 60) / 60) + " " + getResources().getString(R.string.hours);
            if (((time) / 60 % 60) != 0) {
                if (time / 60 / 60 >= 1)
                    timeString = timeString + " " + getResources().getString(R.string.and);

            }
            if ((((time) / 60) % 60) == 1)
                timeString = timeString + " 1 " + getResources().getString(R.string.minute);
            else if ((((time) / 60) % 60) > 1)
                timeString = timeString + " " + (((time) / 60) % 60) + " " + getResources().getString(R.string.minutes);
            if ((((time) % 60) != 0)) {
                if (time % 60 == 1) {
                    timeString = timeString + " " + getResources().getString(R.string.and) + " " + 1 + " " + getResources().getString(R.string.second);
                } else
                    timeString = timeString + " " + getResources().getString(R.string.and) + " " + (time % 60) + " " + getResources().getString(R.string.seconds);
            }

        } else {
            if ((time) != 0) {
                if (time % 60 == 1) {
                    timeString = time + " " + getResources().getString(R.string.second);
                } else
                    timeString = time + " " + getResources().getString(R.string.seconds);
            } else {
                timeString = " " + getResources().getString(R.string.no_pause);
            }
        }
        return timeString;
    }


}
