
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.db.DB_ElementFormat
import com.kispoko.tome.db.dbElementFormat
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.ui.SectionCard
import com.kispoko.tome.model.theme.ColorTheme
import effect.*
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
                         private val position : Position,
                         private val height : Height,
                         private val padding : Spacing,
                         private val margins : Spacing,
                         private val backgroundColorTheme : ColorTheme,
                         private val corners : Corners,
                         private val alignment : Alignment,
                         private val verticalAlignment: VerticalAlignment)
                           : ToDocument, ProdType, Serializable
{

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
               position,
               height,
               padding,
               margins,
               backgroundColorTheme,
               corners,
               alignment,
               verticalAlignment)


    companion object : Factory<ElementFormat>
    {

        private fun defaultPosition()             = Position.Top
        private fun defaultHeight()               = Height.Wrap
        private fun defaultPadding()              = Spacing.default()
        private fun defaultMargins()              = Spacing.default()
        private fun defaultBackgroundColorTheme() = ColorTheme.transparent
        private fun defaultCorners()              = Corners.default()
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
                      // Height
                      split(doc.maybeAt("height"),
                            effValue<ValueError,Height>(defaultHeight()),
                            { Height.fromDocument(it) }),
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
                                      defaultHeight(),
                                      defaultPadding(),
                                      defaultMargins(),
                                      defaultBackgroundColorTheme(),
                                      defaultCorners(),
                                      defaultAlignment(),
                                      defaultVerticalAlignment())

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

    fun position() : Position = this.position


    fun height() : Height = this.height


    fun padding() : Spacing = this.padding


    fun margins() : Spacing = this.margins


    fun backgroundColorTheme() : ColorTheme = this.backgroundColorTheme


    fun corners() : Corners = this.corners


    fun alignment() : Alignment = this.alignment


    fun verticalAlignment() : VerticalAlignment = this.verticalAlignment


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun row() : DB_ElementFormat = dbElementFormat(this.position,
                                                            this.height,
                                                            this.padding,
                                                            this.margins,
                                                            this.backgroundColorTheme,
                                                            this.corners,
                                                            this.alignment,
                                                            this.verticalAlignment)

}
