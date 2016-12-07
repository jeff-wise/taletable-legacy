
package com.kispoko.tome.util.database.query;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kispoko.tome.Global;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.error.NullColumnTypeError;
import com.kispoko.tome.util.database.error.QueryError;
import com.kispoko.tome.util.database.sql.OrderBy;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.tuple.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * ModelQuery
 */
public class ModelQuery
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String              tableName;

    private List<String>        columnNames;
    private List<SQLValue.Type> columnTypes;
    private Integer             columnsSize;

    private String              queryString;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ModelQuery(String tableName,
                       List<Tuple2<String,SQLValue.Type>> columns,
                       ModelQueryParameters queryParameters)
    {
        this.tableName   = tableName;

        // Assign arbitrary index to column names and types for fetching from query by index
        this.columnNames = new ArrayList<>();
        this.columnTypes = new ArrayList<>();
        for (Tuple2<String,SQLValue.Type> column : columns)
        {
            columnNames.add(column.getItem1());
            columnTypes.add(column.getItem2());
        }
        this.columnsSize = columnNames.size();

        switch (queryParameters.getParametersType())
        {
            case PRIMARY_KEY:
                UUID rowId = queryParameters.getPrimaryKeyParameters().getId();
                this.queryString = byPrimaryKeyQueryString(tableName, columnNames, rowId);
                break;
            case TOP_RESULT:
                OrderBy orderBy = queryParameters.getTopResultParameters().getOrderBy();
                this.queryString = byTopResultQueryString(tableName, columnNames, orderBy);
                break;
        }
    }


    // API
    // --------------------------------------------------------------------------------------

    public ResultRow result()
           throws DatabaseException
    {
        SQLiteDatabase database = Global.getDatabase();

        Cursor cursor = database.rawQuery(this.queryString, null);

        ResultRow resultRow = new ResultRow();

        try
        {
            cursor.moveToFirst();

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
                                new NullColumnTypeError(this.tableName, this.columnNames.get(i)));
                }
                resultRow.putSQLValue(columnNames.get(i), columnValue);
            }
        }
        // Catch any exceptions.
        catch (DatabaseException e) {
            throw e;
        }
        catch (Exception e) {
            throw DatabaseException.query(new QueryError(this.queryString));
        }
        // Ensure cursor is closed
        finally {
            cursor.close();
        }

        return resultRow;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Query String Builders
    // ------------------------------------------------------------------------------------------

    /**
     * The query string for a querying a model by its primary key id.
     * @param tableName
     * @param columnNames
     * @param rowId
     * @return
     */
    private static String byPrimaryKeyQueryString(String tableName,
                                                  List<String> columnNames,
                                                  UUID rowId)
    {
        StringBuilder queryBuilder = new StringBuilder();

        // ** SELECT clause
        addSelectClause(queryBuilder, tableName, columnNames);

        // ** FROM clause
        addFromClause(queryBuilder, tableName);

        // ** WHERE Clause
        queryBuilder.append("WHERE ");
        queryBuilder.append(tableName);
        queryBuilder.append(".");
        queryBuilder.append("_id");
        queryBuilder.append(" = ");
        queryBuilder.append("'");
        queryBuilder.append(rowId.toString());
        queryBuilder.append("'");

        return queryBuilder.toString();
    }


    /**
     * The query string for querying a model by sorting all of the models of its type and
     * choosing the top result.
     * @param tableName The model's table name.
     * @param columnNames The names of all the columns in the model table to query.
     * @param orderBy An order by clause to sort the models on.
     * @return A query string for returning the top model of a sorted row set.
     */
    private static String byTopResultQueryString(String tableName,
                                                 List<String> columnNames,
                                                 OrderBy orderBy)
    {
        StringBuilder queryBuilder = new StringBuilder();

        // ** SELECT Clause
        addSelectClause(queryBuilder, tableName, columnNames);

        // ** FROM Clause
        addFromClause(queryBuilder, tableName);

        // ** ORDER BY Clause
        queryBuilder.append(orderBy.toString());

        // ** LIMIT Clause
        queryBuilder.append(" LIMIT 1");

        return queryBuilder.toString();
    }


    // > Query String Builder Helpers
    // ------------------------------------------------------------------------------------------

    /**
     * Add a SELECT clause to the query string builder.
     * @param queryBuilder
     * @param tableName
     * @param columnNames
     */
    private static void addSelectClause(StringBuilder queryBuilder,
                                        String tableName,
                                        List<String> columnNames)
    {
        queryBuilder.append("SELECT ");

        int columnIndex = 0;
        for (String columnName : columnNames) {
            queryBuilder.append(tableName);
            queryBuilder.append(".");
            queryBuilder.append(columnName);

            if (columnIndex == columnNames.size() - 1)
                queryBuilder.append(" ");
            else
                queryBuilder.append(", ");

            columnIndex++;
        }
    }


    /**
     * Add a FROM clause to the query string builder.
     * @param queryBuilder
     * @param tableName
     */
    private static void addFromClause(StringBuilder queryBuilder, String tableName)
    {
        queryBuilder.append("FROM ");
        queryBuilder.append(tableName);
        queryBuilder.append(" ");
    }

}
