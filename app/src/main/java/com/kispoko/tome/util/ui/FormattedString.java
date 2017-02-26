
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;
import android.text.style.StyleSpan;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;

import java.util.List;

import static android.R.attr.width;


/**
 * Formatted String
 */
public class FormattedString
{


    // TODO Use absolute size span
    public static SpannableStringBuilder spannableStringBuilder(String text,
                                                                List<Span> spans,
                                                                Context context)
    {
        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(text);

        // This string builder mimics the span builder content because SpannableStringBuilder does
        // not have indexOf method
        StringBuilder          currentText = new StringBuilder(text);

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

            int spanTextIndex  = currentText.indexOf(span.text());
            int spanTextLength = span.text().length();

            if (spanTextIndex >= 0) {
                formatSpan(spanBuilder, spanTextIndex, spanTextLength,
                           span.format(), span.baseTextSize(), context);

            }

            // (3) Background
            // ---------------------------------------------------------------------------------

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

        }

        return spanBuilder;
    }


    private static void formatSpan(SpannableStringBuilder spanBuilder,
                                   int spanStart,
                                   int spanLength,
                                   TextStyle spanStyle,
                                   TextSize baseTextSize,
                                   Context context)
    {
        // > Typeface
        // -------------------------------------------------------------------------------------
        if (spanStyle.isBold() && spanStyle.isItalic())
        {
            StyleSpan valueBoldItalicSpan = new StyleSpan(Typeface.BOLD_ITALIC);
            spanBuilder.setSpan(valueBoldItalicSpan, spanStart, spanStart + spanLength, 0);
        }
        else if (spanStyle.isBold())
        {
            StyleSpan valueBoldSpan = new StyleSpan(Typeface.BOLD);
            spanBuilder.setSpan(valueBoldSpan, spanStart, spanStart + spanLength, 0);
        }
        else if (spanStyle.isItalic())
        {
            StyleSpan valueItalicSpan = new StyleSpan(Typeface.ITALIC);
            spanBuilder.setSpan(valueItalicSpan, spanStart, spanStart + spanLength, 0);
        }

        // > Color
        // -------------------------------------------------------------------------------------
        spanBuilder.setSpan(spanStyle.color().foregroundColorSpan(context),
                            spanStart, spanStart + spanLength, 0);

        // > Size
        // -------------------------------------------------------------------------------------
        RelativeSizeSpan sizeSpan = spanStyle.size().relativeSizeSpan(baseTextSize, context);
        spanBuilder.setSpan(sizeSpan, spanStart, spanStart + spanLength, 0);
    }



    // SPAN
    // -----------------------------------------------------------------------------------------

    public static class Span
    {

        // PROPERTIES
        // -----------------------------------------------------------------------------------------

        private String              placeholder;
        private String              text;
        private TextStyle           format;
        private TextSize            baseTextSize;
        private TextColor           backgroundColor;
        private SpanBackgroundStyle backgroundStyle;


        // CONSTRUCTORS
        // -----------------------------------------------------------------------------------------

        public Span(String placeholder,
                    String text,
                    TextStyle format,
                    TextSize baseTextSize,
                    TextColor backgroundColor,
                    SpanBackgroundStyle backgroundStyle)
        {
            this.placeholder     = placeholder;
            this.text            = text;
            this.format          = format;
            this.baseTextSize    = baseTextSize;
            this.backgroundColor = backgroundColor;
            this.backgroundStyle = backgroundStyle;
        }


        public Span(String placeholder,
                    String text,
                    TextStyle format,
                    TextSize baseTextSize)
        {
            this.placeholder     = placeholder;
            this.text            = text;
            this.format          = format;
            this.baseTextSize    = baseTextSize;
            this.backgroundColor = null;
            this.backgroundStyle = null;
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


        public TextStyle format()
        {
            return this.format;
        }


        public TextSize baseTextSize()
        {
            return this.baseTextSize;
        }


        public TextColor backgroundColor()
        {
            return this.backgroundColor;
        }


        public SpanBackgroundStyle backgroundStyle()
        {
            return this.backgroundStyle;
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
