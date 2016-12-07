
package com.kispoko.tome.sheet.widget.table.cell;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.rules.programming.summation.SummationException;
import com.kispoko.tome.rules.programming.variable.NumberVariable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.table.column.NumberColumn;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;



/**
 * Number CellUnion
 */
public class NumberCell implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private ModelValue<NumberVariable>    value;
    private PrimitiveValue<CellAlignment> alignment;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberCell()
    {
        this.id        = null;

        this.value     = ModelValue.empty(NumberVariable.class);
        this.alignment = new PrimitiveValue<>(null, CellAlignment.class);
    }


    public NumberCell(UUID id, NumberVariable value, CellAlignment alignment, NumberColumn column)
    {
        this.id        = id;

        if (value == null) {
            value = NumberVariable.asInteger(UUID.randomUUID(),
                                             null,
                                             column.getDefaultValue(),
                                             null);
        }
        this.value     = ModelValue.full(value, NumberVariable.class);

        this.alignment = new PrimitiveValue<>(alignment, CellAlignment.class);
    }


    public static NumberCell fromYaml(Yaml yaml, NumberColumn column)
                  throws YamlException
    {
        UUID          id        = UUID.randomUUID();
        NumberVariable value    = NumberVariable.fromYaml(yaml.atMaybeKey("value"));
        CellAlignment alignment = CellAlignment.fromYaml(yaml.atMaybeKey("alignment"));

        return new NumberCell(id, value, alignment, column);
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


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Number Cell is completely loaded for the first time.
     */
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the value of this number cell which is a number variable.
     * @return The Number Variable value.
     */
    public NumberVariable getValue()
    {
        return this.value.getValue();
    }


    /**
     * Get the alignment of this cell.
     * @return The cell Alignment.
     */
   public CellAlignment getAlignment()
    {
        return this.alignment.getValue();
    }


    // > View
    // ------------------------------------------------------------------------------------------

    public View view(NumberColumn column)
    {
        Context context = SheetManager.currentSheetContext();

        TextView view = new TextView(context);

        TableRow.LayoutParams layoutParams =
                new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                          TableRow.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 0);
        view.setLayoutParams(layoutParams);

        view.setPadding(0, 0, 0, 0);

        // > Set value
        // --------------------------------------------------------------------------------------
        Integer value = null;
        try {
            value = this.value.getValue().value();
        } catch (SummationException exception) {
            ApplicationFailure.summation(exception);
        }

        if (value != null)
            view.setText(Integer.toString(value));
        else
            view.setText(Integer.toString(column.getDefaultValue()));

        view.setTextColor(ContextCompat.getColor(context, R.color.text_medium_light));
        view.setTypeface(Util.serifFontBold(context));

        float textSize = Util.getDim(context, R.dimen.comp_table_cell_text_size);
        view.setTextSize(textSize);

        return view;
    }

}
