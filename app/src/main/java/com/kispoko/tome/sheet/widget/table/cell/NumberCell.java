
package com.kispoko.tome.sheet.widget.table.cell;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.activity.sheet.dialog.ArithmeticDialogType;
import com.kispoko.tome.activity.sheet.dialog.CalculatorDialogFragment;
import com.kispoko.tome.activity.sheet.dialog.DialogOptionButton;
import com.kispoko.tome.activity.sheet.dialog.IncrementDialogFragment;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.table.TableRowFormat;
import com.kispoko.tome.sheet.widget.table.column.NumberColumn;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.sheet.widget.util.WidgetContainer;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Number CellUnion
 */
public class NumberCell extends Cell
                        implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                    id;

    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<NumberVariable>            valueVariable;
    private ModelFunctor<NumberCellFormat>          format;

    private PrimitiveFunctor<ArithmeticDialogType>  editDialogType;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                                 valueViewId;

    private WidgetContainer                         widgetContainer;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberCell()
    {
        this.id             = null;

        this.valueVariable  = ModelFunctor.empty(NumberVariable.class);
        this.format         = ModelFunctor.empty(NumberCellFormat.class);
        this.editDialogType = new PrimitiveFunctor<>(null, ArithmeticDialogType.class);
    }


    public NumberCell(UUID id,
                      NumberVariable valueVariable,
                      NumberCellFormat format,
                      ArithmeticDialogType editDialogType)
    {
        this.id             = id;

        this.valueVariable  = ModelFunctor.full(valueVariable, NumberVariable.class);
        this.format         = ModelFunctor.full(format, NumberCellFormat.class);

        this.editDialogType = new PrimitiveFunctor<>(editDialogType, ArithmeticDialogType.class);

        this.setEditDialogType(editDialogType);

        initializeNumberCell();
    }


    /**
     * Create a Number Cell from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Number Cell.
     * @throws YamlParseException
     */
    public static NumberCell fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID                 id             = UUID.randomUUID();

        NumberVariable       value          = NumberVariable.fromYaml(yaml.atMaybeKey("value"));
        NumberCellFormat     format         = NumberCellFormat.fromYaml(yaml.atMaybeKey("format"));
        ArithmeticDialogType editDialogType = ArithmeticDialogType.fromYaml(
                                                        yaml.atMaybeKey("edit_dialog_type"));

        return new NumberCell(id, value, format, editDialogType);
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
                .putYaml("format", this.format())
                .putYaml("edit_dialog_type", this.editDialogType());
    }


    // > Cell
    // ------------------------------------------------------------------------------------------

    @Override
    public Alignment alignment()
    {
        return this.format().alignment();
    }


    @Override
    public BackgroundColor background()
    {
        return this.format().background();
    }


    /**
     * Set the cells widget container (which is the parent Table Row).
     * @param widgetContainer The widget container.
     */
    public void initialize(NumberColumn column, WidgetContainer widgetContainer)
    {
        // [1] Set the widget container
        // --------------------------------------------------------------------------------------

        this.widgetContainer = widgetContainer;

        // [2] Inherit column properties
        // --------------------------------------------------------------------------------------

        this.valueVariable().setIsNamespaced(column.isNamespaced());

        if (column.defaultLabel() != null && this.valueVariable().label() == null)
            this.valueVariable().setLabel(column.defaultLabel());

        // [3] Initialize the value variable
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
    public String valueString(String columnValuePrefix)
    {
        Integer integerValue = this.value();

        if (integerValue != null)
        {
            String integerString = integerValue.toString();

            String valuePrefix = this.format().resolveValuePrefix(columnValuePrefix);
            if (valuePrefix != null)
                integerString = valuePrefix + integerString;

            return integerString;
        }

        return "";
    }


    // ** Format
    // -----------------------------------------------------------------------------------------

    /**
     * The number cell formatting options.
     * @return The format.
     */
    public NumberCellFormat format()
    {
        return this.format.getValue();
    }


    // ** Edit Dialog Type
    // -----------------------------------------------------------------------------------------

    /**
     * The type of edit dialog.
     * @return The edit dialog type.
     */
    public ArithmeticDialogType editDialogType()
    {
        return this.editDialogType.getValue();
    }


    public void setEditDialogType(ArithmeticDialogType editDialogType)
    {
        if (editDialogType != null)
            this.editDialogType.setValue(editDialogType);
        else
            this.editDialogType.setValue(ArithmeticDialogType.INCREMENTAL);
    }


    // > View
    // -----------------------------------------------------------------------------------------

    public LinearLayout view(NumberColumn column, TableRowFormat format, final Context context)
    {
        TextStyle valueStyle = this.format().resolveStyle(column.style());
        LinearLayout layout = this.layout(column, valueStyle.size(), format.cellHeight(), context);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNumberCellShortClick(context);
            }
        });

        layout.addView(valueTextView(column, context));

        return layout;
    }


    private TextView valueTextView(NumberColumn column, final Context context)
    {
        TextViewBuilder value = new TextViewBuilder();

        this.valueViewId = Util.generateViewId();

        value.id         = this.valueViewId;
        value.width      = LinearLayout.LayoutParams.WRAP_CONTENT;
        value.height     = LinearLayout.LayoutParams.WRAP_CONTENT;

        TextStyle valueStyle = this.format().resolveStyle(column.style());
        valueStyle.styleTextViewBuilder(value, context);

        // > Value
        String valueString = this.valueString(column.format().valuePrefix());
        if (valueString != null)
            value.text = valueString;
        else
            value.text = Integer.toString(column.defaultValue());

        return value.textView(context);
    }


    // > Clicks
    // -----------------------------------------------------------------------------------------

    /**
     * On a short click, open the appropriate editor.
     */
    private void onNumberCellShortClick(Context context)
    {
        SheetActivity sheetActivity = (SheetActivity) context;

        switch (this.editDialogType())
        {
            case INCREMENTAL:
                ArrayList<DialogOptionButton> dialogButtons = new ArrayList<>();

                DialogOptionButton addRowButton =
                        new DialogOptionButton(R.string.add_row,
                                               R.drawable.ic_dialog_table_widget_add_row,
                                               null);

                DialogOptionButton editRowButton =
                        new DialogOptionButton(R.string.edit_row,
                                               R.drawable.ic_dialog_table_widget_edit_row,
                                               null);

                DialogOptionButton editTableButton =
                        new DialogOptionButton(R.string.edit_table,
                                               R.drawable.ic_dialog_table_widget_widget,
                                               null);

                dialogButtons.add(addRowButton);
                dialogButtons.add(editRowButton);
                dialogButtons.add(editTableButton);

                CalculatorDialogFragment dialog =
                            CalculatorDialogFragment.newInstance(valueVariable(), dialogButtons);
                dialog.show(sheetActivity.getSupportFragmentManager(), "");
                break;
        }
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
                textView.setText(this.valueString(null));
        }
    }


}
