
package com.kispoko.tome.sheet.widget.table.cell;


import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.programming.summation.SummationException;
import com.kispoko.tome.engine.programming.variable.NumberVariable;
import com.kispoko.tome.engine.programming.variable.Variable;
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

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;

    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelValue<NumberVariable>    value;
    private PrimitiveValue<CellAlignment> alignment;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                       valueViewId;


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

        initialize();
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
    public void onLoad()
    {
        initialize();
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the value of this number cell which is a number variable.
     * @return The Number Variable value.
     */
    public NumberVariable value()
    {
        return this.value.getValue();
    }


    /**
     * Get the alignment of this cell.
     * @return The cell Alignment.
     */
   public CellAlignment alignment()
    {
        return this.alignment.getValue();
    }


    // > View
    // ------------------------------------------------------------------------------------------

    public View view(NumberColumn column)
    {
        Context context = SheetManager.currentSheetContext();

        TextView view = new TextView(context);

        this.valueViewId = Util.generateViewId();
        view.setId(this.valueViewId);

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


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text cell state.
     */
    private void initialize()
    {
        // [1] Initialize variables with listeners to update the number widget views when the
        //     values of the variables change
        // --------------------------------------------------------------------------------------

        this.valueViewId = null;

        if (!this.value.isNull())
        {
            this.value().addOnUpdateListener(new Variable.OnUpdateListener() {
                @Override
                public void onUpdate() {
                    onValueUpdate();
                }
            });
        }

    }


    /**
     * When the text widget's value is updated.
     */
    private void onValueUpdate()
    {
        Log.d("***NUMBERCELL", "on value update called");
        if (this.valueViewId != null && !this.value.isNull())
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.valueViewId);

            try
            {
                Integer value = this.value().value();

                // TODO can value be null
                if (value != null)
                    textView.setText(Integer.toString(value));
            }
            catch (SummationException exception)
            {
                ApplicationFailure.summation(exception);
            }
        }
    }


}
