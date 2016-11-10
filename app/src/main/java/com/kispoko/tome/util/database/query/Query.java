
package com.kispoko.tome.util.database.query;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;



/**
 * Query
 */
public class Query
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String      tableName;
    private UUID        rowId;
    private Set<String> columnNames;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public Query(String tableName, UUID rowId, Set<String> columnNames)
    {
        this.tableName   = tableName;
        this.rowId       = rowId;
        this.columnNames = columnNames;
    }


    // API
    // --------------------------------------------------------------------------------------


    public List<ResultRow> result()
    {

        return new ArrayList<>();
    }

}
