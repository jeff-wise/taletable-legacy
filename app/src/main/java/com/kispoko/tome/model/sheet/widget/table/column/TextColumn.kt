
package com.kispoko.tome.model.sheet.widget.table.column


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Null
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.widget.table.ColumnFormat
import effect.*
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Text Column Format
 */
data class TextColumnFormat(override val id : UUID,
                            val columnFormat : Func<ColumnFormat>) : Model
{

    companion object : Factory<TextColumnFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TextColumnFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply2(::TextColumnFormat,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Column Format
                          split(doc.maybeAt("column_format"),
                                valueResult<Func<ColumnFormat>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<ColumnFormat>> =
                                    effApply(::Comp, ColumnFormat.fromDocument(d)))
                          )
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}

