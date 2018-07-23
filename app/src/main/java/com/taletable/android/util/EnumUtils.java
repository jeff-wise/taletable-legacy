
package com.taletable.android.util;


import com.taletable.android.error.InvalidEnumError;
import com.taletable.android.exception.InvalidDataException;



/**
 * Enum Utility Functions
 */
public class EnumUtils
{


    public static <A extends Enum<A>> A fromString(Class<A> enumClass, String enumString)
                                      throws InvalidDataException
    {
        if (enumString == null)
            return null;

        try {
            return Enum.valueOf(enumClass, enumString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException(new InvalidEnumError(enumString),
                                           InvalidDataException.ErrorType.INVALID_ENUM);
        }
    }
}
