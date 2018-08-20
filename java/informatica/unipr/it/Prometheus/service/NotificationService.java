/*

MANIFEST
    <service
            android:name=".service.NotificationService"
            android:enabled="true"
            android:exported="true"
            android:icon="@mipmap/ic_launcher"
            android:label="Prometheus"
            android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE">
            <intent-filter>
                <action android:name="android.service.notification.NotificationListenerService" />
            </intent-filter>
        </service>


package informatica.unipr.it.Prometheus.service;


import android.annotation.TargetApi;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {
    private static LinkedList<String> notificationsList;

    public static LinkedList<String> getNotificationsList() {
        return notificationsList;
    }

    public static boolean removeElemNotificationsList(String app){
        if (!notificationsList.isEmpty()){
           return notificationsList.remove(app);
        }
        return false;
    }

    public static String getLastNotification() {
        if (!notificationsList.isEmpty()){
            return notificationsList.getFirst();
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationsList = new LinkedList<String>();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        String pack = null;
        pack = sbn.getPackageName();

        if (!notificationsList.contains(pack)) {
            notificationsList.addFirst(pack);
        }

    //    Log.i("Package,list: ", notificationsList.toString());

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        String pack = null;
        pack = sbn.getPackageName();

        if (notificationsList.contains(pack)) {
            notificationsList.remove(notificationsList.indexOf(pack));
        }

    //    Log.i("Package Removed,list: ", notificationsList.toString());
    }

}
*/