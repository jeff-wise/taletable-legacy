
package com.kispoko.tome.sheet.component;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.R;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.sheet.Page;
import com.kispoko.tome.sheet.component.text.TextEditRecyclerViewAdapter;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.sheet.group.Layout;
import com.kispoko.tome.type.List;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import static android.R.attr.name;


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


    public Text(Integer id, Type.Id typeId, String value, TextSize textSize, String label)
    {
        super(id, typeId, label);
        this.value = value;
        this.textSize = textSize;
    }


    // TODO allow integer values as strings here too
    @SuppressWarnings("unchecked")
    public static Text fromYaml(Map<String, Object> textYaml)
    {
        // Parse Values
        // >> Name
        Integer id = (int) textYaml.get("id");

        // >> Label
        String label = null;
        if (textYaml.containsKey("label"))
            label = (String) textYaml.get("label");

        // >> Text Size
        TextSize textSize = Component.TextSize.fromString((String) textYaml.get("size"));

        // >> Value
        Map<String, Object> dataYaml = (Map<String, Object>) textYaml.get("data");
        String value = (String) dataYaml.get("value");

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

        // Create New Text
        Text text = new Text(name, typeId, value, textSize, label);

        return text;
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



    // >> Views
    // ------------------------------------------------------------------------------------------

    public View getDisplayView(Context context)
    {
        TextView textView = new TextView(context);

        this.displayTextViewId = Util.generateViewId();
        textView.setId(this.displayTextViewId);

        textView.setTextSize(ComponentUtil.getTextSizeSP(context, this.textSize));

        Typeface font = Typeface.createFromAsset(context.getAssets(),
                                                 "fonts/DavidLibre-Regular.ttf");
        textView.setTypeface(font);
        textView.setTextColor(ContextCompat.getColor(context, R.color.text_medium));

        textView.setText(this.value);

        final Text thisText = this;

        final SheetActivity sheetActivity = (SheetActivity) context;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetActivity.openEditActivity(thisText);
            }
        });

        return textView;
    }


    public View getEditorView(Context context)
    {
        if (this.hasType())
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
        List list = (List) this.getType();
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
                String textQuery =
                    "SELECT comp.component_id, comp.label, comp.type_kind, comp.type_id, text.size, text.value " +
                    "FROM Component comp " +
                    "INNER JOIN ComponentText text on ComponentText.component_id = Component.component_id " +
                    "WHERE Component.component_id =  " + Integer.toString(componentId);

                Cursor textCursor = database.rawQuery(textQuery, null);

                Integer componentId;
                String label;
                String textSize;
                String value;
                String typeKind;
                String typeId;
                try {
                    textCursor.moveToFirst();
                    componentId = textCursor.getInt(0);
                    label       = textCursor.getString(1);
                    typeKind    = textCursor.getString(2);
                    typeId      = textCursor.getString(3);
                    textSize    = textCursor.getString(4);
                    value       = textCursor.getString(5);
                }
                // TODO log
                finally {
                    textCursor.close();
                }

                Text text = new Text(componentId,
                                     new Type.Id(typeKind, typeId),
                                     value,
                                     TextSize.fromString(textSize),
                                     label);
                Group.asyncConstructorMap.get(groupConstructorId).addComponent(text);

                return null;
            }

        }.execute();
    }


}
