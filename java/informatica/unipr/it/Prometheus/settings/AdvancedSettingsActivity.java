package informatica.unipr.it.Prometheus.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.receiver.AlarmServicesManager;
import informatica.unipr.it.Prometheus.service.AppUsageService;
import informatica.unipr.it.Prometheus.service.LocationService;
import informatica.unipr.it.Prometheus.theme.ThemesManager;

public class AdvancedSettingsActivity extends AppCompatActivity {

    private ThemesManager themesManager;
    private int dxValue;
    private int sxValue;

    private SharedPreferences avatarSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context context = this;
        themesManager = new ThemesManager(this);
        Drawable upArrow;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_advanced);
        avatarSharedPreferences = getSharedPreferences("Avatar", MODE_PRIVATE);
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

        if (upArrow != null) {
            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setTitle(R.string.advanced_settings_activity_title);

        RelativeLayout appUsageServiceLayout = (RelativeLayout) findViewById(R.id.appUsageServiceLayout);
        if (appUsageServiceLayout != null) {
            appUsageServiceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences setting = getApplication().getSharedPreferences("setting", MODE_PRIVATE);
                    Intent appUsageService = new Intent(getApplication().getApplicationContext(), AppUsageService.class);
                    getApplication().stopService(appUsageService);
                    setting.edit().putBoolean("AppUsageEnabled", false).apply();
                    getApplication().startService(appUsageService);
                    setting.edit().putBoolean("AppUsageEnabled", true).apply();

                    Intent locationService = new Intent(getApplication().getApplicationContext(), LocationService.class);
                    getApplication().stopService(locationService);
                    getApplication().startService(locationService);

                    int intervalTimeForNextSend = 1000 * 60 * 20;
                    Intent alarmServicesManager = new Intent(getApplication(), AlarmServicesManager.class);
                    PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmServicesManager, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager am = (AlarmManager)getApplication().getSystemService(Context.ALARM_SERVICE);
                    am.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), intervalTimeForNextSend , sender);

                    Toast.makeText(getApplication(), getString(R.string.app_service_restarted), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }


    }

    @Override
    public void onBackPressed() {
        Intent settingActivity = new Intent(this, SettingsActivity.class);
        startActivity(settingActivity);
        finish();
    }

}
