
package com.kispoko.tome.model.sheet.widget.table.column


import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue1
import com.kispoko.tome.lib.orm.RowValue7
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.model.sheet.widget.table.ColumnFormat
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Number Column Format
 */
data class NumberColumnFormat(override val id : UUID,
                              val columnFormat : ColumnFormat)
                               : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(columnFormat : ColumnFormat)
        : this(UUID.randomUUID(), columnFormat)


    companion object : Factory<NumberColumnFormat>
    {

        private fun defaultColumnFormat() = ColumnFormat.default()


        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberColumnFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::NumberColumnFormat,
                      // Column Format
                      split(doc.maybeAt("column_format"),
                              effValue(defaultColumnFormat()),
                              { ColumnFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = NumberColumnFormat(defaultColumnFormat())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "column_format" to this.columnFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun columnFormat() : ColumnFormat = this.columnFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetTableColumnNumberFormatValue =
        RowValue1(widgetTableColumnNumberFormatTable,
                  ProdValue(this.columnFormat))

}


/**
 * Value Prefix
 */
data class ValuePrefix(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValuePrefix>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValuePrefix> = when (doc)
        {
            is DocText -> effValue(ValuePrefix(doc.text))
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

    override fun asSQLValue() = SQLText({ this.value })
}
