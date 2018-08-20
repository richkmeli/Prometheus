package informatica.unipr.it.Prometheus.fragment;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;


public class MyValueFormatter implements ValueFormatter {

    int totalTime;

    public MyValueFormatter(int totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

//        return ((int) value / 60 + "' " + "(" + (int) (value * 100 / totalTime) + "%)");
        return ((int) (value * 100 / totalTime) + "%");
    }
}