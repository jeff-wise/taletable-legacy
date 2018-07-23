
package com.taletable.android.lib.ui;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.style.LineBackgroundSpan;


/**
 * Rounded Background Span
 */
public class HighlightSpan implements LineBackgroundSpan
{
    private int CORNER_RADIUS = 5;

    private int backgroundColor = 0;

//    private int textColor = 0;
//    private int textSize = 0;

    private int mWidth = 0;

    private Rect bgRect;

    private int padding = 20;


    public HighlightSpan(int bgColor)
    {
        super();
        this.backgroundColor = bgColor;
//        this.textColor = textColor;
//        this.textSize = textSizePx;

        this.bgRect = new Rect();
    }

//    @Override
//    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
//        //return text with relative to the Paint
//        mWidth = (int) paint.measureText(text, start, end);
//        return mWidth;
//    }

    @Override
    public void drawBackground(Canvas canvas,
                               Paint paint,
                               int left,
                               int right,
                               int top,
                               int baseline,
                               int bottom,
                               CharSequence text,
                               int start,
                               int end,
                               int lnum)
    {
        int textWidth = Math.round(paint.measureText(text, start, end));
        int paintColor = paint.getColor();

        bgRect.set(left - this.padding,
                   top - (lnum == 0 ? this.padding / 2 : - (padding / 2)),
                   left + textWidth + this.padding,
                   bottom + (this.padding / 2));

        paint.setColor(Color.CYAN);
        canvas.drawRect(bgRect, paint);
        paint.setColor(paintColor);

    }


}

