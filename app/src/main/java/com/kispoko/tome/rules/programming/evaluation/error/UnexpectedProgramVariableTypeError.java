
package com.kispoko.tome.rules.programming.evaluation.error;


import com.kispoko.tome.rules.programming.program.ProgramValueType;



/**
 * Evaluation Error: Undefined Variable
 */
public class UnexpectedProgramVariableTypeError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String           variableName;
    private ProgramValueType actualVariableType;
    private ProgramValueType expectedVariableType;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public UnexpectedProgramVariableTypeError(String variableName,
                                              ProgramValueType actualVariableType,
                                              ProgramValueType expectedVariableType)
    {
        this.variableName         = variableName;
        this.actualVariableType   = actualVariableType;
        this.expectedVariableType = expectedVariableType;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Unexpected Program Variable Type:\n" +
               "    Variable Name: " + this.variableName + "\n" +
               "    Actual Variable Type: " + this.actualVariableType.toString() + "\n" +
               "    Expected Variable Type: " + this.expectedVariableType.toString();
    }

}