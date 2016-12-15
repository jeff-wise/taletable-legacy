
package com.kispoko.tome.sheet.group;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;



/**
 * Group
 */
public class Group implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                    id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveValue<String>  label;
    private PrimitiveValue<Integer> index;
    private CollectionValue<Row>    rows;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Group()
    {
        this.id           = null;

        this.label        = new PrimitiveValue<>(null, String.class);
        this.index        = new PrimitiveValue<>(null, Integer.class);

        List<Class<? extends Row>> rowClasses = new ArrayList<>();
        rowClasses.add(Row.class);
        this.rows         = CollectionValue.empty(rowClasses);
    }


    public Group(UUID id, String label, Integer index, List<Row> rows)
    {
        this.id           = id;

        this.label        = new PrimitiveValue<>(label, String.class);
        this.index        = new PrimitiveValue<>(index, Integer.class);

        List<Class<? extends Row>> rowClasses = new ArrayList<>();
        rowClasses.add(Row.class);
        this.rows         = CollectionValue.full(rows, rowClasses);
    }


    @SuppressWarnings("unchecked")
    public static Group fromYaml(Yaml yaml, int groupIndex)
                  throws YamlException
    {
        UUID      id    = UUID.randomUUID();
        String    label = yaml.atMaybeKey("label").getString();
        Integer   index = groupIndex;

        List<Row> rows  = yaml.atKey("rows").forEach(new Yaml.ForEach<Row>() {
            @Override
            public Row forEach(Yaml yaml, int index) throws YamlException {
                return Row.fromYaml(yaml);
            }
        });

        return new Group(id, label, index, rows);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
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


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Roleplay is completely loaded for the first time.
     */
    public void onLoad() { }


    // ** Updates
    // ------------------------------------------------------------------------------------------

    public void onValueUpdate(String valueName) { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Label
    // ------------------------------------------------------------------------------------------

    /**
     * Get the group label.
     * @return The group label String.
     */
    public String getLabel()
    {
        return this.label.getValue();
    }


    // ** Index
    // ------------------------------------------------------------------------------------------

    /**
     * Get the group's index (starting at 0) , which is its position in the page.
     * @return The group index.
     */
    public Integer getIndex()
    {
        return this.index.getValue();
    }


    // ** Rows
    // ------------------------------------------------------------------------------------------

    /**
     * Get the group's rows.
     * @return A list of rows.
     */
    public List<Row> rows()
    {
        return this.rows.getValue();
    }


    // > View
    // ------------------------------------------------------------------------------------------

    public View view()
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        Context context = SheetManager.currentSheetContext();

        // [2] Structure
        // --------------------------------------------------------------------------------------

        LinearLayout layout = this.layout(context);

        if (!this.label.isNull())
            layout.addView(this.labelView(context));

        for (Row row : this.rows()) {
            layout.addView(row.view());
        }

        return layout;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Views
    // ------------------------------------------------------------------------------------------

    private LinearLayout layout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.orientation      = LinearLayout.VERTICAL;
        layout.margin.bottom    = R.dimen.group_margin_bottom;
        layout.padding.left     = R.dimen.group_padding_horz;
        layout.padding.right    = R.dimen.group_padding_horz;

        return layout.linearLayout(context);
    }


    private LinearLayout labelView(Context context)
    {
        // [1] Views
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder labelLayout = new LinearLayoutBuilder();
        TextViewBuilder labelView = new TextViewBuilder();

        // [2 A] Layout
        // --------------------------------------------------------------------------------------

        labelLayout.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        labelLayout.height         = LinearLayout.LayoutParams.WRAP_CONTENT;
        labelLayout.padding.left   = R.dimen.group_label_padding_left;
        labelLayout.padding.bottom = R.dimen.group_label_padding_bottom;

        labelLayout.child(labelView);

        // [2 B] Text
        // --------------------------------------------------------------------------------------

        labelView.id    = R.id.widget_label;
        labelView.size  = R.dimen.group_label_text_size;
        labelView.color = R.color.grey_8;
        labelView.font  = Font.sansSerifFontRegular(context);
        labelView.text  = this.getLabel().toUpperCase();


        return labelLayout.linearLayout(context);
    }

}
