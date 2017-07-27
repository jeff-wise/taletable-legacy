
package com.kispoko.tome.app


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.theme.ThemeId
import effect.effApply
import effect.effError
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

}

