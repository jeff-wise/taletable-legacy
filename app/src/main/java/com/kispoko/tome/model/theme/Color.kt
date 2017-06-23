
package com.kispoko.tome.model.theme


import android.util.Log
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
sealed class ColorId : SQLSerializable, Serializable
{

    object Transparent : ColorId()
    {
        override fun asSQLValue() : SQLValue = SQLText({"transparent"})
    }


    object White : ColorId()
    {
        override fun asSQLValue() : SQLValue = SQLText({"white"})
    }


    object Black : ColorId()
    {
        override fun asSQLValue() : SQLValue = SQLText({"black"})
    }


    data class Theme(val id : String) : ColorId()
    {
        override fun asSQLValue() : SQLValue = SQLText({"this.id"})
    }


    companion object : Factory<ColorId>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ColorId> = when (doc)
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



