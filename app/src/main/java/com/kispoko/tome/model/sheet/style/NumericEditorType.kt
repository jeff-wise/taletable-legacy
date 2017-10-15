
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Numeric Editor Type
 */
sealed class NumericEditorType : ToDocument, SQLSerializable, Serializable
{

    object Calculator : NumericEditorType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "calculator" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("calculator")

    }


    object Simple : NumericEditorType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "simple" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("simple")

    }


    object Adder : NumericEditorType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "adder" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("adder")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<NumericEditorType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "calculator" -> effValue<ValueError,NumericEditorType>(
                                    NumericEditorType.Calculator)
                "adder"      -> effValue<ValueError,NumericEditorType>(NumericEditorType.Adder)
                "simple"     -> effValue<ValueError,NumericEditorType>(NumericEditorType.Simple)
                else         -> effError<ValueError,NumericEditorType>(
                                    UnexpectedValue("NumericEditorType", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}

