
package com.kispoko.tome.model.user


import com.kispoko.tome.lib.Factory
import effect.Err
import effect.effError
import effect.effValue
import lulo.document.DocText
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser



/**
 * User Name
 */
data class UserName(val value : String)
{

    companion object : Factory<UserName>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<UserName> = when (doc)
        {
            is DocText -> effValue(UserName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}

