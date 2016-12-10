
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.graphics.Typeface;



/**
 * Application Fonts
 */
public class Font
{

    private static Typeface serifFontRegular;
    private static Typeface serifFontBold;
    private static Typeface sansSerifFontRegular;
    private static Typeface sansSerifFontBold;


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
