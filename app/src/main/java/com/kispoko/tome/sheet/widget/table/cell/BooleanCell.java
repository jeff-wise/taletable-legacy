
package com.kispoko.tome.sheet.widget.table.cell;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
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



/**
 * Boolean CellUnion
 */
public class BooleanCell implements Model, Cell, ToYaml, Serializable
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

    private String                          trueText;
    private String                          falseText;


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
                       CellAlignment alignment)
    {
        // ** Id
        this.id        = id;

        // ** Value
        this.valueVariable = ModelFunctor.full(valueVariable, BooleanVariable.class);

        // ** Alignment
        this.alignment = new PrimitiveFunctor<>(alignment, CellAlignment.class);

        initializeBooleanCell();
    }


    public static BooleanCell fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID            id        = UUID.randomUUID();

        BooleanVariable value     = BooleanVariable.fromYaml(yaml.atMaybeKey("value"));
        CellAlignment   alignment = CellAlignment.fromYaml(yaml.atMaybeKey("alignment"));

        return new BooleanCell(id, value, alignment);
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


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Boolean Cell's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("value", this.valueVariable())
                .putYaml("alignment", this.alignment());
    }


    // > Cell
    // ------------------------------------------------------------------------------------------

    /**
     * Set the cells widget container (which is the parent Table Row).
     * @param widgetContainer The widget container.
     */
    public void initialize(BooleanColumn column, WidgetContainer widgetContainer)
    {
        // [1] Set the widget container
        // --------------------------------------------------------------------------------------

        this.widgetContainer = widgetContainer;

        // [2] Initialize the value variable
        // --------------------------------------------------------------------------------------

        if (this.valueVariable.isNull()) {
            valueVariable.setValue(BooleanVariable.asBoolean(UUID.randomUUID(),
                                                             column.defaultValue()));
        }

        this.valueVariable().initialize();

        this.valueVariable().setOnUpdateListener(new Variable.OnUpdateListener()
        {
             @Override
             public void onUpdate() {
                onValueUpdate();
            }
        });

        State.addVariable(this.valueVariable());

        // [3] Save Column Data
        // --------------------------------------------------------------------------------------

        this.trueText           = column.trueText();
        this.falseText          = column.falseText();
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
    public CellAlignment alignment()
    {
        return this.alignment.getValue();
    }


    // > View
    // ------------------------------------------------------------------------------------------

    public View view(Context context)
    {
        final TextView valueView = valueView(context);

        valueView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (value()) {
                    valueVariable().setValue(false);
                    valueView.setText(falseText);
                } else {
                    valueVariable().setValue(true);
                    valueView.setText(trueText);
                }
            }
        });

        return valueView;
    }


    private TextView valueView(Context context)
    {
        TextViewBuilder value = new TextViewBuilder();

        value.layoutType        = LayoutType.TABLE_ROW;
        value.width             = TableRow.LayoutParams.WRAP_CONTENT;
        value.height            = TableRow.LayoutParams.WRAP_CONTENT;

        // > Value
        if (this.value())
            value.text          = trueText;
        else
            value.text          = falseText;

        value.font              = Font.serifFontRegular(context);
        value.size              = R.dimen.widget_table_cell_text_size;
        value.color             = R.color.dark_blue_hl_1;

        return value.textView(context);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text cell state.
     */
    private void initializeBooleanCell()
    {
        this.valueViewId        = null;
        this.widgetContainer    = null;
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
