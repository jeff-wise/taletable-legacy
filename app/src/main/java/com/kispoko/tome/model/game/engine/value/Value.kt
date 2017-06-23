
package com.kispoko.tome.model.game.engine.value


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLReal
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.variable.Variable
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.util.*



/**
 * Value
 */
@Suppress("UNCHECKED_CAST")
sealed class Value(open val valueId : Prim<ValueId>,
                   open val description : Maybe<Prim<ValueDescription>>) : Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Value>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Value> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "number" -> ValueNumber.fromDocument(doc)
                                    as ValueParser<Value>
                    "text"   -> ValueText.fromDocument(doc)
                                    as ValueParser<Value>
                    else     -> effError<ValueError,Value>(
                                            UnknownCase(doc.case(), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTER
    // -----------------------------------------------------------------------------------------

    fun valueId() : ValueId = this.valueId.value

    fun description() : ValueDescription? = getMaybePrim(this.description)


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    abstract fun type() : ValueType


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
                       val value : Prim<NumberValue>,
                       val variables : Conj<Variable>)
                        : Value(valueId, description)
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
                value : NumberValue,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(valueId),
               maybeLiftPrim(description),
               Prim(value),
               Conj(variables))


    companion object : Factory<ValueNumber>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ValueNumber> = when (doc)
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
                         // Value
                         doc.at("value") ap { NumberValue.fromDocument(it) },
                         // Variables
                         doc.list("variables") ap { docList ->
                             docList.mapSetMut { Variable.fromDocument(it) }
                         })
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


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "value_text"

    override val modelObject = this

}


/**
 * Text Value
 */
data class ValueText(override val id : UUID,
                     override val valueId : Prim<ValueId>,
                     override val description : Maybe<Prim<ValueDescription>>,
                     val value : Prim<TextValue>,
                     val variables : Conj<Variable>)
                      : Value(valueId, description)
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
                value : TextValue,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(valueId),
               maybeLiftPrim(description),
               Prim(value),
               Conj(variables))


    companion object : Factory<ValueText>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ValueText> = when (doc)
        {
            is DocDict -> effApply(::ValueText,
                                   // Value Id
                                   doc.at("value_id") ap { ValueId.fromDocument(it) },
                                   // Description
                                   split(doc.maybeAt("description"),
                                         effValue<ValueError,Maybe<ValueDescription>>(Nothing()),
                                         { effApply(::Just, ValueDescription.fromDocument(it)) }),
                                   // Value
                                   doc.at("value") ap { TextValue.fromDocument(it) },
                                   // Variables
                                   doc.list("variables") ap { docList ->
                                       docList.mapSetMut { Variable.fromDocument(it) }
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value() : String = this.value.value.value


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() : ValueType = ValueType.TEXT


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "value_text"

    override val modelObject = this

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
            : SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueReference>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ValueReference> = when (doc)
        {
            is DocDict -> effApply(::ValueReference,
                                   // ValueSet Name
                                   doc.at("value_set_name") ap { ValueSetId.fromDocument(it) },
                                   // Value Name
                                   doc.at("value_name") ap { ValueId.fromDocument(it) })
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
data class ValueId(val value : String) : SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueId>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ValueId> = when (doc)
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
data class ValueDescription(val value : String) : SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValueDescription>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ValueDescription> = when (doc)
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
data class NumberValue(val value : Double) : SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberValue>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberValue> = when (doc)
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
data class TextValue(val value : String) : SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextValue>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TextValue> = when (doc)
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

