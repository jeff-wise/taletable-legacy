
package com.kispoko.tome.util;


import com.kispoko.tome.error.InvalidEnumError;
import com.kispoko.tome.exception.InvalidDataException;



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
            return Enum.valueOf(enumClass, enumString);
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException(new InvalidEnumError(enumString),
                                           InvalidDataException.ErrorType.INVALID_ENUM);
        }
    }
}
