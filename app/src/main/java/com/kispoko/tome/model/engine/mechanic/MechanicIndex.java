
package com.kispoko.tome.model.engine.mechanic;


import android.text.TextUtils;

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
import java.util.Collections;
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

    private PatriciaTrie<Mechanic>      activeMechanicNameTrie;
    private PatriciaTrie<Mechanic>      activeMechanicLabelTrie;
    private PatriciaTrie<Set<Mechanic>> activeMechanicVariablesTrie;


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


    // > Mechanics
    // -----------------------------------------------------------------------------------------

    /**
     * Get the mechanics in the index.
     * @return The list of mechanics.
     */
    public List<Mechanic> mechanics()
    {
        return Collections.unmodifiableList(this.mechanics.getValue());
    }


    private List<Mechanic> mechanicsMutable()
    {
        return this.mechanics.getValue();
    }


    /**
     * Return a list of the mechanics in the category separated by strings of their category. This
     * method is used for the Recycler View. For example, this method could return:
     *
     * "Weapons"
     * Mechanic: Sword
     * Mechanic: Spear
     * Mechanic: Bow
     * "Spells"
     * Mechanic: Fireball
     * Mechanic: Magic Missle
     * @return The list of mechanics and their categories
     */
    public List<Object> mechanicByCategoryList()
    {
        List<Object> items = new ArrayList<>();

        for (Map.Entry<String,Set<Mechanic>> entry : this.mechanicsByCategory.entrySet())
        {
            String        category    = entry.getKey();
            Set<Mechanic> mechanicSet = entry.getValue();

            items.add(category);

            for (Mechanic mechanic : mechanicSet) {
                items.add(mechanic);
            }
        }

        return items;
    }


    // > Updates
    // -----------------------------------------------------------------------------------------

    public void onVariableUpdate(String variableName)
    {
        // [1] Update any mechanics that require this variable
        // -------------------------------------------------------------------------------------

        if (this.requirementToListeners.containsKey(variableName))
        {
            for (Mechanic mechanic : this.requirementToListeners.get(variableName))
            {
                Mechanic.UpdateStatus updateStatus = mechanic.onRequirementUpdate();

                if (updateStatus == Mechanic.UpdateStatus.ADDED_TO_STATE)
                    this.indexActiveMechanic(mechanic);
//                else if (updateStatus == Mechanic.UpdateStatus.REMOVED_FROM_STATE)
//                    this.unindexActiveMechanic(mechanic);
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


    // > Active Mechanics
    // ------------------------------------------------------------------------------------------

    public boolean mechanicIsActive(String mechanicName)
    {
        Mechanic mechanic = this.mechanicWithName(mechanicName);

        if (mechanic != null && mechanic.active())
            return true;

        return false;
    }


    // > Search
    // ------------------------------------------------------------------------------------------

    public Collection<EngineActiveSearchResult> search(String query)
    {
        Map<String,ActiveMechanicSearchResult> resultsByMechanicName = new HashMap<>();

        // > Name
        // -------------------------------------------------------------------------------------

        Collection<Mechanic> nameMatches = this.activeMechanicNameTrie.prefixMap(query).values();
        for (Mechanic mechanic : nameMatches)
        {
            String mechanicName  = mechanic.name();

            ActiveMechanicSearchResult result;
            if (resultsByMechanicName.containsKey(mechanicName))
            {
                result = resultsByMechanicName.get(mechanicName);
                result.addToRanking(1f);
            }
            else
            {
                // GET mechanic result data
                String mechanicLabel = mechanic.label();
                String mechanicVariables = TextUtils.join(", ", mechanic.variableNames());

                // CREATE mechanic search result
                result = new ActiveMechanicSearchResult(mechanicName,
                                                        mechanicLabel,
                                                        mechanicVariables);
                resultsByMechanicName.put(mechanicName, result);
            }

            result.setNameIsMatch();
        }

        // > Label
        // -------------------------------------------------------------------------------------

        Collection<Mechanic> labelMatches = this.activeMechanicLabelTrie.prefixMap(query).values();
        for (Mechanic mechanic : labelMatches)
        {
            String mechanicName  = mechanic.name();

            ActiveMechanicSearchResult result;
            if (resultsByMechanicName.containsKey(mechanicName))
            {
                result = resultsByMechanicName.get(mechanicName);
                result.addToRanking(1f);
            }
            else
            {
                // GET mechanic result data
                String mechanicLabel = mechanic.label();
                String mechanicVariables = TextUtils.join(", ", mechanic.variableNames());

                // CREATE mechanic search result
                result = new ActiveMechanicSearchResult(mechanicName,
                                                        mechanicLabel,
                                                        mechanicVariables);
                resultsByMechanicName.put(mechanicName, result);
            }

            result.setLabelIsMatch();
        }

        // > By Variable
        Set<Mechanic> variableMatches = new HashSet<>();
        Collection<Set<Mechanic>> matchCollection =
                                    this.activeMechanicVariablesTrie.prefixMap(query).values();
        for (Set<Mechanic> mechanicSet : matchCollection) {
            variableMatches.addAll(mechanicSet);
        }

        for (Mechanic mechanic : variableMatches)
        {
            String mechanicName  = mechanic.name();

            ActiveMechanicSearchResult result;
            if (resultsByMechanicName.containsKey(mechanicName))
            {
                result = resultsByMechanicName.get(mechanicName);
                result.addToRanking(1f);
            }
            else
            {
                // GET mechanic result data
                String mechanicLabel = mechanic.label();
                String mechanicVariables = TextUtils.join(", ", mechanic.variableNames());

                // CREATE mechanic search result
                result = new ActiveMechanicSearchResult(mechanicName,
                                                        mechanicLabel,
                                                        mechanicVariables);
                resultsByMechanicName.put(mechanicName, result);
            }

            result.setVariablesIsMatch();
        }


        Collection<EngineActiveSearchResult> results = new ArrayList<>();
        for (ActiveMechanicSearchResult mechanicSearchResult : resultsByMechanicName.values()) {
            results.add(new EngineActiveSearchResult(mechanicSearchResult));
        }

        return results;
    }


    public void indexActiveMechanic(Mechanic mechanic)
    {
        this.activeMechanicNameTrie.put(mechanic.name(), mechanic);

        this.activeMechanicLabelTrie.put(mechanic.label(), mechanic);

        for (String variableName : mechanic.variableNames())
        {
            if (!this.activeMechanicVariablesTrie.containsKey(variableName))
                this.activeMechanicVariablesTrie.put(variableName, new HashSet<Mechanic>());

            Set<Mechanic> mechanicsWithVariable =
                                                this.activeMechanicVariablesTrie.get(variableName);
            mechanicsWithVariable.add(mechanic);
        }
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initialize()
    {
        // Initialize search indexes
        // --------------------------------------------------------------------------------------

        this.activeMechanicNameTrie      = new PatriciaTrie<>();
        this.activeMechanicLabelTrie     = new PatriciaTrie<>();
        this.activeMechanicVariablesTrie = new PatriciaTrie<>();

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
