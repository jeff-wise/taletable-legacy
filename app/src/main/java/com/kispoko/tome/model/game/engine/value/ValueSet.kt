
package com.kispoko.tome.model.game.engine.value


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.EngineValueType
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import java.util.*



/**
 * Value Set
 */
@Suppress("UNCHECKED_CAST")
sealed class ValueSet(open val valueSetId : Func<ValueSetId>,
                      open val label : Func<ValueSetLabel>,
                      open val labelSingular: Func<ValueSetLabelSingular>,
                      open val description : Func<ValueSetDescription>,
                      open val valueType : Func<EngineValueType>) : Model
{

    companion object : Factory<ValueSet>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ValueSet> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "base"     -> ValueSetBase.fromDocument(doc)
                                    as ValueParser<ValueSet>
                    "compound" -> ValueSetCompound.fromDocument(doc)
                                    as ValueParser<ValueSet>
                    else       -> effError<ValueError,ValueSet>(
                                            UnknownCase(doc.case(), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Base Value Set
 */
data class ValueSetBase(override val id : UUID,
                        override val valueSetId : Func<ValueSetId>,
                        override val label : Func<ValueSetLabel>,
                        override val labelSingular: Func<ValueSetLabelSingular>,
                        override val description: Func<ValueSetDescription>,
                        override val valueType : Func<EngineValueType>,
                        val values : Coll<Value>)
                        : ValueSet(valueSetId, label, labelSingular, description, valueType)
{

    companion object : Factory<ValueSetBase>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ValueSetBase> = when (doc)
        {
            is DocDict ->
            {
                effApply(::ValueSetBase,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Value Set Id
                         doc.at("value_set_id") ap {
                             effApply(::Prim, ValueSetId.fromDocument(it))
                         },
                         // Label
                         split(doc.maybeAt("label"),
                               nullEff<ValueSetLabel>(),
                               { effApply(::Prim, ValueSetLabel.fromDocument(it)) }),
                         // Label Singular
                         split(doc.maybeAt("label_singular"),
                               nullEff<ValueSetLabelSingular>(),
                               { effApply(::Prim, ValueSetLabelSingular.fromDocument(it)) }),
                         // Description
                         split(doc.maybeAt("description"),
                               nullEff<ValueSetDescription>(),
                               { effApply(::Prim, ValueSetDescription.fromDocument(it)) }),
                         // Value Type
                         split(doc.maybeEnum<EngineValueType>("value_type"),
                               nullEff<EngineValueType>(),
                               { effValue(Prim(it)) }),
                         // Values,
                         doc.list("values") ap { docList ->
                             effApply(::Coll,
                                 docList.map { Value.fromDocument(it) })
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Compound Value Set
 */
data class ValueSetCompound(override val id : UUID,
                            override val valueSetId : Func<ValueSetId>,
                            override val label : Func<ValueSetLabel>,
                            override val labelSingular: Func<ValueSetLabelSingular>,
                            override val description: Func<ValueSetDescription>,
                            override val valueType : Func<EngineValueType>,
                            val valueSetIds : Prim<List<ValueSetId>>)
                            : ValueSet(valueSetId, label, labelSingular, description, valueType)
{

    companion object : Factory<ValueSetCompound>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ValueSetCompound> = when (doc)
        {
            is DocDict ->
            {
                effApply(::ValueSetCompound,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Value Set Id
                         doc.at("value_set_id") ap {
                             effApply(::Prim, ValueSetId.fromDocument(it))
                         },
                         // Label
                         split(doc.maybeAt("label"),
                               nullEff<ValueSetLabel>(),
                               { effApply(::Prim, ValueSetLabel.fromDocument(it)) }),
                         // Label Singular
                         split(doc.maybeAt("label_singular"),
                               nullEff<ValueSetLabelSingular>(),
                               { effApply(::Prim, ValueSetLabelSingular.fromDocument(it)) }),
                         // Description
                         split(doc.maybeAt("description"),
                               nullEff<ValueSetDescription>(),
                               { effApply(::Prim, ValueSetDescription.fromDocument(it)) }),
                         // Value Type
                         split(doc.maybeEnum<EngineValueType>("value_type"),
                               nullEff<EngineValueType>(),
                               { effValue(Prim(it)) }),
                         // Value Set Ids
                         doc.list("value_set_ids") ap { docList ->
                             effApply(::Prim,
                                 docList.map { ValueSetId.fromDocument(it) })
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * ValueSet Id
 */
data class ValueSetId(val value : String)
{

    companion object : Factory<ValueSetId>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ValueSetId> = when (doc)
        {
            is DocText -> effValue(ValueSetId(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * ValueSet Label
 */
data class ValueSetLabel(val value : String)
{

    companion object : Factory<ValueSetLabel>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ValueSetLabel> = when (doc)
        {
            is DocText -> effValue(ValueSetLabel(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * ValueSet Label Singular
 */
data class ValueSetLabelSingular(val value : String)
{

    companion object : Factory<ValueSetLabelSingular>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ValueSetLabelSingular> = when (doc)
        {
            is DocText -> effValue(ValueSetLabelSingular(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * ValueSet Description
 */
data class ValueSetDescription(val value : String)
{

    companion object : Factory<ValueSetDescription>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ValueSetDescription> = when (doc)
        {
            is DocText -> effValue(ValueSetDescription(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}



//
//
///**
// * Value Set Interface
// */
//public interface ValueSet
//{
//    String           name();
//    String           label();
//    String           labelSingular();
//    String           description();
//    int              size();
//    List<ValueUnion> values();
//    int              lengthOfLongestValueString();
//    boolean          hasValue(String valueName);
//    ValueUnion       valueWithName(String valueName);
//}

//
//
//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The value set singular label.
//     * @return The singular label.
//     */
//    public String labelSingular()
//    {
//        return this.labelSingular.getValue();
//    }
//
//
//    public Tuple2<String,String> nextDefaultTextValue()
//    {
//        Set<Integer> numbersUsed = new HashSet<>();
//
//        for (ValueUnion valueUnion : values())
//        {
//            if (valueUnion.type() == ValueType.TEXT)
//            {
//                TextValue textValue = valueUnion.textValue();
//                String textValueName = textValue.name();
//                Matcher m = this.defaultTextValueIdPattern.matcher(textValueName);
//
//                if (m.find()) {
//                    String numberString = m.group(1);
//                    Integer number = Integer.parseInt(numberString);
//                    numbersUsed.add(number);
//                }
//            }
//        }
//
//        int i = 1;
//        while (numbersUsed.contains(i))
//            i++;
//
//        String defaultName = "new_value_" + Integer.toString(i);
//        String defaultValue = "New Value " + Integer.toString(i);
//
//        return new Tuple2<>(defaultName, defaultValue);
//    }
//
//
//    // ** Value Type
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The type of values in the value set.
//     * @return The value type.
//     */
//    public ValueSetValueType valueType()
//    {
//        return this.valueType.getValue();
//    }
//
//
//    /**
//     * Set the value type for all the values in the Value Set. If null, defaults to ANY, which
//     * means the set is untyped.
//     * @param valueType The value type.
//     */
//    public void setValueType(ValueSetValueType valueType)
//    {
//        if (valueType != null)
//            this.valueType.setValue(valueType);
//        else
//            this.valueType.setValue(ValueSetValueType.ANY);
//    }
//
//
//    // > Values
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The values in the set as a mutable collection, but for internal use only.
//     * @return The values.
//     */
//    private List<ValueUnion> valuesMutable()
//    {
//        return this.values.getValue();
//    }
//
//
//    /**
//     * Get the value with the given name from the set. Returns null if the set does not contain
//     * a value with that name.
//     * @param name The value name.
//     * @return The value union.
//     */
//    public ValueUnion valueWithName(String name)
//    {
//        if (valueIndex.containsKey(name))
//        {
//            return valueIndex.get(name);
//        }
//        else
//        {
//            return null;
//        }
//    }
//
//
//    /**
//     * Add a text value to the values collection. If the Value Set value type is not of
//     * type TEXT or ANY, then the text value will not be added and a Value Exception with an
//     * UnexpectedValueTypeError will be thrown.
//     * @param textValue The text value.
//     */
//    public void addValue(TextValue textValue)
//    {
//        if (this.valueType() == ValueSetValueType.ANY ||
//            this.valueType() == ValueSetValueType.TEXT) {
//            this.valuesMutable().add(ValueUnion.asText(UUID.randomUUID(), textValue));
//            this.isSorted = false;
//        }
//    }
//
//
//    /**
//     * Add a text value to the values collection. If the Value Set value type is not of
//     * type TEXT or ANY, then the text value will not be added and a Value Exception with an
//     * UnexpectedValueTypeError will be thrown.
//       @param numberValue The number value.
//     */
//    public void addValue(NumberValue numberValue)
//    {
//        if (this.valueType() == ValueSetValueType.ANY ||
//            this.valueType() == ValueSetValueType.NUMBER) {
//            this.valuesMutable().add(ValueUnion.asNumber(UUID.randomUUID(), numberValue));
//        }
//    }
//
//    // > Length of Longest Value String
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Return the size (in characters) of the value that has the longest string representation.
//     * @return The length.
//     */
//    public int lengthOfLongestValueString()
//    {
//        int longest = 0;
//
//        for (ValueUnion valueUnion : this.values())
//        {
//            int valueLength = 0;
//            switch (valueUnion.type())
//            {
//                case TEXT:
//                    valueLength = valueUnion.textValue().value().length();
//                    break;
//                case NUMBER:
//                    valueLength = valueUnion.numberValue().value().toString().length();
//                    break;
//            }
//
//            if (valueLength > longest)
//                longest = valueLength;
//        }
//
//        return longest;
//    }
//
//
//    // > Sort
//    // ------------------------------------------------------------------------------------------
//
//    public void sortAscByLabel()
//    {
//        if (this.isSorted && this.sort == Sort.ASC)
//            return;
//
//        Collections.sort(this.valuesMutable(), new Comparator<ValueUnion>()
//        {
//            @Override
//            public int compare(ValueUnion valueUnion1, ValueUnion valueUnion2)
//            {
//                return valueUnion1.value().valueString()
//                            .compareToIgnoreCase(valueUnion2.value().valueString());
//            }
//        });
//
//        this.isSorted   = true;
//        this.sort       = Sort.ASC;
//    }
//
//
//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    private void initializeValueSet()
//    {
//        // [1] Index the VALUES
//        // -------------------------------------------------------------------------------------
//
//        this.valueIndex = new HashMap<>();
//
//        for (ValueUnion valueUnion : this.values())
//        {
//            switch (valueUnion.type())
//            {
//                case TEXT:
//                    valueIndex.put(valueUnion.textValue().name(), valueUnion);
//                    break;
//                case NUMBER:
//                    valueIndex.put(valueUnion.numberValue().name(), valueUnion);
//                    break;
//            }
//        }
//
//        // [2] Set pattern to match default IDs
//        // -------------------------------------------------------------------------------------
//
//        this.defaultTextValueIdPattern = Pattern.compile("^new_value_(\\d+)$");
//    }
//
//
//    private void initializeFunctors()
//    {
//        // Name
//        this.name.setName("name");
//        this.name.setLabelId(R.string.value_set_field_id_label);
//        this.name.setDescriptionId(R.string.value_set_field_id_description);
//
//        // Label
//        this.label.setName("label");
//        this.label.setLabelId(R.string.value_set_field_name_label);
//        this.label.setDescriptionId(R.string.value_set_field_name_description);
//
//        // Label Singular
//        this.labelSingular.setName("label_singular");
//        this.labelSingular.setLabelId(R.string.value_set_field_name_singular_label);
//        this.labelSingular.setDescriptionId(R.string.value_set_field_name_singular_description);
//
//        // Description
//        this.description.setName("description");
//        this.description.setLabelId(R.string.value_set_field_description_label);
//        this.description.setDescriptionId(R.string.value_set_field_description_description);
//
//        // Value Type
//        this.valueType.setName("value_type");
//        this.valueType.setLabelId(R.string.value_set_field_value_type_label);
//        this.valueType.setDescriptionId(R.string.value_set_field_value_type_description);
//
//        // Values
//        this.values.setName("values");
//        this.values.setLabelId(R.string.value_set_field_values_label);
//        this.values.setDescriptionId(R.string.value_set_field_values_description);
//    }
//
//
//
//    // SORT
//    // ------------------------------------------------------------------------------------------
//
//    private enum Sort {
//        ASC,
//        DESC
//    }

