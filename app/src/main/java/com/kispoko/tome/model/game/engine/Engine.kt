
package com.kispoko.tome.model.game.engine


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppEngineError
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Coll
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.model.SumModel
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.function.Function
import com.kispoko.tome.model.game.engine.function.FunctionId
import com.kispoko.tome.model.game.engine.mechanic.Mechanic
import com.kispoko.tome.model.game.engine.program.Program
import com.kispoko.tome.model.game.engine.program.ProgramId
import com.kispoko.tome.model.game.engine.value.*
import com.kispoko.tome.rts.game.engine.FunctionDoesNotExist
import com.kispoko.tome.rts.game.engine.ProgramDoesNotExist
import com.kispoko.tome.rts.game.engine.ValueSetDoesNotExist
import effect.effApply
import effect.effError
import effect.effValue
import effect.note
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
                  private val valueSets : Coll<ValueSet>,
                  private val mechanics : Coll<Mechanic>,
                  private val functions : Coll<Function>,
                  private val programs : Coll<Program>,
                  val gameId : GameId) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private val valueSetById : MutableMap<ValueSetId,ValueSet> =
                                            valueSets.list.associateBy { it.valueSetId.value }
                                                as MutableMap<ValueSetId, ValueSet>


    private val programById : MutableMap<ProgramId,Program> =
                                            programs.list.associateBy { it.programId() }
                                                    as MutableMap<ProgramId,Program>


    private val functionById : MutableMap<FunctionId,Function> =
                                            functions.list.associateBy { it.functionId() }
                                                    as MutableMap<FunctionId,Function>

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.valueSets.name     = "value_sets"
        this.mechanics.name     = "mechanics"
        this.functions.name     = "functions"
        this.programs.name      = "programs"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc: SpecDoc, gameId : GameId) : ValueParser<Engine> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Engine,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Value Sets
                         doc.list("value_sets") apply {
                             effApply(::Coll, it.mapMut { ValueSet.fromDocument(it) })
                         },
                         // Mechanics
                         doc.list("mechanics") apply {
                             effApply(::Coll, it.mapMut { Mechanic.fromDocument(it) })
                         },
                         // Functions
                         doc.list("functions") apply {
                             effApply(::Coll, it.mapMut { Function.fromDocument(it) })
                         },
                         // Programs
                         doc.list("programs") apply {
                             effApply(::Coll, it.mapMut { Program.fromDocument(it) })
                         },
                         effValue(gameId)
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "engine"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // ENGINE DATA
    // -----------------------------------------------------------------------------------------

    // Engine Data > Values
    // -----------------------------------------------------------------------------------------

    fun valueSet(valueSetId : ValueSetId) : AppEff<ValueSet> =
            note(this.valueSetById[valueSetId],
                 AppEngineError(ValueSetDoesNotExist(this.gameId, valueSetId)))


    fun textValue(valueReference : ValueReference) : AppEff<ValueText> =
        this.valueSet(valueReference.valueSetId)
                .apply { it.textValue(valueReference.valueId, this) }


    fun numberValue(valueReference : ValueReference) : AppEff<ValueNumber> =
            this.valueSet(valueReference.valueSetId)
                    .apply { it.numberValue(valueReference.valueId, this) }


    // Engine Data > Functions
    // -----------------------------------------------------------------------------------------

    fun function(functionId : FunctionId) : AppEff<Function> =
            note(this.functionById[functionId],
                    AppEngineError(FunctionDoesNotExist(functionId)))


    // Engine Data > Programs
    // -----------------------------------------------------------------------------------------

    fun program(programId : ProgramId) : AppEff<Program> =
            note(this.programById[programId],
                 AppEngineError(ProgramDoesNotExist(programId)))

}


/**
 * Engine Value Type
 */
sealed class EngineValueType : SQLSerializable, Serializable
{

    object NUMBER : EngineValueType()
    {
        override fun asSQLValue() : SQLValue = SQLText({"number"})
    }


