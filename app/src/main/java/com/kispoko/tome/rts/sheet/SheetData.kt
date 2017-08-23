
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
import effect.*


/**
 * Sheet Data
 */
object SheetData
{

    // -----------------------------------------------------------------------------------------
    // REFERENCES
    // -----------------------------------------------------------------------------------------


    fun referenceEngineValue(reference : DataReference,
                             sheetContext : SheetContext) : AppEff<Maybe<EngineValue>> =
        when (reference)
        {
            is DataReferenceBoolean -> this.boolean(sheetContext, reference.reference) ap {
                effValue<AppError,Maybe<EngineValue>>(Just(EngineValueBoolean(it)))
            }
            is DataReferenceDiceRoll -> this.diceRoll(sheetContext, reference.reference) ap {
                effValue<AppError,Maybe<EngineValue>>(Just(EngineValueDiceRoll(it)))
            }
            is DataReferenceNumber -> this.number(sheetContext, reference.reference) ap {
                when (it) {
                    is Just -> effValue<AppError,Maybe<EngineValue>>(Just(EngineValueNumber(it.value)))
                    else    -> effValue<AppError,Maybe<EngineValue>>(Nothing())
                }
            }
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
               numberReference : NumberReference) : AppEff<Maybe<Double>> =
        when (numberReference)
        {
            is NumberReferenceLiteral -> effValue(Just(numberReference.value))
            is NumberReferenceValue    ->
                    GameManager.engine(sheetContext.gameId)
                        .apply({ it.numberValue(numberReference.valueReference, sheetContext) })
                        .apply({ effValue<AppError,Maybe<Double>>(Just(it.value())) })
            is NumberReferenceVariable -> SheetManager.sheetState(sheetContext.sheetId)
                    .apply( { it.numberVariable(numberReference.variableReference)})
                    .apply( { it.value(sheetContext) })

        }


    fun numbers(sheetContext : SheetContext,
                numberReference : NumberReference) : AppEff<List<Maybe<Double>>> =
        when (numberReference)
        {
            is NumberReferenceLiteral -> effValue(listOf(Just(numberReference.value)))
            is NumberReferenceValue    ->
                    GameManager.engine(sheetContext.gameId)
                        .apply({ it.numberValue(numberReference.valueReference, sheetContext) })
                        .apply({ effValue<AppError,List<Maybe<Double>>>(listOf(Just(it.value()))) })
            is NumberReferenceVariable ->
            {
                SheetManager.sheetState(sheetContext.sheetId)
                        .apply( { it.numberVariables(numberReference.variableReference) })
                        .apply( { it.toList().mapMI { it.value(sheetContext) } })
            }
        }


    /**
     * Resolve a text reference.
     */
    fun text(sheetContext : SheetContext,
             reference : TextReference) : AppEff<Maybe<String>> =
        when (reference)
        {
            is TextReferenceLiteral  -> effValue(Just(reference.value))
            is TextReferenceValue    ->
                    GameManager.engine(sheetContext.gameId)
                        .apply({ it.textValue(reference.valueReference, sheetContext) })
                        .apply({ effValue<AppError,Maybe<String>>(Just(it.value())) })
            is TextReferenceVariable -> SheetManager.sheetState(sheetContext.sheetId)
                    .apply( { it.textVariable(reference.variableReference)})
                    .apply( { it.value(sheetContext) })
        }
}
