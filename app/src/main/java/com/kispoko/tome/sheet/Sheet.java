
package com.kispoko.tome.sheet;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.activity.sheet.PagePagerAdapter;
import com.kispoko.tome.campaign.Campaign;
import com.kispoko.tome.campaign.CampaignIndex;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.VariableType;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.game.Game;
import com.kispoko.tome.engine.RulesEngine;
import com.kispoko.tome.sheet.group.Group;
import com.kispoko.tome.sheet.group.GroupRow;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.sheet.widget.WidgetUnion;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.tuple.Tuple2;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Character Sheet
 *
 * This class represents the structure and representation of character sheet. Character sheets
 * can therefore be customized for different roleplaying games or even different campaigns.
 */
public class Sheet implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<Long>      lastUsed;

    private ModelFunctor<Section>       profileSection;
    private ModelFunctor<Section>       encounterSection;
    private ModelFunctor<Section>       campaignSection;

    private ModelFunctor<Game>          game;
    private PrimitiveFunctor<String>    campaignName;
    private ModelFunctor<RulesEngine>   rules;

    private PrimitiveFunctor<String[]>  summaryVariables;
    private ModelFunctor<Summary>       summary;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Map<UUID,Widget>            componentById;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Sheet()
    {
        this.id                 = null;

        Long currentTimeMS      = System.currentTimeMillis();
        this.lastUsed           = new PrimitiveFunctor<>(currentTimeMS, Long.class);

        this.profileSection     = ModelFunctor.empty(Section.class);
        this.encounterSection   = ModelFunctor.empty(Section.class);
        this.campaignSection    = ModelFunctor.empty(Section.class);

        this.campaignName       = new PrimitiveFunctor<>(null, String.class);
        this.game               = ModelFunctor.empty(Game.class);
        this.rules              = ModelFunctor.empty(RulesEngine.class);

        this.summaryVariables   = new PrimitiveFunctor<>(null, String[].class);
        this.summary            = ModelFunctor.empty(Summary.class);
    }


    public Sheet(UUID id,
                 Game game,
                 String campaignName,
                 List<String> summaryVariables,
                 RulesEngine rulesEngine,
                 Section profileSection,
                 Section encounterSection,
                 Section campaignSection)
    {
        this.id                 = id;

        Long currentTimeMS      = System.currentTimeMillis();
        this.lastUsed           = new PrimitiveFunctor<>(currentTimeMS, Long.class);

        this.profileSection     = ModelFunctor.full(profileSection, Section.class);
        this.encounterSection   = ModelFunctor.full(encounterSection, Section.class);
        this.campaignSection    = ModelFunctor.full(campaignSection, Section.class);

        this.campaignName       = new PrimitiveFunctor<>(campaignName, String.class);
        this.game               = ModelFunctor.full(game, Game.class);
        this.rules              = ModelFunctor.full(rulesEngine, RulesEngine.class);

        String[] summaryVariablesArray = summaryVariables.toArray(new String[0]);
        this.summaryVariables   = new PrimitiveFunctor<>(summaryVariablesArray, String[].class);
        this.summary            = ModelFunctor.full(null, Summary.class);

        initializeSheet();
    }


    /**
     * Create a Sheet from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Sheet.
     * @throws YamlParseException
     */
    public static Sheet fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID         id               = UUID.randomUUID();

        Section      profile          = Section.fromYaml(SectionType.PROFILE,
                                                         yaml.atKey("profile"));
        Section      encounter        = Section.fromYaml(SectionType.ENCOUNTER,
                                                        yaml.atKey("encounter"));
        Section      campaign        = Section.fromYaml(SectionType.CAMPAIGN,
                                                        yaml.atKey("campaign"));

        String       campaignName     = yaml.atKey("campaign_name").getString();
        Game         game             = Game.fromYaml(yaml.atKey("game"));
        RulesEngine  rulesEngine      = RulesEngine.fromYaml(yaml.atKey("engine"));

        List<String> summaryVariables = yaml.atKey("summary_variables").getStringList();

        return new Sheet(id, game, campaignName, summaryVariables,
                         rulesEngine, profile, encounter, campaign);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Sheet is completely loaded for the first time.
     */
    public void onLoad()
    {
        initializeSheet();
    }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("game", this.game())
                .putString("campaign_name", this.campaignName())
                .putStringList("summary_variables", this.summaryVariables())
                .putYaml("profile", this.profileSection())
                .putYaml("encounter", this.encounterSection())
                .putYaml("campaign", this.campaignSection())
                .putYaml("engine", this.engine());
    }


    // > Initialize
    // ------------------------------------------------------------------------------------------

    public void initialize()
    {
        State.initializeMechanics();

        this.profileSection().initialize();
        this.encounterSection().initialize();

        this.summary.setValue(sheetSummary());
        this.summary.save();
    }


    // > Components
    // ------------------------------------------------------------------------------------------

    public Widget componentWithId(UUID componentId)
    {
        return this.componentById.get(componentId);
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The name of the campaign.
     * @return The campaign name.
     */
    private String campaignName()
    {
        return this.campaignName.getValue();
    }


    /**
     * The variables which act as summary data for the sheet i.e highlight important information.
     * These values are used in the sheet list to provide an interesting overview of the sheet.
     * @return The List of variable names.
     */
    private List<String> summaryVariables()
    {
        return Arrays.asList(this.summaryVariables.getValue());
    }


    /**
     * The profile section
     * @return The profile section.
     */
    public Section profileSection()
    {
        return this.profileSection.getValue();
    }


    /**
     * The encounter section.
     * @return The encounter section.
     */
    public Section encounterSection()
    {
        return this.encounterSection.getValue();
    }


    /**
     * The campaign section.
     * @return The campaign section.
     */
    public Section campaignSection()
    {
        return this.campaignSection.getValue();
    }


    public Game game()
    {
        return this.game.getValue();
    }


    /**
     * Get the rules engine for this sheet.
     * @return The Rules Engine.
     */
    public RulesEngine engine()
    {
        return this.rules.getValue();
    }


    // ** Page Pager Adapter
    // ------------------------------------------------------------------------------------------

    /**
     * Render the sheet.
     * @param pagePagerAdapter The Page Pager Adapter to be passed to the Roleplay instance so it
     *                         can update the adapter view when the pages change.
     *
     */
    public void render(PagePagerAdapter pagePagerAdapter)
    {
        this.profileSection().render(pagePagerAdapter);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initializeSheet()
    {
        indexComponents();
    }


    /**
     * Index the widgets by their id, so that can later be retrieved.
     */
    private void indexComponents()
    {
        componentById = new HashMap<>();

        for (Page page : this.profileSection.getValue().pages()) {
            for (Group group : page.groups()) {
                for (GroupRow groupRow : group.rows()) {
                    for (WidgetUnion widgetUnion : groupRow.widgets()) {
                        componentById.put(widgetUnion.widget().getId(), widgetUnion.widget());
                    }
                }
            }
        }
    }


    private Summary sheetSummary()
    {
        // > Sheet Name
        String sheetName = "";
        VariableUnion nameVariable = State.variableWithName("name");
        if (nameVariable != null && nameVariable.type() == VariableType.TEXT)
        {
            try {
                sheetName = nameVariable.textVariable().value();
            }
            catch (NullVariableException exception) {
                ApplicationFailure.nullVariable(exception);
                sheetName = "N/A";
            }
        }

        // > Campaign Name
        String campaignName = "";
        Campaign campaign = CampaignIndex.campaignWithName(this.campaignName());
        if (campaign != null)
            campaignName = campaign.label();

        // > Feature 1
        Tuple2<String,String> feature1 = null;
        if (summaryVariables().size() > 0) {
            feature1 = State.variableTuple(summaryVariables().get(0));
        }

        // > Feature 2
        Tuple2<String,String> feature2 = null;
        if (summaryVariables().size() > 1) {
            feature2 = State.variableTuple(summaryVariables().get(1));
        }

        // > Feature 3
        Tuple2<String,String> feature3 = null;
        if (summaryVariables().size() > 2) {
            feature3 = State.variableTuple(summaryVariables().get(2));
        }


        return new Summary(
                        UUID.randomUUID(),
                        sheetName,
                        this.lastUsed.getValue(),
                        campaignName,
                        feature1,
                        feature2,
                        feature3);
    }


    private void initializeSummary()
    {

    }


    // LISTENERS
    // ------------------------------------------------------------------------------------------

    public interface OnSheetListener {
        void onSheet(Sheet sheet);
    }



}
