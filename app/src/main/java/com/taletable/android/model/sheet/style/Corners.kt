
package com.taletable.android.model.sheet.style


import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.util.Util
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable



data class Corners(val topLeftRadius : Double,
                   val topRightRadius : Double,
                   val bottomRightRadius : Double,
                   val bottomLeftRadius : Double)
                    : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Corners>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<Corners> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::Corners,
                      // Top Left
                      split(doc.maybeDouble("top_left_radius"),
                            effValue(0.0),
                            { effValue(it) }),
                      // Top Right
                      split(doc.maybeDouble("top_right_radius"),
                            effValue(0.0),
                            { effValue(it) }),
                       // Bottom Right
                      split(doc.maybeDouble("bottom_right_radius"),
                            effValue(0.0),
                            { effValue(it) }),
                      // Bottom Left
                      split(doc.maybeDouble("bottom_left_radius"),
                            effValue(0.0),
                            { effValue(it) })
                     )
            }

            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = Corners(0.0, 0.0, 0.0, 0.0)

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "top_left_radius" to DocNumber(this.topLeftRadius),
        "top_right_radius" to DocNumber(this.topRightRadius),
        "bottom_right_radius" to DocNumber(this.bottomRightRadius),
        "bottom_left_radius" to DocNumber(this.bottomLeftRadius)
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    // DP (Standard)
    // -----------------------------------------------------------------------------------------

    fun topLeftCornerRadiusDp() : Float = this.topLeftRadius.toFloat()

    fun topRightCornerRadiusDp() : Float = this.topRightRadius.toFloat()

    fun bottomRightCornerRadiusDp() : Float = this.bottomRightRadius.toFloat()

    fun bottomLeftCornerRadiusDp() : Float = this.bottomLeftRadius.toFloat()


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun radiiArray() : FloatArray
    {
        val topLeft     = Util.dpToPixel(this.topLeftCornerRadiusDp()).toFloat()
        val topRight    = Util.dpToPixel(this.topRightCornerRadiusDp()).toFloat()
        val bottomRight = Util.dpToPixel(this.bottomRightCornerRadiusDp()).toFloat()
        val bottomLeft  = Util.dpToPixel(this.bottomLeftCornerRadiusDp()).toFloat()

        val radii = listOf(topLeft, topLeft, topRight, topRight,
                         bottomRight, bottomRight, bottomLeft, bottomLeft)

        return radii.toFloatArray()
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue =
            SQLText({ "$topLeftRadius,$topRightRadius,$bottomRightRadius,$bottomLeftRadius" })

}

//
///**
// * Top Left Corner Radius
// */
//data class TopLeftCornerRadius(val value : Float) : ToDocument, SQLSerializable, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<TopLeftCornerRadius>
//    {
//
//        override fun fromDocument(doc: SchemaDoc): ValueParser<TopLeftCornerRadius> = when (doc)
//        {
//            is DocNumber -> effValue(TopLeftCornerRadius(doc.number.toFloat()))
//            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
//        }
//
//        fun default() = TopLeftCornerRadius(0.0f)
//
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // TO DOCUMENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun toDocument() = DocNumber(this.value.toDouble())
//
//
//    // -----------------------------------------------------------------------------------------
//    // SQL SERIALIZABLE
//    // -----------------------------------------------------------------------------------------
//
//    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})
//
//}
//
//
///**
// * Top Right Corner Radius
// */
//data class TopRightCornerRadius(val value : Float) : ToDocument, SQLSerializable, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<TopRightCornerRadius>
//    {
//
//        override fun fromDocument(doc: SchemaDoc): ValueParser<TopRightCornerRadius> = when (doc)
//        {
//            is DocNumber -> effValue(TopRightCornerRadius(doc.number.toFloat()))
//            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
//        }
//
//        fun default() = TopRightCornerRadius(0.0f)
//
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // TO DOCUMENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun toDocument() = DocNumber(this.value.toDouble())
//
//    // -----------------------------------------------------------------------------------------
//    // SQL SERIALIZABLE
//    // -----------------------------------------------------------------------------------------
//
//    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})
//
//}
//
//
///**
// * Bottom Right Corner Radius
// */
//data class BottomRightCornerRadius(val value : Float) : ToDocument, SQLSerializable, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<BottomRightCornerRadius>
//    {
//
//        override fun fromDocument(doc: SchemaDoc): ValueParser<BottomRightCornerRadius> = when (doc)
//        {
//            is DocNumber -> effValue(BottomRightCornerRadius(doc.number.toFloat()))
//            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
//        }
//
//        fun default() = BottomRightCornerRadius(0.0f)
//
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // TO DOCUMENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun toDocument() = DocNumber(this.value.toDouble())
//
//
//    // -----------------------------------------------------------------------------------------
//    // SQL SERIALIZABLE
//    // -----------------------------------------------------------------------------------------
//
//    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})
//
//}
//
//
///**
// * Bottom Left Corner Radius
// */
//data class BottomLeftCornerRadius(val value : Float) : ToDocument, SQLSerializable, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<BottomLeftCornerRadius>
//    {
//
//        override fun fromDocument(doc: SchemaDoc): ValueParser<BottomLeftCornerRadius> = when (doc)
//        {
//            is DocNumber -> effValue(BottomLeftCornerRadius(doc.number.toFloat()))
//            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
//        }
//
//        fun default() = BottomLeftCornerRadius(0.0f)
//
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // TO DOCUMENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun toDocument() = DocNumber(this.value.toDouble())
//
//
//    // -----------------------------------------------------------------------------------------
//    // SQL SERIALIZABLE
//    // -----------------------------------------------------------------------------------------
//
//    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})
//
//}
