
package com.kispoko.tome.model.sheet.widget.table.column


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Null
import com.kispoko.tome.lib.functor.nullEff
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.widget.table.ColumnFormat
import effect.*
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
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
                effApply(::TextColumnFormat,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Column Format
                         split(doc.maybeAt("column_format"),
                               nullEff<ColumnFormat>(),
                               { effApply(::Comp, ColumnFormat.fromDocument(it)) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}

