
package com.kispoko.tome.util.database.sql;


import java.util.UUID;



/**
 * One-to-Many Relation
 */
public class OneToManyRelation
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String parentTableName;
    private String parentCollectionName;
    private UUID   parentId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public OneToManyRelation(String parentTableName,
                             String parentCollectionName,
                             UUID parentId)
    {
        this.parentTableName      = parentTableName;
        this.parentCollectionName = parentCollectionName;
        this.parentId             = parentId;
    }


    // API
    // ------------------------------------------------------------------------------------------

    public String childSQLColumnName()
    {
        return "parent_" + this.parentCollectionName + "_" + this.parentTableName + "_id";
    }


    public String getParentTableName()
    {
        return this.parentTableName;
    }


    public String getParentCollectionName()
    {
        return this.parentCollectionName;
    }


    public UUID getParentId()
    {
        return this.parentId;
    }
}
