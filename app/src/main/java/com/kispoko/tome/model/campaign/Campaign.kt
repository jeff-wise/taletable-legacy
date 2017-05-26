
package com.kispoko.tome.model.campaign


import com.kispoko.tome.lib.Factory
import effect.Err
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult



/**
 * Campaign Name
 */
data class CampaignName(val name : String)
{

    companion object : Factory<CampaignName>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<CampaignName> = when (doc)
        {
            is DocText -> valueResult(CampaignName(doc.text))
            else       -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}
