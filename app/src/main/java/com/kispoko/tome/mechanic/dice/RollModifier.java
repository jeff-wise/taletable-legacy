
package com.kispoko.tome.mechanic.dice;


import com.kispoko.tome.R;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Roll Modifier
 */
public class RollModifier extends Model
                          implements ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<Integer>   value;
    private PrimitiveFunctor<String>    name;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public RollModifier()
    {
        this.id     = null;

        this.value  = new PrimitiveFunctor<>(null, Integer.class);
        this.name   = new PrimitiveFunctor<>(null, String.class);

        this.initializeFunctors();
    }


    public RollModifier(UUID id, Integer value, String name)
    {
        this.id     = id;

        this.value  = new PrimitiveFunctor<>(value, Integer.class);
        this.name   = new PrimitiveFunctor<>(name, String.class);

        this.initializeFunctors();
    }


    /**
     * Non-persitent constructor.
     * @param value
     */
    public RollModifier(Integer value, String name)
    {
        this.id     = null;

        this.value  = new PrimitiveFunctor<>(value, Integer.class);
        this.name   = new PrimitiveFunctor<>(name, String.class);
    }


    /**
     * Create a Roll Modifier from its yaml representation.
     * @param yaml
     * @return
     * @throws YamlParseException
     */
    public static RollModifier fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID    id    = UUID.randomUUID();

        Integer value = yaml.atKey("value").getInteger();
        String  name  = yaml.atMaybeKey("name").getString();

        return new RollModifier(id, value, name);
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     * @param id The new model UUID.
     */
    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the RulesEngine is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Dice Roll's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putInteger("value", this.value())
                .putString("name", this.name());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The modifier value.
     * @return The modifier integer value.
     */
    public int value()
    {
        return this.value.getValue();
    }


    /**
     * The name of the modifier.
     * @return The modifier name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    // > Value Plus String
    // ------------------------------------------------------------------------------------------

    public String valuePlusString()
    {
        return "+" + Integer.toString(this.value());
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Initialize
    // -----------------------------------------------------------------------------------------

    private void initializeFunctors()
    {
        // Value
        this.value.setName("value");
        this.value.setLabelId(R.string.roll_modifier_field_value_label);
        this.value.setDescriptionId(R.string.roll_modifier_field_value_description);

        // Name
        this.name.setName("name");
        this.name.setLabelId(R.string.roll_modifier_field_name_label);
        this.name.setDescriptionId(R.string.roll_modifier_field_name_description);
    }

}
