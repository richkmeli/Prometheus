package informatica.unipr.it.Prometheus.settings;

import android.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.theme.ThemesManager;
import pl.droidsonroids.gif.GifImageView;

public class GeneralSettingsActivity extends AppCompatActivity {

    private ThemesManager themesManager;
    private int dxValue;
    private int sxValue;

    private SharedPreferences settingSharedPreferences;

    private SharedPreferences avatarSharedPreferences;
    private SharedPreferences maxUsageSP;

    private int maxUsage;
    private TextView maxUsageTimeText;

    private int lowPriorityTime;
    private TextView lowPriorityText;

    private int midPriorityTime;
    private TextView midPriorityText;

    private int highPriorityTime;
    private TextView highPriorityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context context = this;
        themesManager = new ThemesManager(this);
        Drawable upArrow;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_general);
        avatarSharedPreferences = getSharedPreferences("Avatar", MODE_PRIVATE);
        settingSharedPreferences = getSharedPreferences("Location", MODE_PRIVATE);
        maxUsageSP = getSharedPreferences("MaxUsage", Context.MODE_PRIVATE);
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

        getSupportActionBar().setTitle(getString(R.string.general_settings_activity_title));


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        maxUsage = maxUsageSP.getInt("maxUsage", 7200);

        maxUsageTimeText = (TextView) findViewById(R.id.maxUsageTime);
        maxUsageTimeText.setText(setTime(maxUsage));

        final View maxUsageSettings = (View) findViewById(R.id.maxUsageSettings);
        maxUsageSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                maxUsage = maxUsageSP.getInt("maxUsage", 7200);


                View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.time_per_notify, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                TextView textDX = (TextView) view.findViewById(R.id.pickerTextDX);
                TextView textSX = (TextView) view.findViewById(R.id.pickerTextSX);

                textDX.setText(getResources().getString(R.string.minutes_caps));
                textSX.setText(getResources().getString(R.string.hours_caps));

                NumberPicker forTimeDX = (NumberPicker) view.findViewById(R.id.pickerDX);
                NumberPicker forTimeSX = (NumberPicker) view.findViewById(R.id.pickerSX);
                themesManager.setNumberPickerColor(forTimeSX, Color.GRAY);
                themesManager.setNumberPickerColor(forTimeDX, Color.GRAY);

                alertDialog.setCanceledOnTouchOutside(false);

                TextView confirm = (TextView) view.findViewById(R.id.timeNotifyConfirm);
                TextView cancel = (TextView) view.findViewById(R.id.cancelNotifyTime);

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        maxUsageSP.edit().putInt("maxUsage", maxUsage).apply();
                        maxUsageTimeText.setText(setTime(maxUsage));
                        alertDialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                dxValue = ((maxUsage / 60) % 60);
                forTimeDX.setMinValue(0);
                forTimeDX.setMaxValue(59);
                forTimeDX.setValue(dxValue);


                sxValue = ((maxUsage / 60) / 60);
                forTimeSX.setMinValue(1);
                forTimeSX.setMaxValue(12);
                forTimeSX.setValue(sxValue);


                forTimeDX.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        dxValue = newVal;
                        maxUsage = (sxValue * 60 + dxValue) * 60;
                    }
                });

                forTimeSX.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        sxValue = newVal;
                        maxUsage = (sxValue * 60 + dxValue) * 60;
                    }
                });

                alertDialog.setView(view);
                alertDialog.show();
            }
        });


        lowPriorityTime = maxUsageSP.getInt("lowPriority", 300);

        lowPriorityText = (TextView) findViewById(R.id.lowPrioritySettingTime);
        lowPriorityText.setText(setTime(lowPriorityTime));

        final View lowPriorityTimeView = (View) findViewById(R.id.lowPriorityTime);
        lowPriorityTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lowPriorityTime = maxUsageSP.getInt("lowPriority", 300);

                View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.time_per_notify, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                TextView textDX = (TextView) view.findViewById(R.id.pickerTextDX);
                TextView textSX = (TextView) view.findViewById(R.id.pickerTextSX);

                textDX.setText(getResources().getString(R.string.seconds_caps));
                textSX.setText(getResources().getString(R.string.minutes_caps));

                NumberPicker forTimeDX = (NumberPicker) view.findViewById(R.id.pickerDX);
                NumberPicker forTimeSX = (NumberPicker) view.findViewById(R.id.pickerSX);
                themesManager.setNumberPickerColor(forTimeSX, Color.GRAY);
                themesManager.setNumberPickerColor(forTimeDX, Color.GRAY);

                dxValue = (lowPriorityTime % 60);
                forTimeDX.setMinValue(0);
                forTimeDX.setMaxValue(59);
                forTimeDX.setValue(dxValue);


                sxValue = (lowPriorityTime / 60);
                forTimeSX.setMinValue(0);
                forTimeSX.setMaxValue(15);
                forTimeSX.setValue(sxValue);


                forTimeDX.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        dxValue = newVal;
                        lowPriorityTime = (sxValue * 60 + dxValue);
                    }
                });

                forTimeSX.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        sxValue = newVal;
                        lowPriorityTime = (sxValue * 60 + dxValue);
                    }
                });

                alertDialog.setCanceledOnTouchOutside(false);

                TextView confirm = (TextView) view.findViewById(R.id.timeNotifyConfirm);
                TextView cancel = (TextView) view.findViewById(R.id.cancelNotifyTime);

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        maxUsageSP.edit().putInt("lowPriority", lowPriorityTime).apply();
                        lowPriorityText.setText(setTime(lowPriorityTime));
                        alertDialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });


                alertDialog.setView(view);
                alertDialog.show();
            }
        });


        midPriorityTime = maxUsageSP.getInt("midPriority", 150);

        midPriorityText = (TextView) findViewById(R.id.midPrioritySettingTime);
        midPriorityText.setText(setTime(midPriorityTime));

        final View midPriorityTimeView = (View) findViewById(R.id.midPriorityTime);
        midPriorityTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                midPriorityTime = maxUsageSP.getInt("midPriority", 150);

                View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.time_per_notify, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                TextView textDX = (TextView) view.findViewById(R.id.pickerTextDX);
                TextView textSX = (TextView) view.findViewById(R.id.pickerTextSX);

                textDX.setText(getResources().getString(R.string.seconds_caps));
                textSX.setText(getResources().getString(R.string.minutes_caps));

                NumberPicker forTimeDX = (NumberPicker) view.findViewById(R.id.pickerDX);
                NumberPicker forTimeSX = (NumberPicker) view.findViewById(R.id.pickerSX);
                themesManager.setNumberPickerColor(forTimeSX, Color.GRAY);
                themesManager.setNumberPickerColor(forTimeDX, Color.GRAY);

                alertDialog.setCanceledOnTouchOutside(false);

                TextView confirm = (TextView) view.findViewById(R.id.timeNotifyConfirm);
                TextView cancel = (TextView) view.findViewById(R.id.cancelNotifyTime);

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        maxUsageSP.edit().putInt("midPriority", midPriorityTime).apply();
                        midPriorityText.setText(setTime(midPriorityTime));
                        alertDialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                dxValue = (midPriorityTime % 60);
                forTimeDX.setMinValue(0);
                forTimeDX.setMaxValue(59);
                forTimeDX.setValue(dxValue);


                sxValue = (midPriorityTime / 60);
                forTimeSX.setMinValue(0);
                forTimeSX.setMaxValue(10);
                forTimeSX.setValue(sxValue);


                forTimeDX.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        dxValue = newVal;
                        midPriorityTime = (sxValue * 60 + dxValue);
                    }
                });

                forTimeSX.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        sxValue = newVal;
                        midPriorityTime = (sxValue * 60 + dxValue);
                    }
                });

                alertDialog.setView(view);
                alertDialog.show();
            }
        });


        highPriorityTime = maxUsageSP.getInt("highPriority", 75);

        highPriorityText = (TextView) findViewById(R.id.highPrioritySettingTime);
        highPriorityText.setText(setTime(highPriorityTime));

        final View highPriorityTimeView = (View) findViewById(R.id.highPriorityTime);
        highPriorityTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highPriorityTime = maxUsageSP.getInt("highPriority", 75);


                View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.time_per_notify, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                TextView textDX = (TextView) view.findViewById(R.id.pickerTextDX);
                TextView textSX = (TextView) view.findViewById(R.id.pickerTextSX);

                textDX.setText(getResources().getString(R.string.seconds_caps));
                textSX.setText(getResources().getString(R.string.minutes_caps));

                NumberPicker forTimeDX = (NumberPicker) view.findViewById(R.id.pickerDX);
                NumberPicker forTimeSX = (NumberPicker) view.findViewById(R.id.pickerSX);
                themesManager.setNumberPickerColor(forTimeSX, Color.GRAY);
                themesManager.setNumberPickerColor(forTimeDX, Color.GRAY);

                alertDialog.setCanceledOnTouchOutside(false);

                TextView confirm = (TextView) view.findViewById(R.id.timeNotifyConfirm);
                TextView cancel = (TextView) view.findViewById(R.id.cancelNotifyTime);

                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        maxUsageSP.edit().putInt("highPriority", highPriorityTime).apply();
                        highPriorityText.setText(setTime(highPriorityTime));
                        alertDialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                dxValue = (highPriorityTime % 60);
                forTimeDX.setMinValue(0);
                forTimeDX.setMaxValue(59);
                forTimeDX.setValue(dxValue);


                sxValue = (highPriorityTime / 60);
                forTimeSX.setMinValue(0);
                forTimeSX.setMaxValue(5);
                forTimeSX.setValue(sxValue);


                forTimeDX.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        dxValue = newVal;
                        highPriorityTime = (sxValue * 60 + dxValue);
                    }
                });

                forTimeSX.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        sxValue = newVal;
                        highPriorityTime = (sxValue * 60 + dxValue);
                    }
                });

                alertDialog.setView(view);
                alertDialog.show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent settingActivity = new Intent(this, SettingsActivity.class);
        startActivity(settingActivity);
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


    public void setTextAndImage(GifImageView avatarSettings, TextView textTheme) {
        String avatar = avatarSharedPreferences.getString("avatarImage", "prom");
        avatarSettings.setImageResource(getResources().getIdentifier(avatar + "happy", "drawable", getPackageName()));

        if (avatar.equals("prom"))
            textTheme.setText("Prometheus");
        else
            textTheme.setText(avatar.replace(avatar.charAt(0), (char) (avatar.charAt(0) - 32)));
    }

}
