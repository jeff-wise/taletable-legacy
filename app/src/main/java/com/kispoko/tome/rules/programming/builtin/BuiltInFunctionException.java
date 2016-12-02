
package com.kispoko.tome.rules.programming.builtin;


import com.kispoko.tome.rules.programming.builtin.error.InvalidParameterTypeError;
import com.kispoko.tome.rules.programming.builtin.error.WrongNumberOfParametersError;


/**
 * Built-In Function Exception
 */
public class BuiltInFunctionException extends Exception
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object error;
    private Type   errorType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    private BuiltInFunctionException(Object error, Type errorType)
    {
        this.error     = error;
        this.errorType = errorType;
    }


    public static BuiltInFunctionException
                        wrongNumberOfParameters(WrongNumberOfParametersError error)
    {
        return new BuiltInFunctionException(error, Type.WRONG_NUMBER_OF_PARAMETERS);
    }


    public static BuiltInFunctionException invalidParameterType(InvalidParameterTypeError error)
    {
        return new BuiltInFunctionException(error, Type.INVALID_PARAMETER_TYPE);
    }


    // API
    // ------------------------------------------------------------------------------------------


    // EXCEPTION TYPES
    // ------------------------------------------------------------------------------------------

    public enum Type
    {
        WRONG_NUMBER_OF_PARAMETERS,
        INVALID_PARAMETER_TYPE,
    }

}

