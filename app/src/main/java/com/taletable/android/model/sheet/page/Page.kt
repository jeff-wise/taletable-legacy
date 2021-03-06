
package com.taletable.android.model.sheet.page


import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.taletable.android.db.*
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.RowValue3
import com.taletable.android.lib.orm.schema.PrimValue
import com.taletable.android.lib.orm.schema.ProdValue
import com.taletable.android.lib.orm.sql.*
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.model.sheet.group.Group
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.style.ElementFormat
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.sheet.SheetComponent
import com.taletable.android.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



/**
 * Page
 */
data class Page(val pageName : PageName,
                val format : PageFormat,
                val index : PageIndex,
                val groups : List<Group>)
                 : ToDocument, SheetComponent, Serializable
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

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
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context, groupContext : Maybe<GroupContext>)
    {
        this.groups.forEach { it.onSheetComponentActive(entityId, context, groupContext) }
    }


    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(entityId : EntityId, context : Context) : View
    {
        val layout = this.viewLayout(entityId, context)

        this.groups.forEach {

            val trigger = it.trigger()
            when (trigger) {
                is Just -> {
                    val isActive = trigger.value.isActive(entityId)
                    if (isActive) {
                        layout.addView(it.view(entityId, context))
                    }
                }
                is Nothing -> {
                    layout.addView(it.view(entityId, context))
                }
            }

        }

        return layout
    }


    private fun viewLayout(entityId : EntityId, context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        this.viewId             = Util.generateViewId()
        layout.id               = this.viewId

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        val bgColorTheme = this.format().elementFormat().backgroundColorTheme()
        layout.backgroundColor  = colorOrBlack(bgColorTheme, entityId)

        layout.paddingSpacing   = this.format().elementFormat().padding()

        return layout.linearLayout(context)
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
data class PageFormat(val elementFormat : ElementFormat)
                       : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

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

}
