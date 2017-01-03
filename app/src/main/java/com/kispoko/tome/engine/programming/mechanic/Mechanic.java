
package com.kispoko.tome.engine.programming.mechanic;


import android.util.Log;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.programming.mechanic.error.NonBooleanRequirementError;
import com.kispoko.tome.engine.variable.VariableType;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
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

    private PrimitiveFunctor<String>         name;
    private PrimitiveFunctor<String>         type;

    /**
     * A requirement is the name of a boolean variable. If all the requiremnet variable values are
     * true, then the mechanic is set to active ie. added to the state.
     */
    private PrimitiveFunctor<String[]>       requirements;

    private CollectionFunctor<VariableUnion> variables;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private boolean                          active;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Mechanic()
    {
        this.id           = null;

        this.name         = new PrimitiveFunctor<>(null, String.class);
        this.type         = new PrimitiveFunctor<>(null, String.class);
        this.requirements = new PrimitiveFunctor<>(null, String[].class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);
        this.variables    = CollectionFunctor.empty(variableClasses);
    }


    public Mechanic(UUID id,
                    String name,
                    String type,
                    List<String> requirements,
                    List<VariableUnion> variables)
    {
        this.id           = id;

        this.name         = new PrimitiveFunctor<>(name, String.class);
        this.type         = new PrimitiveFunctor<>(type, String.class);

        String[] requirementsArray = new String[requirements.size()];
        requirements.toArray(requirementsArray);
        this.requirements = new PrimitiveFunctor<>(requirementsArray, String[].class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);
        this.variables    = CollectionFunctor.full(variables, variableClasses);
    }


    /**
     * Create a Mechanic from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Mechanic.
     */
    public static Mechanic fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID                id           = UUID.randomUUID();

        String              name         = yaml.atKey("name").getString();
        String              type         = yaml.atMaybeKey("type").getString();
        List<String>        requirements = yaml.atMaybeKey("requirements").getStringList();

        List<VariableUnion> variables = yaml.atKey("variables").forEach(
                                                        new Yaml.ForEach<VariableUnion>()
        {
            @Override
            public VariableUnion forEach(Yaml yaml, int index) throws YamlException {
                return VariableUnion.fromYaml(yaml);
            }
        });

        return new Mechanic(id, name, type, requirements, variables);
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
        this.initialize();
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Name
    // ------------------------------------------------------------------------------------------

    /**
     * Get the mechanic's name.
     * @return The mechanic name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    // ** Requirements
    // ------------------------------------------------------------------------------------------

    /**
     * Get the mechanic's requirements. Each requirement is the name of a boolean variable.
     * @return The requiement array.
     */
    public String[] requirements()
    {
        return this.requirements.getValue();
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


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the mechanic.
     */
    private void initialize()
    {
        this.active = false;

        validateRequirements();

        onRequirementUpdate();
    }


    /**
     * Called when there is an update to one of the mechanic's requirement variables. Checks to
     * see if the active status of the mechanic has changed.
     */
    public void onRequirementUpdate()
    {
        Log.d("***MECHANIC", "on req update " + this.name());
        boolean isActive = true;

        for (String requirement : this.requirements())
        {
            Log.d("***MECHANIC", "requirement:" + requirement);
            if (State.hasVariable(requirement))
            {
                VariableUnion variableUnion = State.variableWithName(requirement);

                if (variableUnion.type() != VariableType.BOOLEAN) {
                    ApplicationFailure.mechanic(
                            MechanicException.nonBooleanRequirement(
                                    new NonBooleanRequirementError(this.name(), requirement)));
                    // TODO add to user programming errors
                    continue;
                }

                if (!variableUnion.booleanVariable().value()) {
                    Log.d("***MECHANIC", "is active value is false:");
                    isActive = false;
                    break;
                }
            }
            else
            {
                Log.d("***MECHANIC", "is active false because val doesn't exist in state");
                isActive = false;
                break;
            }

            Log.d("***MECHANIC", "is active:" + Boolean.toString(isActive));
        }

        // If was active and is now inactive
        if (this.active && !isActive) {
            this.removeFromState();
        }
        // If was not active and is now active
        else if (!this.active && isActive) {
            this.addToState();
        }
    }


    /**
     * Add every variable in the mechanic to the state (if the mechanic is active).
     */
    private void addToState()
    {
        this.active = true;

        for (VariableUnion variableUnion : this.variables()) {
            State.addVariable(variableUnion);
        }
    }


    /**
     * Remove the mechanic from the state. Remove each mechanic variable from the state.
     */
    private void removeFromState()
    {
        this.active = false;

        Log.d("***MECHANIC", "remove from state " + this.name());

        for (VariableUnion variableUnion : this.variables()) {
            State.removeVariable(variableUnion.variable().name());
        }
    }


    /**
     * Check the requirement variables to make sure they are valid.
     */
    private void validateRequirements()
    {
        // TODO need way to analyze variable before added to state.
    }

}
