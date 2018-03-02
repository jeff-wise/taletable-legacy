
package com.kispoko.tome.model.sheet.page


import android.view.View
import android.widget.LinearLayout
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue1
import com.kispoko.tome.lib.orm.RowValue4
import com.kispoko.tome.lib.orm.schema.CollValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.style.ElementFormat
import com.kispoko.tome.rts.entity.sheet.SheetComponent
import com.kispoko.tome.rts.entity.sheet.SheetUIContext
import com.kispoko.tome.rts.entity.sheet.SheetManager
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
                val pageName : PageName,
                val format : PageFormat,
                val index : PageIndex,
                val groups : List<Group>)
                 : ProdType, ToDocument, SheetComponent, Serializable
{

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
                groups : List<Group>)
        : this(UUID.randomUUID(),
               pageName,
               format,
               index,
               groups)


    companion object
    {
        fun fromDocument(doc : SchemaDoc, index : Int) : ValueParser<Page> = when (doc)
        {
            is DocDict ->
            {
                apply(::Page,
                      // Name
                      doc.at("name") ap { PageName.fromDocument(it) },
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(PageFormat.default()),
                            { PageFormat.fromDocument(it) }),
                      // Index
                      effValue(PageIndex(index)),
                      // Groups
                      doc.list("groups") ap { docList ->
                          docList.mapIndexedMut {
                              itemDoc, itemIndex -> Group.fromDocument(itemDoc,itemIndex) }
                      })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun name() : PageName = this.pageName

    fun nameString() : String = this.pageName.value

    fun format() : PageFormat = this.format

    fun indexInt() : Int = this.index.value

    fun groups() : List<Group> = this.groups


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


    override val prodTypeObject = this


    override fun rowValue() : DB_PageValue =
        RowValue4(pageTable, PrimValue(this.pageName),
                             ProdValue(this.format),
                             PrimValue(this.index),
                             CollValue(this.groups))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetUIContext : SheetUIContext)
    {
        this.groups.forEach { it.onSheetComponentActive(sheetUIContext) }
    }


    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(sheetUIContext: SheetUIContext) : View
    {
        val layout = this.viewLayout(sheetUIContext)

        this.groups.forEach { layout.addView(it.view(sheetUIContext)) }

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
                                                     this.format().elementFormat().backgroundColorTheme())

        layout.paddingSpacing   = this.format().elementFormat().padding()

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

    override fun asSQLValue() = this.value.asSQLValue()

}


/**
 * Page Format
 */
data class PageFormat(override val id : UUID,
                      val elementFormat : ElementFormat)
                       : ProdType, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(elementFormat : ElementFormat)
        : this(UUID.randomUUID(),
               elementFormat)


    companion object : Factory<PageFormat>
    {

        private fun defaultElementFormat() = ElementFormat.default()


        override fun fromDocument(doc: SchemaDoc): ValueParser<PageFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::PageFormat,
                      // Element Format
                      split(doc.maybeAt("element_format"),
                            effValue(defaultElementFormat()),
                            { ElementFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = PageFormat(defaultElementFormat())

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun elementFormat() : ElementFormat = this.elementFormat


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "element_format" to this.elementFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_PageFormatValue =
        RowValue1(pageFormatTable, ProdValue(this.elementFormat))

}
