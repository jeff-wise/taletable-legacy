
package com.kispoko.tome.model.sheet.widget.table.column


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.widget.table.ColumnFormat
import effect.*
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Text Column Format
 */
data class TextColumnFormat(override val id : UUID,
                            val columnFormat : Comp<ColumnFormat>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.columnFormat.name      = "column_format"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(columnFormat : ColumnFormat)
        : this(UUID.randomUUID(), Comp(columnFormat))


    companion object : Factory<TextColumnFormat>
    {

        private val defaultColumnFormat = ColumnFormat.default


        override fun fromDocument(doc : SpecDoc) : ValueParser<TextColumnFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TextColumnFormat,
                         // Column Format
                         split(doc.maybeAt("column_format"),
                               effValue(defaultColumnFormat),
                               { ColumnFormat.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : TextColumnFormat = TextColumnFormat(defaultColumnFormat)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun columnFormat() : ColumnFormat = this.columnFormat.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "text_column_format"

    override val modelObject = this

}

