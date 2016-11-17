
package com.kispoko.tome.sheet;


import android.content.Context;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.widget.BooleanWidget;
import com.kispoko.tome.sheet.widget.ImageWidget;
import com.kispoko.tome.sheet.widget.NumberWidget;
import com.kispoko.tome.sheet.widget.TableWidget;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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

    public Group(UUID id, String label, Integer index, Integer numberOfRows,
                 List<Widget> widgets)
    {
        this.id           = id;
        this.label        = new PrimitiveValue<>(label, this, String.class);
        this.index        = new PrimitiveValue<>(index, this, Integer.class);
        this.numberOfRows = new PrimitiveValue<>(numberOfRows, this, Integer.class);

        List<Class<Widget>> widgetClasses = Arrays.asList(TextWidget.class,
                                                          NumberWidget.class,
                                                          BooleanWidget.class,
                                                          TableWidget.class,
                                                          Image.class);
        this.widgets      = new CollectionValue<>(widgets, this, widgetClasses);
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
                String widgetType = yaml.atKey("type").getString();
                switch (widgetType)
                {
                    case "text":
                        return TextWidget.fromYaml(yaml);
                    case "number":
                        return NumberWidget.fromYaml(yaml);
                    case "boolean":
                        return BooleanWidget.fromYaml(yaml);
                    case "image":
                        return ImageWidget.fromYaml(yaml);
                    case "table":
                        return TableWidget.fromYaml(yaml);
                    default:
                        return null;
                }
            }
        });

        return new Group(id, label, index, numberOfRows, groups);
    }


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


    // ** Updates
    // ------------------------------------------------------------------------------------------

    public void onModelUpdate(String valueName) { }


    // > Views
    // ------------------------------------------------------------------------------------------

    public View getView(Context context, Rules rules)
    {
        LinearLayout groupLayout = new LinearLayout(context);
        LinearLayout.LayoutParams mainLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.MATCH_PARENT);
        int groupHorzMargins = (int) Util.getDim(context, R.dimen.group_horz_margins);
        int groupMarginBottom = (int) Util.getDim(context, R.dimen.group_margin_bottom);
        groupLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.sheet_medium));
        mainLayoutParams.setMargins(groupHorzMargins, 0,
                                    groupHorzMargins, groupMarginBottom);
        groupLayout.setOrientation(LinearLayout.VERTICAL);
        groupLayout.setLayoutParams(mainLayoutParams);

        int groupPaddingTop = (int) Util.getDim(context, R.dimen.group_padding_top);
        int groupPaddingHorz = (int) Util.getDim(context, R.dimen.group_padding_horz);
        groupLayout.setPadding(groupPaddingHorz, groupPaddingTop, groupPaddingHorz, 0);


        ArrayList<ArrayList<WidgetData>> rows = new ArrayList<>();
        for (int i = 0; i  < this.numberOfRows; i++) {
            rows.add(new ArrayList<WidgetData>());
        }

        // Sort by row
        for (WidgetData widgetData : this.widgetDatas)
        {
            int rowIndex = widgetData.getRow() - 1;
            rows.get(rowIndex).add(widgetData);
        }

        // Sort by column
        for (int j = 0; j < this.numberOfRows; j++) {
            ArrayList<WidgetData> row = rows.get(j);
            Collections.sort(row, new Comparator<WidgetData>() {
                @Override
                public int compare(WidgetData c1, WidgetData c2) {
                    if (c1.getColumn() > c2.getColumn())
                        return 1;
                    if (c1.getColumn() < c2.getColumn())
                        return -1;
                    return 0;
                }
            });
        }

        groupLayout.addView(this.labelView(context));

        for (ArrayList<WidgetData> row : rows)
        {
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(Util.linearLayoutParamsMatch());
            int rowPaddingBottom = (int) context.getResources()
                                              .getDimension(R.dimen.row_padding_bottom);
            rowLayout.setPadding(0, 0, 0, rowPaddingBottom);

            for (WidgetData widgetData : row)
            {
                LinearLayout frameLayout = new LinearLayout(context);
                frameLayout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams frameLayoutParams = Util.linearLayoutParamsWrap();
                frameLayoutParams.width = 0;
                frameLayoutParams.weight = widgetData.getWidth();
                frameLayout.setLayoutParams(frameLayoutParams);

                // Add WidgetData View
                View componentView = widgetData.getDisplayView(context, rules);
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
        LinearLayout layout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = Util.linearLayoutParamsMatchWrap();
        layout.setLayoutParams(layoutParams);

        int paddingLeft = (int) Util.getDim(context, R.dimen.group_label_padding_left);
        int paddingBottom = (int) Util.getDim(context, R.dimen.group_label_padding_bottom);
        layout.setPadding(paddingLeft, 0, 0, paddingBottom);


        TextView textView = new TextView(context);
        textView.setId(R.id.component_label);

        float labelTextSize = (int) Util.getDim(context, R.dimen.group_label_text_size);
        textView.setTextSize(labelTextSize);

        textView.setTextColor(ContextCompat.getColor(context, R.color.text_light));

        textView.setTypeface(Util.sansSerifFontRegular(context));

        //textView.setText(this.label.toUpperCase());
        textView.setText(this.label);

        layout.addView(textView);

        return layout;
    }

}
