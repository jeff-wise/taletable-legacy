
package com.kispoko.tome.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


/**
 * Launch Activity
 *
 * This activity displays nothing. It looks for existing characters, and if it finds one, it starts
 * the sheet activity with the last used character. If none are found, it starts the new character
 * activity.
 */
public class LaunchActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //SheetDatabase sheetDatabase = new SheetDatabase();

        Intent intent;
        // No characters exist, go to New Character Activity
        if (SheetDatabase.size() == 0)
        {
            intent = new Intent(this, NewCharacterActivity.class);
        }
        // Characters found, go to sheet of last used character
        else
        {
            intent = new Intent(this, SheetActivity.class);
        }

        startActivity(intent);
        finish();
    }

}
