
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.sheet.style.Alignment
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.Spacing
import com.kispoko.tome.model.theme.ColorTheme
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
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
                        val padding : Comp<Spacing>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.width.name                 = "width"
        this.alignment.name             = "alignment"
        this.backgroundColorTheme.name  = "background_color_theme"
        this.corners.name               = "corners"
        this.margins.name               = "margins"
        this.padding.name               = "padding"
    }


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
                             Alignment.Center,
                             ColorTheme.transparent,
                             Corners.None,
                             Spacing.default,
                             Spacing.default)
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun width() : Int = this.width.value.value

    fun alignment() : Alignment = this.alignment.value

    fun backgroundColorTheme() : ColorTheme = this.backgroundColorTheme.value

    fun corners() : Corners = this.corners.value

    fun margins() : Spacing = this.margins.value

    fun padding() : Spacing = this.padding.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "widget_format"

    override val modelObject = this

}


/**
 * Widget Width
 */
data class WidgetWidth(val value : Int) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<WidgetWidth>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<WidgetWidth> = when (doc)
        {
            is DocNumber -> effValue(WidgetWidth(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        val default = WidgetWidth(1)
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLInt({ this.value })

}

