
package com.kispoko.tome.app


import android.graphics.Color
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.Theme
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.theme.AppThemeNotSupported
import com.kispoko.tome.rts.theme.ThemeDoesNotHaveColor
import com.kispoko.tome.rts.theme.ThemeManager
import com.kispoko.tome.rts.theme.ThemeNotSupported
import effect.*
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Application Settings
 */
data class AppSettings(override val id : UUID,
                       val themeId : Prim<ThemeId>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.themeId.name = "theme_id"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(themeId : ThemeId)
        : this(UUID.randomUUID(),
               Prim(themeId))


    companion object : Factory<AppSettings>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<AppSettings> = when (doc)
        {
            is DocDict ->
            {
                effApply(::AppSettings,
                         // Theme Id
                         doc.at("theme_id") apply { ThemeId.fromDocument(it) })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun themeId() : ThemeId = this.themeId.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "app_settings"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun color(colorId : ColorId) : Int
    {
        val color = ThemeManager.theme(this.themeId())
                                .apply { color(it, colorId) }

        when (color)
        {
            is Val -> return color.value
            is Err -> ApplicationLog.error(color.error)
        }

        return Color.BLACK
    }


    fun color(colorTheme : ColorTheme) : Int
    {
        val color = colorId(this.themeId(), colorTheme) ap { colorId ->
                    ThemeManager.theme(this.themeId())  ap { theme ->
                    color(theme, colorId)
                    } }

        when (color)
        {
            is Val -> return color.value
            is Err -> ApplicationLog.error(color.error)
        }

        return Color.BLACK
    }


    private fun colorId(themeId : ThemeId,
                        colorTheme : ColorTheme) : AppEff<ColorId> =
            note(colorTheme.themeColorId(themeId),
                 AppThemeError(AppThemeNotSupported(themeId)))

    private fun color(theme : Theme, colorId : ColorId) : AppEff<Int> =
        note(theme.color(colorId),
             AppThemeError(ThemeDoesNotHaveColor(theme.themeId(), colorId)))


}

