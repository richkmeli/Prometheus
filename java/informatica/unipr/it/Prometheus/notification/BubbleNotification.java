package informatica.unipr.it.Prometheus.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import informatica.unipr.it.Prometheus.R;
import pl.droidsonroids.gif.GifImageView;

import static android.provider.Settings.canDrawOverlays;


public class BubbleNotification {
    private BubblesManager bubblesManager;

    public BubbleNotification(final String processName, final Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {  // maggiore della versione 23

            if (canDrawOverlays(context)) {
                bubblesManager = new BubblesManager.Builder(context)
                        //.setTrashLayout(R.layout.bubble_trash_layout)
                        .setInitializationCallback(new OnInitializedCallback() {
                            @Override
                            public void onInitialized() {
                                addNewBubble(context, processName);
                            }
                        })
                        .build();
                bubblesManager.initialize();
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.notification_toast_bubble_overdraw), Toast.LENGTH_LONG).show();
            }
        } else {
            bubblesManager = new BubblesManager.Builder(context)
                    //.setTrashLayout(R.layout.bubble_trash_layout)
                    .setInitializationCallback(new OnInitializedCallback() {
                        @Override
                        public void onInitialized() {
                            addNewBubble(context, processName);
                        }
                    })
                    .build();
            bubblesManager.initialize();
        }


    }


    private void addNewBubble(final Context context, final String processName) {
        final BubbleLayout bubbleView = (BubbleLayout) LayoutInflater.from(context).inflate(R.layout.bubble_layout, null);
        SharedPreferences avatarSharedPreferences = context.getSharedPreferences("Avatar", Context.MODE_PRIVATE);

        String avatar = avatarSharedPreferences.getString("avatarImage", "prom");
        int idAvatar;
        if (avatar.equals("android") || avatar.equals("prom")) {
            idAvatar = context.getResources().getIdentifier(avatar + "desperate", "drawable", context.getPackageName());
        } else {
            idAvatar = context.getResources().getIdentifier(avatar + "angry", "drawable", context.getPackageName());
        }


        GifImageView avatarImage = (GifImageView) bubbleView.findViewById(R.id.avatarBubble);
        avatarImage.setImageResource(idAvatar);

        bubbleView.setShouldStickToWall(true);

        bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
            @Override
            public void onBubbleRemoved(BubbleLayout bubble) {
                recycle();
            }
        });

        bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {
            @Override
            public void onBubbleClick(BubbleLayout bubble) {

                Toast.makeText(context, context.getResources().getString(R.string.notification_too_much_used) + " " + processName + " !",
                        Toast.LENGTH_SHORT).show();
                recycle();

            }
        });
        bubbleView.setShouldStickToWall(true);
        bubblesManager.addBubble(bubbleView, 60, 20);
    }

    public void recycle() {
        bubblesManager.recycle();
    }
}






