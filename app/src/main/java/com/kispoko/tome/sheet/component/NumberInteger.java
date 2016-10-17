
package com.kispoko.tome.sheet.component;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
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


    public NumberInteger(UUID id, Type.Id typeId, String label, Integer row, Integer column,
                         Integer width, Integer keyStat, Integer value)
    {
        super(id, typeId, label, row, column, width);
        this.keyStat = keyStat;
        this.value = value;
        this.textSize = TextSize.MEDIUM;
    }


    @SuppressWarnings("unchecked")
    public static NumberInteger fromYaml(Map<String, Object> integerYaml)
    {
        // Values to parse
        UUID id = null;            // Isn't actually parsed, is only stored in DB
        Type.Id typeId = null;
        String label = null;
        Integer row = null;
        Integer column = null;
        Integer width = null;
        String prefix = null;
        Integer keyStat = null;
        Integer value = null;

        // Parse Values
        Map<String, Object> formatYaml = (Map<String, Object>) integerYaml.get("format");
        Map<String, Object> dataYaml   = (Map<String, Object>) integerYaml.get("data");

        // >> Type Id
        if (dataYaml.containsKey("type"))
        {
            Map<String, Object> typeYaml = (Map<String, Object>) dataYaml.get("type");
            String _typeId = null;
            String typeKind = null;

            if (typeYaml.containsKey("id"))
                _typeId = (String) typeYaml.get("id");

            if (typeYaml.containsKey("kind"))
                typeKind = (String) typeYaml.get("kind");

            typeId = new Type.Id(typeKind, _typeId);
        }

        // >> Label
        if (formatYaml.containsKey("label"))
            label = (String) formatYaml.get("label");

        // >> Row
        if (formatYaml.containsKey("row"))
            row = (Integer) formatYaml.get("row");

        // >> Column
        if (formatYaml.containsKey("column"))
            column = (Integer) formatYaml.get("column");

        // >> Width
        if (formatYaml.containsKey("width"))
            width = (Integer) formatYaml.get("width");

        // >> Prefix
        if (formatYaml.containsKey("prefix"))
            prefix = (String) formatYaml.get("prefix");

        // >> Key Stat
        if (integerYaml.containsKey("key_stat"))
            keyStat = (Integer) integerYaml.get("key_stat");

        // >> Value
        if (dataYaml.containsKey("value"))
            value = (Integer) dataYaml.get("value");

        // Create Integer
        NumberInteger integer = new NumberInteger(id, typeId, label, row,
                                                  column, width, keyStat, value);

        if (prefix != null) integer.setPrefix(prefix);

        return integer;
    }


    // > API
    // ------------------------------------------------------------------------------------------

    // >> Getters/Setters
    // ------------------------------------------------------------------------------------------

    public String componentName()
    {
        return "integer";
    }


    public Integer getKeyStat()
    {
        return this.keyStat;
    }


    // >>> Value
    // ------------------------------------------------------------------------------------------

    public Integer getValue()
    {
        return this.value;
    }


    public void setValue(Integer value)
    {
        this.value = value;
    }


    // >>> Prefix
    // ------------------------------------------------------------------------------------------

    public String getPrefix()
    {
        return this.prefix;
    }


    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }


    // >> Views
    // ------------------------------------------------------------------------------------------

    public View getDisplayView(Context context)
    {
        LinearLayout integerLayout = Component.linearLayout(context);

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
        textView.setTextColor(ContextCompat.getColor(context, R.color.text_medium_dark));

        if (this.value != null)
            textView.setText(Integer.toString(this.value));
        else
            textView.setText("");

        contentLayout.addView(textView);

        integerLayout.addView(contentLayout);

        return integerLayout;
    }


    public View getEditorView(Context context)
    {
        return new LinearLayout(context);
    }


    // >> Views
    // ------------------------------------------------------------------------------------------

    /**
     * Load a Group from the database.
     * @param database The sqlite database object.
     * @param groupConstructorId The id of the async page constructor.
     * @param componentId The database id of the group to load.
     */
    public static void load(final SQLiteDatabase database,
                            final UUID groupConstructorId,
                            final UUID componentId)
    {
        new AsyncTask<Void,Void,NumberInteger>()
        {

            @Override
            protected NumberInteger doInBackground(Void... args)
            {
                // Query Component
                String integerQuery =
                    "SELECT comp.label, comp.row, comp.column, comp.width, comp.type_kind, " +
                           "comp.type_id, comp.key_stat, int.value, int.prefix " +
                    "FROM component comp " +
                    "INNER JOIN component_integer int on int.component_id = comp.component_id " +
                    "WHERE comp.component_id =  " + SQL.quoted(componentId.toString());


                Cursor textCursor = database.rawQuery(integerQuery, null);

                String label;
                Integer row;
                Integer column;
                Integer width;
                String typeKind;
                String typeId;
                Integer keyStat;
                Integer value;
                String prefix;
                try {
                    textCursor.moveToFirst();
                    label       = textCursor.getString(0);
                    row         = textCursor.getInt(1);
                    column      = textCursor.getInt(2);
                    width       = textCursor.getInt(3);
                    typeKind    = textCursor.getString(4);
                    typeId      = textCursor.getString(5);
                    keyStat     = textCursor.getInt(6);
                    value       = textCursor.getInt(7);
                    prefix      = textCursor.getString(8);
                }
                // TODO log
                finally {
                    textCursor.close();
                }

                NumberInteger integer = new NumberInteger(componentId,
                                                          new Type.Id(typeKind, typeId),
                                                          label,
                                                          row,
                                                          column,
                                                          width,
                                                          keyStat,
                                                          value);
                integer.setPrefix(prefix);

                return integer;
            }

            @Override
            protected void onPostExecute(NumberInteger integer)
            {
                Group.getAsyncConstructor(groupConstructorId).addComponent(integer);
            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param database The SQLite database object.
     * @param groupId The ID of the parent group object.
     */
    public void save(final SQLiteDatabase database, final UUID groupTrackerId, final UUID groupId)
    {
        final NumberInteger thisInteger = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                ContentValues componentRow = new ContentValues();

                componentRow.put("component_id", thisInteger.getId().toString());
                componentRow.put("group_id", groupId.toString());
                componentRow.put("data_type", thisInteger.componentName());
                componentRow.put("label", thisInteger.getLabel());
                componentRow.put("row", thisInteger.getRow());
                componentRow.put("column", thisInteger.getColumn());
                componentRow.put("width", thisInteger.getWidth());
                componentRow.put("text_value", thisInteger.getValue().toString());
                componentRow.putNull("type_kind");
                componentRow.putNull("type_id");

                if (thisInteger.getKeyStat() != null)
                    componentRow.put("key_stat", thisInteger.getKeyStat());
                else
                    componentRow.putNull("key_stat");

                database.insertWithOnConflict(SheetContract.Component.TABLE_NAME,
                                              null,
                                              componentRow,
                                              SQLiteDatabase.CONFLICT_REPLACE);


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
                Group.getTracker(groupTrackerId).setComponentId(thisInteger.getId());
            }

        }.execute();
    }


}
