
package com.kispoko.tome.sheet.component;


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
import com.kispoko.tome.sheet.component.type.Bool;
import com.kispoko.tome.sheet.component.type.Image;
import com.kispoko.tome.sheet.component.type.Number;
import com.kispoko.tome.sheet.component.type.Table;
import com.kispoko.tome.sheet.component.type.Text;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.Unique;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.yaml.YamlException;

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

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // General
    private UUID id;
    private String name;
    private UUID groupId;
    private Type.Id typeId;
    private List<String> actions;
    private Format format;


    // INTERFACE
    // ------------------------------------------------------------------------------------------

    abstract public View getDisplayView(Context context, Rules rules);
    abstract public View getEditorView(Context context, Rules rules);

    abstract public void runAction(String actionName, Context context, Rules rules);

    abstract public String componentName();

    abstract public void save(UUID callerTrackerId);
    abstract public void load(UUID callerTrackerId);


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Component(UUID id,
                     String name,
                     UUID groupId,
                     Type.Id typeId,
                     Format format,
                     List<String> actions)
    {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.typeId = typeId;
        this.actions = actions;
        this.format = format;
    }


    public static Component empty(UUID id, UUID groupId, String kind)
    {
        switch (kind)
        {
            case "text":
                return new Text(id, groupId);
            case "integer":
                return new Number(id, groupId);
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



    // API
    // ------------------------------------------------------------------------------------------

    // > State
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** Name
    // ------------------------------------------------------------------------------------------

    public String getName()
    {
        return this.name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    // ** Group Id
    // ------------------------------------------------------------------------------------------

    public UUID getGroupId()
    {
        return this.groupId;
    }

    public void setGroupId(UUID groupId)
    {
        this.groupId = groupId;
    }


    // ** Type Id
    // ------------------------------------------------------------------------------------------

    public Type.Id getTypeId()
    {
        return this.typeId;
    }


    public void setTypeId(Type.Id typeId)
    {
        this.typeId = typeId;
    }


    // ** Format
    // ------------------------------------------------------------------------------------------

    public Format getFormat()
    {
        return this.format;
    }


    public void setFormat(Format format)
    {
        this.format = format;
    }


    // ** Actions
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


    protected void putComponentSQLRows(ContentValues row)
    {
        try
        {
            row.put("component_id", this.getId().toString());
            row.put("name", this.getName());
            row.put("value", this.getValue().getId().toString());
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


    // CONSTRAINTS
    // ------------------------------------------------------------------------------------------

    protected boolean constraint_isLoaded()
    {
        return true;
    }


    protected boolean constraint_isSaved()
    {
        return true;
    }


    // >> STATIC METHODS
    // ------------------------------------------------------------------------------------------

    /**
     * Create a component from Yaml.
     * @param componentYaml The yaml object.
     * @return The parsed component.
     */
    public static Component fromYaml(UUID groupId, Map<String, Object> componentYaml)
                  throws YamlException
    {
        String componentType = (String) componentYaml.get("type");

        switch (componentType)
        {
            case "text":
                return Text.fromYaml(groupId, componentYaml);
            case "image":
                return Image.fromYaml(groupId, componentYaml);
            case "integer":
                return Number.fromYaml(groupId, componentYaml);
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

}
