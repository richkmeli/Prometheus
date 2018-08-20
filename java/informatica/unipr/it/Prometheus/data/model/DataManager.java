package informatica.unipr.it.Prometheus.data.model;

import java.util.List;

public interface DataManager {
    List<AppInfo> refreshApp();

    boolean loadTimeUsage(String packageProcessName, int timeUsage);

    boolean loadMaxTime(String packageProcessName, int maxTime);

    boolean loadTracking(String packageProcessName, boolean tracking);

    boolean loadNotifying(String packageProcessName, boolean notifying);

    boolean loadNotifyPriority(String packageProcessName, int notifyPriority);

    boolean loadNotifyTime(String packageProcessName, int notifyTime);

    int readTimeUsage(String packageProcessName);

    int readMaxTime(String packageProcessName);

    int readTotalUsage(String packageProcessName);

    int readNumberMonitoringDay(String packageProcessName);

    boolean isTracking(String packageProcessName);

    boolean isNotifying(String packageProcessName);

    int readNotifyPriority(String packageProcessName);

    int readNotifyTime(String packageProcessName);

    boolean isExist(String packageProcessName);

    boolean incrementDay();

}