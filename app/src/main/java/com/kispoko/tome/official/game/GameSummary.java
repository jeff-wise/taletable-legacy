
package com.kispoko.tome.official.game;


import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.util.UUID;



/**
 * GameSummary
 *
 * // TODO when do these get loaded? how do they get updated? can you create custom games?
 */
public class GameSummary extends Model
                  implements ToYaml
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                        id;

    private PrimitiveFunctor<String>    name;
    private PrimitiveFunctor<String>    label;
    private PrimitiveFunctor<String>    description;
    private PrimitiveFunctor<String>    genre;
    private PrimitiveFunctor<String>    created;
    private PrimitiveFunctor<String>    creators;
    private PrimitiveFunctor<Integer>   players;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public GameSummary()
    {
        this.id          = null;

        this.name        = new PrimitiveFunctor<>(null, String.class);
        this.label       = new PrimitiveFunctor<>(null, String.class);
        this.description = new PrimitiveFunctor<>(null, String.class);
        this.genre       = new PrimitiveFunctor<>(null, String.class);
        this.created     = new PrimitiveFunctor<>(null, String.class);
        this.creators    = new PrimitiveFunctor<>(null, String.class);
        this.players     = new PrimitiveFunctor<>(null, Integer.class);
    }


    public GameSummary(UUID id,
                       String name,
                       String label,
                       String description,
                       String genre,
                       String created,
                       String creators,
                       Integer players)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);
        this.label          = new PrimitiveFunctor<>(label, String.class);
        this.description    = new PrimitiveFunctor<>(description, String.class);
        this.genre          = new PrimitiveFunctor<>(genre, String.class);
        this.created        = new PrimitiveFunctor<>(created, String.class);
        this.creators       = new PrimitiveFunctor<>(creators, String.class);
        this.players        = new PrimitiveFunctor<>(players, Integer.class);
    }


    public static GameSummary fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        // Values to parse
        UUID id            = UUID.randomUUID();
        String name        = yaml.atKey("name").getString();
        String label       = yaml.atKey("label").getString();
        String description = yaml.atKey("description").getString();
        String genre       = yaml.atKey("genre").getString();
        String created     = yaml.atKey("created").getString();
        String creators    = yaml.atKey("creators").getString();
        Integer players    = yaml.atKey("players").getInteger();

        return new GameSummary(id, name, label, description, genre, created, creators, players);
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


    // ** On load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the GameSummary is completely loaded for the first time.
     */
    public void onLoad() { }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The game's Yaml representation.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putString("label", this.label())
                .putString("description", this.description())
                .putString("genre", this.genre())
                .putString("created", this.created())
                .putString("creators", this.creators())
                .putInteger("players", this.players());
    }


    // > State
    // ------------------------------------------------------------------------------------------


    /**
     * The game name (identifier).
     * @return The name
     */
    public String name()
    {
        return this.name.getValue();
    }


    /**
     * The game name (for display).
     * @return The name.
     */
    public String label()
    {
        return this.label.getValue();
    }


    /**
     * The game description.
     * @return The description.
     */
    public String description()
    {
        return this.description.getValue();
    }


    /**
     * The game genre e.g. Fantasy, Horror, etc.
     * @return The genre.
     */
    public String genre()
    {
        return this.genre.getValue();
    }


    /**
     * The games creation date as a string.
     * @return The date created.
     */
    public String created()
    {
        return this.created.getValue();
    }


    /**
     * The game's authors/creators/publisher.
     * @return The creators.
     */
    public String creators()
    {
        return this.creators.getValue();
    }


    public Integer players()
    {
        return this.players.getValue();
    }

}

