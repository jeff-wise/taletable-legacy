
package com.kispoko.tome.engine.value;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
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
 * ValueSet
 */
public class ValueSet implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;

    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>        name;
    private PrimitiveFunctor<String>        label;
    private PrimitiveFunctor<String>        labelSingular;
    private PrimitiveFunctor<String>        description;
    private CollectionFunctor<ValueUnion>   values;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Map<String,ValueUnion>          valueIndex;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ValueSet()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.label          = new PrimitiveFunctor<>(null, String.class);
        this.labelSingular  = new PrimitiveFunctor<>(null, String.class);
        this.description    = new PrimitiveFunctor<>(null, String.class);

        List<Class<? extends ValueUnion>> valueClasses = new ArrayList<>();
        valueClasses.add(ValueUnion.class);
        this.values         = CollectionFunctor.empty(valueClasses);
    }


    public ValueSet(UUID id,
                    String name,
                    String label,
                    String labeSingular,
                    String description,
                    List<ValueUnion> values)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);
        this.label          = new PrimitiveFunctor<>(label, String.class);
        this.labelSingular  = new PrimitiveFunctor<>(labeSingular, String.class);
        this.description    = new PrimitiveFunctor<>(description, String.class);

        List<Class<? extends ValueUnion>> valueClasses = new ArrayList<>();
        valueClasses.add(ValueUnion.class);
        this.values         = CollectionFunctor.full(values, valueClasses);

        initialize();
    }


    /**
     * Create a Value Set from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Value Set.
     * @throws YamlParseException
     */
    public static ValueSet fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID             id             = UUID.randomUUID();

        String           name           = yaml.atKey("name").getString();

        String           label          = yaml.atMaybeKey("label").getTrimmedString();

        String           labelSingular  = yaml.atMaybeKey("label_singular").getTrimmedString();

        String           description = yaml.atMaybeKey("description").getTrimmedString();

        List<ValueUnion> values      = yaml.atKey("values").forEach(
                                            new YamlParser.ForEach<ValueUnion>()
        {
            @Override
            public ValueUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return ValueUnion.fromYaml(yaml);
            }
        });

        return new ValueSet(id, name, label, labelSingular, description, values);
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
     * The Value Set's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putList("values", this.values());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The value set name.
     * @return The name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    /**
     * The value set's label.
      * @return The label.
     */
    public String label()
    {
        return this.label.getValue();
    }


    /**
     * The value set singular label.
     * @return The singular label.
     */
    public String labelSingular()
    {
        return this.labelSingular.getValue();
    }


    /**
     * The value set description.
     * @return The description.
     */
    public String description()
    {
        return this.description.getValue();
    }


    // > Values
    // ------------------------------------------------------------------------------------------

    /**
     * The size of the value set (number of values it contains).
     * @return The size.
     */
    public Integer size()
    {
        return this.values().size();
    }


    /**
     * The set values.
     * @return The list of values.
     */
    public List<ValueUnion> values()
    {
        return this.values.getValue();
    }


    /**
     * Get the value with the given name from the set. Returns null if the set does not contain
     * a value with that name.
     * @param name The value name.
     * @return The value union.
     */
    public ValueUnion valueWithName(String name)
    {
        if (valueIndex.containsKey(name))
        {
            return valueIndex.get(name);
        }
        else
        {
            return null;
        }
    }


    /**
     * Returns true if the set contains a value with the given name. False otherwise.
     * @param valueName The value name.
     * @return True if the set has a value with the name.
     */
    public boolean hasValue(String valueName)
    {
        return valueIndex.containsKey(valueName);
    }


    // > Length of Longest Value String
    // ------------------------------------------------------------------------------------------

    /**
     * Return the size (in characters) of the value that has the longest string representation.
     * @return The length.
     */
    public int lengthOfLongestValueString()
    {
        int longest = 0;

        for (ValueUnion valueUnion : this.values())
        {
            int valueLength = 0;
            switch (valueUnion.type())
            {
                case TEXT:
                    valueLength = valueUnion.textValue().value().length();
                    break;
                case NUMBER:
                    valueLength = valueUnion.numberValue().value().toString().length();
                    break;
            }

            if (valueLength > longest)
                longest = valueLength;
        }

        return longest;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initialize()
    {
        this.valueIndex = new HashMap<>();

        for (ValueUnion valueUnion : this.values())
        {
            switch (valueUnion.type())
            {
                case TEXT:
                    valueIndex.put(valueUnion.textValue().name(), valueUnion);
                    break;
                case NUMBER:
                    valueIndex.put(valueUnion.numberValue().name(), valueUnion);
                    break;
            }
        }

    }

}
