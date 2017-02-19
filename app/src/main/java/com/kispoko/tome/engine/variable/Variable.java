
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
    public abstract void                    setName(String name);
    public abstract String                  label();
    public abstract void                    setLabel(String label);
    public abstract boolean                 isNamespaced();
    public abstract List<VariableReference> dependencies();
    public abstract List<String>            tags();
    public abstract String                  valueString() throws NullVariableException;
    public abstract void                    initialize();


    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private OnUpdateListener                onUpdateListener;

    protected String                        originalName;
    protected String                        originalLabel;


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


    public void setNamespace(Namespace namespace)
    {
        // > Update name
        String previousName = this.name();

        String newName = namespace.name() + "." + this.originalName;
        this.setName(newName);

        // > Update label
        String newLabel = namespace.label() + " " + this.originalLabel;
        this.setLabel(newLabel);

        // > Reindex variable
        State.removeVariable(previousName);
        State.addVariable(this);
    }



    // ON UPDATE LISTENER
    // ------------------------------------------------------------------------------------------

    public interface OnUpdateListener
    {
        void onUpdate();
    }


}
