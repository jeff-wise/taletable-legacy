
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
