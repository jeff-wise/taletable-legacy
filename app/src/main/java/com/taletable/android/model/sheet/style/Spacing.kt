
package com.taletable.android.model.sheet.style


import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.util.Util
import effect.apply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Spacing
 */
data class Spacing(val top : Double,
                   val right : Double,
                   val bottom : Double,
                   val left : Double)
                    : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Spacing>
    {

        override fun fromDocument(doc : SchemaDoc) : ValueParser<Spacing> = when (doc)
        {
            is DocDict ->
            {
                apply(::Spacing,
                      // Top
                      split(doc.maybeDouble("top"),
                            effValue(0.0),
                            { effValue(it) }),
                      // Right
                      split(doc.maybeDouble("right"),
                            effValue(0.0),
                            { effValue(it) }),
                      // Bottom
                      split(doc.maybeDouble("bottom"),
                            effValue(0.0),
                            { effValue(it) }),
                      // Left
                      split(doc.maybeDouble("left"),
                            effValue(0.0),
                            { effValue(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = Spacing(0.0, 0.0, 0.0, 0.0)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    // DP (Standard)
    // -----------------------------------------------------------------------------------------

    fun leftDp() : Float = this.left.toFloat()

    fun topDp() : Float = this.top.toFloat()

    fun rightDp() : Float = this.right.toFloat()

    fun bottomDp() : Float = this.bottom.toFloat()


    // Pixels
    // -----------------------------------------------------------------------------------------

    fun leftPx() : Int = Util.dpToPixel(this.leftDp().toFloat())

    fun topPx() : Int = Util.dpToPixel(this.topDp().toFloat())

    fun rightPx() : Int = Util.dpToPixel(this.rightDp().toFloat())

    fun bottomPx() : Int = Util.dpToPixel(this.bottomDp().toFloat())


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "top" to DocNumber(this.top),
        "right" to DocNumber(this.right),
        "bottom" to DocNumber(this.bottom),
        "left" to DocNumber(this.left)
    ))


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ "$top,$right,$bottom,$left" })


}


/**
 * Left Spacing
 */
//data class LeftSpacing(val value : Float) : ToDocument, SQLSerializable, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<LeftSpacing>
//    {
//
//        override fun fromDocument(doc: SchemaDoc): ValueParser<LeftSpacing> = when (doc)
//        {
//            is DocNumber -> effValue(LeftSpacing(doc.number.toFloat()))
//            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
//        }
//
//        fun default() : LeftSpacing = LeftSpacing(0.0f)
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
// * Top Spacing
// */
//data class TopSpacing(val value : Float) : ToDocument, SQLSerializable, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<TopSpacing>
//    {
//
//        override fun fromDocument(doc: SchemaDoc): ValueParser<TopSpacing> = when (doc)
//        {
//            is DocNumber -> effValue(TopSpacing(doc.number.toFloat()))
//            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
//        }
//
//        fun default() : TopSpacing = TopSpacing(0.0f)
//    }
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
// * Right Spacing
// */
//data class RightSpacing(val value : Float) : ToDocument, SQLSerializable, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<RightSpacing>
//    {
//
//        override fun fromDocument(doc: SchemaDoc): ValueParser<RightSpacing> = when (doc)
//        {
//            is DocNumber -> effValue(RightSpacing(doc.number.toFloat()))
//            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
//        }
//
//        fun default() : RightSpacing = RightSpacing(0.0f)
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
// * Bottom Spacing
// */
//data class BottomSpacing(val value : Float) : ToDocument, SQLSerializable, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<BottomSpacing>
//    {
//
//        override fun fromDocument(doc: SchemaDoc): ValueParser<BottomSpacing> = when (doc)
//        {
//            is DocNumber -> effValue(BottomSpacing(doc.number.toFloat()))
//            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
//        }
//
//        fun default() : BottomSpacing = BottomSpacing(0.0f)
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
