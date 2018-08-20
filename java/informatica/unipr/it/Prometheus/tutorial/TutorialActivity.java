package informatica.unipr.it.Prometheus.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Locale;

import informatica.unipr.it.Prometheus.R;

public class TutorialActivity extends AppCompatActivity {

    private int actual;
    private Button forward;
    private boolean changed;
    private Button back;
    private String which;
    private ScaleAnimation scaleIn;
    private ImageView tutorial;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);


        scaleIn = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_PARENT, 0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
        scaleIn.setDuration(500);
        scaleIn.setInterpolator(new LinearInterpolator());

        tutorial = (ImageView) findViewById(R.id.tutorialImage);
        actual = 1;
        Intent intentWhich = getIntent();
        which = intentWhich.getStringExtra("which");
        changed = false;

        forward = (Button) findViewById(R.id.forwardTutorialButton);
        back = (Button) findViewById(R.id.backTutorialButton);


        forward.setText(R.string.tutorial_forward);


        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++actual;
                setTutorialImage();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --actual;
                setTutorialImage();
            }
        });

        setTutorialImage();

    }

    private void setTutorialImage() {

        int idTutorialImage;
        if (Locale.getDefault().getDisplayLanguage().equals("italiano")) {
            idTutorialImage = getResources().getIdentifier(which + "_tutorial_" + actual + "_it", "drawable", getPackageName());
        } else {
            idTutorialImage = getResources().getIdentifier(which + "_tutorial_" + actual + "_en", "drawable", getPackageName());
        }
        if (idTutorialImage != 0) {

            if (actual != 1) {
                tutorial.startAnimation(scaleIn);
            }
            tutorial.setImageResource(idTutorialImage);

            if (changed) {
                forward.setText(R.string.tutorial_forward);
                forward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ++actual;
                        setTutorialImage();
                    }
                });
                changed = false;
            }
            int next = actual + 1;
            if (getResources().getIdentifier(which + "_tutorial_" + next + "_en", "drawable", getPackageName()) == 0) {
                forward.setText(R.string.finish_button_tutorial);
                forward.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
                changed = true;
            }
            if (actual == 1) {
                back.setVisibility(View.GONE);
            } else {
                back.setVisibility(View.VISIBLE);

            }
        }
    }
}

