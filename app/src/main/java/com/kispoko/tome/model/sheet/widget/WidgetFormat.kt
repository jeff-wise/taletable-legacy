
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.db.DB_WidgetFormatValue
import com.kispoko.tome.db.widgetFormatTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue2
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.asSQLValue
import com.kispoko.tome.model.sheet.group.RowColumn
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.theme.ColorTheme
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Widget Format
 */
data class WidgetFormat(override val id : UUID,
                        val width : WidgetWidth,
                        val column : RowColumn,
                        val elementFormat : ElementFormat)
                         : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(width : WidgetWidth,
                column : RowColumn,
                elmentFormat : ElementFormat)
        : this(UUID.randomUUID(),
               width,
               column,
               elmentFormat)

    companion object : Factory<WidgetFormat>
    {

        private fun defaultWidth()         = WidgetWidth.default()
        private fun defaultColumn()        = RowColumn.default()
        private fun defaultElementFormat() = ElementFormat(Position.Top,
                                                           Height.Wrap,
                                                           Width.Justify,
                                                           Spacing.default(),
                                                           Spacing.default(),
                                                           ColorTheme.transparent,
                                                           Corners.default(),
                                                           Border.default(),
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


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetFormatValue =
        RowValue3(widgetFormatTable,
                  PrimValue(this.width),
                  PrimValue(this.column),
                  ProdValue(this.elementFormat))

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


