
package com.kispoko.tome.sheet.widget.table.cell;


import com.kispoko.tome.sheet.widget.table.column.ColumnUnion;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;



/**
 * Table Widget CellUnion
 */
public class CellUnion implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                     id;

    private ModelValue<TextCell>     textCell;
    private ModelValue<NumberCell>   numberCell;
    private ModelValue<BooleanCell>  booleanCell;

    private PrimitiveValue<CellType> type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public CellUnion()
    {
        this.id          = null;

        this.textCell    = new ModelValue<>(null, TextCell.class);
        this.numberCell  = new ModelValue<>(null, NumberCell.class);
        this.booleanCell = new ModelValue<>(null, BooleanCell.class);

        this.type        = new PrimitiveValue<>(null, CellType.class);
    }


    private CellUnion(UUID id, Object cell, CellType type)
    {
        this.id          = id;


        this.textCell    = new ModelValue<>(null, TextCell.class);
        this.numberCell  = new ModelValue<>(null, NumberCell.class);
        this.booleanCell = new ModelValue<>(null, BooleanCell.class);

        this.type        = new PrimitiveValue<>(type, CellType.class);

        switch (type)
        {
            case TEXT:
                this.textCell.setValue((TextCell) cell);
                break;
            case NUMBER:
                this.numberCell.setValue((NumberCell) cell);
                break;
            case BOOLEAN:
                this.booleanCell.setValue((BooleanCell) cell);
                break;
        }
    }


    /**
     * Create the "text" variant.
     * @param id The Model id.
     * @param textCell The text cell.
     * @return The new CellUnion as the text case.
     */
    public static CellUnion asText(UUID id, TextCell textCell)
    {
        return new CellUnion(id, textCell, CellType.TEXT);
    }


    /**
     * Create the "number" variant.
     * @param id The Model id.
     * @param numberCell The number cell.
     * @return The new CellUnion as the number case.
     */
    public static CellUnion asNumber(UUID id, NumberCell numberCell)
    {
        return new CellUnion(id, numberCell, CellType.NUMBER);
    }


    /**
     * Create the "boolean" variant.
     * @param id The Model id.
     * @param booleanCell The boolean cell.
     * @return The new CellUnion as the boolean case.
     */
    public static CellUnion asBoolean(UUID id, BooleanCell booleanCell)
    {
        return new CellUnion(id, booleanCell, CellType.BOOLEAN);
    }


    /**
     * Create a Cell Union from its Yaml representation.
     * @param yaml The Yaml parser.
     * @param columnUnion The column the cell belongs to.
     * @return The parsed Cell Union.
     * @throws YamlException
     */
    public static CellUnion fromYaml(Yaml yaml, ColumnUnion columnUnion)
                  throws YamlException
    {
        UUID     id   = UUID.randomUUID();

        switch (columnUnion.getType())
        {
            case TEXT:
                TextCell textCell = TextCell.fromYaml(yaml);
                return CellUnion.asText(id, textCell);
            case NUMBER:
                NumberCell numberCell = NumberCell.fromYaml(yaml);
                return CellUnion.asNumber(id, numberCell);
            case BOOLEAN:
                BooleanCell booleanCell = BooleanCell.fromYaml(yaml);
                return CellUnion.asBoolean(id, booleanCell);
        }

        // CANNOT REACH HERE. If VariableType is null, an InvalidEnum exception would be thrown.
        return null;

    }


    // API
    // ------------------------------------------------------------------------------------------

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

    public void onValueUpdate(String valueName) { }







    // > Views
    // ------------------------------------------------------------------------------------------


    /*
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

*/


}

