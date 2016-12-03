
package com.kispoko.tome.rules.programming.builtin.error;


import com.kispoko.tome.rules.programming.program.ProgramValueType;



/**
 * Built-In Funtion Error: Invalid Parameter ErrorType
 */
public class InvalidParameterTypeError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private int              parameterIndex;
    private ProgramValueType actualParameterType;
    private ProgramValueType expectedParameterType;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public InvalidParameterTypeError(int parameterIndex,
                                     ProgramValueType actualParameterType,
                                     ProgramValueType expectedParameterType)
    {
        this.parameterIndex        = parameterIndex;
        this.actualParameterType   = actualParameterType;
        this.expectedParameterType = expectedParameterType;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String errorMessage()
    {
        return "Invalid Parameter ErrorType:\n" +
               "    Parameter Index: " + Integer.toString(this.parameterIndex) + "\n" +
               "    Actual Parameter ErrorType: " + this.actualParameterType.toString() + "\n" +
               "    Expected Parameter ErrorType: " + this.expectedParameterType.toString();
    }

}
