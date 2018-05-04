
package com.kispoko.tome.rts.entity.engine.search;


import com.kispoko.tome.model.engine.mechanic.ActiveMechanicSearchResult;
import com.kispoko.tome.rts.entity.engine.ActiveVariableSearchResult;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/**
 * Search Result: Active Elemnets in the Engine
 */
public class EngineActiveSearchResult
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private Type                        type;

    private ActiveVariableSearchResult  variableResult;
    private ActiveMechanicSearchResult  mechanicResult;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    /**
     * Create a variable search result.
     * @param result The active variable search result.
     */
    public EngineActiveSearchResult(ActiveVariableSearchResult result)
    {
        this.type           = Type.VARIABLE;

        this.variableResult = result;
    }


    /**
     * Create a mechanic search result.
     * @param result The active mechanic search result.
     */
    public EngineActiveSearchResult(ActiveMechanicSearchResult result)
    {
        this.type           = Type.MECHANIC;

        this.mechanicResult = result;
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > State
    // -----------------------------------------------------------------------------------------

    // ** Type
    // -----------------------------------------------------------------------------------------

    public Type type()
    {
        return this.type;
    }


    // ** Cases
    // -----------------------------------------------------------------------------------------

    /**
     * The variable search result case.
     * @return The variable search result (if it is a variable result).
     */
    public ActiveVariableSearchResult variableSearchResult()
    {
//        if (this.type() != Type.VARIABLE) {
//            ApplicationFailure.union(
//                    UnionException.invalidCase(
//                            new InvalidCaseError("variable", this.type.toString())));
//        }
        return this.variableResult;
    }


    /**
     * The mechanic search result case.
     * @return The mechanic search result (if it is a mechanic result).
     */
    public ActiveMechanicSearchResult mechanicSearchResult()
    {
//        if (this.type() != Type.MECHANIC) {
//            ApplicationFailure.union(
//                    UnionException.invalidCase(
//                            new InvalidCaseError("mechanic", this.type.toString())));
//        }
        return this.mechanicResult;
    }


    // > Ranking
    // -----------------------------------------------------------------------------------------

    public float ranking()
    {
        switch (this.type())
        {
            case VARIABLE:
                return this.variableSearchResult().ranking();
            case MECHANIC:
                return this.mechanicSearchResult().ranking();
            default:
                return 0;
        }
    }


    // > Equals / HashCode
    // -----------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EngineActiveSearchResult that = (EngineActiveSearchResult) o;

        return new EqualsBuilder()
                .append(type, that.type)
                .append(variableResult, that.variableResult)
                .append(mechanicResult, that.mechanicResult)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(type)
                .append(variableResult)
                .append(mechanicResult)
                .toHashCode();
    }


    // RESULT TYPE
    // -----------------------------------------------------------------------------------------

    public enum Type {
        VARIABLE,
        MECHANIC
    }
}
