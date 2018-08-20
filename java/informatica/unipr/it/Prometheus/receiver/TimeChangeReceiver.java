package informatica.unipr.it.Prometheus.receiver;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;

import informatica.unipr.it.Prometheus.data.database.DatabaseManager;
import informatica.unipr.it.Prometheus.data.model.DataManager;
import informatica.unipr.it.Prometheus.service.AppUsageService;
import informatica.unipr.it.Prometheus.service.LocationService;


/*   SE SI USANO QUESTI BROADCAST METTERE IL CODICE SOTTOSTANTE NEL MANIFEST
<receiver android:name=".receiver.TimeChangeReceiver">
            <intent-filter>
                <action android:name="android.intent.action.TIME_SET" />
                <action android:name="android.intent.action.DATE_CHANGED" />
                <action android:name="android.intent.action.TIME_TICK" />
                <action android:name="android.intent.action.TIMEZONE_CHANGED" />
            </intent-filter>
        </receiver>
 */

public class TimeChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(Intent.ACTION_TIME_CHANGED)) {
        }

        if (action.equals(Intent.ACTION_DATE_CHANGED)) {
        }

        if (action.equals(Intent.ACTION_TIME_TICK)) {
        }

        if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
        }


    }

    private void dayIncrementAppUsage(Context context) { // se si sta usando il tracciamento sistema le colonne del db(azzera tempo giornaliero e incrementa totale...)
        SharedPreferences setting = context.getSharedPreferences("setting", context.MODE_PRIVATE);  // booleano che dice se il usare il servizio di tracciamento o no
        if (setting.getBoolean("AppUsageEnabled", false)) { // booleano che dice se tracciare o no. ritorna la preferenza, nel caso non esista da falso
            DataManager dataManager = new DatabaseManager(context);
            dataManager.incrementDay();
        }
    }
}
