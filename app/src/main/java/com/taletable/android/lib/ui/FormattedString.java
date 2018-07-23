
package com.taletable.android.lib.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ReplacementSpan;

import com.taletable.android.R;
import com.taletable.android.model.sheet.style.TextFont;
import com.taletable.android.util.Util;

import java.util.ArrayList;
import java.util.List;



/**
 * Formatted String
 */
public class FormattedString
{


    public static SpannableStringBuilder spannableStringBuilder(String text, Span span)
    {
        List<Span> spans = new ArrayList<>();
        spans.add(span);
        return FormattedString.spannableStringBuilder(text, spans);
    }


    public static SpannableStringBuilder spannableStringBuilder(String text, List<Span> spans)
    {
        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(text);

        // This string builder mimics the span builder content because SpannableStringBuilder does
        // not have indexOf method
        StringBuilder          currentText = new StringBuilder(text.toLowerCase());

        for (Span span : spans)
        {
            // (1) If placeholder remove and replace with text
            // ---------------------------------------------------------------------------------

            if (span.placeholder() != null)
            {
                int placeholderIndex  = currentText.indexOf(span.placeholder());
                int placeholderLength = span.placeholder().length();

                // IF the placeholder EXISTS in the text...
                if (placeholderIndex >= 0)
                {
                    // (1 A) Remove the placeholder string
                    // ---------------------------------------
                    currentText.replace(placeholderIndex, placeholderIndex + placeholderLength, "");
                    spanBuilder.delete(placeholderIndex, placeholderIndex + placeholderLength);

                    // (1 B) Insert the text
                    // ---------------------------------------
                    spanBuilder.insert(placeholderIndex, span.text());
                    currentText.insert(placeholderIndex, span.text());
                }
            }

            // (2) Format span
            //     Now that the placeholder is resolved, the span text should exist in the string.
            //     Find it and then format it.
            // ---------------------------------------------------------------------------------

            int spanTextIndex  = currentText.indexOf(span.text().toLowerCase());
            int spanTextLength = span.text().length();

            if (spanTextIndex >= 0)
            {
                formatSpan(spanBuilder, spanTextIndex, spanTextLength,
                        span.textColor(), span.textSize(), span.font());
            }

            // (3) Background
            // ---------------------------------------------------------------------------------

            /*
            if (span.backgroundStyle() != null)
            {
                switch (span.backgroundStyle())
                {
                    case ARROWS_VERTICAL:
                        VerticalArrowsBackgroundSpan verticalArrowsBackgroundSpan =
                            new VerticalArrowsBackgroundSpan(context,
                                                             span.backgroundColor().resourceId(),
                                                             span.format().color().resourceId());
                        spanBuilder.setSpan(verticalArrowsBackgroundSpan,
                                            spanTextIndex, spanTextIndex + spanTextLength, 0);
                        break;
                }
            }

            */

        }

        return spanBuilder;
    }


    public static void formatSpan(SpannableStringBuilder spanBuilder,
                                  int spanStart,
                                  int spanLength,
                                  Integer color,
                                  Float textSizeDp,
                                  TextFont font)
    {
        // > Typeface
        // -------------------------------------------------------------------------------------

        if (font != null)
        {
//            switch (font)
//            {
//                case BOLD:
//                    StyleSpan valueBoldSpan = new StyleSpan(BOLD);
//                    spanBuilder.setSpan(valueBoldSpan, spanStart, spanStart + spanLength, 0);
//                    break;
//                case ITALIC:
//                    StyleSpan valueItalicSpan = new StyleSpan(ITALIC);
//                    spanBuilder.setSpan(valueItalicSpan, spanStart, spanStart + spanLength, 0);
//                    break;
//                case BOLD_ITALIC:
//                    StyleSpan valueBoldItalicSpan = new StyleSpan(BOLD_ITALIC);
//                    spanBuilder.setSpan(valueBoldItalicSpan, spanStart, spanStart + spanLength, 0);
//                    break;
//            }
        }

        // > Color
        // -------------------------------------------------------------------------------------

        if (color != null)
        {
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
            spanBuilder.setSpan(colorSpan, spanStart, spanStart + spanLength, 0);
        }

        // > Size
        // -------------------------------------------------------------------------------------

        if (textSizeDp != null)
        {
            int textSizePx = Util.dpToPixel(textSizeDp);
            AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(textSizePx, true);
            spanBuilder.setSpan(sizeSpan, spanStart, spanStart + spanLength, 0);
        }
    }



