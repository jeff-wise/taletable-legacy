
package com.kispoko.tome.lib.database.sql;


import android.text.TextUtils;

import java.util.List;



/**
 * SQL: Function
 */
public enum Function
{

    DATETIME;


    public static String applyFunctionString(Function function, List<String> parameters)
    {
        String parametersString = TextUtils.join(", ", parameters);
        String functionString   = function.name().toLowerCase();

        return functionString + "(" + parametersString + ")";
    }

}
