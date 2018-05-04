
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.engine.dice.DiceRoll
import com.kispoko.tome.util.Util
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Roll Format
 */
sealed class RollFormat : ToDocument, SQLSerializable, Serializable
{

    object ModifierOnly : RollFormat()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "modifier_only" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("modifier_only")
    }


    object Normal : RollFormat()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"normal"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("normal")
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<RollFormat> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "normal"        -> effValue<ValueError,RollFormat>(RollFormat.Normal)
                "modifier_only" -> effValue<ValueError,RollFormat>(RollFormat.ModifierOnly)
                else            -> effError<ValueError,RollFormat>(
                                       UnexpectedValue("RollFormat", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    fun rollString(diceRoll : DiceRoll) : String = when (this)
    {
        is Normal       -> diceRoll.toString()
        is ModifierOnly -> diceRoll.modifierString()
    }

}

