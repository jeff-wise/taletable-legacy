
package com.kispoko.tome.engine.variable.error;


import com.kispoko.tome.engine.variable.VariableType;



/**
 * Variable Error: Unexpected Type
 */
public class UnexpectedVariableTypeError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String       variableName;
    private VariableType expectedType;
    private VariableType actualType;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public UnexpectedVariableTypeError(String variableName,
                                       VariableType expectedType,
                                       VariableType actualType)
    {
        this.variableName = variableName;
        this.expectedType = expectedType;
        this.actualType   = actualType;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Unexpected Variable Type: " +
                "    Variable Name: " + this.variableName + "\n" +
                "    Expected Type: " + this.expectedType.name() + "\n" +
                "    Actual Type: " + this.actualType.name();
    }


}
