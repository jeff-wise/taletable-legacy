
package com.kispoko.tome.engine.mechanic;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.functor.CollectionFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;



/**
 * Mechanic Index
 */
public class MechanicIndex implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private CollectionFunctor<Mechanic> mechanics;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Map<String,Set<Mechanic>>   requirementToListeners;

    private Map<String,Mechanic>        mechanicsByName;
    private Map<String,Set<Mechanic>>   mechanicsByCategory;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public MechanicIndex()
    {
        this.id            = null;

        this.mechanics     = CollectionFunctor.empty(Mechanic.class);
    }


    public MechanicIndex(UUID id, List<Mechanic> mechanics)
    {
        this.id            = id;

        this.mechanics     = CollectionFunctor.full(mechanics, Mechanic.class);

        this.initialize();
    }


    /**
     * Create a Mechanic Index from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Mechanic Index
     * @throws YamlParseException
     */
    public static MechanicIndex fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID           id        = UUID.randomUUID();

        List<Mechanic> mechanics = yaml.forEach(new YamlParser.ForEach<Mechanic>() {
            @Override
            public Mechanic forEach(YamlParser yaml, int index) throws YamlParseException {
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
        this.initialize();
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Mechanic Index's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.list(this.mechanics());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the mechanics in the index.
     * @return The list of mechanics.
     */
    public List<Mechanic> mechanics()
    {
        return this.mechanics.getValue();
    }


    public void onVariableUpdate(String variableName)
    {
        // [1] Update any mechanics that require this variable
        // --------------------------------------------------------------------------------------

        if (this.requirementToListeners.containsKey(variableName))
        {
            for (Mechanic mechanic : this.requirementToListeners.get(variableName)) {
                mechanic.onRequirementUpdate();
            }
        }
    }


    public Mechanic mechanicWithName(String mechanicName)
    {
        return this.mechanicsByName.get(mechanicName);
    }


    /**
     * Get all of the mechanics in a category.
     * @param categoryName The category name.
     * @param active If true, only returns mechanics that are currently active.
     * @return The mechanic list.
     */
    public Set<Mechanic> mechanicsInCategory(String categoryName, Boolean active)
    {
        Set<Mechanic> mechanics = this.mechanicsByCategory.get(categoryName);

        if (mechanics == null)
            return new HashSet<>();

        if (!active)
            return mechanics;

        Set<Mechanic> activeMechanics = new HashSet<>();

        for (Mechanic mechanic : mechanics) {
            if (mechanic.active())
                activeMechanics.add(mechanic);
        }

        return activeMechanics;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initialize()
    {
        // Index mechanic requirements
        // --------------------------------------------------------------------------------------

        this.requirementToListeners = new HashMap<>();

        for (Mechanic mechanic : this.mechanics())
        {
            for (String requirement : mechanic.requirements())
            {
                if (!this.requirementToListeners.containsKey(requirement))
                    this.requirementToListeners.put(requirement, new HashSet<Mechanic>());

                Set<Mechanic> listeners = this.requirementToListeners.get(requirement);
                listeners.add(mechanic);
            }
        }

        // Index mechanic names
        // --------------------------------------------------------------------------------------

        this.mechanicsByName = new HashMap<>();
        for (Mechanic mechanic : this.mechanics()) {
            this.mechanicsByName.put(mechanic.name(), mechanic);
        }

        // Index mechanic categories
        // --------------------------------------------------------------------------------------

        this.mechanicsByCategory = new HashMap<>();
        for (Mechanic mechanic : this.mechanics())
        {
            if (!this.mechanicsByCategory.containsKey(mechanic.type()))
                this.mechanicsByCategory.put(mechanic.type(), new HashSet<Mechanic>());

            Set<Mechanic> mechanics = this.mechanicsByCategory.get(mechanic.type());
            mechanics.add(mechanic);
        }

    }

}
