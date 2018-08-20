package informatica.unipr.it.Prometheus.service.serviceThread;


import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;

import informatica.unipr.it.Prometheus.notification.NotificationDispatcher;

public class NotifyThread extends Thread {
    public Handler handler;
    public boolean isEnded = false;
    public NotificationDispatcher notificationDispatcher;
    private Context context;
    private int timeSleep = 5000;


    public NotifyThread(Context contextTmp, int timeSleepTmp) {
        context = contextTmp;
        timeSleep = timeSleepTmp;
    }

    @Override
    public void run() {

        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        ForegroundApp foreApp = new ForegroundApp(context);

        handler = new Handler(context.getMainLooper());
        notificationDispatcher = new NotificationDispatcher(context);

        while (!isEnded) {
            if (!myKM.inKeyguardRestrictedInputMode()) {   // se lo schermo è acceso

                final String foregroundApp = foreApp.foregroundApp();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (foregroundApp != null) {
                                notificationDispatcher.Notify(foregroundApp);
                            }
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
