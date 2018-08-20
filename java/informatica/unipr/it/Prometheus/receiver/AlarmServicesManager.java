package informatica.unipr.it.Prometheus.receiver;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import informatica.unipr.it.Prometheus.service.AppUsageService;
import informatica.unipr.it.Prometheus.service.LocationService;


public class AlarmServicesManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean serviceAppUsageServiceRun = false;
        boolean serviceLocationServiceRun = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service != null) {
                if (service.service.getClassName().compareTo("informatica.unipr.it.Prometheus.service.AppUsageService") == 0) {
                    serviceAppUsageServiceRun = true;
                } else if (service.service.getClassName().compareTo("informatica.unipr.it.Prometheus.service.LocationService") == 0) {
                    serviceLocationServiceRun = true;
                }
            }
        }

        if (!serviceAppUsageServiceRun) {
            SharedPreferences setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
            if (setting.getBoolean("AppUsageEnabled", false)) {
                Intent serviceIntent = new Intent(context, AppUsageService.class);
                context.startService(serviceIntent);
            }
        }

        if (!serviceLocationServiceRun) {
            SharedPreferences setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
            if (setting.getBoolean("AppUsageEnabled", false)) {
                Intent locationIntent = new Intent(context, LocationService.class);
                context.startService(locationIntent);
            }
        }
    }

}
