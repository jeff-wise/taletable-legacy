
package com.kispoko.tome.util.yaml.error;



/**
 * Yaml Parsing Error: Empty Value
 *
 * This error occurs when a value in the yaml structure is missing or null, and a valid
 * value must be present for the program to function.
 */
public class EmptyValueError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public EmptyValueError()
    {
    }


    // API
    // --------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Empty Value Error";
    }

}