    // SPAN
    // -----------------------------------------------------------------------------------------

    public static class Span
    {

        // PROPERTIES
        // -----------------------------------------------------------------------------------------

        private String              placeholder;
        private String              text;

        private TextFont            textFont;
        private Integer             textColor;
        private Float               textSize;


        // CONSTRUCTORS
        // -----------------------------------------------------------------------------------------

        public Span(String text, Integer color)
        {
            this.placeholder    = null;

            this.text           = text;

            this.textColor      = color;
            this.textSize       = null;
            this.textFont       = null;
        }


        public Span(String text, Integer color, TextFont textFont)
        {
            this.placeholder    = null;

            this.text           = text;

            this.textColor      = color;
            this.textSize       = null;
            this.textFont       = textFont;
        }


        public Span(String text, Integer color, Float size)
        {
            this.placeholder    = null;

            this.text           = text;

            this.textColor      = color;
            this.textSize       = size;
            this.textFont       = null;
        }


        public Span(String text, String placeholder, Integer color, Float size, TextFont textFont)
        {
            this.text           = text;
            this.placeholder    = placeholder;

            this.textColor      = color;
            this.textSize       = size;
            this.textFont       = textFont;
        }


        public Span(String text, Integer color, Float size, TextFont textFont)
        {
            this.text           = text;
            this.placeholder    = null;

            this.textColor      = color;
            this.textSize       = size;
            this.textFont       = textFont;
        }


        // API
        // -----------------------------------------------------------------------------------------

        public String placeholder()
        {
            return this.placeholder;
        }


        public String text()
        {
            return this.text;
        }


        public Float textSize()
        {
            return this.textSize;
        }


        public TextFont font()
        {
            return this.textFont;
        }


        public int textColor()
        {
            return this.textColor;
        }

    }


    // SPAN BACKGROUND STYLE
    // -----------------------------------------------------------------------------------------

    public enum SpanBackgroundStyle
    {
        ARROWS_VERTICAL
    }


    public static class VerticalArrowsBackgroundSpan extends ReplacementSpan
    {

        private int CORNER_RADIUS = 8;
        private int backgroundColor = 0;
        private int textColor = 0;
        private int chevronColor = 0;
        private VectorDrawableCompat chevronUp;


        public VerticalArrowsBackgroundSpan(Context context, int backgroundColor, int textColor)
        {
            super();

            this.backgroundColor = ContextCompat.getColor(context, backgroundColor);
            this.textColor       = ContextCompat.getColor(context, textColor);
            this.chevronUp       = VectorDrawableCompat.create(context.getResources(),
                                                               R.drawable.ic_option_chevron_up,
                                                               null);
            this.chevronColor    = ContextCompat.getColor(context, R.color.gold_light);
        }


        @Override
        public void draw(Canvas canvas, CharSequence text,
                         int start, int end, float x, int top, int y, int bottom, Paint paint)
        {

            RectF rect = new RectF(x, top, x + measureText(paint, text, start, end), bottom);
            paint.setColor(this.backgroundColor);
            canvas.drawRoundRect(rect, CORNER_RADIUS, CORNER_RADIUS, paint);
            paint.setColor(this.textColor);
            canvas.drawText(text, start, end, x, y, paint);


            int intX = Float.valueOf(x).intValue();
            this.chevronUp.setBounds(intX, top + (top - bottom), intX + 60, bottom);
            this.chevronUp.draw(canvas);
        }


        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end,
                           Paint.FontMetricsInt fm)
        {
            return Math.round(paint.measureText(text, start, end));
        }


        private float measureText(Paint paint, CharSequence text, int start, int end)
        {
            return paint.measureText(text, start, end);
        }

    }


}
