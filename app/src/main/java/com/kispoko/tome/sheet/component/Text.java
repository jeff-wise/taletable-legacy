
package com.kispoko.tome.sheet.component;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.Global;
import com.kispoko.tome.R;
import com.kispoko.tome.activity.EditActivity;
import com.kispoko.tome.activity.EditResult;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.rules.RulesEngine;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.sheet.component.text.TextEditRecyclerViewAdapter;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.type.ListType;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Text
 */
public class Text extends Component implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String value;
    private TextSize textSize;
    private Integer keyStat;

    private int displayTextViewId;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------


    public Text(UUID id, UUID groupId, Type.Id typeId, Format format, List<String> actions,
                TextSize textSize, Integer keyStat, String value)
    {
        super(id, groupId, typeId, format, actions);
        this.keyStat = keyStat;
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
        Format format = null;
        ArrayList<String> actions = null;
        TextSize textSize = null;
        Integer keyStat = null;
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

        // >> Format
        format = Component.parseFormatYaml(textYaml);

        // >> Actions
        if (textYaml.containsKey("actions"))
            actions = (ArrayList<String>) textYaml.get("actions");

        // >> Text Size
        if (formatYaml.containsKey("text_size"))
            textSize = Component.TextSize.fromString((String) formatYaml.get("text_size"));

        // >> Key Stat
        if (textYaml.containsKey("key_stat"))
            keyStat = (Integer) textYaml.get("key_stat");

        // >> Value
        if (dataYaml.containsKey("value"))
            value = (String) dataYaml.get("value");

        // Create Text
        return new Text(id, null, typeId, format, actions, textSize, keyStat, value);
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

        this.save(Global.getDatabase(), null);
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


    public Integer getKeyStat()
    {
        return this.keyStat;
    }


    public void runAction(Context context, String actionName)
    {
        switch (actionName)
        {
            case "edit":
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("COMPONENT", this);
                ((Activity) context).startActivityForResult(intent, SheetActivity.COMPONENT_EDIT);
                break;
        }
    }


    // >> Views
    // ------------------------------------------------------------------------------------------

    public View getDisplayView(final Context context)
    {
        LinearLayout textLayout = this.linearLayout(context);


        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        this.displayTextViewId = Util.generateViewId();
        textView.setId(this.displayTextViewId);

        textView.setTextSize(ComponentUtil.getTextSizeSP(context, this.textSize));

        textView.setTypeface(Util.serifFontBold(context));
        textView.setTextColor(ContextCompat.getColor(context, R.color.text_medium_dark));

        textView.setText(this.value);


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
        ListType list = (ListType) RulesEngine.getType(this.getTypeId());
        ListType listWithoutCurrentValue = list.asClone();
        listWithoutCurrentValue.getValueList().remove(this.value);

        TextEditRecyclerViewAdapter adapter =
                new TextEditRecyclerViewAdapter(this, listWithoutCurrentValue);
        // Attach the adapter to the recyclerview to populate items
        textEditorView.setAdapter(adapter);
        // Set layout manager to position the items
        textEditorView.setLayoutManager(new LinearLayoutManager(context));

        return textEditorView;
    }


    public View getFreeEditorView(final Context context)
    {
        // Layout
        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = Util.linearLayoutParamsMatch();

        int layoutParamsMargins = (int) Util.getDim(context,
                                                R.dimen.comp_text_editor_free_layout_margins);
        layoutParams.setMargins(layoutParamsMargins, layoutParamsMargins,
                                layoutParamsMargins, layoutParamsMargins);
        layout.setLayoutParams(layoutParams);
        layout.setBackgroundColor(ContextCompat.getColor(context, R.color.sheet_medium));
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setOrientation(LinearLayout.VERTICAL);

        int layoutPaddingHorz = (int) Util.getDim(context,
                                              R.dimen.comp_text_editor_free_layout_padding_horz);
        int layoutPaddingVert = (int) Util.getDim(context,
                                              R.dimen.comp_text_editor_free_layout_padding_vert);
        layout.setPadding(layoutPaddingHorz, layoutPaddingVert,
                          layoutPaddingHorz, layoutPaddingVert);


        // Edit Text
        EditText editView = new EditText(context);
        editView.setId(R.id.comp_text_editor_value);

        editView.setTextSize(ComponentUtil.getTextSizeSP(context, this.textSize));

        editView.setTypeface(Util.serifFontBold(context));
        editView.setTextColor(ContextCompat.getColor(context, R.color.text_medium_dark));
        editView.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams editViewLayoutParams = Util.linearLayoutParamsMatchWrap();
        editViewLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        editView.setLayoutParams(editViewLayoutParams);
        editView.setBackgroundResource(R.drawable.bg_text_component_editor);


        // Save Button
        TextView saveButton = new TextView(context);
        LinearLayout.LayoutParams saveButtonLayoutParams = Util.linearLayoutParamsMatchWrap();
        saveButtonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        saveButtonLayoutParams.topMargin = (int) Util.getDim(context, R.dimen.two_dp);
        saveButton.setGravity(Gravity.CENTER_HORIZONTAL);
        saveButton.setLayoutParams(saveButtonLayoutParams);
        saveButton.setBackgroundResource(R.drawable.bg_text_component_editor_save_button);
        saveButton.setText("SAVE");
        saveButton.setTypeface(Util.sansSerifFontBold(context));
        saveButton.setTextColor(ContextCompat.getColor(context, R.color.green_medium));
        float saveButtonTextSize = Util.getDim(context,
                                           R.dimen.comp_text_editor_free_save_button_text_size);
        saveButton.setTextSize(saveButtonTextSize);

        final Text thisText = this;
        final EditText thisEditView = editView;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity editActivity = (Activity) context;
                String newValue = thisEditView.getText().toString();
                EditResult editResult = new EditResult(EditResult.ResultType.TEXT_VALUE,
                                                       thisText.getId(), newValue);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("RESULT", editResult);
                editActivity.setResult(Activity.RESULT_OK, resultIntent);
                editActivity.finish();
            }
        });


        float valueTextSize = Util.getDim(context, R.dimen.comp_text_editor_value_text_size);
        editView.setTextSize(valueTextSize);

        editView.setText(this.value);


        // Define layout structure
        layout.addView(editView);
        layout.addView(saveButton);

        return layout;
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
                    "SELECT comp.group_id, comp.label, comp.row, comp.column, comp.width, " +
                           "comp.type_kind, comp.type_id, comp.key_stat, comp.actions, text.size, text.value " +
                    "FROM Component comp " +
                    "INNER JOIN component_text text on text.component_id = comp.component_id " +
                    "WHERE comp.component_id =  " + SQL.quoted(componentId.toString());

                Cursor textCursor = database.rawQuery(textQuery, null);

                UUID groupId;
                String label;
                Integer row;
                Integer column;
                Integer width;
                String typeKind;
                String typeId;
                String textSize;
                Integer keyStat;
                List actions;
                String value;
                try {
                    textCursor.moveToFirst();
                    groupId         = UUID.fromString(textCursor.getString(0));
                    label           = textCursor.getString(1);
                    row             = textCursor.getInt(2);
                    column          = textCursor.getInt(3);
                    width           = textCursor.getInt(4);
                    typeKind        = textCursor.getString(5);
                    typeId          = textCursor.getString(6);
                    keyStat         = textCursor.getInt(7);
                    actions         = new ArrayList<>(Arrays.asList(
                                            TextUtils.split(textCursor.getString(8), ",")));
                    textSize        = textCursor.getString(9);
                    value           = textCursor.getString(10);
                }
                // TODO log
                finally {
                    textCursor.close();
                }

                return new Text(componentId,
                                groupId,
                                new Type.Id(typeKind, typeId),
                                new Format(label, row, column, width),
                                actions,
                                TextSize.fromString(textSize),
                                keyStat,
                                value);
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
     */
    public void save(final SQLiteDatabase database, final UUID groupTrackerId)
    {
        final Text thisText = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                ContentValues componentRow = new ContentValues();

                componentRow.put("component_id", thisText.getId().toString());
                componentRow.put("group_id", thisText.getGroupId().toString());
                componentRow.put("data_type", thisText.componentName());
                componentRow.put("label", thisText.getLabel());
                componentRow.put("row", thisText.getRow());
                componentRow.put("column", thisText.getColumn());
                componentRow.put("width", thisText.getWidth());
                componentRow.put("actions", TextUtils.join(",", thisText.getActions()));
                componentRow.put("text_value", thisText.getValue());

                if (thisText.getTypeId() != null) {
                    componentRow.put("type_kind", thisText.getTypeId().getKind());
                    componentRow.put("type_id", thisText.getTypeId().getId());
                } else {
                    componentRow.putNull("type_kind");
                    componentRow.putNull("type_id");
                }

                if (thisText.getKeyStat() != null)
                    componentRow.put("key_stat", thisText.getKeyStat());
                else
                    componentRow.putNull("key_stat");


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
                if (groupTrackerId != null)
                    Group.getTracker(groupTrackerId).setComponentId(thisText.getId());
            }


        }.execute();
    }

}
