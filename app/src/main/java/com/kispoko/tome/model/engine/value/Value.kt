
package com.kispoko.tome.model.engine.value


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppEngineError
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue4
import com.kispoko.tome.lib.orm.RowValue5
import com.kispoko.tome.lib.orm.schema.*
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.book.BookReference
import com.kispoko.tome.model.engine.reference.TextReference
import com.kispoko.tome.model.engine.reference.TextReferenceLiteral
import com.kispoko.tome.model.engine.variable.Variable
import com.kispoko.tome.model.engine.variable.VariableReference
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.engine.ValueIsOfUnexpectedType
import com.kispoko.tome.util.Util
import effect.*
import maybe.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*



/**
 * Value
 */
@Suppress("UNCHECKED_CAST")
sealed class Value(open val valueId : ValueId,
                   open val description : ValueDescription,
                   open val bookReference : Maybe<BookReference>,
                   open val variables : List<Variable>,
                   open val valueSetId : ValueSetId)
                    : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc: SchemaDoc, valueSetId: ValueSetId): ValueParser<Value> =
            when (doc)
            {
                is DocDict ->
                {
                    when (doc.case())
                    {
                        "value_number" -> ValueNumber.fromDocument(doc, valueSetId) as ValueParser<Value>
                        "value_text"   -> ValueText.fromDocument(doc, valueSetId) as ValueParser<Value>
                        else           -> effError<ValueError, Value>(
                                              UnknownCase(doc.case(), doc.path))
                    }
                }
                else -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // GETTER
    // -----------------------------------------------------------------------------------------

    fun valueId() : ValueId = this.valueId


    fun valueSetId() : ValueSetId = this.valueSetId


    fun description() : ValueDescription = this.description


    fun bookReference() : Maybe<BookReference> = this.bookReference


    fun variables() : List<Variable> = this.variables


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    abstract fun type() : ValueType


    abstract fun valueString() : String


    fun numberValue() : AppEff<ValueNumber> = when (this)
    {
        is ValueNumber -> effValue(this)
        else           -> effError(AppEngineError(
                                ValueIsOfUnexpectedType(this.valueSetId(),
                                                        this.valueId(),
                                                        ValueType.Number,
                                                        this.type())))
    }


    fun textValue() : AppEff<ValueText> = when (this)
    {
        is ValueText -> effValue(this)
        else         -> effError(AppEngineError(
                                ValueIsOfUnexpectedType(this.valueSetId(),
                                                        this.valueId(),
                                                        ValueType.Text,
                                                        this.type())))
    }


    fun valueReference() =
        ValueReference(TextReferenceLiteral(this.valueSetId.value),
                       TextReferenceLiteral(this.valueId.value))



    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

}


/**
 * Number Value
 */
