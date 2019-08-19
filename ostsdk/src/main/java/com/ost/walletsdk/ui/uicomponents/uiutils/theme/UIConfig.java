package com.ost.walletsdk.ui.uicomponents.uiutils.theme;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.ost.walletsdk.ui.uicomponents.uiutils.Font;
import com.ost.walletsdk.ui.uicomponents.uiutils.FontFactory;

import org.json.JSONObject;

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
        this.fontWeight = jsonObject.optString("system_font_weight");
        this.font = jsonObject.optString("font");
        this.alignment = jsonObject.optString("alignment");
    }

    private String color;
    private int size;
    private String font;
    private String backgroundColor;
    private String fontWeight;
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
        setFontAndWeight(textView);
    }

    private void setFontAndWeight(TextView textView) {
        Font font = FontFactory.getInstance(getContext());
        Typeface typeface = font.getFont(this.font);
        if (FONT_REGULAR.equals(this.fontWeight)) {
            if (null == typeface) typeface = Typeface.create("sans-serif", Typeface.NORMAL);
            textView.setTypeface(typeface);
        } else if (FONT_MEDIUM.equals(this.fontWeight)) {
            if (null == typeface) typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL);
            textView.setTypeface(typeface);
        } else if (FONT_BOLD.equals(this.fontWeight)) {
            if (null == typeface) typeface = Typeface.create("sans-serif-medium", Typeface.BOLD);
            textView.setTypeface(typeface);
        } else if (FONT_SEMI_BOLD.equals(this.fontWeight)) {
            if (null == typeface) typeface = Typeface.create("sans-serif", Typeface.BOLD);
            textView.setTypeface(typeface);
        } else {
            textView.setTypeface(typeface);
        }
    }

    protected String getColor() {
        return color;
    }

    protected int getSize() {
        return size;
    }

    protected String getFont() {
        return font;
    }

    protected String getBackgroundColor() {
        return backgroundColor;
    }

    protected String getFontWeight() {
        return fontWeight;
    }

    protected String getAlignment() {
        return alignment;
    }
}