
package com.kispoko.tome.model.engine.variable


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.model.engine.dice.DiceRoll
import com.kispoko.tome.rts.entity.EntityId
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Dice Variable Value
 */
sealed class DiceRollVariableValue : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // COSNTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceRollVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DiceRollVariableValue> =
            when (doc.case())
            {
                "dice_roll" -> DiceRollVariableLiteralValue.fromDocument(doc)
                else        -> effError(UnknownCase(doc.case(), doc.path))
            }

    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    open fun dependencies() : Set<VariableReference> = setOf()


    abstract fun value() : DiceRoll


    abstract fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>>

}


/**
 * Literal Value
 */
data class DiceRollVariableLiteralValue(val diceRoll : DiceRoll) : DiceRollVariableValue()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceRollVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DiceRollVariableValue> =
                effApply(::DiceRollVariableLiteralValue, DiceRoll.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.diceRoll.toDocument().withCase("dice_roll")


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value() : DiceRoll = this.diceRoll


    override fun companionVariables(entityId : EntityId) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM TYPE
    // -----------------------------------------------------------------------------------------

//    override val sumModelObject = this
//
//
//    override fun case() = "dice_roll"
//
//
//    override fun columnValue() = ProdValue(this.diceRoll)

}
