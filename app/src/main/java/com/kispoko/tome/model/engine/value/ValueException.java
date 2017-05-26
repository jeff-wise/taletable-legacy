
package com.kispoko.tome.model.engine.value;


import com.kispoko.tome.model.engine.value.error.UndefinedValueError;
import com.kispoko.tome.model.engine.value.error.UnexpectedValueTypeError;



/**
 * Value Exception
 */
public class ValueException extends Exception
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object    error;
    private ErrorType errorType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    private ValueException(Object error, ErrorType errorType)
    {
        this.error     = error;
        this.errorType = errorType;
    }


    public static ValueException unexpectedValueType(UnexpectedValueTypeError error)
    {
        return new ValueException(error, ErrorType.UNEXPECTED_VALUE_TYPE);
    }


    public static ValueException undefinedValue(UndefinedValueError error)
    {
        return new ValueException(error, ErrorType.UNDEFINED_VALUE);
    }


    // API
    // ------------------------------------------------------------------------------------------

    public ErrorType getErrorType()
    {
        return this.errorType;
    }


    public String errorMessage()
    {
        StringBuilder errorBuilder = new StringBuilder();

        errorBuilder.append("Value Error: ");

        switch (this.errorType)
        {
            case UNEXPECTED_VALUE_TYPE:
                errorBuilder.append(((UnexpectedValueTypeError) this.error).errorMessage());
                break;
            case UNDEFINED_VALUE:
                errorBuilder.append(((UndefinedValueError) this.error).errorMessage());
                break;
        }

        return errorBuilder.toString();
    }


    // EXCEPTION TYPES
    // ------------------------------------------------------------------------------------------

    private enum ErrorType
    {
        UNEXPECTED_VALUE_TYPE,
        UNDEFINED_VALUE
    }

}
