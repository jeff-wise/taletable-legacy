
package com.kispoko.tome.model.game.engine.value


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppEngineError
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue6
import com.kispoko.tome.lib.orm.schema.CollValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.engine.ValueSetDoesNotContainValue
import com.kispoko.tome.rts.entity.valueSet
import effect.*
import effect.Val
import maybe.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Value Set
 */
@Suppress("UNCHECKED_CAST")
sealed class ValueSet(open val valueSetId : ValueSetId,
                      open val label : ValueSetLabel,
                      open val labelSingular : ValueSetLabelSingular,
                      open val description : ValueSetDescription,
                      open val valueType : ValueType)
                       : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueSet>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ValueSet> = when (doc)
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

    fun valueSetId() : ValueSetId = this.valueSetId


    fun label() : ValueSetLabel = this.label


    fun labelString() : String = this.label.value


    fun labelSingular() : ValueSetLabelSingular = this.labelSingular


    fun description() : ValueSetDescription = this.description


    fun descriptionString() : String = this.description.value


    fun valueType() : ValueType = this.valueType


    // Lookup
    // -----------------------------------------------------------------------------------------

    abstract fun value(valueId : ValueId, entityId : EntityId) : AppEff<Value>


    abstract fun numberValue(valueId : ValueId, entityId : EntityId) : AppEff<ValueNumber>


    abstract fun textValue(valueId : ValueId, entityId : EntityId) : AppEff<ValueText>


    abstract fun values(entityId : EntityId) : AppEff<Set<Value>>

}


/**
 * Base Value Set
 */
