
package com.kispoko.tome.model.game.engine


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppEngineError
import com.kispoko.tome.app.AppError
import com.kispoko.tome.db.DB_EngineValue
import com.kispoko.tome.db.engineTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue6
import com.kispoko.tome.lib.orm.RowValue7
import com.kispoko.tome.lib.orm.SumType
import com.kispoko.tome.lib.orm.schema.CollValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.function.Function
import com.kispoko.tome.model.game.engine.function.FunctionId
import com.kispoko.tome.model.game.engine.mechanic.Mechanic
import com.kispoko.tome.model.game.engine.mechanic.MechanicCategory
import com.kispoko.tome.model.game.engine.mechanic.MechanicCategoryId
import com.kispoko.tome.model.game.engine.procedure.Procedure
import com.kispoko.tome.model.game.engine.procedure.ProcedureId
import com.kispoko.tome.model.game.engine.program.Program
import com.kispoko.tome.model.game.engine.program.ProgramId
import com.kispoko.tome.model.game.engine.summation.Summation
import com.kispoko.tome.model.game.engine.summation.SummationId
import com.kispoko.tome.model.game.engine.value.*
import com.kispoko.tome.rts.game.engine.*
import com.kispoko.tome.rts.sheet.SheetContext
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
                  val procedures : List<Procedure>)
                   : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    private val valueSetById : MutableMap<ValueSetId,ValueSet> =
                                            valueSets.associateBy { it.valueSetId() }
                                                as MutableMap<ValueSetId, ValueSet>


    private val mechanicsByCategoryId
                : MutableMap<MechanicCategoryId,MutableSet<Mechanic>> = mutableMapOf()


    private val mechanicCategoryById : MutableMap<MechanicCategoryId,MechanicCategory> =
                                    mechanicCategories.associateBy { it.categoryId() }
                                            as MutableMap<MechanicCategoryId,MechanicCategory>


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
                          it.map { Procedure.fromDocument(it) } }
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


    fun baseValueSet(valueSetId : ValueSetId) : AppEff<ValueSetBase> =
        this.valueSet(valueSetId) ap {
            when (it) {
                is ValueSetBase -> effValue(it)
                else            -> effError<AppError,ValueSetBase>(
                                            AppEngineError(ValueSetIsNotBase(valueSetId)))
            }
        }


//    fun removeValueSet(valueSetId : ValueSetId) : Boolean
//    {
//        val newValueSets : MutableSet<ValueSet> = mutableSetOf()
//
//        this.valueSets().forEach {
//            if (it.valueSetId() != valueSetId)
//                newValueSets.add(it)
//        }
//
//        val removedSet = newValueSets.size != this.valueSets().size
//
//        this.valueSets.set.clear()
//
//        newValueSets.forEach {
//            this.valueSets.set.add(it)
//        }
//
//        return removedSet
//    }


//    fun updateValueSet(updatedValueSet : ValueSet)
//    {
//        Log.d("***ENGINE", "called update value set")
//        val removed = this.removeValueSet(updatedValueSet.valueSetId())
//        if (removed) {
//            this.valueSets.set.add(updatedValueSet)
//            Log.d("***ENGINE", "updated value set")
//            Log.d("***ENGINE", updatedValueSet.toString())
//        }
//
//    }

//
//    fun addValueSet(newValueSet : ValueSet)
//    {
//        this.valueSets.set.add(newValueSet)
//    }


    // Engine Data > Values
    // -----------------------------------------------------------------------------------------


    fun value(valueReference : ValueReference, gameId : GameId) : AppEff<Value> =
            this.valueSet(valueReference.valueSetId)
                    .apply { it.value(valueReference.valueId, gameId) }


    fun textValue(valueReference : ValueReference, sheetContext : SheetContext) : AppEff<ValueText> =
        this.valueSet(valueReference.valueSetId)
                .apply { it.textValue(valueReference.valueId, sheetContext) }


    fun numberValue(valueReference : ValueReference,
                    sheetContext : SheetContext) : AppEff<ValueNumber> =
        this.valueSet(valueReference.valueSetId)
                .apply { it.numberValue(valueReference.valueId, sheetContext) }


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


    fun mechanicsInCategory(categoryId : MechanicCategoryId) : Set<Mechanic> =
        this.mechanicsByCategoryId[categoryId] ?: setOf()


    // Engine Data > Mechanic Categories
    // -----------------------------------------------------------------------------------------

    fun mechanicCategories() : List<MechanicCategory> = this.mechanicCategories


    fun mechanicCategoryWithId(categoryId : MechanicCategoryId) : MechanicCategory? =
            this.mechanicCategoryById[categoryId]


    // Engine Data > Summations
    // -----------------------------------------------------------------------------------------

    fun summations() : List<Summation> = this.summations


    fun summation(summationid : SummationId) : AppEff<Summation> =
            note(this.summationById[summationid],
                    AppEngineError(SummationDoesNotExist(summationid)))


    // Engine Data > Procedures
    // -----------------------------------------------------------------------------------------

    fun procedures() : List<Procedure> = this.procedures


    fun procedureWithId(procedureId : ProcedureId) : AppEff<Procedure> =
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

}

