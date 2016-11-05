
package com.kispoko.tome.sheet.component;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.Tracker;
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

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private TextSize textSize;
    private ComponentValue prefix;
    private ComponentValue postfix;
    private Integer keyStat;
    private Integer value;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberInteger(UUID id, UUID groupId) {
        super(id, null, groupId, null, null, null, null);
        this.keyStat = null;
        this.value = null;
        this.textSize = TextSize.MEDIUM;
    }


    public NumberInteger(UUID id, String name, UUID groupId, ComponentValue value, Type.Id typeId,
                         Format format, List<String> actions, Integer keyStat)
    {
        super(id, name, groupId, value, typeId, format, actions);
        this.keyStat = keyStat;
        this.textSize = TextSize.MEDIUM;
    }


    @SuppressWarnings("unchecked")
    public static NumberInteger fromYaml(UUID groupId, Map<String, Object> integerYaml)
    {
        // VALUES TO PARSE
        // --------------------------------------------------------------------------------------
        UUID id = UUID.randomUUID();
        String name = null;
        ComponentValue value = null;
        Type.Id typeId = null;
        Format format = null;
        List<String> actions = null;
        ComponentValue prefix = null;
        ComponentValue postfix = null;
        Integer keyStat = null;

        // PARSE VALUES
        // --------------------------------------------------------------------------------------

        // > Top Level
        // --------------------------------------------------------------------------------------

        // ** Name
        if (integerYaml.containsKey("name"))
            name = (String) integerYaml.get("name");

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
                value = ComponentValue.fromYaml((Map<String,Object>) dataYaml.get("value"));
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
                prefix = ComponentValue.fromYaml((Map<String,Object>) dataYaml.get("prefix"));

            // ** Postfix
            if (formatYaml.containsKey("postfix"))
                postfix = ComponentValue.fromYaml((Map<String,Object>) dataYaml.get("postfix"));
        }

        // CREATE INTEGER
        NumberInteger integer = new NumberInteger(id, name, groupId, value, typeId, format,
                                                  actions, keyStat);

        integer.setPrefix(prefix);
        integer.setPostfix(postfix);

        return integer;
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > State
    // ------------------------------------------------------------------------------------------

    public String componentName()
    {
        return "integer";
    }


    // ** Key Stat
    // ------------------------------------------------------------------------------------------

    public Integer getKeyStat() {
        return this.keyStat;
    }


    public void setKeyStat(Integer keyStat) {
        this.keyStat = keyStat;
    }


    // ** Prefix
    // ------------------------------------------------------------------------------------------

    public ComponentValue getPrefix() {
        return this.prefix;
    }


    public void setPrefix(ComponentValue prefix) {
        this.prefix = prefix;
    }


    // ** Prefix
    // ------------------------------------------------------------------------------------------

    public ComponentValue getPostfix() {
        return this.prefix;
    }


    public void setPostfix(ComponentValue postfix) {
        this.postfix = postfix;
    }


    public void runAction(String actionName, Context context, Rules rules)
    {

    }


    // > Views
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
            contentLayout.addView(
                    ComponentUtil.prefixView(context, this.prefix.getString(), this.textSize));
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


    // > Database
    // ------------------------------------------------------------------------------------------

    /**
     * Load a Group from the database.
     * @param callerTrackerId The async tracker ID of the caller.
     */
    public void load(final UUID callerTrackerId)
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
                    "SELECT comp.name, comp.value, comp.label, comp.show_label, comp.row, " +
                           "comp.column, comp.width, " +
                           "comp.alignment, comp.type_kind, comp.type_id, comp.key_stat, " +
                           "comp.actions, int.prefix, int.postfix " +
                    "FROM component comp " +
                    "INNER JOIN component_integer int on int.component_id = comp.component_id " +
                    "WHERE comp.component_id =  " + SQL.quoted(thisInteger.getId().toString());


                Cursor integerCursor = database.rawQuery(integerQuery, null);

                String name = null;
                UUID valueId = null;
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
                UUID prefixId = null;
                UUID postfixId = null;
                try
                {
                    integerCursor.moveToFirst();
                    name        = integerCursor.getString(0);
                    valueId     = UUID.fromString(integerCursor.getString(1));
                    label       = integerCursor.getString(2);
                    showLabel   = SQL.intAsBool(integerCursor.getInt(3));
                    row         = integerCursor.getInt(4);
                    column      = integerCursor.getInt(5);
                    width       = integerCursor.getInt(6);
                    alignment   = Alignment.fromString(integerCursor.getString(7));
                    typeKind    = integerCursor.getString(8);
                    typeId      = integerCursor.getString(9);
                    keyStat     = integerCursor.getInt(10);
                    actions     = new ArrayList<>(Arrays.asList(
                                        TextUtils.split(integerCursor.getString(11), ",")));
                    prefixId    = UUID.fromString(integerCursor.getString(12));
                    postfixId   = UUID.fromString(integerCursor.getString(13));
                } catch (Exception e) {
                    Log.d("***INTEGER", Log.getStackTraceString(e));
                }
                finally {
                    integerCursor.close();
                }

                thisInteger.setName(name);
                thisInteger.setTypeId(new Type.Id(typeKind, typeId));
                thisInteger.setLabel(label);
                thisInteger.setShowLabel(showLabel);
                thisInteger.setRow(row);
                thisInteger.setColumn(column);
                thisInteger.setWidth(width);
                thisInteger.setAlignment(alignment);
                thisInteger.setActions(actions);
                thisInteger.setKeyStat(keyStat);
                thisInteger.setValue(new ComponentValue(valueId));
                thisInteger.setPrefix(new ComponentValue(prefixId));
                thisInteger.setPostfix(new ComponentValue(postfixId));

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                List<String> trackingKeys = new ArrayList<>();
                trackingKeys.add("value");
                trackingKeys.add("prefix");
                trackingKeys.add("postfix");

                Tracker.OnReady onReady = new Tracker.OnReady() {
                    @Override
                    protected void go() {
                        Global.getTracker(callerTrackerId).setKey(thisInteger.getId().toString());
                    }
                };

                UUID textTrackerId = Global.addTracker(new Tracker(trackingKeys, onReady));

                thisInteger.getValue().load(thisInteger.getId(), "value", textTrackerId);
                thisInteger.getPrefix().load(thisInteger.getId(), "prefix", textTrackerId);
                thisInteger.getPostfix().load(thisInteger.getId(), "postfix", textTrackerId);
            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param callerTrackerId The async tracker ID for the caller.
     */
    public void save(final UUID callerTrackerId)
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
                integerComponentRow.put("prefix", thisInteger.getPrefix().getId().toString());
                integerComponentRow.put("postfix", thisInteger.getPostfix().getId().toString());

                database.insertWithOnConflict(SheetContract.ComponentInteger.TABLE_NAME,
                                              null,
                                              integerComponentRow,
                                              SQLiteDatabase.CONFLICT_REPLACE);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                List<String> trackingKeys = new ArrayList<>();
                trackingKeys.add("value");
                trackingKeys.add("prefix");
                trackingKeys.add("postfix");

                Tracker.OnReady onReady = new Tracker.OnReady() {
                    @Override
                    protected void go() {
                        Global.getTracker(callerTrackerId).setKey(thisInteger.getId().toString());
                    }
                };

                UUID textTrackerId = Global.addTracker(new Tracker(trackingKeys, onReady));

                thisInteger.getValue().save("value", textTrackerId);
                thisInteger.getPrefix().save("prefix", textTrackerId);
                thisInteger.getPostfix().save("postfix", textTrackerId);
            }

        }.execute();
    }

}
