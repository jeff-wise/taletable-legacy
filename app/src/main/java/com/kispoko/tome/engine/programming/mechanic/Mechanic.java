
package com.kispoko.tome.engine.programming.mechanic;


import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.programming.variable.VariableUnion;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Mechanic
 */
public class Mechanic implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveValue<String>         type;
    private PrimitiveValue<Boolean>        active;
    private CollectionValue<VariableUnion> variables;

    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Mechanic()
    {
        this.id = null;

        this.type   = new PrimitiveValue<>(null, String.class);
        this.active = new PrimitiveValue<>(null, Boolean.class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);
        this.variables = CollectionValue.empty(variableClasses);
    }


    public Mechanic(UUID id, String type, Boolean active, List<VariableUnion> variables)
    {
        this.id = id;

        this.type   = new PrimitiveValue<>(type, String.class);
        this.active = new PrimitiveValue<>(active, Boolean.class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);
        this.variables = CollectionValue.full(variables, variableClasses);
    }


    /**
     * Create a Mechanic from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Mechanic.
     */
    public static Mechanic fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID                id        = UUID.randomUUID();

        String              type      = yaml.atMaybeKey("type").getString();
        Boolean             active    = yaml.atKey("active").getBoolean();

        List<VariableUnion> variables = yaml.atKey("variables").forEach(
                                                        new Yaml.ForEach<VariableUnion>()
        {
            @Override
            public VariableUnion forEach(Yaml yaml, int index) throws YamlException {
                return VariableUnion.fromYaml(yaml);
            }
        });

        return new Mechanic(id, type, active, variables);
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


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Active
    // ------------------------------------------------------------------------------------------

    /**
     * Returns true if the mechanic is active.
     * @return The mechanic's active state.
     */
    public boolean active()
    {
        return this.active.getValue();
    }


    // ** Variables
    // ------------------------------------------------------------------------------------------

    /**
     * Get the mechanic's variables.
     * @return The Variable list.
     */
    private List<VariableUnion> variables()
    {
        return this.variables.getValue();
    }


    // > Variables
    // ------------------------------------------------------------------------------------------

    /**
     * Add every variable in the mechanic to the state (if the mechanic is active).
     */
    public void addToState()
    {
        if (!this.active())
            return;

        for (VariableUnion variableUnion : this.variables()) {
            State.addVariable(variableUnion);
        }
    }

}
