
package com.kispoko.tome.rules.programming.evaluation.error;



/**
 * Evaluation Error: Undefined Program Variable
 */
public class UndefinedProgramVariableError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String variableName;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public UndefinedProgramVariableError(String variableName)
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
        return "Undefined Program Variable: " + this.variableName;
    }

}
