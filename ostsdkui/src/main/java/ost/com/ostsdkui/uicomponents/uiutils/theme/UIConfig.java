package ost.com.ostsdkui.uicomponents.uiutils.theme;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

public class UIConfig {

    UIConfig(int size, String color, String font) {
        this.size = 0;
        this.color = color;
        this.font = font;
    }

    UIConfig(JSONObject jsonObject) {
        this.color = jsonObject.optString("color");
        this.size = jsonObject.optInt("size");
        this.font = jsonObject.optString("font");
    }

    private String color;
    private int size;
    private String font;

    public void apply(TextView textView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        textView.setTextColor(Color.parseColor(color));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
    }
}