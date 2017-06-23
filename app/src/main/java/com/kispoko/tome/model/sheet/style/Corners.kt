
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.R
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
 * Corners
 */
sealed class Corners : SQLSerializable, Serializable
{

    object None : Corners()
    {
        override fun asSQLValue() : SQLValue = SQLText({"none"})
    }


    object Small : Corners()
    {
        override fun asSQLValue() : SQLValue = SQLText({"small"})
    }


    object Medium : Corners()
    {
        override fun asSQLValue() : SQLValue = SQLText({"medium"})
    }


    object Large : Corners()
    {
        override fun asSQLValue() : SQLValue = SQLText({"large"})
    }


    fun resourceId() : Int = when(this)
    {
        is None   -> R.drawable.bg_sheet_corners_none
        is Small  -> R.drawable.bg_sheet_corners_small
        is Medium -> R.drawable.bg_sheet_corners_medium
        is Large  -> R.drawable.bg_sheet_corners_large
    }


    companion object
    {
        fun fromDocument(doc : SpecDoc) : ValueParser<Corners> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "none"   -> effValue<ValueError,Corners>(Corners.None)
                "small"  -> effValue<ValueError,Corners>(Corners.Small)
                "medium" -> effValue<ValueError,Corners>(Corners.Medium)
                "large"  -> effValue<ValueError,Corners>(Corners.Large)
                else     -> effError<ValueError,Corners>(
                                    UnexpectedValue("Corners", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}




