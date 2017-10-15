
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
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
 * Element Format
 */
data class ElementFormat(override val id : UUID,
                         val position : Prim<Position>,
                         val height : Prim<Height>,
                         val padding : Comp<Spacing>,
                         val margins : Comp<Spacing>,
                         val backgroundColorTheme : Prim<ColorTheme>,
                         val corners : Comp<Corners>,
                         val alignment: Prim<Alignment>,
                         val verticalAlignment: Prim<VerticalAlignment>)
                           : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.position.name              = "position"
        this.height.name                = "height"
        this.padding.name               = "padding"
        this.margins.name               = "margins"
        this.backgroundColorTheme.name  = "background_color_theme"
        this.corners.name               = "corners"
        this.alignment.name             = "alignment"
        this.verticalAlignment.name     = "vertical_alignment"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(position : Position,
                height : Height,
                padding : Spacing,
                margins : Spacing,
                backgroundColorTheme : ColorTheme,
                corners : Corners,
                alignment : Alignment,
                verticalAlignment : VerticalAlignment)
        : this(UUID.randomUUID(),
               Prim(position),
               Prim(height),
               Comp(padding),
               Comp(margins),
               Prim(backgroundColorTheme),
               Comp(corners),
               Prim(alignment),
               Prim(verticalAlignment))


    companion object : Factory<ElementFormat>
    {

        private val defaultPosition             = Position.Top
        private val defaultHeight               = Height.Wrap
        private val defaultPadding              = Spacing.default()
        private val defaultMargins              = Spacing.default()
        private val defaultBackgroundColorTheme = ColorTheme.black
        private val defaultCorners              = Corners.default()
        private val defaultAlignment            = Alignment.Center
        private val defaultVerticalAlignment    = VerticalAlignment.Middle

        override fun fromDocument(doc: SchemaDoc): ValueParser<ElementFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::ElementFormat,
                         // Position
                         split(doc.maybeAt("position"),
                               effValue<ValueError,Position>(defaultPosition),
                               { Position.fromDocument(it) }),
                         // Height
                         split(doc.maybeAt("height"),
                               effValue<ValueError,Height>(defaultHeight),
                               { Height.fromDocument(it) }),
                         // Padding
                         split(doc.maybeAt("padding"),
                               effValue(defaultPadding),
                               { Spacing.fromDocument(it) }),
                         // Margins
                         split(doc.maybeAt("margins"),
                               effValue(defaultMargins),
                               { Spacing.fromDocument(it) }),
                         // Background Color Theme
                         split(doc.maybeAt("background_color_theme"),
                               effValue(defaultBackgroundColorTheme),
                               { ColorTheme.fromDocument(it) }),
                         // Corners
                         split(doc.maybeAt("corners"),
                               effValue(defaultCorners),
                               { Corners.fromDocument(it) }),
                         // Alignment
                         split(doc.maybeAt("alignment"),
                               effValue<ValueError,Alignment>(defaultAlignment),
                               { Alignment.fromDocument(it) }),
                         // Vertical Alignment
                         split(doc.maybeAt("vertical_alignment"),
                               effValue<ValueError,VerticalAlignment>(defaultVerticalAlignment),
                               { VerticalAlignment.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ElementFormat(defaultPosition,
                                      defaultHeight,
                                      defaultPadding,
                                      defaultMargins,
                                      defaultBackgroundColorTheme,
                                      defaultCorners,
                                      defaultAlignment,
                                      defaultVerticalAlignment)

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "position" to this.position().toDocument(),
        "height" to this.height().toDocument(),
        "padding" to this.padding().toDocument(),
        "margins" to this.margins().toDocument(),
        "background_color_theme" to this.backgroundColorTheme().toDocument(),
        "corners" to this.corners().toDocument(),
        "alignment" to this.alignment().toDocument(),
        "vertical_alignment" to this.verticalAlignment().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun position() : Position = this.position.value

    fun height() : Height = this.height.value

    fun padding() : Spacing = this.padding.value

    fun margins() : Spacing = this.margins.value

    fun backgroundColorTheme() : ColorTheme = this.backgroundColorTheme.value

    fun corners() : Corners = this.corners.value

    fun alignment() : Alignment = this.alignment.value

    fun verticalAlignment() : VerticalAlignment = this.verticalAlignment.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "element_format"

    override val modelObject = this

}
