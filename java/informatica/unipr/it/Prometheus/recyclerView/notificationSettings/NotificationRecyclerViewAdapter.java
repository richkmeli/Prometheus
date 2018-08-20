package informatica.unipr.it.Prometheus.recyclerView.notificationSettings;

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

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationViewHolder> implements ListRemover {


    public static List<StatisticsObject> statistics;
    private Context context;
    private int pageIndex;

    //Costruttore
    public NotificationRecyclerViewAdapter(List<StatisticsObject> statistics, Context context, int pageIndex) {
        this.statistics = statistics;
        this.context = context;
        this.pageIndex = pageIndex;
    }

    //Per rimuovere elemento
    @Override
    public void removeItem(int position) {
        statistics.remove(position);
        notifyItemRemoved(position);
    }

    //Passo il layout della row
    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_show_item, parent, false);
        return new NotificationViewHolder(view, context, this, pageIndex);
    }

    @Override
    public int getItemCount() {
        return statistics.size();
    }


    //Funzione che rimpiazza cioò che sta nelle view
    //Setto in base all'oggetto del numero corrispondente nella lista
    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            NotificationViewHolder notificationViewHolder = holder;
            try {
                notificationViewHolder.setIconAndTime(statistics.get(position));
            } catch (PackageManager.NameNotFoundException e) {
                //e.printStackTrace();
            }
        }
    }


}