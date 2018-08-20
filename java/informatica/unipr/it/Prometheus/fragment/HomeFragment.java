package informatica.unipr.it.Prometheus.fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.data.database.DatabaseManager;
import informatica.unipr.it.Prometheus.data.model.AppInfo;
import informatica.unipr.it.Prometheus.data.model.DataManager;
import informatica.unipr.it.Prometheus.permissions.NotificationPermissionManager;
import informatica.unipr.it.Prometheus.receiver.AlarmServicesManager;
import informatica.unipr.it.Prometheus.service.AppUsageService;
import informatica.unipr.it.Prometheus.service.LocationService;
import informatica.unipr.it.Prometheus.settings.SettingsActivity;
import informatica.unipr.it.Prometheus.tutorial.TutorialActivity;
import pl.droidsonroids.gif.GifImageView;


public class HomeFragment extends Fragment {

    public PackageManager pm;
    //Usiamo gif cosi si può muovere
    public GifImageView image;
    private int nAppInChart = 5;
    private int maxUsage;
    private List<AppInfo> allApps;
    private int totalTime;
    private int totalNotifyTime;
    private TextView notEnough;
    private SharedPreferences avatarSharedPreferences;
    private SharedPreferences locationSharedPreferences;
    private int count = 0;
    private TextView totalUsageText;
    private TextView totalNotifyText;
    private View.OnClickListener clickSmallToBig = null;
    private ArrayList<String> nameForChart = new ArrayList<>();
    private ArrayList<Entry> timeForChart = new ArrayList<>();
    private PieChart myChart;
    private int[] rainbow;
    private TextView muteTextHome;
    private SharedPreferences setting;


