
package com.kispoko.tome.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.atomic.AtomicInteger;



/**
 * Helpers for verbose ComponentUtil code.
 */
public class Util
{

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    private static Typeface serifFontRegular;
    private static Typeface serifFontBold;
    private static Typeface sansSerifFontRegular;
    private static Typeface sansSerifFontBold;


    public static LinearLayout.LayoutParams linearLayoutParamsWrap()
    {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                             LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public static LinearLayout.LayoutParams linearLayoutParamsMatch()
    {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                             LinearLayout.LayoutParams.MATCH_PARENT);
    }

    public static LinearLayout.LayoutParams linearLayoutParamsMatchWrap()
    {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                             LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public static LinearLayout.LayoutParams linearLayoutParamsWrapMatch()
    {
        return new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                             LinearLayout.LayoutParams.MATCH_PARENT);
    }

    public static float getDim(Context context, int id)
    {
        return context.getResources().getDimension(id);
    }


    /**
     * Generate a value suitable for use in {@link #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId()
    {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }



    public static Typeface serifFontRegular(Context context)
    {
        if (serifFontRegular == null)
//            serifFontRegular = Typeface.createFromAsset(context.getAssets(),
//                                                 "fonts/DavidLibre-Regular.ttf");
            serifFontRegular = Typeface.createFromAsset(context.getAssets(),
                                                        "fonts/Lora-Regular.ttf");
        return serifFontRegular;
    }


    public static Typeface serifFontBold(Context context)
    {
        if (serifFontBold == null)
//            serifFontBold = Typeface.createFromAsset(context.getAssets(),
//                    "fonts/DavidLibre-Bold.ttf");
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

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}

