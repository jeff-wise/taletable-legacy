
package com.taletable.android.model.sheet;


import com.taletable.android.model.sheet.error.UndefinedValueSetError;
import com.taletable.android.util.ApplicationError;


/**
 * Sheet Exception
 */
public class SheetException extends Exception
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    ApplicationError error;
    ErrorType        errorType;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    private SheetException(ApplicationError error, ErrorType errorType)
    {
        this.error     = error;
        this.errorType = errorType;
    }


    public static SheetException undefinedValueSet(UndefinedValueSetError error)
    {
        return new SheetException(error, ErrorType.UNDEFINED_VALUE_SET);
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Error Message
    // -----------------------------------------------------------------------------------------


    public String errorMessage()
    {
        StringBuilder errorBuilder = new StringBuilder();
        errorBuilder.append("Sheet Error: ");

        errorBuilder.append(this.error.errorMessage());

        return errorBuilder.toString();
    }


    // NESTED DEFINITIONS
    // -----------------------------------------------------------------------------------------

    public enum ErrorType
    {
        UNDEFINED_VALUE_SET,
    }


}
