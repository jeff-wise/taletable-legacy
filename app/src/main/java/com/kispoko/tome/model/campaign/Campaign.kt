
package com.kispoko.tome.model.campaign


import com.kispoko.tome.lib.Factory
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser



/**
 * Campaign Name
 */
data class CampaignName(val name : String)
{

    companion object : Factory<CampaignName>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<CampaignName> = when (doc)
        {
            is DocText -> effValue(CampaignName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}
