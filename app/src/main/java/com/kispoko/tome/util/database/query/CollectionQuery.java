
package com.kispoko.tome.util.database.query;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;



/**
 * Collection Query
 */
public class CollectionQuery
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String      tableName;
    private String      parentTableName;
    private UUID        parentId;
    private Set<String> columnNames;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public CollectionQuery(String tableName,
                           String parentTableName,
                           UUID parentId,
                           Set<String> columnNames)
    {
        this.tableName       = tableName;
        this.parentTableName = parentTableName;
        this.parentId        = parentId;
        this.columnNames     = columnNames;
    }


    // API
    // --------------------------------------------------------------------------------------

    public List<ResultRow> result()
    {
        return new ArrayList<>();
    }

}

