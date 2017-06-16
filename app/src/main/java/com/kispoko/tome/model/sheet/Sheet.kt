
package com.kispoko.tome.model.sheet


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.sheet.section.Section
import com.kispoko.tome.model.sheet.section.SectionName
import com.kispoko.tome.rts.sheet.SheetContext
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Sheet
 */
data class Sheet(override val id : UUID,
                 val sheetId : Prim<SheetId>,
                 val campaignId: Prim<CampaignId>,
                 val sections : Coll<Section>,
                 val settings : Comp<Settings>) : Model, Serializable
{

    companion object : Factory<Sheet>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Sheet> = when (doc)
        {
            is DocDict -> effApply(::Sheet,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Sheet Id
                                   doc.at("id") ap {
                                       effApply(::Prim, SheetId.fromDocument(it))
                                   },
                                   // Campaign Id
                                   doc.at("campaign_id") ap {
                                       effApply(::Prim, CampaignId.fromDocument(it))
                                   },
                                   // Section List
                                   doc.list("sections") ap { docList ->
                                       effApply(::Coll,
                                                docList.map { Section.fromDocument(it) })
                                   },
                                   // Sheet Settings
                                   split(doc.maybeAt("description"),
                                         effValue(Comp(Settings.default())),
                                         { effApply(::Comp, Settings.fromDocument(it)) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun sheetId() : SheetId = this.sheetId.value

    fun campaignId() : CampaignId = this.campaignId.value

    fun sections() : List<Section> = this.sections.list

    fun sectionWithName(sectionName : SectionName) : Section? =
        this.sections().filter { it.name().equals(sectionName) }
                       .firstOrNull()

    fun settings() : Settings = this.settings.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // ON ACTIVE
    // -----------------------------------------------------------------------------------------

    fun onActive(sheetContext : SheetContext)
    {
        sections.list.forEach { it.onActive(sheetContext) }
    }

}


/**
 * Sheet Id
 */
data class SheetId(val name : String) : Serializable
{

    companion object : Factory<SheetId>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<SheetId> = when (doc)
        {
            is DocText -> effValue(SheetId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}

