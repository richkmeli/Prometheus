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
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import informatica.unipr.it.Prometheus.R;
import pl.droidsonroids.gif.GifImageView;

public class ThemeSettingsActivity extends AppCompatActivity {

    private SharedPreferences avatarSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context context = this;
        Drawable upArrow;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_theme);
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

        getSupportActionBar().setTitle(getString(R.string.theme_settings_activity_title));

        View avatarChooser = findViewById(R.id.avatarChooser);

        final GifImageView avatarSettings = (GifImageView) findViewById(R.id.avatarImageSettings);
        final TextView textTheme = (TextView) findViewById(R.id.themeSettingsName);

        setTextAndImage(avatarSettings, textTheme);

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }


        if (avatarChooser != null) {
            avatarChooser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog alertDialogTheme = new AlertDialog.Builder(context).create();
                    final View viewTheme = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.dialog_select_image, null);

                    View pig = viewTheme.findViewById(R.id.pigTap);
                    final View android = viewTheme.findViewById(R.id.androidTap);
                    View turkey = viewTheme.findViewById(R.id.turkeyTap);
                    View dog = viewTheme.findViewById(R.id.dogTap);
                    View material = viewTheme.findViewById(R.id.materialTap);
                    View prom = viewTheme.findViewById(R.id.promTap);

                    final SharedPreferences.Editor avatarEditor = avatarSharedPreferences.edit();
                    pig.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT == 19) {
                                View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.dialog_theme_issues, null);

                                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                alertDialog.setCanceledOnTouchOutside(false);

                                ImageView icon = (ImageView) view.findViewById(R.id.dialog_theme_issues_image);
                                icon.setImageResource(R.drawable.pighappy);

                                TextView title = (TextView) view.findViewById(R.id.textViewThemeIssues);
                                title.setText(getApplication().getString(R.string.dialog_theme_issues_pig19));

                                TextView confirmButton = (TextView) view.findViewById(R.id.issues_theme_confirm);
                                TextView cancelButton = (TextView) view.findViewById(R.id.issues_theme_cancel);

                                confirmButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        avatarEditor.putString("avatarImage", "pig").apply();
                                        avatarEditor.putString("avatarTheme", "PigTheme").apply();
                                        setTextAndImage(avatarSettings, textTheme);
                                        alertDialog.dismiss();
                                        alertDialogTheme.dismiss();
                                    }
                                });

                                cancelButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });

                                alertDialog.setView(view);
                                alertDialog.show();
                                //alertDialog.getWindow().getDecorView().setBackgroundResource(R.color.transparent);

                            }else if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 19){
                                View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.dialog_theme_issues, null);

                                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                alertDialog.setCanceledOnTouchOutside(false);

                                ImageView icon = (ImageView) view.findViewById(R.id.dialog_theme_issues_image);
                                icon.setImageResource(R.drawable.pighappy);

                                TextView title = (TextView) view.findViewById(R.id.textViewThemeIssues);
                                title.setText(getApplication().getString(R.string.dialog_theme_issues_static_avatar_jellybean));

                                TextView confirmButton = (TextView) view.findViewById(R.id.issues_theme_confirm);
                                TextView cancelButton = (TextView) view.findViewById(R.id.issues_theme_cancel);

                                confirmButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        avatarEditor.putString("avatarImage", "pig").apply();
                                        avatarEditor.putString("avatarTheme", "PigTheme").apply();
                                        setTextAndImage(avatarSettings, textTheme);
                                        alertDialog.dismiss();
                                        alertDialogTheme.dismiss();
                                    }
                                });

                                cancelButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });

                                alertDialog.setView(view);
                                alertDialog.show();
                                //alertDialog.getWindow().getDecorView().setBackgroundResource(R.color.transparent);

                            }else{
                                avatarEditor.putString("avatarImage", "pig").apply();
                                avatarEditor.putString("avatarTheme", "PigTheme").apply();
                                setTextAndImage(avatarSettings, textTheme);
                                alertDialogTheme.dismiss();
                            }

                        }
                    });

                    android.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 19) {
                                View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.dialog_theme_issues, null);

                                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                alertDialog.setCanceledOnTouchOutside(false);

                                ImageView icon = (ImageView) view.findViewById(R.id.dialog_theme_issues_image);
                                icon.setImageResource(R.drawable.androidhappy);

                                TextView title = (TextView) view.findViewById(R.id.textViewThemeIssues);
                                title.setText(getApplication().getString(R.string.dialog_theme_issues_static_avatar_jellybean));

                                TextView confirmButton = (TextView) view.findViewById(R.id.issues_theme_confirm);
                                TextView cancelButton = (TextView) view.findViewById(R.id.issues_theme_cancel);

                                confirmButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        avatarEditor.putString("avatarImage", "android").apply();
                                        avatarEditor.putString("avatarTheme", "AndroidTheme").apply();
                                        setTextAndImage(avatarSettings, textTheme);
                                        alertDialog.dismiss();
                                        alertDialogTheme.dismiss();
                                    }
                                });

                                cancelButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });

                                alertDialog.setView(view);
                                alertDialog.show();
                                //alertDialog.getWindow().getDecorView().setBackgroundResource(R.color.transparent);

                            }else{
                                avatarEditor.putString("avatarImage", "android").apply();
                                avatarEditor.putString("avatarTheme", "AndroidTheme").apply();
                                setTextAndImage(avatarSettings, textTheme);
                                alertDialogTheme.dismiss();
                            }

                        }
                    });

                    dog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 19) {
                                View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.dialog_theme_issues, null);

                                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                alertDialog.setCanceledOnTouchOutside(false);

                                ImageView icon = (ImageView) view.findViewById(R.id.dialog_theme_issues_image);
                                icon.setImageResource(R.drawable.doghappy);

                                TextView title = (TextView) view.findViewById(R.id.textViewThemeIssues);
                                title.setText(getApplication().getString(R.string.dialog_theme_issues_static_avatar_jellybean));

                                TextView confirmButton = (TextView) view.findViewById(R.id.issues_theme_confirm);
                                TextView cancelButton = (TextView) view.findViewById(R.id.issues_theme_cancel);

                                confirmButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        avatarEditor.putString("avatarImage", "dog").apply();
                                        avatarEditor.putString("avatarTheme", "DogTheme").apply();
                                        setTextAndImage(avatarSettings, textTheme);
                                        alertDialog.dismiss();
                                        alertDialogTheme.dismiss();
                                    }
                                });

                                cancelButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });

                                alertDialog.setView(view);
                                alertDialog.show();
                                //alertDialog.getWindow().getDecorView().setBackgroundResource(R.color.transparent);

                            }else{
                                avatarEditor.putString("avatarImage", "dog").apply();
                                avatarEditor.putString("avatarTheme", "DogTheme").apply();
                                setTextAndImage(avatarSettings, textTheme);
                                alertDialogTheme.dismiss();
                            }

                        }
                    });

                    turkey.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 19) {
                                View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.dialog_theme_issues, null);

                                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                alertDialog.setCanceledOnTouchOutside(false);

                                ImageView icon = (ImageView) view.findViewById(R.id.dialog_theme_issues_image);
                                icon.setImageResource(R.drawable.turkeyhappy);

                                TextView title = (TextView) view.findViewById(R.id.textViewThemeIssues);
                                title.setText(getApplication().getString(R.string.dialog_theme_issues_static_avatar_jellybean));

                                TextView confirmButton = (TextView) view.findViewById(R.id.issues_theme_confirm);
                                TextView cancelButton = (TextView) view.findViewById(R.id.issues_theme_cancel);

                                confirmButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        avatarEditor.putString("avatarImage", "turkey").apply();
                                        avatarEditor.putString("avatarTheme", "TurkeyTheme").apply();
                                        setTextAndImage(avatarSettings, textTheme);
                                        alertDialog.dismiss();
                                        alertDialogTheme.dismiss();
                                    }
                                });

                                cancelButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });

                                alertDialog.setView(view);
                                alertDialog.show();
                                //alertDialog.getWindow().getDecorView().setBackgroundResource(R.color.transparent);

                            }else{
                                avatarEditor.putString("avatarImage", "turkey").apply();
                                avatarEditor.putString("avatarTheme", "TurkeyTheme").apply();
                                setTextAndImage(avatarSettings, textTheme);
                                alertDialogTheme.dismiss();
                            }

                        }
                    });

                    material.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            avatarEditor.putString("avatarImage", "material").apply();
                            avatarEditor.putString("avatarTheme", "MaterialTheme").apply();
                            setTextAndImage(avatarSettings, textTheme);
                            alertDialogTheme.dismiss();
                        }
                    });

                    prom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            avatarEditor.putString("avatarImage", "prom").apply();
                            avatarEditor.putString("avatarTheme", "PromTheme").apply();
                            setTextAndImage(avatarSettings, textTheme);
                            alertDialogTheme.dismiss();
                        }
                    });

                    alertDialogTheme.setView(viewTheme);
                    alertDialogTheme.show();
                }
            });
        }

        CheckBox checkBoxNavBar = (CheckBox) findViewById(R.id.checkBoxColoredNavBar);

        if (avatarSharedPreferences.getBoolean("isColoredNavBar", false)) {
            if (checkBoxNavBar != null) {
                checkBoxNavBar.setChecked(true);
            }
        }
        if (checkBoxNavBar != null) {
            checkBoxNavBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    avatarSharedPreferences.edit().putBoolean("isColoredNavBar", isChecked).apply();
                }
            });
        }
 //
        CheckBox checkBoxColorFull = (CheckBox) findViewById(R.id.checkBoxColorTraApp);

        if (avatarSharedPreferences.getBoolean("colorfullMode", true)) {
            if (checkBoxColorFull != null) {
                checkBoxColorFull.setChecked(true);
            }
        }
        if (checkBoxColorFull != null) {
            checkBoxColorFull.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    avatarSharedPreferences.edit().putBoolean("colorfullMode", isChecked).apply();
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


    public void setTextAndImage(GifImageView avatarSettings, TextView textTheme) {
        String avatar = avatarSharedPreferences.getString("avatarImage", "prom");
        avatarSettings.setImageResource(getResources().getIdentifier(avatar + "happy", "drawable", getPackageName()));

        if (avatar.equals("prom"))
            textTheme.setText(getString(R.string.prometheus_theme));
        else {
            int strings = this.getResources().getIdentifier(avatar + "_theme", "string", this.getPackageName());
            textTheme.setText(getString(strings));
        }
    }

}
