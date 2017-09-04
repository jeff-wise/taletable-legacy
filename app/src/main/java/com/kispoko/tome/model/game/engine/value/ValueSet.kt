
package com.kispoko.tome.model.game.engine.value


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppEngineError
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.Engine
import com.kispoko.tome.model.game.engine.EngineValueType
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.game.engine.ValueSetDoesNotContainValue
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetUIContext
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*
import kotlin.collections.HashSet



/**
 * Value Set
 */
@Suppress("UNCHECKED_CAST")
sealed class ValueSet(open val valueSetId : Prim<ValueSetId>,
                      open val label : Prim<ValueSetLabel>,
                      open val labelSingular: Prim<ValueSetLabelSingular>,
                      open val description : Prim<ValueSetDescription>,
                      open val valueType : Maybe<Prim<EngineValueType>>)
                       : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueSet>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValueSet> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "value_set_base"     -> ValueSetBase.fromDocument(doc)
                                                as ValueParser<ValueSet>
                    "value_set_compound" -> ValueSetCompound.fromDocument(doc)
                                                as ValueParser<ValueSet>
                    else                 -> effError<ValueError,ValueSet>(
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

    fun label() : String = this.label.value.value

    fun labelSingular() : String = this.labelSingular.value.value

    fun description() : String = this.description.value.value

    fun valueType() : EngineValueType? = getMaybePrim(this.valueType)


    // Lookup
    // -----------------------------------------------------------------------------------------

    abstract fun value(valueId : ValueId, sheetContext : SheetContext) : AppEff<Value>

    abstract fun numberValue(valueId : ValueId, sheetContext : SheetContext) : AppEff<ValueNumber>

    abstract fun textValue(valueId : ValueId, sheetContext: SheetContext) : AppEff<ValueText>

    abstract fun values(gameId : GameId) : AppEff<Set<Value>>

}


/**
 * Base Value Set
 */
