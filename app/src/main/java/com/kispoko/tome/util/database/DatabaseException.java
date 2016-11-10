
package com.kispoko.tome.util.database;


import com.kispoko.tome.util.database.error.DatabaseError;



/**
 * Database Exception
 */
public class DatabaseException extends Exception
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    DatabaseError databaseError;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public DatabaseException(DatabaseError databaseError)
    {
        this.databaseError = databaseError;
    }


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public DatabaseError getDatabaseError()
    {
        return this.databaseError;
    }

}
