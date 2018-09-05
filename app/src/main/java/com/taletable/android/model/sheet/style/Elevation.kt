
package com.taletable.android.model.sheet.style


import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.asSQLValue
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Elevation
 */
data class Elevation(val value : Double) : ToDocument, Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Elevation>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Elevation> = when (doc)
        {
            is DocNumber -> effValue(Elevation(doc.number))
            else         -> effError(lulo.value.UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = Elevation(0.0)
    }


    // | Document
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value)

}

