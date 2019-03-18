package ost.com.sampleostsdkapplication;

import android.app.Application;

import com.ost.walletsdk.OstSdk;

public class App extends Application {


    public static final String BASE_URL_MAPPY = "https://s5-mappy.stagingost.com/api/";
    public static final String BASE_URL_KIT = "https://s6-api.stagingost.com/testnet/v2";
    private LogInUser loggedUser;
    @Override
    public void onCreate() {
        super.onCreate();

        OstSdk.init(getApplicationContext(), BASE_URL_KIT);
    }

    public LogInUser getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(LogInUser loggedUser) {
        this.loggedUser = loggedUser;
    }
}