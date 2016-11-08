
package com.kispoko.tome.util.yaml;



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

    public YamlError.MissingKey getMissingKeyError()
    {
        return (YamlError.MissingKey) this.error;
    }


    public YamlError.UnexpectedType getUnexpectedTypeError()
    {
        return (YamlError.UnexpectedType) this.error;
    }


    // EXCEPTION TYPES
    // ------------------------------------------------------------------------------------------

    public enum Type
    {
        MISSING_KEY,
        UNEXPECTED_TYPE
    }



}

