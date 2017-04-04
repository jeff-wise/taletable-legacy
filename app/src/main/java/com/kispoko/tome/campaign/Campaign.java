
package com.kispoko.tome.campaign;


import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.util.UUID;



/**
 * Campaign
 */
public class Campaign extends Model
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>        name;
    private PrimitiveFunctor<String>        label;
    private PrimitiveFunctor<String>        gameName;
    private PrimitiveFunctor<String>        dungeonMaster;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public Campaign()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.label          = new PrimitiveFunctor<>(null, String.class);
        this.gameName       = new PrimitiveFunctor<>(null, String.class);
        this.dungeonMaster  = new PrimitiveFunctor<>(null, String.class);
    }


    public Campaign(UUID id, String name, String label, String gameName, String dungeonMaster)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);
        this.label          = new PrimitiveFunctor<>(label, String.class);
        this.gameName       = new PrimitiveFunctor<>(gameName, String.class);
        this.dungeonMaster  = new PrimitiveFunctor<>(dungeonMaster, String.class);
    }


    /**
     * Create a campaign from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Campaign.
     * @throws YamlParseException
     */
    public static Campaign fromYaml(YamlParser yaml)
            throws YamlParseException
    {
        UUID   id            = UUID.randomUUID();

        String name          = yaml.atKey("name").getString();
        String label         = yaml.atKey("label").getString();
        String gameName      = yaml.atKey("game").getString();
        String dungeonMaster = yaml.atKey("dungeon_master").getString();

        return new Campaign(id, name, label, gameName, dungeonMaster);
    }


    // API
    // -----------------------------------------------------------------------------------------

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
    public void onLoad() { }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The campaign name.
     * @return The campaign name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    /**
     * The campaign label (the name for human consumption).
     * @return The label.
     */
    public String label()
    {
        return this.label.getValue();
    }


    /**
     * The name (identifier) of the game the campaign uses.
     * @return The game name.
     */
    public String gameName()
    {
        return this.gameName.getValue();
    }


    /**
     * The name of the dungeon master for the campaign.
     * @return The dungeon master.
     */
    public String dungeonMaster()
    {
        return this.dungeonMaster.getValue();
    }


}
