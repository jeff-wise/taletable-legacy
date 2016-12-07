
package com.kispoko.tome.sheet.widget.table.cell;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;

import com.kispoko.tome.R;
import com.kispoko.tome.rules.programming.variable.BooleanVariable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.table.column.BooleanColumn;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;



/**
 * Boolean CellUnion
 */
public class BooleanCell implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private ModelValue<BooleanVariable>   value;
    private PrimitiveValue<CellAlignment> alignment;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BooleanCell()
    {
        this.id        = null;

        this.value     = ModelValue.empty(BooleanVariable.class);
        this.alignment = new PrimitiveValue<>(null, CellAlignment.class);
    }


    public BooleanCell(UUID id,
                       BooleanVariable value,
                       CellAlignment alignment,
                       BooleanColumn column)
    {
        // ** Id
        this.id        = id;

        // ** Value
        if (value == null) {
            value = BooleanVariable.asBoolean(UUID.randomUUID(),
                                              null,
                                              column.getDefaultValue(),
                                              null);
        }
        this.value     = ModelValue.full(value, BooleanVariable.class);

        // ** Alignment
        this.alignment = new PrimitiveValue<>(alignment, CellAlignment.class);
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
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the boolean variable that contains the value of the boolean cell.
     * @return The Number Variable value.
     */
    public BooleanVariable getValue()
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

    public View view(BooleanColumn column)
    {
        final Context context = SheetManager.currentSheetContext();

        final ImageView view = new ImageView(context);

        Boolean value = this.getValue().value();

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
                if (getValue().value()) {
                    getValue().setValue(false);
                    view.setImageDrawable(
                            ContextCompat.getDrawable(context, R.drawable.ic_boolean_false));
                } else {
                    getValue().setValue(true);
                    view.setImageDrawable(
                            ContextCompat.getDrawable(context, R.drawable.ic_boolean_true));
                }
            }
        });

        return view;
    }


}
