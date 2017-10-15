
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.model.sheet.style.Alignment
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.Spacing
import com.kispoko.tome.model.theme.ColorTheme
import effect.effApply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
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
                        val corners : Comp<Corners>,
                        val margins : Comp<Spacing>,
                        val padding : Comp<Spacing>)
                         : ToDocument, Model, Serializable
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
               Comp(corners),
               Comp(margins),
               Comp(padding))

    companion object : Factory<WidgetFormat>
    {

        private val defaultWidth                = WidgetWidth.default()
        private val defaultAlignment            = Alignment.Center
        private val defaultBackgroundColorTheme = ColorTheme.transparent
        private val defaultCorners              = Corners.default()
        private val defaultMargins              = Spacing.default()
        private val defaultPadding              = Spacing.default()


        override fun fromDocument(doc: SchemaDoc): ValueParser<WidgetFormat> = when (doc)
        {
            is DocDict -> effApply(::WidgetFormat,
                                   // Width
                                   split(doc.maybeAt("width"),
                                         effValue(defaultWidth),
                                         { WidgetWidth.fromDocument(it) }),
                                   // Alignment
                                   split(doc.maybeAt("alignment"),
                                         effValue<ValueError,Alignment>(defaultAlignment),
                                         { Alignment.fromDocument(it) }),
                                   // Background Color Theme
                                   split(doc.maybeAt("background_color_theme"),
                                         effValue(defaultBackgroundColorTheme),
                                         { ColorTheme.fromDocument(it) }),
                                   // Corners
                                   split(doc.maybeAt("corners"),
                                         effValue<ValueError,Corners>(defaultCorners),
                                         { Corners.fromDocument(it) }),
                                   // Margins
                                   split(doc.maybeAt("margins"),
                                         effValue(defaultMargins),
                                         { Spacing.fromDocument(it) }),
                                   // Padding
                                   split(doc.maybeAt("padding"),
                                         effValue(defaultPadding),
                                         { Spacing.fromDocument(it) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() : WidgetFormat =
                WidgetFormat(defaultWidth,
                             defaultAlignment,
                             defaultBackgroundColorTheme,
                             defaultCorners,
                             defaultMargins,
                             defaultPadding)
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "width" to this.width.value.toDocument(),
        "alignment" to this.alignment().toDocument(),
        "background_color_theme" to this.backgroundColorTheme().toDocument(),
        "corners" to this.corners().toDocument(),
        "margins" to this.margins().toDocument(),
        "padding" to this.padding().toDocument()
    ))


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
data class WidgetWidth(val value : Int) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<WidgetWidth>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<WidgetWidth> = when (doc)
        {
            is DocNumber -> effValue(WidgetWidth(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = WidgetWidth(1)
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLInt({ this.value })

}

