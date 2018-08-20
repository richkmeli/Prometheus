package informatica.unipr.it.Prometheus.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* classe per la creazione e modifica della struttura del database
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        /* invocazione del costruttore della classe base con il nome(DBNAME) del database
           e versione database(1)
         */
        super(context, DatabaseManager.DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) // Creazione database
    {
        String q = "CREATE TABLE " + DatabaseManager.FIELD_TABLE +
                " ( " +
                DatabaseManager.FIELD_PACKAGE + " TEXT PRIMARY KEY," +
                DatabaseManager.FIELD_USAGE + " INTEGER, " +
                DatabaseManager.FIELD_MAXTIME + " INTEGER, " +
                DatabaseManager.FIELD_TOTALUSAGE + " INTEGER, " +
                DatabaseManager.FIELD_NUMBERMONITORINGDAY + " INTEGER, " +
                DatabaseManager.FIELD_TRACKING + " INTEGER, " +
                DatabaseManager.FIELD_NOTIFYING + " INTEGER, " +
                DatabaseManager.FIELD_NOTIFYPRIORITY + " INTEGER, " +
                DatabaseManager.FIELD_NOTIFYTIME + " INTEGER );";
        db.execSQL(q);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* Modifica Database :viene invocato nel momento in cui si richiede una
           versione del
           database più aggiornata di quella presente su disco
         */
    }

}
