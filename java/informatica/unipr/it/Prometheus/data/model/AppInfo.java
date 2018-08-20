package informatica.unipr.it.Prometheus.data.model;

/*
* modello di dati dell'applicazione con i campi costanti contenenti i nomi
* degli attributi per la costruzione del DB da parte del DatabaseHelper
* */

public class AppInfo {
    private String packageProcessName;
    private int timeUsage;
    private int maxTimeUsage;
    private int totalTimeUsage;
    private int numberOfMonitoringDay;
    private boolean tracking;
    private boolean notifying;
    private int notifyPriority;
    private int notifyTime;


    public AppInfo(String packageProcessName, int timeUsage, int maxTimeUsage, int totalTimeUsage, int numberOfMonitoringDay, boolean tracking, boolean notifying, int notifyPriority, int notifyTime) {
        this.packageProcessName = packageProcessName;
        this.timeUsage = timeUsage;
        this.maxTimeUsage = maxTimeUsage;
        this.totalTimeUsage = totalTimeUsage;
        this.numberOfMonitoringDay = numberOfMonitoringDay;
        this.tracking = tracking;
        this.notifying = notifying;
        this.notifyPriority = notifyPriority;
        this.notifyTime = notifyTime;
    }

    public String getPackageProcessName() {
        return packageProcessName;
    }

    public void setPackageProcessName(String packageProcessName) {
        this.packageProcessName = packageProcessName;
    }

    public int getTimeUsage() {
        return timeUsage;
    }

    public void setTimeUsage(int timeUsage) {
        this.timeUsage = timeUsage;
    }

    public int getMaxTimeUsage() {
        return maxTimeUsage;
    }

    public void setMaxTimeUsage(int maxTimeUsage) {
        this.maxTimeUsage = maxTimeUsage;
    }

    public int getTotalTimeUsage() {
        return totalTimeUsage;
    }

    public void setTotalTimeUsage(int totalTimeUsage) {
        this.totalTimeUsage = totalTimeUsage;
    }

    public int getNumberOfMonitoringDay() {
        return numberOfMonitoringDay;
    }

    public void setNumberOfMonitoringDay(int numberOfMonitoringDay) {
        this.numberOfMonitoringDay = numberOfMonitoringDay;
    }

    public boolean isTracking() {
        return tracking;
    }

    public void setTracking(boolean tracking) {
        this.tracking = tracking;
    }

    public boolean isNotifying() {
        return notifying;
    }

    public void setNotifying(boolean notifying) {
        this.notifying = notifying;
    }

    public int getNotifyPriority() {
        return notifyPriority;
    }

    public void setNotifyPriority(int notifyPriority) {
        this.notifyPriority = notifyPriority;
    }

    public int getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(int notifyTime) {
        this.notifyTime = notifyTime;
    }
}
