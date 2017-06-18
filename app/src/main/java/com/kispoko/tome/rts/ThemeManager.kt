
package com.kispoko.tome.rts


import com.kispoko.tome.model.theme.*


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

    // Light
    // -----------------------------------------------------------------------------------------

    private val lightUIColors =
            UIColors(ColorId.Theme("light_grey"),
                     ColorId.Theme("light_grey"),
                     ColorId.Theme("dark_grey"),
                     ColorId.Theme("dark_grey"),
                     ColorId.Theme("dark_grey"),
                     ColorId.Theme("dark_grey"),
                     ColorId.Theme("dark_grey"),
                     ColorId.Theme("light_grey"),
                     ColorId.Theme("dark_grey"),
                     ColorId.Theme("dark_grey"))


    private val lightPalette : MutableSet<ThemeColor> =
            mutableSetOf(ThemeColor(ColorId.Theme("dark_grey"), "#555555"),
                    ThemeColor(ColorId.Theme("light_grey"), "#CCCCCC"))


    val lightTheme = Theme(ThemeId.Light, lightPalette, lightUIColors)


    // Dark
    // -----------------------------------------------------------------------------------------

    private val darkUIColors =
            UIColors(ColorId.Theme("dark_grey"),
                     ColorId.Theme("dark_grey"),
                     ColorId.Theme("light_grey"),
                     ColorId.Theme("light_grey"),
                     ColorId.Theme("light_grey"),
                     ColorId.Theme("light_grey"),
                     ColorId.Theme("light_grey"),
                     ColorId.Theme("dark_grey"),
                     ColorId.Theme("light_grey"),
                     ColorId.Theme("light_grey"))


    private val darkPalette : MutableSet<ThemeColor> =
            mutableSetOf(ThemeColor(ColorId.Theme("dark_grey"), "#282828"),
                         ThemeColor(ColorId.Theme("light_grey"), "#EEEEEE"))


    val darkTheme = Theme(ThemeId.Dark, darkPalette, darkUIColors)

}
