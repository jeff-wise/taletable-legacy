
package com.kispoko.tome.lib.database.error;


import com.kispoko.tome.util.ApplicationError;



/**
 * Database Error: Serialization Exception
 *
 * Occurs while trying to read a class object and convert that to a SQL table.
 */
public class SerializationError implements ApplicationError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String    className;
    private Exception exception;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public SerializationError(String className, Exception exception)
    {
        this.className = className;
        this.exception = exception;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Serialization Exception: In class: " + "\n" +
                "    Class: " + this.className + "\n" +
                "    Exception:\n" + this.exception.getMessage();
    }

}
