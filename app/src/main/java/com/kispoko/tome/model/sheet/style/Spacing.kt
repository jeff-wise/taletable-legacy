
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLReal
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.util.Util
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
 * Spacing
 */
data class Spacing(override val id : UUID,
                   val left : Prim<LeftSpacing>,
                   val top : Prim<TopSpacing>,
                   val right : Prim<RightSpacing>,
                   val bottom : Prim<BottomSpacing>)
                    : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.left.name      = "left"
        this.top.name       = "top"
        this.right.name     = "right"
        this.bottom.name    = "bottom"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(left : LeftSpacing,
                top : TopSpacing,
                right : RightSpacing,
                bottom : BottomSpacing)
            : this(UUID.randomUUID(),
                   Prim(left),
                   Prim(top),
                   Prim(right),
                   Prim(bottom))


    companion object : Factory<Spacing>
    {

        override fun fromDocument(doc : SchemaDoc) : ValueParser<Spacing> = when (doc)
        {
            is DocDict ->
            {
                apply(::Spacing,
                      // Left
                      split(doc.maybeAt("left"),
                            effValue(LeftSpacing.default()),
                            { LeftSpacing.fromDocument(it) }),
                      // Top
                      split(doc.maybeAt("top"),
                            effValue(TopSpacing.default()),
                            { TopSpacing.fromDocument(it) }),
                      // Right
                      split(doc.maybeAt("right"),
                            effValue(RightSpacing.default()),
                            { RightSpacing.fromDocument(it) }),
                      // Bottom
                      split(doc.maybeAt("bottom"),
                            effValue(BottomSpacing.default()),
                            { BottomSpacing.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = Spacing(LeftSpacing(0.0f),
                                TopSpacing(0.0f),
                                RightSpacing(0.0f),
                                BottomSpacing(0.0f))

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    // DP (Standard)
    // -----------------------------------------------------------------------------------------

    fun leftDp() : Float = this.left.value.value

    fun topDp() : Float = this.top.value.value

    fun rightDp() : Float = this.right.value.value

    fun bottomDp() : Float = this.bottom.value.value

    // Pixels
    // -----------------------------------------------------------------------------------------

    fun leftPx() : Int = Util.dpToPixel(this.leftDp())

    fun topPx() : Int = Util.dpToPixel(this.topDp())

    fun rightPx() : Int = Util.dpToPixel(this.rightDp())

    fun bottomPx() : Int = Util.dpToPixel(this.bottomDp())


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "left" to this.left.value.toDocument(),
        "top" to this.top.value.toDocument(),
        "right" to this.right.value.toDocument(),
        "bottom" to this.bottom.value.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "spacing"

    override val modelObject = this

}


/**
 * Left Spacing
 */
data class LeftSpacing(val value : Float) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<LeftSpacing>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<LeftSpacing> = when (doc)
        {
            is DocNumber -> effValue(LeftSpacing(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() : LeftSpacing = LeftSpacing(0.0f)

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}


/**
 * Top Spacing
 */
data class TopSpacing(val value : Float) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TopSpacing>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<TopSpacing> = when (doc)
        {
            is DocNumber -> effValue(TopSpacing(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() : TopSpacing = TopSpacing(0.0f)
    }

    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}


/**
 * Right Spacing
 */
data class RightSpacing(val value : Float) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RightSpacing>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<RightSpacing> = when (doc)
        {
            is DocNumber -> effValue(RightSpacing(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() : RightSpacing = RightSpacing(0.0f)

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}


/**
 * Bottom Spacing
 */
data class BottomSpacing(val value : Float) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BottomSpacing>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<BottomSpacing> = when (doc)
        {
            is DocNumber -> effValue(BottomSpacing(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() : BottomSpacing = BottomSpacing(0.0f)

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}
