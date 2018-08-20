package informatica.unipr.it.Prometheus.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

import informatica.unipr.it.Prometheus.data.model.AppInfo;
import informatica.unipr.it.Prometheus.data.model.DataException;
import informatica.unipr.it.Prometheus.data.model.DataManager;

public class DatabaseManager implements DataManager {
    // COSTANTI
    public static final String DBNAME = "DATABASE";
    public static final String FIELD_TABLE = "APPINFO"; // costanti usate dall'helper per la creazione dei campi del DB
    public static final String FIELD_PACKAGE = "package_process_name";
    public static final String FIELD_USAGE = "time_usage";
    public static final String FIELD_MAXTIME = "max_time_usage";
    public static final String FIELD_TOTALUSAGE = "total_time_usage";
    public static final String FIELD_NUMBERMONITORINGDAY = "number_of_monitoring_day";
    public static final String FIELD_TRACKING = "tracking";
    public static final String FIELD_NOTIFYING = "notifying";
    public static final String FIELD_NOTIFYPRIORITY = "notify_priority";
    public static final String FIELD_NOTIFYTIME = "notify_time";
    private DatabaseHelper databaseHelper;
    private Context context;


    public DatabaseManager(Context ctx) {
        databaseHelper = new DatabaseHelper(ctx);  // se esiste gia non lo ricrea perche non c'è una "replace" nella query
        context = ctx;
    }
    //Non lo dichiaro fuori e non lo inizializzo in ogni funzione perche se esiste gia non lo ricreo ogni volta

    private boolean add(String packageProcessName, int timeUsage, int maxTime, int totalUsage, int numberMonitoringDay, boolean tracking, boolean notifying, int notify_priority, int notify_time) { // salva un nuovo package del processo
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(FIELD_PACKAGE, packageProcessName);
        cv.put(FIELD_USAGE, timeUsage);
        cv.put(FIELD_MAXTIME, maxTime);
        cv.put(FIELD_TOTALUSAGE, totalUsage);
        cv.put(FIELD_NUMBERMONITORINGDAY, numberMonitoringDay);

        // booleano per la classe ma intero per il DB... 0 falso 1 vero
        if (tracking == true) cv.put(FIELD_TRACKING, 1);
        else cv.put(FIELD_TRACKING, 0);

        if (notifying == true) cv.put(FIELD_NOTIFYING, 1);
        else cv.put(FIELD_NOTIFYING, 0);

        cv.put(FIELD_NOTIFYPRIORITY, notify_priority);
        cv.put(FIELD_NOTIFYTIME, notify_time);

        try {
            if (db.insert(FIELD_TABLE, null, cv) != -1) {
                db.close();
                return true;  // se l'inseriento va a buon fine torna vero
            }
            db.close();
            return false;   // se l'inserimento fallisce ritorna false al chiamante
        } catch (SQLiteException sqle) {
            db.close();
            return false;   // se arrivano eccezioni ritorna false
        }
    }

    private boolean remove(String packageProcessName) { // rimuove un package del processo dal db
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        try {
            if (db.delete(FIELD_TABLE, FIELD_PACKAGE + " = ?", new String[]{packageProcessName}) > 0) {
                db.close();
                return true;
            }
            db.close();
            return false;
        } catch (SQLiteException sqle) {
            db.close();
            return false;
        }
    }

    private Cursor query() throws DataException {
        // funzione che passando null come packageProcessName salva tutto il contenuto
        // della tabella per poi gestirla dal cursore, se si passa il parametro torna
        // la riga riferita a quel package del processo
        //SQLiteDatabase db = databaseHelper.getReadableDatabase();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor crs = null;
        try {
            crs = db.query(FIELD_TABLE, null, null, null, null, null, null); // resultset : tutto il contenuto
        } catch (SQLiteException sqle) {
            db.close();
            throw new DataException(sqle);
        }
        if (crs != null) crs.moveToFirst();
        db.close();
        return crs;     // non viene chiuso la risorsa cursore perche verra gestita dal chiamante, essendo il valore di ritorno
    }

    private Cursor query(String packageProcessName) throws DataException {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor crs = null;
        try {
            crs = db.query(FIELD_TABLE, null, FIELD_PACKAGE + " = ?", new String[]{packageProcessName}, null, null, null);
        } catch (SQLiteException sqle) {
            db.close();
            throw new DataException(sqle);
        }
        if (crs != null) crs.moveToFirst();
        db.close();
        return crs;
    }

