
package com.kispoko.tome.rules.programming.interpreter.error;


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
        return "Unexpected Program Variable ErrorType:\n" +
               "    Variable Name: " + this.variableName + "\n" +
               "    Actual Variable ErrorType: " + this.actualVariableType.toString() + "\n" +
               "    Expected Variable ErrorType: " + this.expectedVariableType.toString();
    }

}