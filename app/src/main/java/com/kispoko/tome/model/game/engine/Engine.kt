
package com.kispoko.tome.model.game.engine


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Coll
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.function.Function
import com.kispoko.tome.model.game.engine.mechanic.Mechanic
import com.kispoko.tome.model.game.engine.program.Program
import com.kispoko.tome.model.game.engine.value.*
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.util.*



/**
 * Engine
 */
data class Engine(override val id : UUID,
                  val valueSets : Coll<ValueSet>,
                  val mechanics : Coll<Mechanic>,
                  val functions : Coll<Function>,
                  val programs : Coll<Program>) : Model, EngineData
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private val valueSetById : MutableMap<ValueSetId,ValueSet> =
                                            valueSets.list.associateBy { it.valueSetId.value }
                                                as MutableMap<ValueSetId, ValueSet>


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc: SpecDoc) : ValueParser<Engine> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Engine,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Value Sets
                         doc.list("value_sets") apply {
                             effApply(::Coll, it.map { ValueSet.fromDocument(it) })
                         },
                         // Mechanics
                         doc.list("mechanics") apply {
                             effApply(::Coll, it.map { Mechanic.fromDocument(it) })
                         },
                         // Functions
                         doc.list("functions") apply {
                             effApply(::Coll, it.map { Function.fromDocument(it) })
                         },
                         // Programs
                         doc.list("programs") apply {
                             effApply(::Coll, it.map { Program.fromDocument(it) })
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // ENGINE DATA
    // -----------------------------------------------------------------------------------------

    override fun valueSet(valueSetId : ValueSetId) : ValueSet? = this.valueSetById[valueSetId]


    override fun textValue(valueReference : ValueReference) : ValueText? =
        this.valueSet(valueReference.valueSetId.value)
            ?.textValue(valueReference.valueId.value, this)

}


/**
 * Engine Data
 */
interface EngineData
{

    fun valueSet(valueSetId : ValueSetId) : ValueSet?

    fun textValue(valueReference : ValueReference) : Value?

}



/**
 * Engine Value Type
 */
enum class EngineValueType
{
    NUMBER,
    TEXT,
    BOOLEAN,
    DICE_ROLL,
    LIST_TEXT;
}


/**
 * Engine Value
 */
@Suppress("UNCHECKED_CAST")
sealed class EngineValue
{

    companion object : Factory<EngineValue>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<EngineValue> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "number"    -> EngineNumberValue.fromDocument(doc)
                                    as ValueParser<EngineValue>
                    "text"      -> EngineTextValue.fromDocument(doc)
                                    as ValueParser<EngineValue>
                    "boolean"   -> EngineBooleanValue.fromDocument(doc)
                                    as ValueParser<EngineValue>
                    "dice_roll" -> EngineDiceRollValue.fromDocument(doc)
                                    as ValueParser<EngineValue>
                    "list_text" -> EngineTextListValue.fromDocument(doc)
                                    as ValueParser<EngineValue>
                    else        -> effError<ValueError,EngineValue>(
                                    UnknownCase(doc.case(), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

}

/**
 * Engine Number Value
 */
data class EngineNumberValue(val value : Double) : EngineValue()
{

    companion object : Factory<EngineNumberValue>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<EngineNumberValue> = when (doc)
        {
            is DocDict -> effApply(::EngineNumberValue, doc.double("value"))
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

}

/**
 * Engine Text Value
 */
data class EngineTextValue(val value : String) : EngineValue()
{

    companion object : Factory<EngineTextValue>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<EngineTextValue> = when (doc)
        {
            is DocDict -> effApply(::EngineTextValue, doc.text("value"))
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

}

/**
 * Engine Boolean Value
 */
data class EngineBooleanValue(val value : Boolean) : EngineValue()
{

    companion object : Factory<EngineBooleanValue>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<EngineBooleanValue> = when (doc)
        {
            is DocDict -> effApply(::EngineBooleanValue, doc.boolean("value"))
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

}

/**
 * Engine Dice Roll Value
 */
data class EngineDiceRollValue(val value : DiceRoll) : EngineValue()
{

    companion object : Factory<EngineDiceRollValue>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<EngineDiceRollValue> = when (doc)
        {
            is DocDict -> doc.at("value") ap {
                              effApply(::EngineDiceRollValue, DiceRoll.Companion.fromDocument(it))
                          }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

}

/**
 * Engine Text List Value
 */
data class EngineTextListValue(val value : List<String>) : EngineValue()
{

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

