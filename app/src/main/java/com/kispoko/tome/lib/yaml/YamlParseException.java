
package com.kispoko.tome.lib.yaml;


import com.kispoko.tome.lib.yaml.error.EmptyValueError;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;
import com.kispoko.tome.lib.yaml.error.MissingKeyError;
import com.kispoko.tome.lib.yaml.error.UnexpectedTypeError;



/**
 * Yaml Parse Exception
 */
public class YamlParseException extends Exception
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object error;
    private Type errorType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    private YamlParseException(Object error, Type errorType)
    {
        this.error = error;
        this.errorType = errorType;
    }


    public static YamlParseException missingKey(MissingKeyError missingKeyError)
    {
        return new YamlParseException(missingKeyError, Type.MISSING_KEY);
    }


    public static YamlParseException unexpectedType(UnexpectedTypeError unexpectedTypeError)
    {
        return new YamlParseException(unexpectedTypeError, Type.UNEXPECTED_TYPE);
    }


    public static YamlParseException invalidEnum(InvalidEnumError invalidEnumError)
    {
        return new YamlParseException(invalidEnumError, Type.INVALID_ENUM);
    }


    public static YamlParseException emptyValue(EmptyValueError emptyValueError)
    {
        return new YamlParseException(emptyValueError, Type.EMPTY_VALUE);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Error ErrorType
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

