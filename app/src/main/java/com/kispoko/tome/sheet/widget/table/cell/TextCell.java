
package com.kispoko.tome.sheet.widget.table.cell;


import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.programming.variable.TextVariable;
import com.kispoko.tome.engine.programming.variable.Variable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.table.column.TextColumn;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.UUID;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


/**
 * Text CellUnion
 */
public class TextCell implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                          id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelValue<TextVariable>      value;
    private PrimitiveValue<CellAlignment> alignment;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                       valueViewId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextCell()
    {
        this.id        = null;

        this.value     = ModelValue.empty(TextVariable.class);
        this.alignment = new PrimitiveValue<>(null, CellAlignment.class);
    }


    public TextCell(UUID id, TextVariable value, CellAlignment alignment, TextColumn column)
    {
        // ** Id
        this.id        = id;

        // ** Value
        if (value == null) {
            value = TextVariable.asText(UUID.randomUUID(),
                                        null,
                                        column.getDefaultValue(),
                                        null);
        }
        this.value     = ModelValue.full(value, TextVariable.class);

        // ** Alignment
        this.alignment = new PrimitiveValue<>(alignment, CellAlignment.class);

        // > Initialize state
        initialize();
    }


    public static TextCell fromYaml(Yaml yaml, TextColumn column)
                  throws YamlException
    {
        UUID          id        = UUID.randomUUID();
        TextVariable  value     = TextVariable.fromYaml(yaml.atMaybeKey("value"));
        CellAlignment alignment = CellAlignment.fromYaml(yaml.atMaybeKey("alignment"));

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
        initialize();
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
        return this.value.getValue();
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
        cellView.color      = R.color.light_grey_9;
        cellView.font       = Font.serifFontRegular(context);
        cellView.size       = R.dimen.widget_table_cell_text_size;

        if (this.value() != null)
            cellView.text = this.value();
        else
            cellView.text = column.getDefaultValue();

        //layoutParams.setMargins(0, 0, 0, 0);
        //view.setPadding(0, 0, 0, 0);

        return cellView.textView(context);
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

        this.valueViewId   = null;

        if (!this.value.isNull())
        {
            this.valueVariable().addOnUpdateListener(new Variable.OnUpdateListener() {
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
        if (this.valueViewId != null && !this.value.isNull())
        {
            Activity activity = (Activity) SheetManager.currentSheetContext();
            TextView textView = (TextView) activity.findViewById(this.valueViewId);

            if (this.value() != null)
                textView.setText(this.value());
        }
    }


}
