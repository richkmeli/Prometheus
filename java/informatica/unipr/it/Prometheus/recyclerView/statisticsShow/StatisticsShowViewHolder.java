package informatica.unipr.it.Prometheus.recyclerView.statisticsShow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.data.database.DatabaseManager;
import informatica.unipr.it.Prometheus.data.model.AppInfo;
import informatica.unipr.it.Prometheus.data.model.DataManager;
import informatica.unipr.it.Prometheus.recyclerView.ListRemover;
import informatica.unipr.it.Prometheus.recyclerView.StatisticsObject;
import informatica.unipr.it.Prometheus.theme.ThemesManager;
import pl.droidsonroids.gif.GifImageView;

public class StatisticsShowViewHolder extends RecyclerView.ViewHolder {
    public ImageView icon;
    public Context context;
    public String processName;
    public String packageName;
    public DataManager dataManager;
    public ListRemover remover;
    public NumberPicker hoursNotification;
    public NumberPicker minuteNotification;
    public TextView timeToNotify;
    public TextView title;
    public ImageView priorityPoint;
    public List<AppInfo> apps = new ArrayList<>();
    private ImageView clock;
    private TextView usage;
    private ImageView arrow;
    private PackageManager packageManager;
    private View mView;
    private Switch switchNotify;
    private int hoursNotificationValue;
    private int minuteNotificationValue;
    private String avatar;
    private AppInfo singleApp;
    private int maxUsageDialogTimePicker;
    private View tapLayout;
    private View modLayout;
    private Switch switchTrack;
    private SharedPreferences avatarSharedPreferences;


    public StatisticsShowViewHolder(View rootView, final Context context, final ListRemover remover) {
        super(rootView);
        this.mView = rootView;
        this.context = context;
        this.packageManager = context.getPackageManager();
        this.remover = remover;
        this.title = (TextView) rootView.findViewById(R.id.titleShow);
        this.icon = (ImageView) rootView.findViewById(R.id.icon);
        this.usage = (TextView) rootView.findViewById(R.id.usage);
        this.timeToNotify = (TextView) rootView.findViewById(R.id.timeNotify);
        this.arrow = (ImageView) rootView.findViewById(R.id.arrowCard);
        this.priorityPoint = (ImageView) rootView.findViewById(R.id.priorityPoint);

        this.tapLayout = (View) rootView.findViewById(R.id.titleImageRelative);
        this.modLayout = (View) rootView.findViewById(R.id.LinearLayotSelezione);

        this.avatarSharedPreferences = context.getSharedPreferences("Avatar", Context.MODE_PRIVATE);
        avatarSharedPreferences.getString("avatarImage", "prom");
        this.avatar = avatarSharedPreferences.getString("avatarImage", "prom");

        dataManager = new DatabaseManager(context);

        apps.clear();
        apps = dataManager.refreshApp();


        setListenerFirst();


    }


    public View getView() {
        return mView;
    }


    public void setIconAndTime(StatisticsObject statisticsObject) throws PackageManager.NameNotFoundException {
        this.icon.setImageDrawable(statisticsObject.getIcon());

        if (avatarSharedPreferences.getBoolean("colorfullMode", true)) {
            LinearLayout colorBarDown = (LinearLayout) mView.findViewById(R.id.cardLayoutDOWN);
            int realIdColorBar;
            try {
                int idColorBar = context.getResources().getIdentifier(avatar + "_colorCardBackground", "color", context.getPackageName());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    realIdColorBar = context.getResources().getColor(idColorBar, context.getTheme());
                } else {
                    realIdColorBar = context.getResources().getColor(idColorBar);
                }
                colorBarDown.setBackgroundColor(realIdColorBar);
            } catch (Resources.NotFoundException notFoundException) {
                colorBarDown.setBackgroundColor(Color.BLACK);
            }
        }


        //Inizializzazione grazie all'oggetto
        this.packageName = statisticsObject.getUsage();
        this.processName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        singleApp = getElementByName(packageName);

