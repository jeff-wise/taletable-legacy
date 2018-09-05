
package com.taletable.android.model.sheet.widget.table.column


import com.taletable.android.lib.Factory
import com.taletable.android.model.sheet.widget.table.ColumnFormat
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable



/**
 * Text Column Format
 */
data class TextColumnFormat(val columnFormat : ColumnFormat)
                             : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

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


}


