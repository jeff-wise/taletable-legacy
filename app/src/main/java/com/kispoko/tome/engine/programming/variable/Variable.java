
package com.kispoko.tome.engine.programming.variable;


import android.util.Log;

import com.kispoko.tome.engine.State;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;



/**
 * Variable Interface
 */
public abstract class Variable
{

    // ABSTRACT METHODS
    // ------------------------------------------------------------------------------------------

    public abstract String getName();


    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // Variables that this variable's value is dependent on. This variable is a listener to each
    // of these variables
    private Set<String>           variableDependencies;

    // Variables which are listening for changes on this variable
    private Set<String>           variableListeners;

    // Listeners that are not variables, but which want to be notified when this variable changes.
    private Set<OnUpdateListener> onUpdateListeners;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Variable()
    {
        this.variableDependencies = new HashSet<>();
        this.variableListeners    = new HashSet<>();
        this.onUpdateListeners    = new HashSet<>();
    }


    // API
    // ------------------------------------------------------------------------------------------

    protected void setVariableDependencies(Collection<String> variableDependencies)
    {
        // [1] Remove this variable from all current dependencies
        // --------------------------------------------------------------------------------------

        for (String variableName : this.variableDependencies)
        {
            VariableUnion variableUnion = State.variableWithName(variableName);
            variableUnion.variable().removeVariableListener(this.getName());
        }

        // [2] Set the new variable dependencies
        // --------------------------------------------------------------------------------------

        this.variableDependencies = new HashSet<>(variableDependencies);

        // [3] Subscribe this variable to all of the new dependencies
        // --------------------------------------------------------------------------------------

        for (VariableUnion variableUnion : State.variables())
        {
            Variable variable = variableUnion.variable();
            if (variableDependencies.contains(variable.getName())) {
                variable.addVariableListener(this.getName());
            }
        }
    }


    /**
     * This method should be called when the variable's value changes. This could happen directly
     * from user input, or indirectly, if user input changes a variable that was part of this
     * variable's value.
     */
    public void onUpdate()
    {
        Log.d("***VARIABLE", "on update called");

        // [1] Notify all on update listeners
        // --------------------------------------------------------------------------------------

        for (OnUpdateListener onUpdateListener : this.onUpdateListeners)
        {
            Log.d("***VARIABLE", "calling on update listener");
            onUpdateListener.onUpdate();
        }

        // [2] Notify all variable listeners
        // --------------------------------------------------------------------------------------

        for (String variableName : this.variableListeners)
        {
            Log.d("***VARIABLE", "notifying variable listener");
            VariableUnion variableUnion = State.variableWithName(variableName);
            variableUnion.variable().onUpdate();
        }
    }


    public void onNewVariable(Variable newVariable)
    {
        if (variableDependencies.contains(newVariable.getName()))
        {
            newVariable.addVariableListener(this.getName());
        }
    }


    public void addVariableListener(String variableName)
    {
        Log.d("***VARIABLE", "adding variable listener " + variableName);
        this.variableListeners.add(variableName);
    }


    public void removeVariableListener(String variableName)
    {
        this.variableListeners.remove(variableName);
    }


    public void addOnUpdateListener(OnUpdateListener onUpdateListener)
    {
        this.onUpdateListeners.add(onUpdateListener);
    }


    // ON UPDATE LISTENER
    // ------------------------------------------------------------------------------------------

    public interface OnUpdateListener
    {
        void onUpdate();
    }


}
