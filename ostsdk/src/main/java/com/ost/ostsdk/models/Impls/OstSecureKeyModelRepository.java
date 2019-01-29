package com.ost.ostsdk.models.Impls;

import android.content.SharedPreferences;

import com.ost.ostsdk.database.KeySharedPreferences;
import com.ost.ostsdk.models.OstSecureKeyModel;
import com.ost.ostsdk.models.entities.OstSecureKey;

public class OstSecureKeyModelRepository implements OstSecureKeyModel {

    private SharedPreferences mSharedPreferences;

    public OstSecureKeyModelRepository() {
        super();
        mSharedPreferences = KeySharedPreferences.getPref();
    }

    @Override
    public void insertSecureKey(OstSecureKey ostSecureKey) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(ostSecureKey.getKey(), ostSecureKey.getStringData());
        editor.apply();
    }

    @Override
    public OstSecureKey getByKey(String key) {
        String dataString = mSharedPreferences.getString(key, null);
        if (dataString != null) {
            return new OstSecureKey(key, dataString);
        }
        return null;
    }

    @Override
    public void deleteAllSecureKeys() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public OstSecureKey initSecureKey(String key, byte[] data) {
        OstSecureKey ostSecureKey = new OstSecureKey(key, data);
        insertSecureKey(new OstSecureKey(key, data));
        return ostSecureKey;
    }
}
