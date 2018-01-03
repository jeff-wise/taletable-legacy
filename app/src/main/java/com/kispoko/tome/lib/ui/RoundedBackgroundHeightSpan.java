
package com.kispoko.tome.lib.ui;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.style.LineHeightSpan;
import android.text.style.ReplacementSpan;

import com.kispoko.tome.model.sheet.style.IconFormat;
import com.kispoko.tome.util.Util;


/**
 * Rounded Background Span
 */
public class RoundedBackgroundHeightSpan extends ReplacementSpan implements LineHeightSpan
{

    private static int CORNER_RADIUS = 24;
    private final int textColor;
    private final int backgroundColor;

    private final int lineSpacing;
    private final float lineHeight;


    private Drawable drawable;
    private IconFormat iconFormat;


    public RoundedBackgroundHeightSpan(float lineHeight, int lineSpacing, int textColor, int backgroundColor)
    {
        super();

        this.textColor = textColor;
        this.backgroundColor = backgroundColor;

        this.lineHeight = lineHeight;
        this.lineSpacing = lineSpacing;

        this.drawable = null;
        this.iconFormat = null;
    }


    public RoundedBackgroundHeightSpan(float lineHeight, int lineSpacing, int textColor, int backgroundColor, Drawable drawable, IconFormat iconFormat)
    {
        super();

        this.textColor = textColor;
        this.backgroundColor = backgroundColor;

        this.lineHeight = lineHeight;
        this.lineSpacing = lineSpacing;

        this.drawable = drawable;
        this.iconFormat = iconFormat;
    }


    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint)
    {
        final float textSize = paint.getTextSize();
        final float textLength = x + this.measureText(paint, text, start, end);
        //final float badgeHeight = textSize * this.lineHeight;
        final float badgeHeight = lineSpacing * lineHeight;
        // final float textOffsetVertical = textSize * 0.5f;


//        RectF badge = new RectF(x, y, textLength, y + badgeHeight);
        RectF badge = new RectF(x, y - badgeHeight, textLength, y + badgeHeight * 0.5f);
        paint.setColor(this.backgroundColor);
        canvas.drawRoundRect(badge, CORNER_RADIUS, CORNER_RADIUS, paint);

        paint.setColor(this.textColor);
//        canvas.drawText(text, start, end, x, y + textOffsetVertical, paint);
        canvas.drawText(text, start, end, x, y, paint);


        if (drawable != null & iconFormat != null)
        {
            int textLen = Math.round(paint.measureText("  ", 0, 2));

            int widthPx = Util.dpToPixel(iconFormat.size().getWidth());
            int heightPx = Util.dpToPixel(iconFormat.size().getHeight());

            int xOrigin = Math.round(x) + textLen;
            int yOrigin = Math.round(y - (heightPx * 0.75f));


            drawable.setBounds(
                    xOrigin,
                    yOrigin,
                    xOrigin + widthPx,
                    yOrigin + heightPx);
            drawable.draw(canvas);
        }
    }


    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm)
    {
        return Math.round(paint.measureText(text, start, end));
    }


    private float measureText(Paint paint, CharSequence text, int start, int end)
    {
        return paint.measureText(text, start, end);
    }


    @Override
    public void chooseHeight(CharSequence charSequence, int i, int i1, int i2, int i3, Paint.FontMetricsInt fontMetricsInt)
    {
        fontMetricsInt.bottom = this.lineSpacing;
        fontMetricsInt.descent = this.lineSpacing;
    }


}
