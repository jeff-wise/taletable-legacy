
package com.kispoko.tome.util.yaml;


import com.kispoko.tome.util.yaml.error.EmptyValueError;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;
import com.kispoko.tome.util.yaml.error.MissingKeyError;
import com.kispoko.tome.util.yaml.error.UnexpectedTypeError;



/**
 * Yaml Parse Exception
 */
public class YamlException extends Exception
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object error;
    private Type errorType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    private YamlException(Object error, Type errorType)
    {
        this.error = error;
        this.errorType = errorType;
    }


    public static YamlException missingKey(MissingKeyError missingKeyError)
    {
        return new YamlException(missingKeyError, Type.MISSING_KEY);
    }


    public static YamlException unexpectedType(UnexpectedTypeError unexpectedTypeError)
    {
        return new YamlException(unexpectedTypeError, Type.UNEXPECTED_TYPE);
    }


    public static YamlException invalidEnum(InvalidEnumError invalidEnumError)
    {
        return new YamlException(invalidEnumError, Type.INVALID_ENUM);
    }


    public static YamlException emptyValue(EmptyValueError emptyValueError)
    {
        return new YamlException(emptyValueError, Type.EMPTY_VALUE);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Error Type
    // ------------------------------------------------------------------------------------------

    public Type getErrorType()
    {
        return this.errorType;
    }


    // > Errors
    // ------------------------------------------------------------------------------------------

    public MissingKeyError getMissingKeyError()
    {
        return (MissingKeyError) this.error;
    }


    public UnexpectedTypeError getUnexpectedTypeError()
    {
        return (UnexpectedTypeError) this.error;
    }


    public InvalidEnumError getInvalidEnumError()
    {
        return (InvalidEnumError) this.error;
    }


    public EmptyValueError getEmptyValueError()
    {
        return (EmptyValueError) this.error;
    }


    // > Error Message
    // ------------------------------------------------------------------------------------------

    public String errorMessage()
    {
        StringBuilder errorBuilder = new StringBuilder();
        errorBuilder.append("Yaml Error: ");

        switch (this.errorType)
        {
            case MISSING_KEY:
                errorBuilder.append(this.getMissingKeyError().errorMessage());
                break;
            case UNEXPECTED_TYPE:
                errorBuilder.append(this.getUnexpectedTypeError().errorMessage());
                break;
            case INVALID_ENUM:
                errorBuilder.append(this.getInvalidEnumError().errorMessage());
                break;
            case EMPTY_VALUE:
                errorBuilder.append(this.getEmptyValueError().errorMessage());
                break;
        }

        return errorBuilder.toString();
    }


    // EXCEPTION TYPES
    // ------------------------------------------------------------------------------------------

    public enum Type
    {
        MISSING_KEY,
        UNEXPECTED_TYPE,
        EMPTY_VALUE,
        INVALID_ENUM
    }


}