    private boolean updateTimeUsage(String packageProcessName, int timeUsage) { // aggiorna il timeusage di un package del processo
        int tmpTime = readTimeUsage(packageProcessName);    // leggo quello locale per poterlo sommare
        int mt = readMaxTime(packageProcessName);
        int tu = readTotalUsage(packageProcessName);
        int nmd = readNumberMonitoringDay(packageProcessName);
        boolean t = isTracking(packageProcessName);
        boolean n = isNotifying(packageProcessName);
        int np = readNotifyPriority(packageProcessName);
        int nt = readNotifyTime(packageProcessName);

        if (remove(packageProcessName) == false)    // si salvano le variabili prima di fare la remove
            return false;    // se fallisce la remove quindi ritorna falso anche update ritorna falso
        return add(packageProcessName, tmpTime + timeUsage, mt, tu, nmd, t, n, np, nt);    // se la remove va in porto ma fallisce l'add ritorna falso l'update
    }

    private boolean updateMaxTime(String packageProcessName, int maxTime) { // aggiorna il timeusage di un package del processo
        int timeUsage = readTimeUsage(packageProcessName);
        int tu = readTotalUsage(packageProcessName);
        int nmd = readNumberMonitoringDay(packageProcessName);
        boolean t = isTracking(packageProcessName);
        boolean n = isNotifying(packageProcessName);
        int np = readNotifyPriority(packageProcessName);
        int nt = readNotifyTime(packageProcessName);


        if (remove(packageProcessName) == false)
            return false;    // se fallisce la remove quindi ritorna falso anche update ritorna falso
        return add(packageProcessName, timeUsage, maxTime, tu, nmd, t, n, np, nt);    // se la remove va in porto ma fallisce l'add ritorna falso l'update
    }

    private boolean updateTracking(String packageProcessName, boolean tracking) { // aggiorna il timeusage di un package del processo
        int timeUsage = readTimeUsage(packageProcessName);
        int mt = readMaxTime(packageProcessName);
        int tu = readTotalUsage(packageProcessName);
        int nmd = readNumberMonitoringDay(packageProcessName);
        boolean n;
        if (tracking)
            n = isNotifying(packageProcessName);  // se il tracciamento è falso anche la notifica è falsa
        else n = false;

        int np = readNotifyPriority(packageProcessName);
        int nt = readNotifyTime(packageProcessName);

        if (remove(packageProcessName) == false)
            return false;    // se fallisce la remove quindi ritorna falso anche update ritorna falso
        return add(packageProcessName, timeUsage, mt, tu, nmd, tracking, n, np, nt);    // se la remove va in porto ma fallisce l'add ritorna falso l'update
    }

    private boolean updateNotifying(String packageProcessName, boolean notifying) { // aggiorna il timeusage di un package del processo
        int timeUsage = readTimeUsage(packageProcessName);
        int mt = readMaxTime(packageProcessName);
        int tu = readTotalUsage(packageProcessName);
        int nmd = readNumberMonitoringDay(packageProcessName);
        boolean t = isTracking(packageProcessName);
        int np = readNotifyPriority(packageProcessName);
        int nt = readNotifyTime(packageProcessName);

        if (remove(packageProcessName) == false)
            return false;    // se fallisce la remove quindi ritorna falso anche update ritorna falso
        return add(packageProcessName, timeUsage, mt, tu, nmd, t, notifying, np, nt);    // se la remove va in porto ma fallisce l'add ritorna falso l'update
    }

    private boolean updateNotifyPriority(String packageProcessName, int notifyPriority) { // aggiorna il timeusage di un package del processo
        int timeUsage = readTimeUsage(packageProcessName);
        int mt = readMaxTime(packageProcessName);
        int tu = readTotalUsage(packageProcessName);
        int nmd = readNumberMonitoringDay(packageProcessName);
        boolean t = isTracking(packageProcessName);
        boolean n = isNotifying(packageProcessName);
        int nt = readNotifyTime(packageProcessName);

        if (remove(packageProcessName) == false)
            return false;    // se fallisce la remove quindi ritorna falso anche update ritorna falso
        return add(packageProcessName, timeUsage, mt, tu, nmd, t, n, notifyPriority, nt);    // se la remove va in porto ma fallisce l'add ritorna falso l'update
    }

    private boolean updateNotifyTime(String packageProcessName, int notifyTime) {
        int timeUsage = readTimeUsage(packageProcessName);
        int mt = readMaxTime(packageProcessName);
        int tu = readTotalUsage(packageProcessName);
        int nmd = readNumberMonitoringDay(packageProcessName);
        boolean t = isTracking(packageProcessName);
        boolean n = isNotifying(packageProcessName);
        int np = readNotifyPriority(packageProcessName);

        if (remove(packageProcessName) == false)
            return false;    // se fallisce la remove quindi ritorna falso anche update ritorna falso
        return add(packageProcessName, timeUsage, mt, tu, nmd, t, n, np, notifyTime);
    }

