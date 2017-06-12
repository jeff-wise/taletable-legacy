
package com.kispoko.tome.model.sheet


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.sheet.section.Section
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.State
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.util.*



/**
 * Sheet
 */
data class Sheet(override val id : UUID,
                 val sheetId : Prim<SheetId>,
                 val campaignId: Prim<CampaignId>,
                 val sections : Coll<Section>,
                 val settings : Comp<Settings>) : Model
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

    fun settings() : Settings = this.settings.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // ON ACTIVE
    // -----------------------------------------------------------------------------------------

    fun onActive(sheetContext : SheetContext)
    {
        sections.list.forEach { it.onSheetComponentActive(sheetContext) }
    }

}


/**
 * Sheet Id
 */
data class SheetId(val name : String)
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




//
//
//    // ** Page Pager Adapter
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Render the sheet.
//     * @param pagePagerAdapter The Page Pager Adapter to be passed to the Roleplay instance so it
//     *                         can update the adapter view when the pages change.
//     *
//     */
//    public void render(PagePagerAdapter pagePagerAdapter)
//    {
////         this.profileSection().render(pagePagerAdapter);
//    }
//
//
//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    private void initializeSheet()
//    {
//        indexWidgets();
//    }
//
//
//    /**
//     * Index the widgets by their id, so that can later be retrieved.
//     */
//    private void indexWidgets()
//    {
//        widgetById = new HashMap<>();
//
//        for (Section section : this.sections())
//        {
//            for (Page page : section.pages()) {
//                for (Group group : page.groups()) {
//                    for (GroupRow groupRow : group.rows()) {
//                        for (WidgetUnion widgetUnion : groupRow.widgets()) {
//                            widgetById.put(widgetUnion.widget().getId(), widgetUnion);
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//    private Summary sheetSummary()
//    {
//        // > Sheet Name
//        String sheetName = "";
//        VariableUnion nameVariable = State.variableWithName("name");
//        if (nameVariable != null && nameVariable.type() == VariableType.TEXT)
//        {
//            try {
//                sheetName = nameVariable.textVariable().value();
//            }
//            catch (NullVariableException exception) {
//                ApplicationFailure.nullVariable(exception);
//                sheetName = "N/A";
//            }
//        }
//
//        // > Campaign Name
//        String campaignName = "";
//        Campaign campaign = CampaignIndex.campaignWithName(this.campaignName());
//        if (campaign != null)
//            campaignName = campaign.label();
//
//        // > Feature 1
//        Tuple2<String,String> feature1 = null;
//        if (summaryVariables().size() > 0) {
//            feature1 = State.variableTuple(summaryVariables().get(0));
//        }
//
//        // > Feature 2
//        Tuple2<String,String> feature2 = null;
//        if (summaryVariables().size() > 1) {
//            feature2 = State.variableTuple(summaryVariables().get(1));
//        }
//
//        // > Feature 3
//        Tuple2<String,String> feature3 = null;
//        if (summaryVariables().size() > 2) {
//            feature3 = State.variableTuple(summaryVariables().get(2));
//        }
//
//
//        return new Summary(
//                        UUID.randomUUID(),
//                        sheetName,
//                        this.lastUsed.getValue(),
//                        campaignName,
//                        feature1,
//                        feature2,
//                        feature3);
//    }
//
//
//    private void initializeSummary()
//    {
//
//    }
