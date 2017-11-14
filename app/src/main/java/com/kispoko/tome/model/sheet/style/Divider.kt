
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.db.dbDivider
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.orm.Row
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.theme.ColorTheme
import effect.apply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Divider
 */
data class Divider(override val id : UUID,
                   private var colorTheme : ColorTheme,
                   private var margins : Spacing,
                   private var thickness : DividerThickness)
                    : ToDocument, ProdType, Serializable
{


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(colorTheme : ColorTheme,
                margins : Spacing,
                thickness : DividerThickness)
        : this(UUID.randomUUID(),
               colorTheme,
               margins,
               thickness)


    companion object : Factory<Divider>
    {

        private fun defaultColorTheme() = ColorTheme.transparent
        private fun defaultMargins()    = Spacing.default()
        private fun defaultThickness()  = DividerThickness.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<Divider> = when (doc)
        {
            is DocDict ->
            {
                apply(::Divider,
                      // Color Theme
                      split(doc.maybeAt("color_theme"),
                            effValue(defaultColorTheme()),
                            { ColorTheme.fromDocument(it)} ),
                      // Margins
                      split(doc.maybeAt("margins"),
                            effValue(defaultMargins()),
                            { Spacing.fromDocument(it) }),
                      // Thickness
                      split(doc.maybeAt("thickness"),
                            effValue(defaultThickness()),
                            { DividerThickness.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = Divider(defaultColorTheme(),
                                defaultMargins(),
                                defaultThickness())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "color_theme" to this.colorTheme.toDocument(),
        "margins" to this.margins.toDocument(),
        "thickness" to this.thickness.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun colorTheme() : ColorTheme = this.colorTheme


    fun margins() : Spacing = this.margins


    fun thickness() : DividerThickness = this.thickness


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun row() : Row = dbDivider(this.colorTheme, this.margins, this.thickness)

}



/**
 * Divider Thickness
 */
data class DividerThickness(val value : Int) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DividerThickness>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DividerThickness> = when (doc)
        {
            is DocNumber -> effValue(DividerThickness(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() : DividerThickness = DividerThickness(0)
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.value.asSQLValue()

}