data class ValueSetBase(override val id : UUID,
                        override val valueSetId : ValueSetId,
                        override val label : ValueSetLabel,
                        override val labelSingular : ValueSetLabelSingular,
                        override val description : ValueSetDescription,
                        override val valueType : ValueType,
                        val values : MutableList<Value>)
                         : ValueSet(valueSetId, label, labelSingular, description, valueType)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private val valuesById : MutableMap<ValueId,Value> =
                                        values.associateBy { it.valueId }
                                                as MutableMap<ValueId,Value>


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(valueSetId : ValueSetId,
                label : ValueSetLabel,
                labelSingular : ValueSetLabelSingular,
                description : ValueSetDescription,
                valueType : ValueType,
                values : List<Value>)
        : this(UUID.randomUUID(),
               valueSetId,
               label,
               labelSingular,
               description,
               valueType,
               values.toMutableList())


    companion object : Factory<ValueSetBase>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValueSetBase> = when (doc)
        {
            is DocDict ->
            {
                doc.at("value_set_id") ap { ValueSetId.fromDocument(it) } ap { valueSetId ->
                    apply(::ValueSetBase,
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
                                effValue<ValueError,ValueType>(ValueType.Any),
                                { ValueType.fromDocument(it) }),
                          // Values,
                          doc.list("values") ap { docList ->
                              docList.map { Value.fromDocument(it, valueSetId) }
                          })
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "value_set_id" to this.valueSetId().toDocument(),
        "label" to this.label().toDocument(),
        "label_singular" to this.labelSingular().toDocument(),
        "description" to this.description().toDocument(),
        "value_type" to this.valueType().toDocument(),
        "values" to DocList(this.values.map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_ValueSetBaseValue =
        RowValue6(valueSetBaseTable, PrimValue(this.valueSetId),
                                     PrimValue(this.label),
                                     PrimValue(this.labelSingular),
                                     PrimValue(this.description),
                                     PrimValue(this.valueType),
                                     CollValue(this.values))


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    override fun value(valueId : ValueId, entityId : EntityId) : AppEff<Value> =
            note(this.valuesById[valueId],
                 AppEngineError(ValueSetDoesNotContainValue(this.valueSetId(), valueId)))


    override fun numberValue(valueId : ValueId,
                             entityId : EntityId) : AppEff<ValueNumber> =
            this.value(valueId, entityId).apply { it.numberValue() }


    override fun textValue(valueId : ValueId, entityId : EntityId) : AppEff<ValueText> =
        this.value(valueId, entityId).apply { it.textValue() }


    override fun values(entityId : EntityId) : AppEff<Set<Value>> =
            effValue(this.values.toSet())


    fun sortedValues() : List<Value> =
        when (this.valueType())
        {
            is ValueType.Number -> numberValues().sortedBy { it.value() }
            is ValueType.Text   -> textValues().sortedBy { it.valueMinusThe() }
            is ValueType.Any    ->
            {
                numberValues().sortedBy { it.value() }
                    .plus(textValues().sortedBy { it.value() })
            }
        }


    fun numberValues() : List<ValueNumber> =
        this.values.mapNotNull {
            when (it) {
                is ValueNumber -> it
                else           -> null
            }
        }


    fun textValues() : List<ValueText> =
        this.values.mapNotNull {
            when (it) {
                is ValueText -> it
                else         -> null
            }
        }


    // -----------------------------------------------------------------------------------------
    // NEW VALUES
    // -----------------------------------------------------------------------------------------

    fun newDefaultTextValue() : ValueText
    {
        val indicesUsed : MutableSet<Int> = mutableSetOf()
        val newTextValueRegex = Regex("^New Value (\\d+)$")

        this.values.forEach {
            when (it)
            {
                is ValueText ->
                {
                    val textValue = it.value()
                    val indexString = newTextValueRegex.matchEntire(textValue)?.groupValues?.get(1)

                    if (indexString != null)
                        indicesUsed.add(indexString.toInt())
                }
            }
        }

        var i : Int = 1
        while (indicesUsed.contains(i))  { i += 1 }

        val defaultValueId = "new_value_" + i.toString()
        val defaultValue = "New Value " + i.toString()

        return ValueText(ValueId(defaultValueId),
                         ValueDescription(defaultValue),
                         Nothing(),
                         listOf(),
                         this.valueSetId(),
                         TextValue(defaultValue))
    }


    // -----------------------------------------------------------------------------------------
    // VALUES
    // -----------------------------------------------------------------------------------------

    fun addValue(value : Value)
    {
        this.values.add(value)
        this.valuesById.put(value.valueId(), value)
    }


    fun removeValue(valueId : ValueId) : Boolean
    {
        val newValues : MutableSet<Value> = mutableSetOf()

        this.values.forEach {
            if (it.valueId() != valueId)
                newValues.add(it)
        }

        val removed = newValues.size != this.values.size

        this.values.clear()

        newValues.forEach {
            this.values.add(it)
        }

        return removed
    }

}


/**
 * Compound Value Set
 */
data class ValueSetCompound(override val id : UUID,
                            override val valueSetId : ValueSetId,
                            override val label : ValueSetLabel,
                            override val labelSingular: ValueSetLabelSingular,
                            override val description: ValueSetDescription,
                            override val valueType : ValueType,
                            val valueSetIds : MutableList<ValueSetId>)
                            : ValueSet(valueSetId, label, labelSingular, description, valueType)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(valueSetId : ValueSetId,
                label : ValueSetLabel,
                labelSingular : ValueSetLabelSingular,
                description : ValueSetDescription,
                valueType : ValueType,
                valueSetIds : List<ValueSetId>)
        : this(UUID.randomUUID(),
               valueSetId,
               label,
               labelSingular,
               description,
               valueType,
               valueSetIds.toMutableList())


    companion object : Factory<ValueSetCompound>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValueSetCompound> = when (doc)
        {
            is DocDict ->
            {
                apply(::ValueSetCompound,
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
                            effValue<ValueError,ValueType>(ValueType.Any),
                            { ValueType.fromDocument(it) }),
                      // Value Set Ids
                      doc.list("value_set_ids") ap { docList ->
                          docList.map { ValueSetId.fromDocument(it) }
                      })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "value_set_id" to this.valueSetId().toDocument(),
        "label" to this.label().toDocument(),
        "label_singular" to this.labelSingular().toDocument(),
        "description" to this.description().toDocument(),
        "value_type" to this.valueType().toDocument(),
        "value_set_ids" to DocList(this.valueSetIds().map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun valueSetIds() : List<ValueSetId> = this.valueSetIds


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this



    override fun rowValue() : DB_ValueSetCompoundValue =
        RowValue6(valueSetCompoundTable,
                  PrimValue(this.valueSetId),
                  PrimValue(this.label),
                  PrimValue(this.labelSingular),
                  PrimValue(this.description),
                  PrimValue(this.valueType),
                  PrimValue(ValueSetIdSet(this.valueSetIds)))


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    override fun value(valueId : ValueId, entityId : EntityId) : AppEff<Value>
    {
        val valueSets = this.valueSetIds().toList().mapM { valueSet(it, entityId) }

        when (valueSets)
        {
            is Val ->
            {
                for (valueSet in valueSets.value) {
                    val value = valueSet.value(valueId, entityId)
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
                             entityId : EntityId) : AppEff<ValueNumber> =
        this.value(valueId, entityId).apply { it.numberValue() }


    override fun textValue(valueId : ValueId, entityId : EntityId) : AppEff<ValueText> =
        this.value(valueId, entityId).apply { it.textValue() }


    override fun values(entityId : EntityId) : AppEff<Set<Value>>
    {
        val valueSets = this.valueSetIds().toList().mapM { valueSet(it, entityId) }

        fun values(valueSets : List<ValueSet>) : AppEff<Set<Value>> =
                valueSets.mapM { it.values(entityId) }
                         .apply { effValue<AppError,Set<Value>>(it.flatten().toSet()) }

        return valueSets.apply { values(it) }
    }


    fun valueSets(entityId : EntityId) : AppEff<Set<ValueSet>> =
        this.valueSetIds().toList()
            .mapM  { valueSet(it, entityId) }
            .apply { effValue<AppError,Set<ValueSet>>(it.toSet()) }


}


/**
 * ValueSet Id Set
 */
data class ValueSetIdSet(val ids : List<ValueSetId>) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueSetIdSet>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ValueSetIdSet> = when (doc)
        {
            is DocList -> apply(::ValueSetIdSet, doc.map { ValueSetId.fromDocument(it) })
            else       -> effError(lulo.value.UnexpectedType(DocType.LIST, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ ids.joinToString(",") })

}


/**
 * ValueSet Id
 */
data class ValueSetId(val value : String) : ToDocument, SQLSerializable, Serializable
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
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * ValueSet Label
 */
data class ValueSetLabel(val value : String) : ToDocument, SQLSerializable, Serializable
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
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * ValueSet Label Singular
 */
data class ValueSetLabelSingular(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueSetLabelSingular>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ValueSetLabelSingular> = when (doc)
        {
            is DocText -> effValue(ValueSetLabelSingular(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * ValueSet Description
 */
data class ValueSetDescription(val value : String) : ToDocument, SQLSerializable, Serializable
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
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}




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
