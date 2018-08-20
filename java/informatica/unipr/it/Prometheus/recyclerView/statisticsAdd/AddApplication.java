package informatica.unipr.it.Prometheus.recyclerView.statisticsAdd;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.data.database.DatabaseManager;
import informatica.unipr.it.Prometheus.data.model.AppInfo;
import informatica.unipr.it.Prometheus.data.model.DataManager;
import informatica.unipr.it.Prometheus.recyclerView.StatisticsObject;
import informatica.unipr.it.Prometheus.theme.ThemesManager;

public class AddApplication extends AppCompatActivity {

    private RecyclerView myRecyclerView;
    private StatisticsAddAdapterRV myAdapter;
    private List<AppInfo> appsStatistics;
    private List<PackageInfo> allApp;
    private List<StatisticsObject> objectList;
    private DataManager dataManager;
    private PackageManager packageManager;
    private String packageName;
    private ProgressBar loadingAdd;
    private Context context;
    private Thread threadData;
    private Handler handlerData;
    private SearchView searchViewAddApplication;
    private SharedPreferences avatarSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Settaggio tema
        final ThemesManager themesManager = new ThemesManager(this);
        themesManager.setTheme();
        themesManager.setStatusBarColor(getWindow());
        themesManager.setNavBarColor(getWindow());

//        final View rootView = this.findViewById(android.R.id.content);

        appsStatistics = new ArrayList<>();
        allApp = new ArrayList<>();
        objectList = new ArrayList<>();

        setContentView(R.layout.activity_add_application);

        dataManager = new DatabaseManager(this);

        myRecyclerView = (RecyclerView) findViewById(R.id.statistics_add_RV);


        avatarSP = getSharedPreferences("Avatar", Context.MODE_PRIVATE);
        Drawable upArrow;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAddActivity);
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
        if (avatarSP.getString("avatarTheme", "PromTheme").equals("PigTheme")) {
            upArrow.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            themesManager.setToolbarColor(toolbar);
        } else {
            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
        getSupportActionBar().setTitle(getApplication().getResources().getString(R.string.activity_add_application_title));


//        SearchView searchView = (SearchView) findViewById(R.id.searchViewAddApplication);




/*        if(avatarSP.getString("avatarTheme", "PromTheme").equals("DogTheme")){
            searchView.setBackgroundColor(Color.parseColor("#212121"));
        }*/


        final Context context = this;
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        loadingAdd = (ProgressBar) findViewById(R.id.loadingAdd);

        //Per risolvere problema che non si assocci alla reycler view un adapter vuoto perchè poi quando chiami l'altro thread lo reclama
        myAdapter = new StatisticsAddAdapterRV(objectList, this);
        myRecyclerView.setAdapter(myAdapter);

        handlerData = new Handler();
        threadData = new Thread(new Runnable() {
            @Override
            public void run() {
                //Per avere il comparatore descendente
                Comparator<AppInfo> appComparator = new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo a1, AppInfo a2) {
                        return (Integer.valueOf(a2.getTimeUsage())).compareTo(a1.getTimeUsage());
                    }
                };

                packageManager = context.getPackageManager();
                appsStatistics.clear();
                appsStatistics = dataManager.refreshApp();
                allApp = packageManager.getInstalledPackages(0);

                Collections.sort(appsStatistics, appComparator);

                objectList.clear();

                // aggiunge alla lista tutte le applicazioni del sistema senza quelle gia tracciate in ordine descrescente
                List<String> appsStatisticsPackage = new ArrayList<>();
                for (AppInfo ai : appsStatistics) {
                    if (!ai.isTracking() && (ai.getPackageProcessName().compareTo("com.melfab.prometheus") != 0)) {
                        packageName = ai.getPackageProcessName();
                        try {
                            Drawable icon = packageManager.getApplicationIcon(packageName);
                            objectList.add(new StatisticsObject(icon, packageName, ai.getTimeUsage()));
                        } catch (PackageManager.NameNotFoundException e) {
                        }
                    }
                    appsStatisticsPackage.add(ai.getPackageProcessName());
                }

                // aggiunge in fondo applicazioni non tracciate e mai rilevate
                for (PackageInfo pi : allApp) {
                    packageName = pi.packageName;
                    if (!appsStatisticsPackage.contains(packageName) && (packageName.compareTo("com.melfab.prometheus") != 0)) {
                        try {
                            Drawable icon = packageManager.getApplicationIcon(packageName);
                            objectList.add(new StatisticsObject(icon, packageName, 0));
                        } catch (PackageManager.NameNotFoundException e) {
                        }
                    }
                }

                handlerData.post(new Runnable() {
                    @Override
                    public void run() {
                        loadingAdd.setVisibility(View.GONE);
                        myAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        threadData.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // Serve per soluzione alternativa
        context = this;
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setQueryHint(getString(R.string.search_hint_add));


        if (!avatarSP.getString("avatarTheme", "PromTheme").equals("PigTheme")) {
            int searchImgId = android.support.v7.appcompat.R.id.search_button;
            ImageView iconSearch = (ImageView) searchView.findViewById(searchImgId);
            iconSearch.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(Color.WHITE);
            int xButton = android.support.v7.appcompat.R.id.search_close_btn;
            ImageView xButtonIcon = (ImageView) searchView.findViewById(xButton);
            xButtonIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        } else {
            int searchImgId = android.support.v7.appcompat.R.id.search_button;
            ImageView iconSearch = (ImageView) searchView.findViewById(searchImgId);
            iconSearch.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
            int xButton = android.support.v7.appcompat.R.id.search_close_btn;
            ImageView xButtonIcon = (ImageView) searchView.findViewById(xButton);
            xButtonIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        }
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));


        //v.setImageResource(R.drawable.android);

        AutoCompleteTextView searchTextView = (AutoCompleteTextView) search.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            if (!avatarSP.getString("avatarTheme", "PromTheme").equals("PigTheme")) {
                mCursorDrawableRes.set(searchTextView, R.drawable.cursor);
            } else {
                mCursorDrawableRes.set(searchTextView, R.drawable.cursor_black);
            }
        } catch (Exception e) {
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!threadData.isAlive()) {
                    packageManager = context.getPackageManager();

                    List<StatisticsObject> tmpArray = new ArrayList<>();

                    for (StatisticsObject so : objectList) {
                        String processName = "";
                        try {
                            processName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(so.getUsage(), PackageManager.GET_META_DATA));
                        } catch (PackageManager.NameNotFoundException e) {
                        }

                        if ((processName.toUpperCase()).compareTo(query.toUpperCase()) == 0) {
                            tmpArray.add(so);
                        }
                    }

                    if (tmpArray.isEmpty()) {
                        objectList.clear();

                        myAdapter.notifyDataSetChanged();
                        return false;
                    } else {
                        objectList.clear();
                        objectList.addAll(tmpArray);

                        myAdapter.notifyDataSetChanged();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!threadData.isAlive()) {
                    if (objectList.size() < 5) {
                        threadData.run();
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    AppInfo getElementByName(String name) {
        for (int i = 0; i < appsStatistics.size(); ++i) {
            if (appsStatistics.get(i).getPackageProcessName().equals(name)) {
                return appsStatistics.get(i);
            }
        }
        return null;
    }

}