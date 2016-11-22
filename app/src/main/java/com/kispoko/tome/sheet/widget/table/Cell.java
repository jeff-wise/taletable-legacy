
package com.kispoko.tome.sheet.widget.table;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.sheet.widget.BooleanWidget;
import com.kispoko.tome.sheet.widget.NumberWidget;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.sheet.widget.util.WidgetFormat;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Table Widget Cell
 */
public class Cell implements Model, Serializable
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private UUID                    id;

    private PrimitiveValue<Integer> rowIndex;
    private PrimitiveValue<Integer> columnIndex;
    private ModelValue<Widget>      widget;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------


    public Cell(UUID id, Integer rowIndex, Integer columnIndex, Widget widget, Cell template)
    {
        this.id          = id;

        this.rowIndex    = new PrimitiveValue<>(rowIndex, this, Integer.class);
        this.columnIndex = new PrimitiveValue<>(columnIndex, this, Integer.class);
        this.widget      = new ModelValue<>(widget, this, Widget.class);

        this.initializeFromTemplate(template);
    }


    public static Cell fromYaml(Yaml yaml, int rowIndex, int columnIndex, Cell template)
                  throws YamlException
    {
        UUID   id     =  UUID.randomUUID();
        Widget widget = Widget.fromYaml(yaml.atKey("widget"));

        return new Cell(id, rowIndex, columnIndex, widget, template);
    }


    // API
    // --------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     * @param id The new model UUID.
     */
    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onModelUpdate(String valueName) { }


    // > State
    // ------------------------------------------------------------------------------------------

    public Widget getWidget()
    {
        return this.widget.getValue();
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    public View getView(Context context)
    {
        View view = new TextView(context);

        Widget widget = this.widget.getValue();
        if (widget instanceof TextWidget || widget instanceof NumberWidget) {
            view = this.textView(context);
        } else if (widget instanceof BooleanWidget) {
            view = this.boolView(context);
        }

        TableRow.LayoutParams layoutParams = (TableRow.LayoutParams) view.getLayoutParams();

        // Configure alignment
        WidgetFormat.Alignment alignment = this.getWidget().data().getFormat().getAlignment();
        if (alignment != null) {
            switch (alignment) {
                case LEFT:
                    layoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                    break;
                case CENTER:
                    Log.d("***CELL", "setting center alignment");
                    layoutParams.gravity = Gravity.CENTER | Gravity.CENTER_VERTICAL;
                    break;
                case RIGHT:
                    layoutParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                    break;
            }
        }

        // Configure column width
        Integer width = this.getWidget().data().getFormat().getWidth();
        if (width != null) {
            layoutParams.width = 0;
            layoutParams.weight = 1;
        }

        return view;
    }


    private View textView(Context context)
    {
        TextView view = new TextView(context);

        TableRow.LayoutParams layoutParams =
                new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                          TableRow.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 0);
        view.setLayoutParams(layoutParams);

        WidgetFormat.Alignment alignment = this.getWidget().data().getFormat().getAlignment();
        if (alignment != null) {
            switch (alignment) {
                case LEFT:
                    view.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    break;
                case CENTER:
                    view.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
                    break;
                case RIGHT:
                    view.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                    break;
            }
        }

        view.setPadding(0, 0, 0, 0);

        String widgetValue = ((TextWidget) this.getWidget()).getValue().getString();
        view.setText(widgetValue);

        view.setTextColor(ContextCompat.getColor(context, R.color.text_medium_light));
        view.setTypeface(Util.serifFontBold(context));

        float textSize = Util.getDim(context, R.dimen.comp_table_cell_text_size);
        view.setTextSize(textSize);

        return view;
    }


    private View boolView(final Context context)
    {
        final ImageView view = new ImageView(context);

        final BooleanWidget booleanWidget = ((BooleanWidget) this.widget.getValue());
        final Boolean widgetValue = booleanWidget.getValue().getBoolean();

        if (booleanWidget.getValue() != null) {
            if (widgetValue) {
                view.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_boolean_true));
            } else {
                view.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_boolean_false));
            }
        }

        TableRow.LayoutParams layoutParams =
                new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                          TableRow.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (widgetValue) {
                    booleanWidget.getValue().setBoolean(false);
                    view.setImageDrawable(
                            ContextCompat.getDrawable(context, R.drawable.ic_boolean_false));
                } else {
                    booleanWidget.getValue().setBoolean(true);
                    view.setImageDrawable(
                            ContextCompat.getDrawable(context, R.drawable.ic_boolean_true));
                }
            }
        });

        return view;
    }



    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initializeFromTemplate(Cell template)
    {
        if (template == null) return;

        WidgetFormat templateFormat = template.getWidget().data().getFormat();

        WidgetFormat thisFormat = this.getWidget().data().getFormat();

        if (thisFormat.getLabel() == null)
            thisFormat.setLabel(templateFormat.getLabel());

        if (thisFormat.getShowLabel() == null)
            thisFormat.setShowLabel(templateFormat.getShowLabel());

        if (thisFormat.getRow() == null)
            thisFormat.setRow(templateFormat.getRow());

        if (thisFormat.getColumn() == null)
            thisFormat.setColumn(templateFormat.getColumn());

        if (thisFormat.getWidth() == null)
            thisFormat.setWidth(templateFormat.getWidth());

        if (thisFormat.getAlignment() == null) {
            thisFormat.setAlignment(templateFormat.getAlignment());
        }

        // > Initialize Widget-specific data

        // ** Boolean Widget
        if (this.widget.getValue() instanceof BooleanWidget)
        {
            BooleanWidget booleanWidget = (BooleanWidget) this.widget.getValue();
            if (booleanWidget.getValue() == null) {
                booleanWidget.getValue().setBoolean(
                        ((BooleanWidget) template.getWidget()).getValue().getBoolean());
            }
        }

    }

}

