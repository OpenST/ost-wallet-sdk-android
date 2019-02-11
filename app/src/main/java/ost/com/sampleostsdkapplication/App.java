package ost.com.sampleostsdkapplication;

import android.app.Application;

import com.ost.mobilesdk.OstSdk;

public class App extends Application {

    private LogInUser loggedUser;
    @Override
    public void onCreate() {
        super.onCreate();
        OstSdk.init(getApplicationContext());
    }

    public LogInUser getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(LogInUser loggedUser) {
        this.loggedUser = loggedUser;
    }
}
