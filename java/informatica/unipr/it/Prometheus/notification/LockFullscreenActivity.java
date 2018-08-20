package informatica.unipr.it.Prometheus.notification;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.theme.ThemesManager;
import pl.droidsonroids.gif.GifImageView;

public class LockFullscreenActivity extends AppCompatActivity {

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private TextView noticeTextView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            noticeTextView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private int timeDelayClickable = 5000;
    private GifImageView animalImage;
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Settaggio tema
        ThemesManager themesManager = new ThemesManager(this);
        themesManager.setTheme();
        themesManager.setNavBarColor(getWindow());

        setContentView(R.layout.activity_lock_fullscreen);

        mVisible = true;
        noticeTextView = (TextView) findViewById(R.id.fullscreen_notice);

        // Set up the user interaction to manually show or hide the system UI.
        noticeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

      /*  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) { // per sopprimere errore dentro la funzione    // disabilitato pinned
            this.startLockTask();
        }*/


        // Setting avatar
        SharedPreferences avatarSharedPreferences = getSharedPreferences("Avatar", MODE_PRIVATE);
        String avatar = avatarSharedPreferences.getString("avatarImage", "prom");
        int id2;
        if (avatar.equals("android") || avatar.equals("prom")) {
            id2 = getResources().getIdentifier(avatar + "desperate", "drawable", getPackageName());
        } else {
            id2 = getResources().getIdentifier(avatar + "angry", "drawable", getPackageName());
        }
        animalImage = (GifImageView) findViewById(R.id.lockscreenImage);   // binding oggetto con componente
        animalImage.setImageResource(id2);   // setta l'animale scelto

/*          // ANIMAZIONE CANE
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    final Handler handler2 = new Handler(getApplicationContext().getMainLooper());
                    handler2.post(new Runnable() {
                        @Override
                        public void run() {
                            Animation animation = new TranslateAnimation(animalImage.getX(),animalImage.getX()+10,animalImage.getY(),animalImage.getY());
                            animation.setDuration(1000);
                            Animation animation2 = new TranslateAnimation(animalImage.getX(),animalImage.getX()-10,animalImage.getY(),animalImage.getY());
                            animation.setDuration(1000);

                            animalImage.startAnimation(animation);
                            animalImage.startAnimation(animation2);
                        }
                    });

                    try {   // manda in sleep il processo ogni secondo (tempo di campionamento)
                        Thread.sleep(2500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
*/

        noticeTextView.append(" ");
        noticeTextView.append(getIntent().getStringExtra("processName"));

        final Button okButton = (Button) findViewById(R.id.buttonFullscreen);
        okButton.setVisibility(View.INVISIBLE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startMain);
                finish();
            }
        });

        final Handler handler = new Handler(getApplicationContext().getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                okButton.setVisibility(View.VISIBLE);
            }
        }, timeDelayClickable);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(1);
    }


    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, 1);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        //noticeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        //        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, 1);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();      // per non permettere di uscire
    }
}
