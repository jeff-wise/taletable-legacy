
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLReal
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.effApply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



data class Corners(override val id : UUID,
                   val topLeftRadius : Prim<TopLeftCornerRadius>,
                   val topRightRadius : Prim<TopRightCornerRadius>,
                   val bottomRightRadius : Prim<BottomRightCornerRadius>,
                   val bottomLeftRadius : Prim<BottomLeftCornerRadius>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.topLeftRadius.name     = "top_left_radius"
        this.topRightRadius.name    = "top_right_radius"
        this.bottomRightRadius.name = "bottom_right_radius"
        this.bottomLeftRadius.name  = "bottom_left_radius"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(topLeftRadius : TopLeftCornerRadius,
                topRightRadius : TopRightCornerRadius,
                bottomRightRadius : BottomRightCornerRadius,
                bottomLeftRadius: BottomLeftCornerRadius)
            : this(UUID.randomUUID(),
                   Prim(topLeftRadius),
                   Prim(topRightRadius),
                   Prim(bottomRightRadius),
                   Prim(bottomLeftRadius))


    companion object : Factory<Corners>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<Corners> = when (doc)
        {
            is DocDict -> effApply(::Corners,
                                   // Top Left
                                   split(doc.maybeAt("top_left_radius"),
                                         effValue(TopLeftCornerRadius.default()),
                                         { TopLeftCornerRadius.fromDocument(it) }),
                                   // Top Right
                                   split(doc.maybeAt("top_right_radius"),
                                         effValue(TopRightCornerRadius.default()),
                                         { TopRightCornerRadius.fromDocument(it) }),
                                   // Bottom Right
                                   split(doc.maybeAt("bottom_right_radius"),
                                         effValue(BottomRightCornerRadius.default()),
                                         { BottomRightCornerRadius.fromDocument(it) }),
                                   // Bottom Left
                                   split(doc.maybeAt("bottom_left_radius"),
                                         effValue(BottomLeftCornerRadius.default()),
                                         { BottomLeftCornerRadius.fromDocument(it) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() : Corners = Corners(TopLeftCornerRadius.default(),
                                          TopRightCornerRadius.default(),
                                          BottomRightCornerRadius.default(),
                                          BottomLeftCornerRadius.default())

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    // DP (Standard)
    // -----------------------------------------------------------------------------------------

    fun topLeftCornerRadiusDp() : Float = this.topLeftRadius.value.value

    fun topRightCornerRadiusDp() : Float = this.topRightRadius.value.value

    fun bottomRightCornerRadiusDp() : Float = this.bottomRightRadius.value.value

    fun bottomLeftCornerRadiusDp() : Float = this.bottomLeftRadius.value.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "spacing"

    override val modelObject = this

}


/**
 * Top Left Corner Radius
 */
data class TopLeftCornerRadius(val value : Float) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TopLeftCornerRadius>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<TopLeftCornerRadius> = when (doc)
        {
            is DocNumber -> effValue(TopLeftCornerRadius(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = TopLeftCornerRadius(0.0f)

    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}


/**
 * Top Right Corner Radius
 */
data class TopRightCornerRadius(val value : Float) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TopRightCornerRadius>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<TopRightCornerRadius> = when (doc)
        {
            is DocNumber -> effValue(TopRightCornerRadius(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = TopRightCornerRadius(0.0f)

    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}


/**
 * Bottom Right Corner Radius
 */
data class BottomRightCornerRadius(val value : Float) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BottomRightCornerRadius>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<BottomRightCornerRadius> = when (doc)
        {
            is DocNumber -> effValue(BottomRightCornerRadius(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = BottomRightCornerRadius(0.0f)

    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}


/**
 * Bottom Left Corner Radius
 */
data class BottomLeftCornerRadius(val value : Float) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BottomLeftCornerRadius>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<BottomLeftCornerRadius> = when (doc)
        {
            is DocNumber -> effValue(BottomLeftCornerRadius(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = BottomLeftCornerRadius(0.0f)

    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}
