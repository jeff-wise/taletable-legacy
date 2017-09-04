
package com.kispoko.tome.model.game.engine.value


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppEngineError
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLReal
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.RulebookReference
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.rts.game.engine.ValueIsOfUnexpectedType
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable
import java.util.*



/**
 * Value
 */
@Suppress("UNCHECKED_CAST")
sealed class Value(open val valueId : Prim<ValueId>,
                   open val description : Maybe<Prim<ValueDescription>>,
                   open val rulebookReference : Maybe<Comp<RulebookReference>>,
                   open val variables : Conj<Variable>,
                   open val valueSetId : ValueSetId)
                    : Model, Serializable
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

    fun valueId() : ValueId = this.valueId.value

    fun valueSetId() : ValueSetId = this.valueSetId

    fun description() : String? = getMaybePrim(this.description)?.value

    fun rulebookReference() : Maybe<RulebookReference> = getMaybeComp(this.rulebookReference)

    fun variables() : Set<Variable> = this.variables.set


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    abstract fun type() : ValueType

    abstract fun valueString() : String

    fun isNumber() = this.type() == ValueType.NUMBER

    fun isText() = this.type() == ValueType.TEXT


    fun numberValue() : AppEff<ValueNumber> = when (this)
    {
        is ValueNumber -> effValue(this)
        else           -> effError(AppEngineError(
                                ValueIsOfUnexpectedType(this.valueSetId(),
                                                        this.valueId(),
                                                        ValueType.NUMBER,
                                                        this.type())))
    }


    fun textValue() : AppEff<ValueText> = when (this)
    {
        is ValueText -> effValue(this)
        else         -> effError(AppEngineError(
                                ValueIsOfUnexpectedType(this.valueSetId(),
                                                        this.valueId(),
                                                        ValueType.TEXT,
                                                        this.type())))
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

}


/**
 * Number Value
 */
data class ValueNumber(override val id : UUID,
                       override val valueId : Prim<ValueId>,
                       override val description: Maybe<Prim<ValueDescription>>,
                       override val rulebookReference : Maybe<Comp<RulebookReference>>,
                       override val variables : Conj<Variable>,
                       override val valueSetId : ValueSetId,
                       val value : Prim<NumberValue>)
                        : Value(valueId, description, rulebookReference, variables, valueSetId)
{

    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    init
    {
        this.valueId.name                           = "value_id"

        when (this.description) {
            is Just -> this.description.value.name  = "description"
        }

        this.value.name                             = "value"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(valueId : ValueId,
                description : Maybe<ValueDescription>,
                rulebookReference : Maybe<RulebookReference>,
                variables : MutableSet<Variable>,
                valueSetId : ValueSetId,
                value : NumberValue)
        : this(UUID.randomUUID(),
               Prim(valueId),
               maybeLiftPrim(description),
               maybeLiftComp(rulebookReference),
               Conj(variables),
               valueSetId,
               Prim(value))


    companion object
    {
        fun fromDocument(doc : SchemaDoc,
                         valueSetId : ValueSetId) : ValueParser<ValueNumber> = when (doc)
        {
            is DocDict ->
            {
                effApply(::ValueNumber,
                         // Value Id
                         doc.at("value_id") ap { ValueId.fromDocument(it) },
                         // Description
                         split(doc.maybeAt("description"),
                               effValue<ValueError,Maybe<ValueDescription>>(Nothing()),
                               { effApply(::Just, ValueDescription.fromDocument(it)) }),
                         // Rulebook Reference
                         split(doc.maybeAt("rulebook_reference"),
                               effValue<ValueError,Maybe<RulebookReference>>(Nothing()),
                               { effApply(::Just, RulebookReference.fromDocument(it)) }),
                         // Variables
                         doc.list("variables") ap { docList ->
                             docList.mapSetMut { Variable.fromDocument(it) }
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
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun value() : Double = this.value.value.value


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() : ValueType = ValueType.NUMBER

    override fun valueString() : String = Util.doubleString(this.value())


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "value_text"

    override val modelObject = this


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
                     override val valueId : Prim<ValueId>,
                     override val description : Maybe<Prim<ValueDescription>>,
                     override val rulebookReference : Maybe<Comp<RulebookReference>>,
                     override val variables : Conj<Variable>,
                     override val valueSetId : ValueSetId,
                     val value : Prim<TextValue>)
                      : Value(valueId, description, rulebookReference, variables, valueSetId)
{

    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    init
    {
        this.valueId.name                           = "value_id"

        when (this.description) {
            is Just -> this.description.value.name  = "description"
        }

        this.value.name                             = "value"

        this.variables.name                         = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(valueId : ValueId,
                description : Maybe<ValueDescription>,
                rulebookReference : Maybe<RulebookReference>,
                variables : MutableSet<Variable>,
                valueSetId : ValueSetId,
                value : TextValue)
        : this(UUID.randomUUID(),
               Prim(valueId),
               maybeLiftPrim(description),
               maybeLiftComp(rulebookReference),
               Conj(variables),
               valueSetId,
               Prim(value))


    companion object
    {
        fun fromDocument(doc : SchemaDoc,
                         valueSetId : ValueSetId) : ValueParser<ValueText> = when (doc)
        {
            is DocDict -> effApply(::ValueText,
                                   // Value Id
                                   doc.at("value_id") ap { ValueId.fromDocument(it) },
                                   // Description
                                   split(doc.maybeAt("description"),
                                         effValue<ValueError,Maybe<ValueDescription>>(Nothing()),
                                         { effApply(::Just, ValueDescription.fromDocument(it)) }),
                                   // Rulebook Reference
                                   split(doc.maybeAt("rulebook_reference"),
                                         effValue<ValueError,Maybe<RulebookReference>>(Nothing()),
                                         { effApply(::Just, RulebookReference.fromDocument(it)) }),
                                   // Variables
                                   split(doc.maybeList("variables"),
                                         effValue(mutableSetOf()),
                                         { it.mapSetMut { Variable.fromDocument(it) } }),
                                   // Value Set Id
                                   effValue(valueSetId),
                                   // Value
                                   doc.at("value") ap { TextValue.fromDocument(it) }
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value() : String = this.value.value.value

    fun valueMinusThe() : String
    {
        val valueString = this.value()

        if (valueString.startsWith("the ", true))
            return valueString.drop(4)
        else
            return valueString
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() : ValueType = ValueType.TEXT

    override fun valueString() : String = this.value()


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "value_text"

    override val modelObject = this


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


enum class ValueType
{
    NUMBER,
    TEXT
}


/**
 * Value Reference
 */
data class ValueReference(val valueSetId : ValueSetId, val valueId : ValueId)
            : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValueReference> = when (doc)
        {
            is DocDict -> effApply(::ValueReference,
                                   // Value Set Name
                                   doc.at("value_set_id") ap { ValueSetId.fromDocument(it) },
                                   // Value Name
                                   doc.at("value_id") ap { ValueId.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
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
data class ValueId(val value : String) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Value Description
 */
data class ValueDescription(val value : String) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Number Value
 */
data class NumberValue(val value : Double) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value})

}


/**
 * Text Value
 */
data class TextValue(val value : String) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

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

