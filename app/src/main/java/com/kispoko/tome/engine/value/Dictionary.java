
package com.kispoko.tome.engine.value;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.engine.value.error.UndefinedValueError;
import com.kispoko.tome.engine.value.error.UnexpectedValueTypeError;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Dictionary
 */
public class Dictionary implements Model, Serializable
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


    public static Dictionary fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID           id        = UUID.randomUUID();

        List<ValueSet> valueSets = yaml.atKey("sets").forEach(new Yaml.ForEach<ValueSet>() {
            @Override
            public ValueSet forEach(Yaml yaml, int index) throws YamlException {
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


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The value sets.
     * @return The value sets.
     */
    private List<ValueSet> valueSets()
    {
        return this.valueSets.getValue();
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