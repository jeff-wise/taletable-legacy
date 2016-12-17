
package com.kispoko.tome.engine.programming.mechanic;


import com.kispoko.tome.engine.programming.interpreter.Interpreter;
import com.kispoko.tome.engine.programming.program.Program;
import com.kispoko.tome.engine.programming.program.ProgramIndex;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Mechanic Index
 */
public class MechanicIndex implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private CollectionValue<Mechanic> mechanics;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public MechanicIndex()
    {
        this.id            = null;

        List<Class<? extends Mechanic>> mechanicClasses = new ArrayList<>();
        mechanicClasses.add(Mechanic.class);
        this.mechanics     = CollectionValue.empty(mechanicClasses);
    }


    public MechanicIndex(UUID id, List<Mechanic> mechanics)
    {
        this.id            = id;

        List<Class<? extends Mechanic>> mechanicClasses = new ArrayList<>();
        mechanicClasses.add(Mechanic.class);
        this.mechanics     = CollectionValue.full(mechanics, mechanicClasses);

        this.addMechanicsToState();
    }


    /**
     * Create a Mechanic Index from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Mechanic Index
     * @throws YamlException
     */
    public static MechanicIndex fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID           id        = UUID.randomUUID();

        List<Mechanic> mechanics = yaml.forEach(new Yaml.ForEach<Mechanic>() {
            @Override
            public Mechanic forEach(Yaml yaml, int index) throws YamlException {
                return Mechanic.fromYaml(yaml);
            }
        });

        return new MechanicIndex(id, mechanics);
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
    public void onLoad()
    {
        this.addMechanicsToState();
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the mechanics in the index.
     * @return The list of mechanics.
     */
    private List<Mechanic> mechanics()
    {
        return this.mechanics.getValue();
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void addMechanicsToState()
    {
        for (Mechanic mechanic : this.mechanics())
        {
            mechanic.addToState();
        }

    }

}
