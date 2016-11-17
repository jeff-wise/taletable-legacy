
package com.kispoko.tome.error;



/**
 * Error: Invalid Enum
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


}
