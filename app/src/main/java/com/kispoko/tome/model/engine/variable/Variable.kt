
package com.kispoko.tome.model.engine.variable


import android.util.Log
import com.kispoko.tome.app.*
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue4
import com.kispoko.tome.lib.orm.RowValue5
import com.kispoko.tome.lib.orm.RowValue6
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.SumValue
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.book.BookReference
import com.kispoko.tome.model.engine.*
import com.kispoko.tome.model.engine.constraint.*
import com.kispoko.tome.model.engine.dice.DiceRoll
import com.kispoko.tome.model.engine.reference.TextReferenceLiteral
import com.kispoko.tome.model.engine.value.Value
import com.kispoko.tome.model.engine.value.ValueId
import com.kispoko.tome.model.engine.value.ValueReference
import com.kispoko.tome.model.engine.value.ValueSetId
import com.kispoko.tome.rts.entity.*
import com.kispoko.tome.rts.entity.sheet.*
import com.kispoko.tome.util.Util
import effect.*
import maybe.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import maybe.Nothing
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*


//typealias VariableOnUpdateListener = () -> Unit

/**
 * Variable
 */
@Suppress("UNCHECKED_CAST")
sealed class Variable : ProdType, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


//    private var onUpdateListeners : MutableList<VariableOnUpdateListener> = mutableListOf()


    private val relationToVariableId : MutableMap<VariableRelation,VariableId> = mutableMapOf()
    private var relatedParentIds : MutableSet<VariableId> = mutableSetOf()


    var lastUpdateId : UUID? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Variable>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Variable> =
            when (doc.case())
            {
                "variable_boolean"   -> BooleanVariable.fromDocument(doc) as ValueParser<Variable>
                "variable_dice_roll" -> DiceRollVariable.fromDocument(doc) as ValueParser<Variable>
                "variable_number"    -> NumberVariable.fromDocument(doc) as ValueParser<Variable>
                "variable_text"      -> TextVariable.fromDocument(doc) as ValueParser<Variable>
                "variable_text_list" -> TextListVariable.fromDocument(doc) as ValueParser<Variable>
                else                 -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // STATE
    // -----------------------------------------------------------------------------------------


    abstract fun variableId() : VariableId


    abstract fun setVariableId(variableId : VariableId)


    abstract fun label() : VariableLabel


    abstract fun description() : VariableDescription


    abstract fun tags() : List<VariableTag>


    abstract fun addTags(tags : Set<VariableTag>)


    fun addRelatedParent(variableId : VariableId)
    {
        this.relatedParentIds.add(variableId)
    }


    fun relatedParents() : Set<VariableId> = this.relatedParentIds


    fun relatedVariableId(relation : VariableRelation) : Maybe<VariableId>  =
        maybe(this.relationToVariableId[relation])


    fun setRelation(relation : VariableRelation,
                    variableId : VariableId,
                    entityId : EntityId)
    {
        this.relationToVariableId.put(relation, variableId)

        variable(variableId, entityId) apDo {
            it.addRelatedParent(this.variableId())
        }

        ApplicationLog.event(AppStateEvent(VariableRelationAdded(variableId, relation)))
    }


    fun hasRelation(relation : VariableRelation) : Boolean =
            this.relationToVariableId.containsKey(relation)


    abstract fun relation() : Maybe<VariableRelation>


    abstract fun bookReference(entityId : EntityId) : Maybe<BookReference>


    fun booleanVariable(entityId : EntityId) : AppEff<BooleanVariable> = when (this)
    {
        is BooleanVariable -> effValue(this)
        else               -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(entityId,
                                                               this.variableId(),
                                                               VariableType.BOOLEAN,
                                                               this.type())))
    }


    fun diceRollVariable(entityId : EntityId) : AppEff<DiceRollVariable> = when (this)
    {
        is DiceRollVariable -> effValue(this)
        else                -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(entityId,
                                                               this.variableId(),
                                                               VariableType.DICE_ROLL,
                                                               this.type())))
    }


    fun numberVariable(entityId : EntityId) : AppEff<NumberVariable> = when (this)
    {
        is NumberVariable -> effValue(this)
        else              -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(entityId,
                                                               this.variableId(),
                                                               VariableType.NUMBER,
                                                               this.type())))
    }


    fun textVariable(entityId : EntityId) : AppEff<TextVariable> = when (this)
    {
        is TextVariable -> effValue(this)
        else            -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(entityId,
                                                               this.variableId(),
                                                               VariableType.TEXT,
                                                               this.type())))
    }


    fun textListVariable(entityId : EntityId) : AppEff<TextListVariable> = when (this)
    {
        is TextListVariable -> effValue(this)
        else                -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(entityId,
                                                               this.variableId(),
                                                               VariableType.TEXT_LIST,
                                                               this.type())))
    }


    // -----------------------------------------------------------------------------------------
    // HISTORY
    // -----------------------------------------------------------------------------------------

    open fun historyVariableId() = VariableId(this.variableId().name.value + "__history__")


    open fun historyVariableLabel() = VariableLabel(this.label().value + " History")


    open fun historyVariableDescription() = VariableDescription(this.description().value + " history")


    abstract fun historyVariable() : Variable


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    abstract fun dependencies(entityId : EntityId) : Set<VariableReference>


    abstract fun type() : VariableType


    abstract fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>>


    abstract fun onAddToState(entityId : EntityId, parentVariable : Variable? = null)


    abstract fun engineValue(entityId : EntityId) : AppEff<EngineValue>


    // -----------------------------------------------------------------------------------------
    // VALUE STRING
    // -----------------------------------------------------------------------------------------

    abstract fun valueString(entityId : EntityId) : AppEff<String>

}


