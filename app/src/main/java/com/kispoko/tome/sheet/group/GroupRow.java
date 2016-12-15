
package com.kispoko.tome.sheet.group;


import android.content.Context;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.BooleanWidget;
import com.kispoko.tome.sheet.widget.ImageWidget;
import com.kispoko.tome.sheet.widget.NumberWidget;
import com.kispoko.tome.sheet.widget.TableWidget;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Row
 *
 * A row of widgets in a group.
 */
public class GroupRow implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                         id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveValue<RowAlignment> alignment;
    private PrimitiveValue<RowWidth>     width;
    private CollectionValue<Widget>      widgets;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public GroupRow()
    {
        this.id           = null;

        this.alignment    = new PrimitiveValue<>(null, RowAlignment.class);
        this.width        = new PrimitiveValue<>(null, RowWidth.class);

        List<Class<? extends Widget>> widgetClasses = new ArrayList<>();
        widgetClasses.add(TextWidget.class);
        widgetClasses.add(NumberWidget.class);
        widgetClasses.add(BooleanWidget.class);
        widgetClasses.add(TableWidget.class);
        widgetClasses.add(ImageWidget.class);
        this.widgets      = CollectionValue.empty(widgetClasses);
    }


    public GroupRow(UUID id, List<Widget> widgets, RowAlignment alignment, RowWidth width)
    {
        this.id           = id;

        this.alignment    = new PrimitiveValue<>(alignment, RowAlignment.class);
        this.width        = new PrimitiveValue<>(width, RowWidth.class);

        List<Class<? extends Widget>> widgetClasses = new ArrayList<>();
        widgetClasses.add(TextWidget.class);
        widgetClasses.add(NumberWidget.class);
        widgetClasses.add(BooleanWidget.class);
        widgetClasses.add(TableWidget.class);
        widgetClasses.add(ImageWidget.class);
        this.widgets      = CollectionValue.full(widgets, widgetClasses);
    }


    /**
     * Create a row from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The new row.
     * @throws YamlException
     */
    public static GroupRow fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID         id        = UUID.randomUUID();

        RowAlignment alignment = RowAlignment.fromYaml(yaml.atMaybeKey("alignment"));
        RowWidth     width     = RowWidth.fromYaml(yaml.atMaybeKey("width"));

        List<Widget> widgets   = yaml.atKey("widgets").forEach(new Yaml.ForEach<Widget>() {
            @Override
            public Widget forEach(Yaml yaml, int index) throws YamlException {
                return Widget.fromYaml(yaml);
            }
        });

        return new GroupRow(id, widgets, alignment, width);
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


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Widgets
    // ------------------------------------------------------------------------------------------

    /**
     * Get the widgets in the row.
     * @return A list of widgets.
     */
    public List<Widget> widgets()
    {
        return this.widgets.getValue();
    }


    // ** Width
    // ------------------------------------------------------------------------------------------

    /**
     * Get the width of the row. The width is a value between 1 and 100, and represents a
     * percentage width of the screen that the row should span.
     * @return The width.
     */
    public RowWidth width()
    {
        return this.width.getValue();
    }



    // > Views
    // ------------------------------------------------------------------------------------------

    public LinearLayout view()
    {
        Context context = SheetManager.currentSheetContext();

        LinearLayout layout = this.layout(context);

        for (Widget widget : this.widgets())
        {
            int weight = widget.data().getFormat().getWidth();
            LinearLayout tileLayout = this.tileLayout(weight, context);

            tileLayout.addView(widget.tileView());

            layout.addView(tileLayout);
        }

        return layout;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private LinearLayout layout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation      = LinearLayout.HORIZONTAL;
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.padding.bottom   = R.dimen.group_row_padding_bottom;

        // Row Width
        switch (this.width())
        {
            case THREE_QUARTERS:
                layout.padding.left  = R.dimen.group_row_three_quarters_padding;
                layout.padding.right = R.dimen.group_row_three_quarters_padding;
                break;
            case HALF:
                layout.padding.left  = R.dimen.group_row_half_padding;
                layout.padding.right = R.dimen.group_row_half_padding;
                break;
        }

        return layout.linearLayout(context);
    }


    private LinearLayout tileLayout(Integer weight, Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.orientation  = LinearLayout.VERTICAL;
        layout.width        = 0;
        layout.weight       = weight.floatValue();

        return layout.linearLayout(context);
    }


}
