
package com.kispoko.tome.engine.programming.interpreter.error;



/**
 * Evaluation Error: Undefined Variable
 */
public class UndefinedVariableError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String variableName;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public UndefinedVariableError(String variableName)
    {
        this.variableName = variableName;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String getVariableName()
    {
        return this.variableName;
    }


    public String errorMessage()
    {
        return "Undefined Variable: " + this.variableName;
    }

}