    @Override
    public List<AppInfo> refreshApp() {
        List<AppInfo> appInfoList = new ArrayList<>();
        Cursor crs = null;
        try {
            crs = query();
        } catch (DataException e) {
            return appInfoList;
        }

        while (!crs.isAfterLast()) {
            String packageProcessName = crs.getString(crs.getColumnIndex(FIELD_PACKAGE));
            int timeUsage = crs.getInt(crs.getColumnIndex(FIELD_USAGE));
            int maxTimeUsage = crs.getInt(crs.getColumnIndex(FIELD_MAXTIME));
            int totalTimeUsage = crs.getInt(crs.getColumnIndex(FIELD_TOTALUSAGE));
            int numberOfMonitoringDay = crs.getInt(crs.getColumnIndex(FIELD_NUMBERMONITORINGDAY));
            boolean tracking = (crs.getInt(crs.getColumnIndex(FIELD_TRACKING)) == 1);
            boolean notifying = (crs.getInt(crs.getColumnIndex(FIELD_NOTIFYING)) == 1);
            int notifyPriority = crs.getInt(crs.getColumnIndex(FIELD_NOTIFYPRIORITY));
            int notifyTime = crs.getInt(crs.getColumnIndex(FIELD_NOTIFYTIME));

            appInfoList.add(new AppInfo(packageProcessName, timeUsage, maxTimeUsage, totalTimeUsage, numberOfMonitoringDay, tracking, notifying, notifyPriority, notifyTime));
            crs.moveToNext();
        }
        return appInfoList;
    }

    public boolean loadTimeUsage(String packageProcessName, int timeUsage) {  // add che tiene conto dell'elemento gia presente, cioè gestisce quando chiamare l'add o l'update
        if (isExist(packageProcessName))
            return updateTimeUsage(packageProcessName, timeUsage);   // se esiste aggiunge il tempo a quello esistente, e ritorna vero o falso in base a quello che ritorna l'update
        return add(packageProcessName, timeUsage, 0, 0, 0, false, false, 1, 0); // se non esiste lo crea da nuovo, e ritorna vero o falso in base a quello che ritorna l'add
    }

    public boolean loadMaxTime(String packageProcessName, int maxTime) {
        if (isExist(packageProcessName))
            return updateMaxTime(packageProcessName, maxTime); // se esiste gia aggiorna solo il massimo
        return add(packageProcessName, 0, maxTime, 0, 0, true, false, 1, 0);
    }

    public boolean loadTracking(String packageProcessName, boolean tracking) {
        if (isExist(packageProcessName))
            return updateTracking(packageProcessName, tracking); // se esiste gia aggiorna solo il massimo
        return add(packageProcessName, 0, 0, 0, 0, tracking, false, 1, 0);
    }

    public boolean loadNotifying(String packageProcessName, boolean notifying) {
        if (isExist(packageProcessName))
            return updateNotifying(packageProcessName, notifying); // se esiste gia aggiorna solo il massimo
        return add(packageProcessName, 0, 7200, 0, 0, true, notifying, 1, 0);  // 7200 secondi per formare 2 ore
    }

    public boolean loadNotifyPriority(String packageProcessName, int notifyPriority) {
        if (isExist(packageProcessName))
            return updateNotifyPriority(packageProcessName, notifyPriority); // se esiste gia aggiorna solo il massimo
        return add(packageProcessName, 0, 7200, 0, 0, true, false, notifyPriority, 0);
    }

    @Override
    public boolean loadNotifyTime(String packageProcessName, int notifyTime) {
        if (isExist(packageProcessName))
            return updateNotifyTime(packageProcessName, notifyTime); // se esiste gia aggiorna solo il massimo
        return add(packageProcessName, 0, 7200, 0, 0, true, false, 1, notifyTime);
    }


    public int readTimeUsage(String packageProcessName) {   // legge il time usage dell'app richiesta
        if (isExist(packageProcessName)) {  // se il processo esiste nel DB
            Cursor crs = null;
            int rtu = 0;
            try {
                crs = query(packageProcessName);
            } catch (DataException e) {
                return 0;
            }
            if (crs.getCount() != 0) {
                rtu = crs.getInt(crs.getColumnIndex(FIELD_USAGE));
            }
            crs.close();
            return rtu;
        }
        return 0;
    }

    public int readMaxTime(String packageProcessName) {
        if (isExist(packageProcessName)) {
            Cursor crs = null;
            int rmt = 0;

            try {
                crs = query(packageProcessName);
            } catch (DataException e) {
                return 0;
            }
            if (crs.getCount() != 0) {
                rmt = crs.getInt(crs.getColumnIndex(FIELD_MAXTIME));
            }
            crs.close();
            return rmt;
        }
        return 0;
    }

