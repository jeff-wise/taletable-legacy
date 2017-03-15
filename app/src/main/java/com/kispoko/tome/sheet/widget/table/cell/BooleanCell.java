
package com.kispoko.tome.sheet.widget.table.cell;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.BooleanVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.table.TableRowFormat;
import com.kispoko.tome.sheet.widget.table.column.BooleanColumn;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.sheet.widget.util.WidgetContainer;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.functor.ModelFunctor;
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
public class BooleanCell extends Cell
                         implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<BooleanVariable>   valueVariable;
    private ModelFunctor<BooleanCellFormat> format;


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
        this.id             = null;

        this.valueVariable  = ModelFunctor.empty(BooleanVariable.class);
        this.format         = ModelFunctor.empty(BooleanCellFormat.class);
    }


    public BooleanCell(UUID id,
                       BooleanVariable valueVariable,
                       BooleanCellFormat format)
    {
        // ** Id
        this.id             = id;

        // ** Value
        this.valueVariable  = ModelFunctor.full(valueVariable, BooleanVariable.class);

        // ** Format
        this.format         = ModelFunctor.full(format, BooleanCellFormat.class);

        initializeBooleanCell();
    }


    public static BooleanCell fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID              id     = UUID.randomUUID();

        BooleanVariable   value  = BooleanVariable.fromYaml(yaml.atMaybeKey("value"));
        BooleanCellFormat format = BooleanCellFormat.fromYaml(yaml.atMaybeKey("format"));

        return new BooleanCell(id, value, format);
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
                .putYaml("format", this.format());
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
    public void initialize(BooleanColumn column, WidgetContainer widgetContainer)
    {
        // [1] Set the widget container
        // --------------------------------------------------------------------------------------

        this.widgetContainer = widgetContainer;

        // [2] Inherit column properites
        // --------------------------------------------------------------------------------------

        this.valueVariable().setIsNamespaced(column.isNamespaced());

        if (column.defaultLabel() != null && this.valueVariable().label() == null)
            this.valueVariable().setLabel(column.defaultLabel());


        // [3] Initialize the value variable
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

        // [4] Save Column Data
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

    // ** Value
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


    // ** Format
    // ------------------------------------------------------------------------------------------

    /**
     * The cell's formatting options.
     * @return The format.
     */
    public BooleanCellFormat format()
    {
        return this.format.getValue();
    }


    // > View
    // ------------------------------------------------------------------------------------------

    public View view(BooleanColumn column, TableRowFormat rowFormat, final Context context)
    {
        final LinearLayout valueView = valueView(column, rowFormat, context);

        return valueView;
    }


    private LinearLayout valueView(BooleanColumn column,
                                   TableRowFormat rowFormat,
                                   final Context context)
    {
        TextStyle textStyle = this.format().resolveStyle(column.format().style());

        LinearLayout layout = this.layout(column, textStyle.size(),
                                          rowFormat.cellHeight(), context);

        if ((this.value() && this.format().showTrueIcon()) ||
            (!this.value() && this.format().showFalseIcon()))
        {
            layout.addView(valueIconView(context));
        }

        // > Text View
        // -------------------------------------------------------------------------------------

        final TextView valueView = valueTextView(column, context);
        layout.addView(valueView);

        valueView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (value())
                {
                    valueVariable().setValue(false);
                    valueView.setText(falseText);

                    // No false style, but need to undo true style
                    if (format().falseStyle() == null && format().trueStyle() != null) {
                        format().style().styleTextView(valueView, context);
                    }
                    // Set false style
                    else if (format().falseStyle() != null) {
                        format().falseStyle().styleTextView(valueView, context);
                    }
                }
                else
                {
                    valueVariable().setValue(true);
                    valueView.setText(trueText);

                    // No true style, but need to undo false style
                    if (format().trueStyle() == null && format().falseStyle() != null) {
                        format().style().styleTextView(valueView, context);
                    }
                    // Set true style
                    else if (format().trueStyle() != null) {
                        format().trueStyle().styleTextView(valueView, context);
                    }
                }
            }
        });


        return layout;
    }


    private ImageView valueIconView(Context context)
    {
        ImageViewBuilder icon = new ImageViewBuilder();

        icon.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        icon.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        if (this.value())
            icon.image      = R.drawable.ic_boolean_cell_true;
        else
            icon.image      = R.drawable.ic_boolean_cell_false;

        icon.margin.right   = R.dimen.four_dp;

        if (this.value() && this.format().trueStyle() != null) {
            icon.color      = this.format().trueStyle().color().resourceId();
        }
        else if (!this.value() && this.format().falseStyle() != null) {
            icon.color      = this.format().falseStyle().color().resourceId();
        }
        else {
            icon.color      = this.format().style().color().resourceId();
        }

        return icon.imageView(context);
    }


    /**
     * The cell's value text view.
     * @param context The context.
     * @return The value Text View.
     */
    private TextView valueTextView(BooleanColumn column, final Context context)
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

        // > Styles
        // -------------------------------------------------------------------------------------

        TextStyle defaultStyle  = this.format().resolveStyle(column.format().style());
        TextStyle trueStyle     = this.format().resolveTrueStyle(column.format().trueStyle());
        TextStyle falseStyle    = this.format().resolveFalseStyle(column.format().falseStyle());

        if (this.value() && trueStyle != null) {
            trueStyle.styleTextViewBuilder(value, context);
        }
        else if (!this.value() && falseStyle != null) {
            falseStyle.styleTextViewBuilder(value, context);
        }
        else {
            defaultStyle.styleTextViewBuilder(value, context);
        }

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
