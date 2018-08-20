package informatica.unipr.it.Prometheus.service.serviceThread;


import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.ArrayList;

import informatica.unipr.it.Prometheus.permissions.NotificationPermissionManager;
import informatica.unipr.it.Prometheus.service.AppUsageService;
import informatica.unipr.it.Prometheus.service.LocationService;

public class ScanningAppThread extends Thread {
    public boolean isEnded = false;
    private ArrayList<String> usageList = new ArrayList<>();
    private Context context;
    private int numScan = 30;
    private int timeSleep = 1000;

    public ScanningAppThread(Context contextTmp, int numScanTmp, int timeSleepTmp) {
        context = contextTmp;
        numScan = numScanTmp;
        timeSleep = timeSleepTmp;

    }

    @Override
    public void run() {

        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        ForegroundApp foreApp = new ForegroundApp(context);
        int counter = 1;

        while (!isEnded) {
            if (!myKM.inKeyguardRestrictedInputMode()) {   // se lo schermo è acceso

                final String foregroundApp = foreApp.foregroundApp();
                if (foregroundApp != null)
                    usageList.add(foregroundApp);    // ulteriore controllo anche se non dovrebbe essere null dato che si controllao i permessi, ed è null solo se non si hanno

                // PASSAGGIO DATI AL DB
                if (counter == numScan) {    //Dopo 30 scansioni chiamo DataUploaderThread per caricare i dati sul DB
                    counter = 1;
                    //Per caricare i dati sul db chiamiamo un thread
                    DataUploaderThread dataUploaderThread = new DataUploaderThread(context, usageList); // viene passata l'array con le stringhe di elementi
                    dataUploaderThread.start();
                    usageList.clear();
                } else ++counter;

            }
            try {   // manda in sleep il processo ogni secondo (tempo di campionamento)
                Thread.sleep(timeSleep);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }

        }
        return;
    }

}
