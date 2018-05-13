
package com.kispoko.tome.model.sheet.widget.table.column


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.model.sheet.widget.table.ColumnFormat
import effect.apply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Image Column Format
 */
data class ImageColumnFormat(val columnFormat : ColumnFormat)
                               : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ImageColumnFormat>
    {

        private fun defaultColumnFormat() = ColumnFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<ImageColumnFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::ImageColumnFormat,
                      // Column Format
                      split(doc.maybeAt("column_format"),
                              effValue(defaultColumnFormat()),
                              { ColumnFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ImageColumnFormat(defaultColumnFormat())
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
