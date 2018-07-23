package com.taletable.android.model.engine


import com.taletable.android.lib.Factory
import com.taletable.android.model.engine.dice.DiceQuantity
import com.taletable.android.model.engine.dice.DiceRoll
import com.taletable.android.model.engine.dice.DiceRollQuantity
import com.taletable.android.model.engine.variable.VariableReference
import com.taletable.android.rts.entity.EntityId
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Formula Modifier
 */
sealed class FormulaModifier : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<FormulaModifier>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<FormulaModifier> =
            when (doc.case())
            {
                "multiply_dice" -> FormulaModifierMultiplyDice.fromDocument(doc.nextCase())
                else            -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    open fun dependencies(entityId : EntityId) : Set<VariableReference> = setOf()

}


/**
 * Formula Modifier: Multply Dice
 */
data class FormulaModifierMultiplyDice(val factor : Int) : FormulaModifier()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<FormulaModifier>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<FormulaModifier> = when (doc)
        {
            is DocNumber -> effValue(FormulaModifierMultiplyDice(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.factor.toDouble()).withCase("multiply_dice")


    // -----------------------------------------------------------------------------------------
    // APPLY
    // -----------------------------------------------------------------------------------------

    fun apply(diceRoll : DiceRoll) : DiceRoll
    {
        val newQuantities : MutableList<DiceQuantity> = mutableListOf()
        diceRoll.quantities.forEach {
            val newRollQuantity = DiceRollQuantity(it.quantity().value * 2)
            val newQuantity = DiceQuantity(it.sides(), newRollQuantity)
            newQuantities.add(newQuantity)
        }

        return DiceRoll(newQuantities, diceRoll.modifiers(), diceRoll.formulaModifiers())
    }
}

