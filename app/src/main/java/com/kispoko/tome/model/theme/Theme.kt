
package com.kispoko.tome.model.theme


import android.graphics.Color
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Conj
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Theme
 */
data class Theme(override val id : UUID,
                 val themeId : Prim<ThemeId>,
                 val palette : Conj<ThemeColor>,
                 val uiColors : Comp<UIColors>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(themeId : ThemeId, palette : Set<ThemeColor>, uiColors : UIColors)
        : this(UUID.randomUUID(),
               Prim(themeId),
               Conj(palette.toMutableSet()),
               Comp(uiColors))


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
                                   },
                                   // UI Colors
                                   doc.at("ui_colors") ap {
                                       effApply(::Comp, UIColors.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun themeId() : ThemeId = this.themeId.value

    fun uiColors() : UIColors = this.uiColors.value


    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    private val colorById : MutableMap<ColorId,Int> = this.palette.set
                                                          .associateBy({it.colorId}, {it.color})
                                                          .toMutableMap()


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun color(colorId : ColorId) : Int? = when (colorId)
    {
        is ColorId.Black       -> Color.BLACK
        is ColorId.White       -> Color.WHITE
        is ColorId.Transparent -> Color.TRANSPARENT
        is ColorId.Theme       -> this.colorById[colorId]
    }

}


data class UIColors(override val id: UUID,
                    val toolbarColorId : Prim<ColorId>,
                    val tabBarColorId : Prim<ColorId>,
                    val tabTextNormalColorId : Prim<ColorId>,
                    val tabTextSelectedColorId : Prim<ColorId>,
                    val tabUnderlineColorId : Prim<ColorId>,
                    val titleColorId : Prim<ColorId>,
                    val toolbarIconsColorId : Prim<ColorId>,
                    val bottomBarBackgroundColorId : Prim<ColorId>,
                    val bottomBarActiveColorId : Prim<ColorId>,
                    val bottomBarInactiveColorId : Prim<ColorId>) : Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(toolbarColorId : ColorId,
                tabBarColorId : ColorId,
                tabTextNormalColorId : ColorId,
                tabTextSelectedColorId : ColorId,
                tabUnderlineColorId: ColorId,
                titleColorId : ColorId,
                toolbarIconsColorId : ColorId,
                bottomBarBackgroundColorId : ColorId,
                bottomBarActiveColorId : ColorId,
                bottomBarInactiveColorId : ColorId)
        : this(UUID.randomUUID(),
               Prim(toolbarColorId),
               Prim(tabBarColorId),
               Prim(tabTextNormalColorId),
               Prim(tabTextSelectedColorId),
               Prim(tabUnderlineColorId),
               Prim(titleColorId),
               Prim(toolbarIconsColorId),
               Prim(bottomBarBackgroundColorId),
               Prim(bottomBarActiveColorId),
               Prim(bottomBarInactiveColorId))


    companion object : Factory<UIColors>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<UIColors> = when (doc)
        {
            is DocDict -> effApply(::UIColors,
                                   // Toolbar Color
                                   doc.at("toolbar") ap { ColorId.fromDocument(it) },
                                   // Tab Bar
                                   doc.at("tab_bar") ap { ColorId.fromDocument(it) },
                                   // Tab Text Normal
                                   doc.at("tab_text_normal") ap { ColorId.fromDocument(it) },
                                   // Tab Text Selected
                                   doc.at("tab_text_selected") ap { ColorId.fromDocument(it) },
                                   // Tab Underline
                                   doc.at("tab_underline") ap { ColorId.fromDocument(it) },
                                   // Title
                                   doc.at("title") ap { ColorId.fromDocument(it) },
                                   // Icon
                                   doc.at("icon") ap { ColorId.fromDocument(it) },
                                   // Bottom Bar
                                   doc.at("bottom_bar_background") ap { ColorId.fromDocument(it) },
                                   // Bottom Bar Active
                                   doc.at("bottom_bar_active") ap { ColorId.fromDocument(it) },
                                   // Bottom Bar Inactive
                                   doc.at("bottom_bar_inactive") ap { ColorId.fromDocument(it) }
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun toolbarColorId() : ColorId = this.toolbarColorId.value

    fun tabBarColorId() : ColorId = this.tabBarColorId.value

    fun tabTextNormalColorId() : ColorId = this.tabTextNormalColorId.value

    fun tabTextSelectedColorId() : ColorId = this.tabTextSelectedColorId.value

    fun tabUnderlineColorId() : ColorId = this.tabUnderlineColorId.value

    fun titleColorId() : ColorId = this.titleColorId.value

    fun toolbarIconsColorId() : ColorId = this.toolbarIconsColorId.value

    fun bottomBarBackgroundColorId() : ColorId = this.bottomBarBackgroundColorId.value

    fun bottomBarActiveColorId() : ColorId = this.bottomBarActiveColorId.value

    fun bottomBarInactiveColorId() : ColorId = this.bottomBarInactiveColorId.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

}



sealed class ThemeId : Serializable
{


    object Light : ThemeId()
    object Dark : ThemeId()


    /**
     * Custom Theme Id
     */
    data class Custom(val name : String) : ThemeId()
    {
        companion object : Factory<ThemeId.Custom>
        {
            override fun fromDocument(doc: SpecDoc) : ValueParser<ThemeId.Custom> = when (doc)
            {
                is DocText ->
                    effValue(Custom(doc.text))
                else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
            }
        }
    }


    companion object
    {
        fun fromDocument(doc : SpecDoc) : ValueParser<ThemeId> = when (doc)
        {
            is DocText ->
            {
                when (doc.text)
                {
                    "light" -> effValue<ValueError,ThemeId>(ThemeId.Light)
                    "dark"  -> effValue<ValueError,ThemeId>(ThemeId.Dark)
                    else    -> effValue<ValueError,ThemeId>(ThemeId.Custom(doc.text))
                }
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }

    }

}


/**
 * Theme Color Id
 */
data class ThemeColorId(val themeId : ThemeId, val colorId : ColorId) : Serializable
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
data class ThemeColor(val colorId : ColorId, val color : Int) : Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(colorId : ColorId, colorHexString : String)
        : this(colorId, Color.parseColor(colorHexString))


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
data class ColorTheme(val themeColorIds: Set<ThemeColorId>) : Serializable
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

        val transparent = ColorTheme(setOf(ThemeColorId(ThemeId.Light, ColorId.Transparent),
                                           ThemeColorId(ThemeId.Dark, ColorId.Transparent)))

        val black = ColorTheme(setOf(ThemeColorId(ThemeId.Light, ColorId.Black),
                                     ThemeColorId(ThemeId.Dark, ColorId.Black)))
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

