
package com.kispoko.tome;


import android.util.Log;

import com.kispoko.tome.model.game.engine.mechanic.MechanicException;
import com.kispoko.tome.model.game.engine.summation.SummationException;
import com.kispoko.tome.model.game.engine.variable.NullVariableException;
import com.kispoko.tome.model.game.engine.variable.VariableException;
import com.kispoko.tome.exception.TemplateFileException;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.lib.functor.FunctorException;
import com.kispoko.tome.model.sheet.SheetException;
import com.kispoko.tome.lib.database.DatabaseException;



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


    public static void database(DatabaseException exception)
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


    public static void functor(FunctorException exception)
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


    public static void union(UnionException exception)
    {
        Log.d("***APP", exception.errorMessage(), exception);
    }


}
