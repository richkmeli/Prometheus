package informatica.unipr.it.Prometheus.service.serviceThread.Compat;


import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;

import informatica.unipr.it.Prometheus.notification.NotificationDispatcher;

public class NotifyThreadCompat extends Thread {
    public Handler handler;
    public boolean isEnded = false;
    public NotificationDispatcher notificationDispatcher;
    private Context context;
    private int timeSleep = 5000;


    public NotifyThreadCompat(Context contextTmp, int timeSleepTmp) {
        context = contextTmp;
        timeSleep = timeSleepTmp;
    }

    @Override
    public void run() {

        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        ForegroundAppCompat foreAppCompat = new ForegroundAppCompat(context);
        handler = new Handler(context.getMainLooper());
        notificationDispatcher = new NotificationDispatcher(context);

        while (!isEnded) {
            if (!myKM.inKeyguardRestrictedInputMode()) {   // se lo schermo è acceso

                final String foregroundApp = foreAppCompat.foregroundApp();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            notificationDispatcher.Notify(foregroundApp);
                        } catch (PackageManager.NameNotFoundException e) {
                            //e.printStackTrace();
                        }
                    }
                });

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
