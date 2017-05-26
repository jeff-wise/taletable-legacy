
package com.kispoko.tome.model.sheet.widget.table.column


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Null
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.table.ColumnFormat
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Number Column Format
 */
data class NumberColumnFormat(override val id : UUID,
                              val columnFormat : Func<ColumnFormat>,
                              val valuePrefix : Func<DefaultValuePrefix>) : Model
{

    companion object : Factory<NumberColumnFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberColumnFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply3(::NumberColumnFormat,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Column Format
                          split(doc.maybeAt("column_format"),
                                valueResult<Func<ColumnFormat>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<ColumnFormat>> =
                                    effApply(::Comp, ColumnFormat.fromDocument(d))),
                          // Default Value Prefix
                          split(doc.maybeAt("default_value_prefix"),
                                valueResult<Func<DefaultValuePrefix>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<DefaultValuePrefix>> =
                                    effApply(::Prim, DefaultValuePrefix.fromDocument(d)))
                          )
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Default Value Prefix
 */
data class DefaultValuePrefix(val value : String)
{

    companion object : Factory<DefaultValuePrefix>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<DefaultValuePrefix> = when (doc)
        {
            is DocText -> valueResult(DefaultValuePrefix(doc.text))
            else       -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}
