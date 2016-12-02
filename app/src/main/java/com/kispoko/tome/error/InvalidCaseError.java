
package com.kispoko.tome.error;



/**
 * Union Error: Invalid Case
 *
 * This error occurs when the case of a union value is accessed, but the value is not of that case.
 * In a language with proper sum types, this couldn't get pass the compiler, but since we are
 * faking it, we have this exception :)
 */
public class InvalidCaseError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String accessedCase;
    private String actualCase;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public InvalidCaseError(String accessedCase, String actualCase)
    {
        this.accessedCase = accessedCase;
        this.actualCase   = actualCase;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String getAccessedCase()
    {
        return this.accessedCase;
    }


    public String getActualCase()
    {
        return this.actualCase;
    }


    public String errorMessage()
    {
        return "Invalid Case:\n" +
               "    Accessed Case: " + this.accessedCase + "\n" +
               "    Actual Case: " + this.actualCase;
    }


}
