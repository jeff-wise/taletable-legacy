
package com.kispoko.tome.util;


import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.query.Query;
import com.kispoko.tome.util.database.query.ResultRow;
import com.kispoko.tome.util.database.value.DBValue;
import com.kispoko.tome.util.value.Value;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;



/**
 * Model
 */
public abstract class Model<A>
{

    // ABSTRACT METHODS
    // --------------------------------------------------------------------------------------

    abstract public void onUpdateModel(String name);


    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private UUID   id;
    private String name;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public Model(UUID id, String name)
    {
        this.id   = id;
        this.name = name;
    }


    // API
    // --------------------------------------------------------------------------------------

    /**
     * Automatically load this model from the database using reflection on its Value properties
     * and the database data stored within them.
     * @return A new instance of the moddel.
     * @throws DatabaseException
     */
    public A fromDatabase()
           throws DatabaseException
    {
        // QUERY values from this models' table
        // --------------------------------------------------------------------------------------

        // [A 1] Get all of the class's Value fields
        // --------------------------------------------------------------------------------------
        List<Field> valueFields = new ArrayList<>();

        Field[] fields = this.getClass().getFields();
        for (int i = 0; i < fields.length; i++)
        {
            if (Value.class.isAssignableFrom(fields[i].getType()))
                valueFields.add(fields[i]);
        }

        // [A 2] Get all of the columns we need to query
        // --------------------------------------------------------------------------------------
        Map<String,Value<?>> columnValueMap = new HashMap<>();
        Map<String,Value<?>> foreignValueMap = new HashMap<>();

        try
        {
            for (Field field : valueFields)
            {
                Value<?> value = (Value<?>) field.get(this);

                DBValue dbValue = value.getDBValue();
                DBValue.Type referencetype = dbValue.getType();

                // Add Column Value to column map
                if (referencetype == DBValue.Type.COLUMN_VALUE) {
                    DBValue.ColumnValue columnValue = dbValue.getColumnValue();
                    columnValueMap.put(columnValue.getColumnName(), value);
                }
                // Add Foreign Value to column map
                else if (referencetype == DBValue.Type.FOREIGN_VALUE) {
                    DBValue.ForeignValue foreignValue = dbValue.getForeignValue();
                    foreignValueMap.put(foreignValue.getColumnName(), value);
                }
            }
        } catch (IllegalAccessException e) {
            throw new DatabaseException();
        }

        // [A 3] Query the columns.
        // --------------------------------------------------------------------------------------
        Set<String> columnNameSet = new HashSet<>();
        columnNameSet.addAll(columnValueMap.keySet());
        columnNameSet.addAll(foreignValueMap.keySet());

        Query query = new Query(this.name, this.id, columnNameSet);
        List<ResultRow> resultRows = query.result();

        // LOAD all of the model's values by reading the query results and running any other
        // queries necessary to find related values
        // --------------------------------------------------------------------------------------

        if (resultRows.size() > 1)
            throw new DatabaseException();

        ResultRow modelRow = resultRows.get(0);

        // [B 1] Evaluate values directly stored in columns
        // --------------------------------------------------------------------------------------

        for (Map.Entry<String, Value<?>> entry : columnValueMap.entrySet())
        {
            String   columnName             = entry.getKey();
            Value<?> value                  = entry.getValue();

            DBValue.ColumnValue columnValue = value.getDBValue().getColumnValue();

            switch (columnValue.getDataType())
            {
                case INTEGER:
                    ((Value<Integer>) value).setValue(modelRow.getIntegerResult(columnName));
                    break;
                case TEXT:
                    ((Value<String>) value).setValue(modelRow.getIntegerResult(columnName));
                    break;
                case BLOB:
                    break;
            }
            }
        }


        // [B 2] Evaluate many-to-one relations
        // --------------------------------------------------------------------------------------


        // [B 3] Evaluate many-to-one relations
        // --------------------------------------------------------------------------------------


    }

}
