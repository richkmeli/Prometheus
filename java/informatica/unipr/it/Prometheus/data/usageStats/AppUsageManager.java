package informatica.unipr.it.Prometheus.data.usageStats;


import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import informatica.unipr.it.Prometheus.data.model.AppInfo;
import informatica.unipr.it.Prometheus.data.model.DataManager;

public class AppUsageManager implements DataManager {
    Context context;
    // ATTRIBUTI
    private UsageStatsManager usageStatsManager;
    private long todayTime;
    private long currentTime;
    private Map<String, Integer> appMap;
    private boolean liveMode; // se falso viene costruita una mappa solo nel costruttore altrimenti in ogni read o exist(utilizza molte risorse ma si aggiorna ogni volta)

    // METODI
    // Costruttore
    public AppUsageManager(Context ctx, boolean liveModeT) {
        usageStatsManager = (UsageStatsManager) ctx.getSystemService(Context.USAGE_STATS_SERVICE);
        context = ctx;  // salva il context passato quando si chiama il costruttore per lo SharedPreference
        appMap = new HashMap<String, Integer>();
        settingTime();

        liveMode = liveModeT;
        if (liveMode == false) {
            makeAppList(UsageStatsManager.INTERVAL_DAILY, todayTime, currentTime);
        }
    }

    public AppUsageManager(Context ctx, boolean liveModeT, int intervalTime, long beginTime, long endTime) {
        usageStatsManager = (UsageStatsManager) ctx.getSystemService(Context.USAGE_STATS_SERVICE);
        context = ctx;  // salva il context passato quando si chiama il costruttore per lo SharedPreference
        appMap = new HashMap<String, Integer>();
        settingTime();

        liveMode = liveModeT;
        if (liveMode == false) makeAppList(intervalTime, beginTime, endTime);
    }

    private void settingTime() {
        Calendar calendar = Calendar.getInstance();
        currentTime = calendar.getTimeInMillis();
        calendar.add(Calendar.HOUR_OF_DAY, -calendar.get(Calendar.HOUR_OF_DAY)); // rimoviamo le ore al calendario per costruire mezzanotte di oggi
        calendar.add(Calendar.MINUTE, -calendar.get(Calendar.MINUTE));
        calendar.add(Calendar.SECOND, -calendar.get(Calendar.SECOND));
        todayTime = calendar.getTimeInMillis(); // millisecondi fino a precisamente stamattina a mezzanotte

    }

