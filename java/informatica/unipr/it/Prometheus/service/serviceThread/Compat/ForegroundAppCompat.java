package informatica.unipr.it.Prometheus.service.serviceThread.Compat;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class ForegroundAppCompat {
    Context context;

    public ForegroundAppCompat(Context contextTmp) {
        context = contextTmp;
    }

    public String foregroundApp() {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(5);
        String foregroundApp = null;

        for (ActivityManager.RunningAppProcessInfo currentApp : processes) {
            if (currentApp.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    currentApp.processName.compareTo("com.android.systemui") != 0 &&
                    currentApp.processName.compareTo("system") != 0 &&
                    currentApp.processName.compareTo("com.android.phone") != 0) {
                for (ActivityManager.RunningTaskInfo currentTask : tasks) { // confronto con metodo alternativo per migliorare affidabilità app scansionata
                    if (currentApp.processName.compareTo(currentTask.topActivity.getPackageName()) == 0) {
                        foregroundApp = currentApp.processName;
                    }
                }
                if (foregroundApp == null)
                    foregroundApp = currentApp.processName;      // se confronto con metodo alternativo non va a buon fine scrive comunque
            }
        }
        return foregroundApp;
    }
}
