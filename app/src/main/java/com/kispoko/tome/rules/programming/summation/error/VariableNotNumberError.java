
package com.kispoko.tome.rules.programming.summation.error;



/**
 * Summation Error: Undefined Variable
 */
public class VariableNotNumberError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String variableName;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public VariableNotNumberError(String variableName)
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
        return "Variable is not a number: " + this.variableName;
    }

}
