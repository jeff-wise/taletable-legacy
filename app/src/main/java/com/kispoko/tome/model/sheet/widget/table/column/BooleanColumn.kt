
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
import lulo.value.ValueError
import lulo.value.ValueParser
import java.util.*



/**
 * Boolean Column Format
 */
data class BooleanColumnFormat(override val id : UUID,
                               val columnFormat : Comp<ColumnFormat>,
                               val trueStyle : Maybe<Comp<TextStyle>>,
                               val falseStyle : Maybe<Comp<TextStyle>>,
                               val showTrueIcon : Prim<Boolean>,
                               val showFalseIcon : Prim<Boolean>) : Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : ColumnFormat,
                trueStyle : Maybe<TextStyle>,
                falseStyle : Maybe<TextStyle>,
                showTrueIcon : Boolean,
                showFalseIcon : Boolean)
        : this(UUID.randomUUID(),
               Comp(format),
               maybeLiftComp(trueStyle),
               maybeLiftComp(falseStyle),
               Prim(showTrueIcon),
               Prim(showFalseIcon))


    companion object : Factory<BooleanColumnFormat>
    {

        private val defaultColumnFormat  = ColumnFormat.default
        private val defaultTrueStyle     = TextStyle.default
        private val defaultFalseStyle    = TextStyle.default
        private val defaultShowTrueIcon  = false
        private val defaultShowFalseIcon = false


        override fun fromDocument(doc : SpecDoc) : ValueParser<BooleanColumnFormat> = when (doc)
        {
            is DocDict -> effApply(::BooleanColumnFormat,
                                   // Column Format
                                   split(doc.maybeAt("column_format"),
                                         effValue(defaultColumnFormat),
                                         { ColumnFormat.fromDocument(it) }),
                                   // True Style
                                   split(doc.maybeAt("true_style"),
                                         effValue<ValueError,Maybe<TextStyle>>(Nothing()),
                                         { effApply(::Just, TextStyle.fromDocument(it)) }),
                                   // False Style
                                   split(doc.maybeAt("false_style"),
                                         effValue<ValueError,Maybe<TextStyle>>(Nothing()),
                                         { effApply(::Just, TextStyle.fromDocument(it)) }),
                                   // Show True Icon?
                                   split(doc.maybeBoolean("show_true_icon"),
                                         effValue(defaultShowTrueIcon),
                                         { effValue(it) }),
                                   // Show False Icon?
                                   split(doc.maybeBoolean("show_false_icon"),
                                         effValue(defaultShowFalseIcon),
                                         { effValue(it) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : BooleanColumnFormat =
                BooleanColumnFormat(defaultColumnFormat,
                                    Nothing(),
                                    Nothing(),
                                    defaultShowTrueIcon,
                                    defaultShowFalseIcon)
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun columnFormat() : ColumnFormat = this.columnFormat.value

    fun trueStyle() : Maybe<TextStyle> = getMaybeComp(this.trueStyle)

    fun falseStyle() : Maybe<TextStyle> = getMaybeComp(this.falseStyle)

    fun showTrueIcon() : Boolean = this.showTrueIcon.value

    fun showFalseIcon() : Boolean = this.showFalseIcon.value


    // -----------------------------------------------------------------------------------------
    // MODELS
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

}
