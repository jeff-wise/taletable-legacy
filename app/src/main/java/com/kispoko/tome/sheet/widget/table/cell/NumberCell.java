
package com.kispoko.tome.sheet.widget.table.cell;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.activity.sheet.dialog.ArithmeticDialogType;
import com.kispoko.tome.activity.sheet.dialog.IncrementDialogFragment;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.table.column.NumberColumn;
import com.kispoko.tome.sheet.widget.util.TextColor;
import com.kispoko.tome.sheet.widget.util.TextSize;
import com.kispoko.tome.sheet.widget.util.TextStyle;
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

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.kispoko.tome.R.string.character_counter_pattern;
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

    private UUID                                    id;

    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<NumberVariable>            valueVariable;
    private PrimitiveFunctor<CellAlignment>         alignment;

    /**
     * The number cell style. Often inherited from the column.
     */
    private ModelFunctor<TextStyle>                 style;

    private PrimitiveFunctor<String>                valuePrefix;
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
        this.alignment      = new PrimitiveFunctor<>(null, CellAlignment.class);
        this.style          = ModelFunctor.empty(TextStyle.class);
        this.valuePrefix    = new PrimitiveFunctor<>(null, String.class);
        this.editDialogType = new PrimitiveFunctor<>(null, ArithmeticDialogType.class);
    }


    public NumberCell(UUID id,
                      NumberVariable valueVariable,
                      CellAlignment alignment,
                      TextStyle style,
                      String valuePrefix,
                      ArithmeticDialogType editDialogType)
    {
        this.id             = id;

        this.valueVariable  = ModelFunctor.full(valueVariable, NumberVariable.class);

        this.alignment      = new PrimitiveFunctor<>(alignment, CellAlignment.class);
        this.style          = ModelFunctor.full(style, TextStyle.class);

        this.valuePrefix    = new PrimitiveFunctor<>(valuePrefix, String.class);
        this.editDialogType = new PrimitiveFunctor<>(editDialogType, ArithmeticDialogType.class);

        this.setEditDialogType(editDialogType);

        this.setStyle(style);

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
        CellAlignment        alignment      = CellAlignment.fromYaml(yaml.atMaybeKey("alignment"));
        TextStyle            style          = TextStyle.fromYaml(yaml.atMaybeKey("style"), false);
        String               prefix         = yaml.atMaybeKey("value_prefix").getString();
        ArithmeticDialogType editDialogType = ArithmeticDialogType.fromYaml(
                                                        yaml.atMaybeKey("edit_dialog_type"));

        return new NumberCell(id, value, alignment, style, prefix, editDialogType);
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
                .putYaml("style", this.style())
                .putString("value_prefix", this.valuePrefix())
                .putYaml("edit_dialog_type", this.editDialogType());
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

        // [2] Inherit column properties
        // --------------------------------------------------------------------------------------

        this.valueVariable().setIsNamespaced(column.isNamespaced());

        if (column.defaultLabel() != null && this.valueVariable().label() == null)
            this.valueVariable().setLabel(column.defaultLabel());

        if (column.valuePrefix() != null && this.valuePrefix() == null)
            this.setValuePrefix(column.valuePrefix());

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
    public String valueString()
    {
        Integer integerValue = this.value();

        if (integerValue != null)
        {
            String integerString = integerValue.toString();

            if (!this.valuePrefix.isNull())
                integerString = this.valuePrefix() + integerString;

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


    // ** Style
    // ------------------------------------------------------------------------------------------

    /**
     * The number cell style. Often inherited from the column.
     * @return The text cell Text Style.
     */
    public TextStyle style()
    {
        return this.style.getValue();
    }


    /**
     * Set the number cell's text style. If null, a default style is provided.
     * @param style The text style.
     */
    public void setStyle(TextStyle style)
    {
        if (style != null) {
            this.style.setValue(style);
        }
        else {
            TextStyle defaultNumberCellStyle = new TextStyle(UUID.randomUUID(),
                                                             TextColor.MEDIUM,
                                                             TextSize.MEDIUM_SMALL);
            this.style.setValue(defaultNumberCellStyle);
        }
    }


    // ** Value Prefix
    // -----------------------------------------------------------------------------------------

    public String valuePrefix()
    {
        return this.valuePrefix.getValue();
    }


    public void setValuePrefix(String valuePrefix)
    {
        this.valuePrefix.setValue(valuePrefix);
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

    public View view(NumberColumn column, final Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        TextViewBuilder cellView = new TextViewBuilder();
        this.valueViewId = Util.generateViewId();

        // [2] Cell View
        // -------------------------------------------------------------------------------------

        cellView.id         = this.valueViewId;
        cellView.layoutType = LayoutType.TABLE_ROW;
        cellView.width      = TableRow.LayoutParams.WRAP_CONTENT;
        cellView.height     = TableRow.LayoutParams.WRAP_CONTENT;
        cellView.color      = R.color.dark_blue_hl_1;
        cellView.size       = R.dimen.widget_table_cell_text_size;

        // > Font
        if (column.style().isBold())
            cellView.font   = Font.serifFontBold(context);
        else
            cellView.font   = Font.serifFontRegular(context);

        String valueString = this.valueString();
        if (valueString != null)
            cellView.text = valueString;
        else
            cellView.text = Integer.toString(column.defaultValue());


        cellView.onClick    = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNumberCellShortClick(context);
            }
        };


        return cellView.textView(context);
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
                IncrementDialogFragment incDialog =
                        IncrementDialogFragment.newInstance(this.valueVariable().label(),
                                                            this.value());
                incDialog.show(sheetActivity.getSupportFragmentManager(), "");
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
                textView.setText(this.valueString());
        }
    }


}
