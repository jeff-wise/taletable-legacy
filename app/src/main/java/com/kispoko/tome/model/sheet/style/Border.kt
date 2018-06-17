
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.db.DB_BorderValue
import com.kispoko.tome.db.borderTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue4
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.sql.SQLBlob
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.asSQLValue
import com.kispoko.tome.model.theme.ColorTheme
import effect.*
import maybe.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Nothing
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*



/**
 * Border
 */
data class Border(override val id : UUID,
                  private var top : Maybe<BorderEdge>,
                  private var right : Maybe<BorderEdge>,
                  private var bottom : Maybe<BorderEdge>,
                  private var left : Maybe<BorderEdge>)
                   : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(top : Maybe<BorderEdge>,
                right : Maybe<BorderEdge>,
                bottom : Maybe<BorderEdge>,
                left : Maybe<BorderEdge>)
        : this(UUID.randomUUID(),
               top,
               right,
               bottom,
               left)


    companion object : Factory<Border>
    {

        override fun fromDocument(doc : SchemaDoc) : ValueParser<Border> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::Border,
                      // Top
                      split(doc.maybeAt("top"),
                            effValue<ValueError,Maybe<BorderEdge>>(Nothing()),
                            { effApply(::Just, BorderEdge.fromDocument(it)) }),
                      // Right
                      split(doc.maybeAt("right"),
                            effValue<ValueError,Maybe<BorderEdge>>(Nothing()),
                             { effApply(::Just, BorderEdge.fromDocument(it)) }),
                      // Bottom
                      split(doc.maybeAt("bottom"),
                            effValue<ValueError,Maybe<BorderEdge>>(Nothing()),
                            { effApply(::Just, BorderEdge.fromDocument(it)) }),
                      // Left
                      split(doc.maybeAt("left"),
                            effValue<ValueError,Maybe<BorderEdge>>(Nothing()),
                            { effApply(::Just, BorderEdge.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = Border(Nothing(), Nothing(), Nothing(), Nothing())


        fun top(topEdge : BorderEdge) : Border =
                Border(Just(topEdge), Nothing(), Nothing(), Nothing())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
//        "top" to this.top.toDocument(),
//        "right" to this.right.toDocument(),
//        "bottom" to this.bottom.toDocument(),
//        "left" to this.left.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun top() : Maybe<BorderEdge> = this.top


    fun right() : Maybe<BorderEdge> = this.right


    fun bottom() : Maybe<BorderEdge> = this.bottom


    fun left() : Maybe<BorderEdge> = this.left


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_BorderValue =
        RowValue4(borderTable, MaybePrimValue(this.top),
                               MaybePrimValue(this.right),
                               MaybePrimValue(this.bottom),
                               MaybePrimValue(this.left))

}



/**
 * Border Edge
 */
data class BorderEdge(private var colorTheme : ColorTheme,
                      private var thickness : BorderThickness)
                       : ToDocument, SQLSerializable, Serializable
{


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {

        private fun defaultColorTheme() = ColorTheme.transparent
        private fun defaultThickness()  = BorderThickness.default()


        fun fromDocument(doc : SchemaDoc) : ValueParser<BorderEdge> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::BorderEdge,
                      // Color Theme
                      split(doc.maybeAt("color_theme"),
                            effValue(defaultColorTheme()),
                            { ColorTheme.fromDocument(it)} ),
                      // Thickness
                      split(doc.maybeAt("thickness"),
                            effValue(defaultThickness()),
                            { BorderThickness.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = BorderEdge(defaultColorTheme(),
                                   defaultThickness())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "color_theme" to this.colorTheme.toDocument(),
        "thickness" to this.thickness.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun colorTheme() : ColorTheme = this.colorTheme


    fun thickness() : BorderThickness = this.thickness


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLBlob({ SerializationUtils.serialize(this) })


}



/**
 * Border Thickness
 */
data class BorderThickness(val value : Int) : ToDocument, SQLSerializable, java.io.Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BorderThickness>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BorderThickness> = when (doc)
        {
            is DocNumber -> effValue(BorderThickness(doc.number.toInt()))
            else         -> effError(lulo.value.UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = BorderThickness(0)
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



