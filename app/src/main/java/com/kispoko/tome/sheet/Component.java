
package com.kispoko.tome.sheet;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.activity.sheet.ActionDialogFragment;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.component.Image;
import com.kispoko.tome.sheet.component.NumberInteger;
import com.kispoko.tome.sheet.component.Table;
import com.kispoko.tome.sheet.component.Text;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.TrackerId;
import com.kispoko.tome.util.Unique;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Component
 *
 */
public abstract class Component implements Unique, Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;
    private UUID groupId;
    private Type.Id typeId;
    private Format format;
    private List<String> actions;


    // > INTERFACE
    // ------------------------------------------------------------------------------------------

    abstract public View getDisplayView(Context context, Rules rules);
    abstract public View getEditorView(Context context, Rules rules);

    abstract public void runAction(String actionName, Context context, Rules rules);

    abstract public String componentName();

    abstract public void save(TrackerId trackerId);
    abstract public void load(TrackerId trackerId);


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Component(UUID id, UUID groupId, Type.Id typeId, Format format, List<String> actions)
    {
        if (id != null)
            this.id = id;
        else
            this.id = UUID.randomUUID();

        this.groupId = groupId;
        this.typeId = typeId;
        this.format = format;
        this.actions = actions;
    }


    public static Component empty(UUID id, UUID groupId, String kind)
    {
        switch (kind)
        {
            case "text":
                return new Text(id, groupId);
            case "integer":
                return new NumberInteger(id, groupId);
            case "image":
                return new Image(id, groupId);
            case "table":
                return new Table(id, groupId);
            default:
                return null;
        }
    }



    // > API
    // ------------------------------------------------------------------------------------------

    // >> Id
    // ------------------------------------------------------------------------------------------

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    // >> Group Id
    // ------------------------------------------------------------------------------------------

    public UUID getGroupId() {
        return this.groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }


    // >> Type Id
    // ------------------------------------------------------------------------------------------

    public Type.Id getTypeId() {
        return this.typeId;
    }


    public void setTypeId(Type.Id typeId) {
        this.typeId = typeId;
    }


    // >> Format
    // ------------------------------------------------------------------------------------------

    public Format getFormat() {
        return this.format;
    }


    public void setFormat(Format format) {
        this.format = format;
    }


    // >> Label
    // ------------------------------------------------------------------------------------------

    public boolean hasLabel() {
        return this.format.getLabel() != null;
    }


    public String getLabel() {
        return this.format.getLabel();
    }


    // >> Row
    // ------------------------------------------------------------------------------------------

    public Integer getRow() {
        return this.format.getRow();
    }


    public void setRow(Integer row) {
        this.format.setRow(row);
    }


    // >> Column
    // ------------------------------------------------------------------------------------------

    public Integer getColumn() {
        return this.format.getColumn();
    }


    public void setColumn(Integer column) {
        this.format.setColumn(column);
    }


    // >> Width
    // ------------------------------------------------------------------------------------------

    public Integer getWidth() {
        return this.format.getWidth();
    }


    public void setWidth(Integer width) {
        this.format.setWidth(width);
    }


    // >> Actions
    // ------------------------------------------------------------------------------------------

    public List<String> getActions() {
        return this.actions;
    }


    public void setActions(List actions) {
        this.actions = actions;
    }


    public String getTextValue()
    {
        if (this instanceof Text) {
            return ((Text) this).getValue();
        } else if (this instanceof NumberInteger) {
            return ((NumberInteger) this).getValue().toString();
        } else {
            return "";
        }
    }


    // >> STATIC METHODS
    // ------------------------------------------------------------------------------------------

    /**
     * Create a component from Yaml.
     * @param componentYaml The yaml object.
     * @return The parsed component.
     */
    public static Component fromYaml(Map<String, Object> componentYaml)
    {
        String componentType = (String) componentYaml.get("type");

        switch (componentType)
        {
            case "text":
                return Text.fromYaml(componentYaml);
            case "image":
                return Image.fromYaml(componentYaml);
            case "integer":
                return NumberInteger.fromYaml(componentYaml);
            case "table":
                return Table.fromYaml(componentYaml);
        }

        return null;
    }


    /**
     * Load a Group from the database.
     * @param database The sqlite database object.
     * @param trackerId The ID of the async tracker for the caller.
     * @param componentId The database id of the component to load.
     */
    /*
    public static void load(TrackerId trackerId, UUID componentId, String componentType)
    {
        switch (componentType)
        {
            case "text":
                Text.load(trackerId, componentId);
                break;
            case "image":
                Image.load(trackerId, componentId);
                break;
            case "integer":
                NumberInteger.load(trackerId, componentId);
                break;
            case "table":
                Table.load(trackerId, componentId);
                break;
        }
    }
    */


    // > INTERNAL API
    // ------------------------------------------------------------------------------------------


    /**
     * The layout for a component.
     * @param context Context object.
     * @return A LinearLayout that represents the outer-most container of a component view.
     */
    protected LinearLayout linearLayout(Context context, final Rules rules)
    {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.MATCH_PARENT);

        int layoutMarginsHorz = (int) Util.getDim(context, R.dimen.comp_layout_margins_horz);

        linearLayoutParams.setMargins(layoutMarginsHorz, 0, layoutMarginsHorz, 0);
        layout.setLayoutParams(linearLayoutParams);

        layout.setBackgroundResource(R.drawable.bg_component);

        final Component thisComponent = this;

        final SheetActivity thisActivity = (SheetActivity) context;
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionDialogFragment actionDialogFragment =
                        ActionDialogFragment.newInstance(thisComponent, rules);
                actionDialogFragment.show(thisActivity.getSupportFragmentManager(),
                                          actionDialogFragment.getTag());
            }
        });


        // Add label
        TextView labelView = new TextView(context);
        labelView.setGravity(Gravity.CENTER_HORIZONTAL);
        labelView.setText(this.getLabel().toUpperCase());
        float labelTextSize = (int) Util.getDim(context, R.dimen.comp_label_text_size);
        labelView.setTextSize(labelTextSize);

        int labelPaddingBottom = (int) Util.getDim(context, R.dimen.comp_label_padding_bottom);
        labelView.setPadding(0, 0, 0, labelPaddingBottom);

        labelView.setTextColor(ContextCompat.getColor(context, R.color.text_light));

        labelView.setTypeface(Util.sansSerifFontRegular(context));

        layout.addView(labelView);

        return layout;
    }


    /**
     * Common method for parsing format section of component yaml.
     * @param componentYaml Component top-level parsed yaml object.
     * @return The component's parsed Format object.
     */
    @SuppressWarnings("unchecked")
    protected static Format parseFormatYaml(Map<String,Object> componentYaml)
    {
        if (!componentYaml.containsKey("format")) return null;

        // Format values
        String label = null;
        Integer row = null;
        Integer column = null;
        Integer width = null;

        // Parse Format
        Map<String,Object> formatYaml = (Map<String,Object>) componentYaml.get("format");

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

        return new Format(label, row, column, width);
    }


    // > NESTED CLASSES
    // ------------------------------------------------------------------------------------------

    public enum TextSize
    {
        SMALL,
        MEDIUM,
        LARGE;

        public static TextSize fromString(String textSize)
        {
            return TextSize.valueOf(textSize.toUpperCase());
        }

    }


    public static class Format implements Serializable
    {

        // > PROPERTIES
        // --------------------------------------------------------------------------------------

        private String label;
        private Integer row;
        private Integer column;
        private Integer width;


        // > CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public Format(String label, Integer row, Integer column, Integer width)
        {
            this.label = label;
            this.row = row;
            this.column = column;
            this.width = width;
        }


        // > API
        // --------------------------------------------------------------------------------------

        public String getLabel() {
            return this.label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Integer getRow() {
            return this.row;
        }

        public void setRow(Integer row) {
            this.row = row;
        }

        public Integer getColumn() {
            return this.column;
        }

        public void setColumn(Integer column) {
            this.column = column;
        }

        public Integer getWidth() {
            return this.width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

    }

}
