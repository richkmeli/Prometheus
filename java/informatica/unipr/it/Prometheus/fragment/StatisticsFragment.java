package informatica.unipr.it.Prometheus.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.data.database.DatabaseManager;
import informatica.unipr.it.Prometheus.data.model.AppInfo;
import informatica.unipr.it.Prometheus.data.model.DataManager;
import informatica.unipr.it.Prometheus.settings.SettingsActivity;
import pl.droidsonroids.gif.GifImageView;


public class StatisticsFragment extends Fragment {
    private DataManager dataManager;
    private PackageManager packageManager;
    private ProgressBar loadingStatistics;
    private TableLayout tableLayout;
    private TableLayout tableLayoutIntestazione;
    private List<AppInfo> appsStatistics;
    //  private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handlerTable;
    private Thread threadLoadingData;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataManager = new DatabaseManager(getActivity());

        appsStatistics = new ArrayList<>();

        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        tableLayout = (TableLayout) rootView.findViewById(R.id.table);
        tableLayoutIntestazione = (TableLayout) rootView.findViewById(R.id.tableIntestazione);
        //  swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefresh);
        setHasOptionsMenu(true);


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.statistics_table_title));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        buildTable();
    }

    void buildTable() {
        loadingStatistics = (ProgressBar) getActivity().findViewById(R.id.loadingStatistics);
        handlerTable = new Handler(getActivity().getMainLooper());
        tableLayout.removeAllViews();   // rimuove tutti gli elementi per evitare di appendere piu tabelle
        tableLayoutIntestazione.removeAllViews();


        final TableRow tableRowIntestazione = new TableRow(getContext());

        final TextView textViewRankIntestazione = new TextView(getContext());
        final TextView textViewApplicationNameIntestazione = new TextView(getContext());
        final TextView textVieTimeUsageIntestazione = new TextView(getContext());
        final TextView textViewMaxTimeIntestazione = new TextView(getContext());
        final TextView textViewTotalUsageIntestazione = new TextView(getContext());
        final TextView textViewNumberMonitoringDayIntestazione = new TextView(getContext());
        final TextView textViewIsNotifyingIntestazione = new TextView(getContext());
        final TextView textViewIsTrackingIntestazione = new TextView(getContext());
        final TextView textViewNotifyPriorityIntestazione = new TextView(getContext());
        final TextView textViewNotifyTimeIntestazione = new TextView(getContext());

        textViewRankIntestazione.setPadding(10, 10, 10, 10);
        textViewApplicationNameIntestazione.setPadding(10, 10, 10, 10);
        textVieTimeUsageIntestazione.setPadding(10, 10, 10, 10);
        textViewMaxTimeIntestazione.setPadding(10, 10, 10, 10);
        textViewTotalUsageIntestazione.setPadding(10, 10, 10, 10);
        textViewNumberMonitoringDayIntestazione.setPadding(10, 10, 10, 10);
        textViewIsNotifyingIntestazione.setPadding(10, 10, 10, 10);
        textViewIsTrackingIntestazione.setPadding(10, 10, 10, 10);
        textViewNotifyPriorityIntestazione.setPadding(10, 10, 10, 10);
        textViewNotifyTimeIntestazione.setPadding(10, 10, 10, 10);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textViewRankIntestazione.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textViewApplicationNameIntestazione.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textVieTimeUsageIntestazione.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textViewMaxTimeIntestazione.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textViewTotalUsageIntestazione.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textViewNumberMonitoringDayIntestazione.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textViewIsNotifyingIntestazione.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textViewIsTrackingIntestazione.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textViewNotifyPriorityIntestazione.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textViewNotifyTimeIntestazione.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }

        textViewRankIntestazione.setText(getResources().getString(R.string.statistics_rank) + " |");
        textViewApplicationNameIntestazione.setText(getResources().getString(R.string.statistics_app_name) + " |");
        textVieTimeUsageIntestazione.setText(getResources().getString(R.string.statistics_time_usage) + " |");
        textViewMaxTimeIntestazione.setText(getResources().getString(R.string.statistics_max_time) + " |");
        textViewTotalUsageIntestazione.setText(getResources().getString(R.string.statistics_total_usage) + " |");
        textViewNumberMonitoringDayIntestazione.setText(getResources().getString(R.string.statistics_number_if_monitoring_day) + " |");
        textViewIsNotifyingIntestazione.setText(getResources().getString(R.string.statistics_is_notifying) + " |");
        textViewIsTrackingIntestazione.setText(getResources().getString(R.string.statistics_is_tracking) + " |");
        textViewNotifyPriorityIntestazione.setText(getResources().getString(R.string.statistics_priority) + " |");
        textViewNotifyTimeIntestazione.setText(getResources().getString(R.string.statistics_notify_time));

        tableRowIntestazione.setBackgroundColor(Color.BLACK);
        textViewRankIntestazione.setTextColor(Color.WHITE);
        textViewApplicationNameIntestazione.setTextColor(Color.WHITE);
        textVieTimeUsageIntestazione.setTextColor(Color.WHITE);
        textViewMaxTimeIntestazione.setTextColor(Color.WHITE);
        textViewTotalUsageIntestazione.setTextColor(Color.WHITE);
        textViewNumberMonitoringDayIntestazione.setTextColor(Color.WHITE);
        textViewIsNotifyingIntestazione.setTextColor(Color.WHITE);
        textViewIsTrackingIntestazione.setTextColor(Color.WHITE);
        textViewNotifyPriorityIntestazione.setTextColor(Color.WHITE);
        textViewNotifyTimeIntestazione.setTextColor(Color.WHITE);

        tableRowIntestazione.addView(textViewRankIntestazione);
        tableRowIntestazione.addView(textViewApplicationNameIntestazione);
        tableRowIntestazione.addView(textVieTimeUsageIntestazione);
        tableRowIntestazione.addView(textViewMaxTimeIntestazione);
        tableRowIntestazione.addView(textViewTotalUsageIntestazione);
        tableRowIntestazione.addView(textViewNumberMonitoringDayIntestazione);
        tableRowIntestazione.addView(textViewIsNotifyingIntestazione);
        tableRowIntestazione.addView(textViewIsTrackingIntestazione);
        tableRowIntestazione.addView(textViewNotifyPriorityIntestazione);
        tableRowIntestazione.addView(textViewNotifyTimeIntestazione);

        tableLayoutIntestazione.addView(tableRowIntestazione);


        threadLoadingData = new Thread(new Runnable() {
            @Override
            public void run() {

                Comparator<AppInfo> appComparator = new Comparator<AppInfo>() {
                    @Override
                    public int compare(AppInfo a1, AppInfo a2) {
                        return (Integer.valueOf(a2.getTimeUsage())).compareTo(a1.getTimeUsage());
                    }
                };

                packageManager = getActivity().getPackageManager();
                appsStatistics.clear();
                appsStatistics = dataManager.refreshApp();

                Collections.sort(appsStatistics, appComparator);

                final TableLayout tableLayoutTMP = new TableLayout(getActivity());
                int rank = 1;
                for (AppInfo app : appsStatistics) {
                    TableRow tableRow = new TableRow(context);

                    final SharedPreferences avatarSharedPreferences = context.getSharedPreferences("Avatar", getActivity().MODE_PRIVATE);
                    final String avatarTheme = avatarSharedPreferences.getString("avatarTheme", "PromTheme");
                    if (avatarTheme.equals("DogTheme")) {
                        if (rank % 2 == 0) {
                            tableRow.setBackgroundColor(Color.BLACK);
                        } else {
                            tableRow.setBackgroundColor(Color.DKGRAY);
                        }
                    } else {
                        if (rank % 2 == 0) {
                            tableRow.setBackgroundColor(Color.LTGRAY);
                        } else {
                            tableRow.setBackgroundColor(Color.WHITE);
                        }
                    }

                    final TextView textViewRank = new TextView(context);
                    final TextView textViewApplicationName = new TextView(context);
                    final TextView textVieTimeUsage = new TextView(context);
                    final TextView textViewMaxTime = new TextView(context);
                    final TextView textViewTotalUsage = new TextView(context);
                    final TextView textViewNumberMonitoringDay = new TextView(context);
                    final TextView textViewIsNotifying = new TextView(context);
                    final TextView textViewIsTracking = new TextView(context);
                    final TextView textViewNotifyPriority = new TextView(context);
                    final TextView textViewNotifyTime = new TextView(context);

                    textViewRank.setPadding(10, 10, 10, 10);
                    textViewApplicationName.setPadding(10, 10, 10, 10);
                    textVieTimeUsage.setPadding(10, 10, 10, 10);
                    textViewMaxTime.setPadding(10, 10, 10, 10);
                    textViewTotalUsage.setPadding(10, 10, 10, 10);
                    textViewNumberMonitoringDay.setPadding(10, 10, 10, 10);
                    textViewIsNotifying.setPadding(10, 10, 10, 10);
                    textViewIsTracking.setPadding(10, 10, 10, 10);
                    textViewNotifyPriority.setPadding(10, 10, 10, 10);
                    textViewNotifyTime.setPadding(10, 10, 10, 10);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        textViewRank.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textViewApplicationName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textVieTimeUsage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textViewMaxTime.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textViewTotalUsage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textViewNumberMonitoringDay.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textViewIsNotifying.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textViewIsTracking.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textViewNotifyPriority.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        textViewNotifyTime.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }

                    textViewRank.setWidth(textViewRankIntestazione.getWidth());
                    textViewApplicationName.setWidth(textViewApplicationNameIntestazione.getWidth());
                    textVieTimeUsage.setWidth(textVieTimeUsageIntestazione.getWidth());
                    textViewMaxTime.setWidth(textViewMaxTimeIntestazione.getWidth());
                    if (textVieTimeUsageIntestazione.getWidth() > textVieTimeUsage.getWidth())
                        textViewTotalUsage.setWidth(textViewTotalUsageIntestazione.getWidth());
                    textViewNumberMonitoringDay.setWidth(textViewNumberMonitoringDayIntestazione.getWidth());
                    textViewIsNotifying.setWidth(textViewIsNotifyingIntestazione.getWidth());
                    textViewIsTracking.setWidth(textViewIsTrackingIntestazione.getWidth());
                    textViewNotifyPriority.setWidth(textViewNotifyPriorityIntestazione.getWidth());
                    textViewNotifyTime.setWidth(textViewNotifyTimeIntestazione.getWidth());

                    String s = "";
                    s = rank + "";
                    textViewRank.setText(s);
                    String processName = null;
                    try {
                        processName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(app.getPackageProcessName(), PackageManager.GET_META_DATA));
                    } catch (PackageManager.NameNotFoundException e) {
                        processName = (app.getPackageProcessName()).substring(app.getPackageProcessName().lastIndexOf('.') + 1, app.getPackageProcessName().length()) + " (" + getResources().getString(R.string.statistics_table_app_name_deleted) + ")";
                        textViewApplicationName.setPaintFlags(textViewApplicationName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    textViewApplicationName.setText(processName);
                    s = app.getTimeUsage() / 3600 + context.getResources().getString(R.string.hour_short) + " :" + (app.getTimeUsage() / 60) % 60 + context.getResources().getString(R.string.minute_short);
                    textVieTimeUsage.setText(s);
                    s = app.getMaxTimeUsage() / 3600 + context.getResources().getString(R.string.hour_short) + " :" + (app.getMaxTimeUsage() / 60) % 60 + context.getResources().getString(R.string.minute_short);
                    textViewMaxTime.setText(s);
                    s = app.getTotalTimeUsage() / 3600 + context.getResources().getString(R.string.hour_short) + " :" + (app.getTotalTimeUsage() / 60) % 60 + context.getResources().getString(R.string.minute_short);
                    textViewTotalUsage.setText(s);
                    s = app.getNumberOfMonitoringDay() + "";
                    textViewNumberMonitoringDay.setText(s);

                    if (!app.isNotifying()) {
                        textViewIsNotifying.setText(context.getResources().getString(R.string.false_short));
                        textViewIsNotifying.setTextColor(Color.RED);
                    } else {
                        textViewIsNotifying.setText(context.getResources().getString(R.string.true_short));
                        textViewIsNotifying.setTextColor(Color.parseColor("#01579b"));
                    }

                    if (!app.isTracking()) {
                        textViewIsTracking.setText(context.getResources().getString(R.string.false_short));
                        textViewIsTracking.setTextColor(Color.RED);
                    } else {
                        textViewIsTracking.setText(context.getResources().getString(R.string.true_short));
                        textViewIsTracking.setTextColor(Color.parseColor("#01579b"));
                    }

                    if (app.getNotifyPriority() == 1) {
                        textViewNotifyPriority.setText(context.getResources().getString(R.string.statistics_table_priority_low)); // TODO metti strighe low priority...
                        textViewNotifyPriority.setBackgroundColor(Color.parseColor("#64DD17"));
                    } else if (app.getNotifyPriority() == 2) {
                        textViewNotifyPriority.setText(context.getResources().getString(R.string.statistics_table_priority_mid));
                        textViewNotifyPriority.setBackgroundColor(Color.parseColor("#FFEB3B"));
                    } else if (app.getNotifyPriority() == 3) {
                        textViewNotifyPriority.setText(context.getResources().getString(R.string.statistics_table_priority_high));
                        textViewNotifyPriority.setBackgroundColor(Color.parseColor("#FB8C00"));
                    } else if (app.getNotifyPriority() == 4) {
                        textViewNotifyPriority.setText(context.getResources().getString(R.string.statistics_table_priority_extreme));
                        textViewNotifyPriority.setBackgroundColor(Color.parseColor("#F44336"));
                    }
                    s = app.getNotifyTime() + "";
                    textViewNotifyTime.setText(s);

                    tableRow.addView(textViewRank);
                    tableRow.addView(textViewApplicationName);
                    tableRow.addView(textVieTimeUsage);
                    tableRow.addView(textViewMaxTime);
                    tableRow.addView(textViewTotalUsage);
                    tableRow.addView(textViewNumberMonitoringDay);
                    tableRow.addView(textViewIsNotifying);
                    tableRow.addView(textViewIsTracking);
                    tableRow.addView(textViewNotifyPriority);

                    final AppInfo a = app;
                    tableRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.dialog_track, null);
                            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

                            TextView titleTextView = (TextView) view.findViewById(R.id.timeTitle);
                            titleTextView.setText(getResources().getString(R.string.statistics_dialog_title));

                            TextView messageTextView = (TextView) view.findViewById(R.id.messageTrack);
                            String s = getResources().getString(R.string.statistics_rank) + ": " + textViewRank.getText() + "\n" +
                                    getResources().getString(R.string.statistics_app_name) + ": " + textViewApplicationName.getText() + "\n" +
                                    getResources().getString(R.string.statistics_time_usage) + ": " + textVieTimeUsage.getText() + "\n" +
                                    getResources().getString(R.string.statistics_max_time) + ": " + textViewMaxTime.getText() + "\n" +
                                    getResources().getString(R.string.statistics_total_usage) + ": " + textViewTotalUsage.getText() + "\n" +
                                    getResources().getString(R.string.statistics_number_if_monitoring_day) + ": " + textViewNumberMonitoringDay.getText() + "\n" +
                                    getResources().getString(R.string.statistics_is_notifying) + ": " + textViewIsNotifying.getText() + "\n" +
                                    getResources().getString(R.string.statistics_is_tracking) + ": " + textViewIsTracking.getText() + "\n" +
                                    getResources().getString(R.string.statistics_priority) + ": " + textViewNotifyPriority.getText() + "\n" +
                                    getResources().getString(R.string.statistics_notify_time) + ": " + textViewNotifyTime.getText();
                            messageTextView.setText(s);

                            //Settaggio immagine
                            String avatar = avatarSharedPreferences.getString("avatarImage", "prom");
                            int idAvatar = getActivity().getResources().getIdentifier(avatar + "happy", "drawable", getActivity().getPackageName());

                            GifImageView avatarImage = (GifImageView) view.findViewById(R.id.titleDialogTrackAvatar);
                            avatarImage.setImageResource(idAvatar);

                            ImageView appImage = (ImageView) view.findViewById(R.id.dialogTrackAppImage);
                            try {
                                appImage.setImageDrawable(packageManager.getApplicationIcon(a.getPackageProcessName()));
                            } catch (PackageManager.NameNotFoundException e) {
                                appImage.setImageResource(R.drawable.android);
                            }

                            LinearLayout colorBar = (LinearLayout) view.findViewById(R.id.colorBar);
                            int realIdColorBar;
                            try {
                                int idColorBar = getActivity().getResources().getIdentifier(avatar + "_colorPrimaryDark", "color", getActivity().getPackageName());

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    realIdColorBar = getActivity().getResources().getColor(idColorBar, getActivity().getTheme());
                                } else {
                                    realIdColorBar = getActivity().getResources().getColor(idColorBar);
                                }
                                colorBar.setBackgroundColor(realIdColorBar);
                            } catch (Resources.NotFoundException notFoundException) {
                                colorBar.setBackgroundColor(Color.BLACK);
                            }


                            Button yesButton = (Button) view.findViewById(R.id.yesTrack);
                            Button noButton = (Button) view.findViewById(R.id.noTrack);

                            yesButton.setVisibility(View.GONE);


                            noButton.setVisibility(View.GONE);
                            alertDialog.setView(view);
                            alertDialog.show();


                            return;
                        }
                    });

                    tableRow.addView(textViewNotifyTime);

                    tableLayoutTMP.addView(tableRow);

                    rank++;

                }
                handlerTable.post(new Runnable() {
                    @Override
                    public void run() {
                        loadingStatistics.setVisibility(View.GONE);
                        tableLayout.addView(tableLayoutTMP);
                    }
                });

                //              swipeRefreshLayout.setRefreshing(false);
            }
        }


        );


        threadLoadingData.start();

        //      swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefresh);
  /*      swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (!threadLoadingData.isAlive())
                            threadLoadingData.run();

                    }

                });
*/
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(getContext(), SettingsActivity.class);
            startActivity(settings);
            getActivity().finish();


        } else if (id == R.id.action_tutorial) {
            Toast.makeText(getActivity(), R.string.tutorial_not_necessary, Toast.LENGTH_SHORT).show();
            item.setVisible(false);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}