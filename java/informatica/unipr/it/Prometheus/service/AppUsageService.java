package informatica.unipr.it.Prometheus.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import informatica.unipr.it.Prometheus.service.serviceThread.CheckDatabaseIntegrityThread;
import informatica.unipr.it.Prometheus.service.serviceThread.Compat.NotifyThreadCompat;
import informatica.unipr.it.Prometheus.service.serviceThread.Compat.ScanningAppThreadCompat;
import informatica.unipr.it.Prometheus.service.serviceThread.NotifyThread;
import informatica.unipr.it.Prometheus.service.serviceThread.ScanningAppThread;


public class AppUsageService extends Service {
    ScanningAppThread scanningAppThread;
    NotifyThread notifyThread;
    ScanningAppThreadCompat scanningAppThreadCompat;
    NotifyThreadCompat notifyThreadCompat;
    CheckDatabaseIntegrityThread checkDatabaseIntegrityThread;
    private int numScan = 30;
    private int timeSleepScanningApp = 1000;
    private int timeSleepNotify = 5000;
    private int timeSleepCheckDatabaseIntegrity = 60000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Creiamo un nuovo thread e eseguiamo lo scan
    @Override
    public int onStartCommand(Intent i, final int flags, int startId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) { // per le api superiori alla 21 inclusa (LOLLIPOP in poi)
            scanningAppThread = new ScanningAppThread(getApplicationContext(), numScan, timeSleepScanningApp);
            notifyThread = new NotifyThread(getApplicationContext(), timeSleepNotify);

            notifyThread.start();
            scanningAppThread.start();

        } else { // per le api inferiori alla 21 esclusa
            scanningAppThreadCompat = new ScanningAppThreadCompat(getApplicationContext(), numScan, timeSleepScanningApp);
            notifyThreadCompat = new NotifyThreadCompat(getApplicationContext(), timeSleepNotify);

            notifyThreadCompat.start();
            scanningAppThreadCompat.start();
        }
        // in comune per le versioni di android
        checkDatabaseIntegrityThread = new CheckDatabaseIntegrityThread(getApplicationContext(), timeSleepCheckDatabaseIntegrity);
        checkDatabaseIntegrityThread.start();

        return START_STICKY;    // se viene distrutto il service il sistema lo ricrea
    }

    @Override
    public void onDestroy() {
        if (notifyThread != null) notifyThread.isEnded = true;    // permette al thread di terminare
        if (scanningAppThread != null) scanningAppThread.isEnded = true;
        if (notifyThreadCompat != null)
            notifyThreadCompat.isEnded = true;    // permette al thread di terminare
        if (scanningAppThreadCompat != null) scanningAppThreadCompat.isEnded = true;

        super.onDestroy();
    }


}



