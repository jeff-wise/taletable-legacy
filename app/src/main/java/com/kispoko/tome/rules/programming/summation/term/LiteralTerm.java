
package com.kispoko.tome.rules.programming.summation.term;


import com.kispoko.tome.rules.programming.summation.SummationException;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;



/**
 * Summation Term: Literal
 */
public class LiteralTerm extends Term
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                         id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelValue<IntegerTermValue> termValue;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public LiteralTerm()
    {
        this.id        = null;

        this.termValue = ModelValue.empty(IntegerTermValue.class);
    }

    public LiteralTerm(UUID id, IntegerTermValue termValue)
    {
        this.id        = id;

        this.termValue = ModelValue.full(termValue, IntegerTermValue.class);
    }


    public static LiteralTerm fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID             id        = UUID.randomUUID();

        IntegerTermValue termValue = IntegerTermValue.fromYaml(yaml.atKey("value"));

        return new LiteralTerm(id, termValue);
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
     * This method is called when the Column Union is completely loaded for the first time.
     */
    public void onLoad() { }


    // > Term
    // ------------------------------------------------------------------------------------------

    /**
     * Get the term value. The returned value is just the value of the referenced variable.
     * @return The term value. Throws SummationException if the variable is invalid.
     */
    public Integer value()
           throws SummationException
    {
        return termValue.getValue().value();
    }

}
