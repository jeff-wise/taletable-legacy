
package com.kispoko.tome.error;



/**
 * Error: Unknown Variant
 *
 *
 * Occurs when a switch statement falls through.
 *
 * Reasons this could happen:
 *    * Add a new case in the enum, but forget to add it to a switch statement.
 *    * Enum value is null.
 *
 */
public class UnknownVariantError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String variantName;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public UnknownVariantError(String variantName)
    {
        this.variantName = variantName;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String getVariantName()
    {
        return this.variantName;
    }


    public String errorMessage()
    {
        return "Unknown Variant: Of type: " + variantName;
    }


}
