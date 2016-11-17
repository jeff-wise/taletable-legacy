
package com.kispoko.tome;


import android.util.Log;

import com.kispoko.tome.exception.TemplateFileException;
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
    }


    public static void yaml(YamlException exception)
    {
        Log.d("***APP", "yaml exception");
    }


    public static void database(DatabaseException exception)
    {
        Log.d("***APP", "database exception");
    }


    public static void invalidEnum(IllegalArgumentException exception)
    {
        Log.d("***APP", "invalid enum");
        exception.printStackTrace();
    }
}
