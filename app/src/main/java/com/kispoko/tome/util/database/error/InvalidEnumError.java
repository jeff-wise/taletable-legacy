
package com.kispoko.tome.util.database.error;


import com.kispoko.tome.util.ApplicationError;



/**
 * Database Error: Invalid Enum
 */
public class InvalidEnumError implements ApplicationError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String enumValue;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public InvalidEnumError(String enumValue)
    {
        this.enumValue = enumValue;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String getEnumValue()
    {
        return this.enumValue;
    }


    public String errorMessage()
    {
        return "Invalid Enum: Unknown Value: " + this.enumValue;
    }

}
