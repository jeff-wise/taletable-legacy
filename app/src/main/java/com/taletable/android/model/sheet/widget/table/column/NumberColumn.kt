
package com.taletable.android.model.sheet.widget.table.column


import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.model.sheet.widget.table.ColumnFormat
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Number Column Format
 */
data class NumberColumnFormat(val columnFormat : ColumnFormat)
                               : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

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
