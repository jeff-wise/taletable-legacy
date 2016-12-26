
package com.kispoko.tome.sheet.widget.table.cell;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.programming.summation.SummationException;
import com.kispoko.tome.engine.programming.variable.NumberVariable;
import com.kispoko.tome.engine.programming.variable.Variable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.table.column.NumberColumn;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Number CellUnion
 */
public class NumberCell implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;

    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<NumberVariable> value;
    private PrimitiveFunctor<CellAlignment> alignment;
    private PrimitiveFunctor<String> prefix;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                       valueViewId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberCell()
    {
        this.id        = null;

        this.value     = ModelFunctor.empty(NumberVariable.class);
        this.alignment = new PrimitiveFunctor<>(null, CellAlignment.class);
        this.prefix    = new PrimitiveFunctor<>(null, String.class);
    }


    public NumberCell(UUID id,
                      NumberVariable value,
                      CellAlignment alignment,
                      NumberColumn column,
                      String prefix)
    {
        this.id        = id;

        if (value == null) {
            value = NumberVariable.asInteger(UUID.randomUUID(),
                                             null,
                                             column.getDefaultValue(),
                                             null);
        }
        this.value     = ModelFunctor.full(value, NumberVariable.class);

        this.alignment = new PrimitiveFunctor<>(alignment, CellAlignment.class);
        this.prefix    = new PrimitiveFunctor<>(prefix, String.class);

        initialize();
    }


    public static NumberCell fromYaml(Yaml yaml, NumberColumn column)
                  throws YamlException
    {
        UUID           id        = UUID.randomUUID();
        NumberVariable value     = NumberVariable.fromYaml(yaml.atMaybeKey("value"));
        CellAlignment  alignment = CellAlignment.fromYaml(yaml.atMaybeKey("alignment"));
        String         prefix    = yaml.atMaybeKey("prefix").getString();

        return new NumberCell(id, value, alignment, column, prefix);
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

    // ** Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the cell's value variable.
     * @return The NumberVariable.
     */
    public NumberVariable valueVariable()
    {
        return this.value.getValue();
    }


    /**
     * Get the cell's integer value.
     * @return The cell value.
     */
    public Integer value()
    {
        try
        {
            if (this.valueVariable() != null)
                return this.valueVariable().value();
            return null;
        }
        catch (SummationException exception)
        {
            ApplicationFailure.summation(exception);
        }

        return null;
    }


    /**
     * Get the cell's integer value as a string.
     * @return The cell's value as a string.
     */
    public String valueString()
    {
        Integer integerValue = this.value();

        if (integerValue != null)
        {
            String integerString = integerValue.toString();

            if (!this.prefix.isNull())
                integerString = this.prefix() + " " + integerString;

            return integerString;
        }

        return "";
    }


    // ** Alignment
    // ------------------------------------------------------------------------------------------

    /**
     * Get the alignment of this cell.
     * @return The cell Alignment.
     */
    public CellAlignment alignment()
    {
        return this.alignment.getValue();
    }


    // ** Prefix
    // ------------------------------------------------------------------------------------------

    public String prefix()
    {
        return this.prefix.getValue();
    }


    // > View
    // ------------------------------------------------------------------------------------------

    public View view(NumberColumn column)
    {
        // [1] Declarations
        // ------------------------------------------------------------------------------------------

        Context context = SheetManager.currentSheetContext();

        TextViewBuilder cellView = new TextViewBuilder();
        this.valueViewId = Util.generateViewId();

        // [2] Cell View
        // ------------------------------------------------------------------------------------------

        cellView.id         = this.valueViewId;
        cellView.layoutType = LayoutType.TABLE_ROW;
        cellView.width      = TableRow.LayoutParams.WRAP_CONTENT;
        cellView.height     = TableRow.LayoutParams.WRAP_CONTENT;
        cellView.color      = R.color.dark_blue_hl_3;
        cellView.font       = Font.serifFontRegular(context);
        cellView.size       = R.dimen.widget_table_cell_text_size;

        String valueString = this.valueString();
        if (valueString != null)
            cellView.text = valueString;
        else
            cellView.text = Integer.toString(column.getDefaultValue());


        return cellView.textView(context);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text cell state.
     */
    private void initialize()
    {
        // [1] The boolean cell's value view ID. It is null until the view is created.
        // --------------------------------------------------------------------------------------

        this.valueViewId = null;

        // [2] Initialize the value variable
        // --------------------------------------------------------------------------------------

        if (!this.value.isNull())
        {
            this.valueVariable().setOnUpdateListener(new Variable.OnUpdateListener() {
                @Override
                public void onUpdate() {
                    onValueUpdate();
                }
            });

            State.addVariable(this.valueVariable());
        }

    }


    /**
     * When the text widget's value is updated.
     */
    private void onValueUpdate()
    {
        if (this.valueViewId != null && !this.value.isNull())
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.valueViewId);

            if (this.value() != null)
                textView.setText(this.valueString());
        }
    }


}
