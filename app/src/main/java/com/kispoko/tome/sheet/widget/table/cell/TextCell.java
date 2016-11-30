
package com.kispoko.tome.sheet.widget.table.cell;


import com.kispoko.tome.rules.programming.variable.TextVariable;
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


    public TextCell(UUID id, TextVariable value, CellAlignment alignment)
    {
        this.id        = id;

        this.value     = ModelValue.full(value, TextVariable.class);
        this.alignment = new PrimitiveValue<>(alignment, CellAlignment.class);
    }


    public static TextCell fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID          id        = UUID.randomUUID();
        TextVariable  value     = TextVariable.fromYaml(yaml.atMaybeKey("value"));
        CellAlignment alignment = CellAlignment.fromYaml(yaml.atMaybeKey("alignment"));

        return new TextCell(id, value, alignment);
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

}
