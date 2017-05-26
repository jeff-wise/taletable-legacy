
package com.kispoko.tome.model.sheet;


import com.kispoko.tome.model.campaign.Campaign;


/**
 * Character Sheet
 *
 * This class represents the structure and representation of character sheet. Character sheets
 * can therefore be customized for different roleplaying games or even different campaigns.
 */

/*
public class Sheet extends Model
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<Long>      lastUsed;


    private CollectionFunctor<Section>  sections;

    private PrimitiveFunctor<String>    campaignName;

    private PrimitiveFunctor<String[]>  summaryVariables;
    private ModelFunctor<Summary>       summary;

    private ModelFunctor<Settings>      settings;


    // > Internal
    // -----------------------------------------------------------------------------------------

    private Map<UUID,WidgetUnion>       widgetById;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public Sheet()
    {
        this.id                 = null;

        Long currentTimeMS      = System.currentTimeMillis();
        this.lastUsed           = new PrimitiveFunctor<>(currentTimeMS, Long.class);

        this.sections           = CollectionFunctor.empty(Section.class);

        this.campaignName       = new PrimitiveFunctor<>(null, String.class);

        this.summaryVariables   = new PrimitiveFunctor<>(null, String[].class);
        this.summary            = ModelFunctor.empty(Summary.class);

        this.settings           = ModelFunctor.empty(Settings.class);
    }


    public Sheet(UUID id,
                 String campaignName,
                 List<String> summaryVariables,
                 Settings settings,
                 List<Section> sections)
    {
        this.id                 = id;

        Long currentTimeMS      = System.currentTimeMillis();
        this.lastUsed           = new PrimitiveFunctor<>(currentTimeMS, Long.class);

        this.sections           = CollectionFunctor.full(sections, Section.class);

        this.campaignName       = new PrimitiveFunctor<>(campaignName, String.class);

        String[] summaryVariablesArray = summaryVariables.toArray(new String[0]);
        this.summaryVariables   = new PrimitiveFunctor<>(summaryVariablesArray, String[].class);
        this.summary            = ModelFunctor.full(null, Summary.class);

        this.settings           = ModelFunctor.full(settings, Settings.class);

        initializeSheet();
    }

        */
//
//    /**
//     * Create a Sheet from its Yaml representation.
//     * @param yaml The yaml parser.
//     * @return The parsed Sheet.
//     * @throws YamlParseException
//     */
//    public static Sheet fromYaml(YamlParser yaml)
//                  throws YamlParseException
//    {
//        UUID         id               = UUID.randomUUID();
//
//        String       campaignName     = yaml.atKey("campaign_name").getString();
//
//        List<String> summaryVariables = yaml.atKey("summary_variables").getStringList();
//
//        Settings     settings         = Settings.fromYaml(yaml.atMaybeKey("settings"));
//
//        return new Sheet(id, campaignName, summaryVariables, settings,
//                         new ArrayList<Section>());
//    }
//
//
//    // API
//    // ------------------------------------------------------------------------------------------
//
//    // > Model
//    // ------------------------------------------------------------------------------------------
//
//    // ** Id
//    // ------------------------------------------------------------------------------------------
//
//    public UUID getId()
//    {
//        return this.id;
//    }
//
//
//    public void setId(UUID id)
//    {
//        this.id = id;
//    }
//
//
//    // ** On Load
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * This method is called when the Sheet is completely loaded for the first time.
//     */
//    public void onLoad()
//    {
//        initializeSheet();
//    }
//
//
//    // > Yaml
//    // ------------------------------------------------------------------------------------------
//
//    public YamlBuilder toYaml()
//    {
//        return YamlBuilder.map()
//                .putString("campaign_name", this.campaignName())
//                .putStringList("summary_variables", this.summaryVariables())
//                .putYaml("settings", this.settings());
//    }
//
//
//    // > Initialize
//    // ------------------------------------------------------------------------------------------
//
//    public void initialize(Context context)
//    {
//        State.initialize();
//
//
//        this.summary.setValue(sheetSummary());
//        this.summary.saveAsync();
//    }
//
//
//    // > Components
//    // ------------------------------------------------------------------------------------------
//
//    public WidgetUnion widgetWithId(UUID widgetId)
//    {
//        return this.widgetById.get(widgetId);
//    }
//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The name of the campaign.
//     * @return The campaign name.
//     */
//    private String campaignName()
//    {
//        return this.campaignName.getValue();
//    }
//
//
//    /**
//     * The variables which act as summary data for the sheet i.e highlight important information.
//     * These values are used in the sheet list to provide an interesting overview of the sheet.
//     * @return The List of variable names.
//     */
//    private List<String> summaryVariables()
//    {
//        return Arrays.asList(this.summaryVariables.getValue());
//    }
//
//
//    /**
//     * The sheet settings.
//     * @return The settings.
//     */
//    public Settings settings()
//    {
//        return this.settings.getValue();
//    }
//
//
//    public List<Section> sections()
//    {
//        return this.sections.getValue();
//    }
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
//
//
//    // LISTENERS
//    // ------------------------------------------------------------------------------------------
//
//    public interface OnSheetListener {
//        void onSheet(Sheet sheet);
//    }
//
//
//
//}
