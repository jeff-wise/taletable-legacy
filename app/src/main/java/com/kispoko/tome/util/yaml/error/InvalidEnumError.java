
package com.kispoko.tome.util.yaml.error;



/**
 * Yaml Error: Invalid Enum
 */
public class InvalidEnumError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String enumValue;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public InvalidEnumError(String enumValue)
    {
        this.enumValue = enumValue;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String getEnumValue()
    {
        return this.enumValue;
    }


    public String errorMessage()
    {
        return "Invalid Enum Error: " + this.enumValue;
    }

}
