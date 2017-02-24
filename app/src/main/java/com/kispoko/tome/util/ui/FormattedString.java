
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.kispoko.tome.sheet.widget.util.TextStyle;

import java.util.List;



/**
 * Formatted String
 */
public class FormattedString
{


    public static SpannableStringBuilder spannableStringBuilder(String text,
                                                                TextStyle textStyle,
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
                           span.format(), textStyle, context);

            }


        }

        return spanBuilder;
    }


    private static void formatSpan(SpannableStringBuilder spanBuilder,
                                   int spanStart,
                                   int spanLength,
                                   TextStyle spanStyle,
                                   TextStyle textStyle,
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
        RelativeSizeSpan sizeSpan = spanStyle.size().relativeSizeSpan(textStyle.size(), context);
        spanBuilder.setSpan(sizeSpan, spanStart, spanStart + spanLength, 0);
    }



    // SPAN
    // -----------------------------------------------------------------------------------------

    public static class Span
    {

        // PROPERTIES
        // -----------------------------------------------------------------------------------------

        private String    placeholder;
        private String    text;
        private TextStyle format;


        // CONSTRUCTORS
        // -----------------------------------------------------------------------------------------

        public Span(String placeholder, String text, TextStyle format)
        {
            this.placeholder = placeholder;
            this.text        = text;
            this.format      = format;
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

    }


}
