package informatica.unipr.it.Prometheus.recyclerView;

import android.graphics.drawable.Drawable;


public class StatisticsObject {

    private Drawable icon;
    private String usage;
    private int time;

    public StatisticsObject() {
        this.icon = null;
        this.usage = null;
        this.time = 0;
    }

    public StatisticsObject(Drawable icon, String usage, int time) {
        this.icon = icon;
        this.usage = usage;
        this.time = time;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
