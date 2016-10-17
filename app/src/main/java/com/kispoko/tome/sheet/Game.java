
package com.kispoko.tome.sheet;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.tuple.Tuple3;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static android.R.attr.data;

/**
 * Game
 */
public class Game {

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String id;
    private String label;
    private String description;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Game(String id, String label, String description) {
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

    // >> Getters/Setters
    // ------------------------------------------------------------------------------------------

    public String getId()
    {
        return this.id;
    }

    public String getLabel()
    {
        return this.label;
    }

    public String getDescription()
    {
        return this.description;
    }


    // >> Database
    // ------------------------------------------------------------------------------------------

    public static void load(final SQLiteDatabase database, final UUID sheetConstructorId,
                            final String gameId) {
        new AsyncTask<Void, Void, Game>() {

            @Override
            protected Game doInBackground(Void... args) {
                // Query for the game
                String gameQuery =
                        "SELECT game.label, game.description " +
                                "FROM game " +
                                "WHERE game.game_id =  " + SQL.quoted(gameId);

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

                return new Game(gameId, label, description);
            }

            @Override
            protected void onPostExecute(Game game) {
                Sheet.getAsyncConstructor(sheetConstructorId).setGame(game);
            }

        }.execute();
    }
}