    @Override
    public void onResume() {
        super.onResume();
        //Lettura Statistiche
        rainbow = getActivity().getResources().getIntArray(R.array.chartUntil7);
        totalTime = 0;
        final Handler setImage = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final DataManager dataManager = new DatabaseManager(getActivity());

/*
                allApps = pm.getInstalledPackages(0);*/
                pm = getActivity().getPackageManager();
                allApps = dataManager.refreshApp();
                int tmpTime;
                totalNotifyTime = 0;

                for (AppInfo app : allApps) {
                    tmpTime = app.getTimeUsage();
                    totalNotifyTime = totalNotifyTime + app.getNotifyTime();
                    totalTime = totalTime + tmpTime;
                }

                setImage.post(new Runnable() {
                    @Override
                    public void run() {
                        setAvatar();
                        if (totalTime > 0) {
                            String s = getResources().getString(R.string.total_usage_home_1) + setTime(totalTime) + " " + getResources().getString(R.string.total_usage_home_2);
                            totalUsageText.setText(s);
                        } else {
                            totalUsageText.setText(setTime(totalTime));
                        }
                        String s = getResources().getString(R.string.total_notify_home_1) + " " + totalNotifyTime + " " + getResources().getString(R.string.total_notify_home_2);
                        if (totalNotifyTime == 0) {
                            s = s + getString(R.string.good_job);
                        } else {
                            s = s + getString(R.string.think_about);
                        }
                        totalNotifyText.setText(s);
                    }
                });
            }
        }).start();


        final Handler handlerChart = new Handler();

        Thread threadChart = new Thread(new Runnable() {
            @Override
            public void run() {
                final DataManager dataManager = new DatabaseManager(getActivity());

                pm = getActivity().getPackageManager();
                List<AppInfo> allAppsChart = dataManager.refreshApp();
                timeForChart.clear();
                nameForChart.clear();/*
                Comparator<Integer> myComparator = new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o2.compareTo(o1);
                    }
                };

                //Tree Multimap con ordine descendente delle chiavi
                TreeMultimap<Integer, String> processMap = TreeMultimap.create(myComparator, Ordering.natural());

                AppInfo app;
                for (int j = 0; j < allAppsChart.size(); ++j) {
                    app = allAppsChart.get(j);
                    // if (app.isTracking()) {
                    processMap.put(app.getTimeUsage(), app.getPackageProcessName());
                    // }
                }*/
                Comparator<AppInfo> appComparator = new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo a1, AppInfo a2) {
                        return (Integer.valueOf(a2.getTimeUsage())).compareTo(a1.getTimeUsage());
                    }
                };

                Collections.sort(allAppsChart, appComparator);

                int timeOther = 0;
                int totalChart = 0;
                boolean activeOther = false;
                int h = 0;
                int key;

                for (AppInfo appInfo : allAppsChart) {
                    try {
                        key = appInfo.getTimeUsage();
                        if (h < nAppInChart && key > 120) {
                            nameForChart.add((String) pm.getApplicationLabel(pm.getApplicationInfo((String) appInfo.getPackageProcessName(), PackageManager.GET_META_DATA)));
                            timeForChart.add(new Entry(key, h));
                            totalChart = totalChart + key;
                            ++h;
                        } else {
                            activeOther = true;
                            totalChart = totalChart + key;
                            timeOther = timeOther + key;
                        }
                    } catch (PackageManager.NameNotFoundException | NullPointerException e) {

                    }
                }
                if (h > 4 || activeOther) {
                    nameForChart.add(getResources().getString(R.string.home_pie_chart_other));
                    timeForChart.add(new Entry(timeOther, h + 1));
                }
                count = h;


                String avatarTheme = avatarSharedPreferences.getString("avatarTheme", "PromTheme");

                PieDataSet dataSet = new PieDataSet(timeForChart, "");
                dataSet.setSliceSpace(3);
                dataSet.setSelectionShift(5);
                //dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                dataSet.setColors(rainbow);
                PieData data = new PieData(nameForChart, dataSet);
                data.setValueFormatter(new MyValueFormatter(totalChart));
                //data.setValueFormatter(new PercentFormatter());
                data.setValueTextSize(15f);
                myChart.setDrawSliceText(false);
                myChart.setDrawMarkerViews(true);
                myChart.setTransparentCircleRadius(0);
                myChart.setHoleRadius(0);
                myChart.setDescription("");
                myChart.setData(data);
                Legend l = myChart.getLegend();
                //l.setEnabled(false);
                l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
                l.setForm(Legend.LegendForm.CIRCLE);
                l.setTextSize(12f);
                l.setFormSize(12f);
                if (avatarTheme.equals("DogTheme")) {
                    l.setTextColor(Color.WHITE);
                }
                handlerChart.post(new Runnable() {
                    @Override
                    public void run() {
                        //notEnough.setVisibility(View.GONE);
                        myChart.invalidate();
                    }
                });
            }
        });

        threadChart.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        SharedPreferences tutorialSharedPreferences = getActivity().getSharedPreferences("Tutorial", Context.MODE_PRIVATE);
        if (!tutorialSharedPreferences.getBoolean("tutHomeShowed", false)) {
            tutorialSharedPreferences.edit().putBoolean("tutHomeShowed", true).apply();
            Intent tutorial = new Intent(getContext(), TutorialActivity.class);
            tutorial.putExtra("which", "home");
            startActivity(tutorial);
        }
        //Settaggio Immagine

        setting = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
        locationSharedPreferences = getActivity().getSharedPreferences("Location", Context.MODE_PRIVATE);
        avatarSharedPreferences = getActivity().getSharedPreferences("Avatar", Context.MODE_PRIVATE);

        image = (GifImageView) rootView.findViewById(R.id.homeImage);
        notEnough = (TextView) rootView.findViewById(R.id.notEnoughApp);
        totalUsageText = (TextView) rootView.findViewById(R.id.totalUsageText);
        totalNotifyText = (TextView) rootView.findViewById(R.id.totalNotifyText);

        muteTextHome = (TextView) rootView.findViewById(R.id.muteTextHome);

        notEnough.setVisibility(View.GONE);
        totalUsageText.setVisibility(View.GONE);
        totalNotifyText.setVisibility(View.GONE);
        setHasOptionsMenu(true);


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.home));

        myChart = (PieChart) rootView.findViewById(R.id.myChar);
        myChart.setVisibility(View.GONE);

        String avatar = avatarSharedPreferences.getString("avatarImageHomeFragment", "prom");
        int id = getResources().getIdentifier(avatar, "drawable", getActivity().getPackageName());

        SharedPreferences maxUsageSP = getActivity().getSharedPreferences("MaxUsage", Context.MODE_PRIVATE);
        maxUsage = maxUsageSP.getInt("maxUsage", 7200);
        image.setImageResource(id);


        //Costruzione OnclickListener per ingrandimento e rimpicciolimento immagine
        final View.OnClickListener clickBigToSmall = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                final Handler rotationHandler = new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        final Animation rotation = new RotateAnimation(0, 360,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        rotation.setRepeatCount(10);
                        rotation.setDuration(1000);
                        rotationHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                image.animate().rotationX(360);
                            }
                        });
                    }
                }).start();*/

                NotificationPermissionManager npm = new NotificationPermissionManager(getActivity());
                if (!setting.getBoolean("AppUsageEnabled", false)) {

                    Intent appUsageService = new Intent(getContext().getApplicationContext(), AppUsageService.class);
                    Intent locationService = new Intent(getContext().getApplicationContext(), LocationService.class);
                    SharedPreferences.Editor editor = setting.edit();
                    Calendar calendar = Calendar.getInstance();
                    editor.putInt("today", calendar.get(Calendar.DAY_OF_MONTH));   // la data di oggi è settata, necessario per il controllo del DB che utilizzera il service
                    if (!npm.checkAppUsagePermission()) {
                        npm.showAppUsageDialogPermission();
                    }
                    if (!npm.checkOverlayPermission()) {
                        npm.showOverlayDialogPermission();
                    }
                   /* if (!npm.checkNotificationPermissions()) {
                        npm.showNotificationDialogPermission();
                    }*/
                    npm.checkForBootCompletedPermissions();      // controllo se ho permessi che servono al service

                    getActivity().startService(appUsageService);
                    getActivity().startService(locationService);
                    editor.putBoolean("AppUsageEnabled", true).apply();    // carica le modifiche
                    setAvatar();

                    int intervalTimeForNextSend = 1000 * 60 * 20;
                    Intent alarmServicesManager = new Intent(getActivity(), AlarmServicesManager.class);
                    PendingIntent sender = PendingIntent.getBroadcast(getActivity(), 0, alarmServicesManager, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager am = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
                    am.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), intervalTimeForNextSend , sender);

                } else if (setting.getBoolean("isMute", false)) {

                    if (!npm.checkAppUsagePermission()) {
                        npm.showAppUsageDialogPermission();
                    }
                    if (!npm.checkOverlayPermission()) {
                        npm.showOverlayDialogPermission();
                    }
                    setting.edit().putBoolean("isMute", false).apply();
                    locationSharedPreferences.edit().putBoolean("sleepLocation", false).apply();
                    setAvatar();
                } else if (locationSharedPreferences.getBoolean("sleepLocation", false) && locationSharedPreferences.getBoolean("isLocationActive", true)) {
                    locationSharedPreferences.edit().putBoolean("sleepLocation", false).apply();
                    setAvatar();

                } else {

                    image.setPivotX(0);
                    image.setPivotY(0);
                    image.animate().scaleX(0.4f).scaleY(0.4f).setInterpolator(new BounceInterpolator()).setDuration(1000);
                    image.setOnClickListener(clickSmallToBig);
                    totalUsageText.setVisibility(View.VISIBLE);
                    totalNotifyText.setVisibility(View.VISIBLE);

                    Animation animationTextAppear = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left);
                    animationTextAppear.setDuration(1000);
                    totalUsageText.startAnimation(animationTextAppear);
                    totalNotifyText.startAnimation(animationTextAppear);


                    if (count <= 1) {
                        myChart.setVisibility(View.GONE);
                        notEnough.setVisibility(View.VISIBLE);

                    } else {
                        notEnough.setVisibility(View.GONE);
                        myChart.setVisibility(View.VISIBLE);
                        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
                        scaleAnimation.setDuration(1000);
                        scaleAnimation.setInterpolator(new FastOutSlowInInterpolator());
                        myChart.startAnimation(scaleAnimation);

                    }
                /*ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
                scaleAnimation.setDuration(1000);
                scaleAnimation.setInterpolator(new BounceInterpolator());
                baloon.startAnimation(scaleAnimation);
*/
                    //baloon.animate().scaleXBy(0).scaleYBy(0).setDuration(1000);
                }

            }
        };

        clickSmallToBig = new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (count <= 1) {

                    notEnough.setVisibility(View.GONE);
                } else {
                    myChart.setVisibility(View.GONE);
                    ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0, 1, 0, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
                    scaleAnimation.setDuration(800);
                    scaleAnimation.setInterpolator(new DecelerateInterpolator());
                    myChart.startAnimation(scaleAnimation);
                }
                image.setPivotX(0.5f);
                image.setPivotY(0.5f);

                image.animate().scaleX(1).scaleY(1).setInterpolator(new BounceInterpolator()).setDuration(1000);
                image.setOnClickListener(clickBigToSmall);


                totalNotifyText.setVisibility(View.INVISIBLE);
                totalUsageText.setVisibility(View.INVISIBLE);

                Animation animationTextDisappear = AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right);
                animationTextDisappear.setDuration(1000);
                totalUsageText.startAnimation(animationTextDisappear);
                totalNotifyText.startAnimation(animationTextDisappear);
            }

        };

        image.setOnClickListener(clickBigToSmall);

        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (setting.getBoolean("isMute", false)) {
                    return false;
                } else if (locationSharedPreferences.getBoolean("sleepLocation", false) && locationSharedPreferences.getBoolean("isLocationActive", true)) {
                    return false;
                } else {
                    View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.dialog_track, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

                    TextView titleTextView = (TextView) view.findViewById(R.id.timeTitle);
                    String s = getActivity().getResources().getString(R.string.recycler_view_statistics_show_dialog_title_stop_tracking);
                    titleTextView.setText(s);

                    TextView messageTextView = (TextView) view.findViewById(R.id.messageTrack);
                    s = getActivity().getResources().getString(R.string.dialog_mute);
                    messageTextView.setText(s);

                    //Settaggio immagine
                    String avatarMute = avatarSharedPreferences.getString("avatarImage", "prom");
                    int idAvatar = getActivity().getResources().getIdentifier(avatarMute + "happy", "drawable", getActivity().getPackageName());

                    GifImageView avatarImage = (GifImageView) view.findViewById(R.id.titleDialogTrackAvatar);
                    avatarImage.setImageResource(idAvatar);

                    ImageView appImage = (ImageView) view.findViewById(R.id.dialogTrackAppImage);
                    appImage.setVisibility(View.GONE);


                    LinearLayout colorBar = (LinearLayout) view.findViewById(R.id.colorBar);
                    int realIdColorBar;
                    try {
                        int idColorBar = getActivity().getResources().getIdentifier(avatarMute + "_colorPrimaryDark", "color", getActivity().getPackageName());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            realIdColorBar = getActivity().getResources().getColor(idColorBar, getActivity().getTheme());
                        } else {
                            realIdColorBar = getActivity().getResources().getColor(idColorBar);
                        }
                        colorBar.setBackgroundColor(realIdColorBar);
                    } catch (Resources.NotFoundException notFoundException) {
                        colorBar.setBackgroundColor(Color.BLACK);
                    }


                    Button yesButton = (Button) view.findViewById(R.id.yesTrack);
                    Button noButton = (Button) view.findViewById(R.id.noTrack);

                    yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setting.edit().putBoolean("isMute", true).apply();
                            setAvatar();
                            alertDialog.dismiss();
                        }
                    });


                    noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.setView(view);
                    alertDialog.show();


                    return true;
                }
            }
        });

        return rootView;
    }


    //Metodo per selezione giusto mood
    private void setAvatar() {


        String avatar = avatarSharedPreferences.getString("avatarImage", "prom");
        String avatarEmo;
        setting = getActivity().getSharedPreferences("setting", Context.MODE_PRIVATE);
        int idAvatarWake;
        if (!setting.getBoolean("AppUsageEnabled", false) || setting.getBoolean("isMute", false)) {
            avatarEmo = avatar;
            idAvatarWake = getResources().getIdentifier(avatarEmo, "drawable", getActivity().getPackageName());
            avatarSharedPreferences.edit().putString("avatarImageHomeFragment", avatarEmo).apply();
            muteTextHome.setVisibility(View.VISIBLE);

        } else if (locationSharedPreferences.getBoolean("sleepLocation", false) && locationSharedPreferences.getBoolean("isLocationActive", true)) {
            avatarEmo = avatar;
            idAvatarWake = getResources().getIdentifier(avatarEmo, "drawable", getActivity().getPackageName());
            avatarSharedPreferences.edit().putString("avatarImageHomeFragment", avatarEmo).apply();
            muteTextHome.setVisibility(View.VISIBLE);
        } else {
            if ((float) totalTime <= (float) maxUsage / 2) {
                avatarEmo = avatar + "happy";
                idAvatarWake = getResources().getIdentifier(avatarEmo, "drawable", getActivity().getPackageName());
                avatarSharedPreferences.edit().putString("avatarImageHomeFragment", avatarEmo).apply();
            } else if (totalTime <= maxUsage) {
                avatarEmo = avatar + "pity";
                idAvatarWake = getResources().getIdentifier(avatarEmo, "drawable", getActivity().getPackageName());
                avatarSharedPreferences.edit().putString("avatarImageHomeFragment", avatarEmo).apply();
            } else if ((float) totalTime <= (float) maxUsage * 1.5) {
                avatarEmo = avatar + "angry";
                idAvatarWake = getResources().getIdentifier(avatarEmo, "drawable", getActivity().getPackageName());
                avatarSharedPreferences.edit().putString("avatarImageHomeFragment", avatarEmo).apply();
            } else {
                avatarEmo = avatar + "desperate";
                idAvatarWake = getResources().getIdentifier(avatarEmo, "drawable", getActivity().getPackageName());
                avatarSharedPreferences.edit().putString("avatarImageHomeFragment", avatarEmo).apply();
            }
            muteTextHome.setVisibility(View.GONE);
        }
        image.setImageResource(idAvatarWake);
    }

    String setTime(int time) {
        String timeString = "";
        if (((time) / 60) != 0) {
            if ((((time) / 60) / 60) == 1)
                timeString = timeString + " 1 " + getActivity().getResources().getString(R.string.hour);
            else if ((((time) / 60) / 60) > 1)
                timeString = timeString + " " + (((time) / 60) / 60) + " " + getActivity().getResources().getString(R.string.hours);
            if (((time) / 60 % 60) != 0) {
                if (time / 60 / 60 >= 1)
                    timeString = timeString + " " + getActivity().getResources().getString(R.string.and);

            }
            if ((((time) / 60) % 60) == 1)
                timeString = timeString + " 1 " + getActivity().getResources().getString(R.string.minute);
            else if ((((time) / 60) % 60) > 1)
                timeString = timeString + " " + (((time) / 60) % 60) + " " + getActivity().getResources().getString(R.string.minutes);
        } else {
            if ((time) != 0) {
                if (time % 60 == 1) {
                    timeString = " " + time + " " + getActivity().getResources().getString(R.string.second);
                } else
                    timeString = " " + time + " " + getActivity().getResources().getString(R.string.seconds);
            } else {
                timeString = " " + getActivity().getResources().getString(R.string.not_used_phone);
            }
        }
        return timeString;
    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(getContext(), SettingsActivity.class);
            startActivity(settings);
            getActivity().finish();


        } else if (id == R.id.action_tutorial) {
            Intent tutorial = new Intent(getContext(), TutorialActivity.class);
            tutorial.putExtra("which", "home");
            startActivity(tutorial);
        }

        return super.onOptionsItemSelected(item);
    }

}