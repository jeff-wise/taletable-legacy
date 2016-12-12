
package com.kispoko.tome.sheet;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.widget.BooleanWidget;
import com.kispoko.tome.sheet.widget.ImageWidget;
import com.kispoko.tome.sheet.widget.NumberWidget;
import com.kispoko.tome.sheet.widget.TableWidget;
import com.kispoko.tome.sheet.widget.TextWidget;
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

    private UUID                    id;
    private PrimitiveValue<String>  label;
    private CollectionValue<Widget> widgets;
    private PrimitiveValue<Integer> numberOfRows;
    private PrimitiveValue<Integer> index;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Group()
    {
        this.id           = null;

        this.label        = new PrimitiveValue<>(null, String.class);
        this.index        = new PrimitiveValue<>(null, Integer.class);
        this.numberOfRows = new PrimitiveValue<>(null, Integer.class);

        List<Class<? extends Widget>> widgetClasses = new ArrayList<>();
        widgetClasses.add(TextWidget.class);
        widgetClasses.add(NumberWidget.class);
        widgetClasses.add(BooleanWidget.class);
        widgetClasses.add(TableWidget.class);
        widgetClasses.add(ImageWidget.class);
        this.widgets      = CollectionValue.empty(widgetClasses);
    }


    public Group(UUID id, String label, Integer index, Integer numberOfRows,
                 List<Widget> widgets)
    {
        this.id           = id;

        this.label        = new PrimitiveValue<>(label, String.class);
        this.index        = new PrimitiveValue<>(index, Integer.class);
        this.numberOfRows = new PrimitiveValue<>(numberOfRows, Integer.class);

        List<Class<? extends Widget>> widgetClasses = new ArrayList<>();
        widgetClasses.add(TextWidget.class);
        widgetClasses.add(NumberWidget.class);
        widgetClasses.add(BooleanWidget.class);
        widgetClasses.add(TableWidget.class);
        widgetClasses.add(ImageWidget.class);
        this.widgets      = CollectionValue.full(widgets, widgetClasses);
    }


    @SuppressWarnings("unchecked")
    public static Group fromYaml(Yaml yaml, int groupIndex)
                  throws YamlException
    {
        UUID    id           = UUID.randomUUID();
        String  label        = yaml.atKey("label").getString();
        Integer index        = groupIndex;
        Integer numberOfRows = yaml.atKey("number_of_rows").getInteger();

        List<Widget> groups = yaml.atKey("widgets").forEach(new Yaml.ForEach<Widget>() {
            @Override
            public Widget forEach(Yaml yaml, int index) throws YamlException {
                return Widget.fromYaml(yaml);

            }
        });

        return new Group(id, label, index, numberOfRows, groups);
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


    // ** Widgets
    // ------------------------------------------------------------------------------------------

    /**
     * Get the widgets in the group.
     * @return The group's widgets.
     */
    public List<Widget> getWidgets()
    {
        return this.widgets.getValue();
    }


    // ** Number Of Rows
    // ------------------------------------------------------------------------------------------


    /**
     * Get the number of rows in this group.
     * @return Number of rows in group.
     */
    public Integer getNumberOfRows()
    {
        return this.numberOfRows.getValue();
    }



    // > View
    // ------------------------------------------------------------------------------------------

    public View view(Context context)
    {
        LinearLayout groupLayout = new LinearLayout(context);
        LinearLayout.LayoutParams mainLayoutParams =
               new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.MATCH_PARENT);
        int groupHorzMargins = (int) Util.getDim(context, R.dimen.group_horz_margins);
        int groupMarginBottom = (int) Util.getDim(context, R.dimen.group_margin_bottom);
        groupLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_grey_7));
        mainLayoutParams.setMargins(groupHorzMargins, 0,
                                    groupHorzMargins, groupMarginBottom);
        groupLayout.setOrientation(LinearLayout.VERTICAL);
        groupLayout.setLayoutParams(mainLayoutParams);

        int groupPaddingTop = (int) Util.getDim(context, R.dimen.group_padding_top);
        int groupPaddingHorz = (int) Util.getDim(context, R.dimen.group_padding_horz);
        groupLayout.setPadding(groupPaddingHorz, groupPaddingTop, groupPaddingHorz, 0);


        List<List<Widget>> rows = new ArrayList<>();
        for (int i = 0; i  < this.getNumberOfRows(); i++) {
            rows.add(new ArrayList<Widget>());
        }

        // Sort by row
        for (Widget widget : this.getWidgets())
        {
            int rowIndex = widget.data().getFormat().getRow() - 1;
            rows.get(rowIndex).add(widget);
        }

        // Sort by column
        for (int j = 0; j < this.getNumberOfRows(); j++) {
            List<Widget> row = rows.get(j);
            Collections.sort(row, new Comparator<Widget>() {
                @Override
                public int compare(Widget c1, Widget c2) {
                    Integer c1Column = c1.data().getFormat().getColumn();
                    Integer c2Column = c2.data().getFormat().getColumn();
                    if (c1Column > c2Column)
                        return 1;
                    if (c1Column < c2Column)
                        return -1;
                    return 0;
                }
            });
        }

        groupLayout.addView(this.labelView(context));

        for (List<Widget> row : rows)
        {
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(Util.linearLayoutParamsMatch());
            int rowPaddingBottom = (int) context.getResources()
                                              .getDimension(R.dimen.row_padding_bottom);
            rowLayout.setPadding(0, 0, 0, rowPaddingBottom);

            for (Widget widget : row)
            {
                LinearLayout frameLayout = new LinearLayout(context);
                frameLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams frameLayoutParams = Util.linearLayoutParamsWrap();
                frameLayoutParams.width = 0;
                frameLayoutParams.weight = widget.data().getFormat().getWidth();
                frameLayout.setLayoutParams(frameLayoutParams);

                // Add WidgetData View
                View componentView = widget.tileView();
                frameLayout.addView(componentView);

                rowLayout.addView(frameLayout);
            }

            groupLayout.addView(rowLayout);
        }

        return groupLayout;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Views
    // ------------------------------------------------------------------------------------------

    private LinearLayout labelView(Context context)
    {
        // [1] Layout
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder labelLayout = new LinearLayoutBuilder();

        labelLayout.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        labelLayout.height         = LinearLayout.LayoutParams.WRAP_CONTENT;
        labelLayout.padding.left   = R.dimen.group_label_padding_left;
        labelLayout.padding.bottom = R.dimen.group_label_padding_bottom;

        // [2] Text
        // --------------------------------------------------------------------------------------

        TextViewBuilder labelView = new TextViewBuilder();

        labelView.id    = R.id.widget_label;
        labelView.size  = R.dimen.group_label_text_size;
        labelView.color = R.color.dark_grey_1;
        labelView.font  = Font.sansSerifFontRegular(context);
        labelView.text  = this.getLabel().toUpperCase();

        // [3] Define structure
        // --------------------------------------------------------------------------------------

        labelLayout.child(labelView.textView(context));

        return labelLayout.linearLayout(context);
    }

}
