package informatica.unipr.it.Prometheus.service.serviceThread.Compat;


import android.app.KeyguardManager;
import android.content.Context;

import java.util.ArrayList;

import informatica.unipr.it.Prometheus.service.serviceThread.DataUploaderThread;

public class ScanningAppThreadCompat extends Thread {
    public ArrayList<String> usageList = new ArrayList<>();
    public boolean isEnded = false;
    private Context context;
    private int numScan = 30;
    private int timeSleep = 1000;


    public ScanningAppThreadCompat(Context contextTmp, int numScanTmp, int timeSleepTmp) {
        context = contextTmp;
        numScan = numScanTmp;
        timeSleep = timeSleepTmp;
    }

    @Override
    public void run() {

        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        ForegroundAppCompat foreAppCompat = new ForegroundAppCompat(context);
        int counter = 1;

        while (!isEnded) {
            if (!myKM.inKeyguardRestrictedInputMode()) {   // se lo schermo è acceso

                final String foregroundApp = foreAppCompat.foregroundApp();
                if (foregroundApp != null)
                    usageList.add(foregroundApp);    // ulteriore controllo anche se non dovrebbe essere null dato che si controllao i permessi, ed è null solo se non si hanno

                // PASSAGGIO DATI AL DB
                if (counter == numScan) {    //Dopo 30 scansioni chiamo DataUploaderThread per caricare i dati sul DB
                    counter = 1;
                    //Per caricare i dati sul db chiamiamo un thread
                    DataUploaderThread dataUploaderThread = new DataUploaderThread(context, usageList);
                    dataUploaderThread.run();
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
