
package com.kispoko.tome.sheet;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kispoko.tome.Global;
import com.kispoko.tome.activity.ManageSheetsActivity;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.TrackerId;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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

    private Game game;
    private Roleplay roleplay;
    private Rules rules;

    private Map<UUID,Component> componentById;
    private Map<String,Component> componentByLabel;

    // >> STATIC
    //private static Map<UUID,AsyncConstructor> asyncConstructorMap = new HashMap<>();

    private static Map<UUID,AsyncTracker> asyncTrackerMap = new HashMap<>();



    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Sheet(UUID id, String gameId)
    {
        this.id = id;

        this.game = new Game(gameId);
        this.roleplay = new Roleplay(id);
        this.rules = new Rules(id);
    }

    public Sheet(UUID id, Game game, Roleplay roleplay, Rules rules)
    {
        if (id != null)
            this.id = id;
        else
            this.id = UUID.randomUUID();

        this.game = game;
        this.roleplay = roleplay;
        this.rules = rules;

        indexComponents();
    }


    @SuppressWarnings("unchecked")
    public static Sheet fromYaml(Map<String, Object> sheetYaml)
    {
        // Values to parse
        UUID sheetId = UUID.randomUUID();
        Game game = null;
        Roleplay roleplay;
        Rules rules = new Rules(sheetId);

        // Parse Values
        Map<String,Object> rulesYaml = (Map<String,Object>) sheetYaml.get("rules");
        Map<String,Object> sectionsYaml = (Map<String,Object>) sheetYaml.get("sections");
        Map<String,Object> roleplayYaml = (Map<String,Object>) sectionsYaml.get("roleplay");

        // >> Rules

        // >>> Types
        ArrayList<Map<String,Object>> typesYaml =
                            (ArrayList<Map<String,Object>>) rulesYaml.get("types");

        for (Map<String,Object> typeYaml : typesYaml) {
            Type typ = Type.fromYaml(typeYaml);
            rules.getTypes().addType(typ);
        }

        // >> Game
        if (sheetYaml.containsKey("game"))
            game = Game.fromYaml((Map<String,Object>) sheetYaml.get("game"));

        // >> Roleplay
        roleplay = Roleplay.fromYaml(sheetId, roleplayYaml);

        return new Sheet(sheetId, game, roleplay, rules);
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
    private TrackerId addAsyncTracker(SheetActivity sheetActivity)
    {
        UUID trackerCode = UUID.randomUUID();
        Sheet.asyncTrackerMap.put(trackerCode, new AsyncTracker(sheetActivity));
        return new TrackerId(trackerCode, TrackerId.Target.SHEET);
    }


    public static AsyncTracker getAsyncTracker(UUID trackerCode)
    {
        return Sheet.asyncTrackerMap.get(trackerCode);
    }


    // >> Asynchronous Constructor
    // ------------------------------------------------------------------------------------------

//    private static UUID addAsyncConstructor(UUID id, SheetActivity sheetActivity)
//    {
//        UUID constructorId = UUID.randomUUID();
//        Sheet.asyncConstructorMap.put(constructorId, new AsyncConstructor(id, sheetActivity));
//        return constructorId;
//    }
//
//
//    public static AsyncConstructor getAsyncConstructor(UUID constructorId)
//    {
//        return Sheet.asyncConstructorMap.get(constructorId);
//    }


    // >> State
    // ------------------------------------------------------------------------------------------

    public UUID getId() {
        return this.id;
    }


    public Component componentWithId(UUID componentId) {
        return this.componentById.get(componentId);
    }


    public Component componentWithLabel(String componentLabel) {
        return this.componentByLabel.get(componentLabel.toLowerCase());
    }


    public Roleplay getRoleplay() {
        return this.roleplay;
    }


    public Game getGame() {
        return this.game;
    }


    public Rules getRules() {
        return this.rules;
    }


    // >> I/O Methods
    // ------------------------------------------------------------------------------------------

    // >>> Database
    // ------------------------------------------------------------------------------------------

    // >>>> Save/Load
    // ------------------------------------------------------------------------------------------

    public static void loadMostRecent(final SheetActivity sheetActivity)
    {
        new AsyncTask<Void,Void,Sheet>()
        {

            protected Sheet doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                String mostRecentSheetIdQuery =
                    "SELECT sheet.sheet_id, sheet.game_id " +
                    "FROM sheet " +
                    "ORDER BY datetime(sheet.last_used) DESC " +
                    "LIMIT 1";

                Cursor cursor = database.rawQuery(mostRecentSheetIdQuery, null);

                UUID sheetId;
                String gameId;
                try {
                    cursor.moveToFirst();
                    sheetId = UUID.fromString(cursor.getString(0));
                    gameId  = cursor.getString(1);
                }
                finally {
                    cursor.close();
                }

                return new Sheet(sheetId, gameId);
            }

            protected void onPostExecute(Sheet sheet)
            {
                // Create an asynchronous Sheet constructor
                TrackerId sheetTrackerId = sheet.addAsyncTracker(sheetActivity);

                // Load the sheet components
                sheet.getGame().load(sheetTrackerId);
                sheet.getRoleplay().load(sheetTrackerId);
                sheet.getRules().load(sheetTrackerId);
            }

        }.execute();
    }

    /**
     * Save this sheet to the database.
     * @param recursive If true, saves all child objects as well.
     */
    public void save(final SheetActivity sheetActivity, final boolean recursive)
    {
        // Update last used
        this.lastUsed = System.currentTimeMillis();

        Log.d("***SHEET", "sheet save called");

        final Sheet thisSheet = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Insert Sheet Row
                // -----------------------------------------------------------------------------
                ContentValues row = new ContentValues();
                row.put("sheet_id", thisSheet.getId().toString());
                row.put("last_used", thisSheet.lastUsed);
                row.put("game_id", thisSheet.getGame().getId());

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

                TrackerId sheetTrackerId = thisSheet.addAsyncTracker(sheetActivity);

                // Save the child data to the database as well
                thisSheet.roleplay.save(sheetTrackerId, true);
                thisSheet.rules.save(sheetTrackerId, true);
                thisSheet.game.save(sheetTrackerId);
            }

        }.execute();

    }


    // >>>> Queries
    // ------------------------------------------------------------------------------------------

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


    /**
     * Query basic information about the stored sheets.
     * @param database The SQLite database object.
     * @return Array of sheet summary info objects.
     */
    public static void summaryInfo(final SQLiteDatabase database,
                                   final ManageSheetsActivity manageSheetsActivity)
    {

        final String summaryInfoQuery =
            "SELECT sh.last_used, cname.text_value, cstat1.label, cstat1.text_value, cstat2.label, " +
                   "cstat2.text_value, cstat3.label, cstat3.text_value " +
            "FROM sheet sh " +
            "INNER JOIN page p ON p.sheet_id = sh.sheet_id " +
            "INNER JOIN _group g ON g.page_id = p.page_id " +
            "INNER JOIN component cname ON (cname.group_id = g.group_id and cname.label = 'Name') " +
            "LEFT JOIN component cstat1 ON (cstat1.group_id = g.group_id and cstat1.key_stat = 1) " +
            "LEFT JOIN component cstat2 ON (cstat2.group_id = g.group_id and cstat2.key_stat = 2) " +
            "LEFT JOIN component cstat3 ON (cstat3.group_id = g.group_id and cstat3.key_stat = 3) " +
            "ORDER BY sh.last_used DESC ";


        new AsyncTask<Void,Void,List<SummaryInfo>>()
        {

            @Override
            protected List<SummaryInfo> doInBackground(Void... args)
            {
                Cursor summaryInfoCursor = database.rawQuery(summaryInfoQuery, null);

                ArrayList<SummaryInfo> summaryInfos = new ArrayList<>();
                try {
                    while (summaryInfoCursor.moveToNext())
                    {
                        Calendar lastUsed = Calendar.getInstance();
                        lastUsed.setTimeInMillis(summaryInfoCursor.getLong(0));
                        String name       = summaryInfoCursor.getString(1);
                        String stat1Name  = summaryInfoCursor.getString(2);
                        String stat1Value = summaryInfoCursor.getString(3);
                        String stat2Name  = summaryInfoCursor.getString(4);
                        String stat2Value = summaryInfoCursor.getString(5);
                        String stat3Name  = summaryInfoCursor.getString(6);
                        String stat3Value = summaryInfoCursor.getString(7);

                        summaryInfos.add(new SummaryInfo(name, lastUsed, stat1Name, stat1Value, stat2Name,
                                                         stat2Value, stat3Name, stat3Value));
                    }
                }
                finally {
                    summaryInfoCursor.close();
                }

                return summaryInfos;
            }

            @Override
            protected void onPostExecute(List<SummaryInfo> summaryInfos)
            {
                manageSheetsActivity.renderSheetSummaries(summaryInfos);
            }

        }.execute();

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
     * @return ListType of template games.
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


    // > INTERNAL
    // ------------------------------------------------------------------------------------------

    private void indexComponents()
    {
        // Index components
        componentById = new HashMap<>();
        componentByLabel = new HashMap<>();

        for (Page page : this.roleplay.getPages())
        {
            for (Group group : page.getGroups())
            {
                for (Component component : group.getComponents())
                {
                    componentById.put(component.getId(), component);

                    if (component.hasLabel())
                        componentByLabel.put(component.getLabel().toLowerCase(), component);
                }
            }
        }

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


    /**
     * Stores summary information about a sheet for the user to browse.
     */
    public static class SummaryInfo implements Serializable
    {
        private String name;
        private Calendar lastUsed;

        private String stat1Name;
        private String stat1Value;
        private String stat2Name;
        private String stat2Value;
        private String stat3Name;
        private String stat3Value;

        public SummaryInfo(String name, Calendar lastUsed, String stat1Name, String stat1Value,
                           String stat2Name, String stat2Value, String stat3Name, String stat3Value)
        {
            this.name = name;
            this.lastUsed = lastUsed;
        }

        public String getName() {
            return this.name;
        }

        public Calendar getLastUsed() {
            return this.lastUsed;
        }

        public String getStat1Name() {
            return this.stat1Name;
        }

        public String getStat2Name() {
            return this.stat2Name;
        }

        public String getStat2Value() {
            return this.stat2Value;
        }

        public String getStat3Name() {
            return this.stat3Name;
        }

        public String getStat3Value() {
            return this.stat3Value;
        }
    }


    /*
    public static class AsyncConstructor
    {
        private UUID id;
        private Roleplay roleplay;
        private Game game;
        private Rules rules;

        private SheetActivity sheetActivity;

        public AsyncConstructor(UUID id, SheetActivity sheetActivity)
        {
            this.id = id;
            this.sheetActivity = sheetActivity;

            this.roleplay = null;
            this.game = null;
        }

        synchronized public void setGame(Game game) {
            this.game = game;
            if (isReady()) ready();
        }

        synchronized public void setRoleplay(Roleplay roleplay) {
            this.roleplay = roleplay;
            if (isReady()) ready();
        }

        synchronized public void setRules(Rules rules) {
            this.rules = rules;
            if (isReady()) ready();
        }

        private boolean isReady() {
            return this.game != null &&
                   this.roleplay != null &&
                   this.rules != null;
        }

        private void ready()
        {
            Sheet sheet = new Sheet(this.id, this.game, this.roleplay, this.rules);
            sheetActivity.setSheet(sheet);
            sheetActivity.renderSheet();
        }

    }
    */


    /**
     * Track state of Sheet.
     */
    public static class AsyncTracker
    {
        private SheetActivity sheetActivity;

        private boolean game;
        private boolean roleplay;
        private boolean rules;

        public AsyncTracker(SheetActivity sheetActivity)
        {
            this.sheetActivity = sheetActivity;
            this.roleplay = game;
            this.roleplay = false;
            this.rules = false;
        }

        synchronized public void markGame() {
            Log.d("***SHEET", "mark game");
            this.game = true;
            if (isReady()) ready();
        }

        synchronized public void markRoleplay() {
            Log.d("***SHEET", "mark roleplay");
            this.roleplay = true;
            if (isReady()) ready();
        }

        synchronized public void markRules() {
            Log.d("***SHEET", "mark rules");
            this.rules = true;
            if (isReady()) ready();
        }

        private boolean isReady() {
            return this.roleplay &&
                   this.rules &&
                   this.game;
        }

        private void ready() {
            Log.d("***SHEET", "sheet is ready to render");
            this.sheetActivity.renderSheet();
        }

    }

}
