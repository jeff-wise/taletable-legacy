
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
import effect.Err
import effect.effApply
import effect.effApply9
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Widget Format
 */
data class WidgetFormat(override val id : UUID,
                        val label : Func<WidgetLabel>,
                        val width : Func<WidgetWidth>,
                        val alignment : Func<Alignment>,
                        val labelStyle : Func<TextStyle>,
                        val backgroundColor : Func<ColorId>,
                        val corners : Func<Corners>,
                        val margins : Func<Spacing>,
                        val padding : Func<Spacing>) : Model
{
    companion object : Factory<WidgetFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<WidgetFormat> = when (doc)
        {
            is DocDict -> effApply9(::WidgetFormat,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Label
                                    doc.at("label") ap {
                                        effApply(::Prim, WidgetLabel.fromDocument(it))
                                    },
                                    // Width
                                    doc.at("width") ap {
                                        effApply(::Prim, WidgetWidth.fromDocument(it))
                                    },
                                    // Alignment
                                    effApply(::Prim, doc.enum<Alignment>("alignment")),
                                    // Label Style
                                    doc.at("label_style") ap {
                                        effApply(::Comp, TextStyle.fromDocument(it))
                                    },
                                    // Background Color
                                    doc.at("background_color") ap {
                                        effApply(::Prim, ColorId.fromDocument(it))
                                    },
                                    // Corners
                                    effApply(::Prim, doc.enum<Corners>("corners")),
                                    // Margins
                                    doc.at("margins") ap {
                                        effApply(::Prim, Spacing.fromDocument(it))
                                    },
                                    // Padding
                                    doc.at("padding") ap {
                                        effApply(::Prim, Spacing.fromDocument(it))
                                    })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Widget Name
 */
data class WidgetLabel(val value : String)
{

    companion object : Factory<WidgetLabel>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<WidgetLabel> = when (doc)
        {
            is DocText -> valueResult(WidgetLabel(doc.text))
            else       -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
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
            is DocInteger -> valueResult(WidgetWidth(doc.integer.toInt()))
            else          -> Err(UnexpectedType(DocType.INTEGER, docType(doc)), doc.path)
        }
    }
}

