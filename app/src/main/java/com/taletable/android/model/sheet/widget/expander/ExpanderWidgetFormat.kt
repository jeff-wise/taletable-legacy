
package com.taletable.android.model.sheet.widget.expander


import com.taletable.android.lib.Factory
import com.taletable.android.model.sheet.style.ElementFormat
import com.taletable.android.model.sheet.style.Icon
import com.taletable.android.model.sheet.style.IconType
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.model.sheet.widget.OfficialWidgetFormat
import com.taletable.android.model.sheet.widget.WidgetFormat
import effect.effError
import effect.apply
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Nothing
import maybe.Maybe
import java.io.Serializable


// -----------------------------------------------------------------------------------------
// | EXPANDER WIDGET FORMAT
// -----------------------------------------------------------------------------------------

sealed class ExpanderWidgetFormat
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ExpanderWidgetFormat>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<ExpanderWidgetFormat> =
            when (doc.case())
            {
                "expander_widget_format_custom" -> ExpanderWidgetFormatCustom.fromDocument(doc) as ValueParser<ExpanderWidgetFormat>
                "widget_format_official"        -> ExpanderWidgetFormatOfficial.fromDocument(doc) as ValueParser<ExpanderWidgetFormat>
                else                 -> {
                    effError(UnknownCase(doc.case(), doc.path))
                }
            }
    }

}


// ---------------------------------------------------------------------------------------------
// | EXPANDER WIDGET FORMAT: *THEME*
// ---------------------------------------------------------------------------------------------


data class ExpanderWidgetFormatOfficial(
    val format : OfficialWidgetFormat
) : ExpanderWidgetFormat()
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ExpanderWidgetFormatOfficial>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<ExpanderWidgetFormatOfficial> =
            apply(::ExpanderWidgetFormatOfficial, OfficialWidgetFormat.fromDocument(doc))
    }

}


// ---------------------------------------------------------------------------------------------
// | EXPANDER WIDGET FORMAT: *CUSTOM*
// ---------------------------------------------------------------------------------------------
/**
 * Expander Widget Format
 */
data class ExpanderWidgetFormatCustom(
        val widgetFormat : WidgetFormat,
        val viewType : ExpanderWidgetViewType,
        val contentFormat : ElementFormat,
        val headerOpenFormat : TextFormat,
        val headerClosedFormat : TextFormat,
        val headerLabelOpenFormat : TextFormat,
        val headerLabelClosedFormat : TextFormat,
        val headerOpenIcon : Icon,
        val headerClosedIcon : Icon,
        val avatarFormat : Maybe<TextFormat>,
        val avatarText : Maybe<String>)
         : ExpanderWidgetFormat(), ToDocument, Serializable
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ExpanderWidgetFormatCustom>
    {

        private fun defaultWidgetFormat()             = WidgetFormat.default()
        private fun defaultViewType()                 = ExpanderWidgetViewType.Plain
        private fun defaultContentFormat()            = ElementFormat.default()
        private fun defaultHeaderOpenFormat()         = TextFormat.default()
        private fun defaultHeaderClosedFormat()       = TextFormat.default()
        private fun defaultHeaderLabelOpenFormat()    = TextFormat.default()
        private fun defaultHeaderLabelClosedFormat()  = TextFormat.default()
        private fun defaultHeaderOpenIcon()           = Icon.default(IconType.ChevronDownBold)
        private fun defaultHeaderClosedIcon()         = Icon.default(IconType.ChevronRightBold)


        override fun fromDocument(doc : SchemaDoc) : ValueParser<ExpanderWidgetFormatCustom> = when (doc)
        {
            is DocDict ->
            {
                apply(::ExpanderWidgetFormatCustom,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // View Type
                      split(doc.maybeAt("view_type"),
                            effValue<ValueError, ExpanderWidgetViewType>(defaultViewType()),
                            { ExpanderWidgetViewType.fromDocument(it) }),
                      // Content Format
                     split(doc.maybeAt("content_format"),
                           effValue(defaultContentFormat()),
                           { ElementFormat.fromDocument(it) }),
                      // Header Open
                      split(doc.maybeAt("header_open_format"),
                            effValue(defaultHeaderOpenFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Header Closed
                      split(doc.maybeAt("header_closed_format"),
                            effValue(defaultHeaderClosedFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Header Label Open
                      split(doc.maybeAt("header_label_open_format"),
                            effValue(defaultHeaderLabelOpenFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Header Label Closed
                      split(doc.maybeAt("header_label_closed_format"),
                            effValue(defaultHeaderLabelClosedFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Header Open Icon
                      split(doc.maybeAt("header_open_icon"),
                            effValue(defaultHeaderOpenIcon()),
                            { Icon.fromDocument(it) }),
                      // Header Closed Icon
                      split(doc.maybeAt("header_closed_icon"),
                            effValue(defaultHeaderClosedIcon()),
                            { Icon.fromDocument(it) }),
                      // Avatar Format
                      split(doc.maybeAt("avatar_format"),
                            effValue<ValueError,Maybe<TextFormat>>(Nothing()),
                            { apply(::Just, TextFormat.fromDocument(it)) }),
                      // Avatar Text
                      split(doc.maybeText("avatar_text"),
                            effValue(Nothing()),
                            { effValue<ValueError,Maybe<String>>(Just(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ExpanderWidgetFormatCustom(
                            defaultWidgetFormat(),
                            defaultViewType(),
                            defaultContentFormat(),
                            defaultHeaderOpenFormat(),
                            defaultHeaderClosedFormat(),
                            defaultHeaderLabelOpenFormat(),
                            defaultHeaderLabelClosedFormat(),
                            defaultHeaderOpenIcon(),
                            defaultHeaderClosedIcon(),
                            Nothing(),
                            Nothing())
    }


    // | TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat.toDocument(),
        "view_type" to this.viewType.toDocument(),
        "content_format" to this.contentFormat.toDocument(),
        "header_open_format" to this.headerOpenFormat.toDocument(),
        "header_closed_format" to this.headerClosedFormat.toDocument(),
        "header_label_open_format" to this.headerLabelOpenFormat.toDocument(),
        "header_label_closed_format" to this.headerLabelClosedFormat.toDocument(),
        "header_open_icon" to this.headerOpenIcon.toDocument(),
        "header_closed_icon" to this.headerClosedIcon.toDocument()
    ))


}

