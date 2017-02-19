
package com.kispoko.tome.engine.variable;


import com.kispoko.tome.engine.State;
import com.kispoko.tome.mechanic.dice.DiceRoll;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Dice Variable
 */
public class DiceVariable extends Variable
                          implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>    name;
    private PrimitiveFunctor<String>    label;

    private ModelFunctor<DiceRoll>      diceRoll;

    private PrimitiveFunctor<Boolean>   isNamespaced;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public DiceVariable()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.label          = new PrimitiveFunctor<>(null, String.class);

        this.diceRoll       = ModelFunctor.empty(DiceRoll.class);

        this.isNamespaced   = new PrimitiveFunctor<>(null, Boolean.class);
    }


    public DiceVariable(UUID id, String name, String label, DiceRoll diceRoll, Boolean isNamespaced)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);
        this.label          = new PrimitiveFunctor<>(label, String.class);

        this.diceRoll       = ModelFunctor.full(diceRoll, DiceRoll.class);

        if (isNamespaced == null) isNamespaced = false;
        this.isNamespaced   = new PrimitiveFunctor<>(isNamespaced, Boolean.class);
    }


    /**
     * Create a Dice Variable from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Dice Variable.
     * @throws YamlParseException
     */
    public static DiceVariable fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return null;

        UUID     id             = UUID.randomUUID();

        String   name           = yaml.atMaybeKey("name").getString();
        String   label          = yaml.atMaybeKey("label").getString();

        DiceRoll diceRoll       = DiceRoll.fromYaml(yaml.atKey("dice"));
        Boolean  isNamespaced   = yaml.atMaybeKey("namespaced").getBoolean();

        return new DiceVariable(id, name, label, diceRoll, isNamespaced);
    }


    // API
    // ------------------------------------------------------------------------------------------

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


    // > Variable
    // ------------------------------------------------------------------------------------------

    /**
     * Get the variable name which is a unique identifier.
     * @return The variable name.
     */
    @Override
    public String name()
    {
        return this.name.getValue();
    }


    @Override
    public void setName(String name)
    {
        this.name.setValue(name);
    }


    @Override
    public String label()
    {
        return this.label.getValue();
    }


    @Override
    public void setLabel(String label)
    {
        this.label.setValue(label);
    }


    @Override
    public boolean isNamespaced()
    {
        return this.isNamespaced.getValue();
    }


    @Override
    public void setIsNamespaced(Boolean isNamespaced)
    {
        if (isNamespaced != null)
            this.isNamespaced.setValue(isNamespaced);
        else
            this.isNamespaced.setValue(false);
    }


    public List<VariableReference> dependencies()
    {
        return new ArrayList<>();
    }


    @Override
    public List<String> tags()
    {
        return new ArrayList<>();
    }


    @Override
    public String valueString()
    {
        return this.diceRoll().toString();
    }


    // > Initialize
    // ------------------------------------------------------------------------------------------

    public void initialize()
    {

    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Dice Variable's yaml representation.
     * @return
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putString("label", this.label())
                .putYaml("dice", this.diceRoll())
                .putBoolean("namespaced", this.isNamespaced());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Dice Roll
    // ------------------------------------------------------------------------------------------

    /**
     * Get the dice roll.
     * @return The dice roll.
     */
    public DiceRoll diceRoll()
    {
        return this.diceRoll.getValue();
    }


    public Integer rollValue()
    {
        return this.diceRoll().roll();

    }

}

