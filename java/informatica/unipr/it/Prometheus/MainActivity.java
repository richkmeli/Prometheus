package informatica.unipr.it.Prometheus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import informatica.unipr.it.Prometheus.analytics.AnalyticsHelper;
import informatica.unipr.it.Prometheus.download.DownloadAndSaveManager;
import informatica.unipr.it.Prometheus.fragment.AppUsageManagerFragment;
import informatica.unipr.it.Prometheus.fragment.HomeFragment;
import informatica.unipr.it.Prometheus.fragment.MapsFragment;
import informatica.unipr.it.Prometheus.fragment.StatisticsFragment;
import informatica.unipr.it.Prometheus.login.FacebookCallbackOnSuccess;
import informatica.unipr.it.Prometheus.login.FacebookLogin;
import informatica.unipr.it.Prometheus.login.FirstStartLoginActivity;
import informatica.unipr.it.Prometheus.login.GoogleCallbackManager;
import informatica.unipr.it.Prometheus.login.GoogleLogin;
import informatica.unipr.it.Prometheus.permissions.NotificationPermissionManager;
import informatica.unipr.it.Prometheus.theme.ThemesManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION_COMPLETED = 2;
    NavigationView navigationView;
    // Notifiche
    private boolean anotherFragmentIsOpen = false;
    // Login
    private GoogleLogin googleLogin;
    private FacebookLogin facebookLogin;
    private NotificationPermissionManager npm;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Settaggio tema
        Window window = getWindow();
        ThemesManager themesManager = new ThemesManager(this);
        themesManager.setMainActivityTheme(getApplication());
        themesManager.setStatusBarColor(window);
        themesManager.setNavBarColor(window);

        setContentView(R.layout.activity_main);

        npm = new NotificationPermissionManager(this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        //Settaggio ColoreBackground
        themesManager.setColorBackground(this.findViewById(android.R.id.content));


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        themesManager.setToolbarColor(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                loadUserDataNavDrawer();
            }
        };
        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }
        toggle.syncState();

