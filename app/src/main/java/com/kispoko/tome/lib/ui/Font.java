
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

    private static Typeface cursiveRegular;
    private static Typeface cursiveBold;


    public static Typeface serifFontRegular(Context context)
    {
        if (serifFontRegular == null)
            serifFontRegular = Typeface.createFromAsset(context.getAssets(),
                                                        "fonts/Lora-Regular.ttf");
        return serifFontRegular;
    }


    public static Typeface serifFontBold(Context context)
    {
        if (serifFontBold == null)
            serifFontBold = Typeface.createFromAsset(context.getAssets(),
                                                    "fonts/Lora-Bold.ttf");
        return serifFontBold;
    }


    public static Typeface serifFontItalic(Context context)
    {
        if (serifFontItalic == null)
            serifFontItalic = Typeface.createFromAsset(context.getAssets(),
                                                    "fonts/Lora-Italic.ttf");
        return serifFontItalic;
    }


    public static Typeface serifFontBoldItalic(Context context)
    {
        if (serifFontBoldItalic == null)
            serifFontBoldItalic = Typeface.createFromAsset(context.getAssets(),
                                                    "fonts/Lora-BoldItalic.ttf");
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



    public static Typeface cursiveRegular(Context context)
    {
        if (cursiveRegular == null)
            cursiveRegular = Typeface.createFromAsset(context.getAssets(),
                                                            "fonts/DancingScript-Regular.ttf");
        return cursiveRegular;
    }


    public static Typeface cursiveBold(Context context)
    {
        if (cursiveBold == null)
            cursiveBold = Typeface.createFromAsset(context.getAssets(),
                                                   "fonts/DancingScript-Bold.ttf");
        return cursiveBold;
    }

}
