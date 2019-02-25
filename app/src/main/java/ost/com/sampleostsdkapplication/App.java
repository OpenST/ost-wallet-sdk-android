package ost.com.sampleostsdkapplication;

import android.app.Application;

import com.ost.mobilesdk.OstSdk;

public class App extends Application {

    //    private static final String BASE_URL = "http://172.16.0.194:7001/testnet/v2";
    private static final String BASE_URL = "https://s5-api.stagingost.com/testnet/v2";
    private LogInUser loggedUser;
    @Override
    public void onCreate() {
        super.onCreate();

        OstSdk.init(getApplicationContext(), BASE_URL);
    }

    public LogInUser getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(LogInUser loggedUser) {
        this.loggedUser = loggedUser;
    }
}