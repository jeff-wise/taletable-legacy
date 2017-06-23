
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLReal
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.util.Util
import effect.effApply
import effect.effError
import effect.effValue
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
                   val bottom : Prim<BottomSpacing>) : Model, Serializable
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

        override fun fromDocument(doc : SpecDoc) : ValueParser<Spacing> = when (doc)
        {
            is DocDict -> effApply(::Spacing,
                                   // Left
                                   doc.at("left") ap { LeftSpacing.fromDocument(it) },
                                   // Top
                                   doc.at("top") ap { TopSpacing.fromDocument(it) },
                                   // Right
                                   doc.at("right") ap { RightSpacing.fromDocument(it) },
                                   // Bottom
                                   doc.at("bottom") ap { BottomSpacing.fromDocument(it) }
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : Spacing = Spacing(LeftSpacing(0.0f),
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
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "spacing"

    override val modelObject = this

}


/**
 * Left Spacing
 */
data class LeftSpacing(val value : Float) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<LeftSpacing>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<LeftSpacing> = when (doc)
        {
            is DocNumber -> effValue(LeftSpacing(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}


/**
 * Top Spacing
 */
data class TopSpacing(val value : Float) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TopSpacing>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TopSpacing> = when (doc)
        {
            is DocNumber -> effValue(TopSpacing(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}


/**
 * Right Spacing
 */
data class RightSpacing(val value : Float) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RightSpacing>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<RightSpacing> = when (doc)
        {
            is DocNumber -> effValue(RightSpacing(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}


/**
 * Bottom Spacing
 */
data class BottomSpacing(val value : Float) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BottomSpacing>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<BottomSpacing> = when (doc)
        {
            is DocNumber -> effValue(BottomSpacing(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}
