
package com.kispoko.tome.lib.ui;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.LineHeightSpan;
import android.text.style.ReplacementSpan;
import android.util.Log;


/**
 * Created by jeff on 12/19/17.
 */
public class RoundedBackgroundSpan extends ReplacementSpan implements LineHeightSpan
{

    private static int CORNER_RADIUS = 24;
    private final int textColor;
    private final int backgroundColor;
    private final int lineHeight;


    public RoundedBackgroundSpan(int lineHeight, int textColor, int backgroundColor)
    {
        super();
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.lineHeight = lineHeight;
    }


    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint)
    {
        final float textSize = paint.getTextSize();
        final float textLength = x + this.measureText(paint, text, start, end);
        final float badgeHeight = textSize * 2.25f;
        final float textOffsetVertical = textSize * 1.45f;

        Log.d("***BG_SPAN", "text offset: " + Float.toString(textOffsetVertical));

        Log.d("***BG_SPAN", "top: " + Integer.toString(top));

        RectF badge = new RectF(x, y, textLength, y + badgeHeight);
        paint.setColor(this.backgroundColor);
        canvas.drawRoundRect(badge, CORNER_RADIUS, CORNER_RADIUS, paint);

        paint.setColor(this.textColor);
        canvas.drawText(text, start, end, x, y + textOffsetVertical, paint);
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
        fontMetricsInt.bottom = lineHeight;
        fontMetricsInt.descent = lineHeight;
    }


}
