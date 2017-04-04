
package com.kispoko.tome.engine.program;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.mechanic.dice.DiceRoll;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



/**
 * Function Value
 */
public class ProgramValueUnion extends Model
                               implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<Integer>           integerValue;
    private PrimitiveFunctor<String>            stringValue;
    private PrimitiveFunctor<Boolean>           booleanValue;
    private ModelFunctor<DiceRoll>              diceValue;
    private PrimitiveFunctor<String[]>          listValue;

    private PrimitiveFunctor<ProgramValueType>  valueType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ProgramValueUnion()
    {
        this.id             = null;

        this.integerValue   = new PrimitiveFunctor<>(null, Integer.class);
        this.stringValue    = new PrimitiveFunctor<>(null, String.class);
        this.booleanValue   = new PrimitiveFunctor<>(null, Boolean.class);
        this.diceValue      = ModelFunctor.empty(DiceRoll.class);
        this.listValue      = new PrimitiveFunctor<>(null, String[].class);

        this.valueType      = new PrimitiveFunctor<>(null, ProgramValueType.class);
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
        this.diceValue    = ModelFunctor.full(null, DiceRoll.class);
        this.listValue    = new PrimitiveFunctor<>(null, String[].class);

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
            case DICE:
                this.diceValue.setValue((DiceRoll) value);
                break;
            case LIST:
                this.listValue.setValue((String[]) value);
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


    /**
     * Create a "dice" variant.
     * @param diceValue The dice value.
     * @return A Program Value "dice" case.
     */
    public static ProgramValueUnion asDice(UUID id, DiceRoll diceValue)
    {
        return new ProgramValueUnion(id, diceValue, ProgramValueType.DICE);
    }


    /**
     * Create a "dice" variant that is not a model.
     * @param diceValue The dice value.
     * @return A Program Value "dice" case.
     */
    public static ProgramValueUnion asDice(DiceRoll diceValue)
    {
        return new ProgramValueUnion(null, diceValue, ProgramValueType.DICE);
    }


    /**
     * Create a "list" variant.
     * @param listValue The list value.
     * @return A Program Value "list" case.
     */
    public static ProgramValueUnion asList(UUID id, List<String> listValue)
    {
        String[] stringArray = new String[listValue.size()];
        listValue.toArray(stringArray);
        return new ProgramValueUnion(id, stringArray, ProgramValueType.LIST);
    }


    /**
     * Create a "list" variant that is not a model.
     * @param listValue The list value.
     * @return A Program Value "list" case.
     */
    public static ProgramValueUnion asList(List<String> listValue)
    {
        String[] stringArray = new String[listValue.size()];
        listValue.toArray(stringArray);
        return new ProgramValueUnion(null, stringArray, ProgramValueType.LIST);
    }


    /**
     * Create a ProgramValueUnion from its Yaml representation.
     * @param yaml The yaml parser.
     * @param valueType The type of Program VAlue.
     * @return The parsed ProgramValueUnion.
     * @throws YamlParseException
     */
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
            case DICE:
                return ProgramValueUnion.asDice(id, DiceRoll.fromYaml(yaml));
            case LIST:
                return ProgramValueUnion.asList(id, yaml.getStringList());
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
            case DICE:
                return this.diceValue().toYaml();
            case LIST:
                return YamlBuilder.stringList(this.listValue());
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


    /**
     * The dice value case.
     * @return The DiceRoll.
     */
    public DiceRoll diceValue()
    {
        return this.diceValue.getValue();
    }


    /**
     * The list value case.
     * @return The list value.
     */
    public List<String> listValue()
    {
        return Arrays.asList(this.listValue.getValue());
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
            case DICE:
                return this.diceValue().toString();
            case LIST:
                return this.listValue().toString();
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
            case DICE:
                DiceRoll thatDiceRoll = functionValue.diceValue();
                DiceRoll thisDiceRoll = this.diceValue();
                return new EqualsBuilder()
                        .append(thisDiceRoll, thatDiceRoll)
                        .isEquals();
            case LIST:
                List<String> thatList = functionValue.listValue();
                List<String> thisList = this.listValue();
                return new EqualsBuilder()
                        .append(thisList, thatList)
                        .isEquals();
        }

        return false;
    }


    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                    .append(stringValue)
                    .append(integerValue)
                    .append(booleanValue)
                    .append(diceValue)
                    .append(listValue)
                    .append(valueType)
                    .toHashCode();
    }



}
