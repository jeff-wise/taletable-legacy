
package com.kispoko.tome.sheet.widget.table.cell;


import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.programming.variable.BooleanVariable;
import com.kispoko.tome.engine.programming.variable.Variable;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.table.column.BooleanColumn;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Boolean CellUnion
 */
public class BooleanCell implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelValue<BooleanVariable>   value;
    private PrimitiveValue<CellAlignment> alignment;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Integer                       valueViewId;


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

        initialize();
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
        initialize();
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the boolean variable that contains the value of the boolean cell.
     * @return The Number Variable value.
     */
    public BooleanVariable value()
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

        Boolean value = this.value().value();

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
                if (value().value()) {
                    value().setValue(false);
                    view.setImageDrawable(
                            ContextCompat.getDrawable(context, R.drawable.ic_boolean_false));
                } else {
                    value().setValue(true);
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
    private void initialize()
    {
        // [1] Initialize variables with listeners to update the number widget views when the
        //     values of the variables change
        // --------------------------------------------------------------------------------------

        this.valueViewId   = null;

        if (!this.value.isNull())
        {
            this.value().addOnUpdateListener(new Variable.OnUpdateListener() {
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

            Boolean value = this.value().value();

            // TODO can value be null
            if (value != null)
                textView.setText(Boolean.toString(value));
        }
    }



}
