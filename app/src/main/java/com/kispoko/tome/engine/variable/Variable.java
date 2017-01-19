
package com.kispoko.tome.engine.variable;


import com.kispoko.tome.engine.State;

import java.util.List;



/**
 * Variable Interface
 */
public abstract class Variable
{

    // ABSTRACT METHODS
    // ------------------------------------------------------------------------------------------

    public abstract String                  name();
    public abstract String                  label();
    public abstract void                    setName(String name);
    public abstract boolean                 isNamespaced();
    public abstract List<VariableReference> dependencies();
    public abstract List<String>            tags();


    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private OnUpdateListener                onUpdateListener;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Variable()
    {
        this.onUpdateListener = null;
    }


    // API
    // ------------------------------------------------------------------------------------------


    /**
     * This method should be called when the variable's value changes. This could happen directly
     * from user input, or indirectly, if user input changes a variable that was part of this
     * variable's value.
     */
    public void onUpdate()
    {
        // [1] Call the variable's update listener
        // --------------------------------------------------------------------------------------

        if (this.onUpdateListener != null) {
            this.onUpdateListener.onUpdate();
        }

        // [2] Update any variables that depend on this variable
        // --------------------------------------------------------------------------------------

        State.updateVariableDependencies(this);
    }


    public void setOnUpdateListener(OnUpdateListener onUpdateListener)
    {
        this.onUpdateListener = onUpdateListener;
    }


    // ON UPDATE LISTENER
    // ------------------------------------------------------------------------------------------

    public interface OnUpdateListener
    {
        void onUpdate();
    }


}
