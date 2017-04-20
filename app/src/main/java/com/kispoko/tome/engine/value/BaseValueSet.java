
package com.kispoko.tome.engine.value;



import com.kispoko.tome.R;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.util.tuple.Tuple2;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * ValueSet
 */
public class BaseValueSet extends Model
                          implements ValueSet, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                id;

    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>            name;
    private PrimitiveFunctor<String>            label;
    private PrimitiveFunctor<String>            labelSingular;
    private PrimitiveFunctor<String>            description;
    private PrimitiveFunctor<ValueSetValueType> valueType;
    private CollectionFunctor<ValueUnion>       values;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Map<String,ValueUnion>              valueIndex;

    private Pattern                             defaultTextValueIdPattern;


    private boolean                             isSorted;
    private Sort                                sort;

    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BaseValueSet()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.label          = new PrimitiveFunctor<>(null, String.class);
        this.labelSingular  = new PrimitiveFunctor<>(null, String.class);
        this.description    = new PrimitiveFunctor<>(null, String.class);

        this.valueType      = new PrimitiveFunctor<>(null, ValueSetValueType.class);
        this.values         = CollectionFunctor.empty(ValueUnion.class);

        this.initializeFunctors();
    }


    public BaseValueSet(UUID id,
                        String name,
                        String label,
                        String labeSingular,
                        String description,
                        ValueSetValueType valueType,
                        List<ValueUnion> values)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);
        this.label          = new PrimitiveFunctor<>(label, String.class);
        this.labelSingular  = new PrimitiveFunctor<>(labeSingular, String.class);
        this.description    = new PrimitiveFunctor<>(description, String.class);

        this.valueType      = new PrimitiveFunctor<>(valueType, ValueSetValueType.class);
        this.values         = CollectionFunctor.full(values, ValueUnion.class);

        // TODO remove / prevent duplicates

        this.setValueType(valueType);

        this.initializeFunctors();
        this.initializeValueSet();
    }


    /**
     * Create a Value Set from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Value Set.
     * @throws YamlParseException
     */
    public static BaseValueSet fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID              id            = UUID.randomUUID();

        String            name          = yaml.atKey("name").getString();

        String            label         = yaml.atMaybeKey("label").getTrimmedString();

        String            labelSingular = yaml.atMaybeKey("label_singular").getTrimmedString();

        String            description   = yaml.atMaybeKey("description").getTrimmedString();

        ValueSetValueType valueType     = ValueSetValueType.fromYaml(
                                                        yaml.atMaybeKey("value_type"));

        List<ValueUnion>  values        = yaml.atKey("values").forEach(
                                            new YamlParser.ForEach<ValueUnion>()
        {
            @Override
            public ValueUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return ValueUnion.fromYaml(yaml);
            }
        });

        return new BaseValueSet(id, name, label, labelSingular, description, valueType, values);
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
        initializeValueSet();
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
                .putString("label", this.label())
                .putString("description", this.description())
                .putYaml("value_type", this.valueType())
                .putList("values", this.valuesMutable());
    }


    // > Value Set
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
     * The value set description.
     * @return The description.
     */
    public String description()
    {
        return this.description.getValue();
    }


    /**
     * The size of the value set (number of values it contains).
     * @return The size.
     */
    public int size()
    {
        return this.values().size();
    }


    /**
     * The set values as an immutable collection.
     * @return The values.
     */
    public List<ValueUnion> values()
    {
        return Collections.unmodifiableList(this.values.getValue());
    }


    /**
     * Returns true if the set contains a value with the given name. False otherwise.
     * @param valueName The value name.
     * @return True if the set has a value with the name.
     */
    @Override
    public boolean hasValue(String valueName)
    {
        return valueIndex.containsKey(valueName);
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The value set singular label.
     * @return The singular label.
     */
    public String labelSingular()
    {
        return this.labelSingular.getValue();
    }


    public Tuple2<String,String> nextDefaultTextValue()
    {
        Set<Integer> numbersUsed = new HashSet<>();

        for (ValueUnion valueUnion : values())
        {
            if (valueUnion.type() == ValueType.TEXT)
            {
                TextValue textValue = valueUnion.textValue();
                String textValueName = textValue.name();
                Matcher m = this.defaultTextValueIdPattern.matcher(textValueName);

                if (m.find()) {
                    String numberString = m.group(1);
                    Integer number = Integer.parseInt(numberString);
                    numbersUsed.add(number);
                }
            }
        }

        int i = 1;
        while (numbersUsed.contains(i))
            i++;

        String defaultName = "new_value_" + Integer.toString(i);
        String defaultValue = "New Value " + Integer.toString(i);

        return new Tuple2<>(defaultName, defaultValue);
    }


    // ** Value Type
    // ------------------------------------------------------------------------------------------

    /**
     * The type of values in the value set.
     * @return The value type.
     */
    public ValueSetValueType valueType()
    {
        return this.valueType.getValue();
    }


    /**
     * Set the value type for all the values in the Value Set. If null, defaults to ANY, which
     * means the set is untyped.
     * @param valueType The value type.
     */
    public void setValueType(ValueSetValueType valueType)
    {
        if (valueType != null)
            this.valueType.setValue(valueType);
        else
            this.valueType.setValue(ValueSetValueType.ANY);
    }


    // > Values
    // ------------------------------------------------------------------------------------------

    /**
     * The values in the set as a mutable collection, but for internal use only.
     * @return The values.
     */
    private List<ValueUnion> valuesMutable()
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
     * Add a text value to the values collection. If the Value Set value type is not of
     * type TEXT or ANY, then the text value will not be added and a Value Exception with an
     * UnexpectedValueTypeError will be thrown.
     * @param textValue The text value.
     */
    public void addValue(TextValue textValue)
    {
        if (this.valueType() == ValueSetValueType.ANY ||
            this.valueType() == ValueSetValueType.TEXT) {
            this.valuesMutable().add(ValueUnion.asText(UUID.randomUUID(), textValue));
            this.isSorted = false;
        }
    }


    /**
     * Add a text value to the values collection. If the Value Set value type is not of
     * type TEXT or ANY, then the text value will not be added and a Value Exception with an
     * UnexpectedValueTypeError will be thrown.
       @param numberValue The number value.
     */
    public void addValue(NumberValue numberValue)
    {
        if (this.valueType() == ValueSetValueType.ANY ||
            this.valueType() == ValueSetValueType.NUMBER) {
            this.valuesMutable().add(ValueUnion.asNumber(UUID.randomUUID(), numberValue));
        }
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


    // > Sort
    // ------------------------------------------------------------------------------------------

    public void sortAscByLabel()
    {
        if (this.isSorted && this.sort == Sort.ASC)
            return;

        Collections.sort(this.valuesMutable(), new Comparator<ValueUnion>()
        {
            @Override
            public int compare(ValueUnion valueUnion1, ValueUnion valueUnion2)
            {
                return valueUnion1.value().valueString()
                            .compareToIgnoreCase(valueUnion2.value().valueString());
            }
        });

        this.isSorted   = true;
        this.sort       = Sort.ASC;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initializeValueSet()
    {
        // [1] Index the VALUES
        // -------------------------------------------------------------------------------------

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

        // [2] Set pattern to match default IDs
        // -------------------------------------------------------------------------------------

        this.defaultTextValueIdPattern = Pattern.compile("^new_value_(\\d+)$");
    }


    private void initializeFunctors()
    {
        // Name
        this.name.setName("name");
        this.name.setLabelId(R.string.value_set_field_id_label);
        this.name.setDescriptionId(R.string.value_set_field_id_description);

        // Label
        this.label.setName("label");
        this.label.setLabelId(R.string.value_set_field_name_label);
        this.label.setDescriptionId(R.string.value_set_field_name_description);

        // Label Singular
        this.labelSingular.setName("label_singular");
        this.labelSingular.setLabelId(R.string.value_set_field_name_singular_label);
        this.labelSingular.setDescriptionId(R.string.value_set_field_name_singular_description);

        // Description
        this.description.setName("description");
        this.description.setLabelId(R.string.value_set_field_description_label);
        this.description.setDescriptionId(R.string.value_set_field_description_description);

        // Value Type
        this.valueType.setName("value_type");
        this.valueType.setLabelId(R.string.value_set_field_value_type_label);
        this.valueType.setDescriptionId(R.string.value_set_field_value_type_description);

        // Values
        this.values.setName("values");
        this.values.setLabelId(R.string.value_set_field_values_label);
        this.values.setDescriptionId(R.string.value_set_field_values_description);
    }



    // SORT
    // ------------------------------------------------------------------------------------------

    private enum Sort {
        ASC,
        DESC
    }

}
