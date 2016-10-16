
package com.kispoko.tome.sheet.component;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.R;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.rules.RulesEngine;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.sheet.component.text.TextEditRecyclerViewAdapter;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.type.List;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.security.cert.CertificateNotYetValidException;
import java.util.Map;
import java.util.UUID;

import static com.kispoko.tome.R.id.text;
import static com.kispoko.tome.R.id.textView;


/**
 * Text
 */
public class Text extends Component implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String value;
    private TextSize textSize;

    private int displayTextViewId;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------


    public Text(UUID id, Type.Id typeId, String label, Integer row, Integer column,
                Integer width, TextSize textSize, String value)
    {
        super(id, typeId, label, row, column, width);
        this.value = value;
        this.textSize = textSize;
    }


    // TODO allow integer values as strings here too
    @SuppressWarnings("unchecked")
    public static Text fromYaml(Map<String, Object> textYaml)
    {
        // Values to parse
        UUID id = null;            // Isn't actually parsed, is only stored in DB
        Type.Id typeId = null;
        String label = null;
        Integer row = null;
        Integer column = null;
        Integer width = null;
        TextSize textSize = null;
        String value = null;

        // Parse Values
        Map<String, Object> formatYaml = (Map<String, Object>) textYaml.get("format");
        Map<String, Object> dataYaml   = (Map<String, Object>) textYaml.get("data");

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

        // >> Text Size
        if (formatYaml.containsKey("text_size"))
            textSize = Component.TextSize.fromString((String) formatYaml.get("text_size"));

        // >> Value
        if (dataYaml.containsKey("value"))
            value = (String) dataYaml.get("value");

        // Create Text
        return new Text(id, typeId, label, row, column, width, textSize, value);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    public void setValue(String value, Context context)
    {
        this.value = value;

        if (context != null) {
            TextView textView = (TextView) ((Activity) context)
                                    .findViewById(this.displayTextViewId);
            textView.setText(this.value);
        }
    }


    public String getValue()
    {
        return this.value;
    }


    public String componentName()
    {
        return "text";
    }


    public TextSize getTextSize()
    {
        return this.textSize;
    }


    // >> Views
    // ------------------------------------------------------------------------------------------

    public View getDisplayView(Context context)
    {
        LinearLayout textLayout = Component.linearLayout(context);


        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        this.displayTextViewId = Util.generateViewId();
        textView.setId(this.displayTextViewId);

        textView.setTextSize(ComponentUtil.getTextSizeSP(context, this.textSize));

        textView.setTypeface(Util.serifFontBold(context));
        textView.setTextColor(ContextCompat.getColor(context, R.color.text_medium_dark));

        textView.setText(this.value);

        final Text thisText = this;

        final SheetActivity sheetActivity = (SheetActivity) context;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetActivity.openEditActivity(thisText);
            }
        });

        textLayout.addView(textView);

        return textLayout;
    }


    public View getEditorView(Context context)
    {
        if (!this.getTypeId().isNull())
            return this.getTypeEditorView(context);
        // No type is set, so allow free form edit
        else
            return this.getFreeEditorView(context);
    }


    public View getTypeEditorView(Context context)
    {
        // Lookup the recyclerview in activity layout
        RecyclerView textEditorView = new RecyclerView(context);
        textEditorView.setLayoutParams(Util.linearLayoutParamsMatch());
        textEditorView.addItemDecoration(new SimpleDividerItemDecoration(context));

        // Create adapter passing in the sample user data
        // TODO verify type

        // Create copy of type so we only display values that are not currently chosen
        List list = (List) RulesEngine.getType(this.getTypeId());
        List listWithoutCurrentValue = list.asClone();
        listWithoutCurrentValue.getValueList().remove(this.value);

        TextEditRecyclerViewAdapter adapter =
                new TextEditRecyclerViewAdapter(this, listWithoutCurrentValue);
        // Attach the adapter to the recyclerview to populate items
        textEditorView.setAdapter(adapter);
        // Set layout manager to position the items
        textEditorView.setLayoutManager(new LinearLayoutManager(context));

        return textEditorView;
    }


    public View getFreeEditorView(Context context)
    {
        // Header Layout
        LinearLayout headerLayout = new LinearLayout(context);
        headerLayout.setLayoutParams(Util.linearLayoutParamsMatch());
        headerLayout.setOrientation(LinearLayout.VERTICAL);
        int headerHorzPadding = (int) Util.getDim(context,
                R.dimen.comp_text_editor_free_header_horz_padding);
        int headerTopPadding = (int) Util.getDim(context,
                R.dimen.comp_text_editor_header_top_padding);
        headerLayout.setPadding(headerHorzPadding, headerTopPadding, headerHorzPadding, 0);
        headerLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.bluegrey_900));

        // >> Title
        TextView titleView = new TextView(context);
        int titleViewPaddingLeft = (int) Util.getDim(context,
                                          R.dimen.comp_text_editor_free_header_name_left_padding);
        titleView.setPadding(titleViewPaddingLeft, 0, 0, 0);
        titleView.setText(this.getLabel().toUpperCase());
        float titleTextSize = Util.getDim(context, R.dimen.comp_text_editor_title_text_size);
        titleView.setTextSize(titleTextSize);
        titleView.setTextColor(ContextCompat.getColor(context, R.color.bluegrey_400));
        titleView.setTypeface(null, Typeface.BOLD);

        headerLayout.addView(titleView);

        EditText editView = new EditText(context);

        editView.setTextSize(ComponentUtil.getTextSizeSP(context, this.textSize));

        Typeface font = Typeface.createFromAsset(context.getAssets(),
                                                 "fonts/DavidLibre-Regular.ttf");
        editView.setTypeface(font);
        editView.setTextColor(ContextCompat.getColor(context, R.color.amber_500));
        //editView.setPadding(0, 0, 0, 0);

        LinearLayout.LayoutParams editViewLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT);
        //editViewLayoutParams.leftMargin = 0;
        editView.setLayoutParams(editViewLayoutParams);

        float valueTextSize = Util.getDim(context, R.dimen.comp_text_editor_value_text_size);
        editView.setTextSize(valueTextSize);

        editView.setText(this.value);

        headerLayout.addView(editView);


        return headerLayout;
    }


    /**
     * Return a view of the header for the text editor view.
     * @param context The parent context object.
     * @return A View represent the text editing header.
     */
    public View getTypeEditorHeaderView(Context context)
    {
        // Header Layout
        LinearLayout headerLayout = new LinearLayout(context);
        headerLayout.setOrientation(LinearLayout.VERTICAL);
        int headerHorzPadding = (int) Util.getDim(context,
                R.dimen.comp_text_editor_header_horz_padding);
        int headerTopPadding = (int) Util.getDim(context,
                R.dimen.comp_text_editor_header_top_padding);
        LinearLayout.LayoutParams fieldLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        headerLayout.setPadding(headerHorzPadding, headerTopPadding, headerHorzPadding, 0);
        headerLayout.setLayoutParams(fieldLayoutParams);
        headerLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.bluegrey_900));


        // >> Title
        TextView titleView = new TextView(context);
        titleView.setText(this.getLabel().toUpperCase());
        float titleTextSize = Util.getDim(context, R.dimen.comp_text_editor_title_text_size);
        titleView.setTextSize(titleTextSize);
        titleView.setTextColor(ContextCompat.getColor(context, R.color.bluegrey_400));
        titleView.setTypeface(null, Typeface.BOLD);

        headerLayout.addView(titleView);

        Typeface font = Typeface.createFromAsset(context.getAssets(),
                "fonts/DavidLibre-Regular.ttf");

        // >> Value
        TextView valueView = new TextView(context);

        float valueTextSize = Util.getDim(context, R.dimen.comp_text_editor_value_text_size);
        valueView.setTextSize(valueTextSize);
        valueView.setText(this.getValue());
        valueView.setTextColor(ContextCompat.getColor(context, R.color.amber_500));
        valueView.setTypeface(font);

        headerLayout.addView(valueView);

        // >> Type Title
        TextView typeTitleView = new TextView(context);
        String typeTitle = "SELECT NEW " + this.getLabel().toUpperCase();
        typeTitleView.setText(typeTitle);
        float typeTitleTextSize = Util.getDim(context, R.dimen.comp_text_editor_type_title_text_size);
        typeTitleView.setTextSize(typeTitleTextSize);
        typeTitleView.setTextColor(ContextCompat.getColor(context, R.color.bluegrey_400));
        typeTitleView.setTypeface(null, Typeface.BOLD);

        int typeTitleLeftPadding = (int) Util.getDim(context,
                                                R.dimen.comp_text_editor_type_title_left_padding);
        int typeTitleTopPadding = (int) Util.getDim(context,
                                                  R.dimen.comp_text_editor_type_title_top_padding);
        int typeTitleBottomPadding = (int) Util.getDim(context,
                                               R.dimen.comp_text_editor_type_title_bottom_padding);
        typeTitleView.setPadding(typeTitleLeftPadding, typeTitleTopPadding,
                                 0, typeTitleBottomPadding);

        headerLayout.addView(typeTitleView);

        return headerLayout;
    }


    // >> Database
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
        new AsyncTask<Void,Void,Text>()
        {

            @Override
            protected Text doInBackground(Void... args)
            {
                // Query Component
                String textQuery =
                    "SELECT comp.label, comp.row, comp.column, comp.width, comp.type_kind, " +
                           "comp.type_id, text.size, text.value " +
                    "FROM Component comp " +
                    "INNER JOIN component_text text on text.component_id = comp.component_id " +
                    "WHERE comp.component_id =  " + SQL.quoted(componentId.toString());

                Cursor textCursor = database.rawQuery(textQuery, null);

                String label;
                Integer row;
                Integer column;
                Integer width;
                String typeKind;
                String typeId;
                String textSize;
                String value;
                try {
                    textCursor.moveToFirst();
                    label           = textCursor.getString(0);
                    row             = textCursor.getInt(1);
                    column          = textCursor.getInt(2);
                    width           = textCursor.getInt(3);
                    typeKind        = textCursor.getString(4);
                    typeId          = textCursor.getString(5);
                    textSize        = textCursor.getString(6);
                    value           = textCursor.getString(7);
                }
                // TODO log
                finally {
                    textCursor.close();
                }

                Text text = new Text(componentId,
                                     new Type.Id(typeKind, typeId),
                                     label,
                                     row,
                                     column,
                                     width,
                                     TextSize.fromString(textSize),
                                     value);

                return text;
            }

            @Override
            protected void onPostExecute(Text text)
            {
                Group.getAsyncConstructor(groupConstructorId).addComponent(text);
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
        final Text thisText = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                ContentValues componentRow = new ContentValues();

                componentRow.put("component_id", thisText.getId().toString());
                componentRow.put("group_id", groupId.toString());
                componentRow.put("data_type", thisText.componentName());
                componentRow.put("label", thisText.getLabel());
                componentRow.put("row", thisText.getRow());
                componentRow.put("column", thisText.getColumn());
                componentRow.put("width", thisText.getWidth());

                if (thisText.getTypeId() != null) {
                    componentRow.put("type_kind", thisText.getTypeId().getKind());
                    componentRow.put("type_id", thisText.getTypeId().getId());
                } else {
                    componentRow.putNull("type_kind");
                    componentRow.putNull("type_id");
                }

                database.insertWithOnConflict(SheetContract.Component.TABLE_NAME,
                                              null,
                                              componentRow,
                                              SQLiteDatabase.CONFLICT_REPLACE);


                ContentValues textComponentRow = new ContentValues();
                textComponentRow.put("component_id", thisText.getId().toString());
                textComponentRow.put("value", thisText.getValue());
                textComponentRow.put("size", thisText.getTextSize().toString().toLowerCase());

                database.insertWithOnConflict(SheetContract.ComponentText.TABLE_NAME,
                                              null,
                                              textComponentRow,
                                              SQLiteDatabase.CONFLICT_REPLACE);


                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                Group.getTracker(groupTrackerId).setComponentId(thisText.getId());
            }


        }.execute();
    }

}
