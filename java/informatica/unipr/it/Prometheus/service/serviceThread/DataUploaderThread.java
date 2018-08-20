package informatica.unipr.it.Prometheus.service.serviceThread;


import android.content.Context;

import java.util.ArrayList;
import java.util.Vector;

import informatica.unipr.it.Prometheus.data.database.DatabaseManager;
import informatica.unipr.it.Prometheus.data.model.DataManager;

public class DataUploaderThread extends Thread {
    private ArrayList<String> usageList = new ArrayList<>();
    private Context context;

    public DataUploaderThread(Context contextTmp, ArrayList<String> usageListTmp) {
        context = contextTmp;
        usageList.addAll(usageListTmp);     // visto che l'array viene passato per riferimento, con il metodo addAll aggiungiamo uno ad uno gli elementi in un altra struttura. in modo che quando viene chiamato lo start, non faccia riferimento alla struttura passata per rfferimento, che facendo un clear toglie gli elementi quindi nel nostro run non vediamo piu gli elementi
    }

    @Override
    public void run() {
        Vector<String> packageToDB = new Vector<>();
        Vector<Integer> timeToDB = new Vector<>(30);
        boolean processAlreadyExists = false;

        DataManager dataManager = new DatabaseManager(context);

 /*       //////////////////////////////////////////////// // TODO: RichkDEBUG
        Handler handler = new Handler(context.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                HashSet<String> s = new HashSet<String>();
                for (String s1 : usageList) {
                    s.add(s1);
                }
                Toast.makeText(context, "RichkDEBUG : " + usageList.size() + s, Toast.LENGTH_LONG).show();
            }
        });/////////////////////////////////////////////////////////////////////////////////////////////////////
*/
        for (int i = 0; i < 30; ++i) {
            timeToDB.add(0);
        }

        //Inserisco nel vector packageToDB i nomi dei package
        //timeToDB i tempi alla stessa posizione del nome
        for (int j = 0; j < usageList.size(); ++j) {
            int pos = 0;

            for (int i = 0; i < packageToDB.size(); ++i) {
                if (usageList.get(j).equals(packageToDB.get(i))) {
                    processAlreadyExists = true;
                    pos = i;
                }
            }
            if (processAlreadyExists) {
                int tmp = timeToDB.get(pos);
                timeToDB.set(pos, (tmp + 1));
            } else {
                int vSize = packageToDB.size();
                packageToDB.add(usageList.get(j));
                timeToDB.set(vSize, 1);
            }
            processAlreadyExists = false;
        }

        for (int i = 0; i < packageToDB.size(); ++i) {
            dataManager.loadTimeUsage(packageToDB.get(i), timeToDB.get(i));
        }
        return;
    }
}
