
package com.taletable.android.model.engine.function


import com.taletable.android.app.AppEff
import com.taletable.android.app.AppEvalError
import com.taletable.android.model.engine.EngineValue
import com.taletable.android.model.engine.EngineValueBoolean
import com.taletable.android.model.engine.EngineValueDiceRoll
import com.taletable.android.model.engine.EngineValueNumber
import com.taletable.android.rts.entity.engine.interpreter.PlatformFunctionDoesNotExist
import effect.effError
import effect.effValue
import java.io.Serializable



/**
 * Built In Functions
 */

val platformFunctionNames = setOf(
        FunctionId("sum"),
        FunctionId("dice_roll"),
        FunctionId("toggle"),
        FunctionId("falsify")
    )


fun isPlatformFunction(functionId : FunctionId) : Boolean =
    platformFunctionNames.contains(functionId)


fun runPlatformFunction(functionId : FunctionId, params : PlatformFunctionParameters) : AppEff<EngineValue> =
    when (functionId.value)
    {
        "sum"       -> effValue(sumFunction(params))
        "dice_roll" -> effValue(diceRollFunction(params))
        "toggle"    -> effValue(toggleFunction(params))
        "falsify"   -> effValue(falsifyFunction(params))
        else        -> effError(AppEvalError(PlatformFunctionDoesNotExist(functionId)))
    }


data class PlatformFunctionParameters(val parameters : List<EngineValue>) : Serializable



// ---------------------------------------------------------------------------------------------
// Platform Functions
// ---------------------------------------------------------------------------------------------


fun sumFunction(params : PlatformFunctionParameters) : EngineValueNumber
{
    var sum : Double = 0.0

    params.parameters.forEach { engineValue ->
        when (engineValue)
        {
            is EngineValueNumber ->
            {
                sum += engineValue.value
            }
        }
    }

    return EngineValueNumber(sum)
}


fun diceRollFunction(params : PlatformFunctionParameters) : EngineValueNumber
{
    var roll : Int = 0

    params.parameters.forEach { engineValue ->
        when (engineValue)
        {
            is EngineValueDiceRoll ->
            {
                roll += engineValue.value.roll()
            }
        }
    }

    return EngineValueNumber(roll.toDouble())
}


fun toggleFunction(params : PlatformFunctionParameters) : EngineValueBoolean =
    if (params.parameters.isNotEmpty())
    {
        val param = params.parameters.first()
        when (param)
        {
            is EngineValueBoolean ->
            {
                if (param.value)
                    EngineValueBoolean(false)
                else
                    EngineValueBoolean(true)
            }
            else -> EngineValueBoolean(false)
        }
    }
    else
    {
        EngineValueBoolean(false)
    }


fun falsifyFunction(params : PlatformFunctionParameters) : EngineValueBoolean =
        EngineValueBoolean(false)



