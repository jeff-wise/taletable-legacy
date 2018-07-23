
package com.taletable.android.app


import android.graphics.Color
import com.taletable.android.db.DB_AppSettingsValue
import com.taletable.android.db.appSettingsTable
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.RowValue1
import com.taletable.android.lib.orm.schema.PrimValue
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.Theme
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.theme.AppThemeNotSupported
import com.taletable.android.rts.entity.theme.ThemeDoesNotHaveColor
import com.taletable.android.rts.entity.theme.ThemeManager
import effect.*
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SchemaDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Application Settings
 */
data class AppSettings(override val id : UUID,
                       val themeId : ThemeId)
                        : ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(themeId : ThemeId)
        : this(UUID.randomUUID(), themeId)


    companion object : Factory<AppSettings>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<AppSettings> = when (doc)
        {
            is DocDict ->
            {
                apply(::AppSettings,
                      // Theme Id
                      doc.at("theme_id") apply { ThemeId.fromDocument(it) })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun themeId() : ThemeId = this.themeId


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_AppSettingsValue =
            RowValue1(appSettingsTable, PrimValue(themeId))



    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun color(colorId : ColorId) : Int
    {
        val color = ThemeManager.theme(this.themeId())
                                .apply { color(it, colorId) }

        when (color)
        {
            is effect.Val -> return color.value
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
            is effect.Val -> return color.value
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

