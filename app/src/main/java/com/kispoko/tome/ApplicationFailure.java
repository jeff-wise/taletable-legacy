
package com.kispoko.tome;


import android.util.Log;

import com.kispoko.tome.engine.mechanic.MechanicException;
import com.kispoko.tome.engine.summation.SummationException;
import com.kispoko.tome.engine.value.ValueException;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.VariableException;
import com.kispoko.tome.exception.TemplateFileException;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.engine.interpreter.InterpreterException;
import com.kispoko.tome.engine.function.InvalidFunctionException;
import com.kispoko.tome.sheet.SheetException;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.yaml.YamlParseException;



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
        Log.d("***APP", "template file exception", exception);
    }


    public static void yaml(YamlParseException exception)
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


    public static void mechanic(MechanicException exception)
    {
        Log.d("***APP", exception.errorMessage(), exception);
    }


    public static void variable(VariableException exception)
    {
        Log.d("***APP", exception.errorMessage(), exception);
    }


    public static void summation(SummationException exception)
    {
        Log.d("***APP", exception.errorMessage(), exception);
    }


    public static void sheet(SheetException exception)
    {
        Log.d("***APP", exception.errorMessage(), exception);
    }


    public static void nullVariable(NullVariableException exception)
    {
        Log.d("***APP", "null variable exception");
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


    public static void value(ValueException exception)
    {
        Log.d("***APP", exception.errorMessage(), exception);
    }


}