/**
 * Boolean Variable
 */
data class BooleanVariable(override val id : UUID,
                           private var variableId : VariableId,
                           private var label : VariableLabel,
                           private var description : VariableDescription,
                           private val tags : MutableList<VariableTag>,
                           private val relation : Maybe<VariableRelation>,
                           private var variableValue : BooleanVariableValue)
                            : Variable()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : List<VariableTag>,
                relation : Maybe<VariableRelation>,
                value : BooleanVariableValue)
        : this(UUID.randomUUID(),
               variableId,
               label,
               description,
               tags.toMutableList(),
               relation,
               value)


    companion object : Factory<BooleanVariable>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<BooleanVariable> = when (doc)
        {
            is DocDict ->
            {
                apply(::BooleanVariable,
                      // Variable Id
                      doc.at("id") ap { VariableId.fromDocument(it) },
                      // Label
                      doc.at("label") ap { VariableLabel.fromDocument(it) },
                      // Description
                      doc.at("description") ap { VariableDescription.fromDocument(it) },
                      // Tags
                      split(doc.maybeList("tags"),
                            effValue(listOf()),
                            { it.map { VariableTag.fromDocument(it) } }),
                      // Variable Relation
                      split(doc.maybeAt("relation"),
                            effValue<ValueError,Maybe<VariableRelation>>(Nothing()),
                            { apply(::Just, VariableRelation.fromDocument(it)) }),
                      // Value
                      doc.at("value") ap { BooleanVariableValue.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.variableId.toDocument(),
        "label" to this.label.toDocument(),
        "description" to this.description.toDocument(),
        "tags" to DocList(this.tags.map { it.toDocument() }),
        "value" to this.variableValue().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    override fun variableId() : VariableId = this.variableId


    override fun setVariableId(variableId : VariableId) {
        this.variableId = variableId
    }


    override fun description() : VariableDescription = this.description


    override fun label() : VariableLabel = this.label


    override fun tags(): List<VariableTag> = this.tags


    override fun addTags(tags : Set<VariableTag>) {
    }


    fun variableValue() : BooleanVariableValue = this.variableValue


    override fun relation() = this.relation


    override fun bookReference(entityId : EntityId) : Maybe<BookReference> = Nothing()


    // -----------------------------------------------------------------------------------------
    // HISTORY
    // -----------------------------------------------------------------------------------------

    override fun historyVariable() =
            NumberVariable(this.historyVariableId(),
                           this.historyVariableLabel(),
                           this.historyVariableDescription(),
                           listOf(),
                           Nothing(),
                           NumberVariableLiteralValue(0.0))


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override val prodTypeObject: ProdType = this


    override fun onLoad() {}


    override fun rowValue() : DB_VariableBooleanValue =
        RowValue5(variableBooleanTable,
                  PrimValue(this.variableId),
                  PrimValue(this.label),
                  PrimValue(this.description),
                  PrimValue(VariableTagSet(this.tags)),
                  SumValue(this.variableValue))


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun type(): VariableType = VariableType.BOOLEAN


    override fun dependencies(entityId : EntityId) = this.variableValue().dependencies(entityId)


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(entityId)


    override fun onAddToState(entityId : EntityId, parentVariable : Variable?)
    {
        val rel = this.relation()
        when (rel)
        {
            is Just -> {
                parentVariable?.setRelation(rel.value, this.variableId(), entityId)
            }
        }
    }


    override fun valueString(entityId : EntityId) : AppEff<String> =
        this.value().apply { effValue<AppError,String>(it.toString()) }


    override fun engineValue(entityId : EntityId) : AppEff<EngineValue> =
        apply(::EngineValueBoolean, this.value())


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value() : AppEff<Boolean> = this.variableValue().value()


    fun toggleValue(entityId : EntityId) {
        this.value() apDo {
            if (it)
                this.updateValue(false, entityId)
            else
                this.updateValue(true, entityId)
        }
    }


    fun updateValue(value : Boolean, entityId : EntityId)
    {
        when (this.variableValue())
        {
            is BooleanVariableLiteralValue -> {
                this.variableValue = BooleanVariableLiteralValue(value)
                onVariableUpdate(this, entityId)
//                this.onUpdate()
            }
        }
    }

}


/**
 * Dice Variable
 */
data class DiceRollVariable(override val id : UUID,
                            private var variableId : VariableId,
                            private var label : VariableLabel,
                            private var description : VariableDescription,
                            private val tags : MutableList<VariableTag>,
                            private val relation : Maybe<VariableRelation>,
                            val variableValue: DiceRollVariableValue)
                            : Variable()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : List<VariableTag>,
                relation : Maybe<VariableRelation>,
                value : DiceRollVariableValue)
        : this(UUID.randomUUID(),
               variableId,
               label,
               description,
               tags.toMutableList(),
               relation,
               value)


    companion object : Factory<DiceRollVariable>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DiceRollVariable> = when (doc)
        {
            is DocDict ->
            {
                apply(::DiceRollVariable,
                      // Variable Id
                      doc.at("id") ap { VariableId.fromDocument(it) },
                      // Label
                      doc.at("label") ap { VariableLabel.fromDocument(it) },
                      // Description
                      doc.at("description") ap { VariableDescription.fromDocument(it) },
                      // Tags
                      split(doc.maybeList("tags"),
                            effValue(listOf()),
                            { it.map { VariableTag.fromDocument(it) } }),
                      // Variable Relation
                      split(doc.maybeAt("relation"),
                            effValue<ValueError,Maybe<VariableRelation>>(Nothing()),
                            { apply(::Just, VariableRelation.fromDocument(it)) }),
                      // Value
                      doc.at("value") ap { DiceRollVariableValue.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.variableId.toDocument(),
        "label" to this.label.toDocument(),
        "description" to this.description.toDocument(),
        "tags" to DocList(this.tags.map { it.toDocument() }),
        "value" to this.variableValue().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // STATE
    // -----------------------------------------------------------------------------------------

    override fun variableId() : VariableId = this.variableId


    override fun setVariableId(variableId : VariableId) {
        this.variableId = variableId
    }


    override fun description() : VariableDescription = this.description


    override fun label() : VariableLabel = this.label


    override fun tags(): List<VariableTag> = this.tags


    override fun addTags(tags : Set<VariableTag>) {
    }


    override fun relation() = this.relation


    override fun bookReference(entityId : EntityId) : Maybe<BookReference> = Nothing()


    fun variableValue() : DiceRollVariableValue = this.variableValue


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    override val prodTypeObject: ProdType = this


    override fun rowValue() : DB_VariableDiceRollValue =
        RowValue4(variableDiceRollTable,
                  PrimValue(this.variableId),
                  PrimValue(this.label),
                  PrimValue(this.description),
                  PrimValue(VariableTagSet(this.tags)))


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) =
            this.variableValue.dependencies()


    override fun type(): VariableType = VariableType.DICE_ROLL


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(entityId)


    override fun onAddToState(entityId : EntityId, parentVariable : Variable?) { }


    override fun valueString(entityId : EntityId) : AppEff<String> =
            effValue(this.value().toString())


    override fun engineValue(entityId : EntityId) : AppEff<EngineValue> =
            effValue(EngineValueDiceRoll(this.value()))


    // -----------------------------------------------------------------------------------------
    // HISTORY
    // -----------------------------------------------------------------------------------------

    override fun historyVariable() =
            NumberVariable(this.historyVariableId(),
                           this.historyVariableLabel(),
                           this.historyVariableDescription(),
                           listOf(),
                           Nothing(),
                           NumberVariableLiteralValue(0.0))


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value() : DiceRoll = this.variableValue().value()

}


/**
 * Number Variable
 */
data class NumberVariable(override val id : UUID,
                          private var variableId : VariableId,
                          private var label : VariableLabel,
                          private var description : VariableDescription,
                          private val tags : MutableList<VariableTag>,
                          private val relation : Maybe<VariableRelation>,
                          var variableValue : NumberVariableValue,
                          var constraint : Maybe<ConstraintNumber>)
                           : Variable()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : List<VariableTag>,
                relation: Maybe<VariableRelation>,
                value : NumberVariableValue)
        : this(UUID.randomUUID(),
               variableId,
               label,
               description,
               tags.toMutableList(),
               relation,
               value,
               Nothing())


    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : List<VariableTag>,
                relation: Maybe<VariableRelation>,
                value : NumberVariableValue,
                constraint : Maybe<ConstraintNumber>)
        : this(UUID.randomUUID(),
               variableId,
               label,
               description,
               tags.toMutableList(),
               relation,
               value,
               constraint)


    companion object : Factory<NumberVariable>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<NumberVariable> = when (doc)
        {
            is DocDict ->
            {
                apply(::NumberVariable,
                      // Variable Id
                      doc.at("id") ap { VariableId.fromDocument(it) },
                      // Label
                      doc.at("label") ap { VariableLabel.fromDocument(it) },
                      // Description
                      doc.at("description") ap { VariableDescription.fromDocument(it) },
                      // Tags
                      split(doc.maybeList("tags"),
                            effValue(listOf()),
                            { it.map { VariableTag.fromDocument(it) } }),
                      // Variable Relation
                      split(doc.maybeAt("relation"),
                            effValue<ValueError,Maybe<VariableRelation>>(Nothing()),
                            { apply(::Just, VariableRelation.fromDocument(it)) }),
                      // Value
                      doc.at("value") ap { NumberVariableValue.fromDocument(it) },
                      // Constraint
                      split(doc.maybeAt("constraint"),
                            effValue<ValueError,Maybe<ConstraintNumber>>(Nothing()),
                            { apply(::Just, ConstraintNumber.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.variableId.toDocument(),
        "label" to this.label.toDocument(),
        "description" to this.description.toDocument(),
        "tags" to DocList(this.tags.map { it.toDocument() }),
        "value" to this.variableValue().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // STATE
    // -----------------------------------------------------------------------------------------

    override fun variableId() : VariableId = this.variableId


    override fun setVariableId(variableId : VariableId) {
        this.variableId = variableId
    }


    override fun description() : VariableDescription = this.description


    override fun label() : VariableLabel = this.label


    override fun tags(): List<VariableTag> = this.tags


    override fun addTags(tags : Set<VariableTag>) {
    }


    override fun relation() = this.relation


    override fun bookReference(entityId : EntityId) : Maybe<BookReference> = Nothing()


    fun variableValue() : NumberVariableValue = this.variableValue


    fun constraint() : Maybe<ConstraintNumber> = this.constraint


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    override val prodTypeObject : ProdType = this


    override fun rowValue() : DB_VariableNumberValue =
        RowValue5(variableNumberTable,
                  PrimValue(this.variableId),
                  PrimValue(this.label),
                  PrimValue(this.description),
                  PrimValue(VariableTagSet(this.tags)),
                  SumValue(this.variableValue))


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) : Set<VariableReference> =
            this.variableValue.dependencies(entityId)


    override fun type(): VariableType = VariableType.NUMBER


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(entityId)


    override fun onAddToState(entityId : EntityId, parentVariable : Variable?)
    {
        val rel = this.relation()
        when (rel)
        {
            is Just -> {
                parentVariable?.setRelation(rel.value, this.variableId(), entityId)
            }
        }
    }


    override fun engineValue(entityId : EntityId) : AppEff<EngineValue> =
        this.value(entityId).apply {
            when (it) {
                is Just -> effValue<AppError,EngineValue>(EngineValueNumber(it.value))
                else    -> effError<AppError,EngineValue>(AppStateError(VariableDoesNotHaveValue(this.variableId)))
            }
        }



    // -----------------------------------------------------------------------------------------
    // HISTORY
    // -----------------------------------------------------------------------------------------

    override fun historyVariable() : NumberListVariable =
            NumberListVariable(this.historyVariableId(),
                               this.historyVariableLabel(),
                               this.historyVariableDescription(),
                               listOf(),
                               Nothing(),
                               Nothing())


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value(entityId : EntityId) : AppEff<Maybe<Double>> =
            this.variableValue().value(entityId)


    /**
     * The string representation of the widget's current value. This method returns 0 when the
     * value is null for some reason.
     */
    override fun valueString(entityId : EntityId) : AppEff<String>
    {
        fun maybeString(mDouble : Maybe<Double>) : AppEff<String> =
            when (mDouble) {
                is Just -> effValue(Util.doubleString(mDouble.value))
                else    -> effValue("")
            }

        return this.value(entityId).apply(::maybeString)
    }


    fun valueOrZero(entityId : EntityId) : Double
    {
        val valueEff = this.value(entityId)
        when (valueEff) {
            is effect.Val -> {
                val maybeValue = valueEff.value
                when (maybeValue) {
                    is Just -> return maybeValue.value
                }
            }
        }

        return 0.0
    }


    fun valueOrError(entityId : EntityId) : AppEff<Double> =
        this.value(entityId) ap {
            when (it) {
                is Just -> effValue(it.value)
                else    -> effError<AppError,Double>(AppStateError(VariableDoesNotHaveValue(this.variableId)))
            }
        }


    fun updateValue(value : Double, entityId : EntityId)
    {
        when (this.variableValue())
        {
            is NumberVariableLiteralValue ->
            {
                val constraint = this.constraint
                Log.d("***WIDGET", "updating number litera variable: $constraint")
                when (constraint)
                {
                    is Just ->
                    {
                        Log.d("***WIDGET", "updating number litera variable yes constraint")
                        constraint.value.constrainedValue(value, entityId) apDo {
                            this.variableValue = NumberVariableLiteralValue(it)
                            onVariableUpdate(this, entityId)
//                            this.onUpdate()
                        }
                    }
                    is Nothing ->
                    {
                        Log.d("***WIDGET", "updating number litera variable no constraint")
                        this.variableValue = NumberVariableLiteralValue(value)
                        onVariableUpdate(this, entityId)
//                        this.onUpdate()
                    }
                }
            }
        }
    }

}


/**
 * Number List Variable
 */
data class NumberListVariable(override val id : UUID,
                              private var variableId : VariableId,
                              private var label : VariableLabel,
                              private var description : VariableDescription,
                              private val tags : MutableList<VariableTag>,
                              private val relation : Maybe<VariableRelation>,
                              var variableValue : NumberListVariableValue,
                              var valueSetId : Maybe<ValueSetId>)
                               : Variable()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : List<VariableTag>,
                relation : Maybe<VariableRelation>,
                valueSetId : Maybe<ValueSetId>)
        : this(UUID.randomUUID(),
               variableId,
               label,
               description,
               tags.toMutableList(),
               relation,
               NumberListVariableLiteralValue(listOf()),
               valueSetId)


    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : List<VariableTag>,
                relation : Maybe<VariableRelation>,
                variableValue : NumberListVariableValue,
                valueSetId : Maybe<ValueSetId>)
        : this(UUID.randomUUID(),
               variableId,
               label,
               description,
               tags.toMutableList(),
               relation,
               variableValue,
               valueSetId)


    companion object : Factory<NumberListVariable>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<NumberListVariable> = when (doc)
        {
            is DocDict ->
            {
                apply(::NumberListVariable,
                      // Variable Id
                      doc.at("id") ap { VariableId.fromDocument(it) },
                      // Label
                      doc.at("label") ap { VariableLabel.fromDocument(it) },
                      // Description
                      doc.at("description") ap { VariableDescription.fromDocument(it) },
                      // Tags
                      split(doc.maybeList("tags"),
                            effValue(listOf()),
                            { it.map { VariableTag.fromDocument(it) } }),
                      // Variable Relation
                      split(doc.maybeAt("relation"),
                            effValue<ValueError,Maybe<VariableRelation>>(Nothing()),
                            { apply(::Just, VariableRelation.fromDocument(it)) }),
                      // Value
                      doc.at("value") ap { NumberListVariableValue.fromDocument(it) },
                      // Value Set Id
                      split(doc.maybeAt("value_set_id"),
                            effValue<ValueError,Maybe<ValueSetId>>(Nothing()),
                            { apply(::Just, ValueSetId.fromDocument(it)) } )
                    )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.variableId.toDocument(),
        "label" to this.label.toDocument(),
        "description" to this.description.toDocument(),
        "tags" to DocList(this.tags.map { it.toDocument() }),
        "value" to this.variableValue.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // STATE
    // -----------------------------------------------------------------------------------------

    override fun variableId() : VariableId = this.variableId


    override fun setVariableId(variableId : VariableId) {
        this.variableId = variableId
    }


    override fun description() : VariableDescription = this.description


    override fun label() : VariableLabel = this.label


    override fun tags(): List<VariableTag> = this.tags


    override fun addTags(tags : Set<VariableTag>) {
    }


    override fun relation() = this.relation


    override fun bookReference(entityId : EntityId) : Maybe<BookReference> = Nothing()


    fun variableValue() : NumberListVariableValue = this.variableValue


    fun valueSetId() : Maybe<ValueSetId> = this.valueSetId


    override fun valueString(entityId : EntityId) : AppEff<String> =
        this.value(entityId).apply { effValue<AppError,String>(it.toString()) }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    override val prodTypeObject: ProdType = this


    override fun rowValue() : DB_VariableNumberListValue =
        RowValue6(variableNumberListTable,
                  PrimValue(this.variableId),
                  PrimValue(this.label),
                  PrimValue(this.description),
                  PrimValue(VariableTagSet(this.tags)),
                  SumValue(this.variableValue),
                  MaybePrimValue(this.valueSetId))


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) = this.variableValue().dependencies()


    override fun type(): VariableType = VariableType.TEXT


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(entityId)


    override fun onAddToState(entityId : EntityId, parentVariable : Variable?)
    {
        val rel = this.relation()
        when (rel)
        {
            is Just -> {
                parentVariable?.setRelation(rel.value, this.variableId(), entityId)
            }
        }
    }


    override fun engineValue(entityId : EntityId) : AppEff<EngineValue> =
        apply(::EngineNumberListValue, this.value(entityId))


    // -----------------------------------------------------------------------------------------
    // HISTORY
    // -----------------------------------------------------------------------------------------

    override fun historyVariable() =
            NumberVariable(this.historyVariableId(),
                           this.historyVariableLabel(),
                           this.historyVariableDescription(),
                           listOf(),
                           Nothing(),
                           NumberVariableLiteralValue(0.0))


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value(entityId : EntityId) : AppEff<List<Double>> =
            this.variableValue().value(entityId)


    fun updateLiteralValue(value : List<Double>, entityId : EntityId)
    {
        when (this.variableValue())
        {
            is NumberListVariableLiteralValue ->
            {
                this.variableValue = NumberListVariableLiteralValue(value)
                onVariableUpdate(this, entityId)
            }
        }
    }

}


/**
 * Text Variable
 */
data class TextVariable(override val id : UUID,
                        private var variableId : VariableId,
                        private var label : VariableLabel,
                        private var description : VariableDescription,
                        private val tags : MutableList<VariableTag>,
                        private val relation : Maybe<VariableRelation>,
                        var variableValue : TextVariableValue)
                        : Variable()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : List<VariableTag>,
                relation : Maybe<VariableRelation>,
                variableValue : TextVariableValue)
        : this(UUID.randomUUID(),
               variableId,
               label,
               description,
               tags.toMutableList(),
               relation,
               variableValue)


    companion object : Factory<TextVariable>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TextVariable> = when (doc)
        {
            is DocDict ->
            {
                apply(::TextVariable,
                      // Variable Id
                      doc.at("id") ap { VariableId.fromDocument(it) },
                      // Label
                      doc.at("label") ap { VariableLabel.fromDocument(it) },
                      // Description
                      doc.at("description") ap { VariableDescription.fromDocument(it) },
                      // Tags
                      split(doc.maybeList("tags"),
                            effValue(listOf()),
                            { it.map { VariableTag.fromDocument(it) } }),
                      // Variable Relation
                      split(doc.maybeAt("relation"),
                            effValue<ValueError,Maybe<VariableRelation>>(Nothing()),
                            { apply(::Just, VariableRelation.fromDocument(it)) }),
                      // Value
                      doc.at("value") ap { TextVariableValue.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.variableId.toDocument(),
        "label" to this.label.toDocument(),
        "description" to this.description.toDocument(),
        "tags" to DocList(this.tags.map { it.toDocument() }),
        "value" to this.variableValue.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // STATE
    // -----------------------------------------------------------------------------------------

    override fun variableId() : VariableId = this.variableId


    override fun setVariableId(variableId : VariableId) {
        this.variableId = variableId
    }


    override fun description() : VariableDescription = this.description


    override fun label() : VariableLabel = this.label


    override fun tags(): List<VariableTag> = this.tags


    override fun addTags(tags : Set<VariableTag>) {
        this.tags.addAll(tags)
    }


    override fun relation() = this.relation


    override fun bookReference(entityId : EntityId) : Maybe<BookReference>
    {
        val variableValue = this.variableValue
        when (variableValue)
        {
            is TextVariableValueValue -> {
                val value = value(variableValue.valueReference, entityId)
                when (value) {
                    is Val -> return value.value.bookReference
                }
            }
        }

        return Nothing()
    }


    fun variableValue() : TextVariableValue = this.variableValue


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    override val prodTypeObject : ProdType = this


    override fun rowValue() : DB_VariableTextValue =
        RowValue5(variableTextTable,
                  PrimValue(this.variableId),
                  PrimValue(this.label),
                  PrimValue(this.description),
                  PrimValue(VariableTagSet(this.tags)),
                  SumValue(this.variableValue))


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) =
            this.variableValue().dependencies(entityId)


    override fun type() : VariableType = VariableType.TEXT


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(entityId)


    override fun onAddToState(entityId : EntityId, parentVariable : Variable?)
    {
        val rel = this.relation()
        when (rel)
        {
            is Just -> {
                parentVariable?.setRelation(rel.value, this.variableId(), entityId)
            }
        }
    }


    override fun engineValue(entityId : EntityId) : AppEff<EngineValue> =
        this.value(entityId).apply {
            when (it) {
                is Just -> effValue<AppError,EngineValue>(EngineValueText(it.value))
                else    -> effError<AppError,EngineValue>(AppStateError(VariableDoesNotHaveValue(this.variableId)))
            }
        }


    // -----------------------------------------------------------------------------------------
    // HISTORY
    // -----------------------------------------------------------------------------------------

    override fun historyVariable() =
            NumberVariable(this.historyVariableId(),
                           this.historyVariableLabel(),
                           this.historyVariableDescription(),
                           listOf(),
                           Nothing(),
                           NumberVariableLiteralValue(0.0))


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value(entityId : EntityId) : AppEff<Maybe<String>> =
            this.variableValue().value(entityId)


    override fun valueString(entityId : EntityId) : AppEff<String>
    {
        fun maybeString(mString : Maybe<String>) : AppEff<String> =
            when (mString) {
                is Just -> effValue(mString.value)
                else    -> effValue("")
            }

        return this.value(entityId).apply(::maybeString)
    }


//    fun updateLiteralValue(value : String, sheetId : SheetId)
//    {
//        when (this.variableValue())
//        {
//        }
//    }


    fun updateValue(value : String, entityId : EntityId)
    {
        val currentVariableValue = this.variableValue()
        when (currentVariableValue)
        {
            is TextVariableLiteralValue ->
            {
                this.variableValue = TextVariableLiteralValue(value)
                onVariableUpdate(this, entityId)
//                this.onUpdate()
            }
            is TextVariableValueValue -> {
                val valueSetId = currentVariableValue.valueReference.valueSetId
                val newValueReference = ValueReference(valueSetId, TextReferenceLiteral(value))
                this.variableValue = TextVariableValueValue(newValueReference)
                onVariableUpdate(this, entityId)
//                this.onUpdate()
            }
            is TextVariableValueUnknownValue -> {
                val valueSetId = currentVariableValue.valueSetId
                val newValueReference = ValueReference(TextReferenceLiteral(valueSetId.value),
                                                       TextReferenceLiteral(value))
                this.variableValue = TextVariableValueValue(newValueReference)
                onVariableUpdate(this, entityId)
//                this.onUpdate()
            }
        }
    }


    private fun updateRelations(entityId : EntityId)
    {
        when (this.variableValue)
        {
            is TextVariableValueValue ->
            {
                this.variableValue.companionVariables(entityId) apDo {
                    it.forEach { variable ->
                        val relation = variable.relation()
                        when (relation) {
                            is Just -> {
                                this.setRelation(relation.value, variable.variableId(), entityId)
                            }
                        }
                    }
                }
            }
        }

    }

}


/**
 * Text List Variable
 */
data class TextListVariable(override val id : UUID,
                            private var variableId : VariableId,
                            private var label : VariableLabel,
                            private var description : VariableDescription,
                            private val tags : MutableList<VariableTag>,
                            private val relation : Maybe<VariableRelation>,
                            var variableValue : TextListVariableValue,
                            var constraint : Maybe<Constraint>,
                            var valueSetId : Maybe<ValueSetId>,
                            var setVariableId : Maybe<VariableId>)
                            : Variable()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : List<VariableTag>,
                relation : Maybe<VariableRelation>,
                variableValue : TextListVariableValue,
                constraint : Maybe<Constraint>,
                valueSetId : Maybe<ValueSetId>,
                setVariableId : Maybe<VariableId>)
        : this(UUID.randomUUID(),
               variableId,
               label,
               description,
               tags.toMutableList(),
               relation,
               variableValue,
               constraint,
               valueSetId,
               setVariableId)


    companion object : Factory<TextListVariable>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TextListVariable> = when (doc)
        {
            is DocDict ->
            {
                apply(::TextListVariable,
                      // Variable Id
                      doc.at("id") ap { VariableId.fromDocument(it) },
                      // Label
                      doc.at("label") ap { VariableLabel.fromDocument(it) },
                      // Description
                      doc.at("description") ap { VariableDescription.fromDocument(it) },
                      // Tags
                      split(doc.maybeList("tags"),
                            effValue(listOf()),
                            { it.map { VariableTag.fromDocument(it) } }),
                      // Variable Relation
                      split(doc.maybeAt("relation"),
                            effValue<ValueError,Maybe<VariableRelation>>(Nothing()),
                            { apply(::Just, VariableRelation.fromDocument(it)) }),
                      // Value
                      doc.at("value") ap { TextListVariableValue.fromDocument(it) },
                      // Constraint
                      split(doc.maybeAt("constraint"),
                            effValue<ValueError,Maybe<Constraint>>(Nothing()),
                            { apply(::Just, Constraint.fromDocument(it)) } ),
                      // Value Set Id
                      split(doc.maybeAt("value_set_id"),
                            effValue<ValueError,Maybe<ValueSetId>>(Nothing()),
                            { apply(::Just, ValueSetId.fromDocument(it)) } ),
                      // Set Variable Id
                      split(doc.maybeAt("set_variable_id"),
                            effValue<ValueError,Maybe<VariableId>>(Nothing()),
                            { apply(::Just, VariableId.fromDocument(it)) } )
                    )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.variableId.toDocument(),
        "label" to this.label.toDocument(),
        "description" to this.description.toDocument(),
        "tags" to DocList(this.tags.map { it.toDocument() }),
        "value" to this.variableValue.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // STATE
    // -----------------------------------------------------------------------------------------

    override fun variableId() : VariableId = this.variableId


    override fun setVariableId(variableId : VariableId) {
        this.variableId = variableId
    }


    override fun description() : VariableDescription = this.description


    override fun label() : VariableLabel = this.label


    override fun tags(): List<VariableTag> = this.tags


    override fun addTags(tags : Set<VariableTag>) {
    }


    override fun relation() = this.relation


    override fun bookReference(entityId : EntityId) : Maybe<BookReference> = Nothing()


    fun variableValue() : TextListVariableValue = this.variableValue


    fun constraint() : Maybe<Constraint> = this.constraint


    fun valueSetId() : Maybe<ValueSetId> = this.valueSetId


    fun setVariableId() : Maybe<VariableId> = this.setVariableId


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    override val prodTypeObject: ProdType = this


    override fun rowValue() : DB_VariableTextListValue =
        RowValue6(variableTextListTable,
                  PrimValue(this.variableId),
                  PrimValue(this.label),
                  PrimValue(this.description),
                  PrimValue(VariableTagSet(this.tags)),
                  SumValue(this.variableValue),
                  MaybePrimValue(this.valueSetId))


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) = this.variableValue().dependencies()


    override fun type(): VariableType = VariableType.TEXT


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(entityId)


    override fun onAddToState(entityId : EntityId, parentVariable : Variable?)
    {
        val rel = this.relation()
        when (rel)
        {
            is Just -> {
                parentVariable?.setRelation(rel.value, this.variableId(), entityId)
            }
        }
    }


    override fun valueString(entityId : EntityId): AppEff<String> =
        this.value(entityId).apply { effValue<AppError,String>(it.toString()) }


    override fun engineValue(entityId : EntityId) : AppEff<EngineValue> =
            apply(::EngineTextListValue, this.value(entityId))


    fun setVariable(entityId : EntityId) : Maybe<TextListVariable>
    {
        val setVariableId =this.setVariableId
        return when (setVariableId) {
            is Just -> {
                val variable = textListVariable(setVariableId.value, entityId)
                when (variable) {
                    is Val -> Just(variable.value)
                    is Err -> Nothing()
                }
            }
            is Nothing -> Nothing()
        }
    }


    // -----------------------------------------------------------------------------------------
    // HISTORY
    // -----------------------------------------------------------------------------------------

    override fun historyVariable() =
            NumberVariable(this.historyVariableId(),
                           this.historyVariableLabel(),
                           this.historyVariableDescription(),
                           listOf(),
                           Nothing(),
                           NumberVariableLiteralValue(0.0))


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value(entityId : EntityId) : AppEff<List<String>> =
            this.variableValue().value(entityId)


    fun updateLiteralValue(value : List<String>, entityId : EntityId)
    {
        when (this.variableValue())
        {
            is TextListVariableLiteralValue ->
            {
                this.variableValue = TextListVariableLiteralValue(value)
                onVariableUpdate(this, entityId)
            }
        }
    }


    fun addValue(value : String, entityId : EntityId)
    {
        val variableValue = this.variableValue()
        when (variableValue)
        {
            is TextListVariableLiteralValue ->
            {
                val newValueList = variableValue.value.plus(value)
                this.variableValue = TextListVariableLiteralValue(newValueList)
                onVariableUpdate(this, entityId)
            }
        }
    }


    fun values(entityId : EntityId) : List<Value>
    {
        var values : List<Value> = listOf()

        this.valueSetId.doMaybe {
            valueSet(it, entityId)
            .apDo { valueSet ->
                value(entityId)
            .apDo { valueStrings ->
                val valueIds = valueStrings.map { ValueId(it) }
                Log.d("***VARIABLE", "value ids: $valueIds ")
                values = valueSet.values(valueIds, entityId)
            } }
        }

        return values
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRAINTS
    // -----------------------------------------------------------------------------------------

    fun hasSetConstraint() : Boolean {
        val constraint = this.constraint
        return when (constraint) {
            is Just    -> constraint.value.hasConstraintType(ConstraintTypeTextListIsSet)
            is Nothing -> false
        }
    }

}



enum class VariableType
{
    BOOLEAN,
    DICE_ROLL,
    NUMBER,
    TEXT,
    TEXT_LIST
}



/**
 * Variable NameSpace
 */
data class VariableNamespace(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<VariableNamespace>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<VariableNamespace> = when (doc)
        {
            is DocText -> effValue(VariableNamespace(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Variable Name
 */
data class VariableName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<VariableName>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<VariableName> = when (doc)
        {
            is DocText -> effValue(VariableName(doc.text))
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
 * Variable Reference
 */
@Suppress("UNCHECKED_CAST")
sealed class VariableReference : ToDocument, SQLSerializable, Serializable
{

    companion object : Factory<VariableReference>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<VariableReference> =
            when (doc.case())
            {
                "variable_id"          -> VariableId.fromDocument(doc) as ValueParser<VariableReference>
                "variable_tag"         -> VariableTag.fromDocument(doc) as ValueParser<VariableReference>
                "variable_context"     -> VariableContext.fromDocument(doc) as ValueParser<VariableReference>
                "related_variable"     -> RelatedVariable.fromDocument(doc) as ValueParser<VariableReference>
                "related_variable_set" -> RelatedVariableSet.fromDocument(doc) as ValueParser<VariableReference>
                else                   -> effError(UnknownCase(doc.case(), doc.path))
            }
    }
}


/**
 * Variable Id
 */
data class VariableId(val namespace : Maybe<VariableNamespace>,
                      val name : VariableName) : VariableReference(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(name : String)
        : this(Nothing(), VariableName(name))


    constructor(name : VariableName)
            : this(Nothing(), name)


    constructor(namespace : String, name : String)
            : this(Just(VariableNamespace(namespace)), VariableName(name))


    constructor(namespace : VariableNamespace, name : VariableName)
            : this(Just(namespace), name)


    companion object : Factory<VariableId>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<VariableId> = when (doc)
        {
            is DocDict ->
            {
                effApply(::VariableId,
                         // Namespace
                         split(doc.maybeAt("namespace"),
                               effValue<ValueError,Maybe<VariableNamespace>>(Nothing()),
                               { effApply(::Just, VariableNamespace.fromDocument(it)) }),
                         // Name
                         doc.at("name") ap { VariableName.fromDocument(it) }
                         )
            }
            is DocText -> {
                val pieces = doc.text.split("::")
                if (pieces.size == 2) {
                    effValue(VariableId(VariableNamespace(pieces[0]), VariableName(pieces[1])))
                } else {
                    effValue(VariableId(doc.text))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.nameString())


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun nameString() : String = this.name.value


    fun namespaceString() : Maybe<String> = this.namespace.apply { Just(it.value) }


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString(): String
    {
        var s = ""

        val ns = namespaceString()
        when (ns)
        {
            is Just -> {
                s += ns.value
                s += "::"
            }
        }

        s += nameString()

        return s
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.toString() })

}


/**
 * Variable Tag
 */
data class VariableTag(val value : String) : VariableReference(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<VariableTag>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<VariableTag> = when (doc)
        {
            is DocText -> effValue(VariableTag(doc.text))
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
 * Related Variable
 */
data class RelatedVariable(val name : VariableName,
                           val relation : VariableRelation) : VariableReference(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(name : String, relation : String)
        : this(VariableName(name), VariableRelation(relation))


    companion object : Factory<RelatedVariable>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<RelatedVariable> = when (doc)
        {
            is DocDict ->
            {
                apply(::RelatedVariable,
                      // Name
                      doc.at("name") ap { VariableName.fromDocument(it) },
                      // Relation
                      doc.at("relation") ap { VariableRelation.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "name" to this.name().toDocument(),
        "relation" to this.relation().toDocument())
    )


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun name() : VariableName = this.name


    fun relation() : VariableRelation = this.relation


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString() : String = "${this.name().value}->${this.relation().value}"


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.toString() })

}


/**
 * Related Variable Set
 */
data class RelatedVariableSet(val tag : VariableTag,
                              val relation : VariableRelation) : VariableReference(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(tag : String, relation : String)
        : this(VariableTag(tag), VariableRelation(relation))


    companion object : Factory<RelatedVariableSet>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<RelatedVariableSet> = when (doc)
        {
            is DocDict ->
            {
                apply(::RelatedVariableSet,
                      // Tag
                      doc.at("tag") ap { VariableTag.fromDocument(it) },
                      // Relation
                      doc.at("relation") ap { VariableRelation.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "tag" to this.tag().toDocument(),
        "relation" to this.relation().toDocument())
    )


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun tag() : VariableTag = this.tag


    fun relation() : VariableRelation = this.relation


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString() : String = "*${this.tag().value}*->${this.relation().value}"


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.toString() })

}


/**
 * Variable Context
 */
data class VariableContext(val value : String) : VariableReference(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<VariableContext>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<VariableContext> = when (doc)
        {
            is DocText -> effValue(VariableContext(doc.text))
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
 * Variable Relation
 */
data class VariableRelation(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<VariableRelation>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<VariableRelation> = when (doc)
        {
            is DocText -> effValue(VariableRelation(doc.text))
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
 * Variable Label
 */
data class VariableLabel(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<VariableLabel>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<VariableLabel> = when (doc)
        {
            is DocText -> effValue(VariableLabel(doc.text))
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
 * Variable Description
 */
data class VariableDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<VariableDescription>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<VariableDescription> = when (doc)
        {
            is DocText -> effValue(VariableDescription(doc.text))
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
 * Defines Namespace
 */
//data class DefinesNamespace(val value : Boolean) : SQLSerializable, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<DefinesNamespace>
//    {
//        override fun fromDocument(doc: SchemaDoc): ValueParser<DefinesNamespace> = when (doc)
//        {
//            is DocBoolean -> effValue(DefinesNamespace(doc.boolean))
//            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // SQL SERIALIZABLE
//    // -----------------------------------------------------------------------------------------
//
//    override fun asSQLValue() : SQLValue = SQLInt({ if (this.value) 1 else 0 })
//
//}


/**
 * Variable Tag Set
 */
data class VariableTagSet(val variables : List<VariableTag>) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<VariableTagSet>
    {

        override fun fromDocument(doc : SchemaDoc) : ValueParser<VariableTagSet> = when (doc)
        {
            is DocList -> apply(::VariableTagSet, doc.map { VariableTag.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.LIST, docType(doc), doc.path))
        }


        fun empty() : VariableTagSet = VariableTagSet(listOf())
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}

