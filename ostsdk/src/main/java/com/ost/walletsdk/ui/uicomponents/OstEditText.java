package com.ost.walletsdk.ui.uicomponents;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.ost.walletsdk.ui.uicomponents.uiutils.theme.ThemeConfig;

public class OstEditText extends AppCompatEditText {
    public OstEditText(Context context) {
        super(context);
        defineUi(context, null, 0);
    }

    public OstEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        defineUi(context, attrs, 0);
    }

    public OstEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defineUi(context, attrs, defStyleAttr);
    }

    void defineUi(Context context, AttributeSet attrs, int defStyleAttr) {
        ThemeConfig.getInstance().getEditText().apply(this);
    }
}