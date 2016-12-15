
package com.kispoko.tome.sheet.group;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.BooleanWidget;
import com.kispoko.tome.sheet.widget.ImageWidget;
import com.kispoko.tome.sheet.widget.NumberWidget;
import com.kispoko.tome.sheet.widget.TableWidget;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.util.Util;
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
public class Row implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                         id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveValue<RowAlignment> alignment;
    private PrimitiveValue<Integer>      width;
    private CollectionValue<Widget>      widgets;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Row()
    {
        this.id           = null;

        this.alignment    = new PrimitiveValue<>(null, RowAlignment.class);
        this.width        = new PrimitiveValue<>(null, Integer.class);

        List<Class<? extends Widget>> widgetClasses = new ArrayList<>();
        widgetClasses.add(TextWidget.class);
        widgetClasses.add(NumberWidget.class);
        widgetClasses.add(BooleanWidget.class);
        widgetClasses.add(TableWidget.class);
        widgetClasses.add(ImageWidget.class);
        this.widgets      = CollectionValue.empty(widgetClasses);
    }


    public Row(UUID id, List<Widget> widgets, RowAlignment alignment, Integer width)
    {
        this.id           = id;

        this.alignment    = new PrimitiveValue<>(alignment, RowAlignment.class);
        this.width        = new PrimitiveValue<>(width, Integer.class);

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
    public static Row fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID         id        = UUID.randomUUID();

        RowAlignment alignment = RowAlignment.fromYaml(yaml.atMaybeKey("alignment"));
        Integer      width     = yaml.atMaybeKey("width").getInteger();

        List<Widget> widgets   = yaml.atKey("widgets").forEach(new Yaml.ForEach<Widget>() {
            @Override
            public Widget forEach(Yaml yaml, int index) throws YamlException {
                return Widget.fromYaml(yaml);
            }
        });

        return new Row(id, widgets, alignment, width);
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
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.padding.bottom   = R.dimen.group_row_padding_bottom;

        return layout.linearLayout(context);
    }


    private LinearLayout tileLayout(int weight, Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.orientation  = LinearLayout.VERTICAL;
        layout.width        = 0;
        layout.weight       = weight;

        return layout.linearLayout(context);
    }


}
