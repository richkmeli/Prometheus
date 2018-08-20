package informatica.unipr.it.Prometheus.recyclerView.notificationSettings;


import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.data.database.DatabaseManager;
import informatica.unipr.it.Prometheus.data.model.DataManager;
import informatica.unipr.it.Prometheus.recyclerView.ListRemover;
import informatica.unipr.it.Prometheus.recyclerView.StatisticsObject;

public class NotificationViewHolder extends RecyclerView.ViewHolder {
    public ImageView icon;
    public Context context;
    public String processName;
    public String packageName;
    public DataManager dataManager;
    public ListRemover remover;
    public NumberPicker hoursNotification;
    public NumberPicker minuteNotification;
    protected TextView usage;
    private PackageManager pm;
    private View mView;
    private int hoursNotificationValue;
    private int minuteNotificationValue;


    public NotificationViewHolder(View view, final Context context, final ListRemover remover, final int pageNumber) {
        super(view);
        mView = view;
        this.remover = remover;
        usage = (TextView) view.findViewById(R.id.usage);
        icon = (ImageView) view.findViewById(R.id.icon);
        this.context = context;

        this.pm = context.getPackageManager();


        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dataManager = new DatabaseManager(context);

                View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.dialog_track, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                TextView titleTextView = (TextView) view.findViewById(R.id.timeTitle);
                titleTextView.setText(context.getString(R.string.recycler_view_notification_dialog_title));

                TextView messageTextView = (TextView) view.findViewById(R.id.messageTrack);
                String s = context.getString(R.string.recycler_view_notification_dialog_message) + " " + processName + "?";
                messageTextView.setText(s);

                Button yesButton = (Button) view.findViewById(R.id.yesTrack);
                Button noButton = (Button) view.findViewById(R.id.noTrack);

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataManager.loadNotifying(packageName, true);
                        if (pageNumber != 4) {
                            remover.removeItem(getLayoutPosition());
                        }
                        alertDialog.dismiss();
                    }
                });


                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataManager.loadNotifying(packageName, false);
                        if (pageNumber == 4) {
                            remover.removeItem(getLayoutPosition());
                        }
                        alertDialog.dismiss();
                    }
                });

                alertDialog.setView(view);
                alertDialog.show();
                return;
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dataManager = new DatabaseManager(context);

                View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.dialog_notification_time, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                setMinuteNotificationValue(dataManager.readMaxTime(packageName) % 60);
                setHoursNotificationValue((int) dataManager.readMaxTime(packageName) / 60);

                minuteNotification = (NumberPicker) view.findViewById(R.id.minutePicker);
                minuteNotification.setMinValue(0);
                minuteNotification.setMaxValue(59);
                minuteNotification.setValue(getMinuteNotificationValue());
                minuteNotification.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        setMinuteNotificationValue(newVal);
                        int maxUsage = ((getHoursNotificationValue() * 60) + getMinuteNotificationValue());
                        dataManager.loadMaxTime(packageName, maxUsage);
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
                        int maxUsage = ((getHoursNotificationValue() * 60) + getMinuteNotificationValue());
                        dataManager.loadMaxTime(packageName, maxUsage);
                    }
                });


                RadioButton lowPriorityCB = (RadioButton) view.findViewById(R.id.lowPriorityRB);
                RadioButton midPriorityCB = (RadioButton) view.findViewById(R.id.midPriorityRB);
                RadioButton highPriorityCB = (RadioButton) view.findViewById(R.id.highPriorityRB);
                RadioButton extremePriorityCB = (RadioButton) view.findViewById(R.id.extremePriorityRB);

                lowPriorityCB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataManager.loadNotifyPriority(packageName, 1);
                        if (pageNumber == 1) {
                            remover.removeItem(getLayoutPosition());
                        }
                    }
                });

                midPriorityCB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataManager.loadNotifyPriority(packageName, 2);
                        if (pageNumber == 2) {
                            remover.removeItem(getLayoutPosition());
                        }
                    }
                });

                highPriorityCB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataManager.loadNotifyPriority(packageName, 3);
                        if (pageNumber == 3) {
                            remover.removeItem(getLayoutPosition());
                        }
                    }
                });


                switch (dataManager.readNotifyPriority(packageName)) {
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

                alertDialog.setView(view);
                alertDialog.show();

                //TODO: guaradre quale vaore ritornare true o false
                return true;
            }
        });

    }


    public View getView() {
        return mView;
    }


    public void setIconAndTime(StatisticsObject statisticsObject) throws PackageManager.NameNotFoundException {
        this.icon.setImageDrawable(statisticsObject.getIcon());

        //Inizializzazione grazie all'oggetto
        this.packageName = statisticsObject.getUsage();
        this.processName = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA));

        this.packageName = statisticsObject.getUsage();

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

}