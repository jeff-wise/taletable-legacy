
package com.taletable.android.util;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Helpers for verbose ComponentUtil code.
 */
public class Util
{

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);


    /**
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


    public static int dpToPixel(float dp)
    {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static int spToPx(float sp, Context context) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
        return px;
    }


    public static String doubleString(Double d)
    {
        if ((d == Math.floor(d)) && !Double.isInfinite(d)) {
            return Integer.toString(d.intValue());
        } else {
            return d.toString();
        }
    }


    public static String timeDifferenceString(Long startTime, Long endTime)
    {
        Double timeDifferenceMS = Long.valueOf(endTime - startTime).doubleValue() / 1000000;

        BigDecimal bd = new BigDecimal(timeDifferenceMS);
        bd = bd.round(new MathContext(3));
        Double rounded = bd.doubleValue();

        return rounded.toString();
    }


    public static String timeDifferenceString(Long timeDiffNS)
    {
        Double timeDifferenceMS = Long.valueOf(timeDiffNS).doubleValue() / 1000000;

        BigDecimal bd = new BigDecimal(timeDifferenceMS);
        bd = bd.round(new MathContext(3));
        Double rounded = bd.doubleValue();

        return rounded.toString();
    }

}

