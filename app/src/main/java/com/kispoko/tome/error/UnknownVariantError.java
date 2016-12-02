
package com.kispoko.tome.error;



/**
 * Error: Unknown Variant
 *
 * This error occurs when we are creating a union value, but none of the previously known cases
 * matches the value. For example, the code creates a union value that contains either a type A, B,
 * or C, but if we add a type D without modifying the code which places the values in the union,
 * it's possible it will fall through all of the known cases and produce an error. This
 * exception exists for thta case.
 */
public class UnknownVariantError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String unionClassName;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public UnknownVariantError(String unionClassName)
    {
        this.unionClassName = unionClassName;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String getUnionClassName()
    {
        return this.unionClassName;
    }


    public String errorMessage()
    {
        return "Unknown Variant: Of type: " + unionClassName;
    }


}
