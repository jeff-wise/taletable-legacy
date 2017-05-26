
package com.kispoko.tome.model.sheet.widget.table.column


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Null
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.table.ColumnFormat
import effect.Err
import effect.effApply
import effect.effApply6
import effect.split
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
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
            is DocDict -> effApply6(::BooleanColumnFormat,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Column Format
                                    split(doc.maybeAt("column_format"),
                                          valueResult<Func<ColumnFormat>>(Null()),
                                          fun(d : SpecDoc) : ValueParser<Func<ColumnFormat>> =
                                              effApply(::Comp, ColumnFormat.fromDocument(d))),
                                    // True Style
                                    split(doc.maybeAt("true_style"),
                                          valueResult<Func<TextStyle>>(Null()),
                                          fun(d : SpecDoc) : ValueParser<Func<TextStyle>> =
                                              effApply(::Comp, TextStyle.fromDocument(d))),
                                    // False Style
                                    split(doc.maybeAt("false_style"),
                                          valueResult<Func<TextStyle>>(Null()),
                                          fun(d : SpecDoc) : ValueParser<Func<TextStyle>> =
                                              effApply(::Comp, TextStyle.fromDocument(d))),
                                    // Show True Icon?
                                    split(doc.maybeBoolean("show_true_icon"),
                                          valueResult<Func<Boolean>>(Null()),
                                          { valueResult(Prim(it))  }),
                                    // Show True Icon?
                                    split(doc.maybeBoolean("show_false_icon"),
                                          valueResult<Func<Boolean>>(Null()),
                                          { valueResult(Prim(it))  })
                                    )
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}
