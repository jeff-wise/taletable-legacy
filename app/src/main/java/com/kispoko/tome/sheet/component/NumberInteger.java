
package com.kispoko.tome.sheet.component;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.Global;
import com.kispoko.tome.R;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.sheet.component.table.Cell;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.TrackerId;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * NumberInteger Component
 */
public class NumberInteger extends Component implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private TextSize textSize;
    private String prefix;
    private Integer keyStat;
    private Integer value;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberInteger(UUID id, UUID groupId) {
        super(id, groupId, null, null, null);
        this.keyStat = null;
        this.value = null;
        this.textSize = TextSize.MEDIUM;
    }

    public NumberInteger(UUID id, UUID groupId, Type.Id typeId, Format format, List<String> actions,
                         Integer keyStat, Integer value)
    {
        super(id, groupId, typeId, format, actions);
        this.keyStat = keyStat;
        this.value = value;
        this.textSize = TextSize.MEDIUM;
    }


    @SuppressWarnings("unchecked")
    public static NumberInteger fromYaml(UUID groupId, Map<String, Object> integerYaml)
    {
        // VALUES TO PARSE
        // --------------------------------------------------------------------------------------
        UUID id = UUID.randomUUID();
        Type.Id typeId = null;
        Format format = null;
        List<String> actions = null;
        String prefix = null;
        Integer keyStat = null;
        Integer value = null;

        // PARSE VALUES
        // --------------------------------------------------------------------------------------

        // > Top Level
        // --------------------------------------------------------------------------------------

        // ** Actions
        if (integerYaml.containsKey("actions"))
            actions = (List<String>) integerYaml.get("actions");

        // ** Key Stat
        if (integerYaml.containsKey("key_stat"))
            keyStat = (Integer) integerYaml.get("key_stat");

        // >> Data
        // --------------------------------------------------------------------------------------
        Map<String, Object> dataYaml   = (Map<String, Object>) integerYaml.get("data");

        if (dataYaml != null)
        {
            // ** Type Id
            typeId = Type.Id.fromYaml(dataYaml);

            // ** Value
            if (dataYaml.containsKey("value"))
                value = (Integer) dataYaml.get("value");
        }

        // >> Format
        // --------------------------------------------------------------------------------------
        Map<String, Object> formatYaml = (Map<String, Object>) integerYaml.get("format");

        if (formatYaml != null)
        {
            // *** Format
            format = Component.parseFormatYaml(integerYaml);

            // ** Prefix
            if (formatYaml.containsKey("prefix"))
                prefix = (String) formatYaml.get("prefix");
        }

        // CREATE INTEGER
        NumberInteger integer = new NumberInteger(id, groupId, typeId,
                                                  format, actions,
                                                  keyStat, value);

        if (prefix != null) integer.setPrefix(prefix);

        return integer;
    }


    // > API
    // ------------------------------------------------------------------------------------------

    // >> State
    // ------------------------------------------------------------------------------------------

    public String componentName()
    {
        return "integer";
    }


    // >>> Key Stat
    // ------------------------------------------------------------------------------------------

    public Integer getKeyStat() {
        return this.keyStat;
    }


    public void setKeyStat(Integer keyStat) {
        this.keyStat = keyStat;
    }


    // >>> Value
    // ------------------------------------------------------------------------------------------

    public Integer getValue() {
        return this.value;
    }


    public void setValue(Integer value) {
        this.value = value;
    }


    // >>> Prefix
    // ------------------------------------------------------------------------------------------

    public String getPrefix() {
        return this.prefix;
    }


    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }


    public void runAction(String actionName, Context context, Rules rules)
    {

    }


    // >> Views
    // ------------------------------------------------------------------------------------------

    public View getDisplayView(Context context, Rules rules)
    {
        LinearLayout integerLayout = this.linearLayout(context, rules);

        LinearLayout contentLayout = new LinearLayout(context);
        contentLayout.setLayoutParams(Util.linearLayoutParamsMatch());
        contentLayout.setOrientation(LinearLayout.HORIZONTAL);
        contentLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        // Add prefix if exists
        if (this.prefix != null) {
            contentLayout.addView(ComponentUtil.prefixView(context, this.prefix, this.textSize));
        }


        // Add text view
        TextView textView = new TextView(context);

        textView.setTextSize(ComponentUtil.getTextSizeSP(context, this.textSize));

        textView.setTypeface(Util.serifFontBold(context));
        textView.setTextColor(ContextCompat.getColor(context, R.color.text_medium));

        if (this.value != null)
            textView.setText(Integer.toString(this.value));
        else
            textView.setText("");

        contentLayout.addView(textView);

        integerLayout.addView(contentLayout);

        return integerLayout;
    }


    public View getEditorView(Context context, Rules rules)
    {
        return new LinearLayout(context);
    }


    // >> Database
    // ------------------------------------------------------------------------------------------

    /**
     * Load a Group from the database.
     * @param trackerId The async tracker ID of the caller.
     */
    public void load(final TrackerId trackerId)
    {
        final NumberInteger thisInteger = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Query Component
                String integerQuery =
                    "SELECT comp.label, comp.show_label, comp.row, comp.column, comp.width, " +
                           "comp.alignment, comp.type_kind, comp.type_id, comp.key_stat, " +
                           "comp.actions, int.value, int.prefix " +
                    "FROM component comp " +
                    "INNER JOIN component_integer int on int.component_id = comp.component_id " +
                    "WHERE comp.component_id =  " + SQL.quoted(thisInteger.getId().toString());


                Cursor integerCursor = database.rawQuery(integerQuery, null);

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
                Integer value = null;
                String prefix = null;
                try {
                    integerCursor.moveToFirst();
                    label       = integerCursor.getString(0);
                    showLabel   = SQL.intAsBool(integerCursor.getInt(1));
                    row         = integerCursor.getInt(2);
                    column      = integerCursor.getInt(3);
                    width       = integerCursor.getInt(4);
                    alignment   = Alignment.fromString(integerCursor.getString(5));
                    typeKind    = integerCursor.getString(6);
                    typeId      = integerCursor.getString(7);
                    keyStat     = integerCursor.getInt(8);
                    actions     = new ArrayList<>(Arrays.asList(
                                        TextUtils.split(integerCursor.getString(9), ",")));
                    value       = integerCursor.getInt(10);
                    prefix      = integerCursor.getString(11);
                } catch (Exception e) {
                    Log.d("***INTEGER", Log.getStackTraceString(e));
                }
                finally {
                    integerCursor.close();
                }

                thisInteger.setTypeId(new Type.Id(typeKind, typeId));
                thisInteger.setLabel(label);
                thisInteger.setShowLabel(showLabel);
                thisInteger.setRow(row);
                thisInteger.setColumn(column);
                thisInteger.setWidth(width);
                thisInteger.setAlignment(alignment);
                thisInteger.setActions(actions);
                thisInteger.setKeyStat(keyStat);
                thisInteger.setValue(value);
                thisInteger.setPrefix(prefix);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                UUID trackerCode = trackerId.getCode();
                switch (trackerId.getTarget()) {
                    case GROUP:
                        Group.getAsyncTracker(trackerCode).markComponentId(thisInteger.getId());
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
        final NumberInteger thisInteger = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // > Query Component Table
                // ------------------------------------------------------------------------------
                ContentValues componentRow = new ContentValues();

                thisInteger.putComponentSQLRows(componentRow);

                SQL.putOptString(componentRow, "text_value", thisInteger.getValue());

                if (thisInteger.getKeyStat() != null)
                    componentRow.put("key_stat", thisInteger.getKeyStat());
                else
                    componentRow.putNull("key_stat");

                database.insertWithOnConflict(SheetContract.Component.TABLE_NAME,
                                              null,
                                              componentRow,
                                              SQLiteDatabase.CONFLICT_REPLACE);

                // > Query IntegerComponent Table
                // ------------------------------------------------------------------------------
                ContentValues integerComponentRow = new ContentValues();

                integerComponentRow.put("component_id", thisInteger.getId().toString());
                integerComponentRow.put("value", thisInteger.getValue());
                integerComponentRow.put("prefix", thisInteger.getPrefix());

                database.insertWithOnConflict(SheetContract.ComponentInteger.TABLE_NAME,
                                              null,
                                              integerComponentRow,
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
                        Group.getAsyncTracker(trackerCode).markComponentId(thisInteger.getId());
                        break;
                    case CELL:
                        Cell.getAsyncTracker(trackerCode).markComponent();
                        break;
                }
            }

        }.execute();
    }


}
