package ost.com.ostsdkui.uicomponents.uiutils.theme;

import android.content.Context;

import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import org.json.JSONObject;

import java.io.InputStream;

class ThemeDefault {

    static JSONObject getDefaultTheme(Context context) {
        try {
            InputStream configInputStream = context.getAssets().open("ost-theme-config.json");
            int size = configInputStream.available();
            byte[] buffer = new byte[size];

            configInputStream.read(buffer);
            configInputStream.close();

            String json = new String(buffer, "UTF-8");
            return new JSONObject(json);

        } catch (Exception e) {
            throw new OstError("ost_config_rc_1", OstErrors.ErrorCode.CONFIG_READ_FAILED);
        }
    }
}
