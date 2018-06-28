
package com.kispoko.tome.lib.ui


import android.content.Context
import android.graphics.Typeface
import com.kispoko.tome.model.sheet.style.TextFont
import com.kispoko.tome.model.sheet.style.TextFontStyle


/**
 * Font
 */
object Font
{

    // -----------------------------------------------------------------------------------------
    // PATHS
    // -----------------------------------------------------------------------------------------

    private val cabinRegularFilePath            = "fonts/Cabin-Regular.ttf"
    private val cabinMediumFilePath             = "fonts/Cabin-Medium.ttf"
    private val cabinSemiboldFilePath           = "fonts/Cabin-SemiBold.ttf"
    private val cabinBoldFilePath               = "fonts/Cabin-Bold.ttf"
    private val cabinItalicFilePath             = "fonts/Cabin-Italic.ttf"
    private val cabinBoldItalicFilePath         = "fonts/Cabin-BoldItalic.ttf"

    private val robotoCondensedLightFilePath        = "fonts/RobotoCondensed-Light.ttf"
    private val robotoCondensedRegularFilePath      = "fonts/RobotoCondensed-Regular.ttf"
    private val robotoCondensedBoldFilePath         = "fonts/RobotoCondensed-Bold.ttf"
    private val robotoCondensedItalicFilePath       = "fonts/RobotoCondensed-Italic.ttf"
    private val robotoCondensedBoldItalicFilePath   = "fonts/RobotoCondensed-BoldItalic.ttf"

    private val merriweatherRegularFilePath     = "fonts/Merriweather-Regular.ttf"
    private val merriweatherBoldFilePath        = "fonts/Merriweather-Bold.ttf"
    private val merriweatherItalicFilePath      = "fonts/Merriweather-Italic.ttf"
    private val merriweatherBoldItalicFilePath  = "fonts/Merriweather-BoldItalic.ttf"
    private val merriweatherLightFilePath       = "fonts/Merriweather-Light.ttf"

    private val kaushanRegularFilePath          = "fonts/KaushanScript-Regular.ttf"


    // -----------------------------------------------------------------------------------------
    // CACHED FONTS
    // -----------------------------------------------------------------------------------------

    private var robotoRegular : Typeface? = null


    // -----------------------------------------------------------------------------------------
    // GET FONTS
    // -----------------------------------------------------------------------------------------

    fun typeface(font : TextFont, fontStyle : TextFontStyle, context : Context) : Typeface =
        when (font)
        {
            is TextFont.Cabin -> when (fontStyle)
            {
                is TextFontStyle.Regular    -> getTypeface(cabinRegularFilePath, context)
                is TextFontStyle.Medium     -> getTypeface(cabinMediumFilePath, context)
                is TextFontStyle.SemiBold   -> getTypeface(cabinSemiboldFilePath, context)
                is TextFontStyle.Bold       -> getTypeface(cabinBoldFilePath, context)
                is TextFontStyle.BoldItalic -> getTypeface(cabinBoldItalicFilePath, context)
                is TextFontStyle.Italic     -> getTypeface(cabinItalicFilePath, context)
                is TextFontStyle.Light      -> getTypeface(cabinRegularFilePath, context)
            }
            is TextFont.RobotoCondensed -> when (fontStyle)
            {
                is TextFontStyle.Regular    -> getTypeface(robotoCondensedRegularFilePath, context)
                is TextFontStyle.Medium     -> getTypeface(robotoCondensedRegularFilePath, context)
                is TextFontStyle.SemiBold   -> getTypeface(robotoCondensedBoldFilePath, context)
                is TextFontStyle.Bold       -> getTypeface(robotoCondensedBoldFilePath, context)
                is TextFontStyle.Italic     -> getTypeface(robotoCondensedBoldItalicFilePath, context)
                is TextFontStyle.BoldItalic -> getTypeface(robotoCondensedItalicFilePath, context)
                is TextFontStyle.Light      -> getTypeface(robotoCondensedLightFilePath, context)
            }
            is TextFont.Merriweather -> when (fontStyle)
            {
                is TextFontStyle.Light      -> getTypeface(merriweatherLightFilePath, context)
                is TextFontStyle.Regular    -> getTypeface(merriweatherRegularFilePath, context)
                is TextFontStyle.Medium     -> getTypeface(merriweatherRegularFilePath, context)
                is TextFontStyle.SemiBold   -> getTypeface(merriweatherRegularFilePath, context)
                is TextFontStyle.Bold       -> getTypeface(merriweatherBoldFilePath, context)
                is TextFontStyle.Italic     -> getTypeface(merriweatherItalicFilePath, context)
                is TextFontStyle.BoldItalic -> getTypeface(merriweatherBoldItalicFilePath, context)
            }
            is TextFont.Kaushan             -> getTypeface(kaushanRegularFilePath, context)

        }


    fun getTypeface(filepath : String, context : Context) : Typeface
    {
        val typeface = this.robotoRegular

        return if (typeface == null)
            Typeface.createFromAsset(context.assets, filepath)
        else
            typeface
    }


}
