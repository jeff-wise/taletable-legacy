
package com.taletable.android.lib.ui


import android.content.Context
import android.graphics.Typeface
import com.taletable.android.model.sheet.style.TextFont
import com.taletable.android.model.sheet.style.TextFontStyle



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

//    private val firaSansExConLightFilePath      = "fonts/FiraSans-Light.ttf"
//    private val firaSansExConRegularFilePath    = "fonts/FiraSansExtraCondensed-Regular.ttf"
//    private val firaSansExConMediumFilePath     = "fonts/FiraSansExtraCondensed-Medium.ttf"
//    private val firaSansExConSemiBoldFilePath   = "fonts/FiraSansExtraCondensed-SemiBold.ttf"
//    private val firaSansExConBoldFilePath       = "fonts/FiraSansExtraCondensed-Bold.ttf"
//    private val firaSansExConItalicFilePath     = "fonts/FiraSansExtraCondensed-Italic.ttf"

    private val firaSansLightFilePath               = "fonts/FiraSans-Light.ttf"
    private val firaSansRegularFilePath             = "fonts/FiraSans-Regular.ttf"
    private val firaSansMediumFilePath              = "fonts/FiraSans-Medium.ttf"
    private val firaSansBoldFilePath                = "fonts/FiraSans-Bold.ttf"
    private val firaSansExtraBoldFilePath           = "fonts/FiraSans-ExtraBold.ttf"
    private val firaSansBlackFilePath               = "fonts/FiraSans-Black.ttf"

    private val robotoCondensedLightFilePath        = "fonts/RobotoCondensed-Light.ttf"
    private val robotoCondensedRegularFilePath      = "fonts/RobotoCondensed-Regular.ttf"
    private val robotoCondensedBoldFilePath         = "fonts/RobotoCondensed-Bold.ttf"
    private val robotoCondensedItalicFilePath       = "fonts/RobotoCondensed-Italic.ttf"
    private val robotoCondensedBoldItalicFilePath   = "fonts/RobotoCondensed-BoldItalic.ttf"

    private val robotoLightFilePath                 = "fonts/Roboto-Light.ttf"
    private val robotoRegularFilePath               = "fonts/Roboto-Regular.ttf"
    private val robotoItalicFilePath                = "fonts/Roboto-Italic.ttf"
    private val robotoMediumFilePath                = "fonts/Roboto-Medium.ttf"
    private val robotoMediumItalicFilePath          = "fonts/Roboto-MediumItalic.ttf"
    private val robotoBoldFilePath                  = "fonts/Roboto-Bold.ttf"
    private val robotoBoldItalicFilePath            = "fonts/Roboto-BoldItalic.ttf"

    private val latoLightFilePath                   = "fonts/Lato-Light.ttf"
    private val latoRegularFilePath                 = "fonts/Lato-Regular.ttf"
    private val latoItalicFilePath                  = "fonts/Lato-Italic.ttf"
    private val latoBoldFilePath                    = "fonts/Lato-Bold.ttf"
    private val latoBlackFilePath                   = "fonts/Lato-Black.ttf"

    private val merriweatherRegularFilePath         = "fonts/Merriweather-Regular.ttf"
    private val merriweatherBoldFilePath            = "fonts/Merriweather-Bold.ttf"
    private val merriweatherBlackFilePath           = "fonts/Merriweather-Black.ttf"
    private val merriweatherItalicFilePath          = "fonts/Merriweather-Italic.ttf"
    private val merriweatherBoldItalicFilePath      = "fonts/Merriweather-BoldItalic.ttf"
    private val merriweatherLightFilePath           = "fonts/Merriweather-Light.ttf"

    private val vollkornRegularFilePath             = "fonts/Vollkorn-Regular.ttf"

    private val garamondRegularFilePath             = "fonts/EBGaramond-Regular.ttf"
    private val garamondRegularItalicFilePath       = "fonts/EBGaramond-Italic.ttf"
    private val garamondMediumFilePath              = "fonts/EBGaramond-Medium.ttf"
    private val garamondSemiboldFilePath            = "fonts/EBGaramond-SemiBold.ttf"
    private val garamondBoldFilePath                = "fonts/EBGaramond-Bold.ttf"
    private val garamondBoldItalicFilePath          = "fonts/EBGaramond-BoldItalic.ttf"
    private val garamondExtraBoldFilePath           = "fonts/EBGaramond-ExtraBold.ttf"


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
                is TextFontStyle.MediumItalic   -> getTypeface(cabinMediumFilePath, context)
                is TextFontStyle.SemiBold       -> getTypeface(cabinSemiboldFilePath, context)
                is TextFontStyle.Bold           -> getTypeface(cabinBoldFilePath, context)
                is TextFontStyle.BoldItalic -> getTypeface(cabinBoldItalicFilePath, context)
                is TextFontStyle.ExtraBold  -> getTypeface(cabinBoldFilePath, context)
                is TextFontStyle.Black      -> getTypeface(cabinBoldFilePath, context)
                is TextFontStyle.Italic     -> getTypeface(cabinItalicFilePath, context)
                is TextFontStyle.Light      -> getTypeface(cabinRegularFilePath, context)
            }
            is TextFont.FiraSans -> when (fontStyle)
            {
                is TextFontStyle.Light      -> getTypeface(firaSansLightFilePath, context)
                is TextFontStyle.Regular    -> getTypeface(firaSansRegularFilePath, context)
                is TextFontStyle.Italic     -> getTypeface(firaSansRegularFilePath, context)
                is TextFontStyle.Medium     -> getTypeface(firaSansMediumFilePath, context)
                is TextFontStyle.MediumItalic     -> getTypeface(firaSansMediumFilePath, context)
                is TextFontStyle.SemiBold   -> getTypeface(firaSansMediumFilePath, context)
                is TextFontStyle.Bold       -> getTypeface(firaSansBoldFilePath, context)
                is TextFontStyle.BoldItalic -> getTypeface(firaSansRegularFilePath, context)
                is TextFontStyle.ExtraBold  -> getTypeface(firaSansExtraBoldFilePath, context)
                is TextFontStyle.Black      -> getTypeface(firaSansBlackFilePath, context)
            }
            is TextFont.Roboto -> when (fontStyle)
            {
                is TextFontStyle.Light          -> getTypeface(robotoLightFilePath, context)
                is TextFontStyle.Regular        -> getTypeface(robotoRegularFilePath, context)
                is TextFontStyle.Italic         -> getTypeface(robotoItalicFilePath, context)
                is TextFontStyle.Medium         -> getTypeface(robotoMediumFilePath, context)
                is TextFontStyle.MediumItalic   -> getTypeface(robotoMediumItalicFilePath, context)
                is TextFontStyle.SemiBold       -> getTypeface(robotoMediumFilePath, context)
                is TextFontStyle.Bold           -> getTypeface(robotoBoldFilePath, context)
                is TextFontStyle.BoldItalic     -> getTypeface(robotoBoldItalicFilePath, context)
                is TextFontStyle.ExtraBold      -> getTypeface(robotoBoldFilePath, context)
                is TextFontStyle.Black          -> getTypeface(robotoBoldFilePath, context)
            }
            is TextFont.RobotoCondensed -> when (fontStyle)
            {
                is TextFontStyle.Light      -> getTypeface(robotoCondensedLightFilePath, context)
                is TextFontStyle.Regular    -> getTypeface(robotoCondensedRegularFilePath, context)
                is TextFontStyle.Italic     -> getTypeface(robotoCondensedItalicFilePath, context)
                is TextFontStyle.Medium     -> getTypeface(robotoCondensedRegularFilePath, context)
                is TextFontStyle.MediumItalic     -> getTypeface(robotoCondensedRegularFilePath, context)
                is TextFontStyle.SemiBold   -> getTypeface(robotoCondensedBoldFilePath, context)
                is TextFontStyle.Bold       -> getTypeface(robotoCondensedBoldFilePath, context)
                is TextFontStyle.BoldItalic -> getTypeface(robotoCondensedBoldItalicFilePath, context)
                is TextFontStyle.ExtraBold  -> getTypeface(robotoCondensedBoldFilePath, context)
                is TextFontStyle.Black      -> getTypeface(robotoCondensedBoldFilePath, context)
            }
            is TextFont.Merriweather -> when (fontStyle)
            {
                is TextFontStyle.Light      -> getTypeface(merriweatherLightFilePath, context)
                is TextFontStyle.Regular    -> getTypeface(merriweatherRegularFilePath, context)
                is TextFontStyle.Medium     -> getTypeface(merriweatherRegularFilePath, context)
                is TextFontStyle.MediumItalic     -> getTypeface(merriweatherRegularFilePath, context)
                is TextFontStyle.SemiBold   -> getTypeface(merriweatherRegularFilePath, context)
                is TextFontStyle.Bold       -> getTypeface(merriweatherBoldFilePath, context)
                is TextFontStyle.ExtraBold  -> getTypeface(merriweatherBlackFilePath, context)
                is TextFontStyle.Black      -> getTypeface(merriweatherBlackFilePath, context)
                is TextFontStyle.Italic     -> getTypeface(merriweatherItalicFilePath, context)
                is TextFontStyle.BoldItalic -> getTypeface(merriweatherBoldItalicFilePath, context)
            }
            is TextFont.Garamond -> when (fontStyle)
            {
                is TextFontStyle.Light      -> getTypeface(garamondRegularFilePath, context)
                is TextFontStyle.Regular    -> getTypeface(garamondRegularFilePath, context)
                is TextFontStyle.Medium     -> getTypeface(garamondMediumFilePath, context)
                is TextFontStyle.MediumItalic     -> getTypeface(garamondMediumFilePath, context)
                is TextFontStyle.SemiBold   -> getTypeface(garamondSemiboldFilePath, context)
                is TextFontStyle.Bold       -> getTypeface(garamondBoldFilePath, context)
                is TextFontStyle.ExtraBold  -> getTypeface(garamondExtraBoldFilePath, context)
                is TextFontStyle.Black      -> getTypeface(garamondExtraBoldFilePath, context)
                is TextFontStyle.Italic     -> getTypeface(garamondRegularItalicFilePath, context)
                is TextFontStyle.BoldItalic -> getTypeface(garamondBoldItalicFilePath, context)
            }
            is TextFont.Lato -> when (fontStyle)
            {
                is TextFontStyle.Light      -> getTypeface(latoLightFilePath, context)
                is TextFontStyle.Regular    -> getTypeface(latoRegularFilePath, context)
                is TextFontStyle.Medium     -> getTypeface(latoRegularFilePath, context)
                is TextFontStyle.MediumItalic     -> getTypeface(latoRegularFilePath, context)
                is TextFontStyle.SemiBold   -> getTypeface(latoBoldFilePath, context)
                is TextFontStyle.Bold       -> getTypeface(latoBoldFilePath, context)
                is TextFontStyle.ExtraBold  -> getTypeface(latoBlackFilePath, context)
                is TextFontStyle.Black      -> getTypeface(latoBlackFilePath, context)
                is TextFontStyle.Italic     -> getTypeface(latoItalicFilePath, context)
                is TextFontStyle.BoldItalic -> getTypeface(latoBoldFilePath, context)
            }
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
