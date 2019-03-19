package ost.com.sampleostsdkapplication;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.ost.walletsdk.OstSdk;

public class App extends Application {


    public static final String BASE_URL_MAPPY = "https://s5-mappy.stagingost.com/api/";
    public static final String BASE_URL_KIT = "https://s6-api.stagingost.com/testnet/v2";
    private LogInUser loggedUser;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        OstSdk.init(getApplicationContext(), BASE_URL_KIT);

        sharedPreferences = getSharedPreferences("LoggedIn_user", Context.MODE_PRIVATE);
    }

    public LogInUser getLoggedUser() {
        if (null != loggedUser) {
            return loggedUser;
        }

        //load pref id there are any
        loggedUser = getLoggedUserFromPref();

        return loggedUser;
    }

    private LogInUser getLoggedUserFromPref() {
        String userId = sharedPreferences.getString(Constants.USER_ID, null);
        String appId = sharedPreferences.getString(Constants.APP_USER_ID, null);
        String tokenId = sharedPreferences.getString(Constants.TOKEN_ID, null);
        String userPinSalt = sharedPreferences.getString(Constants.USER_PIN_SALT, null);

        if (null == userId || null == appId || null == tokenId || null == userPinSalt) {
            return null;
        }

        return new LogInUser(userId, appId, tokenId, userPinSalt);
    }

    public void setLoggedUser(LogInUser loggedUser) {
        this.loggedUser = loggedUser;
        SharedPreferences.Editor keyValuesEditor = sharedPreferences.edit();

        if (null == loggedUser) {
            keyValuesEditor.clear();
        } else {
            keyValuesEditor.putString(Constants.USER_ID, loggedUser.getOstUserId());
            keyValuesEditor.putString(Constants.APP_USER_ID, loggedUser.getId());
            keyValuesEditor.putString(Constants.TOKEN_ID, loggedUser.getTokenId());
            keyValuesEditor.putString(Constants.USER_PIN_SALT, loggedUser.getPassphrasePrefix());
        }

        keyValuesEditor.apply();
    }
}