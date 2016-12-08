
package com.kispoko.tome.engine;


import com.kispoko.tome.engine.programming.variable.VariableUnion;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * State
 *
 * Manages all of the variables.
 */
public class State
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private static Map<String,VariableUnion> variableByName = new HashMap<>();


    // API
    // ------------------------------------------------------------------------------------------

    /**
     * Add a variable to the index. If a variable with the same name already exists in the index,
     * then the new variable replaces the old variable.
     * @param variableUnion The variable union to add.
     */
    public static void addVariable(VariableUnion variableUnion)
    {
        Collection<VariableUnion> otherVariableUnions = variableByName.values();

        variableByName.put(variableUnion.variable().getName(), variableUnion);

        sendNewVariableNotification(otherVariableUnions, variableUnion);
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
     * Get all of the variables in the state.
     * @return A list of variables.
     */
    public static Collection<VariableUnion> variables()
    {
        return variableByName.values();
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


    // INTERNAL
    // ------------------------------------------------------------------------------------------

   private static void sendNewVariableNotification(Collection<VariableUnion> receivers,
                                                    VariableUnion message)
    {
        for (VariableUnion variableUnion : receivers)
        {
            variableUnion.variable().onNewVariable(message.variable());
        }
    }


}
