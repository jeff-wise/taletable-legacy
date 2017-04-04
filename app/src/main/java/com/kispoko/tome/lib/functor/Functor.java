
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

    /**
     * A name for the functor. Used as a column name.
     */
    private   String            name;

    /**
     * The name of the functor in human-readable form. Used as the label for fields.
     */
    private   String            label;
    private   Integer           labelId;

    /**
     * A description of the functor's purpose. Used in fields.
     */
    private   String            description;
    private   Integer           descriptionId;

    private   OnUpdateListener  onUpdateListener;

    private   boolean           isDefault;

    private   boolean           isSaved;
    private   boolean           isRequired;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public Functor(A value)
    {
        this.value          = value;

        this.name           = null;

        this.label          = null;
        this.labelId        = null;

        this.description    = null;
        this.descriptionId  = null;

        this.isDefault      = false;
        this.isSaved        = false;
        this.isRequired     = false;
    }


    public Functor(A value, boolean isRequired)
    {
        this.value          = value;

        this.name           = null;

        this.label          = null;
        this.labelId        = null;

        this.description    = null;
        this.descriptionId  = null;

        this.isDefault      = false;
        this.isSaved        = false;
        this.isRequired     = isRequired;
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

    /**
     * The functor name.
     * @return the name.
     */
    public String name()
    {
        return this.name;
    }


    /**
     * Set the functor's name.
     * @param name The name.
     */
    public void setName(String name)
    {
        this.name = name;
    }


    // ** Label
    // -----------------------------------------------------------------------------------------

    /**
     * The functor label.
     * @return The label
     */
    public String label()
    {
        return this.label;
    }


    /**
     * Set the functor's label.
     * @param label The label.
     */
    public void setLabel(String label)
    {
        this.label = label;
    }


    // ** Label Id
    // -----------------------------------------------------------------------------------------

    /**
     * The label string resource id.
     * @return The id.
     */
    public Integer labelId()
    {
        return this.labelId;
    }


    /**
     * Set the label string resource id.
     * @param labelId The string resource id.
     */
    public void setLabelId(int labelId)
    {
        this.labelId = labelId;
    }


    // ** Description
    // -----------------------------------------------------------------------------------------

    /**
     * The functor's description.
     * @return The description.
     */
    public String description()
    {
        return this.description;
    }


    /**
     * Set the functor's description.
     * @param description The description.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }


    // ** Description Id
    // -----------------------------------------------------------------------------------------

    /**
     * The description string resource id.
     * @return The id.
     */
    public Integer descriptionId()
    {
        return this.descriptionId;
    }


    /**
     * Set the description string resource id.
     * @param descriptionId The string resource id.
     */
    public void setDescriptionId(int descriptionId)
    {
        this.descriptionId = descriptionId;
    }


    // ** Is Required
    // --------------------------------------------------------------------------------------

    public boolean isRequired()
    {
        return this.isRequired;
    }


    /**
     * Set the functor's isRequired flag.
     * @param isRequired Is required?
     */
    public void setIsRequired(boolean isRequired)
    {
        this.isRequired = isRequired;
    }



    // LISTENERS
    // ------------------------------------------------------------------------------------------

    public interface OnUpdateListener extends Serializable {
        void onUpdate();
    }


}
