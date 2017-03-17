
package com.kispoko.tome.engine.value;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Value Union
 */
public class ValueUnion implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<TextValue>     textValue;
    private ModelFunctor<NumberValue>   numberValue;

    private PrimitiveFunctor<ValueType> type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ValueUnion()
    {
        this.id = null;

        this.textValue = ModelFunctor.empty(TextValue.class);
        this.numberValue = ModelFunctor.empty(NumberValue.class);

        this.type = new PrimitiveFunctor<>(null, ValueType.class);
    }


    private ValueUnion(UUID id, Object value, ValueType type)
    {
        this.id = id;

        this.textValue = ModelFunctor.full(null, TextValue.class);
        this.numberValue = ModelFunctor.full(null, NumberValue.class);

        this.type = new PrimitiveFunctor<>(type, ValueType.class);

        switch (type)
        {
            case TEXT:
                this.textValue.setValue((TextValue) value);
                break;
            case NUMBER:
                this.numberValue.setValue((NumberValue) value);
                break;
        }
    }


    // > Variants
    // ------------------------------------------------------------------------------------------

    /**
     * Create the "text case".
     *
     * @param id        The model id.
     * @param textValue The text value.
     * @return The "text" Value Union.
     */
    public static ValueUnion asText(UUID id, TextValue textValue)
    {
        return new ValueUnion(id, textValue, ValueType.TEXT);
    }


    /**
     * Create the "number case"
     *
     * @param id          The model id.
     * @param numberValue The number value.
     * @return The "number" Value Union.
     */
    public static ValueUnion asNumber(UUID id, NumberValue numberValue)
    {
        return new ValueUnion(id, numberValue, ValueType.NUMBER);
    }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    public static ValueUnion fromYaml(YamlParser yaml)
            throws YamlParseException
    {
        UUID id = UUID.randomUUID();

        ValueType valueType = ValueType.fromYaml(yaml.atKey("type"));

        switch (valueType) {
            case TEXT:
                TextValue textValue = TextValue.fromYaml(yaml.atKey("value"));
                return ValueUnion.asText(id, textValue);
            case NUMBER:
                NumberValue numberValue = NumberValue.fromYaml(yaml.atKey("value"));
                return ValueUnion.asNumber(id, numberValue);
        }

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
     *
     * @return The model UUID.
     */
    public UUID getId() {
        return this.id;
    }


    /**
     * Set the model identifier.
     *
     * @param id The new model UUID.
     */
    public void setId(UUID id) {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    public void onLoad() {
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The value union's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        ToYaml valueToYaml = null;

        switch (this.type())
        {
            case TEXT:
                valueToYaml = this.textValue();
                break;
            case NUMBER:
                valueToYaml = this.numberValue();
                break;
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(ValueType.class.getName())));
        }

        return YamlBuilder.map()
                .putYaml("type", this.type())
                .putYaml("value", valueToYaml);
    }


    // > Value
    // ------------------------------------------------------------------------------------------

    public Value value()
    {
        switch (this.type())
        {
            case TEXT:
                return this.textValue();
            case NUMBER:
                return this.numberValue();
        }

        return null;
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The union type.
     * @return The value type.
     */
    public ValueType type()
    {
        return this.type.getValue();
    }


    /**
     * The text value case.
     * @return The text value.
     */
    public TextValue textValue()
    {
        if (this.type() != ValueType.TEXT) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("text", this.type.name())));
        }
        return this.textValue.getValue();
    }


    /**
     * The number value case.
     * @return The numbrer value.
     */
    public NumberValue numberValue()
    {
        if (this.type() != ValueType.NUMBER) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("number", this.type.name())));
        }
        return this.numberValue.getValue();
    }


}
