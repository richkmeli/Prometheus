package informatica.unipr.it.Prometheus.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.download.DownloadAndSaveManager;

public class GoogleLogin {
    private static final int RC_SIGN_IN = 9001;
    private static GoogleApiClient mGoogleApiClient;
    private Context context;
    private FragmentActivity fragmentActivity;
    private GoogleCallbackManager googleCallbackManager;
    private GoogleSignInOptions gso;
    private View view;


    public GoogleLogin(Context context, FragmentActivity fragmentActivity, View view, GoogleCallbackManager googleCallbackManager) {
        this.context = context;
        this.fragmentActivity = fragmentActivity;
        this.view = view;
        this.googleCallbackManager = googleCallbackManager;
    }

    private void googleSignInInitialization() {
        // Sign-in google, per metterlo all'interno della navigation bisogna crearlo qui, cioè dove istanzia la navigation
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                        Snackbar snackbar = Snackbar.make(view, context.getResources().getString(R.string.login_toast_google_error), Snackbar.LENGTH_SHORT);
                        TextView textSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                        textSnackBar.setTextColor(Color.WHITE);
                        snackbar.show();
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }

    public void googleSignIn() {
        if (mGoogleApiClient == null) {
            googleSignInInitialization();
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        fragmentActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void googleSignOut() {
        if (mGoogleApiClient == null) {
            googleSignInInitialization();
        }

        if (mGoogleApiClient != null) {
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.connect();
                        System.out.println("fdf");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                    }


                    if (mGoogleApiClient.isConnected()) {
                        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {
                                        SharedPreferences userInformation = context.getSharedPreferences("UserInformation", context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = userInformation.edit();

                                        DownloadAndSaveManager dASM = new DownloadAndSaveManager(context);
                                        dASM.deleteImageInternal("GoogleProfileImage.jpg");

                                        //Toast.makeText(context, context.getResources().getString(R.string.login_toast_google_out), Toast.LENGTH_SHORT).show();
                                        Snackbar snackbar = Snackbar.make(view, context.getResources().getString(R.string.login_toast_google_out), Snackbar.LENGTH_SHORT);
                                        TextView textSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                                        textSnackBar.setTextColor(Color.WHITE);
                                        snackbar.show();

                                        editor.putBoolean("GoogleLogin", false);  // salva la signout nello sharedPreferences
                                        editor.apply();

                                        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                                            mGoogleApiClient.disconnect();
                                        }
                                    }
                                });
                    }
                }
            });

            thread.start();

        }

    }

    public void resultManager(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        SharedPreferences userInformation = context.getSharedPreferences("UserInformation", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userInformation.edit();

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //Toast.makeText(context, context.getResources().getString(R.string.login_toast_google_success), Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar.make(view, context.getResources().getString(R.string.login_toast_google_success), Snackbar.LENGTH_SHORT);
            TextView textSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            textSnackBar.setTextColor(Color.WHITE);
            snackbar.show();

            DownloadAndSaveManager dASM = new DownloadAndSaveManager(context);
            dASM.downloadUriAndSaveImageInternal("GoogleProfileImage", acct.getPhotoUrl());

            editor.putString("Name", acct.getDisplayName());
            editor.putString("Email", acct.getEmail());

            editor.putBoolean("GoogleLogin", true);  // salva la riuscita nello sharedPreferences

            editor.apply();    // carica le modifiche

            googleCallbackManager.manageResultOnSuccess();   // chiama il metodo sull'oggetto che usa GoogleLoginManager per uno specifico comportamento
        } else {
            // Signed out, show unauthenticated UI.
            //Toast.makeText(context, context.getResources().getString(R.string.login_toast_google_error), Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar.make(view, context.getResources().getString(R.string.login_toast_google_error), Snackbar.LENGTH_SHORT);
            TextView textSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            textSnackBar.setTextColor(Color.WHITE);
            snackbar.show();

            editor.putBoolean("GoogleLogin", false);  // salva la riuscita nello sharedPreferences
            editor.apply();
        }

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public GoogleSignInOptions getGso() {
        return gso;
    }

}
