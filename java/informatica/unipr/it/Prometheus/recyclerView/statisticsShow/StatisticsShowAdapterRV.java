package informatica.unipr.it.Prometheus.recyclerView.statisticsShow;

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

//Uso un mio view holder g
public class StatisticsShowAdapterRV extends RecyclerView.Adapter<StatisticsShowViewHolder> implements ListRemover {
    private final List<StatisticsObject> statistics;
    private final Context context;
    public PackageManager pm;

    //Costruttore
    public StatisticsShowAdapterRV(List<StatisticsObject> statistics, Context context) {
        this.statistics = statistics;
        this.context = context;
        pm = context.getPackageManager();
    }

    //Per rimuovere elemento
    @Override
    public void removeItem(int position) {
        statistics.remove(position);
        notifyItemRemoved(position);
    }

    //Passo il layout della row
    @Override
    public StatisticsShowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_show_item, parent, false);
        return new StatisticsShowViewHolder(view, context, this);
    }

    @Override
    public int getItemCount() {
        return statistics.size();
    }


    //Funzione che rimpiazza cioò che sta nelle view
    //Setto in base all'oggetto del numero corrispondente nella lista
    @Override
    public void onBindViewHolder(StatisticsShowViewHolder holder, final int position) {
        if (getItemViewType(position) == 0) {
            StatisticsShowViewHolder statisticsShowViewHolder = holder;
            try {
                statisticsShowViewHolder.setIconAndTime(statistics.get(position));

            } catch (PackageManager.NameNotFoundException e) {
                //e.printStackTrace();
            }

        }
    }
}