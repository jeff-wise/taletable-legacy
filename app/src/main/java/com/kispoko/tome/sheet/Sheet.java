
package com.kispoko.tome.sheet;


import com.kispoko.tome.activity.sheet.PagePagerAdapter;
import com.kispoko.tome.game.Game;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
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

    private UUID                 id;


    // > Values
    // ------------------------------------------------------------------------------------------

    private PrimitiveValue<Long> last_used;

    private ModelValue<Game>     game;
    private ModelValue<Roleplay> roleplay;
    private ModelValue<Rules>    rules;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Map<UUID,Widget>     componentById;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Sheet()
    {
        this.id        = null;

        Long currentTimeMS = System.currentTimeMillis();
        this.last_used = new PrimitiveValue<>(currentTimeMS, Long.class);

        this.game      = new ModelValue<>(null, Game.class);
        this.roleplay  = new ModelValue<>(null, Roleplay.class);
        this.rules     = new ModelValue<>(null, Rules.class);
    }


    public Sheet(UUID id,
                 Game game,
                 Roleplay roleplay,
                 Rules rules)
    {
        this.id        = id;

        Long currentTimeMS = System.currentTimeMillis();
        this.last_used = new PrimitiveValue<>(currentTimeMS, Long.class);

        this.game      = new ModelValue<>(game, Game.class);
        this.roleplay  = new ModelValue<>(roleplay, Roleplay.class);
        this.rules     = new ModelValue<>(rules, Rules.class);

        indexComponents();

    }


    public static Sheet fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID id = UUID.randomUUID();
        Game game = Game.fromYaml(yaml.atKey("game"));
        Roleplay roleplay = Roleplay.fromYaml(yaml.atKey("roleplay"));
        Rules rules = Rules.fromYaml(yaml.atKey("rules"));

        return new Sheet(id, game, roleplay, rules);
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


    // > State
    // ------------------------------------------------------------------------------------------


    public Widget componentWithId(UUID componentId)
    {
        return this.componentById.get(componentId);
    }


    public Roleplay getRoleplay()
    {
        return this.roleplay.getValue();
    }


    public Game getGame()
    {
        return this.game.getValue();
    }


    public Rules getRules()
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
        this.getRoleplay().render(pagePagerAdapter);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Index the widgets by their id, so that can later be retrieved.
     */
    private void indexComponents()
    {
        componentById = new HashMap<>();

        for (Page page : this.roleplay.getValue().getPages()) {
            for (Group group : page.getGroups()) {
                for (Widget widget : group.getWidgets()) {
                    componentById.put(widget.getId(), widget);
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
