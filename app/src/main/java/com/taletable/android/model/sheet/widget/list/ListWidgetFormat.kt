
package com.taletable.android.model.sheet.widget.list


import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.model.engine.message.Message
import com.taletable.android.model.sheet.style.ElementFormat
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.model.sheet.widget.OfficialWidgetFormat
import com.taletable.android.model.sheet.widget.WidgetFormat
import effect.effValue
import effect.split
import effect.apply
import effect.effError
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



// -----------------------------------------------------------------------------------------
// | LIST WIDGET FORMAT
// -----------------------------------------------------------------------------------------

sealed class ListWidgetFormat
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ListWidgetFormat>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<ListWidgetFormat> =
            when (doc.case())
            {
                "list_widget_format_custom" -> ListWidgetFormatCustom.fromDocument(doc) as ValueParser<ListWidgetFormat>
                "widget_format_official"    -> ListWidgetFormatOfficial.fromDocument(doc) as ValueParser<ListWidgetFormat>
                else                 -> {
                    effError(UnknownCase(doc.case(), doc.path))
                }
            }
    }

}


// ---------------------------------------------------------------------------------------------
// | LIST WIDGET FORMAT: *THEME*
// ---------------------------------------------------------------------------------------------

data class ListWidgetFormatOfficial(
    val format : OfficialWidgetFormat
) : ListWidgetFormat()
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ListWidgetFormatOfficial>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<ListWidgetFormatOfficial> =
            apply(::ListWidgetFormatOfficial, OfficialWidgetFormat.fromDocument(doc))
    }

}

// -----------------------------------------------------------------------------------------
// | LIST WIDGET FORMAT: *CUSTOM*
// -----------------------------------------------------------------------------------------

/**
 * List Widget Format
 */
