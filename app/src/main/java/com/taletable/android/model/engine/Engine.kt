
package com.taletable.android.model.engine


import com.taletable.android.app.AppEff
import com.taletable.android.app.AppEngineError
import com.taletable.android.app.AppError
import com.taletable.android.db.DB_EngineValue
import com.taletable.android.db.engineTable
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.RowValue7
import com.taletable.android.lib.orm.SumType
import com.taletable.android.lib.orm.schema.CollValue
import com.taletable.android.lib.orm.schema.PrimValue
import com.taletable.android.lib.orm.sql.*
import com.taletable.android.model.engine.dice.DiceRoll
import com.taletable.android.model.engine.function.Function
import com.taletable.android.model.engine.function.FunctionId
import com.taletable.android.model.engine.mechanic.Mechanic
import com.taletable.android.model.engine.mechanic.MechanicCategory
import com.taletable.android.model.engine.mechanic.MechanicCategoryReference
import com.taletable.android.model.engine.mechanic.MechanicId
import com.taletable.android.model.engine.procedure.Procedure
import com.taletable.android.model.engine.procedure.ProcedureId
import com.taletable.android.model.engine.program.Program
import com.taletable.android.model.engine.program.ProgramId
import com.taletable.android.model.engine.reference.TextReference
import com.taletable.android.model.engine.summation.Summation
import com.taletable.android.model.engine.summation.SummationId
import com.taletable.android.model.engine.task.Task
import com.taletable.android.model.engine.value.*
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.engine.*
import com.taletable.android.rts.entity.sheet.SheetData
import com.taletable.android.util.Util
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*



/**
 * Engine
 */
