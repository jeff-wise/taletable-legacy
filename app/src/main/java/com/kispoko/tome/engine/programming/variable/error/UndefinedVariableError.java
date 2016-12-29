
package com.kispoko.tome.engine.programming.variable.error;



/**
 * Variable Error: Undefined Variable
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

    public String errorMessage()
    {
        return "Undefined Variable: " + this.variableName;
    }


}
