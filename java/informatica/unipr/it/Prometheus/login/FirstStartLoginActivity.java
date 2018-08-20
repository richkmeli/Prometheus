package informatica.unipr.it.Prometheus.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import informatica.unipr.it.Prometheus.MainActivity;
import informatica.unipr.it.Prometheus.R;

public class FirstStartLoginActivity extends AppCompatActivity {

    ImageView backgroungImage;
    // Google
    private GoogleLogin googleLogin;
    private SignInButton signInButton;
    // Facebook
    private FacebookLogin facebookLogin;
    private LoginButton facebookLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start_login);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view_first_login);
        googleLogin = new GoogleLogin(getApplicationContext(), this, scrollView, new GoogleCallbackManager() {
            @Override
            public void manageResultOnSuccess() {   // implementa il metodo mROS della interfaccia GoogleLoginManager per un comportamento specifico per il caso di riuscita di login
                onBackPressed();
            }
        });
        googleButtonInitialize();

        facebookLogin = new FacebookLogin(getApplicationContext(), this, scrollView, new FacebookCallbackOnSuccess() {
            @Override
            public void manageResultOnSuccess() {
                onBackPressed();
            }
        });
        facebookButtonInitialize();


        final Context thisContext = this;

        TextView proceed = (TextView) findViewById(R.id.proceedLogin);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void googleButtonInitialize() {   // implementa il bottone per classe GoogleLogin
        signInButton = (SignInButton) findViewById(R.id.google_login_Button);

        signInButton.setSize(SignInButton.SIZE_STANDARD);
//        signInButton.setScopes(googleLogin.getGso().getScopeArray());

        findViewById(R.id.google_login_Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.google_login_Button:
                        googleLogin.googleSignIn();
                        break;
                }
            }
        });
    }

    private void facebookButtonInitialize() {
        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);

        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view_first_login);
        facebookLoginButton.registerCallback(facebookLogin.getCallbackManager(), new FacebookCallbackManager(getApplicationContext(), scrollView, new FacebookCallbackOnSuccess() {
            @Override
            public void manageResultOnSuccess() {
                onBackPressed();
            }
        }));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Google e Facebook Login
        googleLogin.resultManager(requestCode, resultCode, data);
        facebookLogin.resultManager(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_);
        startActivity(intent);
        finish();
    }
}
