
package com.kispoko.tome.model.engine.program


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
 * Program Name
 */
data class ProgramName(val value : String)
{

    companion object : Factory<ProgramName>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ProgramName> = when (doc)
        {
            is DocText -> valueResult(ProgramName(doc.text))
            else       -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}

