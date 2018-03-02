
package com.kispoko.tome.rts.entity.theme


import com.kispoko.tome.app.ApplicationError
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ThemeId



/**
 * State Error
 */
sealed class ThemeError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


/**
 * A color theme does not support a theme. This happens when a sheet has some theme 'xyz' and
 * some object in that sheet does not have a color mapping for theme 'xyz', so we will probably
 * have to pick an arbitrary color for it.
 */
class ThemeNotSupported(val themeId : ThemeId) : ThemeError()
{
    override fun debugMessage(): String = """
            Theme Not Supported
                Theme Id: $themeId
            """

    override fun logMessage(): String = userMessage()
}


/**
 */
class AppThemeNotSupported(val themeId : ThemeId) : ThemeError()
{
    override fun debugMessage(): String = """
            |AppTheme Not Supported
            |    Theme Id: $themeId
            """.trim()

    override fun logMessage(): String = userMessage()
}


/**
 * A theme with the given id does not exist.
 */
class ThemeDoesNotExist(val themeId : ThemeId) : ThemeError()
{
    override fun debugMessage(): String = "Theme Does Not Exist: $themeId"

    override fun logMessage(): String = userMessage()
}


/**
 * The theme does not have a color with the given id.
 */
class ThemeDoesNotHaveColor(val themeId : ThemeId, val colorId : ColorId) : ThemeError()
{
    override fun debugMessage(): String = """
            Theme Does Not Have Color
                Theme Id: $themeId
                Color Id: $colorId
            """

    override fun logMessage(): String = userMessage()
}

