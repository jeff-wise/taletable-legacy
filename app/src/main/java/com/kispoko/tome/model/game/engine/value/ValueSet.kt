
package com.kispoko.tome.model.game.engine.value


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppEngineError
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.Engine
import com.kispoko.tome.model.game.engine.EngineValueType
import com.kispoko.tome.rts.game.*
import com.kispoko.tome.rts.game.engine.ValueIsOfUnexpectedType
import com.kispoko.tome.rts.game.engine.ValueSetDoesNotContainValue
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
sealed class ValueSet(open val valueSetId : Prim<ValueSetId>,
                      open val label : Prim<ValueSetLabel>,
                      open val labelSingular: Prim<ValueSetLabelSingular>,
                      open val description : Maybe<Prim<ValueSetDescription>>,
                      open val valueType : Maybe<Prim<EngineValueType>>) : Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

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


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    // Getters
    // -----------------------------------------------------------------------------------------

    fun valueSetId() : ValueSetId = this.valueSetId.value

    fun label() : ValueSetLabel = this.label.value

    fun labelSingular() : ValueSetLabelSingular = this.labelSingular.value

    fun description() : String? = getMaybePrim(this.description)?.value

    fun valueType() : EngineValueType? = getMaybePrim(this.valueType)


    // Lookup
    // -----------------------------------------------------------------------------------------

    abstract fun value(valueId : ValueId, engine : Engine) : AppEff<Value>

    abstract fun numberValue(valueId : ValueId, engine : Engine) : AppEff<ValueNumber>

    abstract fun textValue(valueId : ValueId, engine : Engine) : AppEff<ValueText>


    // -----------------------------------------------------------------------------------------
    // INTERNAL
    // -----------------------------------------------------------------------------------------

    protected fun maybeNumberValue(gameId : GameId,
                                   valueId : ValueId,
                                   value : Value) : AppEff<ValueNumber> = when (value)
    {
        is ValueNumber -> effValue(value)
        else           -> effError(AppEngineError(
                                ValueIsOfUnexpectedType(gameId,
                                                        this.valueSetId(),
                                                        valueId,
                                                        ValueType.NUMBER,
                                                        value.type())))
    }

    protected fun maybeTextValue(gameId : GameId,
                                 valueId : ValueId,
                                 value : Value) : AppEff<ValueText> = when (value)
    {
        is ValueText -> effValue(value)
        else         -> effError(AppEngineError(
                                ValueIsOfUnexpectedType(gameId,
                                                        this.valueSetId(),
                                                        valueId,
                                                        ValueType.TEXT,
                                                        value.type())))
    }

}


/**
 * Base Value Set
 */
