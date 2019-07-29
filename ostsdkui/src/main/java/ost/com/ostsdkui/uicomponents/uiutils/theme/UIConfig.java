package ost.com.ostsdkui.uicomponents.uiutils.theme;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Objects;

import ost.com.ostsdkui.uicomponents.uiutils.Font;
import ost.com.ostsdkui.uicomponents.uiutils.FontFactory;

import static com.ost.walletsdk.OstSdk.getContext;

public class UIConfig {

    private static final String FONT_REGULAR = "regular";
    private static final String FONT_MEDIUM = "medium";
    private static final String FONT_BOLD = "bold";
    private static final String FONT_SEMI_BOLD = "semi_bold";
    private static final String FONT_ITALIC = "italic";

    UIConfig(JSONObject jsonObject) {
        this.color = jsonObject.optString("color");
        this.size = jsonObject.optInt("size");
        this.backgroundColor = jsonObject.optString("background_color");
        this.fontStyle = jsonObject.optString("font_style");
        this.font = jsonObject.optString("font");
    }

    private String color;
    private int size;
    private String font;
    private String backgroundColor;
    private String fontStyle;

    public void apply(TextView textView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        textView.setTextColor(Color.parseColor(color));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        if (!TextUtils.isEmpty(backgroundColor)) {
            textView.setBackgroundColor(Color.parseColor(backgroundColor));
        }
        Font font = FontFactory.getInstance(getContext(), FontFactory.FONT.LATO);
        if (FONT_REGULAR.equals(this.fontStyle)) {
            textView.setTypeface(font.getLight());
        } else if (FONT_MEDIUM.equals(this.fontStyle)) {
            textView.setTypeface(font.getRegular());
        } else if (FONT_BOLD.equals(this.fontStyle)) {
            textView.setTypeface(font.getBold());
        } else if (FONT_SEMI_BOLD.equals(this.fontStyle)) {
            textView.setTypeface(font.getBold());
        } else if (FONT_ITALIC.equals(this.fontStyle)) {
            textView.setTypeface(font.getItalic());
        } else {
            textView.setTypeface(font.getRegular());
        }
    }
}