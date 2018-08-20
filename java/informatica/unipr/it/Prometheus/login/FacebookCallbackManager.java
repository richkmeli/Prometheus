package informatica.unipr.it.Prometheus.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.download.DownloadAndSaveManager;

public class FacebookCallbackManager implements FacebookCallback<LoginResult> {
    private Context context;
    private FacebookCallbackOnSuccess facebookCallbackOnSuccess;
    private View view;

    public FacebookCallbackManager(Context context, View view, FacebookCallbackOnSuccess facebookCallbackOnSuccess) {
        this.context = context;
        this.view = view;
        this.facebookCallbackOnSuccess = facebookCallbackOnSuccess;
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        //Toast.makeText(context, context.getResources().getString(R.string.login_toast_fb_success), Toast.LENGTH_SHORT).show();
        Snackbar snackbar = Snackbar.make(view, context.getResources().getString(R.string.login_toast_fb_success), Snackbar.LENGTH_SHORT);
        TextView textSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textSnackBar.setTextColor(Color.WHITE);
        snackbar.show();


        DownloadAndSaveManager dASM = new DownloadAndSaveManager(context);
        dASM.downloadAndSaveFacebookProfilePicture(loginResult.getAccessToken().getUserId());

        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String name = response.getJSONObject().getString("name");

                            SharedPreferences userInformation = context.getSharedPreferences("UserInformation", context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = userInformation.edit();

                            editor.putBoolean("FBLogin", true);  // salva la riuscita nello sharedPreferences
                            editor.putString("Name", name);
                            editor.putString("Email", "");
                            editor.apply();

                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }
                    }
                });
        request.executeAsync();

        facebookCallbackOnSuccess.manageResultOnSuccess();
    }

    @Override
    public void onCancel() {
        //Toast.makeText(context, context.getResources().getString(R.string.login_toast_fb_cancel), Toast.LENGTH_SHORT).show();
        Snackbar snackbar = Snackbar.make(view, context.getResources().getString(R.string.login_toast_fb_cancel), Snackbar.LENGTH_SHORT);
        TextView textSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textSnackBar.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public void onError(FacebookException error) {
        //Toast.makeText(context, context.getResources().getString(R.string.login_toast_fb_error), Toast.LENGTH_SHORT).show();
        Snackbar snackbar = Snackbar.make(view, context.getResources().getString(R.string.login_toast_fb_error), Snackbar.LENGTH_SHORT);
        TextView textSnackBar = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textSnackBar.setTextColor(Color.WHITE);
        snackbar.show();
        //error.printStackTrace();
    }
}

