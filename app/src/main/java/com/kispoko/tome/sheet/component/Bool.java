
package com.kispoko.tome.sheet.component;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kispoko.tome.Global;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.sheet.component.table.Cell;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.TrackerId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Boolean Component
 */
public class Bool extends Component implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Boolean value;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Bool(UUID id, UUID groupId) {
        super(id, null, groupId, null, null, null, null);
        this.value = null;
    }

    public Bool(UUID id, String name, UUID groupId, ComponentValue value, Type.Id typeId,
                Format format, List<String> actions)
    {
        super(id, name, groupId, value, typeId, format, actions);
    }


    /**
     * Parse Boolean component from Yaml
     * @param boolYaml Parsed yaml object representing a bool component.
     * @return The parsed Bool.
     */
    @SuppressWarnings("unchecked")
    public static Bool fromYaml(UUID groupId, Map<String, Object> boolYaml)
    {
        // VALUES TO PARSE
        // --------------------------------------------------------------------------------------
        UUID id = UUID.randomUUID();
        String name = null;
        ComponentValue value = null;
        Type.Id typeId = null;
        Format format = null;
        List<String> actions = null;

        // PARSE VALUES
        // --------------------------------------------------------------------------------------

        // > Top Level
        // --------------------------------------------------------------------------------------

        // ** Name
        if (boolYaml.containsKey("name"))
            name = (String) boolYaml.get("name");

        // ** Actions
        if (boolYaml.containsKey("actions"))
            actions = (List<String>) boolYaml.get("actions");

        // >> Data
        // --------------------------------------------------------------------------------------
        Map<String, Object> dataYaml   = (Map<String, Object>) boolYaml.get("data");

        if (dataYaml != null)
        {
            // ** Value
            if (dataYaml.containsKey("value"))
                value = ComponentValue.fromYaml((Map<String,Object>) dataYaml.get("value"));

            // ** Type Id
            typeId = Type.Id.fromYaml(dataYaml);
        }

        // >> Format
        // --------------------------------------------------------------------------------------
        Map<String, Object> formatYaml = (Map<String, Object>) boolYaml.get("format");

        if (formatYaml != null)
        {
            // ** Format
            format = Component.parseFormatYaml(boolYaml);
        }

        return new Bool(id, name, groupId, value, typeId, format, actions);
    }


    // > API
    // ------------------------------------------------------------------------------------------


    public void runAction(String actionName, Context context, Rules rules)
    {

    }


    // >> State
    // ------------------------------------------------------------------------------------------

    public String componentName()
    {
        return "boolean";
    }


    public void setValue(Boolean value, Context context)
    {
        this.value = value;

//        if (context != null) {
//            TextView textView = (TextView) ((Activity) context)
//                                    .findViewById(this.displayTextViewId);
//            textView.setText(this.value);
//        }

        this.save(null);
    }


    // >> Views
    // ------------------------------------------------------------------------------------------

    public View getDisplayView(Context context, Rules rules)
    {
        TextView view = new TextView(context);

        view.setText(this.getValue().toString());

        return view;
    }

    public View getEditorView(Context context, Rules rules) {
        return new TextView(context);
    }


    // >> Database
    // ------------------------------------------------------------------------------------------

    /**
     * Load a Group from the database.
     * @param trackerId The async tracker ID of the caller.
     */
    public void load(final TrackerId trackerId)
    {
        final Bool thisBool = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Query Component
                String boolQuery =
                    "SELECT comp.name, comp.label, comp.show_label, comp.row, comp.column, " +
                           "comp.width, comp.alignment, comp.type_kind, comp.type_id, " +
                           "comp.actions, bool.value " +
                    "FROM component comp " +
                    "INNER JOIN component_boolean bool on bool.component_id = comp.component_id " +
                    "WHERE comp.component_id =  " + SQL.quoted(thisBool.getId().toString());


                Cursor cursor = database.rawQuery(boolQuery, null);

                String name = null;
                String label = null;
                Boolean showLabel = null;
                Integer row = null;
                Integer column = null;
                Integer width = null;
                Alignment alignment = null;
                String typeKind = null;
                String typeId = null;
                Integer keyStat = null;
                List<String> actions = null;
                Boolean value = null;
                try {
                    cursor.moveToFirst();
                    name        = cursor.getString(0);
                    label       = cursor.getString(1);
                    showLabel   = SQL.intAsBool(cursor.getInt(2));
                    row         = cursor.getInt(3);
                    column      = cursor.getInt(4);
                    width       = cursor.getInt(5);
                    alignment   = Alignment.fromString(cursor.getString(6));
                    typeKind    = cursor.getString(7);
                    typeId      = cursor.getString(8);
                    actions     = new ArrayList<>(Arrays.asList(
                                            TextUtils.split(cursor.getString(9), ",")));
                    value       = cursor.getInt(10) != 0;
                } catch (Exception e) {
                    Log.d("***BOOL", Log.getStackTraceString(e));
                }
                finally {
                    cursor.close();
                }

                thisBool.setName(name);
                thisBool.setTypeId(new Type.Id(typeKind, typeId));
                thisBool.setLabel(label);
                thisBool.setShowLabel(showLabel);
                thisBool.setRow(row);
                thisBool.setColumn(column);
                thisBool.setWidth(width);
                thisBool.setAlignment(alignment);
                thisBool.setActions(actions);
                thisBool.setValue(new ComponentValue());

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                TrackerId textTrackerId = thisBool.addComponentAsyncTracker(trackerId);

                thisBool.getValue().load(thisBool, textTrackerId);
            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param trackerId The async tracker ID for the caller.
     */
    public void save(final TrackerId trackerId)
    {
        final Bool thisBool = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // > Update Component Row
                // ------------------------------------------------------------------------------
                ContentValues componentRow = new ContentValues();

                thisBool.putComponentSQLRows(componentRow);

                SQL.putOptString(componentRow, "text_value", thisBool.getValue());

                database.insertWithOnConflict(SheetContract.Component.TABLE_NAME,
                        null,
                        componentRow,
                        SQLiteDatabase.CONFLICT_REPLACE);

                // > Update ComponentBoolean Row
                // ------------------------------------------------------------------------------
                ContentValues boolComponentRow = new ContentValues();

                boolComponentRow.put("component_id", thisBool.getId().toString());

                if (thisBool.getValue() != null)
                    boolComponentRow.put("value", thisBool.getValue().getBoolean());
                else
                    boolComponentRow.putNull("value");

                database.insertWithOnConflict(SheetContract.ComponentBoolean.TABLE_NAME,
                        null,
                        boolComponentRow,
                        SQLiteDatabase.CONFLICT_REPLACE);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                TrackerId textTrackerId = thisBool.addComponentAsyncTracker(trackerId);

                thisBool.getValue().save(thisBool.getId(), textTrackerId);
            }

        }.execute();
    }


}
