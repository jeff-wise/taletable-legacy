
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
            is TextFont.FiraSans -> when (fontStyle)
            {
                is TextFontStyle.Regular    -> getTypeface(firaSansRegularFilePath, context)
                is TextFontStyle.Italic     -> getTypeface(firaSansItalicFilePath, context)
                is TextFontStyle.Bold       -> getTypeface(firaSansBoldFilePath, context)
                is TextFontStyle.BoldItalic -> getTypeface(firaSansBoldItalicFilePath, context)
                is TextFontStyle.Light      -> getTypeface(firaSansLightFilePath, context)
            }
            is TextFont.Merriweather -> when (fontStyle)
            {
                is TextFontStyle.Regular    -> getTypeface(merriweatherRegularFilePath, context)
                is TextFontStyle.Italic     -> getTypeface(merriweatherItalicFilePath, context)
                is TextFontStyle.Bold       -> getTypeface(merriweatherBoldFilePath, context)
                is TextFontStyle.BoldItalic -> getTypeface(merriweatherBoldItalicFilePath, context)
                is TextFontStyle.Light      -> getTypeface(merriweatherLightFilePath, context)
            }
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
