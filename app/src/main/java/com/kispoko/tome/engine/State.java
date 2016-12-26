
package com.kispoko.tome.engine;


import com.kispoko.tome.engine.programming.variable.BooleanVariable;
import com.kispoko.tome.engine.programming.variable.DiceVariable;
import com.kispoko.tome.engine.programming.variable.NumberVariable;
import com.kispoko.tome.engine.programming.variable.TextVariable;
import com.kispoko.tome.engine.programming.variable.Variable;
import com.kispoko.tome.engine.programming.variable.VariableUnion;

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

    private static Map<String,VariableUnion> variableByName      = new HashMap<>();

    private static Map<String,Set<Variable>> variableToListeners = new HashMap<>();


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

        variableByName.put(variableName, variableUnion);

        // [2] Index the variable's dependencies
        // --------------------------------------------------------------------------------------

        for (String dependencyName : variableUnion.variable().dependencies())
        {
            if (!variableToListeners.containsKey(dependencyName))
                variableToListeners.put(dependencyName, new HashSet<Variable>());

            Set<Variable> listeners = variableToListeners.get(dependencyName);
            listeners.add(variableUnion.variable());
        }

        // [3] Notify all current listeners of this variable
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
     * @param name The variable name.
     * @return True if the variable was removed, False if the variable did not exist.
     */
    public static boolean removeVariable(String name)
    {
        if (variableByName.containsKey(name))
        {
            variableByName.remove(name);
            return true;
        }
        else
        {
            return false;
        }
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
        // [1] Call onVariableUpdate on all of that variable's listeners
        // --------------------------------------------------------------------------------------

        if (variableToListeners.containsKey(variable.name()))
        {
            for (Variable listener : variableToListeners.get(variable.name()))
            {
                listener.onUpdate();
            }
        }

    }


}
