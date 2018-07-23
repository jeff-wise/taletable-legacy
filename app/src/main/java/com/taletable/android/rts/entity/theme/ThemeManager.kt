
package com.taletable.android.rts.entity.theme


import com.taletable.android.app.*
import com.taletable.android.model.theme.*
import com.taletable.android.model.theme.official.officialThemeLight
import effect.*
import lulo.schema.Schema



/**
 * Theme Manager
 */
object ThemeManager
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    private var specification : Schema? = null

    private val themeString = "theme"

    val themeById : MutableMap<ThemeId, Theme> = mutableMapOf()

    var officialThemesLoaded = false


    // -----------------------------------------------------------------------------------------
    // OFFICIAL
    // -----------------------------------------------------------------------------------------

//    suspend fun loadOfficialThemes(context : Context)
//    {
//
//        // TODO review this logic/process. what if it fails? shouldn't ever fail though.
//        // TODO test that themes load correctly
//        if (!officialThemesLoaded)
//        {
//            OfficialManager.loadTheme(OfficialTheme.Dark, context)
//            OfficialManager.loadTheme(OfficialTheme.Light, context)
//
//            officialThemesLoaded = true
//        }
//    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------


    fun addTheme(theme : Theme)
    {
        this.themeById.put(theme.themeId(), theme)
    }


    /**
     * The theme with the given id.
     */
    //fun theme(themeId : ThemeId) : Theme? = this.themeById[themeId]

    fun theme(themeId : ThemeId) : AppEff<Theme> = when (themeId)
    {
        is ThemeId.Light -> effValue(officialThemeLight)
        else -> {
            note(this.themeById[themeId],
                    AppThemeError(ThemeDoesNotExist(themeId)))
        }

    }


    fun color(themeId : ThemeId, colorTheme: ColorTheme) : Int?
    {
        fun colorId(themeId : ThemeId, colorTheme : ColorTheme) : AppEff<ColorId> =
            note(colorTheme.themeColorId(themeId),
                 AppThemeError(AppThemeNotSupported(themeId)))

        val colorInt = colorId(themeId, colorTheme) ap { colorId ->
                       ThemeManager.theme(themeId)  ap { theme   ->
                            effValue<AppError,Int?>(theme.color(colorId))
                       } }

        when (colorInt) {
            is Val -> return colorInt.value
            is Err -> ApplicationLog.error(colorInt.error)
        }

        return null
    }

}


