package com.ost.walletsdk.ui.uicomponents.uiutils.theme;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class ButtonDrawable extends Drawable {

    private Paint paint;

    public ButtonDrawable() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        int height = getBounds().height();
        int width = getBounds().width();
        RectF rect = new RectF(0.0f, 0.0f, width, height);
        canvas.drawRoundRect(rect, 15, 15, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

}
