
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

    private val firaSansRegularFilePath         = "fonts/FiraSans-Regular.ttf"
    private val firaSansBoldFilePath            = "fonts/FiraSans-Bold.ttf"
    private val firaSansItalicFilePath          = "fonts/FiraSans-Italic.ttf"
    private val firaSansBoldItalicFilePath      = "fonts/FiraSans-BoldItalic.ttf"
    private val firaSansLightFilePath           = "fonts/FiraSans-Light.ttf"

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
            is TextFont.FiraSans -> when (fontStyle)
            {
                is TextFontStyle.Regular    -> getTypeface(firaSansRegularFilePath, context)
                is TextFontStyle.Medium     -> getTypeface(firaSansRegularFilePath, context)
                is TextFontStyle.SemiBold   -> getTypeface(firaSansRegularFilePath, context)
                is TextFontStyle.Bold       -> getTypeface(firaSansBoldFilePath, context)
                is TextFontStyle.Italic     -> getTypeface(firaSansItalicFilePath, context)
                is TextFontStyle.BoldItalic -> getTypeface(firaSansBoldItalicFilePath, context)
                is TextFontStyle.Light      -> getTypeface(firaSansLightFilePath, context)
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

        if (typeface == null)
            return Typeface.createFromAsset(context.assets, filepath)
        else
            return typeface
    }


}
