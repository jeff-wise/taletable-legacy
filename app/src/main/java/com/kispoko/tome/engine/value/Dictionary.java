
package com.kispoko.tome.engine.value;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.engine.value.error.UndefinedValueError;
import com.kispoko.tome.engine.value.error.UnexpectedValueTypeError;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Dictionary
 */
public class Dictionary implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private CollectionFunctor<ValueSet> valueSets;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Map<String,ValueSet>        valueSetIndex;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Dictionary()
    {
        this.id = null;

        List<Class<? extends ValueSet>> valueSetClasses = new ArrayList<>();
        valueSetClasses.add(ValueSet.class);
        this.valueSets = CollectionFunctor.empty(valueSetClasses);
    }


    public Dictionary(UUID id, List<ValueSet> valueSets)
    {
        this.id        = id;

        List<Class<? extends ValueSet>> valueSetClasses = new ArrayList<>();
        valueSetClasses.add(ValueSet.class);
        this.valueSets = CollectionFunctor.full(valueSets, valueSetClasses);

        initialize();
    }


    public static Dictionary fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID           id        = UUID.randomUUID();

        List<ValueSet> valueSets = yaml.atKey("sets").forEach(new YamlParser.ForEach<ValueSet>() {
            @Override
            public ValueSet forEach(YamlParser yaml, int index) throws YamlParseException {
                return ValueSet.fromYaml(yaml);
            }
        });

        return new Dictionary(id, valueSets);
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

    public void onLoad()
    {
        initialize();
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Dictionary's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putList("sets", this.valueSets());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The value sets.
     * @return The value sets.
     */
    public List<ValueSet> valueSets()
    {
        return this.valueSets.getValue();
    }


    /**
     * Get a value set from the dictionary with the given name.
     * @param setName The value set name.
     * @return The Value Set, or null if it does not exist.
     */
    public ValueSet lookup(String setName)
    {
        return valueSetIndex.get(setName);
    }


    /**
     * Get a value from the dictionary. Returns null if the value could not be found.
     * @param setName The name of the value's value set.
     * @param valueName The value name.
     * @return The Value Union or null.
     */
    public ValueUnion lookup(String setName, String valueName)
    {
        ValueUnion valueUnion = null;

        if (valueSetIndex.containsKey(setName))
        {
            ValueSet valueSet = valueSetIndex.get(setName);

            if (valueSet.hasValue(valueName)) {
                valueUnion = valueSet.valueWithName(valueName);
            }
        }

        return valueUnion;
    }


    public TextValue textValue(ValueReference valueReference)
    {
        ValueUnion valueUnion = this.lookup(valueReference.valueSetName(),
                                            valueReference.valueName());

        if (valueUnion == null) {
            ApplicationFailure.value(
                    ValueException.undefinedValue(
                            new UndefinedValueError(valueReference.valueSetName(),
                                                    valueReference.valueName())));
            return null;
        }


        if (valueUnion.type() != ValueType.TEXT) {
            ApplicationFailure.value(
                    ValueException.unexpectedValueType(
                            new UnexpectedValueTypeError(
                                    valueReference.valueSetName(),
                                    valueReference.valueName(),
                                    ValueType.TEXT,
                                    valueUnion.type())));
            return null;
        }

        return valueUnion.textValue();
    }


    /**
     * Lookup a number value. Handles all of the errors that could occur. May return null, but will
     * log an exception if it does.
     * @param valueReference The number value reference.
     * @return The Number Value.
     */
    public NumberValue numberValue(ValueReference valueReference)
    {
        ValueUnion valueUnion = this.lookup(valueReference.valueSetName(),
                                            valueReference.valueName());

        if (valueUnion == null) {
            ApplicationFailure.value(
                    ValueException.undefinedValue(
                            new UndefinedValueError(valueReference.valueSetName(),
                                                    valueReference.valueName())));
            return null;
        }


        if (valueUnion.type() != ValueType.NUMBER) {
            ApplicationFailure.value(
                    ValueException.unexpectedValueType(
                            new UnexpectedValueTypeError(
                                    valueReference.valueSetName(),
                                    valueReference.valueName(),
                                    ValueType.NUMBER,
                                    valueUnion.type())));
            return null;
        }

        return valueUnion.numberValue();
    }


    public Value value(ValueReference valueReference)
    {
        ValueUnion valueUnion = this.lookup(valueReference.valueSetName(),
                                            valueReference.valueName());


        if (valueUnion != null)
            return valueUnion.value();

        return null;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initialize()
    {
        this.valueSetIndex = new HashMap<>();

        for (ValueSet valueSet : this.valueSets()) {
            this.valueSetIndex.put(valueSet.name(), valueSet);
        }

    }

}