data class Engine(override val id : UUID,
                  val valueSets : List<ValueSet>,
                  val mechanics : List<Mechanic>,
                  val mechanicCategories : List<MechanicCategory>,
                  val functions : List<Function>,
                  val programs : List<Program>,
                  val summations : List<Summation>,
                  val procedures : List<Procedure>,
                  val tasks : List<Task>)
                   : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    private val valueSetById : MutableMap<ValueSetId,ValueSet> =
                                            valueSets.associateBy { it.valueSetId() }
                                                as MutableMap<ValueSetId, ValueSet>


    private val mechanicsByCategoryId
                : MutableMap<MechanicCategoryReference,MutableSet<Mechanic>> = mutableMapOf()


    private val mechanicCategoryById : MutableMap<MechanicCategoryReference,MechanicCategory> =
                                    mechanicCategories.associateBy { it.categoryId() }
                                            as MutableMap<MechanicCategoryReference,MechanicCategory>

    private val mechanicById : MutableMap<MechanicId,Mechanic> =
                                        mechanics.associateBy { it.mechanicId() }
                                                as MutableMap<MechanicId,Mechanic>

    private val programById : MutableMap<ProgramId,Program> =
                                            programs.associateBy { it.programId() }
                                                    as MutableMap<ProgramId,Program>


    private val functionById : MutableMap<FunctionId,Function> =
                                            functions.associateBy { it.functionId() }
                                                    as MutableMap<FunctionId,Function>


    private val summationById : MutableMap<SummationId,Summation> =
                                            summations.associateBy { it.summationId() }
                                                    as MutableMap<SummationId,Summation>

    private val procedureById : MutableMap<ProcedureId,Procedure> =
                                            procedures.associateBy { it.procedureId() }
                                                    as MutableMap<ProcedureId,Procedure>


    init
    {
        this.mechanics.forEach {
            if (!mechanicsByCategoryId.containsKey(it.categoryId()))
                mechanicsByCategoryId.put(it.categoryId(), mutableSetOf())
            val mechanicsInCategorySet = mechanicsByCategoryId[it.categoryId()]
            mechanicsInCategorySet?.add(it)
        }

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<Engine> = when (doc)
        {
            is DocDict ->
            {
                apply(::Engine,
                      // ID
                      effValue(UUID.randomUUID()),
                      // Value Sets
                      doc.list("value_sets") apply {
                          it.map { ValueSet.fromDocument(it) }
                      },
                      // Mechanics
                      doc.list("mechanics") apply {
                          it.map { Mechanic.fromDocument(it) }
                      },
                      // Mechanic Categories
                      doc.list("mechanic_categories") apply {
                          it.map { MechanicCategory.fromDocument(it) }
                      },
                      // Functions
                      doc.list("functions") apply {
                          it.map { Function.fromDocument(it) }
                      },
                      // Programs
                      doc.list("programs") apply {
                          it.map { Program.fromDocument(it) }
                      },
                      // Summations
                      doc.list("summations") apply {
                          it.map { Summation.fromDocument(it) } },
                      // Procedures
                      doc.list("procedures") apply {
                          it.map { Procedure.fromDocument(it) } },
                      // Tasks
                      doc.list("tasks") apply {
                          it.map { Task.fromDocument(it) } }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "value_sets" to DocList(this.valueSets.map { it.toDocument() }),
        "mechanics" to DocList(this.mechanics.map { it.toDocument() }),
        "mechanic_categories" to DocList(this.mechanicCategories.map { it.toDocument() }),
        "functions" to DocList(this.functions.map { it.toDocument() }),
        "programs" to DocList(this.programs.map { it.toDocument() }),
        "summations" to DocList(this.summations.map { it.toDocument() }),
        "procedures" to DocList(this.procedures.map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_EngineValue =
        RowValue7(engineTable, CollValue(this.valueSets),
                               CollValue(this.mechanics),
                               CollValue(this.mechanicCategories),
                               CollValue(this.functions),
                               CollValue(this.programs),
                               CollValue(this.summations),
                               CollValue(this.procedures))


    // -----------------------------------------------------------------------------------------
    // ENGINE DATA
    // -----------------------------------------------------------------------------------------

    // Engine Data > Value Sets
    // -----------------------------------------------------------------------------------------

    fun valueSets() : List<ValueSet> = this.valueSets


    fun valueSet(valueSetId : ValueSetId) : AppEff<ValueSet> =
            note(this.valueSetById[valueSetId],
                 AppEngineError(ValueSetDoesNotExist(valueSetId)))


    fun valueSet(valueSetIdReference : TextReference, entityId : EntityId) : AppEff<ValueSet>
    {
        val error : AppError = AppEngineError(TextReferenceIsNull(valueSetIdReference))

        return SheetData.text(valueSetIdReference, entityId) ap { mValueSetId ->
               note(mValueSetId.toNullable(), error)         ap { valueSetId ->
               this.valueSet(ValueSetId(valueSetId))
               } }
    }


    fun baseValueSet(valueSetId : ValueSetId) : AppEff<ValueSetBase> =
        this.valueSet(valueSetId) ap {
            when (it) {
                is ValueSetBase -> effValue(it)
                else            -> effError<AppError,ValueSetBase>(
                                            AppEngineError(ValueSetIsNotBase(valueSetId)))
            }
        }


    // Engine Data > Values
    // -----------------------------------------------------------------------------------------


    fun value(valueReference : ValueReference, entityId : EntityId) : AppEff<Value>
    {
        val valueSetIdError : AppError = AppEngineError(TextReferenceIsNull(valueReference.valueSetId))
        val valueIdError : AppError = AppEngineError(TextReferenceIsNull(valueReference.valueId))

        return SheetData.text(valueReference.valueSetId, entityId) ap { mValueSetId ->
               note(mValueSetId.toNullable(), valueSetIdError)         ap { valueSetId ->
               SheetData.text(valueReference.valueId, entityId)    ap { mValueId ->
               note(mValueId.toNullable(), valueIdError)               ap { valueId ->
               this.valueSet(ValueSetId(valueSetId))                   ap { valueSet ->
               valueSet.value(ValueId(valueId), entityId)
               } } } } }

    }


    fun numberValue(valueReference : ValueReference, entityId : EntityId) : AppEff<ValueNumber>
    {
        val valueSetIdError : AppError = AppEngineError(TextReferenceIsNull(valueReference.valueSetId))
        val valueIdError : AppError = AppEngineError(TextReferenceIsNull(valueReference.valueId))

        return SheetData.text(valueReference.valueSetId, entityId) ap { mValueSetId ->
               note(mValueSetId.toNullable(), valueSetIdError)         ap { valueSetId ->
               SheetData.text(valueReference.valueId, entityId)    ap { mValueId ->
               note(mValueId.toNullable(), valueIdError)               ap { valueId ->
               this.valueSet(ValueSetId(valueSetId))                   ap { valueSet ->
               valueSet.numberValue(ValueId(valueId), entityId)
               } } } } }
    }


    fun textValue(valueReference : ValueReference, entityId : EntityId) : AppEff<ValueText>
    {
        val valueSetIdError : AppError = AppEngineError(TextReferenceIsNull(valueReference.valueSetId))
        val valueIdError : AppError = AppEngineError(TextReferenceIsNull(valueReference.valueId))

        return SheetData.text(valueReference.valueSetId, entityId) ap { mValueSetId ->
               note(mValueSetId.toNullable(), valueSetIdError)         ap { valueSetId ->
               SheetData.text(valueReference.valueId, entityId)    ap { mValueId ->
               note(mValueId.toNullable(), valueIdError)               ap { valueId ->
               this.valueSet(ValueSetId(valueSetId))                   ap { valueSet ->
               valueSet.textValue(ValueId(valueId), entityId)
               } } } } }
    }


    // Engine Data > Functions
    // -----------------------------------------------------------------------------------------

    fun functions() : List<Function> = this.functions


    fun function(functionId : FunctionId) : AppEff<Function> =
            note(this.functionById[functionId],
                    AppEngineError(FunctionDoesNotExist(functionId)))


    // Engine Data > Programs
    // -----------------------------------------------------------------------------------------

    fun programs() : List<Program> = this.programs


    fun program(programId : ProgramId) : AppEff<Program> =
            note(this.programById[programId],
                 AppEngineError(ProgramDoesNotExist(programId)))


    // Engine Data > Mechanics
    // -----------------------------------------------------------------------------------------

    fun mechanics() : List<Mechanic> = this.mechanics


    fun mechanic(mechanicId : MechanicId) : AppEff<Mechanic> =
            note(this.mechanicById[mechanicId],
                 AppEngineError(MechanicDoesNotExist(mechanicId)))


    fun mechanicsInCategory(categoryId : MechanicCategoryReference) : Set<Mechanic> =
        this.mechanicsByCategoryId[categoryId] ?: setOf()


    // Engine Data > Mechanic Categories
    // -----------------------------------------------------------------------------------------

    fun mechanicCategories() : List<MechanicCategory> = this.mechanicCategories


    fun mechanicCategory(categoryId : MechanicCategoryReference) : AppEff<MechanicCategory> =
            note(this.mechanicCategoryById[categoryId],
                 AppEngineError(MechanicCategoryDoesNotExist(categoryId)))



    // Engine Data > Summations
    // -----------------------------------------------------------------------------------------

    fun summations() : List<Summation> = this.summations


    fun summation(summationId : SummationId) : AppEff<Summation> =
            note(this.summationById[summationId],
                    AppEngineError(SummationDoesNotExist(summationId)))


    // Engine Data > Tasks
    // -----------------------------------------------------------------------------------------

    fun tasks() : List<Task> = this.tasks


    // Engine Data > Procedures
    // -----------------------------------------------------------------------------------------

    fun procedures() : List<Procedure> = this.procedures


    fun procedure(procedureId : ProcedureId) : AppEff<Procedure> =
            note(this.procedureById[procedureId],
                    AppEngineError(ProcedureDoesNotExist(procedureId)))

}


/**
 * Engine Value Type
 */
sealed class EngineValueType : ToDocument, SQLSerializable, Serializable
{

    object Number : EngineValueType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"number"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("number")

    }


    object Text : EngineValueType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"text"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("text")

    }


    object Boolean : EngineValueType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"boolean"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("boolean")

    }


    object DiceRoll : EngineValueType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"dice_roll"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("dice_roll")

    }


    object ListText : EngineValueType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"list_text"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("list_text")

    }


    companion object
    {
        fun fromDocument(doc: SchemaDoc) : ValueParser<EngineValueType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "number"    -> effValue<ValueError,EngineValueType>(EngineValueType.Number)
                "text"      -> effValue<ValueError,EngineValueType>(EngineValueType.Text)
                "boolean"   -> effValue<ValueError,EngineValueType>(EngineValueType.Boolean)
                "dice_roll" -> effValue<ValueError,EngineValueType>(EngineValueType.DiceRoll)
                "list_text" -> effValue<ValueError,EngineValueType>(EngineValueType.ListText)
                else        -> effError<ValueError,EngineValueType>(
                                    UnexpectedValue("EngineValueType", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    override fun toString(): String = when (this)
    {
        is Number   -> "Number"
        is Text     -> "Text"
        is Boolean  -> "Boolean"
        is DiceRoll -> "Dice Roll"
        is ListText -> "ListText"
    }

}


/**
 * Engine Value
 */
@Suppress("UNCHECKED_CAST")
sealed class EngineValue : ToDocument, SumType, Serializable
{

    companion object : Factory<EngineValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<EngineValue> =
            when (doc.case())
            {
                "engine_value_number"  -> EngineValueNumber.fromDocument(doc)
                                            as ValueParser<EngineValue>
                "engine_value_text"    -> EngineValueText.fromDocument(doc)
                                            as ValueParser<EngineValue>
                "engine_value_boolean" -> EngineValueBoolean.fromDocument(doc)
                                            as ValueParser<EngineValue>
                "dice_roll"            -> EngineValueDiceRoll.fromDocument(doc)
                                            as ValueParser<EngineValue>
                "list_text"            -> EngineTextListValue.fromDocument(doc)
                                            as ValueParser<EngineValue>
                else                   -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    abstract fun type() : EngineValueType

}

/**
 * Engine Number Value
 */
data class EngineValueNumber(val value : Double) : EngineValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EngineValueNumber>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<EngineValueNumber> = when (doc)
        {
            is DocNumber -> effValue(EngineValueNumber(doc.number))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value).withCase("engine_value_number")


    // -----------------------------------------------------------------------------------------
    // ENGINE VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() = EngineValueType.Number


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value})


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "number"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString() = Util.doubleString(this.value)

}

