
package com.kispoko.tome.lib.functor.form;



/**
 * Field Options
 */
public class FieldOptions
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------

    private boolean     isEditMode;
    private boolean     isRequired;

    private boolean     isAutoField;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------

    public FieldOptions(boolean isEditMode,
                        boolean isRequired,
                        boolean isAutoField)
    {
        this.isEditMode     = isEditMode;
        this.isRequired     = isRequired;

        this.isAutoField    = isAutoField;
    }


    public FieldOptions()
    {
        this.isEditMode     = false;
        this.isRequired     = false;

        this.isAutoField    = false;
    }


    public static FieldOptions newField(boolean isRequired)
    {
        return new FieldOptions(true, isRequired, false);
    }


    // API
    // -----------------------------------------------------------------------------------------

    public boolean isEditMode()
    {
        return this.isEditMode;
    }


    public boolean isRequired ()
    {
        return this.isRequired;
    }


    public boolean isAutoField()
    {
        return this.isAutoField;
    }

}
