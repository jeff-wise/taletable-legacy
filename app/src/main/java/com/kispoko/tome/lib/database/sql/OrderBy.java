
package com.kispoko.tome.lib.database.sql;


import java.util.ArrayList;
import java.util.List;



/**
 * SQL: Order By
 */
public class OrderBy
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Order       order;
    private List<Field> fields;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public OrderBy(List<Field> fields, Order order)
    {
        this.order  = order;
        this.fields = fields;
    }


    // API
    // ------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        StringBuilder orderByBuilder = new StringBuilder();

        // > ORDER BY
        orderByBuilder.append("ORDER BY ");

        // > FIELDS
        String sep = "";
        for (Field field : this.fields)
        {
            orderByBuilder.append(sep);
            orderByBuilder.append(field.toString());
            sep = ", ";
        }

        // > ORDER
        orderByBuilder.append(" ");
        orderByBuilder.append(this.order.name().toUpperCase());

        return orderByBuilder.toString();
    }



    // NESTED DEFINITIONS
    // ------------------------------------------------------------------------------------------

    public static class Field
    {

        // PROPERTIES
        // --------------------------------------------------------------------------------------

        private String   columnName;
        private Function function;

        // CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public Field(String columnName, Function function)
        {
            this.columnName = columnName;
            this.function   = function;
        }


        // API
        // --------------------------------------------------------------------------------------

        @Override
        public String toString()
        {
            if (function == null) {
                return columnName;
            }
            else {
                List<String> columnNameAsParameterList = new ArrayList<>();
                columnNameAsParameterList.add(this.columnName);

                return Function.applyFunctionString(function, columnNameAsParameterList);
            }
        }


    }




    public enum Order
    {
        ASC,
        DESC
    }
}
