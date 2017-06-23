
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
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.sheet.SheetComponent
import com.kispoko.tome.rts.sheet.SheetContext
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
                val groups : CollS<Group>) : Model, SheetComponent, Serializable
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
        fun fromDocument(doc : SpecDoc, index : Int) : ValueParser<Page> = when (doc)
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

    fun view(sheetContext : SheetContext) : View
    {
        val layout = this.viewLayout(sheetContext)

        this.groups.list.forEach { layout.addView(it.view(sheetContext)) }

        return layout
    }


    private fun viewLayout(sheetContext : SheetContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        this.viewId             = Util.generateViewId()
        layout.id               = this.viewId

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.backgroundColor  = SheetManager.color(sheetContext.sheetId,
                                                     this.format().backgroundColorTheme())

        return layout.linearLayout(sheetContext.context)
    }

}


/**
 * Page Name
 */
data class PageName(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<PageName>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<PageName> = when (doc)
        {
            is DocText -> effValue(PageName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


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
        override fun fromDocument(doc : SpecDoc) : ValueParser<PageIndex> = when (doc)
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
                      val backgroundColorTheme : Prim<ColorTheme>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.backgroundColorTheme.name = "background_color_theme"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(colorTheme : ColorTheme) : this(UUID.randomUUID(), Prim(colorTheme))


    companion object : Factory<PageFormat>
    {

        override fun fromDocument(doc : SpecDoc) : ValueParser<PageFormat> = when (doc)
        {
            is DocDict -> effApply(::PageFormat,
                                   // Model Id
                                    effValue(UUID.randomUUID()),
                                   // Background Color
                                   doc.at("background_color_theme") ap {
                                       effApply(::Prim, ColorTheme.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : PageFormat = PageFormat(ColorTheme.transparent)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun backgroundColorTheme() : ColorTheme = this.backgroundColorTheme.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "page_format"

    override val modelObject = this

}
