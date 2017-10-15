
package com.kispoko.tome.model.sheet.page


import android.view.View
import android.widget.LinearLayout
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.style.Spacing
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.sheet.SheetComponent
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Page
 */
data class Page(override val id : UUID,
                val pageName : Prim<PageName>,
                val format : Comp<PageFormat>,
                val index : Prim<PageIndex>,
                val groups : CollS<Group>)
                 : Model, ToDocument, SheetComponent, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.pageName.name  = "page_name"
        this.format.name    = "format"
        this.index.name     = "index"
        this.groups.name    = "groups"
    }


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(pageName : PageName,
                format : PageFormat,
                index : PageIndex,
                groups : MutableList<Group>)
        : this(UUID.randomUUID(),
               Prim(pageName),
               Comp(format),
               Prim(index),
               CollS(groups))


    companion object
    {
        fun fromDocument(doc : SchemaDoc, index : Int) : ValueParser<Page> = when (doc)
        {
            is DocDict -> effApply(::Page,
                                   // Name
                                   doc.at("name") ap { PageName.fromDocument(it) },
                                   // Format
                                   split(doc.maybeAt("format"),
                                         effValue(PageFormat.default),
                                         { PageFormat.fromDocument(it) }),
                                   // Index
                                   effValue(PageIndex(index)),
                                   // Groups
                                   doc.list("groups") ap { docList ->
                                       docList.mapIndexedMut {
                                           doc, index -> Group.fromDocument(doc,index) }
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun name() : PageName = this.pageName.value

    fun nameString() : String = this.pageName.value.value

    fun format() : PageFormat = this.format.value

    fun indexInt() : Int = this.index.value.value

    fun groups() : List<Group> = this.groups.list


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "name" to this.name().toDocument(),
        "format" to this.format().toDocument(),
        "groups" to DocList(this.groups().map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "page"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext : SheetContext)
    {
        this.groups.list.forEach { it.onSheetComponentActive(sheetContext) }
    }


    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(sheetUIContext: SheetUIContext) : View
    {
        val layout = this.viewLayout(sheetUIContext)

        this.groups.list.forEach { layout.addView(it.view(sheetUIContext)) }

        return layout
    }


    private fun viewLayout(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        this.viewId             = Util.generateViewId()
        layout.id               = this.viewId

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
                                                     this.format().backgroundColorTheme())

        layout.paddingSpacing   = this.format().padding()

        return layout.linearLayout(sheetUIContext.context)
    }

}


/**
 * Page Name
 */
data class PageName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<PageName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<PageName> = when (doc)
        {
            is DocText -> effValue(PageName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Page Index
 */
data class PageIndex(val value : Int) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<PageIndex>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<PageIndex> = when (doc)
        {
            is DocNumber -> effValue(PageIndex(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({this.value})

}


/**
 * Page Format
 */
data class PageFormat(override val id : UUID,
                      val backgroundColorTheme : Prim<ColorTheme>,
                      val padding : Comp<Spacing>)
                       : Model, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.backgroundColorTheme.name  = "background_color_theme"
        this.padding.name               = "padding"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(colorTheme : ColorTheme,
                padding : Spacing)
        : this(UUID.randomUUID(),
               Prim(colorTheme),
               Comp(padding))


    companion object : Factory<PageFormat>
    {

        private val defaultBackgroundColorTheme = ColorTheme.transparent
        private val defaultPadding              = Spacing.default()

        override fun fromDocument(doc: SchemaDoc): ValueParser<PageFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::PageFormat,
                         // Background Color
                         split(doc.maybeAt("background_color_theme"),
                               effValue(defaultBackgroundColorTheme),
                               { ColorTheme.fromDocument(it) }),
                         // Padding
                         split(doc.maybeAt("padding"),
                               effValue(defaultPadding),
                               { Spacing.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : PageFormat = PageFormat(defaultBackgroundColorTheme,
                                              defaultPadding)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun backgroundColorTheme() : ColorTheme = this.backgroundColorTheme.value

    fun padding() : Spacing = this.padding.value


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "background_color_theme" to this.backgroundColorTheme().toDocument(),
        "padding" to this.padding().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "page_format"

    override val modelObject = this

}
