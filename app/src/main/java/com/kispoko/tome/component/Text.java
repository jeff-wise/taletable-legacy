
package com.kispoko.tome.component;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.MainActivity;
import com.kispoko.tome.R;
import com.kispoko.tome.component.text.TextEditRecyclerViewAdapter;
import com.kispoko.tome.type.List;
import com.kispoko.tome.util.SimpleDividerItemDecoration;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.util.Map;

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

    public Text(String name, String typeName, TextSize textSize)
    {
        super(name, typeName);
        this.textSize = textSize;
        this.value = "";
    }


    public Text(String name, String typeName, TextSize textSize, String label)
    {
        super(name, typeName, label);
        this.textSize = textSize;
        this.value = "";
    }


    // TODO allow integer values as strings here too
    @SuppressWarnings("unchecked")
    public static Text fromYaml(Map<String, Object> textYaml)
    {
        // Parse Values
        // >> Name
        String name = (String) textYaml.get("name");

        // >> Label
        String label = null;
        if (textYaml.containsKey("label"))
            label = (String) textYaml.get("label");

        // >> Text Size
        TextSize textSize = Component.TextSize.fromString((String) textYaml.get("size"));

        String value = null;
        String typeName = null;
        Map<String, Object> dataYaml = (Map<String, Object>) textYaml.get("data");

        if (dataYaml != null)
        {
            // >> Value
            if (dataYaml.containsKey("value"))
                value = (String) dataYaml.get("value");

            // >> Type
            if (dataYaml.containsKey("type"))
                typeName = (String) dataYaml.get("type");
        }

        // Create New Text
        Text newText;
        if (label == null)
            newText = new Text(name, typeName, textSize);
        else
            newText = new Text(name, typeName, textSize, label);

        if (value != null)
            newText.setValue(value, null);

        return newText;
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

        textView.setTextSize(com.kispoko.tome.component.Util.getTextSizeSP(context, this.textSize));

        Typeface font = Typeface.createFromAsset(context.getAssets(),
                                                 "fonts/DavidLibre-Regular.ttf");
        textView.setTypeface(font);
        textView.setTextColor(ContextCompat.getColor(context, R.color.text_medium));

        textView.setText(this.value);

        final Text thisText = this;

        final MainActivity mainActivity = (MainActivity) context;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openEditActivity(thisText);
            }
        });

        return textView;
    }


    public View getEditorView(Context context)
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


    /**
     * Return a view of the header for the text editor view.
     * @param context The parent context object.
     * @return A View represent the text editing header.
     */
    public View getEditorHeaderView(Context context)
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

        Typeface font = Typeface.createFromAsset(context.getAssets(),
                                                 "fonts/DavidLibre-Regular.ttf");

        // >> Title
        TextView titleView = new TextView(context);
        titleView.setText(this.getLabel().toUpperCase());
        float titleTextSize = Util.getDim(context, R.dimen.comp_text_editor_title_text_size);
        titleView.setTextSize(titleTextSize);
        titleView.setTextColor(ContextCompat.getColor(context, R.color.bluegrey_400));
        titleView.setTypeface(null, Typeface.BOLD);

        headerLayout.addView(titleView);

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

}
