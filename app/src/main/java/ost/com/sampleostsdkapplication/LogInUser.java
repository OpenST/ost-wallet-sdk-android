package ost.com.sampleostsdkapplication;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

class LogInUser {
    private static final String TAG = "LogInUser";
    private String id;
    private String ostUserId;
    private String tokenId;
    private String password;

    LogInUser(JSONObject jsonObject) {
        try {
            id = jsonObject.getString(Constants.APP_USER_ID);
            ostUserId = jsonObject.getString(Constants.USER_ID);
            tokenId = jsonObject.getString(Constants.TOKEN_ID);
            password = jsonObject.getString(Constants.USER_PIN_SALT);
        } catch (JSONException e) {
            Log.e(TAG, "JSON exception", e.getCause());
        }
    }

    public String getId() {
        return id;
    }

    public String getOstUserId() {
        return ostUserId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public String getPassword() {
        return password;
    }
}