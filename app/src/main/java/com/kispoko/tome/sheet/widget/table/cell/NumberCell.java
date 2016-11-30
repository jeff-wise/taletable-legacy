
package com.kispoko.tome.sheet.widget.table.cell;


import com.kispoko.tome.rules.programming.variable.NumberVariable;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;



/**
 * Number CellUnion
 */
public class NumberCell implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private ModelValue<NumberVariable>     value;
    private PrimitiveValue<CellAlignment> alignment;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberCell()
    {
        this.id        = null;

        this.value     = ModelValue.empty(NumberVariable.class);
        this.alignment = new PrimitiveValue<>(null, CellAlignment.class);
    }


    public NumberCell(UUID id, NumberVariable value, CellAlignment alignment)
    {
        this.id        = id;

        this.value     = ModelValue.full(value, NumberVariable.class);
        this.alignment = new PrimitiveValue<>(alignment, CellAlignment.class);
    }


    public static NumberCell fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID          id        = UUID.randomUUID();
        NumberVariable value    = NumberVariable.fromYaml(yaml.atMaybeKey("value"));
        CellAlignment alignment = CellAlignment.fromYaml(yaml.atMaybeKey("alignment"));

        return new NumberCell(id, value, alignment);
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

    public void onValueUpdate(String valueName) { }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the value of this number cell which is a number variable.
     * @return The Number Variable value.
     */
    public NumberVariable getValue()
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
