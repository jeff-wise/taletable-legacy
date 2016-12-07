
package com.kispoko.tome.util.database.query;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kispoko.tome.Global;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.error.NullColumnTypeError;
import com.kispoko.tome.util.database.error.QueryError;
import com.kispoko.tome.util.database.sql.OneToManyRelation;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.tuple.Tuple2;

import java.util.ArrayList;
import java.util.List;



/**
 * Collection Query
 */
public class CollectionQuery
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String              tableName;
    private OneToManyRelation   oneToManyRelation;

    private List<String>        columnNames;
    private List<SQLValue.Type> columnTypes;
    private Integer             columnsSize;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public CollectionQuery(String tableName,
                           OneToManyRelation oneToManyRelation,
                           List<Tuple2<String,SQLValue.Type>> columns)
    {
        this.tableName         = tableName;
        this.oneToManyRelation = oneToManyRelation;

        // Assign arbitrary index to column names and types for fetching from query by index
        this.columnNames = new ArrayList<>();
        this.columnTypes = new ArrayList<>();
        for (Tuple2<String,SQLValue.Type> column : columns)
        {
            columnNames.add(column.getItem1());
            columnTypes.add(column.getItem2());
        }
        this.columnsSize = columnNames.size();
    }


    // API
    // --------------------------------------------------------------------------------------

    public List<ResultRow> result()
           throws DatabaseException
    {
        SQLiteDatabase database = Global.getDatabase();

        Cursor cursor = database.rawQuery(queryString(), null);

        List<ResultRow> resultRows = new ArrayList<>();

        try
        {
            while (cursor.moveToNext())
            {
                // Read next row
                ResultRow resultRow = new ResultRow();

                for (int i = 0; i < columnsSize; i++)
                {
                    SQLValue columnValue;
                    switch (columnTypes.get(i))
                    {
                        case INTEGER:
                            columnValue = SQLValue.newInteger(cursor.getLong(i));
                            break;
                        case REAL:
                            columnValue = SQLValue.newReal(cursor.getDouble(i));
                            break;
                        case TEXT:
                            columnValue = SQLValue.newText(cursor.getString(i));
                            break;
                        case BLOB:
                            columnValue = SQLValue.newBlob(cursor.getBlob(i));
                            break;
                        case NULL:
                            columnValue = SQLValue.newNull();
                            break;
                        default:
                            throw DatabaseException.nullColumnType(
                                    new NullColumnTypeError(this.tableName,
                                                            this.columnNames.get(i)));
                    }
                    resultRow.putSQLValue(columnNames.get(i), columnValue);
                }

                resultRows.add(resultRow);
            }
        }
        // Catch any exceptions.
        catch (DatabaseException e) {
            throw e;
        }
        catch (Exception e) {
            throw DatabaseException.query(new QueryError(this.queryString()));
        }
        // Ensure cursor is closed
        finally {
            cursor.close();
        }

        return resultRows;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private String queryString()
    {
        StringBuilder queryBuilder = new StringBuilder();

        // SELECT Clause
        // --------------------------------------------------------------------------------------
        queryBuilder.append("SELECT ");

        int columnIndex = 0;
        for (String columnName : this.columnNames) {
            queryBuilder.append(this.tableName);
            queryBuilder.append(".");
            queryBuilder.append(columnName);

            if (columnIndex == this.columnNames.size() - 1)
                queryBuilder.append(" ");
            else
                queryBuilder.append(", ");

            columnIndex++;
        }

        // FROM Clause
        // --------------------------------------------------------------------------------------
        queryBuilder.append("FROM ");
        queryBuilder.append(this.tableName);
        queryBuilder.append(" ");

        // WHERE Clause
        // --------------------------------------------------------------------------------------
        queryBuilder.append("WHERE ");
        queryBuilder.append(this.tableName);
        queryBuilder.append(".");
        queryBuilder.append(this.oneToManyRelation.childSQLColumnName());
        queryBuilder.append(" = ");
        queryBuilder.append("'");
        queryBuilder.append(this.oneToManyRelation.getParentId());
        queryBuilder.append("'");

        return queryBuilder.toString();
    }

}