data class ValueSetBase(override val id : UUID,
                        override val valueSetId : Prim<ValueSetId>,
                        override val label : Prim<ValueSetLabel>,
                        override val labelSingular: Prim<ValueSetLabelSingular>,
                        override val description: Prim<ValueSetDescription>,
                        override val valueType : Maybe<Prim<EngineValueType>>,
                        val values : Conj<Value>)
                         : ValueSet(valueSetId, label, labelSingular, description, valueType)
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.valueSetId.name                        = "value_set_id"
        this.label.name                             = "label"
        this.labelSingular.name                     = "label_singular"
        this.description.name                       = "description"

        when (this.valueType) {
            is Just -> this.valueType.value.name    = "value_type"
        }

        this.values.name                            = "values"
    }


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
                description : ValueSetDescription,
                valueType : Maybe<EngineValueType>,
                values : MutableSet<Value>)
        : this(UUID.randomUUID(),
               Prim(valueSetId),
               Prim(label),
               Prim(labelSingular),
               Prim(description),
               maybeLiftPrim(valueType),
               Conj(values))


    companion object : Factory<ValueSetBase>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValueSetBase> = when (doc)
        {
            is DocDict ->
            {
                doc.at("value_set_id") ap { ValueSetId.fromDocument(it) } ap { valueSetId ->
                    effApply(::ValueSetBase,
                             // Value Set Id
                             effValue(valueSetId),
                             // Label
                             doc.at("label") ap { ValueSetLabel.fromDocument(it) },
                             // Label Singular
                             doc.at("label_singular") ap { ValueSetLabelSingular.fromDocument(it) },
                             // Description
                             doc.at("description") ap { ValueSetDescription.fromDocument(it) },
                             // Value Type
                             split(doc.maybeAt("value_type"),
                                   effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                                   { effApply(::Just, EngineValueType.fromDocument(it)) }),
                             // Values,
                             doc.list("values") ap { docList ->
                                 docList.mapSetMut { Value.fromDocument(it, valueSetId) }
                             })
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "value_set_base"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    override fun value(valueId : ValueId, sheetContext : SheetContext) : AppEff<Value> =
            note(this.valuesById[valueId],
                 AppEngineError(ValueSetDoesNotContainValue(this.valueSetId(), valueId)))


    override fun numberValue(valueId : ValueId,
                             sheetContext : SheetContext) : AppEff<ValueNumber> =
            this.value(valueId, sheetContext).apply { it.numberValue() }


    override fun textValue(valueId : ValueId, sheetContext : SheetContext) : AppEff<ValueText> =
        this.value(valueId, sheetContext).apply { it.textValue() }


    override fun values(gameId : GameId) : AppEff<Set<Value>> =
            effValue(this.values.set.toSet())


    fun sortedValues() : List<Value> =
        when (this.valueType())
        {
            is EngineValueType.Number -> numberValues().sortedBy { it.value() }
            is EngineValueType.Text -> textValues().sortedBy { it.valueMinusThe() }
            else                      ->
            {
                numberValues().sortedBy { it.value() }
                    .plus(textValues().sortedBy { it.value() })
            }
        }


    fun numberValues() : List<ValueNumber> =
        this.values.set.mapNotNull {
            when (it) {
                is ValueNumber -> it
                else           -> null
            }
        }


    fun textValues() : List<ValueText> =
        this.values.set.mapNotNull {
            when (it) {
                is ValueText -> it
                else         -> null
            }
        }

}


/**
 * Compound Value Set
 */
data class ValueSetCompound(override val id : UUID,
                            override val valueSetId : Prim<ValueSetId>,
                            override val label : Prim<ValueSetLabel>,
                            override val labelSingular: Prim<ValueSetLabelSingular>,
                            override val description: Prim<ValueSetDescription>,
                            override val valueType : Maybe<Prim<EngineValueType>>,
                            val valueSetIds : Prim<ValueSetIdSet>)
                            : ValueSet(valueSetId, label, labelSingular, description, valueType)
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.valueSetId.name                        = "value_set_id"
        this.label.name                             = "label"
        this.labelSingular.name                     = "label_singular"
        this.description.name                       = "description"

        when (this.valueType) {
            is Just -> this.valueType.value.name    = "value_type"
        }

        this.valueSetIds.name                        = "value_set_ids"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(valueSetId : ValueSetId,
                label : ValueSetLabel,
                labelSingular : ValueSetLabelSingular,
                description : ValueSetDescription,
                valueType : Maybe<EngineValueType>,
                valueSetIds : Set<ValueSetId>)
        : this(UUID.randomUUID(),
               Prim(valueSetId),
               Prim(label),
               Prim(labelSingular),
               Prim(description),
               maybeLiftPrim(valueType),
               Prim(ValueSetIdSet(valueSetIds.toHashSet())))


    companion object : Factory<ValueSetCompound>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValueSetCompound> = when (doc)
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
                         doc.at("description") ap { ValueSetDescription.fromDocument(it) },
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

    fun valueSetIds() : Set<ValueSetId> = this.valueSetIds.value.idSet()


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "value_set_compound"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    override fun value(valueId : ValueId, sheetContext : SheetContext) : AppEff<Value>
    {
        val valueSets = GameManager.engine(sheetContext.gameId) ap { engine ->
                            this.valueSetIds().toList().mapM { engine.valueSet(it) }
                        }

        when (valueSets)
        {
            is Val ->
            {
                for (valueSet in valueSets.value) {
                    val value = valueSet.value(valueId, sheetContext)
                    when (value) {
                        is Val -> return value
                    }
                }
            }
            is Err -> ApplicationLog.error(valueSets.error)
        }

        return effError(AppEngineError(ValueSetDoesNotContainValue(this.valueSetId(), valueId)))
    }


    override fun numberValue(valueId : ValueId,
                             sheetContext : SheetContext) : AppEff<ValueNumber> =
        this.value(valueId, sheetContext).apply { it.numberValue() }


    override fun textValue(valueId : ValueId, sheetContext : SheetContext) : AppEff<ValueText> =
        this.value(valueId, sheetContext).apply { it.textValue() }


    override fun values(gameId : GameId) : AppEff<Set<Value>>
    {
        fun valueSets(engine : Engine) : AppEff<List<ValueSet>> =
            this.valueSetIds().toList().mapM { engine.valueSet(it) }

        fun values(valueSets : List<ValueSet>) : AppEff<Set<Value>> =
                valueSets.mapM { it.values(gameId) }
                         .apply { effValue<AppError,Set<Value>>(it.flatten().toSet()) }

        return GameManager.engine(gameId)
                          .apply(::valueSets)
                          .apply(::values)
    }


    fun valueSets(sheetUIContext: SheetUIContext) : AppEff<Set<ValueSet>>
    {
        fun valueSets(engine : Engine) : AppEff<Set<ValueSet>> =
            this.valueSetIds().toList()
                .mapM  { engine.valueSet(it) }
                .apply { effValue<AppError,Set<ValueSet>>(it.toSet()) }

        return GameManager.engine(sheetUIContext.gameId)
                          .apply(::valueSets)
    }


}


/**
 * ValueSet Id Set
 */
data class ValueSetIdSet(val idSet : HashSet<ValueSetId>) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueSetIdSet>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValueSetIdSet> = when (doc)
        {
            is DocList -> effApply(::ValueSetIdSet, doc.mapHashSet { ValueSetId.fromDocument(it) })
            else       -> effError(lulo.value.UnexpectedType(DocType.LIST, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun idSet() : Set<ValueSetId> = this.idSet


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({SerializationUtils.serialize(idSet)})

}


/**
 * ValueSet Id
 */
data class ValueSetId(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueSetId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValueSetId> = when (doc)
        {
            is DocText -> effValue(ValueSetId(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * ValueSet Label
 */
data class ValueSetLabel(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueSetLabel>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValueSetLabel> = when (doc)
        {
            is DocText -> effValue(ValueSetLabel(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * ValueSet Label Singular
 */
data class ValueSetLabelSingular(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueSetLabelSingular>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValueSetLabelSingular> = when (doc)
        {
            is DocText -> effValue(ValueSetLabelSingular(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * ValueSet Description
 */
data class ValueSetDescription(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueSetDescription>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValueSetDescription> = when (doc)
        {
            is DocText -> effValue(ValueSetDescription(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

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