/*        getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
        anotherFragmentIsOpen = false;
        navigationView.setCheckedItem(R.id.nav_home);*/

        firstStart();   // se il primo utilizzo manda la finestra di login


        View drawerLayout = findViewById(R.id.activity_main);
        googleLogin = new GoogleLogin(getApplicationContext(), this, drawerLayout, new GoogleCallbackManager() {
            @Override
            public void manageResultOnSuccess() { // implementa il metodo mROS della interfaccia GoogleLoginManager per un comportamento specifico per il caso di riuscita di login
                onResume();
            }
        });

        facebookLogin = new FacebookLogin(getApplicationContext(), this, drawerLayout, new FacebookCallbackOnSuccess() {
            @Override
            public void manageResultOnSuccess() {
                onResume();
            }
        });

        // Analytics
        AnalyticsHelper application = (AnalyticsHelper) getApplication();
        mTracker = application.getDefaultTracker();

        // notifica che è nella MainActivity
        mTracker.setScreenName("MainActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserDataNavDrawer();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (anotherFragmentIsOpen) {
                anotherFragmentIsOpen = false;
                //  fragmentOpen = 1;
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
            finish();

        }else if(id == R.id.action_tutorial){
            Intent tutorial = new Intent(this, TutorialActivity.class);
            tutorial.putExtra("which", fragmentOpen);
            startActivity(tutorial);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            //Controllo il max utilizzo ogni volta quindi nuovo
            navigationView.setCheckedItem(id);
            anotherFragmentIsOpen = false;


            getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
        } else if (id == R.id.nav_appUsageManager) {
            anotherFragmentIsOpen = true;
            navigationView.setCheckedItem(id);

            getSupportFragmentManager().beginTransaction().replace(R.id.content, new AppUsageManagerFragment()).commit();
        } else if (id == R.id.nav_statistics) {
            //content si riferisce alla pagina dove andra il Fragment
            anotherFragmentIsOpen = true;

            //Le statistiche le deve ricreare ogni volta quindi nuovo
            navigationView.setCheckedItem(id);

            getSupportFragmentManager().beginTransaction().replace(R.id.content, new StatisticsFragment()).commit();
        } else if (id == R.id.map_nav) {
            if (npm.checkLocationPermissions()) {
                anotherFragmentIsOpen = true;
                //Le statistiche le deve ricreare ogni volta quindi nuovo
                navigationView.setCheckedItem(id);
                getSupportFragmentManager().beginTransaction().replace(R.id.content, new MapsFragment()).commit();
            } else {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {

                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION_COMPLETED);

                        Toast.makeText(this, this.getResources().getString(R.string.permission_dialog_location), Toast.LENGTH_SHORT).show();

                    } else {

                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION_COMPLETED);
                    }
                }
            }

        } else if (id == R.id.nav_google_login) {
            SharedPreferences userInformation = getApplicationContext().getSharedPreferences("UserInformation", getApplicationContext().MODE_PRIVATE);
            if (userInformation.getBoolean("GoogleLogin", false)) {
                googleLogin.googleSignOut();
            } else {
                googleLogin.googleSignIn();
            }
        } else if (id == R.id.nav_facebook_login) {
            SharedPreferences userInformation = getApplicationContext().getSharedPreferences("UserInformation", Context.MODE_PRIVATE);
            if (userInformation.getBoolean("FBLogin", false)) {
                facebookLogin.facebookSignOut();
            } else {
                facebookLogin.facebookSignIn();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Google e Facebook Login
        googleLogin.resultManager(requestCode, resultCode, data);
        facebookLogin.resultManager(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    private void firstStart() {
        SharedPreferences firstStart = getApplicationContext().getSharedPreferences("firstStart", Context.MODE_PRIVATE);
        if (firstStart.getInt("isFirstStart", 0) == 0) { // se torna uno è la prima volta, di default 0 cosi la prima volta entra perche non esiste prima
            SharedPreferences.Editor editor = firstStart.edit();
            editor.putInt("isFirstStart", 1);
            editor.apply();    // carica le modifiche

            Intent intent = new Intent(this, FirstStartLoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.content, new HomeFragment()).commit();
            anotherFragmentIsOpen = false;
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void loadUserDataNavDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);

            // Informazioni Navigation Drawer - binding variabili e oggetti grafici
            ImageView profileImageNavDraw = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profileImageNavDraw);
            TextView userId = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userId);
            TextView userMailId = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userMailId);
            MenuItem menuItemG = navigationView.getMenu().findItem(R.id.nav_google_login);
            MenuItem menuItemFB = navigationView.getMenu().findItem(R.id.nav_facebook_login);


            SharedPreferences userInformation = getApplicationContext().getSharedPreferences("UserInformation", MODE_PRIVATE);
            if (userInformation.getBoolean("GoogleLogin", false) || userInformation.getBoolean("FBLogin", false)) {

                // il nome e email vengono salvati sullo shared preferences indipendentemente da google o fb, se si effetua il cambio va a sovrascrivere quelli
                if (userId.getText().toString().compareTo(getString(R.string.user)) == 0)   // se è uguale a quella di default allora setta
                    userId.setText(userInformation.getString("Name", getString(R.string.user)));
                if (userMailId.getText().toString().compareTo(getString(R.string.userMail)) == 0)
                    userMailId.setText(userInformation.getString("Email", getString(R.string.userMail)));

                DownloadAndSaveManager dASM = new DownloadAndSaveManager(getApplicationContext());
                if (userInformation.getBoolean("GoogleLogin", false)) {   // se il login è stato effettuato carica i dati nella grafica

                    Bitmap googleProfileImage = dASM.readImageInternal("GoogleProfileImage");
                    profileImageNavDraw.setImageBitmap(googleProfileImage);

                    menuItemG.setTitle("Logout");
                    setDisablefb();
                    menuItemFB.setEnabled(false);

                }
                if (userInformation.getBoolean("FBLogin", false)) {   // se il login è stato effettuato carica i dati nella grafica

                    Bitmap facebookProfileImage = dASM.readImageInternal("FacebookProfileImage");
                    profileImageNavDraw.setImageBitmap(facebookProfileImage);

                    menuItemFB.setTitle("Logout");
                    setDisableG();

                    menuItemG.setEnabled(false);

                }
            } else {
                profileImageNavDraw.setImageBitmap(null);
                userId.setText(getResources().getString(R.string.user));
                userMailId.setText(getResources().getString(R.string.userMail));
                menuItemG.setTitle(getResources().getString(R.string.google_login));
                menuItemFB.setTitle(getResources().getString(R.string.facebook_login));
                menuItemG.setEnabled(true);
                menuItemFB.setEnabled(true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION_COMPLETED: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
/*                    anotherFragmentIsOpen = true;
                    //Le statistiche le deve ricreare ogni volta quindi nuovo
                    navigationView.setCheckedItem(R.id.map_nav);
                    //Toast.makeText(this, "Dovrei aprirmi", Toast.LENGTH_LONG).show();
                    //getSupportFragmentManager().beginTransaction().replace(R.id.content, new MapsFragment()).commit();*/
                } else {
                    Toast.makeText(this, getResources().getString(R.string.permission_dialog_location), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void setDisablefb() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        SharedPreferences avatarSharedPreferences = getSharedPreferences("Avatar", Context.MODE_PRIVATE);
        String avatarTheme = avatarSharedPreferences.getString("avatarTheme", "PromTheme");
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled},
                new int[]{-android.R.attr.state_enabled}
        };
        if (!avatarTheme.equals("DogTheme")) {
            int[] color = new int[]{
                    Color.BLACK,
                    Color.BLACK,
                    Color.BLACK,
                    Color.BLACK,
                    Color.BLACK,
                    Color.GRAY
            };
            ColorStateList colorStateList = new ColorStateList(states, color);
            if (navigationView != null) {
                navigationView.setItemTextColor(colorStateList);
            }
        } else {
            int[] color = new int[]{
                    Color.WHITE,
                    Color.WHITE,
                    Color.WHITE,
                    Color.WHITE,
                    Color.WHITE,
                    Color.GRAY
            };
            ColorStateList colorStateList = new ColorStateList(states, color);
            if (navigationView != null) {
                navigationView.setItemTextColor(colorStateList);
            }
        }
    }

    private void setDisableG() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        SharedPreferences avatarSharedPreferences = getSharedPreferences("Avatar", Context.MODE_PRIVATE);
        String avatarTheme = avatarSharedPreferences.getString("avatarTheme", "PromTheme");
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled},
                new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled}
        };
        if (!avatarTheme.equals("DogTheme")) {
            int[] color = new int[]{
                    Color.BLACK,
                    Color.BLACK,
                    Color.BLACK,
                    Color.BLACK,
                    Color.GRAY,
                    Color.BLACK
            };
            ColorStateList colorStateList = new ColorStateList(states, color);
            navigationView.setItemTextColor(colorStateList);
        } else {
            int[] color = new int[]{
                    Color.WHITE,
                    Color.WHITE,
                    Color.WHITE,
                    Color.WHITE,
                    Color.GRAY,
                    Color.WHITE
            };
            ColorStateList colorStateList = new ColorStateList(states, color);
            navigationView.setItemTextColor(colorStateList);
        }
    }


}
