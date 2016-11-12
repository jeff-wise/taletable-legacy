
package com.kispoko.tome.util.database.query;


import java.util.Set;
import java.util.UUID;



/**
 * ModelQuery
 */
public class ModelQuery
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String      tableName;
    private UUID        rowId;
    private Set<String> columnNames;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public ModelQuery(String tableName, UUID rowId, Set<String> columnNames)
    {
        this.tableName   = tableName;
        this.rowId       = rowId;
        this.columnNames = columnNames;
    }


    // API
    // --------------------------------------------------------------------------------------


    public ResultRow result()
    {

        return null;
    }

}
