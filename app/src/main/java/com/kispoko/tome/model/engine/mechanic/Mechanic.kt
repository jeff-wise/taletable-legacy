
package com.kispoko.tome.model.engine.mechanic


import com.kispoko.tome.lib.Factory
import effect.Err
import lulo.document.DocText
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult



/**
 * Mechanic Category
 */
data class MechanicCategory(val value : String)
{

    companion object : Factory<MechanicCategory>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<MechanicCategory> = when (doc)
        {
            is DocText -> valueResult(MechanicCategory(doc.text))
            else -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}
