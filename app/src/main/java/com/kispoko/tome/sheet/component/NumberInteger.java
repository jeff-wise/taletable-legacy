
package com.kispoko.tome.sheet.component;


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
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.type.Type;

import java.io.Serializable;
import java.util.Map;

import static android.R.attr.name;


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


    public NumberInteger(Integer id, Type.Id typeId, String label, Integer value)
    {
        super(id, typeId, label);
        this.value = value;
        this.textSize = TextSize.MEDIUM;
    }


    @SuppressWarnings("unchecked")
    public static NumberInteger fromYaml(Map<String, Object> integerYaml)
    {
        // Parse all integer fields
        // >> Id
        Integer id = (int) integerYaml.get("id");

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
        NumberInteger integer = new NumberInteger(id, typeId, label, value);

        integer.setTextSize(textSize);
        integer.setPrefix(prefix);

        return integer;
    }


    // > API
    // ------------------------------------------------------------------------------------------


    public void setValue(Integer value)
    {
        this.value = value;
    }


    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }


    public void setTextSize(TextSize textSize)
    {
        this.textSize = textSize;
    }


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
                    "SELECT comp.component_id, comp.label, comp.type_kind, comp.type_id, int.value, int.prefix " +
                    "FROM Component comp " +
                    "INNER JOIN ComponentInteger int on ComponentInteger.component_id = Component.component_id " +
                    "WHERE Component.component_id =  " + Integer.toString(componentId);


                Cursor textCursor = database.rawQuery(integerQuery, null);

                Integer componentId;
                String label;
                String typeKind;
                String typeId;
                Integer value;
                String prefix;
                try {
                    textCursor.moveToFirst();
                    componentId = textCursor.getInt(0);
                    label       = textCursor.getString(1);
                    typeKind    = textCursor.getString(2);
                    typeId      = textCursor.getString(3);
                    value       = textCursor.getInt(4);
                    prefix      = textCursor.getString(5);
                }
                // TODO log
                finally {
                    textCursor.close();
                }

                NumberInteger integer = new NumberInteger(componentId,
                                                          new Type.Id(typeKind, typeId),
                                                          label,
                                                          value);
                integer.setPrefix(prefix);

                Group.asyncConstructorMap.get(groupConstructorId).addComponent(integer);

                return null;
            }

        }.execute();
    }

}
