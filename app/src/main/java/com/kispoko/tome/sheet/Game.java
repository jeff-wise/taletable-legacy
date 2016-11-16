
package com.kispoko.tome.sheet;


import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.util.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.value.Value;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.Map;
import java.util.UUID;



/**
 * Game
 *
 * // TODO when do these get loaded? how do they get updated? can you create custom games?
 */
public class Game extends Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Value<String> name;
    private Value<String> label;
    private Value<String> description;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Game(UUID id, String name, String label, String description)
    {
        super(id);

        this.name        = new PrimitiveValue<>(name, this, String.class);
        this.label       = new PrimitiveValue<>(label, this, String.class);
        this.description = new PrimitiveValue<>(description, this, String.class);
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


    // > Model
    // ------------------------------------------------------------------------------------------

    public void onUpdateModel(String field) { }
}