    private void makeAppList(int intervalTime, long beginTime, long endTime) {   // crea una mappa di <nome Package, UsageStats>
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            List<UsageStats> stats = usageStatsManager.queryUsageStats(intervalTime, beginTime, endTime);  // app usate oggi
            if (stats != null) {
                System.out.println(" ------------------------------ ");

//Toast.makeText(context, "RichkDEBUG : makeAppList ||| ore monitoraggio: " + ((((currentTime - todayTime) / 1000) / 60) / 60), Toast.LENGTH_SHORT).show(); // TODO: RichkDEBUG
                for (UsageStats usageStats : stats) {
                    int tmpTime = 0;
                    if (usageStats.getFirstTimeStamp() > todayTime) { // verifica se l'iniaio dell'evento legato all'app è prima di mezzanotte
                        if (appMap.get(usageStats.getPackageName()) != null)      // controllo se c'è un altro valore e lo metto in tmp
                            tmpTime = appMap.get(usageStats.getPackageName());
                        System.out.println("DopoMez - app: " + usageStats.getPackageName() + "  " + ((int) usageStats.getTotalTimeInForeground() / 60000)
                                + " ultimo utilizzo " + ((usageStats.getLastTimeUsed() - todayTime) / (1000 * 60 * 60)) + ":" + ((usageStats.getLastTimeUsed() - todayTime) / (1000 * 60)) % 60
                                + " first timestamp " + ((usageStats.getFirstTimeStamp() - todayTime) / (1000 * 60 * 60))
                                + " last timestamp " + ((usageStats.getLastTimeStamp() - todayTime) / (1000 * 60 * 60)));
                        appMap.put(usageStats.getPackageName(), ((int) usageStats.getTotalTimeInForeground() + tmpTime));    // preleva e somma tutte le istanze della stessa app e somma i tempi di attività
                    } else {        // caso limite applicazione a cavallo della mezzanotte
                        if (usageStats.getTotalTimeInForeground() > 0) {
                            long timeOverMidnight = usageStats.getLastTimeUsed() - todayTime;    // parte oltre la mezzanotte
                            if (usageStats.getTotalTimeInForeground() < timeOverMidnight)
                                timeOverMidnight = usageStats.getTotalTimeInForeground(); // nel caso il tmepo di foreground sia piccolo(a confronto di quanto siamo distanti da mezzanotte) potrebbe essere dopo mezzanotte effetivamente
                            if (timeOverMidnight > 0) {
                                if (appMap.get(usageStats.getPackageName()) != null)    // controllo se c'è un altro valore e lo metto in tmp
                                    tmpTime = appMap.get(usageStats.getPackageName());
                                appMap.put(usageStats.getPackageName(), ((int) timeOverMidnight + tmpTime));    // preleva e somma tutte le istanze della stessa app e somma i tempi di attività
                                System.out.println("CavalMez - app: " + usageStats.getPackageName() + "  " + ((int) usageStats.getTotalTimeInForeground() / 60000)
                                        + " time foregroundOM : " + timeOverMidnight / 60000
                                        + " ultimo utilizzo " + ((usageStats.getLastTimeUsed() - todayTime) / (1000 * 60 * 60)) + ":" + (((usageStats.getLastTimeUsed() - todayTime) / (1000 * 60)) % 60)
                                        + " first timestamp " + (Math.abs(usageStats.getFirstTimeStamp() - todayTime) / (1000 * 60 * 60))
                                        + " last timestamp " + ((usageStats.getLastTimeStamp() - todayTime) / (1000 * 60 * 60)));
                            }

                        }
                    }
                }
            }
        }
    }


    public int readTimeUsage(String packageProcessName) {   // legge il time usage dell'app richiesta nel giorno di oggi
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (liveMode == true)
                makeAppList(UsageStatsManager.INTERVAL_DAILY, todayTime, currentTime);
            if (isExist(packageProcessName)) {
                //UsageStats app = appMap.get(packageProcessName); // estrae l'app
                //return (int) (app.getTotalTimeInForeground() / 1000);     // da long a int

                return (appMap.get(packageProcessName) / 1000);     // da long a int
            }
        }
        return 0;
    }

    public boolean isExist(String packageProcessName) {     // se esiste oggi
        if (liveMode == true) makeAppList(UsageStatsManager.INTERVAL_DAILY, todayTime, currentTime);
        if (appMap.get(packageProcessName) != null) return true;
        return false;
    }

    @Override
    public boolean incrementDay() {
        return false;
    }

    public int readTimeUsage(String packageProcessName, int intervalTime, long beginTime, long endTime) {   // legge il time usage dell'app richiesta in tempo custom
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (liveMode == true) makeAppList(intervalTime, beginTime, endTime);
            if (isExist(packageProcessName, intervalTime, beginTime, endTime)) {
                //UsageStats app = appMap.get(packageProcessName); // estrae l'app
                //return (int) (app.getTotalTimeInForeground() / 1000);     // da long a int

                return (appMap.get(packageProcessName) / 1000);     // da long a int
            }
        }
        return 0;
    }

    public boolean isExist(String packageProcessName, int intervalTime, long beginTime, long endTime) {   // se esiste nel periodo specificato
        if (liveMode == true) makeAppList(intervalTime, beginTime, endTime);
        if (appMap.get(packageProcessName) != null) return true;
        return false;
    }

    public int readMaxTime(String packageProcessName) {
        SharedPreferences maxTimeSP = context.getSharedPreferences("maxTime", context.MODE_PRIVATE);
        return maxTimeSP.getInt(packageProcessName, 0);
    }

    @Override
    public int readTotalUsage(String packageProcessName) {
        return 0;
    }

    @Override
    public int readNumberMonitoringDay(String packageProcessName) {
        return 0;
    }

    public boolean isTracking(String packageProcessName) {
        SharedPreferences trackingSP = context.getSharedPreferences("tracking", context.MODE_PRIVATE);
        return trackingSP.getBoolean(packageProcessName, false);
    }

    @Override
    public boolean isNotifying(String packageProcessName) {
        return false;
    }

    @Override
    public int readNotifyPriority(String packageProcessName) {
        return 0;
    }

    @Override
    public int readNotifyTime(String packageProcessName) {
        return 0;
    }


    @Override
    public List<AppInfo> refreshApp() {
        return null;
    }

    @Override
    public boolean loadTimeUsage(String packageProcessName, int timeUsage) {
        return false;
    }

    public boolean loadMaxTime(String packageProcessName, int maxTime) {
        SharedPreferences maxTimeSP = context.getSharedPreferences("maxTime", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = maxTimeSP.edit();
        editor.putInt(packageProcessName, maxTime);
        return editor.commit();    // carica le modifiche
    }

    public boolean loadTracking(String packageProcessName, boolean tracking) {
        SharedPreferences trackingSP = context.getSharedPreferences("tracking", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = trackingSP.edit();
        editor.putBoolean(packageProcessName, tracking);
        editor.commit();    // carica le modifiche
        return true;
    }

    @Override
    public boolean loadNotifying(String packageProcessName, boolean notifying) {
        return false;
    }

    @Override
    public boolean loadNotifyPriority(String packageProcessName, int notifyPriority) {
        return false;
    }

    @Override
    public boolean loadNotifyTime(String packageProcessName, int notifyTime) {
        return false;
    }
}
