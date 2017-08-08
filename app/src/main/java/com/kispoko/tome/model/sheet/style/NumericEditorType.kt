
package com.kispoko.tome.model.sheet.style


import android.util.Log
import android.view.Gravity
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.effError
import effect.effValue
import lulo.document.DocText
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Numeric Editor Type
 */
sealed class NumericEditorType : SQLSerializable, Serializable
{

    object Calculator : NumericEditorType()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "calculator" })
    }


    object Simple : NumericEditorType()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "simple" })
    }


    object Adder : NumericEditorType()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "adder" })
    }


    companion object
    {
        fun fromDocument(doc : SpecDoc) : ValueParser<NumericEditorType> = when (doc)
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

