
package com.kispoko.tome.sheet.widget.table.cell;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.table.column.TextColumn;
import com.kispoko.tome.sheet.widget.util.WidgetContainer;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Text CellUnion
 */
public class TextCell implements Model, Cell, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<TextVariable>      valueVariable;
    private PrimitiveFunctor<CellAlignment> alignment;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                         valueViewId;

    private WidgetContainer                 widgetContainer;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextCell()
    {
        this.id                 = null;

        this.valueVariable = ModelFunctor.empty(TextVariable.class);
        this.alignment          = new PrimitiveFunctor<>(null, CellAlignment.class);
    }


    public TextCell(UUID id,
                    TextVariable valueVariable,
                    CellAlignment alignment,
                    TextColumn column)
    {
        // ** Id
        this.id        = id;

        // ** Value
        if (valueVariable == null) {
            valueVariable = TextVariable.asText(UUID.randomUUID(),
                                        column.getDefaultValue());
        }
        this.valueVariable = ModelFunctor.full(valueVariable, TextVariable.class);

        this.alignment          = new PrimitiveFunctor<>(alignment, CellAlignment.class);

        // > Initialize state
        this.initializeTextCell();
    }


    public static TextCell fromYaml(Yaml yaml, TextColumn column)
                  throws YamlException
    {
        UUID          id                = UUID.randomUUID();
        TextVariable  value             = TextVariable.fromYaml(yaml.atMaybeKey("value"));
        CellAlignment alignment         = CellAlignment.fromYaml(yaml.atMaybeKey("alignment"));

        return new TextCell(id, value, alignment, column);
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
     * This method is called when the Text Cell is completely loaded for the first time.
     */
    public void onLoad()
    {
        this.initializeTextCell();
    }


    // > Cell
    // ------------------------------------------------------------------------------------------

    /**
     * Set the cells widget container (which is the parent Table Row).
     * @param widgetContainer The widget container.
     */
    public void initialize(WidgetContainer widgetContainer)
    {
        // [1] Set widget container
        // --------------------------------------------------------------------------------------

        this.widgetContainer = widgetContainer;

        // [2] Initialize value variable
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

        // [3] Configure namespace
        // --------------------------------------------------------------------------------------

        this.configureNamespace();
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
     * Get the value of this text cell which is a text variable.
     * @return The Text Variable value.
     */
    public TextVariable valueVariable()
    {
        return this.valueVariable.getValue();
    }


    public String value()
    {
        if (valueVariable() != null)
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

    public View view(TextColumn column)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        Context context = SheetManager.currentSheetContext();

        TextViewBuilder cellView = new TextViewBuilder();
        this.valueViewId = Util.generateViewId();

        // [2] Cell View
        // --------------------------------------------------------------------------------------

        cellView.id         = this.valueViewId;
        cellView.layoutType = LayoutType.TABLE_ROW;
        cellView.width      = TableRow.LayoutParams.WRAP_CONTENT;
        cellView.height     = TableRow.LayoutParams.WRAP_CONTENT;
        cellView.color      = R.color.dark_blue_hl_3;
        cellView.font       = Font.serifFontRegular(context);
        cellView.size       = R.dimen.widget_table_cell_text_size;

        if (this.value() != null)
            cellView.text = this.value();
        else
            cellView.text = column.getDefaultValue();

        return cellView.textView(context);
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the text cell state.
     */
    private void initializeTextCell()
    {
        this.valueViewId = null;
        this.widgetContainer = null;
    }


    /**
     * Configure the container's namespace. If the text cell's value is a variable that defines
     * a namespace, then update the container namespace.
     */
    private void configureNamespace()
    {
        if (this.valueVariable().definesNamespace()) {
            this.widgetContainer.setNamespace(this.valueVariable().identifier());
        }
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

            if (this.value() != null)
                textView.setText(this.value());
        }
        else if (!this.valueVariable.isNull()) {
            this.configureNamespace();
        }
    }


}
