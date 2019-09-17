
package com.taletable.android.model.sheet.widget


import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.asSQLValue
import com.taletable.android.model.sheet.group.RowColumn
import com.taletable.android.model.sheet.style.*
import com.taletable.android.model.theme.ColorTheme
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



/**
 * Widget Format
 */
data class WidgetFormat(val width : WidgetWidth,
                        val column : RowColumn,
                        val elementFormat : ElementFormat)
                         : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(elementFormat : ElementFormat)
            : this(WidgetWidth.default(), RowColumn.default(), elementFormat)

    companion object : Factory<WidgetFormat>
    {

        private fun defaultWidth()         = WidgetWidth.default()
        private fun defaultColumn()        = RowColumn.default()
        private fun defaultElementFormat() = ElementFormat(Position.Top,
                                                           Nothing(),
                                                           Height.Wrap,
                                                           Width.Wrap,
                                                           Spacing.default(),
                                                           Spacing.default(),
                                                           ColorTheme.transparent,
                                                           Corners.default(),
                                                           Border.default(),
                                                           Elevation.default(),
                                                           Justification.ParentStart,
                                                           Alignment.Center,
                                                           VerticalAlignment.Middle)


        override fun fromDocument(doc : SchemaDoc) : ValueParser<WidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::WidgetFormat,
                      // Width
                      split(doc.maybeAt("width"),
                            effValue(defaultWidth()),
                            { WidgetWidth.fromDocument(it) }),
                      // Column
                      split(doc.maybeAt("column"),
                            effValue(defaultColumn()),
                            { RowColumn.fromDocument(it) }),
                      // Element Format
                      split(doc.maybeAt("element_format"),
                            effValue(defaultElementFormat()),
                            { ElementFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = WidgetFormat(defaultWidth(), defaultColumn(), defaultElementFormat())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "width" to this.width.toDocument(),
        "column" to this.column.toDocument(),
        "element_format" to this.elementFormat.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun width() : Int = this.width.value


    fun column() : Int = this.column.value


    fun elementFormat() : ElementFormat = this.elementFormat

}


/**
 * Widget Width
 */
data class WidgetWidth(val value : Int) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<WidgetWidth>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<WidgetWidth> = when (doc)
        {
            is DocNumber -> effValue(WidgetWidth(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = WidgetWidth(1)
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.value.asSQLValue()

}




/**
 * Widget Theme
 */
sealed class WidgetOfficialTheme : ToDocument, Serializable
{

    // | CASES
    // -----------------------------------------------------------------------------------------

    object Metric : WidgetOfficialTheme() {
        override fun toDocument() = DocText("metric")
    }

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<WidgetOfficialTheme> = when (doc) {
            is DocText -> when (doc.text) {
                "metric" -> effValue<ValueError,WidgetOfficialTheme>(WidgetOfficialTheme.Metric)
                else     -> effError<ValueError,WidgetOfficialTheme>(
                                UnexpectedValue("WidgetOfficialTheme", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


}


/**
 * Widget Style
 */
data class WidgetStyle(val value : String) : ToDocument, Serializable
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<WidgetStyle>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<WidgetStyle> = when (doc) {
            is DocText -> effValue(WidgetStyle(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // | TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}


/**
 * Widget Style Variation
 */
data class WidgetStyleVariation(val value : String) : ToDocument, Serializable
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<WidgetStyleVariation>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<WidgetStyleVariation> = when (doc) {
            is DocText -> effValue(WidgetStyleVariation(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // | TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}



data class OfficialWidgetFormat(
    val theme : WidgetOfficialTheme,
    val style : WidgetStyle,
    val variations : List<WidgetStyleVariation>,
    val widgetFormat : Maybe<WidgetFormat>
)
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<OfficialWidgetFormat>
    {

        /**
         * Read from Lulo format.
         */
        override fun fromDocument(doc : SchemaDoc) : ValueParser<OfficialWidgetFormat> = when (doc)
        {
            is DocDict -> {
                apply(::OfficialWidgetFormat,
                      // Official Theme
                      doc.at("theme").apply { WidgetOfficialTheme.fromDocument(it) },
                      // Widget Style
                      doc.at("style").apply { WidgetStyle.fromDocument(it) },
                      // Widget Style Variation
                      split(doc.maybeList("variations"),
                            effValue(listOf()),
                            { it.map { WidgetStyleVariation.fromDocument(it) } }),
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue<ValueError, Maybe<WidgetFormat>>(Nothing()),
                            { apply(::Just, WidgetFormat.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

}

