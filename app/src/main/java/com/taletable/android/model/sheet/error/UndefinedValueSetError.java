
package com.taletable.android.model.sheet.error;


import com.taletable.android.util.ApplicationError;



/**
 * Sheet Error: Undefined Value
 *
 * Occurs when a value is read from a variable on the sheet, but the value set does not exist.
 *
 * Possible Causes:
 *
 * A valueset is deleted, but a variable still references that value.
 */
public class UndefinedValueSetError implements ApplicationError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String location;
    private String valueSetName;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public UndefinedValueSetError(String location, String valueSetName)
    {
        this.location     = location;
        this.valueSetName = valueSetName;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Undefined Value Set Error:\n" +
                "    Location: " + this.location + "\n" +
                "    Value Set: " + this.valueSetName;
    }

}
