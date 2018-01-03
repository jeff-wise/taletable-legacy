
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.lib.orm.sql.*
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



sealed class Height : ToDocument, SQLSerializable, Serializable
{


    override fun toString() : String = when(this)
    {
        is Wrap  -> "wrap"
        is Fixed -> this.value.toString()
    }


    /**
     * Height Wrap
     */
    object Wrap : Height()
    {
        override fun asSQLValue() : SQLValue = SQLReal({0.0})

        override fun toDocument() = DocNumber(0.0)
    }


    /**
     * Fixed
     */
    data class Fixed(val value : Float) : Height(), SQLSerializable, Serializable
    {

        // -----------------------------------------------------------------------------------------
        // SQL SERIALIZABLE
        // -----------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

        override fun toDocument() = DocNumber(this.value.toDouble())

    }


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<Height> = when (doc)
        {
            is DocNumber ->
            {
                val num = doc.number.toFloat()
                if (num == 0.0f)
                    effValue<ValueError,Height>(Height.Wrap)
                else
                    effValue<ValueError,Height>(Height.Fixed(num))
            }
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    fun isWrap() : Boolean = when (this)
    {
        is Wrap -> true
        else    -> false

    }

}


sealed class Width : ToDocument, SQLSerializable, Serializable
{


    override fun toString() : String = when(this)
    {
        is Wrap  -> "wrap"
        is Fixed -> this.value.toString()
    }


    /**
     * Height Wrap
     */
    object Wrap : Width()
    {
        override fun asSQLValue() : SQLValue = SQLReal({0.0})

        override fun toDocument() = DocNumber(0.0)
    }


    /**
     * Fixed
     */
    data class Fixed(val value : Float) : Width(), SQLSerializable, Serializable
    {

        // -----------------------------------------------------------------------------------------
        // SQL SERIALIZABLE
        // -----------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

        override fun toDocument() = DocNumber(this.value.toDouble())

    }


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<Width> = when (doc)
        {
            is DocNumber ->
            {
                val num = doc.number.toFloat()
                if (num == 0.0f)
                    effValue<ValueError,Width>(Width.Wrap)
                else
                    effValue<ValueError,Width>(Width.Fixed(num))
            }
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    fun isWrap() : Boolean = when (this)
    {
        is Wrap -> true
        else    -> false

    }

}
