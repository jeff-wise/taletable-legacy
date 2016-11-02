
package com.kispoko.tome.rules;



/**
 * Function Value Type
 */
public enum FunctionValueType
{
    INTEGER,
    STRING;

    public static FunctionValueType fromString(String functionValueType)
    {
        if (functionValueType != null)
            return FunctionValueType.valueOf(functionValueType.toUpperCase());
        return null;
    }

}
