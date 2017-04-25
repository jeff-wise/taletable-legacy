
package com.kispoko.tome.engine.mechanic;


import com.kispoko.tome.engine.search.EngineActiveSearchResult;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;



/**
 * Mechanic Index
 */
public class MechanicIndex extends Model
                           implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private CollectionFunctor<Mechanic> mechanics;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Map<String,Set<Mechanic>>   requirementToListeners;

    private Map<String,Mechanic>        mechanicsByName;
    private Map<String,Set<Mechanic>>   mechanicsByCategory;

    // **  Search Indexes
    // ------------------------------------------------------------------------------------------

    private PatriciaTrie<Mechanic> activeMechanicNameTrie;
    private PatriciaTrie<Mechanic> activeMechanicLabelTrie;


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


    // > Search
    // ------------------------------------------------------------------------------------------

    public Collection<EngineActiveSearchResult> search(String query)
    {

        Set<Mechanic> matches = new HashSet<>();
        matches.addAll(this.activeMechanicNameTrie.prefixMap(query).values());
        matches.addAll(this.activeMechanicLabelTrie.prefixMap(query).values());

        Map<String,ActiveMechanicSearchResult> resultsByMechanicName = new HashMap<>();

        for (Mechanic mechanic : matches)
        {
            String mechanicName  = mechanic.name();
            String mechanicLabel = mechanic.label();

            if (resultsByMechanicName.containsKey(mechanicName)) {
                ActiveMechanicSearchResult result = resultsByMechanicName.get(mechanicName);
                result.addToRanking(1f);
            }
            else
            {
                ActiveMechanicSearchResult result =
                                new ActiveMechanicSearchResult(mechanicName, mechanicLabel, 1f);
                resultsByMechanicName.put(mechanicName, result);
            }
        }

        Collection<EngineActiveSearchResult> results = new ArrayList<>();
        for (ActiveMechanicSearchResult mechanicSearchResult : resultsByMechanicName.values()) {
            results.add(new EngineActiveSearchResult(mechanicSearchResult));
        }

        return results;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initialize()
    {
        // Initialize search indexes
        // --------------------------------------------------------------------------------------

        this.activeMechanicNameTrie  = new PatriciaTrie<>();
        this.activeMechanicLabelTrie = new PatriciaTrie<>();

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
