package informatica.unipr.it.Prometheus.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.util.Arrays;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.download.DownloadAndSaveManager;

public class FacebookLogin {
    private Context context;
    private FragmentActivity fragmentActivity;
    private CallbackManager callbackManager;
    private View view;
    private FacebookCallbackOnSuccess facebookCallbackOnSuccess;

    public FacebookLogin(Context context, FragmentActivity fragmentActivity, View view, FacebookCallbackOnSuccess facebookCallbackOnSuccess) {
        this.context = context;
        this.fragmentActivity = fragmentActivity;
        this.view = view;
        this.facebookCallbackOnSuccess = facebookCallbackOnSuccess;
        facebookSignInInitialization();
    }

    private void facebookSignInInitialization() {
        FacebookSdk.sdkInitialize(context);
        callbackManager = CallbackManager.Factory.create();
    }

    public void resultManager(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void facebookSignIn() {
        facebookSignInInitialization();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallbackManager(context, view, facebookCallbackOnSuccess));
        LoginManager.getInstance().logInWithReadPermissions(fragmentActivity, Arrays.asList("public_profile", "email"));
    }

    public void facebookSignOut() {
        facebookSignInInitialization();
        LoginManager.getInstance().logOut();
        SharedPreferences userInformation = context.getSharedPreferences("UserInformation", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userInformation.edit();
        editor.putBoolean("FBLogin", false);  // salva la riuscita nello sharedPreferences
        editor.apply();

        DownloadAndSaveManager dASM = new DownloadAndSaveManager(context);
        dASM.deleteImageInternal("FacebookProfileImage.jpg");

        Snackbar snackbar = Snackbar.make(view, context.getResources().getString(R.string.logout_toast_fb), Snackbar.LENGTH_SHORT);
        TextView textSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textSnackBar.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public CallbackManager getCallbackManager() {
        return callbackManager;
    }
}
