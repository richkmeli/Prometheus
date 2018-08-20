package informatica.unipr.it.Prometheus.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.data.database.DatabaseManager;
import informatica.unipr.it.Prometheus.data.model.AppInfo;
import informatica.unipr.it.Prometheus.data.model.DataManager;
import informatica.unipr.it.Prometheus.permissions.NotificationPermissionManager;
import informatica.unipr.it.Prometheus.recyclerView.StatisticsObject;
import informatica.unipr.it.Prometheus.recyclerView.statisticsAdd.AddApplication;
import informatica.unipr.it.Prometheus.recyclerView.statisticsShow.StatisticsShowAdapterRV;
import informatica.unipr.it.Prometheus.settings.SettingsActivity;
import informatica.unipr.it.Prometheus.theme.ThemesManager;
import informatica.unipr.it.Prometheus.tutorial.TutorialActivity;

public class AppUsageManagerFragment extends Fragment {
    private Switch appUsageSwitch;
    private TextView helpText;
    private NotificationPermissionManager notificationPermissionManager;
    private RecyclerView myRecyclerView = null;
    private StatisticsShowAdapterRV myAdapter = null;
    private List<AppInfo> appsStatistics;
    private List<StatisticsObject> objectList;
    private DataManager dataManager;
    private PackageManager packageManager;
    private FloatingActionButton addStatistics;
    private String packageName;
    private ProgressBar loadingShow;
    private SharedPreferences avatarSharedPreferences;
    private ThemesManager themesManager;
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayoutCard;
    private boolean isTranslate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appsStatistics = new ArrayList<>();
        dataManager = new DatabaseManager(getContext());
        objectList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.card_show_list, container, false);

        SharedPreferences tutorialSharedPreferences = getActivity().getSharedPreferences("Tutorial", Context.MODE_PRIVATE);
        if (!tutorialSharedPreferences.getBoolean("appUsageHomeShowed", false)) {
            tutorialSharedPreferences.edit().putBoolean("appUsageHomeShowed", true).apply();
            Intent tutorial = new Intent(getContext(), TutorialActivity.class);
            tutorial.putExtra("which", "app_usage");
            startActivity(tutorial);
        }

        notificationPermissionManager = new NotificationPermissionManager(getActivity(), getActivity());

        //Settaggio tema
        themesManager = new ThemesManager(getActivity());
        themesManager.setTheme();
        themesManager.setNavBarColor(getActivity().getWindow());
        //themesManager.setFloatingButton((FloatingActionButton) rootView.findViewById(R.id.plusStatiticsButton));
        setHasOptionsMenu(true);


        swipeRefreshLayoutCard = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefreshCard);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.appUsageManager));
        } catch (NullPointerException e) {
            //DO NOTHING
        }
/*

       SharedPreferences setting = getActivity().getSharedPreferences("setting", getActivity().MODE_PRIVATE);
        appUsageSwitch.setChecked(setting.getBoolean("AppUsageEnabled", false));    // imposta lo stato dello switch in base a come è salvato in memoria

        appUsageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent appUsageService = new Intent(getContext().getApplicationContext(), AppUsageService.class);

                SharedPreferences setting = getActivity().getSharedPreferences("setting", getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = setting.edit();
                if (isChecked) {  // se cambia in attivo
                    Calendar calendar = Calendar.getInstance();
                    editor.putInt("today", calendar.get(Calendar.DAY_OF_MONTH));   // la data di oggi è settata, necessario per il controllo del DB che utilizzera il service

                    notificationPermissionManager.checkAppUsagePermission();
                    notificationPermissionManager.checkForBootCompletedPermissions();      // controllo se ho permessi che servono al service
                    notificationPermissionManager.checkNotificationPermissions();
                    notificationPermissionManager.checkOverlayPermission();

                    getActivity().startService(appUsageService);
                    editor.putBoolean("AppUsageEnabled", true); // il service è avviato

                    editor.commit();    // carica le modifiche

                } else {  // ferma il servizio e lo toglie dalla lsita degli avvii automatici settando la SharedPreferences
                    getActivity().stopService(appUsageService);    // ferma il servizio in esecuzione
                    // toglie servizio da lista avvio automatico
                    editor.putBoolean("AppUsageEnabled", false);
                    editor.commit();    // carica le modifiche
                }
            }
        });
*/


        myRecyclerView = (RecyclerView) rootView.findViewById(R.id.statistics_show_RV);
        addStatistics = (FloatingActionButton) rootView.findViewById(R.id.plusStatiticsButton);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            avatarSharedPreferences = getActivity().getSharedPreferences("Avatar", Context.MODE_PRIVATE);
            //String avatarName = avatarSharedPreferences.getString("avatarImage", "android");
            String avatarName = avatarSharedPreferences.getString("avatarImage", "prom");
            int idColor = this.getResources().getIdentifier(avatarName + "_colorPrimary", "colors", getActivity().getPackageName());
            addStatistics.setBackgroundColor(idColor);
        }

        myAdapter = new StatisticsShowAdapterRV(objectList, getActivity());
        myRecyclerView.setAdapter(myAdapter);


        addStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddApplication.class);
                startActivity(intent);
            }
        });
        isTranslate = false;
        final float xFloat = addStatistics.getX();
        addStatistics.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (!isTranslate) {
                    addStatistics.animate().translationX((addStatistics.getWidth() + 100) - rootView.getWidth()).setDuration(700);
                    isTranslate = true;
                } else {
                    addStatistics.animate().translationX(xFloat).setDuration(700);
                    isTranslate = false;
                }

                return true;
            }
        });


        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();

        loadingShow = (ProgressBar) getActivity().findViewById(R.id.loadingShow);
        helpText = (TextView) getActivity().findViewById(R.id.HelpTextAdd);

        final Handler handlerData = new Handler();

        final Thread threadData = new Thread(new Runnable() {
            @Override
            public void run() {
                //Per avere il comparatore descendente
                Comparator<AppInfo> appComparator = new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo a1, AppInfo a2) {
                        return (Integer.valueOf(a2.getTimeUsage())).compareTo(a1.getTimeUsage());
                    }
                };

                packageManager = getActivity().getPackageManager();
                appsStatistics.clear();
                appsStatistics = dataManager.refreshApp();

                Collections.sort(appsStatistics, appComparator);

                objectList.clear();
                for (AppInfo ai : appsStatistics) {
                    if (ai.isTracking()) {
                        packageName = ai.getPackageProcessName();
                        Drawable icon = null;
                        try {
                            icon = packageManager.getApplicationIcon(packageName);
                        } catch (PackageManager.NameNotFoundException e) {
                        }
                        objectList.add(new StatisticsObject(icon, packageName, ai.getTimeUsage()));
                    }
                }


                handlerData.post(new Runnable() {
                    @Override
                    public void run() {
                        loadingShow.setVisibility(View.GONE);
                        if (objectList.size() == 0) helpText.setVisibility(View.VISIBLE);
                        else {
                            helpText.setVisibility(View.GONE);
                            //themesManager.setColorBackgroundCardView(rootView);
                        }
                        myAdapter.notifyDataSetChanged();
                    }
                });

                swipeRefreshLayoutCard.setRefreshing(false);
            }
        }
        );

        threadData.start();

        swipeRefreshLayoutCard = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefreshCard);
        swipeRefreshLayoutCard.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (!threadData.isAlive())
                            threadData.run();

                    }

                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        notificationPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


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
            tutorial.putExtra("which", "app_usage");
            startActivity(tutorial);
        }

        return super.onOptionsItemSelected(item);
    }

}
