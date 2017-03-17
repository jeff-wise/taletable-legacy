
package com.kispoko.tome.lib.functor;


import java.io.Serializable;



/**
 * Value
 */
public abstract class Functor<A> implements Serializable
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    protected A                 value;

    private   String           name;

    private   OnUpdateListener onUpdateListener;

    private   boolean          isDefault;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public Functor(A value)
    {
        this.value      = value;

        this.isDefault  = false;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

    // ** Value
    // --------------------------------------------------------------------------------------

    public A getValue()
    {
        return this.value;
    }


    public void setValue(A newValue)
    {
        if (newValue != null)
        {
            this.value = newValue;

            if (this.onUpdateListener != null)
                this.onUpdateListener.onUpdate();
        }
    }


    // ** Is Default
    // --------------------------------------------------------------------------------------

    /**
     * If true, then the value in the functor is a default value (not set by user).
     * @return Is default?
     */
    public boolean isDefault()
    {
        return this.isDefault;
    }

    /**
     * Set whether or not the value in the functor is a default value.
     * @param isDefault Is default?
     */
    public void setIsDefault(boolean isDefault)
    {
        this.isDefault = isDefault;
    }


    // ** Update Listener
    // --------------------------------------------------------------------------------------

    public void setOnUpdateListener(OnUpdateListener onUpdateListener)
    {
        this.onUpdateListener = onUpdateListener;
    }


    // > Data
    // --------------------------------------------------------------------------------------

    // ** Is Null
    // --------------------------------------------------------------------------------------

    /**
     * Returns true if the data inside the value is null.
     * @return True if the data in the value is null, or false otherwise.
     */
    public boolean isNull()
    {
        return this.value == null;
    }


    // ** Name
    // --------------------------------------------------------------------------------------

    public String name()
    {
        return this.name;
    }


    public void setName(String name)
    {
        this.name = name.toLowerCase();
    }


    // LISTENERS
    // ------------------------------------------------------------------------------------------

    public interface OnUpdateListener extends Serializable {
        void onUpdate();
    }


}