data class ValueSetBase(override val id : UUID,
                        override val valueSetId : Prim<ValueSetId>,
                        override val label : Prim<ValueSetLabel>,
                        override val labelSingular: Prim<ValueSetLabelSingular>,
                        override val description: Maybe<Prim<ValueSetDescription>>,
                        override val valueType : Maybe<Prim<EngineValueType>>,
                        val values : Conj<Value>)
                         : ValueSet(valueSetId, label, labelSingular, description, valueType)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private val valuesById : MutableMap<ValueId,Value> =
                                        values.set.associateBy { it.valueId.value }
                                                as MutableMap<ValueId,Value>


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(valueSetId : ValueSetId,
                label : ValueSetLabel,
                labelSingular : ValueSetLabelSingular,
                description : Maybe<ValueSetDescription>,
                valueType : Maybe<EngineValueType>,
                values : MutableSet<Value>)
        : this(UUID.randomUUID(),
               Prim(valueSetId),
               Prim(label),
               Prim(labelSingular),
               maybeLiftPrim(description),
               maybeLiftPrim(valueType),
               Conj(values))


    companion object : Factory<ValueSetBase>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ValueSetBase> = when (doc)
        {
            is DocDict ->
            {
                effApply(::ValueSetBase,
                         // Value Set Id
                         doc.at("value_set_id") ap { ValueSetId.fromDocument(it) },
                         // Label
                         doc.at("label") ap { ValueSetLabel.fromDocument(it) },
                         // Label Singular
                         doc.at("label_singular") ap { ValueSetLabelSingular.fromDocument(it) },
                         // Description
                         split(doc.maybeAt("description"),
                               effValue<ValueError,Maybe<ValueSetDescription>>(Nothing()),
                               { effApply(::Just, ValueSetDescription.fromDocument(it)) }),
                         // Value Type
                         split(doc.maybeAt("value_type"),
                               effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                               { effApply(::Just, EngineValueType.fromDocument(it)) }),
                         // Values,
                         doc.list("values") ap { docList ->
                             docList.mapSetMut { Value.fromDocument(it) }
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    override fun value(valueId: ValueId, engine: Engine) : AppEff<Value> =
            note(this.valuesById[valueId],
                 AppEngineError(ValueSetDoesNotContainValue(engine.gameId,
                                                            this.valueSetId(),
                                                            valueId)))


    override fun textValue(valueId : ValueId, engine : Engine) : AppEff<ValueText> =
        this.value(valueId, engine)
            .apply { this.maybeTextValue(engine.gameId, valueId, it) }


    override fun numberValue(valueId : ValueId, engine : Engine) : AppEff<ValueNumber> =
        this.value(valueId, engine)
            .apply { this.maybeNumberValue(engine.gameId, valueId, it) }


}


/**
 * Compound Value Set
 */
data class ValueSetCompound(override val id : UUID,
                            override val valueSetId : Prim<ValueSetId>,
                            override val label : Prim<ValueSetLabel>,
                            override val labelSingular: Prim<ValueSetLabelSingular>,
                            override val description: Maybe<Prim<ValueSetDescription>>,
                            override val valueType : Maybe<Prim<EngineValueType>>,
                            val valueSetIds : Prim<Set<ValueSetId>>)
                            : ValueSet(valueSetId, label, labelSingular, description, valueType)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(valueSetId : ValueSetId,
                label : ValueSetLabel,
                labelSingular : ValueSetLabelSingular,
                description : Maybe<ValueSetDescription>,
                valueType : Maybe<EngineValueType>,
                valueSetIds : Set<ValueSetId>)
        : this(UUID.randomUUID(),
               Prim(valueSetId),
               Prim(label),
               Prim(labelSingular),
               maybeLiftPrim(description),
               maybeLiftPrim(valueType),
               Prim(valueSetIds))


    companion object : Factory<ValueSetCompound>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ValueSetCompound> = when (doc)
        {
            is DocDict ->
            {
                effApply(::ValueSetCompound,
                         // Value Set Id
                         doc.at("value_set_id") ap { ValueSetId.fromDocument(it) },
                         // Label
                         doc.at("label") ap { ValueSetLabel.fromDocument(it) },
                         // Label Singular
                         doc.at("label_singular") ap { ValueSetLabelSingular.fromDocument(it) },
                         // Description
                         split(doc.maybeAt("description"),
                               effValue<ValueError,Maybe<ValueSetDescription>>(Nothing()),
                               { effApply(::Just, ValueSetDescription.fromDocument(it)) }),
                         // Value Type
                         split(doc.maybeAt("value_type"),
                               effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                               { effApply(::Just, EngineValueType.fromDocument(it)) }),
                         // Value Set Ids
                         doc.list("value_set_ids") ap { docList ->
                             docList.mapSet { ValueSetId.fromDocument(it) }
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun valueSetIds() : Set<ValueSetId> = this.valueSetIds.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    override fun value(valueId: ValueId, engine: Engine) : AppEff<Value>
    {
        // TODO write utility function for this for Effect pkg
        for (valueSetId in this.valueSetIds()) {
            val valueSet = engine.valueSet(valueSetId)
            when (valueSet) {
                is Val -> {
                    val value = valueSet.value.value(valueId, engine)
                    when (value) {
                        is Val -> return value
                    }
                }
            }
        }

        return effError(
                   AppEngineError(
                       ValueSetDoesNotContainValue(engine.gameId, this.valueSetId(), valueId)))
    }


    override fun numberValue(valueId : ValueId, engine : Engine) : AppEff<ValueNumber> =
        this.value(valueId, engine)
            .apply { maybeNumberValue(engine.gameId, valueId, it) }

    /**
     * A text value in one of the value sets in the compound value set that has the given id.
     */
    override fun textValue(valueId : ValueId, engine : Engine) : AppEff<ValueText> =
        this.value(valueId, engine)
            .apply { maybeTextValue(engine.gameId, valueId, it) }



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

