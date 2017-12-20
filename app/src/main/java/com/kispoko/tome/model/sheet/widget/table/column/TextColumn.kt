
package com.kispoko.tome.model.sheet.widget.table.column


import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue1
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.model.sheet.widget.table.ColumnFormat
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Text Column Format
 */
data class TextColumnFormat(override val id : UUID,
                            val columnFormat : ColumnFormat)
                             : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(columnFormat : ColumnFormat)
        : this(UUID.randomUUID(), columnFormat)


    companion object : Factory<TextColumnFormat>
    {

        private fun defaultColumnFormat() = ColumnFormat.default()


        override fun fromDocument(doc: SchemaDoc): ValueParser<TextColumnFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TextColumnFormat,
                         // Column Format
                         split(doc.maybeAt("column_format"),
                               effValue(defaultColumnFormat()),
                               { ColumnFormat.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = TextColumnFormat(defaultColumnFormat())

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


    override fun rowValue() : DB_WidgetTableColumnTextFormatValue =
        RowValue1(widgetTableColumnTextFormatTable,
                  ProdValue(this.columnFormat))

}

