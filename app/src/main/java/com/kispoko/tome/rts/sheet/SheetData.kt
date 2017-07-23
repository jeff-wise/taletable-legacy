
package com.kispoko.tome.rts.sheet


import android.util.Log
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.model.game.engine.EngineValue
import com.kispoko.tome.model.game.engine.EngineValueBoolean
import com.kispoko.tome.model.game.engine.EngineValueDiceRoll
import com.kispoko.tome.model.game.engine.EngineValueNumber
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.reference.*
import com.kispoko.tome.model.game.engine.summation.Summation
import com.kispoko.tome.model.game.engine.variable.NumberVariableSummationValue
import com.kispoko.tome.rts.game.GameManager
import effect.effApply
import effect.effValue
import effect.mapM


/**
 * Sheet Data
 */
object SheetData
{

    // -----------------------------------------------------------------------------------------
    // REFERENCES
    // -----------------------------------------------------------------------------------------


    fun referenceEngineValue(reference : DataReference,
                             sheetContext : SheetContext) : AppEff<EngineValue> =
        when (reference)
        {
            is DataReferenceBoolean ->
                effApply(::EngineValueBoolean, this.boolean(sheetContext, reference.reference))
            is DataReferenceDiceRoll ->
                effApply(::EngineValueDiceRoll, this.diceRoll(sheetContext, reference.reference))
            is DataReferenceNumber ->
                effApply(::EngineValueNumber, this.number(sheetContext, reference.reference))
        }


    /**
     * Resolve a boolean reference.
     */
    fun boolean(sheetContext : SheetContext,
                reference : BooleanReference) : AppEff<Boolean> =
        when (reference)
        {
            is BooleanReferenceLiteral  -> effValue(reference.value)
            is BooleanReferenceVariable -> SheetManager.sheetState(sheetContext.sheetId)
                    .apply( { it.booleanVariable(reference.variableReference)})
                    .apply( { it.value() })

        }


    /**
     * Resolve a dice roll reference.
     */
    fun diceRoll(sheetContext : SheetContext,
                 reference : DiceRollReference) : AppEff<DiceRoll> =
        when (reference)
        {
            is DiceRollReferenceLiteral -> effValue(reference.value)
            is DiceRollReferenceVariable -> SheetManager.sheetState(sheetContext.sheetId)
                    .apply( { it.diceRollVariable(reference.variableReference)})
                    .apply( { effValue<AppError, DiceRoll>(it.value()) })

        }


    /**
     * Resolve a number reference.
     */
    fun number(sheetContext : SheetContext,
               numberReference : NumberReference) : AppEff<Double> =
        when (numberReference)
        {
            is NumberReferenceLiteral -> effValue(numberReference.value)
            is NumberReferenceValue    ->
                    GameManager.engine(sheetContext.gameId)
                        .apply({ it.numberValue(numberReference.valueReference, sheetContext) })
                        .apply({ effValue<AppError,Double>(it.value()) })
            is NumberReferenceVariable -> SheetManager.sheetState(sheetContext.sheetId)
                    .apply( { it.numberVariable(numberReference.variableReference)})
                    .apply( { it.value(sheetContext) })

        }


    fun numbers(sheetContext : SheetContext,
                numberReference : NumberReference) : AppEff<List<Double>> =
        when (numberReference)
        {
            is NumberReferenceLiteral -> effValue(listOf(numberReference.value))
            is NumberReferenceValue    ->
                    GameManager.engine(sheetContext.gameId)
                        .apply({ it.numberValue(numberReference.valueReference, sheetContext) })
                        .apply({ effValue<AppError,List<Double>>(listOf(it.value())) })
            is NumberReferenceVariable ->
            {
                SheetManager.sheetState(sheetContext.sheetId)
                        .apply( { it.numberVariables(numberReference.variableReference) })
                        .apply( { it.toList().mapM { it.value(sheetContext) } })
            }
        }


    /**
     * Resolve a text reference.
     */
    fun text(sheetContext : SheetContext,
             reference : TextReference) : AppEff<String> =
        when (reference)
        {
            is TextReferenceLiteral  -> effValue(reference.value)
            is TextReferenceValue    ->
                    GameManager.engine(sheetContext.gameId)
                        .apply({ it.textValue(reference.valueReference, sheetContext) })
                        .apply({ effValue<AppError,String>(it.value()) })
            is TextReferenceVariable -> SheetManager.sheetState(sheetContext.sheetId)
                    .apply( { it.textVariable(reference.variableReference)})
                    .apply( { it.value(sheetContext) })
        }
}