/**
 * Engine Text Value
 */
data class EngineValueText(val value : String) : EngineValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EngineValueText>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<EngineValueText> = when (doc)
        {
            is DocText -> effValue(EngineValueText(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value).withCase("engine_value_text")


    // -----------------------------------------------------------------------------------------
    // ENGINE VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() = EngineValueType.Text


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "text"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString() = this.value.toString()

}


/**
 * Engine Boolean Value
 */
data class EngineValueBoolean(val value : Boolean) : EngineValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EngineValueBoolean>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<EngineValueBoolean> = when (doc)
        {
            is DocBoolean -> effValue(EngineValueBoolean(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocBoolean(this.value).withCase("engine_value_boolean")


    // -----------------------------------------------------------------------------------------
    // ENGINE VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() = EngineValueType.Boolean


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if (this.value) 1 else 0 })


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "boolean"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString() = this.value.toString()

}


/**
 * Engine Dice Roll Value
 */
data class EngineValueDiceRoll(val value : DiceRoll) : EngineValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EngineValueDiceRoll>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<EngineValueDiceRoll> =
                apply(::EngineValueDiceRoll, DiceRoll.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.value.toDocument().withCase("dice_roll")


    // -----------------------------------------------------------------------------------------
    // ENGINE VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() = EngineValueType.DiceRoll


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "dice_roll"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this) })


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString() = this.value.toString()

}


/**
 * Engine Text List Value
 */
data class EngineTextListValue(val value : List<String>) : EngineValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EngineTextListValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<EngineTextListValue> = when (doc)
        {
            is DocDict -> doc.list("value") ap {
                              effApply(::EngineTextListValue, it.stringList())
                          }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
            "value" to DocList(this.value.map { DocText(it) })
    )).withCase("list_text")


    // -----------------------------------------------------------------------------------------
    // ENGINE VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() = EngineValueType.ListText


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this) })


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "list_text"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString() = this.value.toString()

}


/**
 * Engine Number List Value
 */
data class EngineNumberListValue(val value : List<Double>) : EngineValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EngineNumberListValue>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<EngineNumberListValue> = when (doc)
        {
            is DocDict -> doc.list("value") ap {
                              effApply(::EngineNumberListValue, it.doubleList())
                          }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
            "value" to DocList(this.value.map { DocNumber(it) })
    )).withCase("list_number")


    // -----------------------------------------------------------------------------------------
    // ENGINE VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() = EngineValueType.ListText


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this) })


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "list_text"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString() = this.value.toString()

}

