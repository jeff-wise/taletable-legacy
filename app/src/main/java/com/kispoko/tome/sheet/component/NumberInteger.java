
package com.kispoko.tome.sheet.component;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.type.Type;

import java.io.Serializable;
import java.util.Map;



/**
 * NumberInteger Component
 */
public class NumberInteger extends Component implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private TextSize textSize;
    private String prefix;
    private Integer value;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------


    public NumberInteger(Id id, Type.Id typeId, String label, Integer value)
    {
        super(id, typeId, label);
        this.value = value;
        this.textSize = TextSize.MEDIUM;
    }


    @SuppressWarnings("unchecked")
    public static NumberInteger fromYaml(Map<String, Object> integerYaml)
    {
        // Parse all integer fields
        // >> Label
        String label = null;
        if (integerYaml.containsKey("label"))
            label = (String) integerYaml.get("label");

        // >> Prefix
        String prefix = null;
        if (integerYaml.containsKey("prefix"))
            prefix = (String) integerYaml.get("prefix");

        // >> Text Size
        TextSize textSize = null;
        if (integerYaml.containsKey("size"))
            textSize = Component.TextSize.fromString((String) integerYaml.get("size"));

        // >> Value
        Map<String, Object> dataYaml = (Map<String, Object>) integerYaml.get("data");
        Integer value = (Integer) dataYaml.get("value");

        // >> Type
        Map<String, Object> typeYaml = (Map<String, Object>) dataYaml.get("type");

        Type.Id typeId = null;
        if (typeYaml != null) {
            String typeKind = (String) dataYaml.get("kind");
            String _typeId = (String) dataYaml.get("id");

            if (typeKind != null && _typeId != null) {
                typeId = new Type.Id(typeKind, _typeId);
            }
        }

        // Construct new integer
        NumberInteger integer = new NumberInteger(null, typeId, label, value);

        integer.setTextSize(textSize);
        integer.setPrefix(prefix);

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


    public Integer getValue()
    {
        return this.value;
    }


    public void setValue(Integer value)
    {
        this.value = value;
    }


    public String getPrefix()
    {
        return this.prefix;
    }


    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }


    public void setTextSize(TextSize textSize)
    {
        this.textSize = textSize;
    }


    // >> Views
    // ------------------------------------------------------------------------------------------

    public View getDisplayView(Context context)
    {
        // Create layout
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);

        // Add prefix if exists
        if (this.prefix != null) {
            layout.addView(ComponentUtil.prefixView(context, this.prefix, this.textSize));
        }

        // Add text view
        TextView textView = new TextView(context);

        textView.setTextSize(ComponentUtil.getTextSizeSP(context, this.textSize));

        Typeface font = Typeface.createFromAsset(context.getAssets(),
                                                 "fonts/DavidLibre-Regular.ttf");
        textView.setTypeface(font);
        textView.setTextColor(ContextCompat.getColor(context, R.color.text_medium));

        if (this.value != null)
            textView.setText(Integer.toString(this.value));
        else
            textView.setText("");

        layout.addView(textView);

        return layout;
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
                            final Integer groupConstructorId,
                            final Integer componentId)
    {
        new AsyncTask<Void,Void,Void>()
        {

            protected Void doInBackground(Void... args)
            {
                // Query Component
                String integerQuery =
                    "SELECT comp.component_id, comp.label, comp.type_kind, comp.type_id, " +
                           "int.integer_id, int.value, int.prefix " +
                    "FROM Component comp " +
                    "INNER JOIN ComponentInteger int on ComponentInteger.component_id = Component.component_id " +
                    "WHERE Component.component_id =  " + Integer.toString(componentId);


                Cursor textCursor = database.rawQuery(integerQuery, null);

                Long componentId;
                String label;
                String typeKind;
                String typeId;
                Long integerId;
                Integer value;
                String prefix;
                try {
                    textCursor.moveToFirst();
                    componentId = textCursor.getLong(0);
                    label       = textCursor.getString(1);
                    typeKind    = textCursor.getString(2);
                    typeId      = textCursor.getString(3);
                    integerId   = textCursor.getLong(4);
                    value       = textCursor.getInt(5);
                    prefix      = textCursor.getString(6);
                }
                // TODO log
                finally {
                    textCursor.close();
                }

                NumberInteger integer = new NumberInteger(new Id(componentId, integerId),
                                                          new Type.Id(typeKind, typeId),
                                                          label,
                                                          value);
                integer.setPrefix(prefix);

                Group.asyncConstructorMap.get(groupConstructorId).addComponent(integer);

                return null;
            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param database The SQLite database object.
     * @param groupId The ID of the parent group object.
     */
    public void save(final SQLiteDatabase database, final Long groupId)
    {
        final NumberInteger thisInteger = this;

        new AsyncTask<Void,Void,Void>()
        {
            protected Void doInBackground(Void... args)
            {
                ContentValues componentRow = new ContentValues();

                if (thisInteger.getId() != null)
                    componentRow.put("component_id", thisInteger.getId().getId());
                else
                    componentRow.putNull("component_id");
                componentRow.put("group_id", groupId);
                componentRow.put("data_type", thisInteger.componentName());
                componentRow.put("label", thisInteger.getLabel());
                componentRow.putNull("type_kind");
                componentRow.putNull("type_id");

                Long componentId = database.insertWithOnConflict(
                                                SheetContract.Component.TABLE_NAME,
                                                null,
                                                componentRow,
                                                SQLiteDatabase.CONFLICT_REPLACE);

                ContentValues integerComponentRow = new ContentValues();

                if (thisInteger.getId() != null)
                    integerComponentRow.put("integer_id", thisInteger.getId().getSubId());
                else
                    integerComponentRow.putNull("integer_id");
                integerComponentRow.put("integer_id", thisInteger.getId().getSubId());
                integerComponentRow.put("component_id", componentId);
                integerComponentRow.put("value", thisInteger.getValue());
                integerComponentRow.put("prefix", thisInteger.getPrefix());

                Long textComponentId = database.insertWithOnConflict(
                                                    SheetContract.ComponentInteger.TABLE_NAME,
                                                    null,
                                                    integerComponentRow,
                                                    SQLiteDatabase.CONFLICT_REPLACE);

                // Set ID in case of first insert and ID was Null
                thisInteger.setId(new Id(componentId, textComponentId));

                return null;
            }

        }.execute();
    }


}
