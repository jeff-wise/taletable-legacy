
package com.kispoko.tome.model.theme


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Color Id
 */
sealed class ColorId : ToDocument, SQLSerializable, Serializable
{

    object Transparent : ColorId()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"transparent"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("transparent")

    }


    object White : ColorId()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"white"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("white")

    }


    object Black : ColorId()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"black"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("black")

    }


    data class Theme(val id : String) : ColorId()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"this.id"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText(this.id)

    }


    companion object : Factory<ColorId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ColorId> = when (doc)
        {
            is DocText ->
            {
                when (doc.text)
                {
                    "transparent" -> effValue<ValueError,ColorId>(ColorId.Transparent)
                    "white"       -> effValue<ValueError,ColorId>(ColorId.White)
                    "black"       -> effValue<ValueError,ColorId>(ColorId.Black)
                    else          -> effValue<ValueError,ColorId>(ColorId.Theme(doc.text))
                }
            }
            else       ->
            {
                effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
            }


        }
    }


}