data class ListWidgetFormatCustom(
        val widgetFormat : WidgetFormat,
        val viewType : ListViewType,
        val listFormat : ElementFormat,
        val itemFormat : TextFormat,
        val itemInactiveFormat : TextFormat,
        val defaultItemText : ListWidgetDefaultItemText,
        val descriptionFormat : TextFormat,
        val titleBarFormat : ElementFormat,
        val titleFormat : TextFormat,
        val editButtonFormat : TextFormat,
        val constraintFormat : ListWidgetConstraintFormat)
         : ListWidgetFormat(), ToDocument, Serializable
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ListWidgetFormatCustom>
    {

        private fun defaultWidgetFormat()       = WidgetFormat.default()
        private fun defaultViewType()           = ListViewType.ParagraphCommas
        private fun defaultListFormat()         = ElementFormat.default()
        private fun defaultItemFormat()         = TextFormat.default()
        private fun defaultItemInactiveFormat() = TextFormat.default()
        private fun defaultItemText()           = ListWidgetDefaultItemText("")
        private fun defaultDescriptionFormat()  = TextFormat.default()
        private fun defaultTitleBarFormat()     = ElementFormat.default()
        private fun defaultTitleFormat()        = TextFormat.default()
        private fun defaultEditButtonFormat()   = TextFormat.default()
        private fun defaultConstraintFormat()   = ListWidgetConstraintFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<ListWidgetFormatCustom> = when (doc)
        {
            is DocDict ->
            {
                apply(::ListWidgetFormatCustom,
                     // Widget Format
                     split(doc.maybeAt("widget_format"),
                           effValue(defaultWidgetFormat()),
                           { WidgetFormat.fromDocument(it) }),
                     // View Type
                     split(doc.maybeAt("view_type"),
                           effValue<ValueError, ListViewType>(defaultViewType()),
                           { ListViewType.fromDocument(it) }),
                     // List Format
                     split(doc.maybeAt("list_format"),
                           effValue(defaultListFormat()),
                           { ElementFormat.fromDocument(it) }),
                     // Item Format
                     split(doc.maybeAt("item_format"),
                           effValue(defaultItemFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Item Inactive Format
                     split(doc.maybeAt("item_inactive_format"),
                           effValue(defaultItemInactiveFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Description Format
                     split(doc.maybeAt("default_item_text"),
                           effValue(defaultItemText()),
                           { ListWidgetDefaultItemText.fromDocument(it) }),
                     // Description Format
                     split(doc.maybeAt("description_format"),
                           effValue(defaultDescriptionFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Title Bar Format
                     split(doc.maybeAt("title_bar_format"),
                           effValue(defaultTitleBarFormat()),
                           { ElementFormat.fromDocument(it) }),
                     // Title Format
                     split(doc.maybeAt("title_format"),
                           effValue(defaultTitleFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Edit Button Format
                     split(doc.maybeAt("edit_button_format"),
                           effValue(defaultEditButtonFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Constraint Format
                     split(doc.maybeAt("constraint_format"),
                           effValue(defaultConstraintFormat()),
                           { ListWidgetConstraintFormat.fromDocument(it) })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ListWidgetFormatCustom(defaultWidgetFormat(),
                defaultViewType(),
                defaultListFormat(),
                defaultItemFormat(),
                defaultItemInactiveFormat(),
                defaultItemText(),
                defaultDescriptionFormat(),
                defaultTitleBarFormat(),
                defaultTitleFormat(),
                defaultEditButtonFormat(),
                defaultConstraintFormat())
    }

    // | TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat.toDocument(),
        "item_format" to this.itemFormat.toDocument(),
        "description_format" to this.descriptionFormat.toDocument()
    ))


}


/**
 * List Widget Constraint Format
 */
data class ListWidgetConstraintFormat(val textFormat : TextFormat,
                                      val failTextFormat : TextFormat,
                                      val message : Maybe<Message>)
                                       : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ListWidgetConstraintFormat>
    {

        private fun defaultTextFormat()     = TextFormat.default()
        private fun defaultFailTextFormat() = TextFormat.default()
        private fun defaultMessage()        = Nothing<Message>()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<ListWidgetConstraintFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::ListWidgetConstraintFormat,
                     // Text Format
                     split(doc.maybeAt("text_format"),
                           effValue(defaultTextFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Fail Text Format
                     split(doc.maybeAt("fail_text_format"),
                           effValue(defaultFailTextFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Message
                     split(doc.maybeAt("message"),
                           effValue<ValueError,Maybe<Message>>(defaultMessage()),
                           { apply(::Just, Message.fromDocument(it))  })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ListWidgetConstraintFormat(defaultTextFormat(),
                defaultFailTextFormat(),
                defaultMessage())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "text_format" to this.textFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun textFormat() : TextFormat = this.textFormat


    fun failTextFormat() : TextFormat = this.failTextFormat


    fun message() : Maybe<Message> = this.message

}


/**
 * List View Type
 */
sealed class ListViewType : ToDocument, SQLSerializable, Serializable
{

    object ParagraphCommas : ListViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "paragraph_commas" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("paragraph_commas")

    }

    object Rows : ListViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "rows" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("rows")

    }

    object RowsSimpleEditor : ListViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "rows_simple_editor" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("rows_simple_editor")

    }

    object Pool : ListViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "pool" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("pool")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<ListViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "paragraph_commas"   -> effValue<ValueError, ListViewType>(ParagraphCommas)
                "rows"               -> effValue<ValueError, ListViewType>(Rows)
                "rows_simple_editor" -> effValue<ValueError, ListViewType>(RowsSimpleEditor)
                "pool"               -> effValue<ValueError, ListViewType>(Pool)
                else                 -> effError<ValueError, ListViewType>(
                                            UnexpectedValue("ListViewType", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * List Widget Default Item Text
 */
data class ListWidgetDefaultItemText(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ListWidgetDefaultItemText>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ListWidgetDefaultItemText> = when (doc)
        {
            is DocText -> effValue(ListWidgetDefaultItemText(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}

