
package com.kispoko.tome.model.engine.value;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.model.engine.value.error.UndefinedValueError;
import com.kispoko.tome.model.engine.value.error.UnexpectedValueTypeError;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Dictionary
 */
public class Dictionary extends Model
                        implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private CollectionFunctor<ValueSetUnion>    valueSets;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Map<String,ValueSetUnion>           valueSetIndex;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Dictionary()
    {
        this.id = null;

        this.valueSets = CollectionFunctor.empty(ValueSetUnion.class);
    }


    public Dictionary(UUID id, List<ValueSetUnion> valueSets)
    {
        this.id        = id;

        this.valueSets = CollectionFunctor.full(valueSets, ValueSetUnion.class);

        initialize();
    }


    public static Dictionary fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID                id        = UUID.randomUUID();

        List<ValueSetUnion> valueSets = yaml.atKey("sets").forEach(
                                                new YamlParser.ForEach<ValueSetUnion>() {
            @Override
            public ValueSetUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return ValueSetUnion.fromYaml(yaml);
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

    // ** Value Sets
    // ------------------------------------------------------------------------------------------

    /**
     * The value sets as an immutable list.
     * @return The value sets.
     */
    public List<ValueSetUnion> valueSets()
    {
        return Collections.unmodifiableList(this.valueSets.getValue());
    }


    /**
     * The value sets.
     * @return The ValueSetUnion list.
     */
    private List<ValueSetUnion> valueSetsMutable()
    {
        return this.valueSets.getValue();
    }


    /**
     * Get a value set from the dictionary with the given name.
     * @param setName The value set name.
     * @return The Value Set, or null if it does not exist.
     */
    public ValueSetUnion lookup(String setName)
    {
        return this.valueSetIndex.get(setName);
    }


    /**
     * Get a value from the dictionary. Returns null if the value could not be found.
     * @param setName The name of the value's value set.
     * @param valueName The value name.
     * @return The Value Union or null.
     */
    public ValueUnion lookup(String setName, String valueName)
    {
        if (setName == null || valueName == null)
            return null;

        ValueUnion valueUnion = null;

        if (valueSetIndex.containsKey(setName))
        {
            ValueSetUnion valueSetUnion = this.valueSetIndex.get(setName);

            if (valueSetUnion != null)
            {
                switch (valueSetUnion.type())
                {
                    case BASE:
                        BaseValueSet baseValueSet = valueSetUnion.base();
                        if (baseValueSet.hasValue(valueName))
                            valueUnion = baseValueSet.valueWithName(valueName);
                        break;
                    case COMPOUND:
                        CompoundValueSet compoundValueSet = valueSetUnion.compound();
                        if (compoundValueSet.hasValue(valueName))
                            valueUnion = compoundValueSet.valueWithName(valueName);
                        break;
                }
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


    public ValueUnion valueUnion(ValueReference valueReference)
    {
        ValueUnion valueUnion = this.lookup(valueReference.valueSetName(),
                                            valueReference.valueName());
        return valueUnion;
    }


    // > Sort
    // ------------------------------------------------------------------------------------------

    public void sortAscByLabel()
    {
        Collections.sort(this.valueSetsMutable(), new Comparator<ValueSetUnion>()
        {
            @Override
            public int compare(ValueSetUnion valueSetUnion1, ValueSetUnion valueSetUnion2)
            {
                return valueSetUnion1.valueSet().label()
                                     .compareToIgnoreCase(valueSetUnion2.valueSet().label());
            }
        });
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initialize()
    {
        this.valueSetIndex = new HashMap<>();

        for (ValueSetUnion valueSetUnion : this.valueSets())
        {
            this.valueSetIndex.put(valueSetUnion.valueSet().name(), valueSetUnion);
        }

    }

}
