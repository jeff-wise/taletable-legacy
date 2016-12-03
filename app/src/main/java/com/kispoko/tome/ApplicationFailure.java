
package com.kispoko.tome;


import android.util.Log;

import com.kispoko.tome.exception.TemplateFileException;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.rules.programming.interpreter.InterpreterException;
import com.kispoko.tome.rules.programming.function.InvalidFunctionException;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.yaml.YamlException;



/**
 * Application Exception
 */
public class ApplicationFailure
{


    public static void handle()
    {

    }


    public static void templateFile(TemplateFileException exception)
    {
        Log.d("***APP", "template file exception");
        exception.printStackTrace();
    }


    public static void yaml(YamlException exception)
    {
        Log.d("***APP", exception.errorMessage(), exception);
    }


    public static void database(DatabaseException exception)
    {
        Log.d("***APP", exception.errorMessage(), exception);
    }


    public static void interpreter(InterpreterException exception)
    {
        Log.d("***APP", exception.errorMessage(), exception);
    }


    public static void invalidEnum(IllegalArgumentException exception)
    {
        Log.d("***APP", "invalid enum");
        exception.printStackTrace();
    }


    public static void invalidFunction(InvalidFunctionException exception)
    {
        Log.d("***APP", "invalid function");
        exception.printStackTrace();
    }


    public static void union(UnionException exception)
    {
        Log.d("***APP", exception.errorMessage(), exception);
    }

}
