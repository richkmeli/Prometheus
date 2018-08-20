package informatica.unipr.it.Prometheus.recyclerView.statisticsAdd;


import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.recyclerView.ListRemover;
import informatica.unipr.it.Prometheus.recyclerView.StatisticsObject;

//Uso un mio view holder
public class StatisticsAddAdapterRV extends RecyclerView.Adapter<StatisticsAddViewHolder> implements ListRemover {
    public static List<StatisticsObject> statistics;
    private Context context;

    //Costruttore
    public StatisticsAddAdapterRV(List<StatisticsObject> statistics, Context context) {
        this.statistics = statistics;
        this.context = context;
    }

    //Per rimuovere elemento
    @Override
    public void removeItem(int position) {
        if (position != -1) {
            statistics.remove(position);
            notifyItemRemoved(position);
        }
    }

    //Passo il layout della row
    @Override
    public StatisticsAddViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_add_item, parent, false);
        return new StatisticsAddViewHolder(view, context, this);
    }

    @Override
    public int getItemCount() {
        return statistics.size();
    }


    //Funzione che rimpiazza cioò che sta nelle view
    //Setto in base all'oggetto del numero corrispondente nella lista
    @Override
    public void onBindViewHolder(StatisticsAddViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            StatisticsAddViewHolder statisticsAddViewHolder = holder;
            try {
                statisticsAddViewHolder.setIconAndTime(statistics.get(position));
            } catch (PackageManager.NameNotFoundException e) {
                //e.printStackTrace();
            }
        }
    }


}
