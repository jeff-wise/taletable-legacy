
package com.kispoko.tome.activity;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kispoko.tome.DatabaseManager;
import com.kispoko.tome.Global;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.database.query.CountQuery;


/**
 * Launch Activity
 *
 * This activity displays nothing. It looks for existing characters, and if it finds one, it starts
 * the sheet activity with the last used character. If none are found, it starts the new character
 * activity.
 */
public class LaunchActivity extends AppCompatActivity
                            implements CountQuery.OnCountListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SQL.initialize();

        // Create database reference and save it for use in application lifecycle.
        DatabaseManager databaseManager = new DatabaseManager(this);
        SQLiteDatabase database = databaseManager.getWritableDatabase();
        Global.setDatabase(database);

        CountQuery.fromModel(Sheet.class).run(this);
    }


    // LISTENERS
    // ------------------------------------------------------------------------------------------

    // > On Count
    // ------------------------------------------------------------------------------------------

    public void onCountResult(String modelName, Integer result)
    {

        Intent intent;
        // No characters exist, go to New Character Activity
        if (result == 0)
        {
            intent = new Intent(this, NewCharacterActivity.class);
        }
        // Characters found, go to sheet of last used character
        else
        {
            intent = new Intent(this, SheetActivity.class);
        }

        startActivity(intent);
    }


    public void onCountError(DatabaseException exception)
    {
        // TODO handle properly
    }




}
