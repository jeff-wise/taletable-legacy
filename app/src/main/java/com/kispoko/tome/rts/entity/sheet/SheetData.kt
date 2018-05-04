
package com.kispoko.tome.rts.entity.sheet


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.model.engine.*
import com.kispoko.tome.model.engine.dice.DiceRoll
import com.kispoko.tome.model.engine.reference.*
import com.kispoko.tome.model.engine.variable.VariableNamespace
import com.kispoko.tome.rts.entity.*
import effect.*
import maybe.Just
import maybe.Nothing
import maybe.Maybe



/**
 * Sheet Data
 */
object SheetData
{

    // -----------------------------------------------------------------------------------------
    // REFERENCES
    // -----------------------------------------------------------------------------------------


    fun referenceEngineValue(reference : DataReference,
                             entityId : EntityId) : AppEff<Maybe<EngineValue>> =
        when (reference)
        {
            is DataReferenceBoolean -> this.boolean(reference.reference, entityId) ap {
                effValue<AppError,Maybe<EngineValue>>(Just(EngineValueBoolean(it)))
            }
            is DataReferenceDiceRoll -> this.diceRoll(reference.reference, entityId) ap {
                effValue<AppError,Maybe<EngineValue>>(Just(EngineValueDiceRoll(it)))
            }
            is DataReferenceNumber -> this.number(reference.reference, entityId) ap {
                when (it) {
                    is Just -> effValue<AppError,Maybe<EngineValue>>(Just(EngineValueNumber(it.value)))
                    else    -> effValue(Nothing())
                }
            }
            is DataReferenceText -> this.text(reference.reference, entityId) ap {
                when (it) {
                    is Just -> effValue<AppError,Maybe<EngineValue>>(Just(EngineValueText(it.value)))
                    else    -> effValue(Nothing())
                }
            }
        }


    /**
     * Resolve a boolean reference.
     */
    fun boolean(reference : BooleanReference, entityId : EntityId) : AppEff<Boolean> =
        when (reference)
        {
            is BooleanReferenceLiteral  -> effValue(reference.value)
            is BooleanReferenceVariable ->
                booleanVariable(reference.variableReference, entityId)
                  .apply( { it.value() })

        }


    /**
     * Resolve a dice roll reference.
     */
    fun diceRoll(reference : DiceRollReference,
                 entityId : EntityId) : AppEff<DiceRoll> =
        when (reference)
        {
            is DiceRollReferenceLiteral  -> effValue(reference.value)
            is DiceRollReferenceVariable ->
                diceRollVariable(reference.variableReference, entityId)
                  .apply( { effValue<AppError, DiceRoll>(it.value()) })
            is DiceRollReferenceSummation ->
                    summation(reference.summationId, entityId)
                            .apply { it.diceRoll(entityId) }

        }


    /**
     * Resolve a number reference.
     */
    fun number(numberReference : NumberReference,
               entityId : EntityId,
               context : Maybe<VariableNamespace> = Nothing()) : AppEff<Maybe<Double>> =
        when (numberReference)
        {
            is NumberReferenceLiteral  -> effValue(Just(numberReference.value))
            is NumberReferenceValue    ->
                numberValue(numberReference.valueReference, entityId)
                  .apply({ effValue<AppError, Maybe<Double>>(Just(it.value())) })
            is NumberReferenceVariable ->
                numberVariable(numberReference.variableReference, entityId)
                    .apply { it.value(entityId) }

        }


    fun numbers(numberReference : NumberReference,
                entityId : EntityId,
                context : Maybe<VariableNamespace> = Nothing()) : AppEff<List<Maybe<Double>>> =
        when (numberReference)
        {
            is NumberReferenceLiteral -> effValue(listOf(Just(numberReference.value)))
            is NumberReferenceValue    ->
                numberValue(numberReference.valueReference, entityId)
                  .apply({ effValue<AppError,List<Maybe<Double>>>(listOf(Just(it.value()))) })
            is NumberReferenceVariable ->
                numberVariables(numberReference.variableReference, entityId)
                  .apply( { it.toList().mapMI { it.value(entityId) } })
        }


    /**
     * Resolve a text reference.
     */
    fun text(reference : TextReference, entityId : EntityId) : AppEff<Maybe<String>> =
        when (reference)
        {
            is TextReferenceLiteral  -> effValue(Just(reference.value))
            is TextReferenceValue    ->
                textValue(reference.valueReference, entityId)
                  .apply({ effValue<AppError,Maybe<String>>(Just(it.value())) })
            is TextReferenceVariable ->
                textVariable(reference.variableReference, entityId)
                  .apply( { it.value(entityId) })
            is TextReferenceProgram  -> apply(::Just, reference.invocation.textValue(entityId))
        }
}
