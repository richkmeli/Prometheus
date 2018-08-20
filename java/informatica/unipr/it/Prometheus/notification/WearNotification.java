package informatica.unipr.it.Prometheus.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import informatica.unipr.it.Prometheus.MainActivity;
import informatica.unipr.it.Prometheus.R;

// Interferisce con il funzionamento del servizio in quanto le notifiche generano problemi nell'app di foreground
public class WearNotification {
    private Context context;

    public WearNotification(Context context) {
        this.context = context;
    }

    public void wearNotification(String processName) {

        int notificationId = 001;

        Intent viewIntent = new Intent(context, MainActivity.class);

        viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(context, 0, viewIntent, 0);

        // Setting avatar
        SharedPreferences avatarSharedPreferences = context.getSharedPreferences("Avatar", context.MODE_PRIVATE);
        String avatar = avatarSharedPreferences.getString("avatarImage", "prom");
        int id2;
        if (avatar.equals("android") || avatar.equals("prom")) {
            id2 = context.getResources().getIdentifier(avatar + "desperate", "drawable", context.getPackageName());
        } else {
            id2 = context.getResources().getIdentifier(avatar + "angry", "drawable", context.getPackageName());
        }

        BitmapDrawable bitmapDrawable = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            bitmapDrawable = (BitmapDrawable) context.getDrawable(id2);
        }
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(id2)
                        .setLargeIcon(bitmapDrawable.getBitmap())
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText("text" + processName)
                        .setAutoCancel(true)
                        .setContentIntent(viewPendingIntent);

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
