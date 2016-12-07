
package com.kispoko.tome.util.database.query;


import android.util.Log;

import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.error.ColumnDoesNotExistError;
import com.kispoko.tome.util.database.sql.SQLValue;

import java.util.HashMap;
import java.util.Map;


/**
 * Result Row
 */
public class ResultRow
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Map<String,SQLValue> resultMap;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ResultRow()
    {
        resultMap = new HashMap<>();
    }


    // API
    // --------------------------------------------------------------------------------------


    public void putSQLValue(String columnName, SQLValue sqlValue)
    {
        this.resultMap.put(columnName, sqlValue);
    }


    public SQLValue getSQLValue(String columnName)
           throws DatabaseException
    {
        if (this.resultMap.containsKey(columnName)) {
            return this.resultMap.get(columnName);
        }
        else {
            throw DatabaseException.columnDoesNotExist(new ColumnDoesNotExistError(columnName));
        }
    }


}
