
package com.kispoko.tome.engine.value;


import com.kispoko.tome.R;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.sheet.SheetManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;



/**
 * Compound Value Set
 */
public class CompoundValueSet extends Model
                              implements ValueSet, ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>            name;
    private PrimitiveFunctor<String>            label;
    private PrimitiveFunctor<String>            labelSingular;
    private PrimitiveFunctor<String>            description;

    private PrimitiveFunctor<ValueSetValueType> valueType;

    private PrimitiveFunctor<String[]>          valueSetNames;


    // > Internal
    // -----------------------------------------------------------------------------------------

    private List<ValueSet>                      valueSets;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    // TODO verify all value set names point to base value sets
    public CompoundValueSet()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.label          = new PrimitiveFunctor<>(null, String.class);
        this.labelSingular  = new PrimitiveFunctor<>(null, String.class);
        this.description    = new PrimitiveFunctor<>(null, String.class);

        this.valueType      = new PrimitiveFunctor<>(null, ValueSetValueType.class);

        this.valueSetNames  = new PrimitiveFunctor<>(null, String[].class);

        this.initializeFunctors();
    }


    public CompoundValueSet(UUID id,
                            String name,
                            String label,
                            String labeSingular,
                            String description,
                            ValueSetValueType valueType,
                            List<String> valueSetNames)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);
        this.label          = new PrimitiveFunctor<>(label, String.class);
        this.labelSingular  = new PrimitiveFunctor<>(labeSingular, String.class);
        this.description    = new PrimitiveFunctor<>(description, String.class);

        this.valueType      = new PrimitiveFunctor<>(valueType, ValueSetValueType.class);

        String[] valueSetNameArray = valueSetNames.toArray(new String[valueSetNames.size()]);
        this.valueSetNames  = new PrimitiveFunctor<>(valueSetNameArray, String[].class);

        this.setValueType(valueType);

        this.initializeFunctors();
    }


    /**
     * Create a Value Set from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Value Set.
     * @throws YamlParseException
     */
    public static CompoundValueSet fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID              id            = UUID.randomUUID();

        String            name          = yaml.atKey("name").getString();
        String            label         = yaml.atMaybeKey("label").getTrimmedString();
        String            labelSingular = yaml.atMaybeKey("label_singular").getTrimmedString();
        String            description   = yaml.atMaybeKey("description").getTrimmedString();

        ValueSetValueType valueType     = ValueSetValueType.fromYaml(
                                                        yaml.atMaybeKey("value_type"));

        List<String>      valueSetNames = yaml.atMaybeKey("value_sets").getStringList();


        return new CompoundValueSet(id, name, label, labelSingular, description,
                                    valueType, valueSetNames);
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
                .putStringList("value_sets", this.valueSetNames());
    }



    // > Value Set
    // ------------------------------------------------------------------------------------------

    /**
     * The compound value set name.
     * @return The name.
     */
    @Override
    public String name()
    {
        return this.name.getValue();
    }


    /**
     * The compound value set's label.
      * @return The label.
     */
    @Override
    public String label()
    {
        return this.label.getValue();
    }


    /**
     * The compound value set singular label.
     * @return The singular label.
     */
    public String labelSingular()
    {
        return this.labelSingular.getValue();
    }


    /**
     * The compound value set description.
     * @return The description.
     */
    @Override
    public String description()
    {
        return this.description.getValue();
    }


    @Override
    public int size()
    {
        int numberOfValues = 0;

        for (ValueSet valueSet : this.valueSets())
        {
            numberOfValues += valueSet.size();
        }

        return numberOfValues;
    }


    @Override
    public List<ValueUnion> values()
    {
        List<ValueUnion> values = new ArrayList<>();

        for (ValueSet valueSet : this.valueSets()) {
            values.addAll(valueSet.values());
        }

        return values;
    }


    @Override
    public int lengthOfLongestValueString()
    {
        int longest = 0;

        for (ValueSet valueSet : this.valueSets())
        {
            int valueSetLongest = valueSet.lengthOfLongestValueString();

            if (valueSetLongest > longest)
                longest = valueSetLongest;
        }

        return longest;
    }


    @Override
    public boolean hasValue(String valueName)
    {
        for (ValueSet valueSet : this.valueSets())
        {
            if (valueSet.hasValue(valueName))
                return true;
        }

        return false;
    }


    @Override
    public ValueUnion valueWithName(String valueName)
    {
        for (ValueSet valueSet : this.valueSets())
        {
            if (valueSet.hasValue(valueName))
                return valueSet.valueWithName(valueName);
        }

        return null;
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Value Type
    // -----------------------------------------------------------------------------------------

    /**
     * The type of values of each value set in the compound value set.
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
        // TODO verify type is true of all child value sets
        if (valueType != null)
            this.valueType.setValue(valueType);
        else
            this.valueType.setValue(ValueSetValueType.ANY);
    }


    // ** Value Sets
    // -----------------------------------------------------------------------------------------

    public List<String> valueSetNames()
    {
        return Collections.unmodifiableList(Arrays.asList(this.valueSetNames.getValue()));
    }


    // ** Values
    // -----------------------------------------------------------------------------------------


    // > Values With Headers
    // -----------------------------------------------------------------------------------------

    /**
     * Returns a list of all of the values in the Compound Value Set with the value set headers
     * above their respective values
     * @return
     */
    public List<Object> valuesWithHeaders()
    {
        List<Object> items = new ArrayList<>();

        for (ValueSet valueSet : this.valueSets())
        {
            items.add(valueSet.label());
            items.addAll(valueSet.values());
        }

        return items;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * The Value Sets contained in the Compound Value Set. If the value sets were already found,
     * use those. If not, look up each value set name in this Compound Value Set in the dictionary,
     * and store those in a list.
     * @return The Value Set list.
     */
    private List<ValueSet> valueSets()
    {
        if (this.valueSets != null)
            return this.valueSets;

        List<ValueSet> valueSets = new ArrayList<>();

        for (String valueSetName : this.valueSetNames())
        {
            Dictionary dictionary = SheetManager.dictionary();

            if (dictionary != null)
            {
                ValueSetUnion valueSetUnion = dictionary.lookup(valueSetName);

                if (valueSetUnion != null)
                    valueSets.add(valueSetUnion.valueSet());
            }
        }

        return valueSets;
    }


    private void initializeFunctors()
    {
        // Name
        this.name.setName("name");
        this.name.setLabelId(R.string.compound_value_set_field_name_label);
        this.name.setDescriptionId(R.string.compound_value_set_field_name_description);

        // Label
        this.label.setName("label");
        this.label.setLabelId(R.string.compound_value_set_field_label_label);
        this.label.setDescriptionId(R.string.compound_value_set_field_label_description);

        // Label Singular
        this.labelSingular.setName("label_singular");
        this.labelSingular.setLabelId(R.string.compound_value_set_field_label_singular_label);
        this.labelSingular
                .setDescriptionId(R.string.compound_value_set_field_label_singular_description);

        // Description
        this.description.setName("description");
        this.description.setLabelId(R.string.compound_value_set_field_description_label);
        this.description
                .setDescriptionId(R.string.compound_value_set_field_description_description);

        // Value Type
        this.valueType.setName("value_type");
        this.valueType.setLabelId(R.string.compound_value_set_field_value_type_label);
        this.valueType.setDescriptionId(R.string.compound_value_set_field_value_type_description);

        // Values
        this.valueSetNames.setName("value_sets");
        this.valueSetNames.setLabelId(R.string.compound_value_set_field_value_sets_label);
        this.valueSetNames
                    .setDescriptionId(R.string.compound_value_set_field_value_sets_description);
    }


}
