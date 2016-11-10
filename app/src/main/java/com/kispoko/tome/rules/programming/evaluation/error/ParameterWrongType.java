
package com.kispoko.tome.rules.programming.evaluation.error;


import com.kispoko.tome.rules.programming.program.Statement;
import com.kispoko.tome.rules.programming.evaluation.EvaluationError;



/**
 * Parameter Wrong Type Error
 */
public class ParameterWrongType extends EvaluationError
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Integer parameterIndex;
    private Statement.ParameterType expectedType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ParameterWrongType(Integer parameterIndex, Statement.ParameterType expectedType)
    {
        this.parameterIndex = parameterIndex;
        this.expectedType = expectedType;
    }


    // API
    // ------------------------------------------------------------------------------------------

    public Integer getParameterIndex() {
        return this.parameterIndex;
    }


    public Statement.ParameterType getExpectedType() {
        return this.expectedType;
    }

}