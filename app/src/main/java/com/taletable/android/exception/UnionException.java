
package com.taletable.android.exception;


import com.taletable.android.error.InvalidCaseError;
import com.taletable.android.error.UnknownVariantError;



/**
 * Exception: Union
 */
public class UnionException extends Exception
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private Object error;
    private Type   errorType;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    private UnionException(Object error, Type errorType)
    {
        this.error     = error;
        this.errorType = errorType;
    }


    public static UnionException unknownVariant(UnknownVariantError error)
    {
        return new UnionException(error, Type.UNKNOWN_VARIANT);
    }


    public static UnionException invalidCase(InvalidCaseError error)
    {
        return new UnionException(error, Type.INVALID_CASE);
    }


    // API
    // -------------------------------------------------------------------------------------------

    public Object getError()
    {
        return this.error;
    }


    public String errorMessage()
    {
        StringBuilder errorBuilder = new StringBuilder();

        errorBuilder.append("Union Error: ");

        switch (errorType)
        {
            case UNKNOWN_VARIANT:
                errorBuilder.append(((UnknownVariantError) error).errorMessage());
                break;
            case INVALID_CASE:
                errorBuilder.append(((InvalidCaseError) error).errorMessage());
                break;
        }

        return errorBuilder.toString();
    }


    // NESTED DEFINITIONS
    // -------------------------------------------------------------------------------------------

    public enum Type
    {
        UNKNOWN_VARIANT,
        INVALID_CASE
    }

}
