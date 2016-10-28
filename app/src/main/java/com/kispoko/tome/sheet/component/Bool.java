
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
        super(id, groupId, null, null, null);
        this.value = null;
    }

    public Bool(UUID id, UUID groupId, Type.Id typeId, Format format, List<String> actions,
                Boolean value)
    {
        super(id, groupId, typeId, format, actions);
        this.value = value;
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
        Type.Id typeId = null;
        Format format = null;
        List<String> actions = null;
        Boolean value = null;

        // PARSE VALUES
        // --------------------------------------------------------------------------------------

        // > Top Level
        // --------------------------------------------------------------------------------------

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
                value = (Boolean) dataYaml.get("value");

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

        return new Bool(id, groupId, typeId, format, actions, value);
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



    // >>> Value
    // ------------------------------------------------------------------------------------------

    public Boolean getValue() {
        return this.value;
    }


    public int getValueAsInt() {
        return this.value ? 1 : 0;
    }


    public void setValue(Boolean value) {
        this.value = value;
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
                    "SELECT comp.group_id, comp.label, comp.row, comp.column, comp.width, " +
                           "comp.type_kind, comp.type_id, comp.actions, bool.value " +
                    "FROM component comp " +
                    "INNER JOIN component_boolean bool on bool.component_id = comp.component_id " +
                    "WHERE comp.component_id =  " + SQL.quoted(thisBool.getId().toString());


                Cursor cursor = database.rawQuery(boolQuery, null);

                UUID groupId;
                String label;
                Integer row;
                Integer column;
                Integer width;
                String typeKind;
                String typeId;
                Integer keyStat;
                List<String> actions;
                Boolean value;
                try {
                    cursor.moveToFirst();
                    groupId     = UUID.fromString(cursor.getString(0));
                    label       = cursor.getString(1);
                    row         = cursor.getInt(2);
                    column      = cursor.getInt(3);
                    width       = cursor.getInt(4);
                    typeKind    = cursor.getString(5);
                    typeId      = cursor.getString(6);
                    actions     = new ArrayList<>(Arrays.asList(
                                            TextUtils.split(cursor.getString(7), ",")));
                    value       = cursor.getInt(8) != 0;
                }
                finally {
                    cursor.close();
                }

                thisBool.setTypeId(new Type.Id(typeKind, typeId));
                thisBool.setLabel(label);
                thisBool.setRow(row);
                thisBool.setColumn(column);
                thisBool.setWidth(width);
                thisBool.setActions(actions);
                thisBool.setValue(value);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                UUID trackerCode = trackerId.getCode();
                switch (trackerId.getTarget()) {
                    case GROUP:
                        Group.getAsyncTracker(trackerCode).markComponentId(thisBool.getId());
                        break;
                    case CELL:
                        Cell.getAsyncTracker(trackerCode).markComponent();
                        break;
                }
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

                componentRow.put("component_id", thisBool.getId().toString());

                SQL.putOptString(componentRow, "group_id", thisBool.getGroupId());
                componentRow.put("data_type", thisBool.componentName());
                componentRow.put("label", thisBool.getLabel());
                componentRow.put("row", thisBool.getRow());
                componentRow.put("column", thisBool.getColumn());
                componentRow.put("width", thisBool.getWidth());
                componentRow.put("actions", TextUtils.join(",", thisBool.getActions()));
                SQL.putOptString(componentRow, "text_value", thisBool.getValue());
                componentRow.putNull("type_kind");
                componentRow.putNull("type_id");

                database.insertWithOnConflict(SheetContract.Component.TABLE_NAME,
                        null,
                        componentRow,
                        SQLiteDatabase.CONFLICT_REPLACE);

                // > Update ComponentBoolean Row
                // ------------------------------------------------------------------------------
                ContentValues boolComponentRow = new ContentValues();

                boolComponentRow.put("component_id", thisBool.getId().toString());

                if (thisBool.getValue() != null)
                    boolComponentRow.put("value", thisBool.getValueAsInt());
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
                if (trackerId == null) return;

                UUID trackerCode = trackerId.getCode();
                switch (trackerId.getTarget()) {
                    case GROUP:
                        Group.getAsyncTracker(trackerCode).markComponentId(thisBool.getId());
                        break;
                    case CELL:
                        Cell.getAsyncTracker(trackerCode).markComponent();
                        break;
                }
            }

        }.execute();
    }


}
