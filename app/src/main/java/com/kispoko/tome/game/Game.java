
package com.kispoko.tome.game;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;



/**
 * Game
 *
 * // TODO when do these get loaded? how do they get updated? can you create custom games?
 */
public class Game implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                   id;

    private PrimitiveValue<String> name;
    private PrimitiveValue<String> label;
    private PrimitiveValue<String> description;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Game()
    {
        this.id          = null;

        this.name        = new PrimitiveValue<>(null, String.class);
        this.label       = new PrimitiveValue<>(null, String.class);
        this.description = new PrimitiveValue<>(null, String.class);
    }


    public Game(UUID id, String name, String label, String description)
    {
        this.id = id;

        this.name        = new PrimitiveValue<>(name, String.class);
        this.label       = new PrimitiveValue<>(label, String.class);
        this.description = new PrimitiveValue<>(description, String.class);
    }


    public static Game fromYaml(Yaml yaml)
                  throws YamlException
    {
        // Values to parse
        UUID id            = UUID.randomUUID();
        String name        = yaml.atKey("name").getString();
        String label       = yaml.atKey("label").getString();
        String description = yaml.atKey("description").getString();

        return new Game(id, name, label, description);
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
     * This method is called when the Game is completely loaded for the first time.
     */
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Name
    // ------------------------------------------------------------------------------------------

    public String getName()
    {
        return this.name.getValue();
    }


    // ** Label
    // ------------------------------------------------------------------------------------------

    public String getLabel() {
        return this.label.getValue();
    }


    // ** Description
    // ------------------------------------------------------------------------------------------

    public String getDescription() {
        return this.description.getValue();
    }

}