    object TEXT : EngineValueType()
    {
        override fun asSQLValue() : SQLValue = SQLText({"text"})
    }


    object BOOLEAN : EngineValueType()
    {
        override fun asSQLValue() : SQLValue = SQLText({"boolean"})
    }


    object DICE_ROLL : EngineValueType()
    {
        override fun asSQLValue() : SQLValue = SQLText({"dice_roll"})
    }


    object LIST_TEXT : EngineValueType()
    {
        override fun asSQLValue() : SQLValue = SQLText({"list_text"})
    }


    companion object
    {
        fun fromDocument(doc : SpecDoc) : ValueParser<EngineValueType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "number"    -> effValue<ValueError,EngineValueType>(EngineValueType.NUMBER)
                "text"      -> effValue<ValueError,EngineValueType>(EngineValueType.TEXT)
                "boolean"   -> effValue<ValueError,EngineValueType>(EngineValueType.BOOLEAN)
                "dice_roll" -> effValue<ValueError,EngineValueType>(EngineValueType.DICE_ROLL)
                "list_text" -> effValue<ValueError,EngineValueType>(EngineValueType.LIST_TEXT)
                else        -> effError<ValueError,EngineValueType>(
                                    UnexpectedValue("EngineValueType", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Engine Value
 */
@Suppress("UNCHECKED_CAST")
sealed class EngineValue : SumModel, Serializable
{

    companion object : Factory<EngineValue>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<EngineValue> =
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
                else                   -> effError<ValueError,EngineValue>(
                                            UnknownCase(doc.case(), doc.path))
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
        override fun fromDocument(doc : SpecDoc) : ValueParser<EngineValueNumber> = when (doc)
        {
            is DocNumber -> effValue(EngineValueNumber(doc.number))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // ENGINE VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() = EngineValueType.NUMBER


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value})


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "number")

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
        override fun fromDocument(doc: SpecDoc): ValueParser<EngineValueText> = when (doc)
        {
            is DocText -> effValue(EngineValueText(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // ENGINE VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() = EngineValueType.TEXT


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "text")

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
        override fun fromDocument(doc: SpecDoc): ValueParser<EngineValueBoolean> = when (doc)
        {
            is DocBoolean -> effValue(EngineValueBoolean(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // ENGINE VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() = EngineValueType.BOOLEAN


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if (this.value) 1 else 0 })


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "boolean")

    override val sumModelObject = this

}


/**
 * Engine Dice Roll Value
 */
data class EngineValueDiceRoll(val value : DiceRoll) : EngineValue(), Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EngineValueDiceRoll>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<EngineValueDiceRoll> = when (doc)
        {
            is DocDict -> doc.at("value") ap {
                              effApply(::EngineValueDiceRoll, DiceRoll.Companion.fromDocument(it))
                          }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // ENGINE VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() = EngineValueType.DICE_ROLL


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() = this.value.onLoad()

    override val id = this.value.id

    override val name = this.value.name

    override val modelObject = this.value


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Comp(this, "dice_roll")

    override val sumModelObject = this

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
        override fun fromDocument(doc: SpecDoc): ValueParser<EngineTextListValue> = when (doc)
        {
            is DocDict -> doc.list("value") ap {
                              effApply(::EngineTextListValue, it.stringList())
                          }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // ENGINE VALUE
    // -----------------------------------------------------------------------------------------

    override fun type() = EngineValueType.LIST_TEXT


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this) })


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "list_text")

    override val sumModelObject = this

}




//
//
//
//
//    public Set<EngineActiveSearchResult> searchActive(String query)
//    {
//        Set<EngineActiveSearchResult> results = new HashSet<>();
//
//        // > Search for active variables
//        results.addAll(State.search(query));
//
//        // > Search for active mechanics
//        results.addAll(this.mechanicIndex().search(query));
//
//        return results;
//    }