    public int readTotalUsage(String packageProcessName) {
        if (isExist(packageProcessName)) {
            Cursor crs = null;
            int rtu = 0;
            try {
                crs = query(packageProcessName);
            } catch (DataException e) {
                return 0;
            }
            if (crs.getCount() != 0) {
                 rtu = crs.getInt(crs.getColumnIndex(FIELD_TOTALUSAGE));
            }
            crs.close();
            return rtu;
        }
        return 0;
    }

    public int readNumberMonitoringDay(String packageProcessName) {
        if (isExist(packageProcessName)) {
            Cursor crs = null;
            int rnmd = 0;
            try {
                crs = query(packageProcessName);
            } catch (DataException e) {
                return 0;
            }
            if (crs.getCount() != 0) {
               rnmd = crs.getInt(crs.getColumnIndex(FIELD_NUMBERMONITORINGDAY));
            }
            crs.close();
            return rnmd;
        }
        return 0;
    }

    public boolean isTracking(String packageProcessName) {
        if (isExist(packageProcessName)) {
            Cursor crs = null;
            try {
                crs = query(packageProcessName);
            } catch (DataException e) {
                return false;
            }
            if (crs.getCount() != 0) {
                if (crs.getInt(crs.getColumnIndex(FIELD_TRACKING)) == 1) {
                    crs.close();
                    return true;
                }
            }
            crs.close();
        }
        return false;
    }

    public boolean isNotifying(String packageProcessName) {
        if (isExist(packageProcessName)) {
            Cursor crs = null;
            try {
                crs = query(packageProcessName);
            } catch (DataException e) {
                return false;
            }
            if (crs.getCount() != 0) {
                if (crs.getInt(crs.getColumnIndex(FIELD_NOTIFYING)) == 1) {
                    crs.close();
                    return true;
                }
            }
            crs.close();
        }
        return false;
    }

    public int readNotifyPriority(String packageProcessName) {
        if (isExist(packageProcessName)) {
            Cursor crs = null;
            int rnp = 0;
            try {
                crs = query(packageProcessName);
            } catch (DataException e) {
                return 0;
            }

            if (crs.getCount() != 0) {
                rnp = crs.getInt(crs.getColumnIndex(FIELD_NOTIFYPRIORITY));
            }

            crs.close();
            return rnp;
        }
        return 0;
    }

    @Override
    public int readNotifyTime(String packageProcessName) {
        if (isExist(packageProcessName)) {
            Cursor crs = null;
            int rnp = 0;
            try {
                crs = query(packageProcessName);
            } catch (DataException e) {
                return 0;
            }
            if (crs.getCount() != 0) {
                rnp = crs.getInt(crs.getColumnIndex(FIELD_NOTIFYTIME));
            }
            crs.close();
            return rnp;
        }
        return 0;
    }


    public boolean isExist(String packageProcessName) {   // essendo chiave primaria il nome si fa riferimento su quello
        Cursor crs = null;
        try {
            crs = query(packageProcessName);
        } catch (DataException e) {
            return false;
        }
        if (crs != null) {
            if (crs.getCount() != 0) {
                crs.close();
                return true; // se non ci sono righe nel db allora non esiste
            }
        }
        if (crs != null) {
            crs.close();
        }
        return false;
    }

    public boolean incrementDay() { // TODO considerare se si incrementa e decrementa manualmente la data...quindi prendere cambiamento e gestirlo
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor crs = null;
        try {
            crs = query();
        } catch (DataException e) {
            db.close();
            return false;
        }
        if (crs != null) {
            while (!crs.isAfterLast()) { // after last per permettergli di fare i calcoli anche sull'ultimo
                String packageProcessName = crs.getString(crs.getColumnIndex(FIELD_PACKAGE));
                int timeUsage = readTimeUsage(packageProcessName);    // leggo quello locale per poterlo sommare
                int mt = readMaxTime(packageProcessName);
                int tu = readTotalUsage(packageProcessName);
                int nmd = readNumberMonitoringDay(packageProcessName);
                boolean t = isTracking(packageProcessName);
                boolean n = isNotifying(packageProcessName);
                int np = readNotifyPriority(packageProcessName);

                if (remove(packageProcessName) == false) {   // si salvano le variabili prima di fare la remove
                    crs.close();
                    db.close();
                    return false;    // se fallisce la remove quindi ritorna falso anche update ritorna falso
                }
                add(packageProcessName, 0, mt, tu + timeUsage, nmd + 1, t, n, np, 0);    // se la remove va in porto ma fallisce l'add ritorna falso l'update

                crs.moveToNext();   // va al successivo
            }
        }
        if (crs != null) {
            crs.close();
        }
        db.close();
        return true;
    }
}
