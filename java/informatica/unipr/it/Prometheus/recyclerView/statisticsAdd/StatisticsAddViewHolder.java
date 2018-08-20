package informatica.unipr.it.Prometheus.recyclerView.statisticsAdd;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.data.database.DatabaseManager;
import informatica.unipr.it.Prometheus.data.model.DataManager;
import informatica.unipr.it.Prometheus.recyclerView.ListRemover;
import informatica.unipr.it.Prometheus.recyclerView.StatisticsObject;
import pl.droidsonroids.gif.GifImageView;

public class StatisticsAddViewHolder extends RecyclerView.ViewHolder {
    private ImageView icon;
    private Context context;
    private String processName;
    private String packageName;
    private DataManager dataManager;
    private TextView usage;
    private PackageManager pm;
    private View colorBarDown;

    public StatisticsAddViewHolder(View rootView, final Context context, final ListRemover remover) {
        super(rootView);

        usage = (TextView) rootView.findViewById(R.id.usage);
        icon = (ImageView) rootView.findViewById(R.id.icon);
        colorBarDown = rootView.findViewById(R.id.cardLayoutDOWN);
        this.context = context;

        this.pm = context.getPackageManager();

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataManager = new DatabaseManager(context);

                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                final View view = LayoutInflater.from(context).inflate(R.layout.dialog_track, null);

                TextView messageTextView = (TextView) view.findViewById(R.id.messageTrack);
                String s = context.getResources().getString(R.string.recycler_view_statistics_add_dialog_tracking_1) + " " + processName + " " + context.getResources().getString(R.string.recycler_view_statistics_add_dialog_tracking_2);
                messageTextView.setText(s);

                //Settaggio immagine
                SharedPreferences avatarSharedPreferences = context.getSharedPreferences("Avatar", Context.MODE_PRIVATE);
                avatarSharedPreferences.getString("avatarImage", "prom");
                String avatar = avatarSharedPreferences.getString("avatarImage", "prom");

                int idAvatar = context.getResources().getIdentifier(avatar + "happy", "drawable", context.getPackageName());

                GifImageView avatarImage = (GifImageView) view.findViewById(R.id.titleDialogTrackAvatar);
                avatarImage.setImageResource(idAvatar);

                ImageView appImage = (ImageView) view.findViewById(R.id.dialogTrackAppImage);
                try {
                    appImage.setImageDrawable(pm.getApplicationIcon(packageName));
                } catch (PackageManager.NameNotFoundException e) {
                    appImage.setImageResource(R.drawable.android);
                }

                LinearLayout colorBar = (LinearLayout) view.findViewById(R.id.colorBar);
                int realIdColorBar;
                try {
                    int idColorBar = context.getResources().getIdentifier(avatar + "_colorPrimaryDark", "color", context.getPackageName());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        realIdColorBar = context.getResources().getColor(idColorBar, context.getTheme());
                    } else {
                        realIdColorBar = context.getResources().getColor(idColorBar);
                    }
                    colorBar.setBackgroundColor(realIdColorBar);
                } catch (Resources.NotFoundException notFoundException) {
                    colorBar.setBackgroundColor(Color.BLACK);
                }

                                    /*Material mid
                    TextView yesButton = (TextView) view.findViewById(R.id.yesTrack);
                    TextView noButton = (TextView) view.findViewById(R.id.noTrack);
                    */

                Button yesButton = (Button) view.findViewById(R.id.yesTrack);
                Button noButton = (Button) view.findViewById(R.id.noTrack);

                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataManager.loadTracking(packageName, true);
                        remover.removeItem(getLayoutPosition());
                        alertDialog.dismiss();
                    }
                });

                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataManager.loadTracking(packageName, false);
                        alertDialog.dismiss();
                    }
                });

                alertDialog.setView(view);
                alertDialog.show();
                return;
            }
        });

    }

    public void setIconAndTime(StatisticsObject statisticsObject) throws PackageManager.NameNotFoundException {
        //Solo la left gli altri a null
        this.icon.setImageDrawable(statisticsObject.getIcon());
        SharedPreferences avatarSP = context.getSharedPreferences("Avatar", Context.MODE_PRIVATE);
        if (avatarSP.getBoolean("colorfullMode", true)) {
            int realIdColorBar;
            String avatar = avatarSP.getString("avatarImage", "prom");
            try {
                int idColorBar = context.getResources().getIdentifier(avatar + "_colorCardBackground", "color", context.getPackageName());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    realIdColorBar = context.getResources().getColor(idColorBar, context.getTheme());
                } else {
                    realIdColorBar = context.getResources().getColor(idColorBar);
                }
                colorBarDown.setBackgroundColor(realIdColorBar);
            } catch (Resources.NotFoundException notFoundException) {
                colorBarDown.setBackgroundColor(Color.BLACK);
            }
        }
        this.packageName = statisticsObject.getUsage();
        this.processName = (String) pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA));


        this.usage.setText("      " + processName);
    }
}
