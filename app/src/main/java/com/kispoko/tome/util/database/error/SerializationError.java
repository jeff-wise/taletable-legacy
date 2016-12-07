
package com.kispoko.tome.util.database.error;



/**
 * Database Error: Serialization Exception
 *
 * Occurs while trying to read a class object and convert that to a SQL table.
 */
public class SerializationError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String className;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public SerializationError(String className)
    {
        this.className = className;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Serialization Exception: In class: " + this.className;
    }

}
