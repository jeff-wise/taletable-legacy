
package com.kispoko.tome.model.sheet.widget.table.column


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.table.ColumnFormat
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
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
                effApply(::NumberColumnFormat,
                        // Model Id
                        effValue(UUID.randomUUID()),
                        // Column Format
                        split(doc.maybeAt("column_format"),
                                nullEff<ColumnFormat>(),
                                { effApply(::Comp, ColumnFormat.fromDocument(it)) }),
                        // Default Value Prefix
                        split(doc.maybeAt("default_value_prefix"),
                                nullEff<DefaultValuePrefix>(),
                                { effApply(::Prim, DefaultValuePrefix.fromDocument(it)) })
                        )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
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
            is DocText -> effValue(DefaultValuePrefix(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}
