
package com.kispoko.tome.rules.programming.program;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.UUID;

import static android.R.attr.value;



/**
 * Function Value
 */
public class ProgramValue implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                    id;

    private PrimitiveValue<Integer> integerValue;
    private PrimitiveValue<String>  stringValue;

    private ProgramValueType        valueType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ProgramValue() { }


    /**
     * Create a new ProgramValue. This constructor is private to force use of the safe constructors,
     * which only permit valid value/type associations.
     * @param id The model id.
     * @param value The ProgramValue encapsulated value.
     * @param valueType The type of ProgramValue (integer/string/etc..)
     */
    private ProgramValue(UUID id, Object value, ProgramValueType valueType)
    {
        this.id        = id;

        this.integerValue = new PrimitiveValue<>(null, this, Integer.class);
        this.stringValue  = new PrimitiveValue<>(null, this, String.class);

        this.valueType = valueType;

        // Set the value
        switch (valueType)
        {
            case INTEGER:
                this.integerValue.setValue((Integer) value);
                break;
            case STRING:
                this.stringValue.setValue((String) value);
                break;
        }
    }


    /**
     * Create an "integer" ProgramValue.
     * @param integerValue The integer value.
     * @return A ProgramValue that represents an Integer.
     */
    public static ProgramValue asInteger(Integer integerValue)
    {
        return new ProgramValue(UUID.randomUUID(), integerValue, ProgramValueType.INTEGER);
    }


    /**
     * Create a "string" ProgramValue.
     * @param stringValue The string value.
     * @return A ProgramValue that represents a String.
     */
    public static ProgramValue asString(String stringValue)
    {
        return new ProgramValue(UUID.randomUUID(), stringValue, ProgramValueType.STRING);
    }


    public static ProgramValue fromYaml(Yaml yaml, ProgramValueType valueType)
                  throws YamlException
    {
        UUID id = UUID.randomUUID();

        switch (valueType)
        {
            case INTEGER:
                Integer integerValue = yaml.getInteger();
                return new ProgramValue(id, integerValue, ProgramValueType.INTEGER);
            case STRING:
                String stringValue = yaml.getString();
                return new ProgramValue(id, stringValue, ProgramValueType.STRING);
        }

        // Shouldn't be possible for ProgramValueType to be null
        return null;
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
     * Get the type of this ProgramValue.
     * @return The ProgramValueType.
     */
    public ProgramValueType getType()
    {
        return this.valueType;
    }


    /**
     * Get the integer value case for this ProgramValue.
     * @return The Integer value.
     */
    public Integer getInteger()
    {
        return this.integerValue.getValue();
    }


    /**
     * Get the string value case for this ProgramValue
     * @return The String value.
     */
    public String getString()
    {
        return this.stringValue.getValue();
    }


    // > Custom Equality Methods
    // ------------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o)
    {

        if (o == this) return true;

        if (!(o instanceof ProgramValue)) {
            return false;
        }

        ProgramValue functionValue = (ProgramValue) o;

        if (functionValue.getType() != this.getType())
            return false;

        switch (functionValue.getType())
        {
            case INTEGER:
                Integer thatInteger = functionValue.getInteger();
                Integer thisInteger = this.getInteger();
                return new EqualsBuilder()
                            .append(thisInteger, thatInteger)
                            .isEquals();
            case STRING:
                String thatString = functionValue.getString();
                String thisString = this.getString();
                return new EqualsBuilder()
                            .append(thisString, thatString)
                            .isEquals();
        }

        return false;
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                    .append(value)
                    .append(valueType)
                    .toHashCode();
    }



}
