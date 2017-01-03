
package com.kispoko.tome.sheet.widget.table.cell;


import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.BooleanVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.table.column.BooleanColumn;
import com.kispoko.tome.sheet.widget.util.WidgetContainer;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Boolean CellUnion
 */
public class BooleanCell implements Model, Cell, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<BooleanVariable>   valueVariable;
    private PrimitiveFunctor<CellAlignment> alignment;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                         valueViewId;

    private WidgetContainer                 widgetContainer;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BooleanCell()
    {
        this.id        = null;

        this.valueVariable = ModelFunctor.empty(BooleanVariable.class);
        this.alignment = new PrimitiveFunctor<>(null, CellAlignment.class);
    }


    public BooleanCell(UUID id,
                       BooleanVariable valueVariable,
                       CellAlignment alignment,
                       BooleanColumn column)
    {
        // ** Id
        this.id        = id;

        // ** Value
        if (valueVariable == null) {
            valueVariable = BooleanVariable.asBoolean(UUID.randomUUID(),
                                              column.getDefaultValue());
        }
        this.valueVariable = ModelFunctor.full(valueVariable, BooleanVariable.class);

        // ** Alignment
        this.alignment = new PrimitiveFunctor<>(alignment, CellAlignment.class);

        initializeBooleanCell();
    }


    public static BooleanCell fromYaml(Yaml yaml, BooleanColumn column)
                  throws YamlException
    {
        UUID            id        = UUID.randomUUID();
        BooleanVariable value     = BooleanVariable.fromYaml(yaml.atMaybeKey("value"));
        CellAlignment   alignment = CellAlignment.fromYaml(yaml.atMaybeKey("alignment"));

        return new BooleanCell(id, value, alignment, column);
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
     * This method is called when the Boolean Cell is completely loaded for the first time.
     */
    public void onLoad()
    {
        initializeBooleanCell();
    }


    // > Cell
    // ------------------------------------------------------------------------------------------

    /**
     * Set the cells widget container (which is the parent Table Row).
     * @param widgetContainer The widget container.
     */
    public void initialize(WidgetContainer widgetContainer)
    {
        // [1] Set the widget container
        // --------------------------------------------------------------------------------------

        this.widgetContainer = widgetContainer;

        // [2] Initialize the value variable
        // --------------------------------------------------------------------------------------

        // > If the variable is non-null
        if (!this.valueVariable.isNull())
        {
            this.valueVariable().initialize();

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

    /**
     * Get the boolean variable that contains the value of the boolean cell.
     * @return The Number Variable value.
     */
    public BooleanVariable valueVariable()
    {
        return this.valueVariable.getValue();
    }


    public Boolean value()
    {
        if (!this.valueVariable.isNull())
            return this.valueVariable().value();
        return null;
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

    public View view(BooleanColumn column)
    {
        final Context context = SheetManager.currentSheetContext();

        final ImageView view = new ImageView(context);

        Boolean value = this.value();

        if (value == null)
            value = column.getDefaultValue();

        if (value) {
            view.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_boolean_true));
        } else {
            view.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_boolean_false));
        }

        TableRow.LayoutParams layoutParams =
                new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                          TableRow.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (value()) {
                    valueVariable().setValue(false);
                    view.setImageDrawable(
                            ContextCompat.getDrawable(context, R.drawable.ic_boolean_false));
                } else {
                    valueVariable().setValue(true);
                    view.setImageDrawable(
                            ContextCompat.getDrawable(context, R.drawable.ic_boolean_true));
                }
            }
        });

        return view;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text cell state.
     */
    private void initializeBooleanCell()
    {
        this.valueViewId   = null;
        this.widgetContainer = null;
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

            Boolean value = this.value();

            // TODO can value be null
            if (value != null)
                textView.setText(Boolean.toString(value));
        }
    }



}
