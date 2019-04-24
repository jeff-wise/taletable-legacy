
package com.taletable.android.model.sheet.widget.number


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

sealed class NumberWidgetFormat
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberWidgetFormat>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<NumberWidgetFormat> =
            when (doc.case())
            {
                "number_widget_format_custom" -> NumberWidgetFormatCustom.fromDocument(doc) as ValueParser<NumberWidgetFormat>
                "widget_format_official"      -> NumberWidgetFormatOfficial.fromDocument(doc) as ValueParser<NumberWidgetFormat>
                else                 -> {
                    effError(UnknownCase(doc.case(), doc.path))
                }
            }
    }

}


// ---------------------------------------------------------------------------------------------
// | NUMBER WIDGET FORMAT: *THEME*
// ---------------------------------------------------------------------------------------------


data class NumberWidgetFormatOfficial(
    val format : OfficialWidgetFormat
) : NumberWidgetFormat()
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberWidgetFormatOfficial>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<NumberWidgetFormatOfficial> =
            apply(::NumberWidgetFormatOfficial, OfficialWidgetFormat.fromDocument(doc))
    }

}


// ---------------------------------------------------------------------------------------------
// | NUMBER WIDGET FORMAT: *CUSTOM*
// ---------------------------------------------------------------------------------------------

/**
 * Number Widget Format: Custom
 */
data class NumberWidgetFormatCustom(
    val widgetFormat  : WidgetFormat,
    val valueFormat   : TextFormat,
    val labelFormat   : TextFormat,
    val prefixFormat  : TextFormat,
    val postfixFormat : TextFormat
) : NumberWidgetFormat(), ToDocument, Serializable {

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat: WidgetFormat,
                valueFormat: TextFormat)
            : this(widgetFormat,
            TextFormat.default(),
            TextFormat.default(),
            TextFormat.default(),
            valueFormat)


    companion object : Factory<NumberWidgetFormatCustom>
    {

        private fun defaultWidgetFormat() = WidgetFormat.default()
        private fun defaultLabelFormat() = TextFormat.default()
        private fun defaultPrefixFormat() = TextFormat.default()
        private fun defaultPostfixFormat() = TextFormat.default()
        private fun defaultValueFormat() = TextFormat.default()


        override fun fromDocument(doc: SchemaDoc)
                : ValueParser<NumberWidgetFormatCustom> = when (doc)
        {
            is DocDict -> {
                apply(::NumberWidgetFormatCustom,
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
            else -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = NumberWidgetFormatCustom(
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

