package ost.com.sampleostsdkapplication;

import android.app.Application;

import com.ost.mobilesdk.OstSdk;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        OstSdk.init(getApplicationContext());
    }
}
