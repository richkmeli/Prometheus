package informatica.unipr.it.Prometheus.receiver;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

import informatica.unipr.it.Prometheus.service.AppUsageService;
import informatica.unipr.it.Prometheus.service.LocationService;

/* questa classe prepara un receiver poi nel manifest si collega questa classe con un particolare
* tipo di messaggio broadcast in questo caso android.intent.action.BOOT_COMPLETED, che mandera un
* intent come vuole la onReceive
* */

public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) { // tipo di intent che gli arriva è gestito dal manifest

        SharedPreferences setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);  // booleano che dice se il usare il servizio di tracciamento o no
        if (setting.getBoolean("AppUsageEnabled", false)) { // booleano che dice se tracciare o no. ritorna la preferenza, nel caso non esista da falso
            Intent serviceIntent = new Intent(context, AppUsageService.class);
            context.startService(serviceIntent);

            Intent locationIntent = new Intent(context, LocationService.class);
            context.startService(locationIntent);
        }


        int intervalTimeForNextSend = 1000 * 60 * 20;
        Intent alarmServicesManager = new Intent(context, AlarmServicesManager.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, alarmServicesManager, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), intervalTimeForNextSend , sender);
    }
}
