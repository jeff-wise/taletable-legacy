
package com.kispoko.tome.model.sheet


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.campaign.CampaignName
import com.kispoko.tome.model.sheet.section.Section
import effect.Err
import effect.effApply
import effect.effApply2
import effect.effApply4
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Sheet
 */
data class Sheet(override val id : UUID,
                 val campaignName : Func<CampaignName>,
                 val sections : Coll<Section>,
                 val settings : Func<Settings>) : Model(id)
{

    companion object : Factory<Sheet>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Sheet> = when (doc)
        {
            is DocDict -> effApply4(::Sheet,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Campaign Name
                                    doc.at("campaign_name") ap {
                                        effApply(::Prim, CampaignName.fromDocument(it))
                                    },
                                    // Section List
                                    doc.list("sections") ap { docList ->
                                        effApply(::Coll,
                                                 docList.map { Section.fromDocument(it) })
                                    },
                                    // Sheet Settings
                                    doc.at("settings") ap {
                                        effApply(::Comp, Settings.fromDocument(it))
                                    })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}






