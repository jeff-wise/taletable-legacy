
package com.kispoko.tome.rules.programming.summation.error;



/**
 * Summation Error: Undefined Variable
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
