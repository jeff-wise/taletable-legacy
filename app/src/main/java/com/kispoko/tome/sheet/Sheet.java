
package com.kispoko.tome.sheet;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

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
import java.util.Random;


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

    private Long sheetId;
    private Long lastUsed;

    private Roleplay roleplay;

    private Map<Integer,Component> componentById;

    public static Map<Integer,AsyncConstructor> asyncConstructorMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Sheet(Roleplay roleplay)
    {
        this.roleplay = roleplay;
        this.sheetId = null;

        // Index Components
        componentById = new HashMap<>();
        for (Page page : this.roleplay.getPages())
        {
            ArrayList<Group> groups = page.getGroups();
            for (Group group : groups)
            {
                ArrayList<Component> components = group.getComponents();
                for (Component component : components)
                {
                    componentById.put(component.getId(), component);
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    public static Sheet fromYaml(Map<String, Object> sheetYaml)
    {
        // Sheet name
        String name = (String) sheetYaml.get("name");

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

        return new Sheet(roleplay);
    }




    public static Integer fromAysnc(SheetActivity sheetActivity)
    {
        Random randGen = new Random();
        Integer constructorId = randGen.nextInt();

        Sheet.asyncConstructorMap.put(randGen.nextInt(), new AsyncConstructor(sheetActivity));

        return constructorId;
    }



    // > API
    // ------------------------------------------------------------------------------------------

    // >> Getters / Setters
    // ------------------------------------------------------------------------------------------

    public void setId(Long sheetId)
    {
        this.sheetId = sheetId;
    }


    // >> I/O Methods
    // ------------------------------------------------------------------------------------------

    // >>> Database
    // ------------------------------------------------------------------------------------------

    public static void loadMostRecent(final SQLiteDatabase database,
                                      final SheetActivity sheetActivity)
    {
        new AsyncTask<Void,Void,Long>()
        {

            protected Long doInBackground(Void... args)
            {
                String mostRecentSheetIdQuery =
                    "SELECT sheet_id " +
                    "FROM Sheet " +
                    "ORDER BY datetime(last_used) DESC " +
                    "LIMIT 1";

                Cursor cursor = database.rawQuery(mostRecentSheetIdQuery, null);

                Long sheetId;
                try {
                    cursor.moveToFirst();
                    sheetId = cursor.getLong(0);
                }
                // TODO log
                finally {
                    cursor.close();
                }

                return sheetId;
            }

            protected void onPostExecute(Long mostRecentSheetId)
            {
                // Create an asynchronous Sheet constructor
                Integer sheetConstructorId = Sheet.fromAysnc(sheetActivity);

                // Load the roleplay and have it delivered to the waiting async constructor
                Roleplay.load(database, sheetConstructorId, mostRecentSheetId);
            }

        }.execute();
    }

    /**
     * Save this sheet to the database.
     * @param recursive If true, saves all child objects as well.
     */
    public void save(final SQLiteDatabase database, final boolean recursive)
    {
        // Update last used
        this.lastUsed = System.currentTimeMillis();

        final Sheet thisSheet = this;

        new AsyncTask<Void,Void,Long>()
        {
            protected Long doInBackground(Void... args)
            {
                ContentValues row = new ContentValues();
                row.put("sheet_id", thisSheet.sheetId);
                row.put("last_used", thisSheet.lastUsed);

                Long sheetId = database.insertWithOnConflict(SheetContract.Sheet.TABLE_NAME,
                                                             null,
                                                             row,
                                                             SQLiteDatabase.CONFLICT_REPLACE);

                // Set ID in case of first insert and ID was Null
                thisSheet.setId(sheetId);

                return sheetId;
            }

            protected void onPostExecute(Long sheetId)
            {
                if (!recursive) return;

                // Save the child data to the database as well
                thisSheet.roleplay.save(database, sheetId, true);
            }

        }.execute();

    }


    // >>> Files
    // ------------------------------------------------------------------------------------------

    /**
     *
     * @param sheetActivity
     * @param fileName
     */
    public static void loadFromFile(final SheetActivity sheetActivity, String fileName)
    {
        new AsyncTask<Void,Void,Sheet>()
        {

            protected Sheet doInBackground(Void... args)
            {
                Sheet sheet = null;
                try {
                    InputStream yamlIS = sheetActivity.getAssets().open("sheet/dnd_ed_5.yaml");
                    Yaml yaml = new Yaml();
                    Object yamlObject = yaml.load(yamlIS);
                    sheet = Sheet.fromYaml((Map<String,Object>) yamlObject);
                } catch (IOException e) {
                }

                return sheet;
            }

            protected void onPostExecute(Sheet sheet)
            {
                sheetActivity.setSheet(sheet);
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

    public Component getComponent(String name)
    {
        return this.componentById.get(name);
    }





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
        private SheetActivity sheetActivity;

        public AsyncConstructor(SheetActivity sheetActivity)
        {
            this.sheetActivity = sheetActivity;
        }

        synchronized public void addRoleplay(Roleplay roleplay)
        {
            Sheet sheet = new Sheet(roleplay);

            sheetActivity.setSheet(sheet);
        }

    }

}
