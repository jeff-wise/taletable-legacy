
package com.kispoko.tome.lib.ui;


import android.content.Context;
import android.graphics.Typeface;



/**
 * Application Fonts
 */
public class Font
{

    private static Typeface serifFontRegular;
    private static Typeface serifFontBold;
    private static Typeface serifFontItalic;
    private static Typeface serifFontBoldItalic;

    private static Typeface sansSerifFontRegular;
    private static Typeface sansSerifFontBold;


    public static Typeface serifFontRegular(Context context)
    {
        if (serifFontRegular == null)
            serifFontRegular = Typeface.createFromAsset(context.getAssets(),
                                                        "fonts/Merriweather-Regular.ttf");
        return serifFontRegular;
    }


    public static Typeface serifFontBold(Context context)
    {
        if (serifFontBold == null)
            serifFontBold = Typeface.createFromAsset(context.getAssets(),
                                                    "fonts/Merriweather-Bold.ttf");
        return serifFontBold;
    }


    public static Typeface serifFontItalic(Context context)
    {
        if (serifFontItalic == null)
            serifFontItalic = Typeface.createFromAsset(context.getAssets(),
                                                    "fonts/Merriweather-Italic.ttf");
        return serifFontItalic;
    }


    public static Typeface serifFontBoldItalic(Context context)
    {
        if (serifFontBoldItalic == null)
            serifFontBoldItalic = Typeface.createFromAsset(context.getAssets(),
                                                    "fonts/Merriweather-BoldItalic.ttf");
        return serifFontBoldItalic;
    }


    public static Typeface sansSerifFontBold(Context context)
    {
        if (sansSerifFontBold == null)
            sansSerifFontBold = Typeface.createFromAsset(context.getAssets(),
                                                         "fonts/Lato-Bold.ttf");
        return sansSerifFontBold;
    }

    public static Typeface sansSerifFontRegular(Context context)
    {
        if (sansSerifFontRegular == null)
            sansSerifFontRegular = Typeface.createFromAsset(context.getAssets(),
                                                            "fonts/Lato-Regular.ttf");
        return sansSerifFontRegular;
    }

}
