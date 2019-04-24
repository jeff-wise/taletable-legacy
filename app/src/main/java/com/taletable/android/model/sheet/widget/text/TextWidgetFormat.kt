
package com.taletable.android.model.sheet.widget.text


import com.taletable.android.lib.Factory
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.model.sheet.widget.OfficialWidgetFormat
import com.taletable.android.model.sheet.widget.WidgetFormat
import effect.apply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable



// -----------------------------------------------------------------------------------------
// | TEXT WIDGET FORMAT
// -----------------------------------------------------------------------------------------

sealed class TextWidgetFormat
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextWidgetFormat>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<TextWidgetFormat> =
            when (doc.case())
            {
                "text_widget_format_custom" -> TextWidgetFormatCustom.fromDocument(doc) as ValueParser<TextWidgetFormat>
                "widget_format_official"    -> TextWidgetFormatOfficial.fromDocument(doc) as ValueParser<TextWidgetFormat>
                else                 -> {
                    effError(UnknownCase(doc.case(), doc.path))
                }
            }
    }

}


// ---------------------------------------------------------------------------------------------
// | TEXT WIDGET FORMAT: *THEME*
// ---------------------------------------------------------------------------------------------


data class TextWidgetFormatOfficial(
    val format : OfficialWidgetFormat
) : TextWidgetFormat()
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextWidgetFormatOfficial>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<TextWidgetFormatOfficial> =
            apply(::TextWidgetFormatOfficial, OfficialWidgetFormat.fromDocument(doc))
    }

}



// -----------------------------------------------------------------------------------------
// | TEXT WIDGET FORMAT: *CUSTOM*
// -----------------------------------------------------------------------------------------

/**
 * Text Widget Format: Custom
 */
data class TextWidgetFormatCustom(
    val widgetFormat  : WidgetFormat,
    val valueFormat   : TextFormat,
    val labelFormat   : TextFormat,
    val prefixFormat  : TextFormat,
    val postfixFormat : TextFormat
) : TextWidgetFormat(), ToDocument, Serializable
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                valueFormat : TextFormat)
      : this(widgetFormat,
             TextFormat.default(),
             TextFormat.default(),
             TextFormat.default(),
             valueFormat)


    companion object : Factory<TextWidgetFormatCustom>
    {

        private fun defaultWidgetFormat()  = WidgetFormat.default()
        private fun defaultLabelFormat()   = TextFormat.default()
        private fun defaultPrefixFormat()  = TextFormat.default()
        private fun defaultPostfixFormat() = TextFormat.default()
        private fun defaultValueFormat()   = TextFormat.default()


        override fun fromDocument(doc : SchemaDoc)
                        : ValueParser<TextWidgetFormatCustom> = when (doc)
        {
            is DocDict ->
            {
                apply(::TextWidgetFormatCustom,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // Label Format
                      split(doc.maybeAt("label_format"),
                            effValue(defaultLabelFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Prefix Format
                      split(doc.maybeAt("prefix_format"),
                            effValue(defaultPrefixFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Postfix Format
                      split(doc.maybeAt("postfix_format"),
                            effValue(defaultPostfixFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Value Format
                      split(doc.maybeAt("value_format"),
                            effValue(defaultValueFormat()),
                            { TextFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = TextWidgetFormatCustom(
                            defaultWidgetFormat(),
                            defaultLabelFormat(),
                            defaultPrefixFormat(),
                            defaultPostfixFormat(),
                            defaultValueFormat())

    }


    // | TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat.toDocument(),
        "label_format" to this.labelFormat.toDocument(),
        "prefix_format" to this.prefixFormat.toDocument(),
        "postfix_format" to this.postfixFormat.toDocument(),
        "value_format" to this.valueFormat.toDocument()
    ))


}
