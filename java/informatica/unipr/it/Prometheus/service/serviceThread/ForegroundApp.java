package informatica.unipr.it.Prometheus.service.serviceThread;

import android.annotation.TargetApi;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import java.util.LinkedList;

public class ForegroundApp {
    private static LinkedList<String> linkedListApp = new LinkedList<>();
    private Context context;
    //  static int i = 0;//////////////////////////////////////////////// // TODO: RichkDEBUG

    public ForegroundApp(Context contextTmp) {
        context = contextTmp;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String foregroundApp() {
        String foregroundApp = null;

        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis(); // prende l'istante attuale
        UsageEvents eventList = usm.queryEvents(time - 1000 * 1000, time);

        if (eventList != null) {
            while (eventList.hasNextEvent()) {
                UsageEvents.Event e = new UsageEvents.Event();
                eventList.getNextEvent(e);
                if (e.getEventType() == 1) {
                    linkedListApp.add(e.getPackageName());
                }
            }
        }

        if (!linkedListApp.isEmpty()) {
            foregroundApp = linkedListApp.getLast();
        }
/*
        //////////////////////////////////////////////// // TODO: RichkDEBUG
        i++;
        final LinkedList<String> a = linkedListApp;
        if (i % 3 == 0) {
            Handler handler = new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!linkedListApp.isEmpty())
                        Toast.makeText(context, "appfore : " + a.getLast(), Toast.LENGTH_SHORT).show();
                }

            })
            ;/////////////////////////////////////////////////////////////////////////////////////////////////////
        }
*/
        return foregroundApp;
    }
}
