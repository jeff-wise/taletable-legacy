
package com.kispoko.tome.sheet;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.rules.RulesEngine;
import com.kispoko.tome.type.Type;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;



/**
 * Character Sheet
 *
 * This class represents the structure and representation of character sheet. Character sheets
 * can therefore be customized for different roleplaying games or even different campaigns.
 */
public class Sheet
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;
    private Long lastUsed;

    private Roleplay roleplay;

    private Map<UUID,Component> componentById;

    // >> STATIC
    private static Map<UUID,AsyncConstructor> asyncConstructorMap = new HashMap<>();

    private static Map<UUID,SaveTracker> trackerMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Sheet(UUID id, Roleplay roleplay)
    {
        if (id != null)
            this.id = id;
        else
            this.id = UUID.randomUUID();

        this.roleplay = roleplay;

        // Index components
        componentById = new HashMap<>();

        for (Page page : this.roleplay.getPages())
        {
            for (Group group : page.getGroups())
            {
                for (Component component : group.getComponents())
                {
                    componentById.put(component.getId(), component);
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    public static Sheet fromYaml(Map<String, Object> sheetYaml)
    {
        // Types
        ArrayList<Map<String,Object>> typesYaml =
                (ArrayList<Map<String,Object>>) sheetYaml.get("types");

        for (Map<String,Object> typeYaml : typesYaml)
        {
            Type typ = Type.fromYaml(typeYaml);
            RulesEngine.addType(typ);
        }

        // Sheet sections
        Map<String,Object> sections = (Map<String,Object>) sheetYaml.get("sections");

        // Roleplay section
        Map<String,Object> roleplayYaml = (Map<String,Object>) sections.get("roleplay");
        Roleplay roleplay = Roleplay.fromYaml(roleplayYaml);

        return new Sheet(null, roleplay);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    // >> Tracking
    // ------------------------------------------------------------------------------------------

    /**
     * Create a tracker for asynchronously tracking the state of a sheet.
     * @param sheetActivity SheetActivity object.
     * @return The new tracker's ID.
     */
    private static UUID addTracker(SheetActivity sheetActivity)
    {
        UUID trackerId = UUID.randomUUID();
        Sheet.trackerMap.put(trackerId, new SaveTracker(sheetActivity));
        return trackerId;
    }


    public static SaveTracker getTracker(UUID trackerId)
    {
        return Sheet.trackerMap.get(trackerId);
    }


    // >> Asynchronous Constructor
    // ------------------------------------------------------------------------------------------

    private static UUID addAsyncConstructor(UUID id, SheetActivity sheetActivity)
    {
        UUID constructorId = UUID.randomUUID();
        Sheet.asyncConstructorMap.put(constructorId, new AsyncConstructor(id, sheetActivity));
        return constructorId;
    }


    public static AsyncConstructor getAsyncConstructor(UUID constructorId)
    {
        return Sheet.asyncConstructorMap.get(constructorId);
    }




    // >> State
    // ------------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public Component componentWithId(UUID componentId)
    {
        return this.componentById.get(componentId);
    }


    public Roleplay getRoleplay()
    {
        return this.roleplay;
    }





    // >> I/O Methods
    // ------------------------------------------------------------------------------------------

    // >>> Database
    // ------------------------------------------------------------------------------------------

    public static void loadMostRecent(final SQLiteDatabase database,
                                      final SheetActivity sheetActivity)
    {
        new AsyncTask<Void,Void,UUID>()
        {

            protected UUID doInBackground(Void... args)
            {
                String mostRecentSheetIdQuery =
                    "SELECT sheet_id " +
                    "FROM Sheet " +
                    "ORDER BY datetime(last_used) DESC " +
                    "LIMIT 1";

                Cursor cursor = database.rawQuery(mostRecentSheetIdQuery, null);

                UUID sheetId;
                try {
                    cursor.moveToFirst();
                    sheetId = UUID.fromString(cursor.getString(0));
                }
                // TODO log
                finally {
                    cursor.close();
                }

                return sheetId;
            }

            protected void onPostExecute(UUID sheetId)
            {
                // Create an asynchronous Sheet constructor
                UUID sheetConstructorId = Sheet.addAsyncConstructor(sheetId, sheetActivity);

                // Load the roleplay and have it delivered to the waiting async constructor
                Roleplay.load(database, sheetConstructorId, sheetId);
            }

        }.execute();
    }

    /**
     * Save this sheet to the database.
     * @param recursive If true, saves all child objects as well.
     */
    public void save(final SQLiteDatabase database, final SheetActivity sheetActivity,
                     final boolean recursive)
    {
        // Update last used
        this.lastUsed = System.currentTimeMillis();

        final Sheet thisSheet = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                ContentValues row = new ContentValues();
                row.put("sheet_id", thisSheet.getId().toString());
                row.put("last_used", thisSheet.lastUsed);

                database.insertWithOnConflict(SheetContract.Sheet.TABLE_NAME,
                                              null,
                                              row,
                                              SQLiteDatabase.CONFLICT_REPLACE);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                if (!recursive) return;

                UUID sheetTrackerId = Sheet.addTracker(sheetActivity);

                // Save the child data to the database as well
                thisSheet.roleplay.save(database, thisSheet.getId(), sheetTrackerId, true);
            }

        }.execute();

    }


    public static Integer count(SQLiteDatabase database)
    {
        String sheetCountQuery = "SELECT count(*) FROM Sheet";

        Cursor cursor = database.rawQuery(sheetCountQuery, null);

        Integer count;
        try {
            cursor.moveToFirst();
            count = cursor.getInt(0);
        }
        finally {
            cursor.close();
        }

        return count;
    }


    // >>> Files
    // ------------------------------------------------------------------------------------------

    /**
     *
     * @param sheetActivity
     * @param templateId The ID of the template yaml file to load.
     */
    public static void loadFromFile(final SheetActivity sheetActivity, String templateId)
    {
        final String templateFileName = "template/" + templateId + ".yaml";
        new AsyncTask<Void,Void,Sheet>()
        {

            protected Sheet doInBackground(Void... args)
            {
                Sheet sheet = null;
                try {
                    InputStream yamlIS = sheetActivity.getAssets().open(templateFileName);
                    Yaml yaml = new Yaml();
                    Object yamlObject = yaml.load(yamlIS);
                    sheet = Sheet.fromYaml((Map<String,Object>) yamlObject);
                } catch (IOException e) {
                    Log.d("***sheet", Log.getStackTraceString(e));
                }

                return sheet;
            }

            protected void onPostExecute(Sheet sheet)
            {
                sheetActivity.setSheet(sheet);
                sheetActivity.saveSheet(true);
            }

        }.execute();
    }


    @SuppressWarnings("unchecked")
    public static Map<String, ArrayList<Name>> templateNamesByGame(Context context)
    {
        Map<String, ArrayList<Name>> namesByGame = new HashMap<>();

        try
        {
            InputStream yamlIS = context.getAssets().open("template/manifest.yaml");
            Yaml yaml = new Yaml();
            Map<String,Object> yamlObject = (Map<String,Object>) yaml.load(yamlIS);

            ArrayList<Map<String,Object>> templatesYaml =
                    (ArrayList<Map<String,Object>>) yamlObject.get("templates");

            for (Map<String,Object> templateYaml : templatesYaml)
            {
                String gameId = (String) templateYaml.get("game");

                Map<String,Object> templateDataYaml =
                        (Map<String,Object>) templateYaml.get("template");

                String id = (String) templateDataYaml.get("id");
                String label = (String) templateDataYaml.get("label");
                String description = (String) templateDataYaml.get("description");

                if (!namesByGame.containsKey(gameId))
                    namesByGame.put(gameId, new ArrayList<Name>());

                ArrayList<Name> names = namesByGame.get(gameId);
                names.add(new Name(id, label, description));
            }
        }
        catch (IOException e)
        {
            // TODO
        }

        return namesByGame;
    }


    /**
     * Read the template manifest file and retrive the list of games available.
     * @param context Context for accessing assets.
     * @return List of template games.
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Game> templateGames(Context context)
    {
        ArrayList<Game> games = new ArrayList<>();

        try
        {
            InputStream yamlIS = context.getAssets().open("template/manifest.yaml");
            Yaml yaml = new Yaml();
            Map<String,Object> yamlObject = (Map<String,Object>) yaml.load(yamlIS);

            ArrayList<Map<String,Object>> gamesYaml =
                    (ArrayList<Map<String,Object>>) yamlObject.get("games");
            for (Map<String,Object> gameYaml : gamesYaml)
            {
                String id  = (String) gameYaml.get("id");
                String label = (String) gameYaml.get("label");
                String description = (String) gameYaml.get("description");
                games.add(new Game(id, label, description));
            }
        }
        catch (IOException e)
        {
            // TODO
        }

        return games;
    }


    public static String officialTemplateId(String gameId, String templateName)
    {
        return "official_" + gameId + "_" + templateName;
    }



    // >> View Methods
    // ------------------------------------------------------------------------------------------





    // > NESTED TYPES
    // ------------------------------------------------------------------------------------------


    public static class Name
    {
        private String name;
        private String label;
        private String description;

        public Name(String name, String label, String description)
        {
            this.name = name;
            this.label = label;
            this.description = description;
        }

        public String getName()
        {
            return this.name;
        }

        public String getLabel()
        {
            return this.label;
        }

        public String getDescription()
        {
            return this.description;
        }

    }


    public static class Game
    {
        private String id;
        private String label;
        private String description;

        public Game(String id, String label, String description)
        {
            this.id = id;
            this.label = label;
            this.description = description;
        }

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
    }


    public static class AsyncConstructor
    {
        private UUID id;
        private SheetActivity sheetActivity;

        public AsyncConstructor(UUID id, SheetActivity sheetActivity)
        {
            this.id = id;
            this.sheetActivity = sheetActivity;
        }

        synchronized public void addRoleplay(Roleplay roleplay)
        {
            Sheet sheet = new Sheet(this.id, roleplay);

            sheetActivity.setSheet(sheet);
            sheetActivity.renderSheet();
        }

    }


    /**
     * Track state of Sheet.
     */
    public static class SaveTracker
    {
        private SheetActivity sheetActivity;

        private boolean roleplay;

        public SaveTracker(SheetActivity sheetActivity)
        {
            this.sheetActivity = sheetActivity;
            this.roleplay = false;
        }

        synchronized public void setRoleplay()
        {
            this.roleplay = true;
            Log.d("***sheet", "set roleplay");
            this.sheetActivity.renderSheet();
        }

    }

}
