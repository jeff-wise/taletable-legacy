
package com.kispoko.tome.sheet.widget.table.cell;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.summation.SummationException;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.engine.variable.VariableException;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.table.column.NumberColumn;
import com.kispoko.tome.sheet.widget.util.WidgetContainer;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.kispoko.tome.R.string.column;


/**
 * Number CellUnion
 */
public class NumberCell implements Model, Cell, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;

    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<NumberVariable>    valueVariable;
    private PrimitiveFunctor<CellAlignment> alignment;
    private PrimitiveFunctor<String>        prefix;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                         valueViewId;

    private WidgetContainer                 widgetContainer;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberCell()
    {
        this.id             = null;

        this.valueVariable  = ModelFunctor.empty(NumberVariable.class);
        this.alignment      = new PrimitiveFunctor<>(null, CellAlignment.class);
        this.prefix         = new PrimitiveFunctor<>(null, String.class);
    }


    public NumberCell(UUID id,
                      NumberVariable valueVariable,
                      CellAlignment alignment,
                      String prefix)
    {
        this.id             = id;

        this.valueVariable = ModelFunctor.full(valueVariable, NumberVariable.class);

        this.alignment      = new PrimitiveFunctor<>(alignment, CellAlignment.class);
        this.prefix         = new PrimitiveFunctor<>(prefix, String.class);

        initializeNumberCell();
    }


    public static NumberCell fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID           id        = UUID.randomUUID();

        NumberVariable value     = NumberVariable.fromYaml(yaml.atMaybeKey("value"));
        CellAlignment  alignment = CellAlignment.fromYaml(yaml.atMaybeKey("alignment"));
        String         prefix    = yaml.atMaybeKey("prefix").getString();

        return new NumberCell(id, value, alignment, prefix);
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
        initializeNumberCell();
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Number Cell's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("value", this.valueVariable())
                .putYaml("alignment", this.alignment())
                .putString("prefix", this.prefix());
    }


    // > Cell
    // ------------------------------------------------------------------------------------------

    /**
     * Set the cells widget container (which is the parent Table Row).
     * @param widgetContainer The widget container.
     */
    public void initialize(NumberColumn column, WidgetContainer widgetContainer)
    {
        // [1] Set the widget container
        // --------------------------------------------------------------------------------------

        this.widgetContainer = widgetContainer;

        // [2] Initialize the value variable
        // --------------------------------------------------------------------------------------

        // > If null, set default value
        if (this.valueVariable.isNull()) {
            valueVariable.setValue(NumberVariable.asInteger(UUID.randomUUID(),
                                                            column.defaultValue()));
        }

        // > Initialize the variable
        this.valueVariable().initialize();

        this.valueVariable().setOnUpdateListener(new Variable.OnUpdateListener() {
                                             @Override
                                             public void onUpdate() {
                onValueUpdate();
        }
    });

        State.addVariable(this.valueVariable());

    }


    /**
     * The cell's variables that may be in a namespace.
     * @return The variable list.
     */
    public List<Variable> namespacedVariables()
    {
        List<Variable> variables = new ArrayList<>();

        if (this.valueVariable().isNamespaced())
            variables.add(this.valueVariable());

        return variables;
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
        return this.valueVariable.getValue();
    }


    /**
     * Get the cell's integer value.
     * @return The cell value.
     */
    public Integer value()
    {
        if (this.valueVariable() != null)
        {
            try {
                return this.valueVariable().value();
            }
            catch (NullVariableException exception) {
                ApplicationFailure.nullVariable(exception);
            }
        }

        return 0;
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
        cellView.color      = R.color.dark_blue_hl_2;
        cellView.size       = R.dimen.widget_table_cell_text_size;

        // > Font
        if (column.isBold())
            cellView.font   = Font.serifFontBold(context);
        else
            cellView.font   = Font.serifFontRegular(context);

        String valueString = this.valueString();
        if (valueString != null)
            cellView.text = valueString;
        else
            cellView.text = Integer.toString(column.defaultValue());


        return cellView.textView(context);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text cell state.
     */
    private void initializeNumberCell()
    {
        // [1] The boolean cell's value view ID. It is null until the view is created.
        // --------------------------------------------------------------------------------------

        this.valueViewId = null;
        this.widgetContainer = null;

        // [2] Initialize the value variable
        // --------------------------------------------------------------------------------------

        // [3] Widget Container
        // --------------------------------------------------------------------------------------

    }


    /**
     * When the text widget's value is updated.
     */
    private void onValueUpdate()
    {
        if (this.valueViewId != null && !this.valueVariable.isNull())
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.valueViewId);

            if (this.value() != null && textView != null)
                textView.setText(this.valueString());
        }
    }


}
