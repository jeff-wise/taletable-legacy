
package com.kispoko.tome.util.database.query;


import com.kispoko.tome.util.database.sql.OrderBy;

import java.util.UUID;



/**
 * Modeler Query Parameters
 */
public class ModelQueryParameters
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object parameters;
    private Type   parametersType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ModelQueryParameters(Object parameters, Type parametersType)
    {
        this.parameters     = parameters;
        this.parametersType = parametersType;

    }


    // NESTED DEFINITIONS
    // ------------------------------------------------------------------------------------------

    public Type getParametersType()
    {
        return this.parametersType;
    }


    public PrimaryKey getPrimaryKeyParameters()
    {
        return (PrimaryKey) this.parameters;
    }


    public TopResult getTopResultParameters()
    {
        return (TopResult) this.parameters;
    }


    // NESTED DEFINITIONS
    // ------------------------------------------------------------------------------------------

    public enum Type
    {
        PRIMARY_KEY,
        TOP_RESULT
    }


    public static class PrimaryKey
    {

        // PROPERTIES
        // ------------------------------------------------------------------------------------------

        private UUID id;

        // CONSTRUCTORS
        // ------------------------------------------------------------------------------------------

        public PrimaryKey(UUID id)
        {
            this.id = id;
        }

        // API
        // ------------------------------------------------------------------------------------------

        public UUID getId()
        {
            return this.id;
        }

    }


    public static class TopResult
    {

        // PROPERTIES
        // ------------------------------------------------------------------------------------------

        private OrderBy orderBy;

        // CONSTRUCTORS
        // ------------------------------------------------------------------------------------------

        public TopResult(OrderBy orderBy)
        {
            this.orderBy = orderBy;
        }

        // API
        // ------------------------------------------------------------------------------------------

        public OrderBy getOrderBy()
        {
            return this.orderBy;
        }

    }

}