data class ValueNumber(override val id : UUID,
                       override val valueId : ValueId,
                       override val description : ValueDescription,
                       override val bookReference: Maybe<BookReference>,
                       override val variables : List<Variable>,
                       override val valueSetId : ValueSetId,
                       val value : NumberValue)
                        : Value(valueId, description, bookReference, variables, valueSetId)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(valueId : ValueId,
                description : ValueDescription,
                bookReference : Maybe<BookReference>,
                variables : List<Variable>,
                valueSetId : ValueSetId,
                value : NumberValue)
        : this(UUID.randomUUID(),
               valueId,
               description,
               bookReference,
               variables,
               valueSetId,
               value)


    companion object
    {
        fun fromDocument(doc : SchemaDoc,
                         valueSetId : ValueSetId) : ValueParser<ValueNumber> = when (doc)
        {
            is DocDict ->
            {
                apply(::ValueNumber,
                      // Value Id
                      doc.at("value_id") ap { ValueId.fromDocument(it) },
                      // Description
                      doc.at("description") ap { ValueDescription.fromDocument(it) },
                      // Rulebook Reference
                      split(doc.maybeAt("book_reference"),
                            effValue<ValueError,Maybe<BookReference>>(Nothing()),
                            { apply(::Just, BookReference.fromDocument(it)) }),
                      // Variables
                      doc.list("variables") ap { docList ->
                          docList.map { Variable.fromDocument(it) }
                      },
                      // Value Set Id
                      effValue(valueSetId),
                      // Value
                      doc.at("value") ap { NumberValue.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "value_id" to this.valueId().toDocument(),
        "variables" to DocList(this.variables().map { it.toDocument() }),
        "value" to this.value.toDocument(),
        "description" to this.description.toDocument()
    ))
    .maybeMerge(this.bookReference().apply {
        Just(Pair("book_reference", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun value() : Double = this.value.value


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() : ValueType = ValueType.Number


    override fun valueString() : String = Util.doubleString(this.value())


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_ValueNumberValue =
        RowValue4(valueNumberTable,
                  PrimValue(this.valueId),
                  PrimValue(this.description),
                  CollValue(this.variables),
                  PrimValue(this.value))


    // -----------------------------------------------------------------------------------------
    // EQUALS
    // -----------------------------------------------------------------------------------------

    override fun equals(other : Any?) : Boolean =
        if (other is ValueNumber)
            other.value() == this.value()
        else
            false

}


/**
 * Text Value
 */
data class ValueText(override val id : UUID,
                     override val valueId : ValueId,
                     override val description : ValueDescription,
                     override val bookReference: Maybe<BookReference>,
                     override val variables : MutableList<Variable>,
                     override val valueSetId : ValueSetId,
                     val value : TextValue)
                      : Value(valueId, description, bookReference, variables, valueSetId)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(valueId : ValueId,
                description : ValueDescription,
                bookReference : Maybe<BookReference>,
                variables : List<Variable>,
                valueSetId : ValueSetId,
                value : TextValue)
        : this(UUID.randomUUID(),
               valueId,
               description,
               bookReference,
               variables.toMutableList(),
               valueSetId,
               value)


    companion object
    {
        fun fromDocument(doc : SchemaDoc,
                         valueSetId : ValueSetId) : ValueParser<ValueText> = when (doc)
        {
            is DocDict ->
            {
                apply(::ValueText,
                      // Value Id
                      doc.at("value_id") ap { ValueId.fromDocument(it) },
                      // Description
                      doc.at("description") ap { ValueDescription.fromDocument(it) },
                      // Book Reference
                      split(doc.maybeAt("book_reference"),
                            effValue<ValueError,Maybe<BookReference>>(Nothing()),
                            { effApply(::Just, BookReference.fromDocument(it)) }),
                      // Variables
                      split(doc.maybeList("variables"),
                            effValue(listOf()),
                            { it.map{ Variable.fromDocument(it) } }),
                      // Value Set Id
                      effValue(valueSetId),
                      // Value
                      doc.at("value") ap { TextValue.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "value_id" to this.valueId().toDocument(),
        "variables" to DocList(this.variables().map { it.toDocument() }),
        "value" to this.value.toDocument(),
        "description" to this.description.toDocument()
    ))
    .maybeMerge(this.bookReference().apply {
        Just(Pair("book_reference", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value() : String = this.value.value


    fun valueMinusThe() : String
    {
        val valueString = this.value()
        return if (valueString.startsWith("the ", true))
            valueString.drop(4)
        else
            valueString
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() : ValueType = ValueType.Text

    override fun valueString() : String = this.value()


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_ValueTextValue =
        RowValue4(valueTextTable,
                  PrimValue(this.valueId),
                  PrimValue(this.description),
                  CollValue(this.variables),
                  PrimValue(this.value))


    // -----------------------------------------------------------------------------------------
    // EQUALS
    // -----------------------------------------------------------------------------------------

    override fun equals(other : Any?) : Boolean =
        if (other is ValueText)
            other.value() == this.value()
        else
            false


    override fun hashCode(): Int {
        return super.hashCode()
    }
}


sealed class ValueType : ToDocument, SQLSerializable, Serializable
{

    object Number : ValueType()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"number"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("number")

    }


    object Text : ValueType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"text"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("text")

    }


    object Any : ValueType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"any"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("any")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<ValueType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "number" -> effValue<ValueError,ValueType>(ValueType.Number)
                "text"   -> effValue<ValueError,ValueType>(ValueType.Text)
                "any"    -> effValue<ValueError,ValueType>(ValueType.Any)
                else     -> effError<ValueError,ValueType>(
                                    UnexpectedValue("ValueType", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


}



/**
 * Value Reference
 */
data class ValueReference(val valueSetId : TextReference, val valueId : TextReference)
            : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueReference>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ValueReference> = when (doc)
        {
            is DocDict -> apply(::ValueReference,
                                // Value Set Name
                                doc.at("value_set_id") ap { TextReference.fromDocument(it) },
                                // Value Name
                                doc.at("value_id") ap { TextReference.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    constructor(valueSetId : ValueSetId, valueId : ValueId)
            : this(TextReferenceLiteral(valueSetId.value), TextReferenceLiteral(valueId.value))


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "value_set_id" to this.valueSetId.toDocument(),
        "value_id" to this.valueId.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    fun dependencies(entityId : EntityId) : Set<VariableReference>
    {
        val references : MutableSet<VariableReference> = mutableSetOf()

        references.addAll(this.valueSetId.dependencies(entityId))

        references.addAll(this.valueId.dependencies(entityId))

        return references
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue =
            SQLText({this.valueSetId.toString() + " " + this.valueId.toString()})

}


/**
 * Value Id
 */
data class ValueId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValueId> = when (doc)
        {
            is DocText -> effValue(ValueId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
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
 * Value Description
 */
data class ValueDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueDescription>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValueDescription> = when (doc)
        {
            is DocText -> effValue(ValueDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
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
 * Number Value
 */
data class NumberValue(val value : Double) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberValue> = when (doc)
        {
            is DocNumber -> effValue(NumberValue(doc.number))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value})

}


/**
 * Text Value
 */
data class TextValue(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextValue> = when (doc)
        {
            is DocText -> effValue(TextValue(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
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
 * Value Id Set
 */
data class ValueIdSet(val valueIds : List<ValueId>) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueIdSet>
    {

        override fun fromDocument(doc : SchemaDoc) : ValueParser<ValueIdSet> = when (doc)
        {
            is DocList -> apply(::ValueIdSet, doc.map { ValueId.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.LIST, docType(doc), doc.path))
        }


        fun empty() : ValueIdSet = ValueIdSet(listOf())
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}



//
// Number Value
//
//    // > Variables
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The text value's variables.
//     * @return The list of variables.
//     */
//    public List<VariableUnion> variables()
//    {
//        return this.variables.getValue();
//    }
//
//
//    public void addToState()
//    {
//        for (VariableUnion variableUnion : this.variables()) {
//            State.addVariable(variableUnion);
//        }
//    }
//
//
//    public void removeFromState()
//    {
//        for (VariableUnion variableUnion : this.variables()) {
//            State.removeVariable(variableUnion.variable().name());
//        }
//    }

