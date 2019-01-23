
package com.taletable.android.model.sheet.style


import android.sax.Element
import com.taletable.android.lib.Factory
import com.taletable.android.model.theme.ColorTheme
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



/**
 * Element Format
 */
data class ElementFormat(private val position : Position,
                         private val style : Maybe<ElementStyle>,
                         private val height : Height,
                         private val width : Width,
                         private val padding : Spacing,
                         private val margins : Spacing,
                         private val backgroundColorTheme : ColorTheme,
                         private val corners : Corners,
                         private val border : Border,
                         private val elevation : Elevation,
                         private val alignment : Alignment,
                         private val verticalAlignment : VerticalAlignment)
                           : ToDocument, Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ElementFormat>
    {

        private fun defaultPosition()             = Position.Top
        private fun defaultElementStyle()         = Nothing<ElementStyle>()
        private fun defaultHeight()               = Height.Wrap
        private fun defaultWidth()                = Width.Wrap
        private fun defaultPadding()              = Spacing.default()
        private fun defaultMargins()              = Spacing.default()
        private fun defaultBackgroundColorTheme() = ColorTheme.transparent
        private fun defaultCorners()              = Corners.default()
        private fun defaultBorder()               = Border.default()
        private fun defaultElevation()            = Elevation(0.0)
        private fun defaultAlignment()            = Alignment.Center
        private fun defaultVerticalAlignment()    = VerticalAlignment.Middle

        override fun fromDocument(doc: SchemaDoc): ValueParser<ElementFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::ElementFormat,
                      // Position
                      split(doc.maybeAt("position"),
                            effValue<ValueError,Position>(defaultPosition()),
                            { Position.fromDocument(it) }),
                      // Element Style
                      split(doc.maybeAt("style"),
                            effValue<ValueError,Maybe<ElementStyle>>(defaultElementStyle()),
                            { apply(::Just, ElementStyle.fromDocument(it)) }),
                      // Height
                      split(doc.maybeAt("height"),
                            effValue<ValueError,Height>(defaultHeight()),
                            { Height.fromDocument(it) }),
                      // Width
                      split(doc.maybeAt("width"),
                            effValue<ValueError,Width>(defaultWidth()),
                            { Width.fromDocument(it) }),
                      // Padding
                      split(doc.maybeAt("padding"),
                            effValue(defaultPadding()),
                            { Spacing.fromDocument(it) }),
                      // Margins
                      split(doc.maybeAt("margins"),
                            effValue(defaultMargins()),
                            { Spacing.fromDocument(it) }),
                      // Background Color Theme
                      split(doc.maybeAt("background_color_theme"),
                            effValue(defaultBackgroundColorTheme()),
                            { ColorTheme.fromDocument(it) }),
                      // Corners
                      split(doc.maybeAt("corners"),
                            effValue(defaultCorners()),
                            { Corners.fromDocument(it) }),
                      // Border
                      split(doc.maybeAt("border"),
                            effValue(defaultBorder()),
                            { Border.fromDocument(it) }),
                      // Elevation
                      split(doc.maybeAt("elevation"),
                            effValue(defaultElevation()),
                            { Elevation.fromDocument(it) }),
                      // Alignment
                      split(doc.maybeAt("horizontal_alignment"),
                            effValue<ValueError,Alignment>(defaultAlignment()),
                            { Alignment.fromDocument(it) }),
                      // Vertical Alignment
                      split(doc.maybeAt("vertical_alignment"),
                            effValue<ValueError,VerticalAlignment>(defaultVerticalAlignment()),
                            { VerticalAlignment.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ElementFormat(defaultPosition(),
                                      defaultElementStyle(),
                                      defaultHeight(),
                                      defaultWidth(),
                                      defaultPadding(),
                                      defaultMargins(),
                                      defaultBackgroundColorTheme(),
                                      defaultCorners(),
                                      defaultBorder(),
                                      defaultElevation(),
                                      defaultAlignment(),
                                      defaultVerticalAlignment())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "position" to this.position().toDocument(),
        "height" to this.height().toDocument(),
        "width" to this.width().toDocument(),
        "padding" to this.padding().toDocument(),
        "margins" to this.margins().toDocument(),
        "background_color_theme" to this.backgroundColorTheme().toDocument(),
        "corners" to this.corners().toDocument(),
        "border" to this.border().toDocument(),
        "elevation" to this.elevation().toDocument(),
        "alignment" to this.alignment().toDocument(),
        "vertical_alignment" to this.verticalAlignment().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun position() : Position = this.position


    fun style() : Maybe<ElementStyle> = this.style


    fun height() : Height = this.height


    fun width() : Width = this.width


    fun padding() : Spacing = this.padding


    fun margins() : Spacing = this.margins


    fun backgroundColorTheme() : ColorTheme = this.backgroundColorTheme


    fun corners() : Corners = this.corners


    fun border() : Border = this.border


    fun elevation() : Elevation = this.elevation


    fun alignment() : Alignment = this.alignment


    fun verticalAlignment() : VerticalAlignment = this.verticalAlignment


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    // UPDATE > Margins
    // -----------------------------------------------------------------------------------------

    fun withTopMargin(topMargin : Double) : ElementFormat
    {
        val newMargins = Spacing(topMargin,
                                 this.margins.right,
                                 this.margins.bottom,
                                 this.margins.left)

        return ElementFormat(this.position,
                             this.style,
                             this.height,
                             this.width,
                             this.padding,
                             newMargins,
                             this.backgroundColorTheme,
                             this.corners,
                             this.border,
                             this.elevation,
                             this.alignment,
                             this.verticalAlignment)
    }


    fun withRightMargin(rightMargin : Double) : ElementFormat
    {
        val newMargins = Spacing(this.margins.top,
                                 rightMargin,
                                 this.margins.bottom,
                                 this.margins.left)

        return ElementFormat(this.position,
                             this.style,
                             this.height,
                             this.width,
                             this.padding,
                             newMargins,
                             this.backgroundColorTheme,
                             this.corners,
                             this.border,
                             this.elevation,
                             this.alignment,
                             this.verticalAlignment)
    }


    fun withLeftMargin(leftMargin : Double) : ElementFormat
    {
        val newMargins = Spacing(this.margins.top,
                                 this.margins.right,
                                 this.margins.bottom,
                                 leftMargin)

        return ElementFormat(this.position,
                             this.style,
                             this.height,
                             this.width,
                             this.padding,
                             newMargins,
                             this.backgroundColorTheme,
                             this.corners,
                             this.border,
                             this.elevation,
                             this.alignment,
                             this.verticalAlignment)
    }


    fun withBottomMargin(bottomMargin : Double) : ElementFormat
    {
        val newMargins = Spacing(this.margins.top,
                                 this.margins.right,
                                 bottomMargin,
                                 this.margins.left)

        return ElementFormat(this.position,
                             this.style,
                             this.height,
                             this.width,
                             this.padding,
                             newMargins,
                             this.backgroundColorTheme,
                             this.corners,
                             this.border,
                             this.elevation,
                             this.alignment,
                             this.verticalAlignment)
    }



    // UPDATE > Padding
    // -----------------------------------------------------------------------------------------

    fun withTopPadding(topPadding : Double) : ElementFormat
    {
        val newPadding = Spacing(topPadding,
                                 this.padding.right,
                                 this.padding.bottom,
                                 this.padding.left)

        return ElementFormat(this.position,
                             this.style,
                             this.height,
                             this.width,
                             newPadding,
                             this.margins,
                             this.backgroundColorTheme,
                             this.corners,
                             this.border,
                             this.elevation,
                             this.alignment,
                             this.verticalAlignment)
    }


    fun withBottomPadding(bottomPadding : Double) : ElementFormat
    {
        val newPadding = Spacing(this.padding.top,
                                 this.padding.right,
                                 bottomPadding,
                                 this.padding.left)

        return ElementFormat(this.position,
                             this.style,
                             this.height,
                             this.width,
                             newPadding,
                             this.margins,
                             this.backgroundColorTheme,
                             this.corners,
                             this.border,
                             this.elevation,
                             this.alignment,
                             this.verticalAlignment)
    }


    // UPDATE > Horizontal Alignment
    // -----------------------------------------------------------------------------------------

    fun withHorizontalAlignment(alignment : Alignment) : ElementFormat
    {
        return ElementFormat(this.position,
                             this.style,
                             this.height,
                             this.width,
                             this.padding,
                             this.margins,
                             this.backgroundColorTheme,
                             this.corners,
                             this.border,
                             this.elevation,
                             alignment,
                             this.verticalAlignment)
    }

}



/**
 * Element Style
 */
sealed class ElementStyle : ToDocument, Serializable
{

    object Card : ElementStyle()
    {

        // | To Document

        override fun toDocument() = DocText("card")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<ElementStyle> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "card"   -> effValue<ValueError,ElementStyle>(ElementStyle.Card)
                else     -> effError<ValueError,ElementStyle>(
                                    UnexpectedValue("ElementStyle", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


