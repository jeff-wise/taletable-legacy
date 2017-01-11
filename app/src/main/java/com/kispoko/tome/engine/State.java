
package com.kispoko.tome.engine;


import com.kispoko.tome.engine.programming.mechanic.MechanicIndex;
import com.kispoko.tome.engine.variable.BooleanVariable;
import com.kispoko.tome.engine.variable.DiceVariable;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.engine.variable.VariableReference;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.sheet.SheetManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



/**
 * State
 *
 * Manages all of the variables.
 */
public class State
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private static Map<String,VariableUnion>      variableByName          = new HashMap<>();

    private static Map<String,Set<Variable>>      variableNameToListeners = new HashMap<>();
    private static Map<String,Set<Variable>>      variableTagToListeners  = new HashMap<>();

    private static Map<String,Set<VariableUnion>> tagIndex                = new HashMap<>();

    private static boolean                        mechanicIndexReady      = false;


    // API
    // ------------------------------------------------------------------------------------------

    /**
     * Add a variable to the index. If a variable with the same name already exists in the index,
     * then the new variable replaces the old variable.
     * @param variableUnion The variable union to add.
     */
    public static void addVariable(VariableUnion variableUnion)
    {
        String variableName = variableUnion.variable().name();

        // [1] Add variable to index.
        // --------------------------------------------------------------------------------------

        if (variableName != null)
            variableByName.put(variableName, variableUnion);

        // [2] Index the variable's dependencies
        // --------------------------------------------------------------------------------------

        for (VariableReference variableReference : variableUnion.variable().dependencies())
        {
            switch (variableReference.type())
            {
                // > Index variable names to listeners
                case NAME:
                    String name = variableReference.name();
                    if (!variableNameToListeners.containsKey(name))
                        variableNameToListeners.put(name, new HashSet<Variable>());
                    Set<Variable> nameListeners = variableNameToListeners.get(name);
                    nameListeners.add(variableUnion.variable());
                    break;
                case TAG:
                    String tag = variableReference.tag();
                    if (!variableTagToListeners.containsKey(tag))
                        variableTagToListeners.put(tag, new HashSet<Variable>());
                    Set<Variable> tagListeners = variableTagToListeners.get(tag);
                    tagListeners.add(variableUnion.variable());
                    break;
            }
        }

        // [3] Index the variable's tags
        // --------------------------------------------------------------------------------------

        for (String tag : variableUnion.variable().tags())
        {
            if (!tagIndex.containsKey(tag))
                tagIndex.put(tag, new HashSet<VariableUnion>());

            Set<VariableUnion> variablesWithTag = tagIndex.get(tag);
            variablesWithTag.add(variableUnion);
        }

        // [4] Notify all current listeners of this variable
        // --------------------------------------------------------------------------------------

        updateVariableDependencies(variableUnion.variable());
    }


    public static void addVariable(Variable variable)
    {
        if (variable instanceof TextVariable) {
            addVariable(VariableUnion.asText((TextVariable) variable));
        }
        else if (variable instanceof NumberVariable) {
            addVariable(VariableUnion.asNumber((NumberVariable) variable));
        }
        else if (variable instanceof BooleanVariable) {
            addVariable(VariableUnion.asBoolean((BooleanVariable) variable));
        }
        else if (variable instanceof DiceVariable) {
            addVariable(VariableUnion.asDice((DiceVariable) variable));
        }
    }


    /**
     * Get the variable from the state that has the given name.
     * @param name The variable name.
     * @return The variable union with the given name.
     */
    public static VariableUnion variableWithName(String name)
    {
        return variableByName.get(name);
    }


    /**
     * Remove the variable with the given name from the state. Returns true if the variable was
     * removed, and false if the variable did not exist in the state.
     * @param variableName The variable name.
     * @return True if the variable was removed, False if the variable did not exist.
     */
    public static boolean removeVariable(String variableName)
    {
        if (!variableByName.containsKey(variableName))
            return false;

        // [1] Remove the variable from the index
        // --------------------------------------------------------------------------------------

        VariableUnion variableUnion = variableByName.get(variableName);
        variableByName.remove(variableName);


        // [2] Notify all current listeners of this variable
        // --------------------------------------------------------------------------------------

        updateVariableDependencies(variableUnion.variable());

        // [3] Un-Index the variable's dependencies
        // --------------------------------------------------------------------------------------

        for (VariableReference variableReference : variableUnion.variable().dependencies())
        {
            switch (variableReference.type())
            {
                // > Index variable names to listeners
                case NAME:
                    String name = variableReference.name();
                    Set<Variable> nameListeners = variableNameToListeners.get(name);
                    nameListeners.remove(variableUnion.variable());

                    if (nameListeners.isEmpty())
                        variableNameToListeners.remove(name);
                    break;
                case TAG:
                    String tag = variableReference.tag();
                    Set<Variable> tagListeners = variableTagToListeners.get(tag);
                    tagListeners.remove(variableUnion.variable());

                    if (tagListeners.isEmpty())
                        variableTagToListeners.remove(tag);
                    break;
            }
        }

        // [4] Un-Index the variable's tags
        // --------------------------------------------------------------------------------------

        for (String tag : variableUnion.variable().tags())
        {
            Set<VariableUnion> variablesWithTag = tagIndex.get(tag);
            variablesWithTag.remove(variableUnion);

            if (variablesWithTag.isEmpty())
                tagIndex.remove(tag);
        }

        return true;
    }


    /**
     * Returns true if the state contains the variable with the given name.
     * @param variableName The variable name.
     * @return True if the state contains the variable, False otherwise.
     */
    public static boolean hasVariable(String variableName)
    {
        return variableByName.containsKey(variableName);
    }


    public static void updateVariableDependencies(Variable variable)
    {
        // [1] Call onVariableUpdate on all variables listening for that variable name.
        // --------------------------------------------------------------------------------------

        if (variableNameToListeners.containsKey(variable.name()))
        {
            for (Variable listener : variableNameToListeners.get(variable.name())) {
                listener.onUpdate();
            }
        }

        // [2] Call onVariableUpdate on all variables listening for any of the variable's tags
        // --------------------------------------------------------------------------------------

        for (String tag : variable.tags())
        {
            if (variableTagToListeners.containsKey(tag))
            {
                for (Variable listener : variableTagToListeners.get(tag)) {
                    listener.onUpdate();
                }
            }
        }

        // [3] Update the mechanics
        // --------------------------------------------------------------------------------------

        updateMechanics(variable.name());
    }


    public static Set<VariableUnion> variablesWithTag(String tag)
    {
        if (tagIndex.containsKey(tag))
            return tagIndex.get(tag);
        else
            return new HashSet<>();
    }


    public static void initializeMechanics()
    {
        mechanicIndexReady = true;

        MechanicIndex mechanicIndex = SheetManager.currentSheet().engine().mechanicIndex();
        for (VariableUnion variableUnion : variableByName.values()) {
            mechanicIndex.onVariableUpdate(variableUnion.variable().name());
        }
    }


    public static void updateMechanics(String variableName)
    {
        MechanicIndex mechanicIndex = SheetManager.currentSheet().engine().mechanicIndex();
        mechanicIndex.onVariableUpdate(variableName);
    }

}
