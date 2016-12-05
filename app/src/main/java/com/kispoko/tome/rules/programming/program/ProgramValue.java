
package com.kispoko.tome.rules.programming.program;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.UUID;



/**
 * Function Value
 */
public class ProgramValue implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                             id;

    private PrimitiveValue<Integer>          integerValue;
    private PrimitiveValue<String>           stringValue;
    private PrimitiveValue<Boolean>          booleanValue;

    private PrimitiveValue<ProgramValueType> valueType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ProgramValue()
    {
        this.id           = null;

        this.integerValue = new PrimitiveValue<>(null, Integer.class);
        this.stringValue  = new PrimitiveValue<>(null, String.class);
        this.booleanValue = new PrimitiveValue<>(null, Boolean.class);

        this.valueType    = new PrimitiveValue<>(null, ProgramValueType.class);
    }


    /**
     * Create a new ProgramValue. This constructor is private to force use of the safe constructors,
     * which only permit valid value/type associations.
     * @param id The model id.
     * @param value The ProgramValue encapsulated value.
     * @param valueType The type of ProgramValue (integer/string/etc..)
     */
    private ProgramValue(UUID id, Object value, ProgramValueType valueType)
    {
        this.id           = id;

        this.integerValue = new PrimitiveValue<>(null, Integer.class);
        this.stringValue  = new PrimitiveValue<>(null, String.class);
        this.booleanValue = new PrimitiveValue<>(null, Boolean.class);

        this.valueType    = new PrimitiveValue<>(valueType, ProgramValueType.class);

        // Set the value
        switch (valueType)
        {
            case INTEGER:
                this.integerValue.setValue((Integer) value);
                break;
            case STRING:
                this.stringValue.setValue((String) value);
                break;
            case BOOLEAN:
                this.stringValue.setValue((String) value);
                break;
        }
    }


    /**
     * Create an "integer" variant.
     * @param integerValue The integer value.
     * @return A Program Value as the integer case.
     */
    public static ProgramValue asInteger(UUID id, Integer integerValue)
    {
        return new ProgramValue(id, integerValue, ProgramValueType.INTEGER);
    }


    public static ProgramValue asInteger(Integer integerValue)
    {
        return new ProgramValue(null, integerValue, ProgramValueType.INTEGER);
    }


    /**
     * Create a "string" variant.
     * @param stringValue The string value.
     * @return A Program Value as the string case.
     */
    public static ProgramValue asString(UUID id, String stringValue)
    {
        return new ProgramValue(id, stringValue, ProgramValueType.STRING);
    }


    public static ProgramValue asString(String stringValue)
    {
        return new ProgramValue(null, stringValue, ProgramValueType.STRING);
    }


    /**
     * Create a "boolean" variant.
     * @param booleanValue The boolean value.
     * @return A Program Value as the boolean case.
     */
    public static ProgramValue asBoolean(UUID id, Boolean booleanValue)
    {
        return new ProgramValue(id, booleanValue, ProgramValueType.BOOLEAN);
    }


    public static ProgramValue asBoolean(Boolean booleanValue)
    {
        return new ProgramValue(null, booleanValue, ProgramValueType.BOOLEAN);
    }


    public static ProgramValue fromYaml(Yaml yaml, ProgramValueType valueType)
                  throws YamlException
    {
        UUID id = UUID.randomUUID();

        switch (valueType)
        {
            case STRING:
                return ProgramValue.asString(id, yaml.getString());
            case INTEGER:
                return ProgramValue.asInteger(id, yaml.getInteger());
            case BOOLEAN:
                return ProgramValue.asBoolean(id, yaml.getBoolean());
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


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Program Value is completely loaded for the first time.
     */
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the type of this ProgramValue.
     * @return The ProgramValueType.
     */
    public ProgramValueType getType()
    {
        return this.valueType.getValue();
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


    /**
     * Get the boolean value case for this ProgramValue
     * @return The Boolean value.
     */
    public Boolean getBoolean()
    {
        return this.booleanValue.getValue();
    }


    // > To String
    // ------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        switch (this.getType())
        {
            case STRING:
                return this.getString();
            case INTEGER:
                return this.getInteger().toString();
            case BOOLEAN:
                return this.getBoolean().toString();
        }

        return "";
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
            case STRING:
                String thatString = functionValue.getString();
                String thisString = this.getString();
                return new EqualsBuilder()
                            .append(thisString, thatString)
                            .isEquals();
            case INTEGER:
                Integer thatInteger = functionValue.getInteger();
                Integer thisInteger = this.getInteger();
                return new EqualsBuilder()
                        .append(thisInteger, thatInteger)
                        .isEquals();
            case BOOLEAN:
                Boolean thatBoolean = functionValue.getBoolean();
                Boolean thisBoolean = this.getBoolean();
                return new EqualsBuilder()
                        .append(thisBoolean, thatBoolean)
                        .isEquals();
        }

        return false;
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                    .append(stringValue)
                    .append(integerValue)
                    .append(booleanValue)
                    .append(valueType)
                    .toHashCode();
    }



}
