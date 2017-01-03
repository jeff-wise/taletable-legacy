
package com.kispoko.tome.sheet;


import com.kispoko.tome.activity.sheet.PagePagerAdapter;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.game.Game;
import com.kispoko.tome.engine.RulesEngine;
import com.kispoko.tome.sheet.group.Group;
import com.kispoko.tome.sheet.group.GroupRow;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.HashMap;
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

    private PrimitiveFunctor<Long>      last_used;

    private ModelFunctor<Game>          game;
    private ModelFunctor<Section>       profileSection;
    private ModelFunctor<Section>       encounterSection;
    private ModelFunctor<Section>       campaignSection;
    private ModelFunctor<RulesEngine>   rules;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Map<UUID,Widget>            componentById;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Sheet()
    {
        this.id                 = null;

        Long currentTimeMS      = System.currentTimeMillis();
        this.last_used          = new PrimitiveFunctor<>(currentTimeMS, Long.class);

        this.game               = ModelFunctor.empty(Game.class);
        this.profileSection     = ModelFunctor.empty(Section.class);
        this.encounterSection   = ModelFunctor.empty(Section.class);
        this.campaignSection    = ModelFunctor.empty(Section.class);
        this.rules              = ModelFunctor.empty(RulesEngine.class);
    }


    public Sheet(UUID id,
                 Game game,
                 Section profileSection,
                 Section encounterSection,
                 Section campaignSection,
                 RulesEngine rulesEngine)
    {
        this.id                 = id;

        Long currentTimeMS      = System.currentTimeMillis();
        this.last_used          = new PrimitiveFunctor<>(currentTimeMS, Long.class);

        this.game               = ModelFunctor.full(game, Game.class);

        this.profileSection     = ModelFunctor.full(profileSection, Section.class);
        this.encounterSection   = ModelFunctor.full(encounterSection, Section.class);
        this.campaignSection    = ModelFunctor.full(null, Section.class);

        this.rules              = ModelFunctor.full(rulesEngine, RulesEngine.class);

        indexComponents();
    }


    public static Sheet fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID        id          = UUID.randomUUID();

        Game        game        = Game.fromYaml(yaml.atKey("game"));

        Section     profile     = Section.fromYaml(yaml.atKey("profile"));
        Section     encounter   = Section.fromYaml(yaml.atKey("encounter"));

        RulesEngine rulesEngine = RulesEngine.fromYaml(yaml.atKey("rulesEngine"));

        return new Sheet(id, game, profile, encounter, null, rulesEngine);
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
        indexComponents();
    }


    // > Initialize
    // ------------------------------------------------------------------------------------------

    public void initialize()
    {
        State.initializeMechanics();

        this.profileSection().initialize();
        this.encounterSection().initialize();
        //this.campaignSection().initialize();
    }


    // > State
    // ------------------------------------------------------------------------------------------


    public Widget componentWithId(UUID componentId)
    {
        return this.componentById.get(componentId);
    }


    // ** Sections
    // ------------------------------------------------------------------------------------------

    /**
     * Get the profileSection section.
     * @return The profileSection section.
     */
    public Section profileSection()
    {
        return this.profileSection.getValue();
    }


    /**
     * Get the encounterSection section.
     * @return The encounterSection section.
     */
    public Section encounterSection()
    {
        return this.encounterSection.getValue();
    }


    public Game getGame()
    {
        return this.game.getValue();
    }


    /**
     * Get the rules engine for this sheet.
     * @return The Rules Engine.
     */
    public RulesEngine rulesEngine()
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

    /**
     * Index the widgets by their id, so that can later be retrieved.
     */
    private void indexComponents()
    {
        componentById = new HashMap<>();

        for (Page page : this.profileSection.getValue().pages()) {
            for (Group group : page.groups()) {
                for (GroupRow groupRow : group.rows()) {
                    for (Widget widget : groupRow.widgets()) {
                        componentById.put(widget.getId(), widget);
                    }
                }
            }
        }
    }


    // LISTENERS
    // ------------------------------------------------------------------------------------------

    public interface OnSheetListener {
        void onSheet(Sheet sheet);
    }



}
