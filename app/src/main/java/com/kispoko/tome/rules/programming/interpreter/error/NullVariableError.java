
package com.kispoko.tome.rules.programming.interpreter.error;



/**
 * Evaluation Error: Undefined Program
 */
public class NullVariableError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String variableName;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public NullVariableError(String variableName)
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
        return "Null Variable : " + this.variableName;
    }

}
