package informatica.unipr.it.Prometheus.theme;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

import informatica.unipr.it.Prometheus.R;


public class ThemesManager {
    private Context context;
    private SharedPreferences avatarSharedPreferences;
    private String avatarTheme;
    private String avatarImage;

    public ThemesManager(Context context) {
        this.context = context;
        this.avatarSharedPreferences = this.context.getSharedPreferences("Avatar", Context.MODE_PRIVATE);
        this.avatarTheme = this.avatarSharedPreferences.getString("avatarTheme", "PromTheme");
        this.avatarImage = this.avatarSharedPreferences.getString("avatarImage", "prom");
    }

    //In teoria solo MainActivity
    public void setMainActivityTheme(Application application) {
        try {
            this.context.getTheme();
            int idTheme = this.context.getResources().getIdentifier(avatarTheme, "style", this.context.getPackageName());
            this.context.setTheme(idTheme);
            application.setTheme(idTheme);
        } catch (Resources.NotFoundException notFoundException) {
            this.context.setTheme(R.style.PromTheme);
        }
    }

    public void setTheme() {
        try {
            this.context.getTheme();
            int idTheme = this.context.getResources().getIdentifier(avatarTheme, "style", this.context.getPackageName());
            this.context.setTheme(idTheme);
        } catch (Resources.NotFoundException notFoundException) {
            this.context.setTheme(R.style.PromTheme);
        }
    }

    public void setStatusBarColor(Window window) {
        //Settaggio StatusBar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            int realIdColorStatBar;
            try {
                int idColorStatBar = this.context.getResources().getIdentifier(avatarImage + "_colorPrimaryDark", "color", this.context.getPackageName());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    realIdColorStatBar = this.context.getResources().getColor(idColorStatBar, this.context.getTheme());
                } else {
                    realIdColorStatBar = this.context.getResources().getColor(idColorStatBar);
                }
                window.setStatusBarColor(realIdColorStatBar);
            } catch (Resources.NotFoundException notFoundException) {
                window.setStatusBarColor(Color.BLACK);
            }
        }
    }


    public void setNavBarColor(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (avatarSharedPreferences.getBoolean("isColoredNavBar", false)) {
                int realIdColorNavBar;
                try {
                    int idColorNavBar = this.context.getResources().getIdentifier(avatarImage + "_colorPrimary", "color", this.context.getPackageName());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        realIdColorNavBar = this.context.getResources().getColor(idColorNavBar, this.context.getTheme());
                    } else {
                        realIdColorNavBar = this.context.getResources().getColor(idColorNavBar);
                    }
                    window.setNavigationBarColor(realIdColorNavBar);
                } catch (Resources.NotFoundException notFoundException) {
                    window.setNavigationBarColor(Color.BLACK);
                }
            }
        }
    }

    public void setColorBackground(View backgroundView) {
        int idColorBackground;
        int realIdColorBackground;
        try {
            idColorBackground = this.context.getResources().getIdentifier(avatarImage + "_colorBackground", "color", this.context.getPackageName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                realIdColorBackground = this.context.getResources().getColor(idColorBackground, this.context.getTheme());
            } else {
                realIdColorBackground = this.context.getResources().getColor(idColorBackground);
            }
            backgroundView.setBackgroundColor(realIdColorBackground);
        } catch (Resources.NotFoundException notFoundException) {
            backgroundView.setBackgroundColor(Color.WHITE);
        }
    }


    public void setColorBackgroundCardView(View backgroundView) {
        int idColorBackground;
        int realIdColorBackground;
        try {
            idColorBackground = this.context.getResources().getIdentifier(avatarImage + "_colorCardBackground", "color", this.context.getPackageName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                realIdColorBackground = this.context.getResources().getColor(idColorBackground, this.context.getTheme());
            } else {
                realIdColorBackground = this.context.getResources().getColor(idColorBackground);
            }
            backgroundView.setBackgroundColor(realIdColorBackground);
        } catch (Resources.NotFoundException notFoundException) {
            backgroundView.setBackgroundColor(Color.WHITE);
        }
    }

    public String getTheme() {
        return avatarSharedPreferences.getString("avatarTheme", "PromTheme");
    }


    public void setToolbarColor(Toolbar toolbar) {
        if (avatarTheme.equals("PigTheme")) {
            toolbar.setTitleTextColor(Color.parseColor("#000000"));
        }
    }

    public boolean setNumberPickerColor(NumberPicker numberPicker, int colorDivider) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(Color.BLACK);
                    ((EditText) child).setTextColor(Color.BLACK);

                    Field mSelectionDivider = numberPicker.getClass()
                            .getDeclaredField("mSelectionDivider");
                    mSelectionDivider.setAccessible(true);
                    mSelectionDivider.set(numberPicker, new ColorDrawable(colorDivider));
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                } catch (IllegalAccessException e) {
                } catch (IllegalArgumentException e) {
                }
            }
        }
        return false;
    }

    public void setToolbarColorBackground(Toolbar toolbar) {
        int idColorBackground;
        int realIdColorBackground;
        try {
            idColorBackground = this.context.getResources().getIdentifier(avatarImage + "_colorPrimary", "color", this.context.getPackageName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                realIdColorBackground = this.context.getResources().getColor(idColorBackground, this.context.getTheme());
            } else {
                realIdColorBackground = this.context.getResources().getColor(idColorBackground);
            }
            toolbar.setBackgroundColor(realIdColorBackground);
        } catch (Resources.NotFoundException notFoundException) {
            toolbar.setBackgroundColor(Color.BLUE);
        }
    }

/*    public void setFloatingButton(FloatingActionButton floatingButton){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (avatarTheme.equals("PigTheme")) {
                Drawable icon = context.getDrawable(android.R.drawable.ic_input_add);
                icon.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
                floatingButton.setImageDrawable(icon);
            }
        }
    }*/
}
