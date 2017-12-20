
package com.kispoko.tome.model.theme


import android.graphics.Color
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue10
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLBlob
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.apply
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*



/**
 * Theme
 */
data class Theme(override val id : UUID,
                 val themeId : ThemeId,
                 val palette : MutableList<ThemeColor>,
                 val uiColors : UIColors)
                  : ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(themeId : ThemeId,
                palette : List<ThemeColor>,
                uiColors : UIColors)
        : this(UUID.randomUUID(),
               themeId,
               palette.toMutableList(),
               uiColors)


    companion object : Factory<Theme>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Theme> = when (doc)
        {
            is DocDict ->
            {
                apply(::Theme,
                      // Theme Id
                      doc.at("id") ap { ThemeId.fromDocument(it) },
                      // Theme Colors
                      doc.list("palette") ap {
                           it.map { ThemeColor.fromDocument(it) }
                      },
                      // UI Colors
                      doc.at("ui_colors") ap { UIColors.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun themeId() : ThemeId = this.themeId


    fun uiColors() : UIColors = this.uiColors


    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    private val colorById : MutableMap<ColorId,Int> = this.palette
                                                          .associateBy({it.colorId}, {it.color})
                                                          .toMutableMap()


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_ThemeValue =
        RowValue3(themeTable,
                  PrimValue(this.themeId),
                  PrimValue(ThemeColorSet(this.palette)),
                  ProdValue(this.uiColors))


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
                    val toolbarBackgroundColorId : ColorId,
                    val toolbarIconsColorId : ColorId,
                    val toolbarTitleColorId : ColorId,
                    val tabBarBackgroundColorId : ColorId,
                    val tabTextNormalColorId : ColorId,
                    val tabTextSelectedColorId : ColorId,
                    val tabUnderlineColorId : ColorId,
                    val bottomBarBackgroundColorId : ColorId,
                    val bottomBarActiveColorId : ColorId,
                    val bottomBarInactiveColorId : ColorId) : ProdType
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(toolbarBackgroundColorId : ColorId,
                toolbarIconsColorId : ColorId,
                toolbarTitleColorId : ColorId,
                tabBarBackgroundColorId : ColorId,
                tabTextNormalColorId : ColorId,
                tabTextSelectedColorId : ColorId,
                tabUnderlineColorId: ColorId,
                bottomBarBackgroundColorId : ColorId,
                bottomBarActiveColorId : ColorId,
                bottomBarInactiveColorId : ColorId)
        : this(UUID.randomUUID(),
               toolbarBackgroundColorId,
               toolbarIconsColorId,
               toolbarTitleColorId,
               tabBarBackgroundColorId,
               tabTextNormalColorId,
               tabTextSelectedColorId,
               tabUnderlineColorId,
               bottomBarBackgroundColorId,
               bottomBarActiveColorId,
               bottomBarInactiveColorId)


    companion object : Factory<UIColors>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<UIColors> = when (doc)
        {
            is DocDict ->
            {
                apply(::UIColors,
                      // Toolbar Color
                      doc.at("toolbar_background") ap { ColorId.fromDocument(it) },
                      // Toolbar Icons
                      doc.at("toolbar_icons") ap { ColorId.fromDocument(it) },
                      // Title
                      doc.at("title") ap { ColorId.fromDocument(it) },
                      // Tab Bar
                      doc.at("tab_bar_background") ap { ColorId.fromDocument(it) },
                      // Tab Text Normal
                      doc.at("tab_text_normal") ap { ColorId.fromDocument(it) },
                      // Tab Text Selected
                      doc.at("tab_text_selected") ap { ColorId.fromDocument(it) },
                      // Tab Underline
                      doc.at("tab_underline") ap { ColorId.fromDocument(it) },
                      // Bottom Bar
                      doc.at("bottom_bar_background") ap { ColorId.fromDocument(it) },
                      // Bottom Bar Active
                      doc.at("bottom_bar_active") ap { ColorId.fromDocument(it) },
                      // Bottom Bar Inactive
                      doc.at("bottom_bar_inactive") ap { ColorId.fromDocument(it) }
                      )
            }

            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun toolbarBackgroundColorId() : ColorId = this.toolbarBackgroundColorId


    fun tabBarBackgroundColorId() : ColorId = this.tabBarBackgroundColorId


    fun tabTextNormalColorId() : ColorId = this.tabTextNormalColorId


    fun tabTextSelectedColorId() : ColorId = this.tabTextSelectedColorId


    fun tabUnderlineColorId() : ColorId = this.tabUnderlineColorId


    fun toolbarTitleColorId() : ColorId = this.toolbarTitleColorId


    fun toolbarIconsColorId() : ColorId = this.toolbarIconsColorId


    fun bottomBarBackgroundColorId() : ColorId = this.bottomBarBackgroundColorId


    fun bottomBarActiveColorId() : ColorId = this.bottomBarActiveColorId


    fun bottomBarInactiveColorId() : ColorId = this.bottomBarInactiveColorId


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_UIColorsValue =
        RowValue10(uiColorsTable,
                   PrimValue(this.toolbarBackgroundColorId),
                   PrimValue(this.toolbarIconsColorId),
                   PrimValue(this.toolbarTitleColorId),
                   PrimValue(this.toolbarBackgroundColorId),
                   PrimValue(this.tabTextNormalColorId),
                   PrimValue(this.tabTextSelectedColorId),
                   PrimValue(this.tabUnderlineColorId),
                   PrimValue(this.bottomBarBackgroundColorId),
                   PrimValue(this.bottomBarActiveColorId),
                   PrimValue(this.bottomBarInactiveColorId))

}



sealed class ThemeId : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CASES
    // -----------------------------------------------------------------------------------------

    object Light : ThemeId()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"light"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("light")

    }


    object Dark : ThemeId()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"dark"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("dark")

    }


    /**
     * Custom Theme Id
     */
    data class Custom(val name : String) : ThemeId()
    {

        // -------------------------------------------------------------------------------------
        // CONSTRUCTORS
        // -------------------------------------------------------------------------------------

        companion object : Factory<ThemeId.Custom>
        {
            override fun fromDocument(doc: SchemaDoc): ValueParser<Custom> = when (doc)
            {
                is DocText ->
                    effValue(Custom(doc.text))
                else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
            }
        }


        // -------------------------------------------------------------------------------------
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({this.name})


        // -------------------------------------------------------------------------------------
        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText(this.name)

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<ThemeId> = when (doc)
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


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString() = when (this)
    {
        is Light  -> "Light"
        is Dark   -> "Dark"
        is Custom -> this.name
    }
}


/**
 * Theme Color Id
 */
data class ThemeColorId(val themeId : ThemeId, val colorId : ColorId)
                : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ThemeColorId>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ThemeColorId> = when (doc)
        {
            is DocDict ->
            {
                apply(::ThemeColorId,
                      // Theme Id
                      doc.at("theme_id") ap { ThemeId.fromDocument(it) },
                      // Color Id
                      doc.at("color_id") ap { ColorId.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "theme_id" to this.themeId.toDocument(),
        "color_id" to this.colorId.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLText({ "${this.themeId}:$this.colorId" })

}


/**
 * Theme Color
 */
data class ThemeColor(val colorId : ColorId, val color : Int) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(colorId : ColorId,
                colorHexString : String)
        : this(colorId,
               Color.parseColor(colorHexString))


    companion object : Factory<ThemeColor>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ThemeColor> = when (doc)
        {
            is DocDict -> apply(::ThemeColor,
                                // Color Id
                                doc.at("color_id") ap { ColorId.fromDocument(it) },
                                // Color
                                effApply({Color.parseColor(it) }, doc.text("color"))
                                )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLText({ "$this.colorId:$this.color" })

}


/**
 * Theme Color Set
 */
data class ThemeColorSet(val themeColors : List<ThemeColor>) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ThemeColorSet>
    {

        override fun fromDocument(doc : SchemaDoc) : ValueParser<ThemeColorSet> = when (doc)
        {
            is DocList -> effect.apply(::ThemeColorSet, doc.map { ThemeColor.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.LIST, docType(doc), doc.path))
        }


        fun empty() = ThemeColorSet(mutableListOf())
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLText({ this.themeColors.joinToString(",") })

}


/**
 * Color Theme
 *
 * A pallette of colors for some object.
 */
data class ColorTheme(val themeColorIds : Set<ThemeColorId>)
                : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ColorTheme>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<ColorTheme> = when (doc)
        {
            is DocDict -> effApply(::ColorTheme,
                                   // ThemeId
                                   doc.list("theme_color_ids") ap {
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

        val white = ColorTheme(setOf(ThemeColorId(ThemeId.Light, ColorId.White),
                                     ThemeColorId(ThemeId.Dark, ColorId.White)))
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


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "theme_color_ids" to DocList(this.themeColorIds.map { it.toDocument() })
    ))

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZATION
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({SerializationUtils.serialize(this)})

}

