
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.widget.util.ComponentUtil;
import com.kispoko.tome.sheet.widget.format.Format;
import com.kispoko.tome.rules.programming.Variable;
import com.kispoko.tome.sheet.widget.text.TextEditRecyclerViewAdapter;
import com.kispoko.tome.sheet.widget.text.TextFormat;
import com.kispoko.tome.type.ListType;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.AsyncFunction;
import com.kispoko.tome.util.Model;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.Tracker;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.database.query.ModelQuery;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



/**
 * TextWidget
 */
public class TextWidget implements Model, Widget, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private WidgetData widgetData;
    private Variable value;

    private int displayTextViewId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextWidget(WidgetData widgetData, Variable value)
    {
        this.widgetData = widgetData;
        this.value = value;
    }


    /**
     * Create a text component from a Yaml representation.
     * @param groupId The parent group identifier.
     * @param yaml The yaml parsing object at the text component node.
     * @return A new TextWidget.
     * @throws YamlException
     */
    public static TextWidget fromYaml(UUID groupId, Yaml yaml)
                  throws YamlException
    {
        UUID            id      = UUID.randomUUID();
        String          name    = yaml.atKey("name").getString();
        Type.Id         typeId  = Type.Id.fromYaml(yaml.atKey("data"));
        TextFormat      format  = TextFormat.fromYaml(yaml.atKey("format"));
        List<String>    actions = yaml.atKey("actions").getStringList();
        Variable        value   = Variable.fromYaml(yaml.atKey("value"));

        return new TextWidget(id, name, groupId, typeId, format, actions, value);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Actions
    // ------------------------------------------------------------------------------------------

    public void runAction(String actionName, Context context, Rules rules)
    {
        switch (actionName)
        {
            case "edit":
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("COMPONENT", this);
                intent.putExtra("RULES", rules);
                ((Activity) context).startActivityForResult(intent, SheetActivity.COMPONENT_EDIT);
                break;
        }
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** TextWidget Format
    // ------------------------------------------------------------------------------------------

    public TextFormat getTextFormat()
    {
        return (TextFormat) this.getFormat();
    }


    public void setTextFormat(TextFormat format)
    {
        this.setFormat(format);
    }


    // ** Value
    // ------------------------------------------------------------------------------------------

    public void setValue(Variable value, Context context)
    {
        this.setValue(value);

        if (context != null) {
            TextView textView = (TextView) ((Activity) context)
                                    .findViewById(this.displayTextViewId);
            textView.setText(this.getValue().getString());
        }

        this.save(null);
    }


    public String componentName() {
        return "text";
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    public View getDisplayView(final Context context, Rules rules)
    {
        LinearLayout textLayout = this.linearLayout(context, rules);


        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        this.displayTextViewId = Util.generateViewId();
        textView.setId(this.displayTextViewId);

        textView.setTextSize(ComponentUtil.getTextSizeSP(context, this.textSize));

        textView.setTypeface(Util.serifFontBold(context));
        textView.setTextColor(ContextCompat.getColor(context, R.color.text_medium));

        textView.setText(this.getValue().getString());

        textLayout.addView(textView);

        return textLayout;
    }


    public View getEditorView(Context context, Rules rules)
    {
        if (!this.getTypeId().isNull())
            return this.getTypeEditorView(context, rules);
        // No type is set, so allow free form edit
        else
            return this.getFreeEditorView(context);
    }


    public View getTypeEditorView(Context context, Rules rules)
    {
        // Lookup the recyclerview in activity layout
        RecyclerView textEditorView = new RecyclerView(context);
        textEditorView.setLayoutParams(Util.linearLayoutParamsMatch());
        textEditorView.addItemDecoration(new SimpleDividerItemDecoration(context));

        // Create adapter passing in the sample user data
        ListType listType = (ListType) rules.getTypes().getType(this.getTypeId());
        TextEditRecyclerViewAdapter adapter = new TextEditRecyclerViewAdapter(this, listType);
        Log.d("***TEXT", "list type size " + Integer.toString(listType.size()));
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


        // Edit TextWidget
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

        final TextWidget thisTextWidget = this;
        final EditText thisEditView = editView;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity editActivity = (Activity) context;
                String newValue = thisEditView.getText().toString();
                EditResult editResult = new EditResult(EditResult.ResultType.TEXT_VALUE,
                                                       thisTextWidget.getId(), newValue);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("RESULT", editResult);
                editActivity.setResult(Activity.RESULT_OK, resultIntent);
                editActivity.finish();
            }
        });


        float valueTextSize = Util.getDim(context, R.dimen.comp_text_editor_value_text_size);
        editView.setTextSize(valueTextSize);

        editView.setText(this.getValue().getString());


        // Define layout structure
        layout.addView(editView);
        layout.addView(saveButton);

        return layout;
    }


    // > Model
    // ------------------------------------------------------------------------------------------


    public void onCreateModel()
    {

    }


    public void onUpdateModel(String updatedFieldName)
    {

    }


    // > Database
    // ------------------------------------------------------------------------------------------


    public static ModelQuery loadQuery(UUID textId)
    {
        String componentTable = SheetContract.Component.TABLE_NAME;
        String textTable = SheetContract.ComponentText.TABLE_NAME;

        String idString = this.getId().toString();

        return ModelQuery.from(componentTable)
                    .select("name").select("type_id").select("value")
                    .from(textTable);
                    .select("size")
                    .and(SQL.is(componentTable, "component_id", textId.toString()));
    }


    public static AsyncFunction<TextWidget> fromDatabase(final UUID textId)
    {
        return new AsyncFunction<>(new AsyncFunction.Action<TextWidget>() {
            @Override
            public TextWidget run() {

                TextWidget textWidget = new TextWidget(textId);

                textWidget.format.setValue(Format.fromDatabase());

                // load basic fields
                // load textWidget format
                // load value

                return textWidget;
            }
        });
    }


    /**
     * Load a Group from the database.
     * @param callerTrackerId The ID of the async tracker for the caller.
     */
    public void load(final UUID callerTrackerId)
    {
        final TextWidget thisTextWidget = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();




                Cursor textCursor = database.rawQuery(textQuery, null);

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
                String textSize = null;
                Integer keyStat = null;
                List actions = null;
                try {
                    textCursor.moveToFirst();
                    name            = textCursor.getString(0);
                    valueId         = UUID.fromString(textCursor.getString(1));
                    label           = textCursor.getString(2);
                    showLabel       = SQL.intAsBool(textCursor.getInt(3));
                    row             = textCursor.getInt(4);
                    column          = textCursor.getInt(5);
                    width           = textCursor.getInt(6);
                    alignment       = Alignment.fromString(textCursor.getString(7));
                    typeKind        = textCursor.getString(8);
                    typeId          = textCursor.getString(9);
                    actions         = new ArrayList<>(Arrays.asList(
                                            TextUtils.split(textCursor.getString(10), ",")));
                    keyStat         = textCursor.getInt(11);
                    textSize        = textCursor.getString(12);
                } catch (Exception e) {
                    Log.d("***TABLE", Log.getStackTraceString(e));
                }
                finally {
                    textCursor.close();
                }

                thisTextWidget.setName(name);
                thisTextWidget.setTypeId(new Type.Id(typeKind, typeId));
                thisTextWidget.setLabel(label);
                thisTextWidget.setShowLabel(showLabel);
                thisTextWidget.setRow(row);
                thisTextWidget.setColumn(column);
                thisTextWidget.setWidth(width);
                thisTextWidget.setAlignment(alignment);
                thisTextWidget.setActions(actions);
                thisTextWidget.setTextSize(TextSize.fromString(textSize));
                thisTextWidget.setKeyStat(keyStat);
                thisTextWidget.setValue(new Variable(valueId));

                return null;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                List<String> trackingKeys = new ArrayList<>();
                trackingKeys.add("value");

                Tracker.OnReady onReady = new Tracker.OnReady() {
                    @Override
                    protected void go() {
                        Global.getTracker(callerTrackerId).setKey(thisTextWidget.getId().toString());
                    }
                };

                UUID textTrackerId = Global.addTracker(new Tracker(trackingKeys, onReady));

                thisTextWidget.getValue().load(thisTextWidget.getId(), "value", textTrackerId);
            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param callerTrackerId The async tracker ID of the caller.
     */
    public void save(final UUID callerTrackerId)
    {
        final TextWidget thisTextWidget = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // > Save WidgetData Row
                // ------------------------------------------------------------------------------
                ContentValues componentRow = new ContentValues();
                thisTextWidget.putComponentSQLRows(componentRow);

                if (thisTextWidget.getKeyStat() != null)
                    componentRow.put("key_stat", thisTextWidget.getKeyStat());
                else
                    componentRow.putNull("key_stat");

                database.insertWithOnConflict(SheetContract.Component.TABLE_NAME,
                        null,
                        componentRow,
                        SQLiteDatabase.CONFLICT_REPLACE);

                // > Save TextComponent Row
                // ------------------------------------------------------------------------------
                ContentValues textComponentRow = new ContentValues();

                textComponentRow.put("component_id", thisTextWidget.getId().toString());
                textComponentRow.put("size", thisTextWidget.getTextSizeAsString());

                database.insertWithOnConflict(SheetContract.ComponentText.TABLE_NAME,
                        null,
                        textComponentRow,
                        SQLiteDatabase.CONFLICT_REPLACE);


                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                List<String> trackingKeys = new ArrayList<>();
                trackingKeys.add("value");

                Tracker.OnReady onReady = new Tracker.OnReady() {
                    @Override
                    protected void go() {
                        Global.getTracker(callerTrackerId).setKey(thisTextWidget.getId().toString());
                    }
                };

                UUID textTrackerId = Global.addTracker(new Tracker(trackingKeys, onReady));

                thisTextWidget.getValue().load(thisTextWidget.getId(), "value", textTrackerId);
            }


        }.execute();
    }


}
