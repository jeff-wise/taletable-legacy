
package com.kispoko.tome.sheet;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kispoko.tome.Global;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.TrackerId;

import java.util.Map;



/**
 * Game
 */
public class Game
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String id;
    private String label;
    private String description;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Game(String id)
    {
        this.id = id;
        this.label = null;
        this.description = null;
    }


    public Game(String id, String label, String description)
    {
        this.id = id;
        this.label = label;
        this.description = description;
    }


    public static Game fromYaml(Map<String,Object> gameYaml)
    {
        // Values to parse
        String id = null;
        String label = null;
        String description = null;

        // Parse values
        // >> Id
        if (gameYaml.containsKey("id"))
            id = (String) gameYaml.get("id");

        // >> Label
        if (gameYaml.containsKey("label"))
            label = (String) gameYaml.get("label");

        // >> Description
        if (gameYaml.containsKey("description"))
            description = (String) gameYaml.get("description");


        return new Game(id, label, description);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    // >> State
    // ------------------------------------------------------------------------------------------

    public String getId()
    {
        return this.id;
    }


    // >>> Label
    // ------------------------------------------------------------------------------------------

    public String getLabel() {
        return this.label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    // >>> Description
    // ------------------------------------------------------------------------------------------

    public String getDescription() {
        return this.description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    // >> Database
    // ------------------------------------------------------------------------------------------

    public void load(final TrackerId sheetTrackerId)
    {
        final Game thisGame = this;

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Query for the game
                String gameQuery =
                    "SELECT game.label, game.description " +
                    "FROM game " +
                    "WHERE game.game_id =  " + SQL.quoted(thisGame.getId());

                Cursor gameCursor = database.rawQuery(gameQuery, null);

                String label;
                String description;
                try {
                    gameCursor.moveToFirst();
                    label = gameCursor.getString(0);
                    description = gameCursor.getString(1);
                } finally {
                    gameCursor.close();
                }

                thisGame.setLabel(label);
                thisGame.setDescription(description);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                Sheet.getAsyncTracker(sheetTrackerId.getCode()).markGame();
            }

        }.execute();
    }


    public void save(final TrackerId sheetTrackerId)
    {

        final Game thisGame = this;

        new AsyncTask<Void,Void,Boolean>() {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Insert Game Row (if doesn't exist)
                // -----------------------------------------------------------------------------
                ContentValues gameRow = new ContentValues();
                gameRow.put("game_id", thisGame.getId());
                gameRow.put("label", thisGame.getLabel());

                if (thisGame.getDescription() != null)
                    gameRow.put("description", thisGame.getId());
                else
                    gameRow.putNull("description");

                database.insertWithOnConflict(SheetContract.Game.TABLE_NAME,
                        null,
                        gameRow,
                        SQLiteDatabase.CONFLICT_REPLACE);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                Log.d("***GAME", "saved game");
                Sheet.getAsyncTracker(sheetTrackerId.getCode()).markGame();
            }
        }.execute();
    }
}

