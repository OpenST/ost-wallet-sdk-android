package ost.com.ostsdkui.uicomponents.uiutils.theme;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import org.json.JSONObject;

import ost.com.ostsdkui.uicomponents.uiutils.Font;
import ost.com.ostsdkui.uicomponents.uiutils.FontFactory;

import static com.ost.walletsdk.OstSdk.getContext;

public class UIConfig {

    private static final String FONT_REGULAR = "regular";
    private static final String FONT_MEDIUM = "medium";
    private static final String FONT_BOLD = "bold";
    private static final String FONT_SEMI_BOLD = "semi_bold";
    private static final String FONT_ITALIC = "italic";
    private static final String ALIGN_RIGHT = "right";
    private static final String ALIGN_LEFT = "left";

    UIConfig(JSONObject jsonObject) {
        this.color = jsonObject.optString("color");
        this.size = jsonObject.optInt("size");
        this.backgroundColor = jsonObject.optString("background_color");
        this.fontStyle = jsonObject.optString("font_style");
        this.font = jsonObject.optString("font");
        this.alignment = jsonObject.optString("alignment");
    }

    private String color;
    private int size;
    private String font;
    private String backgroundColor;
    private String fontStyle;
    private String alignment;

    public void apply(TextView textView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        textView.setTextColor(Color.parseColor(color));

        if (ALIGN_RIGHT.equalsIgnoreCase(alignment)) {
            textView.setGravity(Gravity.RIGHT);
        } else if (ALIGN_LEFT.equalsIgnoreCase(alignment)) {
            textView.setGravity(Gravity.LEFT);
        } else {
            textView.setGravity(Gravity.CENTER);
        }

        if (!TextUtils.isEmpty(backgroundColor)) {
            Drawable drawable = new ButtonDrawable();
            drawable.setColorFilter(Color.parseColor(backgroundColor), PorterDuff.Mode.SRC_IN);
            textView.setBackground(drawable);
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