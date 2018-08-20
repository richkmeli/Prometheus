package informatica.unipr.it.Prometheus.data.model;

public class DataException extends Exception {
    public DataException(Throwable throwable) {
        super(throwable);
    }

    public DataException(String detailMessage) {
        super(detailMessage);
    }
}