        if (singleApp != null) {

            switch (singleApp.getNotifyPriority()) {
                case 1:

                    this.priorityPoint.setImageResource(R.drawable.lowpriority);
                    break;
                case 2:

                    this.priorityPoint.setImageResource(R.drawable.midpriority);
                    break;
                case 3:

                    this.priorityPoint.setImageResource(R.drawable.highpriority);
                    break;
                case 4:

                    this.priorityPoint.setImageResource(R.drawable.extremepriority);
                    break;
                default:

                    this.priorityPoint.setImageResource(R.drawable.lowpriority);
                    break;
            }

            timeToNotify.setText(timeToNotifyString(singleApp.getMaxTimeUsage()));

            modLayout.setVisibility(View.GONE);
            if (avatar.equals("dog")) {
                arrow.setImageResource(R.drawable.down_arrow_white);
            } else {
                arrow.setImageResource(R.drawable.down_arrow);
            }

            title.setText(processName);

            String timeString = context.getResources().getString(R.string.recycler_view_statistics_show_usage_time);
            timeString = timeString + setTime(statisticsObject.getTime());

            this.usage.setText(timeString);

            //switchNotify.setChecked(singleApp.isNotifying());     // setta stato di quell'applicazione se notificato o no nella checkbox
        }
    }

    public int getMinuteNotificationValue() {
        return minuteNotificationValue;
    }

    public void setMinuteNotificationValue(int minuteNotificationValue) {
        this.minuteNotificationValue = minuteNotificationValue;
    }

    public int getHoursNotificationValue() {
        return hoursNotificationValue;
    }

    public void setHoursNotificationValue(int hoursNotificationValue) {
        this.hoursNotificationValue = hoursNotificationValue;
    }


    public String timeToNotifyString(int maxTime) {
        String notificationTimeString = "";
        if (singleApp != null) {

            if (singleApp.isNotifying()) {
                if (((maxTime) / 60) != 0) {
                    notificationTimeString = context.getResources().getString(R.string.recycler_view_statistics_show_notify_time);
                    if ((((maxTime) / 60) / 60) == 1)
                        notificationTimeString = notificationTimeString + " 1 " + context.getResources().getString(R.string.hour);
                    else if ((((maxTime) / 60) / 60) > 1)
                        notificationTimeString = notificationTimeString + " " + (((maxTime) / 60) / 60) + " " + context.getResources().getString(R.string.hours);
                    if (((maxTime) / 60 % 60) != 0) {
                        if (maxTime / 60 / 60 >= 1)
                            notificationTimeString = notificationTimeString + " " + context.getResources().getString(R.string.and);

                    }
                    if ((((maxTime) / 60) % 60) == 1)
                        notificationTimeString = notificationTimeString + " 1 " + context.getResources().getString(R.string.minute);
                    else if ((((maxTime) / 60) % 60) > 1)
                        notificationTimeString = notificationTimeString + " " + (((maxTime) / 60) % 60) + " " + context.getResources().getString(R.string.minutes);
                } else {
                    if ((maxTime) != 0) {
                        if (maxTime / 60 == 1) {
                            notificationTimeString = maxTime + " " + context.getResources().getString(R.string.second);
                        } else
                            notificationTimeString = maxTime + " " + context.getResources().getString(R.string.seconds);
                    } else {
                        //if (singleApp.isNotifying()){
                        notificationTimeString = context.getResources().getString(R.string.recycler_view_statistics_show_notify_time_always);
                    /*} else {
                        notificationTimeString = context.getResources().getString(R.string.recycler_view_statistics_show_notify_time_never);
                    }*/
                    }
                }
            } else
                notificationTimeString = context.getResources().getString(R.string.recycler_view_statistics_show_notify_time_never);
        }
        return notificationTimeString;
    }

    private void setListenerFirst() {
        this.tapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                switchNotify = (Switch) mView.findViewById(R.id.switchNotify);
                switchTrack = (Switch) mView.findViewById(R.id.switchTrack);


                clock = (ImageView) mView.findViewById(R.id.clockImage);
                if (avatar.equals("dog")) {
                    clock.setImageResource(R.drawable.clock_white);
                } else {
                    clock.setImageResource(R.drawable.clock);
                }

                if (singleApp != null) {

                    switchNotify.setChecked(singleApp.isNotifying());
                    switchTrack.setChecked(true);
                    setListenerSecond();

                    if (modLayout.getVisibility() == View.VISIBLE) {
                        modLayout.setVisibility(View.GONE);
                        if (avatar.equals("dog")) {
                            arrow.setImageResource(R.drawable.down_arrow_white);
                        } else {
                            arrow.setImageResource(R.drawable.down_arrow);
                        }
                    } else {
                        modLayout.setVisibility(View.VISIBLE);
                        if (avatar.equals("dog")) {
                            arrow.setImageResource(R.drawable.up_arrow_white);
                        } else {
                            arrow.setImageResource(R.drawable.up_arrow);
                        }
                    }
                }
            }
        });

        priorityPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.dialog_notification_priority, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                TextView title = (TextView) view.findViewById(R.id.timeTitle);
                String s = context.getResources().getString(R.string.recycler_view_statistics_show_dialog_title_priority);
                title.setText(s);

                TextView text = (TextView) view.findViewById(R.id.IntensityText);
                s = context.getResources().getString(R.string.recycler_view_statistics_show_dialog_message_priority) + " " + processName;
                text.setText(s);

                int idAvatar = context.getResources().getIdentifier(avatar + "happy", "drawable", context.getPackageName());

                GifImageView avatarImage = (GifImageView) view.findViewById(R.id.titleDialogPriorityckAvatr);
                avatarImage.setImageResource(idAvatar);


                final ImageView appImage = (ImageView) view.findViewById(R.id.imageViewPriorityNotificationDialog);
                try {
                    appImage.setImageDrawable(packageManager.getApplicationIcon(packageName));
                } catch (PackageManager.NameNotFoundException e) {
                    appImage.setImageResource(R.drawable.android);
                }

                LinearLayout colorBar = (LinearLayout) view.findViewById(R.id.colorBarNoti);
                int realIdColorBar;
                try {
                    int idColorBar = context.getResources().getIdentifier(avatar + "_colorPrimaryDark", "color", context.getPackageName());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        realIdColorBar = context.getResources().getColor(idColorBar, context.getTheme());
                    } else {
                        realIdColorBar = context.getResources().getColor(idColorBar);
                    }
                    colorBar.setBackgroundColor(realIdColorBar);
                } catch (Resources.NotFoundException notFoundException) {
                    colorBar.setBackgroundColor(Color.BLACK);
                }


                RadioButton lowPriorityCB = (RadioButton) view.findViewById(R.id.lowPriorityRB);
                RadioButton midPriorityCB = (RadioButton) view.findViewById(R.id.midPriorityRB);
                RadioButton highPriorityCB = (RadioButton) view.findViewById(R.id.highPriorityRB);
                RadioButton extremePriorityCB = (RadioButton) view.findViewById(R.id.extremePriorityRB);

                lowPriorityCB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (singleApp != null) {
                            dataManager.loadNotifyPriority(packageName, 1);
                            singleApp.setNotifyPriority(1);

                            priorityPoint.setImageResource(R.drawable.lowpriority);
                        }
                    }
                });

                midPriorityCB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (singleApp != null) {
                            dataManager.loadNotifyPriority(packageName, 2);
                            singleApp.setNotifyPriority(2);

                            priorityPoint.setImageResource(R.drawable.midpriority);
                        }
                    }
                });

                highPriorityCB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (singleApp != null) {
                            dataManager.loadNotifyPriority(packageName, 3);
                            singleApp.setNotifyPriority(3);

                            priorityPoint.setImageResource(R.drawable.highpriority);
                        }
                    }
                });

                extremePriorityCB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (singleApp != null) {
                            dataManager.loadNotifyPriority(packageName, 4);
                            singleApp.setNotifyPriority(4);

                            priorityPoint.setImageResource(R.drawable.extremepriority);
                        }
                    }
                });
                if (singleApp != null) {
                    switch (singleApp.getNotifyPriority()) {
                        case 1:
                            lowPriorityCB.setChecked(true);
                            break;
                        case 2:
                            midPriorityCB.setChecked(true);
                            break;
                        case 3:
                            highPriorityCB.setChecked(true);
                            break;
                        case 4:
                            extremePriorityCB.setChecked(true);
                            break;
                        default:
                            lowPriorityCB.setChecked(true);
                            break;
                    }
                }

                alertDialog.setView(view);
                alertDialog.show();

            }
        });

    }

    void setListenerSecond() {

        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (singleApp != null) {
                    View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.dialog_notification_time, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    setMinuteNotificationValue((singleApp.getMaxTimeUsage() / 60) % 60);
                    setHoursNotificationValue((singleApp.getMaxTimeUsage() / 60) / 60);


                    alertDialog.setCanceledOnTouchOutside(false);

                    Button confirm = (Button) view.findViewById(R.id.confirmTimeDialog);
                    Button cancel = (Button) view.findViewById(R.id.cancelTimeDialog);

                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (singleApp != null) {
                                timeToNotify.setText(timeToNotifyString(maxUsageDialogTimePicker));
                                singleApp.setMaxTimeUsage(maxUsageDialogTimePicker);
                                dataManager.loadMaxTime(packageName, maxUsageDialogTimePicker);
                            }
                            alertDialog.dismiss();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });


                    int idAvatar = context.getResources().getIdentifier(avatar + "happy", "drawable", context.getPackageName());

                    GifImageView avatarImage = (GifImageView) view.findViewById(R.id.avatarTimeDialog);
                    avatarImage.setImageResource(idAvatar);

                    LinearLayout colorBar = (LinearLayout) view.findViewById(R.id.colorBarNoti);
                    int realIdColorBar;
                    try {
                        int idColorBar = context.getResources().getIdentifier(avatar + "_colorPrimaryDark", "color", context.getPackageName());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            realIdColorBar = context.getResources().getColor(idColorBar, context.getTheme());
                        } else {
                            realIdColorBar = context.getResources().getColor(idColorBar);
                        }
                        colorBar.setBackgroundColor(realIdColorBar);
                    } catch (Resources.NotFoundException notFoundException) {
                        colorBar.setBackgroundColor(Color.BLACK);
                    }

                    TextView text = (TextView) view.findViewById(R.id.textTimeDialog);
                    String s = context.getResources().getString(R.string.recycler_view_statistics_show_picker_time_notify_1) + " '" + processName + "' " + context.getResources().getString(R.string.recycler_view_statistics_show_picker_time_notify_2);
                    text.setText(s);


                    minuteNotification = (NumberPicker) view.findViewById(R.id.minutePicker);
                    minuteNotification.setMinValue(0);
                    minuteNotification.setMaxValue(59);
                    minuteNotification.setValue(getMinuteNotificationValue());
                    minuteNotification.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            setMinuteNotificationValue(newVal);
                            maxUsageDialogTimePicker = ((getHoursNotificationValue() * 60) + getMinuteNotificationValue()) * 60;
                        }
                    });

                    hoursNotification = (NumberPicker) view.findViewById(R.id.hoursPicker);
                    hoursNotification.setMinValue(0);
                    hoursNotification.setMaxValue(23);
                    hoursNotification.setValue(getHoursNotificationValue());
                    hoursNotification.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            setHoursNotificationValue(newVal);
                            maxUsageDialogTimePicker = ((getHoursNotificationValue() * 60) + getMinuteNotificationValue()) * 60;
                        }
                    });
                    if (avatar.equals("dog")) {
                        ThemesManager themesManager = new ThemesManager(context);
                        int idColor = Color.parseColor("#5d4037");
                        themesManager.setNumberPickerColor(minuteNotification, idColor);
                        themesManager.setNumberPickerColor(hoursNotification, idColor);
                    }

                    alertDialog.setView(view);
                    alertDialog.show();
                }
            }
        });


        switchNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (singleApp != null) {
                    singleApp.setNotifying(isChecked);
                    dataManager.loadNotifying(packageName, isChecked);
                    timeToNotify.setText(timeToNotifyString(maxUsageDialogTimePicker));
                }
            }

        });

        switchTrack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {

                    /*Super Material

                    View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.dialog_material_layout, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                    TextView messageTextView = (TextView) view.findViewById(R.id.textViewDialogMaterial);
                    String s = context.getResources().getString(R.string.recycler_view_statistics_show_dialog_message_stop_tracking_1) + " " + processName + " " + context.getResources().getString(R.string.recycler_view_statistics_show_dialog_message_stop_tracking_2);
                    messageTextView.setText(s);


                    ImageView appImage = (ImageView) view.findViewById(R.id.iconDialogMaterial);
                    try {
                        appImage.setImageDrawable(packageManager.getApplicationIcon(packageName));
                    } catch (PackageManager.NameNotFoundException e) {
                        appImage.setImageResource(R.drawable.android);
                    }

                    alertDialog.setView(view);
                    alertDialog.show();*/


                    View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.dialog_track, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                    TextView titleTextView = (TextView) view.findViewById(R.id.timeTitle);
                    String s = context.getResources().getString(R.string.recycler_view_statistics_show_dialog_title_stop_tracking);
                    titleTextView.setText(s);

                    TextView messageTextView = (TextView) view.findViewById(R.id.messageTrack);
                    s = context.getResources().getString(R.string.recycler_view_statistics_show_dialog_message_stop_tracking_1) + " " + processName + " " + context.getResources().getString(R.string.recycler_view_statistics_show_dialog_message_stop_tracking_2);
                    messageTextView.setText(s);

                    //Settaggio immagine

                    int idAvatar = context.getResources().getIdentifier(avatar + "happy", "drawable", context.getPackageName());

                    GifImageView avatarImage = (GifImageView) view.findViewById(R.id.titleDialogTrackAvatar);
                    avatarImage.setImageResource(idAvatar);

                    ImageView appImage = (ImageView) view.findViewById(R.id.dialogTrackAppImage);
                    try {
                        appImage.setImageDrawable(packageManager.getApplicationIcon(packageName));
                    } catch (PackageManager.NameNotFoundException e) {
                        appImage.setImageResource(R.drawable.android);
                    }

                    LinearLayout colorBar = (LinearLayout) view.findViewById(R.id.colorBar);
                    int realIdColorBar;
                    try {
                        int idColorBar = context.getResources().getIdentifier(avatar + "_colorPrimaryDark", "color", context.getPackageName());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            realIdColorBar = context.getResources().getColor(idColorBar, context.getTheme());
                        } else {
                            realIdColorBar = context.getResources().getColor(idColorBar);
                        }
                        colorBar.setBackgroundColor(realIdColorBar);
                    } catch (Resources.NotFoundException notFoundException) {
                        colorBar.setBackgroundColor(Color.BLACK);
                    }


                    /*Material mid
                    TextView yesButton = (TextView) view.findViewById(R.id.yesTrack);
                    TextView noButton = (TextView) view.findViewById(R.id.noTrack);
                    */

                    Button yesButton = (Button) view.findViewById(R.id.yesTrack);
                    Button noButton = (Button) view.findViewById(R.id.noTrack);

                    yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataManager.loadTracking(packageName, false);
                            //app.setTracking(false);
                            remover.removeItem(getLayoutPosition());
                            alertDialog.dismiss();
                        }
                    });


                    noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataManager.loadTracking(packageName, true);
                            //app.setTracking(true);
                            switchTrack.setChecked(true);
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.setView(view);
                    alertDialog.show();


                    return;
                }

            }
        });
    }


    AppInfo getElementByName(String name) {
        for (AppInfo ai : apps) {
            if (ai.getPackageProcessName().equals(name)) {
                return ai;
            }
        }
        return null;
    }

    String setTime(int time) {
        String timeString = "";
        if (((time) / 60) != 0) {
            if ((((time) / 60) / 60) == 1)
                timeString = timeString + " 1 " + context.getResources().getString(R.string.hour);
            else if ((((time) / 60) / 60) > 1)
                timeString = timeString + " " + (((time) / 60) / 60) + " " + context.getResources().getString(R.string.hours);
            if (((time) / 60 % 60) != 0) {
                if (time / 60 / 60 >= 1)
                    timeString = timeString + " " + context.getResources().getString(R.string.and);

            }
            if ((((time) / 60) % 60) == 1)
                timeString = timeString + " 1 " + context.getResources().getString(R.string.minute);
            else if ((((time) / 60) % 60) > 1)
                timeString = timeString + " " + (((time) / 60) % 60) + " " + context.getResources().getString(R.string.minutes);
        } else {
            if ((time) != 0) {
                if (time / 60 == 1) {
                    timeString = " " + time + " " + context.getResources().getString(R.string.second);
                } else
                    timeString = " " + time + " " + context.getResources().getString(R.string.seconds);
            } else {
                timeString = " " + context.getResources().getString(R.string.not_used_app);
            }
        }
        return timeString;
    }


}