
package com.kispoko.tome.sheet.widget;


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
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.rules.programming.Variable;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.TrackerId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Boolean WidgetData
 */
public class BooleanWidget extends WidgetData implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Boolean value;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BooleanWidget(UUID id, UUID groupId) {
        super(id, null, groupId, null, null, null, null);
        this.value = null;
    }

    public BooleanWidget(UUID id, String name, UUID groupId, Variable value, Type.Id typeId,
                         Format format, List<String> actions)
    {
        super(id, name, groupId, value, typeId, format, actions);
    }


    /**
     * Parse Boolean component from Yaml
     * @param boolYaml Parsed yaml object representing a bool component.
     * @return The parsed BooleanWidget.
     */
    @SuppressWarnings("unchecked")
    public static BooleanWidget fromYaml(UUID groupId, Map<String, Object> boolYaml)
    {
        // VALUES TO PARSE
        // --------------------------------------------------------------------------------------
        UUID id = UUID.randomUUID();
        String name = null;
        Variable value = null;
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
                value = Variable.fromYaml((Map<String,Object>) dataYaml.get("value"));

            // ** Type Id
            typeId = Type.Id.fromYaml(dataYaml);
        }

        // >> Format
        // --------------------------------------------------------------------------------------
        Map<String, Object> formatYaml = (Map<String, Object>) boolYaml.get("format");

        if (formatYaml != null)
        {
            // ** Format
            format = WidgetData.parseFormatYaml(boolYaml);
        }

        return new BooleanWidget(id, name, groupId, value, typeId, format, actions);
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
        final BooleanWidget thisBooleanWidget = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // ModelQuery WidgetData
                String boolQuery =
                    "SELECT comp.name, comp.label, comp.show_label, comp.row, comp.column, " +
                           "comp.width, comp.alignment, comp.type_kind, comp.type_id, " +
                           "comp.actions, bool.value " +
                    "FROM component comp " +
                    "INNER JOIN component_boolean bool on bool.component_id = comp.component_id " +
                    "WHERE comp.component_id =  " + SQL.quoted(thisBooleanWidget.getName().toString());


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

                thisBooleanWidget.setName(name);
                thisBooleanWidget.setTypeId(new Type.Id(typeKind, typeId));
                thisBooleanWidget.setLabel(label);
                thisBooleanWidget.setShowLabel(showLabel);
                thisBooleanWidget.setRow(row);
                thisBooleanWidget.setColumn(column);
                thisBooleanWidget.setWidth(width);
                thisBooleanWidget.setAlignment(alignment);
                thisBooleanWidget.setActions(actions);
                thisBooleanWidget.setValue(new Variable());

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                TrackerId textTrackerId = thisBooleanWidget.addComponentAsyncTracker(trackerId);

                thisBooleanWidget.getValue().load(thisBooleanWidget, textTrackerId);
            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param trackerId The async tracker ID for the caller.
     */
    public void save(final TrackerId trackerId)
    {
        final BooleanWidget thisBooleanWidget = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // > Update WidgetData Row
                // ------------------------------------------------------------------------------
                ContentValues componentRow = new ContentValues();

                thisBooleanWidget.putComponentSQLRows(componentRow);

                SQL.putOptString(componentRow, "text_value", thisBooleanWidget.getValue());

                database.insertWithOnConflict(SheetContract.Component.TABLE_NAME,
                        null,
                        componentRow,
                        SQLiteDatabase.CONFLICT_REPLACE);

                // > Update ComponentBoolean Row
                // ------------------------------------------------------------------------------
                ContentValues boolComponentRow = new ContentValues();

                boolComponentRow.put("component_id", thisBooleanWidget.getName().toString());

                if (thisBooleanWidget.getValue() != null)
                    boolComponentRow.put("value", thisBooleanWidget.getValue().getBoolean());
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
                TrackerId textTrackerId = thisBooleanWidget.addComponentAsyncTracker(trackerId);

                thisBooleanWidget.getValue().save(thisBooleanWidget.getName(), textTrackerId);
            }

        }.execute();
    }


}
