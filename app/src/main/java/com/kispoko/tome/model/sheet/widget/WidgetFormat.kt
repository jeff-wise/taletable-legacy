
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.Alignment
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.Spacing
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.util.*



/**
 * Widget Format
 */
data class WidgetFormat(override val id : UUID,
                        val width : Prim<WidgetWidth>,
                        val alignment : Prim<Alignment>,
                        val backgroundColorTheme : Prim<ColorTheme>,
                        val corners : Prim<Corners>,
                        val margins : Comp<Spacing>,
                        val padding : Comp<Spacing>) : Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widget : WidgetWidth,
                alignment : Alignment,
                backgroundColorTheme : ColorTheme,
                corners : Corners,
                margins : Spacing,
                padding : Spacing)
        : this(UUID.randomUUID(),
               Prim(widget),
               Prim(alignment),
               Prim(backgroundColorTheme),
               Prim(corners),
               Comp(margins),
               Comp(padding))

    companion object : Factory<WidgetFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<WidgetFormat> = when (doc)
        {
            is DocDict -> effApply(::WidgetFormat,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Width
                                   doc.at("width") ap {
                                       effApply(::Prim, WidgetWidth.fromDocument(it))
                                   },
                                   // Alignment
                                   doc.at("alignment") ap {
                                       effApply(::Prim, Alignment.fromDocument(it))
                                   },
                                   // Background Color
                                   doc.at("background_color") ap {
                                       effApply(::Prim, ColorTheme.fromDocument(it))
                                   },
                                   // Corners
                                   doc.at("corners") ap {
                                       effApply(::Prim, Corners.fromDocument(it))
                                   },
                                   // Margins
                                   doc.at("margins") ap {
                                       effApply(::Comp, Spacing.fromDocument(it))
                                   },
                                   // Padding
                                   doc.at("padding") ap {
                                       effApply(::Comp, Spacing.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() : WidgetFormat =
                WidgetFormat(WidgetWidth.default,
                             Alignment.Center(),
                             ColorTheme.transparent,
                             Corners.NONE(),
                             Spacing.default(),
                             Spacing.default())
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------



    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

}


/**
 * Widget Width
 */
data class WidgetWidth(val value : Int)
{

    companion object : Factory<WidgetWidth>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<WidgetWidth> = when (doc)
        {
            is DocNumber -> effValue(WidgetWidth(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        val default = WidgetWidth(1)
    }
}

