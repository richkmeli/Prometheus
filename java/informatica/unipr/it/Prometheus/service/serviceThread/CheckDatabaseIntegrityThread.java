package informatica.unipr.it.Prometheus.service.serviceThread;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

import informatica.unipr.it.Prometheus.data.database.DatabaseManager;
import informatica.unipr.it.Prometheus.data.model.DataManager;

public class CheckDatabaseIntegrityThread extends Thread {
    public boolean isEnded = false;
    private DataManager dataManager;
    private Context context;
    private int timeSleep = 60000;


    public CheckDatabaseIntegrityThread(Context contextTmp, int timeSleepTmp) {
        context = contextTmp;
        timeSleep = timeSleepTmp;
        dataManager = new DatabaseManager(context);
    }

    @Override
    public void run() {

        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        while (!isEnded) {
            if (!myKM.inKeyguardRestrictedInputMode()) {   // se lo schermo è acceso

                if (!isToday()) {
                    if (dataManager.incrementDay()) {
                        setToday(); // oggi diventa today quindi ok
                    }
                    // se non va in porto l'increment ci riprova dopo un minuto dato che isToday sarà ancora false
                }
            }
            try {   // manda in sleep il processo ogni secondo (tempo di campionamento)
                Thread.sleep(timeSleep);
            } catch (InterruptedException e) {
                // e.printStackTrace();
            }

        }
        return;
    }

    private boolean isToday() { // torna sempre true finche non cambia la data
        Calendar calendar = Calendar.getInstance();
        SharedPreferences setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        if (calendar.get(Calendar.DAY_OF_MONTH) != (setting.getInt("today", 0))) {
            return false;
        }
        return true;
    }

    private void setToday() {
        Calendar calendar = Calendar.getInstance();

        SharedPreferences setting = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setting.edit();
        editor.putInt("today", calendar.get(Calendar.DAY_OF_MONTH));
        editor.commit();    // carica le modifiche
    }

}
