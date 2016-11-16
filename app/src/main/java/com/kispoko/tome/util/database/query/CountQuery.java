
package com.kispoko.tome.util.database.query;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kispoko.tome.Global;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.error.QueryError;



/**
 * Query: Count
 *
 * Count the number of saved instances of a model.
 */
public class CountQuery
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String queryString;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public CountQuery(String tableName)
    {
        this.queryString = CountQuery.countQueryString(tableName);
    }


    // API
    // ------------------------------------------------------------------------------------------

    public Integer run()
           throws DatabaseException
    {
        SQLiteDatabase database = Global.getDatabase();

        Cursor cursor = database.rawQuery(this.queryString, null);

        Integer count;

        try
        {
            cursor.moveToFirst();

            count = cursor.getInt(0);
        }
        catch (Exception e) {
            throw new DatabaseException(new QueryError(e), DatabaseException.ErrorType.QUERY);
        }
        // Ensure cursor is closed
        finally {
            cursor.close();
        }

        return count;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------


    private static String countQueryString(String tableName)
    {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("SELECT count(*) FROM ");
        queryBuilder.append(tableName);

        return queryBuilder.toString();
    }

}
