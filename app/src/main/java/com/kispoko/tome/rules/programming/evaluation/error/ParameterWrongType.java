
package com.kispoko.tome.rules.programming.evaluation.error;


import com.kispoko.tome.rules.programming.program.statement.ParameterType;
import com.kispoko.tome.rules.programming.program.statement.Statement;
import com.kispoko.tome.rules.programming.evaluation.EvaluationError;



/**
 * Parameter Wrong Type Error
 */
public class ParameterWrongType extends EvaluationError
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Integer parameterIndex;
    private ParameterType expectedType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ParameterWrongType(Integer parameterIndex, ParameterType expectedType)
    {
        this.parameterIndex = parameterIndex;
        this.expectedType   = expectedType;
    }


    // API
    // ------------------------------------------------------------------------------------------

    public Integer getParameterIndex()
    {
        return this.parameterIndex;
    }


    public ParameterType getExpectedType()
    {
        return this.expectedType;
    }

}
