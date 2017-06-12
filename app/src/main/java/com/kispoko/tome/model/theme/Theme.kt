
package com.kispoko.tome.model.theme


import android.graphics.Color
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Conj
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.util.*



/**
 * Theme
 */
data class Theme(override val id : UUID,
                 val themeId : Prim<ThemeId>,
                 val palette : Conj<ThemeColor>) : Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(themeId : ThemeId, palette: Set<ThemeColor>) :
            this(UUID.randomUUID(), Prim(themeId), Conj(palette.toMutableSet()))


    companion object : Factory<Theme>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Theme> = when (doc)
        {
            is DocDict -> effApply(::Theme,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Theme Id
                                   doc.at("name") ap {
                                       effApply(::Prim, ThemeId.fromDocument(it))
                                   },
                                   // Theme Colors
                                   doc.list("palette") ap {
                                       effApply(::Conj,
                                                it.mapSetMut { ThemeColor.fromDocument(it) })
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    private val colorById : MutableMap<ColorId,Int> = this.palette.set
                                                          .associateBy({it.colorId}, {it.color})
                                                          .toMutableMap()


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun themeId() : ThemeId = this.themeId.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun color(colorId : ColorId) : Int? = this.colorById[colorId]

}



sealed class ThemeId
{


    class Light : ThemeId()
    {

        companion object
        {

            fun fromDocument(doc : SpecDoc) : ValueParser<ThemeId.Light> = when (doc)
            {
                is DocText ->
                {
                    when (doc.text) {
                        "light" -> effValue<ValueError,ThemeId.Light>(ThemeId.Light())
                        else    -> effError<ValueError,ThemeId.Light>(
                                        UnexpectedValue("ThemeId.Light", doc.text, doc.path))
                    }
                }
                else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
            }

        }

    }


    class Dark : ThemeId()
    {

        companion object
        {

            fun fromDocument(doc : SpecDoc) : ValueParser<ThemeId.Dark> = when (doc)
            {
                is DocText ->
                {
                    when (doc.text) {
                        "dark" -> effValue<ValueError,ThemeId.Dark>(ThemeId.Dark())
                        else    -> effError<ValueError,ThemeId.Dark>(
                                        UnexpectedValue("ThemeId.Dark", doc.text, doc.path))
                    }
                }
                else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
            }

        }

    }



    /**
     * Custom Theme Id
     */
    data class Custom(val name : String) : ThemeId()
    {
        companion object : Factory<ThemeId.Custom>
        {
            override fun fromDocument(doc: SpecDoc) : ValueParser<ThemeId.Custom> = when (doc)
            {
                is DocText -> effValue(Custom(doc.text))
                else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
            }
        }
    }


    companion object
    {
        fun fromDocument(doc : SpecDoc) : ValueParser<ThemeId> = this.fromDocument(doc)
    }

}


/**
 * Theme Color Id
 */
data class ThemeColorId(val themeId : ThemeId, val colorId : ColorId)
{

    companion object : Factory<ThemeColorId>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ThemeColorId> = when (doc)
        {
            is DocDict -> effApply(::ThemeColorId,
                                   // ThemeId
                                   ThemeId.fromDocument(doc),
                                   // ThemeId
                                   ColorId.fromDocument(doc)
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }
}


/**
 * Theme Color
 */
data class ThemeColor(val colorId : ColorId, val color : Int)
{

    companion object : Factory<ThemeColor>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ThemeColor> = when (doc)
        {
            is DocDict -> effApply(::ThemeColor,
                                   // Color Id
                                   ColorId.fromDocument(doc),
                                   // Color
                                   effApply({Color.parseColor(it) }, doc.text("color"))
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }
}


/**
 * Color Theme
 *
 * A pallette of colors for some object.
 */
data class ColorTheme(val themeColorIds: Set<ThemeColorId>)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ColorTheme>
    {

        override fun fromDocument(doc: SpecDoc) : ValueParser<ColorTheme> = when (doc)
        {
            is DocDict -> effApply(::ColorTheme,
                                   // ThemeId
                                   doc.list("theme_colors") ap {
                                        it.mapSet { ThemeColorId.fromDocument(it) }
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        // BUILT-IN THEMES
        // -----------------------------------------------------------------------------------------

        val transparent = ColorTheme(setOf(ThemeColorId(ThemeId.Light(), ColorId.Transparent()),
                                           ThemeColorId(ThemeId.Dark(), ColorId.Transparent())))

        val black = ColorTheme(setOf(ThemeColorId(ThemeId.Light(), ColorId.Dark()),
                                     ThemeColorId(ThemeId.Dark(), ColorId.Dark())))
    }


    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    private val colorIdByThemeId : Map<ThemeId,ColorId> =
            this.themeColorIds.associateBy({it.themeId}, {it.colorId})


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun themeColorId(themeId : ThemeId) : ColorId? = this.colorIdByThemeId[themeId]

}

