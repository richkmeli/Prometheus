package informatica.unipr.it.Prometheus.data;

import android.content.Context;

import informatica.unipr.it.Prometheus.data.database.DatabaseManager;
import informatica.unipr.it.Prometheus.data.model.DataManager;

public class DataChooser {

    private DataChooser() {
    }

    public static DataManager getDataManager(Context context) {
        /*  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return new AppUsageManager(context,false);
        }else{
         */
        return new DatabaseManager(context);
        // }
    }
}
