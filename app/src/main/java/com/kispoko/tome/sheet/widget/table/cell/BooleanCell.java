
package com.kispoko.tome.sheet.widget.table.cell;


import com.kispoko.tome.rules.programming.variable.BooleanVariable;
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

    private ModelValue<BooleanVariable>    value;
    private PrimitiveValue<CellAlignment> alignment;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BooleanCell() { }


    public BooleanCell(UUID id, BooleanVariable value, CellAlignment alignment)
    {
        this.id        = id;

        this.value     = new ModelValue<>(value, this, BooleanVariable.class);
        this.alignment = new PrimitiveValue<>(alignment, this, CellAlignment.class);
    }


    public static BooleanCell fromYaml(Yaml yaml)
                  throws YamlException
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


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onModelUpdate(String valueName) { }


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

}
