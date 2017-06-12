
package com.kispoko.tome.rts


import com.kispoko.tome.model.theme.Theme
import com.kispoko.tome.model.theme.ThemeId



/**
 * Theme Manager
 */
object ThemeManager
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val customThemeById : MutableMap<ThemeId.Custom, Theme> = mutableMapOf()


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    /**
     * The theme with the given id.
     */
    fun theme(themeId : ThemeId) : Theme? = when (themeId)
    {
        is ThemeId.Light  -> this.lightTheme
        is ThemeId.Dark   -> this.darkTheme
        is ThemeId.Custom -> this.customThemeById[themeId]
        else              -> null
    }


    // -----------------------------------------------------------------------------------------
    // BUILT-IN THEMES
    // -----------------------------------------------------------------------------------------

    val lightTheme = Theme(ThemeId.Light(), mutableSetOf())

    val darkTheme = Theme(ThemeId.Dark(), mutableSetOf())

}
