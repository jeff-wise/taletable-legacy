
package com.kispoko.tome.sheet.widget.table.cell;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.Namespace;
import com.kispoko.tome.engine.variable.NullVariableException;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.engine.variable.Variable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.table.column.TextColumn;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Text CellUnion
 */
public class TextCell implements Model, Cell, ToYaml, Serializable
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


    /**
     * The text cell style. Often inherited from the text column.
     */
    private ModelFunctor<TextStyle>         style;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                         valueViewId;

    private WidgetContainer                 widgetContainer;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextCell()
    {
        this.id             = null;

        this.valueVariable  = ModelFunctor.empty(TextVariable.class);
        this.alignment      = new PrimitiveFunctor<>(null, CellAlignment.class);
        this.style          = ModelFunctor.empty(TextStyle.class);
    }


    public TextCell(UUID id,
                    TextVariable valueVariable,
                    CellAlignment alignment,
                    TextStyle style)
    {
        // ** Id
        this.id             = id;

        // ** Value
        this.valueVariable  = ModelFunctor.full(valueVariable, TextVariable.class);

        // ** Alignment
        this.alignment      = new PrimitiveFunctor<>(alignment, CellAlignment.class);

        // ** Style
        this.style          = ModelFunctor.full(style, TextStyle.class);

        this.setStyle(style);

        // > Initialize state
        this.initializeTextCell();
    }


    public static TextCell fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID          id        = UUID.randomUUID();

        TextVariable  value     = TextVariable.fromYaml(yaml.atMaybeKey("value"));
        CellAlignment alignment = CellAlignment.fromYaml(yaml.atMaybeKey("alignment"));
        TextStyle     style     = TextStyle.fromYaml(yaml.atMaybeKey("style"), false);

        return new TextCell(id, value, alignment, style);
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


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Text Cell's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("value", this.valueVariable())
                .putYaml("alignment", this.alignment())
                .putYaml("style", this.style());
    }


    // > Cell
    // ------------------------------------------------------------------------------------------

    /**
     * Set the cells widget container (which is the parent Table Row).
     * @param widgetContainer The widget container.
     */
    public void initialize(TextColumn column, WidgetContainer widgetContainer)
    {
        // [1] Set widget container
        // --------------------------------------------------------------------------------------

        this.widgetContainer = widgetContainer;

        // [2] Inherit column properties
        // --------------------------------------------------------------------------------------

        this.valueVariable().setDefinesNamespace(column.definesNamespace());
        this.valueVariable().setIsNamespaced(column.isNamespaced());

        if (column.defaultLabel() != null && this.valueVariable().label() == null)
            this.valueVariable().setLabel(column.defaultLabel());

        // [3] Initialize value variable
        // --------------------------------------------------------------------------------------

        // > If null, set default value
        if (this.valueVariable.isNull()) {
            valueVariable.setValue(TextVariable.asText(UUID.randomUUID(),
                                                       column.defaultValue()));
        }

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
        {
            try {
                return this.valueVariable().value();
            }
            catch (NullVariableException exception) {
                ApplicationFailure.nullVariable(exception);
            }
        }

        return "N/A";
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
     * The text cell style. Often inherited from the column.
     * @return The text cell Text Style.
     */
    public TextStyle style()
    {
        return this.style.getValue();
    }


    /**
     * Set the text cell's text style. If null, a default style is provided.
     * @param style The text style.
     */
    public void setStyle(TextStyle style)
    {
        if (style != null) {
            this.style.setValue(style);
        }
        else {
            TextStyle defaultTextCellStyle = new TextStyle(UUID.randomUUID(),
                                                             TextColor.THEME_MEDIUM,
                                                             TextSize.MEDIUM_SMALL);
            this.style.setValue(defaultTextCellStyle);
        }
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
        cellView.size       = R.dimen.widget_table_cell_text_size;

        // > Font
        if (column.style().isBold()) {
            cellView.font   = Font.serifFontBold(context);
            cellView.color  = R.color.dark_blue_hl_3;
        }
        else {
            cellView.font   = Font.serifFontRegular(context);
            cellView.color  = R.color.dark_blue_hl_1;
        }

        if (this.value() != null)
            cellView.text = this.value();
        else
            cellView.text = column.defaultValue();

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
        if (this.valueVariable().definesNamespace())
        {
            try {
                Namespace namespace = this.valueVariable().namespace();
                this.widgetContainer.setNamespace(namespace);
            }
            catch (NullVariableException exception) {
                ApplicationFailure.nullVariable(exception);
            }
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
