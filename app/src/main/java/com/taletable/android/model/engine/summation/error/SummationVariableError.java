
package com.taletable.android.model.engine.summation.error;


import com.taletable.android.model.engine.variable.VariableException;
import com.taletable.android.util.ApplicationError;



/**
 * Summation Error: Variable
 */
public class SummationVariableError implements ApplicationError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private VariableException variableException;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public SummationVariableError(VariableException variableException)
    {
        this.variableException = variableException;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return variableException.errorMessage();
    }


}
