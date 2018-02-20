
package com.kispoko.tome.model.game.engine.variable


import com.kispoko.tome.R.string.*
import com.kispoko.tome.app.*
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue5
import com.kispoko.tome.lib.orm.RowValue6
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.SumValue
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.reference.TextReferenceLiteral
import com.kispoko.tome.model.game.engine.value.ValueReference
import com.kispoko.tome.model.game.engine.value.ValueSetId
import com.kispoko.tome.model.game.engine.variable.constraint.NumberConstraint
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.rts.sheet.*
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
 * Variable
 */
@Suppress("UNCHECKED_CAST")
sealed class Variable : ProdType, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var onUpdateListener : () -> Unit = fun() : Unit {}


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
                    sheetContext : SheetContext)
    {
        this.relationToVariableId.put(relation, variableId)

        SheetManager.sheetState(sheetContext.sheetId) apDo {
        it.variableWithId(variableId)                 apDo {
            it.addRelatedParent(this.variableId())
        } }

        ApplicationLog.event(AppStateEvent(VariableRelationAdded(variableId, relation)))
    }


    fun hasRelation(relation : VariableRelation) : Boolean =
            this.relationToVariableId.containsKey(relation)


    abstract fun relation() : Maybe<VariableRelation>


    fun booleanVariable(sheetId : SheetId) : AppEff<BooleanVariable> = when (this)
    {
        is BooleanVariable -> effValue(this)
        else               -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(sheetId,
                                                               this.variableId(),
                                                               VariableType.BOOLEAN,
                                                               this.type())))
    }


    fun diceRollVariable(sheetId : SheetId) : AppEff<DiceRollVariable> = when (this)
    {
        is DiceRollVariable -> effValue(this)
        else                -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(sheetId,
                                                               this.variableId(),
                                                               VariableType.DICE_ROLL,
                                                               this.type())))
    }


    fun numberVariable(sheetId : SheetId) : AppEff<NumberVariable> = when (this)
    {
        is NumberVariable -> effValue(this)
        else              -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(sheetId,
                                                               this.variableId(),
                                                               VariableType.NUMBER,
                                                               this.type())))
    }


    fun textVariable(sheetId : SheetId) : AppEff<TextVariable> = when (this)
    {
        is TextVariable -> effValue(this)
        else            -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(sheetId,
                                                               this.variableId(),
                                                               VariableType.TEXT,
                                                               this.type())))
    }


    fun textListVariable(sheetId : SheetId) : AppEff<TextListVariable> = when (this)
    {
        is TextListVariable -> effValue(this)
        else                -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(sheetId,
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

    abstract fun dependencies(sheetContext : SheetContext) : Set<VariableReference>


    abstract fun type() : VariableType


    abstract fun companionVariables(sheetontext : SheetContext) : AppEff<Set<Variable>>


    /**
     * This method is called when one of the variable's dependencies has been updated, and this
     * variable must therefore be udpated.
     */
    fun onUpdate()
    {
        this.onUpdateListener()
    }


    fun setOnUpdateListener(listener : () -> Unit) {
        this.onUpdateListener = listener
    }


    abstract fun onAddToState(sheetContext : SheetContext, parentVariable : Variable? = null)



    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------
//
//    fun setVariableId(variableId : VariableId)
//    {
//        this.variableId. = variableId
//    }


    // -----------------------------------------------------------------------------------------
    // VALUE STRING
    // -----------------------------------------------------------------------------------------

    open fun valueString(sheetContext : SheetContext) : AppEff<String> = when (this)
    {
        is BooleanVariable      -> this.value() ap { effValue<AppError,String>(it.toString()) }
        is DiceRollVariable     -> effValue(this.value().toString())
        is NumberVariable       -> this.valueString(sheetContext)
        is NumberListVariable   -> this.valueString(sheetContext)
        is TextVariable         -> this.valueString(sheetContext)
        is TextListVariable     -> this.valueString(sheetContext)
    }

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


    override fun dependencies(sheetContext : SheetContext) = this.variableValue().dependencies(sheetContext)


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(sheetContext)


    override fun onAddToState(sheetContext : SheetContext, parentVariable : Variable?)
    {
        val rel = this.relation()
        when (rel)
        {
            is Just -> {
                parentVariable?.setRelation(rel.value, this.variableId(), sheetContext)
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value() : AppEff<Boolean> = this.variableValue().value()


    fun toggleValue(sheetId : SheetId)
    {
        this.value() apDo {
            if (it)
                this.updateValue(false, sheetId)
            else
                this.updateValue(true, sheetId)
        }

    }


    fun updateValue(value : Boolean, sheetId : SheetId)
    {
        when (this.variableValue())
        {
            is BooleanVariableLiteralValue -> {
                this.variableValue = BooleanVariableLiteralValue(value)
                SheetManager.sheetState(sheetId) apDo { it.onVariableUpdate(this) }
                this.onUpdate()
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


    fun variableValue() : DiceRollVariableValue = this.variableValue


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    override val prodTypeObject: ProdType = this


    override fun rowValue() : DB_VariableDiceRollValue =
        RowValue5(variableDiceRollTable,
                  PrimValue(this.variableId),
                  PrimValue(this.label),
                  PrimValue(this.description),
                  PrimValue(VariableTagSet(this.tags)),
                  SumValue(this.variableValue))


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun dependencies(sheetContext : SheetContext) =
            this.variableValue.dependencies()


    override fun type(): VariableType = VariableType.DICE_ROLL


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(sheetContext)


    override fun onAddToState(sheetContext : SheetContext, parentVariable : Variable?) { }


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
                          var constraint : Maybe<NumberConstraint>)
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
                constraint : Maybe<NumberConstraint>)
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
                            effValue<ValueError,Maybe<NumberConstraint>>(Nothing()),
                            { apply(::Just, NumberConstraint.fromDocument(it)) })
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


    fun variableValue() : NumberVariableValue = this.variableValue


    fun constraint() : Maybe<NumberConstraint> = this.constraint


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

    override fun dependencies(sheetContext : SheetContext) : Set<VariableReference> =
            this.variableValue.dependencies(sheetContext)


    override fun type(): VariableType = VariableType.NUMBER


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(sheetContext)


    override fun onAddToState(sheetContext : SheetContext, parentVariable : Variable?)
    {
        val rel = this.relation()
        when (rel)
        {
            is Just -> {
                parentVariable?.setRelation(rel.value, this.variableId(), sheetContext)
            }
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

    fun value(sheetContext : SheetContext) : AppEff<Maybe<Double>> =
            this.variableValue().value(sheetContext)


    /**
     * The string representation of the widget's current value. This method returns 0 when the
     * value is null for some reason.
     */
    override fun valueString(sheetContext : SheetContext) : AppEff<String>
    {
        fun maybeString(mDouble : Maybe<Double>) : AppEff<String> =
            when (mDouble) {
                is Just -> effValue(Util.doubleString(mDouble.value))
                else    -> effValue("")
            }

        return this.value(sheetContext).apply(::maybeString)
    }


    fun valueOrZero(sheetContext : SheetContext) : Double
    {
        val valueEff = this.value(sheetContext)
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


    fun valueOrError(sheetContext : SheetContext) : AppEff<Double> =
        this.value(sheetContext) ap {
            when (it) {
                is Just -> effValue(it.value)
                else    -> effError<AppError,Double>(AppStateError(VariableDoesNotHaveValue(this.variableId)))
            }
        }


    fun updateValue(value : Double, sheetContext : SheetContext)
    {
        when (this.variableValue())
        {
            is NumberVariableLiteralValue ->
            {
                val constraint = this.constraint
                when (constraint)
                {
                    is Just ->
                    {
                        constraint.value.constrainedValue(value, sheetContext) apDo {
                            this.variableValue = NumberVariableLiteralValue(it)
                            SheetManager.onVariableUpdate(sheetContext.sheetId, this)
                            this.onUpdate()
                        }
                    }
                    is Nothing ->
                    {
                        this.variableValue = NumberVariableLiteralValue(value)
                        SheetManager.onVariableUpdate(sheetContext.sheetId, this)
                        this.onUpdate()
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


    fun variableValue() : NumberListVariableValue = this.variableValue


    fun valueSetId() : Maybe<ValueSetId> = this.valueSetId


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

    override fun dependencies(sheetContext : SheetContext) = this.variableValue().dependencies()


    override fun type(): VariableType = VariableType.TEXT


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(sheetContext)


    override fun onAddToState(sheetContext : SheetContext, parentVariable : Variable?)
    {
        val rel = this.relation()
        when (rel)
        {
            is Just -> {
                parentVariable?.setRelation(rel.value, this.variableId(), sheetContext)
            }
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

    fun value(sheetContext : SheetContext) : AppEff<List<Double>> =
            this.variableValue().value(sheetContext)


    fun updateLiteralValue(value : List<Double>, sheetId : SheetId)
    {
        when (this.variableValue())
        {
            is NumberListVariableLiteralValue ->
            {
                this.variableValue = NumberListVariableLiteralValue(value)
                SheetManager.onVariableUpdate(sheetId, this)
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

    override fun dependencies(sheetContext : SheetContext) = this.variableValue().dependencies(sheetContext)


    override fun type() : VariableType = VariableType.TEXT


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(sheetContext)


    override fun onAddToState(sheetContext : SheetContext, parentVariable : Variable?)
    {
        val rel = this.relation()
        when (rel)
        {
            is Just -> {
                parentVariable?.setRelation(rel.value, this.variableId(), sheetContext)
            }
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

    fun value(sheetContext : SheetContext) : AppEff<Maybe<String>> =
            this.variableValue().value(sheetContext)


    override fun valueString(sheetContext : SheetContext) : AppEff<String>
    {
        fun maybeString(mString : Maybe<String>) : AppEff<String> =
            when (mString) {
                is Just -> effValue(mString.value)
                else    -> effValue("")
            }

        return this.value(sheetContext).apply(::maybeString)
    }


//    fun updateLiteralValue(value : String, sheetId : SheetId)
//    {
//        when (this.variableValue())
//        {
//        }
//    }


    fun updateValue(value : String, sheetContext : SheetContext)
    {
        val currentVariableValue = this.variableValue()
        when (currentVariableValue)
        {
            is TextVariableLiteralValue ->
            {
                this.variableValue = TextVariableLiteralValue(value)
                SheetManager.onVariableUpdate(sheetContext.sheetId, this)
                this.onUpdate()
            }
            is TextVariableValueValue -> {
                val valueSetId = currentVariableValue.valueReference.valueSetId
                val newValueReference = ValueReference(valueSetId, TextReferenceLiteral(value))
                this.variableValue = TextVariableValueValue(newValueReference)
//                this.updateRelations(sheetContext)
                SheetManager.onVariableUpdate(sheetContext.sheetId, this)
                this.onUpdate()
            }
            is TextVariableValueUnknownValue -> {
                val valueSetId = currentVariableValue.valueSetId
                val newValueReference = ValueReference(TextReferenceLiteral(valueSetId.value),
                                                       TextReferenceLiteral(value))
                this.variableValue = TextVariableValueValue(newValueReference)
                SheetManager.onVariableUpdate(sheetContext.sheetId, this)
                this.onUpdate()
            }
        }
    }


    private fun updateRelations(sheetContext : SheetContext)
    {
        when (this.variableValue)
        {
            is TextVariableValueValue ->
            {
                this.variableValue.companionVariables(sheetContext) apDo {
                    it.forEach { variable ->
                        val relation = variable.relation()
                        when (relation) {
                            is Just -> {
                                this.setRelation(relation.value, variable.variableId(), sheetContext)
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
                variableValue : TextListVariableValue,
                valueSetId : Maybe<ValueSetId>)
        : this(UUID.randomUUID(),
               variableId,
               label,
               description,
               tags.toMutableList(),
               relation,
               variableValue,
               valueSetId)


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


    fun variableValue() : TextListVariableValue = this.variableValue


    fun valueSetId() : Maybe<ValueSetId> = this.valueSetId


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

    override fun dependencies(sheetContext : SheetContext) = this.variableValue().dependencies()


    override fun type(): VariableType = VariableType.TEXT


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(sheetContext)


    override fun onAddToState(sheetContext : SheetContext, parentVariable : Variable?)
    {
        val rel = this.relation()
        when (rel)
        {
            is Just -> {
                parentVariable?.setRelation(rel.value, this.variableId(), sheetContext)
            }
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

    fun value(sheetContext : SheetContext) : AppEff<List<String>> =
            this.variableValue().value(sheetContext)


    fun updateLiteralValue(value : List<String>, sheetId : SheetId)
    {
        when (this.variableValue())
        {
            is TextListVariableLiteralValue ->
            {
                this.variableValue = TextListVariableLiteralValue(value)
                SheetManager.onVariableUpdate(sheetId, this)
            }
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


//
//public abstract class Variable extends ProdType
//{
//
//    // ABSTRACT METHODS
//    // ------------------------------------------------------------------------------------------
//
//    public abstract String                  name();
//    public abstract void                    setName(String name);
//    public abstract String                  label();
//    public abstract void                    setLabel(String label);
//    public abstract String                  description();
//    public abstract boolean                 isNamespaced();
//    public abstract void                    setIsNamespaced(Boolean isNamespaced);
//    public abstract List<VariableReference> dependencies();
//    public abstract List<String>            tags();
//    public abstract String                  valueString() throws NullVariableException;
//    public abstract void                    initialize();
//
//
//    // PROPERTIES
//    // ------------------------------------------------------------------------------------------
//
//    private OnUpdateListener                onUpdateListener;
//
//    protected String                        originalName;
//    protected String                        originalLabel;
//
//
//    // CONSTRUCTORS
//    // ------------------------------------------------------------------------------------------
//
//    public Variable()
//    {
//        this.onUpdateListener = null;
//    }
//
//
//    // API
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * This method should be called when the variable's value changes. This could happen directly
//     * from user input, or indirectly, if user input changes a variable that was part of this
//     * variable's value.
//     */
//    public void onUpdate()
//    {
//        // [1] Call the variable's update listener
//        // --------------------------------------------------------------------------------------
//
//        if (this.onUpdateListener != null) {
//            this.onUpdateListener.onUpdate();
//        }
//
//        // [2] Update any variables that depend on this variable
//        // --------------------------------------------------------------------------------------
//
//        State.updateVariableDependencies(this);
//    }
//
//
//    public void setOnUpdateListener(OnUpdateListener onUpdateListener)
//    {
//        this.onUpdateListener = onUpdateListener;
//    }
//
//
//    public void setNamespace(Namespace namespace)
//    {
//        // > Update name
//        String previousName = this.name();
//
//        String newName;
//        if (this.originalName != null)
//            newName = namespace.name() + "." + this.originalName;
//        else
//            newName = namespace.name();
//        this.setName(newName);
//
//        // > Update label
//        String newLabel;
//        if (this.originalLabel != null)
//            newLabel = namespace.label() + " " + this.originalLabel;
//        else
//            newLabel = namespace.label();
//        this.setLabel(newLabel);
//
//        // > Reindex variable
//        State.removeVariable(previousName);
//        State.addVariable(this);
//    }
//
//
//
//    // ON UPDATE LISTENER
//    // -----------------------------------------------------------------------------------------
//
//    public interface OnUpdateListener
//    {
//        void onUpdate();
//    }
//
//
//}
