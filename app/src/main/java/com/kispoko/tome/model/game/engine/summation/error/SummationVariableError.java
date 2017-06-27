
package com.kispoko.tome.model.game.engine.summation.error;


import com.kispoko.tome.model.game.engine.variable.VariableException;
import com.kispoko.tome.util.ApplicationError;



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