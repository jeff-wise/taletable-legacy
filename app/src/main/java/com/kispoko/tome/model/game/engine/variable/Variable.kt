
package com.kispoko.tome.model.game.engine.variable


import android.util.Log
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.AppStateError
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.value.ValueId
import com.kispoko.tome.model.game.engine.value.ValueReference
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.VariableIsOfUnexpectedType
import com.kispoko.tome.util.Util
import effect.*
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

    open fun valueString(sheetContext : SheetContext)
                          : AppEff<String> = when (this)
    {
        is BooleanVariable  -> this.value() ap { effValue<AppError,String>(it.toString()) }
        is DiceRollVariable -> effValue(this.value().toString())
        is NumberVariable   -> this.valueString(sheetContext)
        is TextVariable     -> this.valueString(sheetContext)
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
                           private var variableValue : BooleanVariableValue)
                            : Variable()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : List<VariableTag>,
                value : BooleanVariableValue)
        : this(UUID.randomUUID(),
               variableId,
               label,
               description,
               tags.toMutableList(),
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


    fun variableValue() : BooleanVariableValue = this.variableValue


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override val prodTypeObject: ProdType = this


    override fun onLoad() {}


    override fun row() : DB_VariableBoolean = dbVariableBoolean(this.variableId,
                                                                this.label,
                                                                this.description,
                                                                this.tags,
                                                                this.variableValue)

    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun type(): VariableType = VariableType.BOOLEAN

    override fun dependencies(sheetContext : SheetContext) = this.variableValue().dependencies()

    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(sheetContext)


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value() : AppEff<Boolean> = this.variableValue().value()


    fun updateValue(value : Boolean, sheetId : SheetId)
    {
        when (this.variableValue())
        {
            is BooleanVariableLiteralValue -> {
                Log.d("***VARIABLE", "update boolean literal called")
                this.variableValue = BooleanVariableLiteralValue(value)
                SheetManager.sheetState(sheetId) apDo { it.onVariableUpdate(this) }
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
                            val variableValue: DiceRollVariableValue)
                            : Variable()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : List<VariableTag>,
                value : DiceRollVariableValue)
        : this(UUID.randomUUID(),
               variableId,
               label,
               description,
               tags.toMutableList(),
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


    fun variableValue() : DiceRollVariableValue = this.variableValue


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    override val prodTypeObject: ProdType = this


    override fun row() : DB_VariableDiceRoll =
            dbVariableDiceRoll(this.variableId,
                               this.label,
                               this.description,
                               this.tags,
                               this.variableValue)


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun dependencies(sheetContext : SheetContext) =
            this.variableValue.dependencies()


    override fun type(): VariableType = VariableType.DICE_ROLL


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(sheetContext)


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
                          var variableValue : NumberVariableValue,
                          val history : NumberVariableHistory)
                          : Variable()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : List<VariableTag>,
                value : NumberVariableValue)
        : this(UUID.randomUUID(),
               variableId,
               label,
               description,
               tags.toMutableList(),
               value,
               NumberVariableHistory())


    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : List<VariableTag>,
                value : NumberVariableValue,
                history : NumberVariableHistory)
        : this(UUID.randomUUID(),
               variableId,
               label,
               description,
               tags.toMutableList(),
               value,
               history)


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
                      // Value
                      doc.at("value") ap { NumberVariableValue.fromDocument(it) },
                      // History
                      split(doc.maybeAt("history"),
                            effValue(NumberVariableHistory()),
                            { NumberVariableHistory.fromDocument(it) })
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


    fun variableValue() : NumberVariableValue = this.variableValue


    fun history() : NumberVariableHistory = this.history


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    override val prodTypeObject : ProdType = this


    override fun row() : DB_VariableNumber = dbVariableNumber(this.variableId,
                                                              this.label,
                                                              this.description,
                                                              this.tags,
                                                              this.variableValue)


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun dependencies(sheetContext : SheetContext) : Set<VariableReference> =
            this.variableValue.dependencies(sheetContext)


    override fun type(): VariableType = VariableType.NUMBER


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(sheetContext)


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


    fun updateValue(value : Double, sheetId : SheetId)
    {
        when (this.variableValue())
        {
            is NumberVariableLiteralValue ->
            {
                this.variableValue = NumberVariableLiteralValue(value)
                this.history().append(this.variableValue())
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
                        var variableValue : TextVariableValue)
                        : Variable()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : List<VariableTag>,
                variableValue : TextVariableValue)
        : this(UUID.randomUUID(),
               variableId,
               label,
               description,
               tags.toMutableList(),
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


    fun variableValue() : TextVariableValue = this.variableValue


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    override val prodTypeObject: ProdType = this


    override fun row() : DB_VariableText = dbVariableText(this.variableId,
                                                          this.label,
                                                          this.description,
                                                          this.tags,
                                                          this.variableValue)


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun dependencies(sheetContext : SheetContext) = this.variableValue().dependencies()


    override fun type(): VariableType = VariableType.TEXT


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(sheetContext)


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


    fun updateLiteralValue(value : String, sheetId : SheetId)
    {
        when (this.variableValue())
        {
            is TextVariableLiteralValue ->
            {
                this.variableValue = TextVariableLiteralValue(value)
                SheetManager.onVariableUpdate(sheetId, this)
            }
        }
    }


    fun updateValue(valueId : ValueId, sheetId : SheetId)
    {
        val currentVariableValue = this.variableValue()
        when (currentVariableValue)
        {
            is TextVariableValueValue -> {
                val valueSetId = currentVariableValue.valueReference.valueSetId
                val newValueReference = ValueReference(valueSetId, valueId)
                this.variableValue = TextVariableValueValue(newValueReference)
                SheetManager.onVariableUpdate(sheetId, this)
            }
            is TextVariableValueUnknownValue -> {
                val valueSetId = currentVariableValue.valueSetId
                val newValueReference = ValueReference(valueSetId, valueId)
                this.variableValue = TextVariableValueValue(newValueReference)
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
    TEXT
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
data class VariableName(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<VariableName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<VariableName> = when (doc)
        {
            is DocText -> effValue(VariableName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

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
                "variable_id"      -> VariableId.fromDocument(doc) as ValueParser<VariableReference>
                "variable_tag"     -> VariableTag.fromDocument(doc) as ValueParser<VariableReference>
                "variable_context" -> VariableContext.fromDocument(doc) as ValueParser<VariableReference>
                else               -> effError(UnknownCase(doc.case(), doc.path))
            }
    }
}


/**
 * Variable Id
 */
data class VariableId(val namespace : Maybe<Prim<VariableNamespace>>,
                      val name : Prim<VariableName>) : VariableReference(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(name : String)
        : this(Nothing(), Prim(VariableName(name)))


    constructor(namespace : String, name : String)
            : this(Just(Prim(VariableNamespace(namespace))), Prim(VariableName(name)))


    constructor(namespace : Maybe<VariableNamespace>, name : VariableName)
            : this(maybeLiftPrim(namespace), Prim(name))


    constructor(namespace : VariableNamespace, name : VariableName)
            : this(Just(Prim(namespace)), Prim(name))


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

    fun nameString() : String = this.name.value.value

    fun namespaceString() : String? = getMaybePrim(this.namespace)?.value


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString(): String
    {
        var s = ""

        if (namespaceString() != null)
        {
            s += namespaceString()
            s += "::"
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
