
package com.kispoko.tome.sheet.widget.table.cell;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.rules.programming.variable.TextVariable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.table.column.TextColumn;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;



/**
 * Text CellUnion
 */
public class TextCell implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                          id;

    private ModelValue<TextVariable>      value;
    private PrimitiveValue<CellAlignment> alignment;


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
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the value of this text cell which is a text variable.
     * @return The Text Variable value.
     */
    public TextVariable getValue()
    {
        return this.value.getValue();
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
        Context context = SheetManager.currentSheetContext();

        TextView view = new TextView(context);

        TableRow.LayoutParams layoutParams =
                new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                                          TableRow.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 0);
        view.setLayoutParams(layoutParams);

        view.setPadding(0, 0, 0, 0);


        // > Set value
        // --------------------------------------------------------------------------------------
        String value = this.value.getValue().getValue();
        if (value != null)
            view.setText(value);
        else
            view.setText(column.getDefaultValue());

        view.setTextColor(ContextCompat.getColor(context, R.color.text_medium_light));
        view.setTypeface(Util.serifFontBold(context));

        float textSize = Util.getDim(context, R.dimen.comp_table_cell_text_size);
        view.setTextSize(textSize);

        return view;
    }

}
