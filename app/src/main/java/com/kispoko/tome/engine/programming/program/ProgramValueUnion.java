
package com.kispoko.tome.engine.programming.program;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.UUID;



/**
 * Function Value
 */
public class ProgramValueUnion implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                             id;

    private PrimitiveFunctor<Integer> integerValue;
    private PrimitiveFunctor<String> stringValue;
    private PrimitiveFunctor<Boolean> booleanValue;

    private PrimitiveFunctor<ProgramValueType> valueType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ProgramValueUnion()
    {
        this.id           = null;

        this.integerValue = new PrimitiveFunctor<>(null, Integer.class);
        this.stringValue  = new PrimitiveFunctor<>(null, String.class);
        this.booleanValue = new PrimitiveFunctor<>(null, Boolean.class);

        this.valueType    = new PrimitiveFunctor<>(null, ProgramValueType.class);
    }


    /**
     * Create a new ProgramValueUnion. This constructor is private to force use of the safe constructors,
     * which only permit valid value/type associations.
     * @param id The model id.
     * @param value The ProgramValueUnion encapsulated value.
     * @param valueType The type of ProgramValueUnion (integer/string/etc..)
     */
    private ProgramValueUnion(UUID id, Object value, ProgramValueType valueType)
    {
        this.id           = id;

        this.integerValue = new PrimitiveFunctor<>(null, Integer.class);
        this.stringValue  = new PrimitiveFunctor<>(null, String.class);
        this.booleanValue = new PrimitiveFunctor<>(null, Boolean.class);

        this.valueType    = new PrimitiveFunctor<>(valueType, ProgramValueType.class);

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
    public static ProgramValueUnion asInteger(UUID id, Integer integerValue)
    {
        return new ProgramValueUnion(id, integerValue, ProgramValueType.INTEGER);
    }


    public static ProgramValueUnion asInteger(Integer integerValue)
    {
        return new ProgramValueUnion(null, integerValue, ProgramValueType.INTEGER);
    }


    /**
     * Create a "string" variant.
     * @param stringValue The string value.
     * @return A Program Value as the string case.
     */
    public static ProgramValueUnion asString(UUID id, String stringValue)
    {
        return new ProgramValueUnion(id, stringValue, ProgramValueType.STRING);
    }


    public static ProgramValueUnion asString(String stringValue)
    {
        return new ProgramValueUnion(null, stringValue, ProgramValueType.STRING);
    }


    /**
     * Create a "boolean" variant.
     * @param booleanValue The boolean value.
     * @return A Program Value as the boolean case.
     */
    public static ProgramValueUnion asBoolean(UUID id, Boolean booleanValue)
    {
        return new ProgramValueUnion(id, booleanValue, ProgramValueType.BOOLEAN);
    }


    public static ProgramValueUnion asBoolean(Boolean booleanValue)
    {
        return new ProgramValueUnion(null, booleanValue, ProgramValueType.BOOLEAN);
    }


    public static ProgramValueUnion fromYaml(Yaml yaml, ProgramValueType valueType)
                  throws YamlException
    {
        UUID id = UUID.randomUUID();

        switch (valueType)
        {
            case STRING:
                return ProgramValueUnion.asString(id, yaml.getString());
            case INTEGER:
                return ProgramValueUnion.asInteger(id, yaml.getInteger());
            case BOOLEAN:
                return ProgramValueUnion.asBoolean(id, yaml.getBoolean());
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
     * Get the type of this ProgramValueUnion.
     * @return The ProgramValueType.
     */
    public ProgramValueType getType()
    {
        return this.valueType.getValue();
    }


    /**
     * Get the integer value case for this ProgramValueUnion.
     * @return The Integer value.
     */
    public Integer getInteger()
    {
        return this.integerValue.getValue();
    }


    /**
     * Get the string value case for this ProgramValueUnion
     * @return The String value.
     */
    public String getString()
    {
        return this.stringValue.getValue();
    }


    /**
     * Get the boolean value case for this ProgramValueUnion
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

        if (!(o instanceof ProgramValueUnion)) {
            return false;
        }

        ProgramValueUnion functionValue = (ProgramValueUnion) o;

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
