
package com.kispoko.tome.util.yaml;


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

    public YamlException() { }


    public YamlException(Object error, Type errorType)
    {
        this.error = error;
        this.errorType = errorType;
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


    // EXCEPTION TYPES
    // ------------------------------------------------------------------------------------------

    public enum Type
    {
        MISSING_KEY,
        UNEXPECTED_TYPE,
        INVALID_ENUM
    }



}

