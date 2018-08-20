package informatica.unipr.it.Prometheus.notification;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.data.database.DatabaseManager;
import informatica.unipr.it.Prometheus.data.model.DataManager;
import informatica.unipr.it.Prometheus.permissions.NotificationPermissionManager;
import pl.droidsonroids.gif.GifImageView;

public class NotificationDispatcher {


    public BubbleNotification bubbleNotification;
    private Context context;
    private DataManager dataManager;
    private PackageManager packageManager;
    private SharedPreferences setting;
    private NotificationPermissionManager npm;


    public NotificationDispatcher(Context context) {

        this.context = context;
        //Dal package name ricavo la priorità della notifica
        dataManager = new DatabaseManager(context);
        this.packageManager = context.getPackageManager();
        this.npm = new NotificationPermissionManager(context);
    }

    public void Notify(String packageName) throws NameNotFoundException {
        SharedPreferences maxUsageSP = context.getSharedPreferences("MaxUsage", Context.MODE_PRIVATE);
        setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);

        int i = 0;
        int LOWPRIORITY = maxUsageSP.getInt("lowPriority", 300);
        int MIDPRIORITY = maxUsageSP.getInt("midPriority", 150);
        int HIGHPRIORITY = maxUsageSP.getInt("highPriority", 75);

        String processName;
        processName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        if (npm.checkOverlayPermission() && !setting.getBoolean("isMute", false)) {
            SharedPreferences locationSharedPreferences = context.getSharedPreferences("Location", Context.MODE_PRIVATE);
            if(dataManager.isExist(packageName)) {
                if (!(locationSharedPreferences.getBoolean("sleepLocation", false) && locationSharedPreferences.getBoolean("isLocationActive", true))) {
                    int priority = dataManager.readNotifyPriority(packageName);
                    if (dataManager.isNotifying(packageName)) {
                        i = dataManager.readNotifyTime(packageName);
                        switch (priority) {
                            case 1: {
                                if ((dataManager.readMaxTime(packageName) + (i * LOWPRIORITY) <= dataManager.readTimeUsage(packageName))) {
                                    Toast.makeText(context, context.getResources().getString(R.string.notification_too_much_used) + " " + processName, Toast.LENGTH_LONG).show();
                                    dataManager.loadNotifyTime(packageName, i + 1);
                                }
                                break;
                            }
                            case 2: {
                                if ((dataManager.readMaxTime(packageName) + (i * MIDPRIORITY) <= dataManager.readTimeUsage(packageName))) {
                                    bubbleNotification = new BubbleNotification(processName, context);
                                    dataManager.loadNotifyTime(packageName, i + 1);
                                }
                                break;
                            }
                            case 3: {
                                if ((dataManager.readMaxTime(packageName) + (i * HIGHPRIORITY) <= dataManager.readTimeUsage(packageName))) {
                                    View view = LayoutInflater.from(context).inflate(R.layout.dialog_too_much_usage, null);

                                    TextView messageTextView = (TextView) view.findViewById(R.id.textTooMuchUsage);
                                    messageTextView.setText(context.getResources().getString(R.string.notification_too_much_used) + " " + processName);


                                    //Settaggio immagine
                                    SharedPreferences avatarSharedPreferences = context.getSharedPreferences("Avatar", Context.MODE_PRIVATE);
                                    avatarSharedPreferences.getString("avatarImage", "prom");
                                    String avatar = avatarSharedPreferences.getString("avatarImage", "prom");

                                    int idAvatar;
                                    if (avatar.equals("android") || avatar.equals("prom")) {
                                        idAvatar = context.getResources().getIdentifier(avatar + "desperate", "drawable", context.getPackageName());
                                    } else {
                                        idAvatar = context.getResources().getIdentifier(avatar + "angry", "drawable", context.getPackageName());
                                    }
                                    GifImageView avatarImage = (GifImageView) view.findViewById(R.id.avatarNotificationDialog);
                                    avatarImage.setImageResource(idAvatar);

                                    ImageView appImage = (ImageView) view.findViewById(R.id.imageNotifiDialog);
                                    try {
                                        appImage.setImageDrawable(packageManager.getApplicationIcon(packageName));
                                    } catch (NameNotFoundException e) {
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


                                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                    Button buttonTooMuchUsage = (Button) view.findViewById(R.id.buttonTooMuchUsage);

                                    buttonTooMuchUsage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                        }
                                    });

                                    Button buttonShutUp = (Button) view.findViewById(R.id.buttonTooMuchUsageShutUp);

                                    buttonShutUp.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                        }
                                    });

                                    alertDialog.setView(view);
                                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                    alertDialog.show();

                                    dataManager.loadNotifyTime(packageName, i + 1);
                                }
                                break;

                            }
                            case 4: {
                                if ((dataManager.readMaxTime(packageName) <= dataManager.readTimeUsage(packageName))) {

                                    Intent intent = new Intent(context, LockFullscreenActivity.class);
                                    intent.putExtra("processName", processName);

                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);

                                    dataManager.loadNotifyTime(packageName, i + 1);
                                }
                                break;
                            }
                            default: {
                                if ((dataManager.readMaxTime(packageName) + (i * LOWPRIORITY) <= dataManager.readTimeUsage(packageName))) {
                                    Toast.makeText(context, context.getResources().getString(R.string.notification_too_much_used) + " " + processName, Toast.LENGTH_LONG).show();

                                    dataManager.loadNotifyTime(packageName, i + 1);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
