
package com.kispoko.tome.sheet;


import android.content.ContentValues;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.activity.sheet.ActionDialogFragment;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.component.Bool;
import com.kispoko.tome.sheet.component.Image;
import com.kispoko.tome.sheet.component.NumberInteger;
import com.kispoko.tome.sheet.component.Table;
import com.kispoko.tome.sheet.component.Text;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.TrackerId;
import com.kispoko.tome.util.Unique;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
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
    private List<String> actions;

    // Format Properties
    private String label;
    private Boolean showLabel;
    private Integer row;
    private Integer column;
    private Integer width;
    private Alignment alignment;



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

        if (actions != null)
            this.actions = actions;
        else
            this.actions = new ArrayList<>();

        // Copy format values
        if (format == null) {
            this.label = null;
            this.showLabel = true;
            this.row = null;
            this.column = null;
            this.width = null;
            this.alignment = null;
        } else {
            this.label  = format.getLabel();
            this.showLabel = format.getShowLabel();
            this.row    = format.getRow();
            this.column = format.getColumn();
            this.width = format.getWidth();
            this.alignment = format.getAlignment();
        }
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
            case "boolean":
                return new Bool(id, groupId);
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


    // >> Label
    // ------------------------------------------------------------------------------------------

    public boolean hasLabel() {
        return this.label != null;
    }


    public String getLabel() {
        return this.label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    // >> Show Label
    // ------------------------------------------------------------------------------------------

    public Boolean getShowLabel() {
        return this.showLabel;
    }


    public void setShowLabel(Boolean showLabel) {
        this.showLabel = showLabel;
    }

    // >> Row
    // ------------------------------------------------------------------------------------------

    public Integer getRow() {
        return this.row;
    }


    public void setRow(Integer row) {
        this.row = row;
    }


    // >> Column
    // ------------------------------------------------------------------------------------------

    public Integer getColumn() {
        return this.column;
    }


    public void setColumn(Integer column) {
        this.column = column;
    }


    // >> Width
    // ------------------------------------------------------------------------------------------

    public Integer getWidth() {
        return this.width;
    }


    public void setWidth(Integer width) {
        this.width = width;
    }


    // >> Alignment
    // ------------------------------------------------------------------------------------------

    public Alignment getAlignment() {
        return this.alignment;
    }


    public String getAlignmentAsString() {
        if (this.alignment != null)
            return this.alignment.toString().toLowerCase();
        return null;
    }


    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }


    // >> Actions
    // ------------------------------------------------------------------------------------------

    public List<String> getActions() {
        return this.actions;
    }


    public void setActions(List actions) {
        if (actions != null)
            this.actions = actions;
        else
            this.actions = new ArrayList<>();
    }


    public String getTextValue()
    {

        if (this instanceof Text) {
            String value = ((Text) this).getValue();
            return value != null ? value : "";
        } else if (this instanceof NumberInteger) {
            Integer value = ((NumberInteger) this).getValue();
            return value != null ? value.toString() : "";
        } else {
            return "";
        }
    }


    protected void putComponentSQLRows(ContentValues row)
    {
        try {
            row.put("component_id", this.getId().toString());
            SQL.putOptString(row, "group_id", this.getGroupId());
            row.put("data_type", this.componentName());
            row.put("label", this.getLabel());
            row.put("show_label", SQL.boolAsInt(this.getShowLabel()));
            row.put("row", this.getRow());
            row.put("column", this.getColumn());
            row.put("width", this.getWidth());
            row.put("actions", TextUtils.join(",", this.getActions()));
            row.put("alignment", this.getAlignmentAsString());

            if (this.getTypeId() != null) {
                row.put("type_kind", this.getTypeId().getKind());
                row.put("type_id", this.getTypeId().getId());
            } else {
                row.putNull("type_kind");
                row.putNull("type_id");
            }
        } catch (Exception e) {
            Log.d("***COMPONENT", Log.getStackTraceString(e));
        }
    }


    // >> STATIC METHODS
    // ------------------------------------------------------------------------------------------

    /**
     * Create a component from Yaml.
     * @param componentYaml The yaml object.
     * @return The parsed component.
     */
    public static Component fromYaml(UUID groupId, Map<String, Object> componentYaml)
    {
        String componentType = (String) componentYaml.get("type");

        switch (componentType)
        {
            case "text":
                return Text.fromYaml(groupId, componentYaml);
            case "image":
                return Image.fromYaml(groupId, componentYaml);
            case "integer":
                return NumberInteger.fromYaml(groupId, componentYaml);
            case "table":
                return Table.fromYaml(groupId, componentYaml);
            case "boolean":
                return Bool.fromYaml(groupId, componentYaml);
        }

        return null;
    }



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


        if (this.getShowLabel())
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
        Boolean showLabel = true;
        Integer row = null;
        Integer column = null;
        Integer width = null;
        Alignment alignment = null;

        // Parse Format
        Map<String,Object> formatYaml = (Map<String,Object>) componentYaml.get("format");

        // >> Label
        if (formatYaml.containsKey("label"))
            label = (String) formatYaml.get("label");

        // >> Show Label
        if (formatYaml.containsKey("show_label"))
            showLabel = (Boolean) formatYaml.get("show_label");


        // >> Row
        if (formatYaml.containsKey("row"))
            row = (Integer) formatYaml.get("row");

        // >> Column
        if (formatYaml.containsKey("column"))
            column = (Integer) formatYaml.get("column");

        // >> Width
        if (formatYaml.containsKey("width"))
            width = (Integer) formatYaml.get("width");

        // >> Alignment
        if (formatYaml.containsKey("alignment"))
            alignment = Alignment.fromString((String) formatYaml.get("alignment"));

        return new Format(label, showLabel, row, column, width, alignment);
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
            if (textSize != null)
                return TextSize.valueOf(textSize.toUpperCase());
            return null;
        }

    }


    public enum Alignment
    {
        LEFT,
        CENTER,
        RIGHT;

        public static Alignment fromString(String alignment)
        {
            if (alignment != null)
                return Alignment.valueOf(alignment.toUpperCase());
            return Alignment.LEFT;
        }

    }


    public static class Format implements Serializable
    {

        // > PROPERTIES
        // --------------------------------------------------------------------------------------

        private String label;
        private Boolean showLabel;
        private Integer row;
        private Integer column;
        private Integer width;
        private Alignment alignment;


        // > CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public Format(String label, Boolean showLabel, Integer row, Integer column, Integer width,
                      Alignment alignment)
        {
            this.label = label;

            if (showLabel != null) {
                this.showLabel = showLabel;
            } else {
                this.showLabel = true;
            }

            this.row = row;
            this.column = column;
            this.width = width;
            this.alignment = alignment;
        }


        // > API
        // --------------------------------------------------------------------------------------

        public String getLabel() {
            return this.label;
        }

//        public void setLabel(String label) {
//            this.label = label;
//        }

        public Boolean getShowLabel() {
            return this.showLabel;
        }

//        public void setShowLabel(Boolean showLabel) {
//            this.showLabel = showLabel;
//        }

        public Integer getRow() {
            return this.row;
        }

//        public void setRow(Integer row) {
//            this.row = row;
//        }

        public Integer getColumn() {
            return this.column;
        }

//        public void setColumn(Integer column) {
//            this.column = column;
//        }

        public Integer getWidth() {
            return this.width;
        }

//        public void setWidth(Integer width) {
//            this.width = width;
//        }

        public Alignment getAlignment() {
            return this.alignment;
        }

//        public void setAlignment(Alignment alignment) {
//            this.alignment = alignment;
//        }

    }

}
