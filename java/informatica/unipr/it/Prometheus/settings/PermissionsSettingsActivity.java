package informatica.unipr.it.Prometheus.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.permissions.NotificationPermissionManager;
import informatica.unipr.it.Prometheus.theme.ThemesManager;

public class PermissionsSettingsActivity extends AppCompatActivity {


    private NotificationPermissionManager npm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context context = this;
        Drawable upArrow;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_permissions);
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

        getSupportActionBar().setTitle(getString(R.string.permission_settings_activity_title));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        npm = new NotificationPermissionManager(this);

        View overlay = findViewById(R.id.overDrawPermissionSettings);
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (npm.checkOverlayPermission()) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setMessage(getString(R.string.overlay_permission_concede));
                    alertDialog.setTitle(getString(R.string.granted_permission));
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.permission_granted_dialog), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                } else {
                    npm.showOverlayDialogPermission();
                }
            }
        });

        View usage = findViewById(R.id.usagePermissionSettings);
        usage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (npm.checkAppUsagePermission()) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setMessage(getString(R.string.usage_permission_concede));
                    alertDialog.setTitle(getString(R.string.granted_permission));
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.permission_granted_dialog), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                } else {
                    npm.showAppUsageDialogPermission();
                }
            }
        });

/*
        View notificationPermission = findViewById(R.id.notificationPermissionSettings);
        notificationPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (npm.checkNotificationPermissions()) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setMessage(getString(R.string.notification_permission_concede));
                    alertDialog.setTitle(getString(R.string.granted_permission));
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.permission_granted_dialog), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                } else {
                    npm.showNotificationDialogPermission();
                }
            }
        });
        */
    }

    @Override
    public void onBackPressed() {
        Intent settingActivity = new Intent(this, SettingsActivity.class);
        startActivity(settingActivity);
        finish();
    }


}
