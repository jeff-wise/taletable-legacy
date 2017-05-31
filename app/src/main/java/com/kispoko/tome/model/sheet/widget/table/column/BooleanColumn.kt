
package com.kispoko.tome.model.sheet.widget.table.column


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.TextStyle
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
 * Boolean Column Format
 */
data class BooleanColumnFormat(override val id : UUID,
                               val columnFormat : Func<ColumnFormat>,
                               val trueStyle : Func<TextStyle>,
                               val falseStyle : Func<TextStyle>,
                               val showTrueIcon : Func<Boolean>,
                               val showFalseIcon : Func<Boolean>) : Model
{

    companion object : Factory<BooleanColumnFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<BooleanColumnFormat> = when (doc)
        {
            is DocDict -> effApply(::BooleanColumnFormat,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Column Format
                                   split(doc.maybeAt("column_format"),
                                         nullEff<ColumnFormat>(),
                                         { effApply(::Comp, ColumnFormat.fromDocument(it))}),
                                   // True Style
                                   split(doc.maybeAt("true_style"),
                                         nullEff<TextStyle>(),
                                         { effApply(::Comp, TextStyle.fromDocument(it)) }),
                                   // False Style
                                   split(doc.maybeAt("false_style"),
                                         nullEff<TextStyle>(),
                                         { effApply(::Comp, TextStyle.fromDocument(it)) }),
                                   // Show True Icon?
                                   split(doc.maybeBoolean("show_true_icon"),
                                         nullEff<Boolean>(),
                                         { effValue(Prim(it)) }),
                                   // Show True Icon?
                                   split(doc.maybeBoolean("show_false_icon"),
                                         nullEff<Boolean>(),
                                         { effValue(Prim(it)) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}
