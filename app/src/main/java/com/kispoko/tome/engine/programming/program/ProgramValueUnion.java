
package com.kispoko.tome.engine.programming.program;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.engine.variable.VariableType;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.UUID;



/**
 * Function Value
 */
public class ProgramValueUnion implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                id;


    // > Model
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<Integer>           integerValue;
    private PrimitiveFunctor<String>            stringValue;
    private PrimitiveFunctor<Boolean>           booleanValue;

    private PrimitiveFunctor<ProgramValueType>  valueType;


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


    public static ProgramValueUnion fromYaml(YamlParser yaml, ProgramValueType valueType)
                  throws YamlParseException
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


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Program Value Union's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        switch (this.type())
        {
            case STRING:
                return YamlBuilder.string(this.stringValue());
            case INTEGER:
                return YamlBuilder.integer(this.integerValue());
            case BOOLEAN:
                return YamlBuilder.bool(this.booleanValue());
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(ProgramValueType.class.getName())));
        }

        return null;
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the type of this ProgramValueUnion.
     * @return The ProgramValueType.
     */
    public ProgramValueType type()
    {
        return this.valueType.getValue();
    }


    /**
     * Get the integer value case for this ProgramValueUnion.
     * @return The Integer value.
     */
    public Integer integerValue()
    {
        return this.integerValue.getValue();
    }


    /**
     * Get the string value case for this ProgramValueUnion
     * @return The String value.
     */
    public String stringValue()
    {
        return this.stringValue.getValue();
    }


    /**
     * Get the boolean value case for this ProgramValueUnion
     * @return The Boolean value.
     */
    public Boolean booleanValue()
    {
        return this.booleanValue.getValue();
    }


    // > To String
    // ------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        switch (this.type())
        {
            case STRING:
                return this.stringValue();
            case INTEGER:
                return this.integerValue().toString();
            case BOOLEAN:
                return this.booleanValue().toString();
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

        if (functionValue.type() != this.type())
            return false;

        switch (functionValue.type())
        {
            case STRING:
                String thatString = functionValue.stringValue();
                String thisString = this.stringValue();
                return new EqualsBuilder()
                            .append(thisString, thatString)
                            .isEquals();
            case INTEGER:
                Integer thatInteger = functionValue.integerValue();
                Integer thisInteger = this.integerValue();
                return new EqualsBuilder()
                        .append(thisInteger, thatInteger)
                        .isEquals();
            case BOOLEAN:
                Boolean thatBoolean = functionValue.booleanValue();
                Boolean thisBoolean = this.booleanValue();
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
