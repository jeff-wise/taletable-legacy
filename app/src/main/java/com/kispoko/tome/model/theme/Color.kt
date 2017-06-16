
package com.kispoko.tome.model.theme


import com.kispoko.tome.lib.Factory
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
sealed class ColorId : Serializable
{

    object Transparent : ColorId()

    object White : ColorId()

    object Black : ColorId()

    data class Theme(val id : String) : ColorId()


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
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


}



