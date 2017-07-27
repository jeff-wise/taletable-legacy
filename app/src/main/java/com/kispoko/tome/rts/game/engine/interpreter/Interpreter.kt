
package com.kispoko.tome.rts.game.engine.interpreter


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.AppEvalError
import com.kispoko.tome.model.game.engine.EngineValue
import com.kispoko.tome.model.game.engine.EngineValueNumber
import com.kispoko.tome.model.game.engine.EngineValueType
import com.kispoko.tome.model.game.engine.function.Function
import com.kispoko.tome.model.game.engine.program.*
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.SheetData
import effect.*
import effect.Nothing
import java.io.Serializable


/**
 * Interpreter
 */
object Interpreter
{


    data class StatementBinding(val name : String, val value : EngineValue)



    // -----------------------------------------------------------------------------------------
    // EVALUATE
    // -----------------------------------------------------------------------------------------

    fun evaluate(invocation : Invocation, sheetContext : SheetContext) : AppEff<EngineValue>
    {
        val programEff    = GameManager.engine(sheetContext.gameId)
                                       .apply({ it.program(invocation.programId())})

        val parametersEff = this.programParameters(invocation, sheetContext)

        return programEff    ap { program ->
               parametersEff ap { parameters ->
                   this.programValue(program, parameters, sheetContext)
               } }
    }


    fun evaluateNumber(invocation : Invocation, sheetContext : SheetContext) : AppEff<Double> =
        this.evaluate(invocation, sheetContext) ap { engineValue ->
            when (engineValue)
            {
                is EngineValueNumber -> effValue<AppError,Double>(engineValue.value)
                else                 ->
                    effError<AppError,Double>(
                            AppEvalError(UnexpectedProgramResultType(invocation.programId(),
                                                                      engineValue.type(),
                                                                      EngineValueType.NUMBER)))
            }
        }


    /**
     * Evaluate the program parameters.
     */
    private fun programParameters(invocation : Invocation,
                                  sheetContext : SheetContext) : AppEff<Parameters>
    {
        // Parameter 1
        val parameter1 = SheetData.referenceEngineValue(invocation.parameter1(), sheetContext)

        // Parameter 2 (Maybe)
        val invocationParameter2 = invocation.parameter2()
        val parameter2 = when (invocationParameter2) {
            is Just    -> effApply(::Just, SheetData.referenceEngineValue(
                                                    invocationParameter2.value, sheetContext))
            is Nothing -> effValue<AppError,Maybe<EngineValue>>(effect.Nothing())
        }

        // Parameter 3 (Maybe)
        val invocationParameter3 = invocation.parameter3()
        val parameter3 = when (invocationParameter3) {
            is Just    -> effApply(::Just, SheetData.referenceEngineValue(
                                                    invocationParameter3.value, sheetContext))
            is Nothing -> effValue<AppError,Maybe<EngineValue>>(effect.Nothing())
        }

        // Parameter 4 (Maybe)
        val invocationParameter4 = invocation.parameter4()
        val parameter4 = when (invocationParameter4) {
            is Just    -> effApply(::Just, SheetData.referenceEngineValue(
                                                invocationParameter4.value, sheetContext))
            is Nothing -> effValue<AppError,Maybe<EngineValue>>(effect.Nothing())
        }

        // Parameter 5 (Maybe)
        val invocationParameter5 = invocation.parameter5()
        val parameter5 = when (invocationParameter5) {
            is Just    -> effApply(::Just, SheetData.referenceEngineValue(
                                                invocationParameter5.value, sheetContext))
            is Nothing -> effValue<AppError,Maybe<EngineValue>>(effect.Nothing())
        }

        return effApply(::Parameters, parameter1, parameter2, parameter3, parameter4, parameter5)
    }


    @Suppress("UNCHECKED_CAST")
    private fun programValue(program : Program,
                             programParameters : Parameters,
                             sheetContext : SheetContext) : AppEff<EngineValue>
    {
        val bindings : MutableMap<String,EngineValue> = mutableMapOf()

        for (statement in program.statements())
        {
            val value = this.statementValue(statement, programParameters, bindings,
                                            program.programId(), sheetContext)
            when (value)
            {
                is Val ->
                {
                    val binding = value.value
                    bindings.put(binding.name, binding.value)
                }
                is Err -> return value as AppEff<EngineValue>
            }
        }

        return note(bindings[program.resultBindingName()],
                    AppEvalError(ResultBindingDoesNotExist(program.resultBindingName(),
                                                            program.programId())))
    }


    private fun statementValue(statement : Statement,
                               programParameters : Parameters,
                               bindings : Map<String,EngineValue>,
                               programId : ProgramId,
                               sheetContext : SheetContext) : AppEff<StatementBinding>
    {

        val functionEff   = GameManager.engine(sheetContext.gameId)
                                       .apply({ it.function(statement.functionId())})

        val parametersEff = this.statementParameters(statement, programParameters, bindings,
                                                     programId, sheetContext)

        fun binding(engineValue : EngineValue) : StatementBinding =
            StatementBinding(statement.bindingName(), engineValue)


        return functionEff     ap { function ->
               parametersEff   ap { parameters ->
                   effApply(::binding, this.functionValue(function, parameters))
               } }
    }


    private fun statementParameters(statement : Statement,
                                    programParameters : Parameters,
                                    bindings : Map<String,EngineValue>,
                                    programId : ProgramId,
                                    sheetContext : SheetContext) : AppEff<Parameters>
    {
        val parameter1 = this.statementParameterValue(statement.parameter1(),
                                                      programParameters,
                                                      bindings,
                                                      programId,
                                                      sheetContext)

        val statementParameter2 = statement.parameter2()
        val parameter2 = when (statementParameter2) {
            is Just    -> effApply(::Just, this.statementParameterValue(statementParameter2.value,
                                                                        programParameters,
                                                                        bindings,
                                                                        programId,
                                                                        sheetContext))
            is Nothing -> effValue<AppError,Maybe<EngineValue>>(Nothing())
        }

        val statementParameter3 = statement.parameter3()
        val parameter3 = when (statementParameter3) {
            is Just    -> effApply(::Just, this.statementParameterValue(statementParameter3.value,
                                                                        programParameters,
                                                                        bindings,
                                                                        programId,
                                                                        sheetContext))
            is Nothing -> effValue<AppError,Maybe<EngineValue>>(Nothing())
        }

        val statementParameter4 = statement.parameter4()
        val parameter4 = when (statementParameter4) {
            is Just    -> effApply(::Just, this.statementParameterValue(statementParameter4.value,
                                                                        programParameters,
                                                                        bindings,
                                                                        programId,
                                                                        sheetContext))
            is Nothing -> effValue<AppError,Maybe<EngineValue>>(Nothing())
        }

        val statementParameter5 = statement.parameter5()
        val parameter5 = when (statementParameter5) {
            is Just    -> effApply(::Just, this.statementParameterValue(statementParameter5.value,
                                                                        programParameters,
                                                                        bindings,
                                                                        programId,
                                                                        sheetContext))
            is Nothing -> effValue<AppError,Maybe<EngineValue>>(Nothing())
        }

        return effApply(::Parameters, parameter1, parameter2, parameter3, parameter4, parameter5)
    }


    private fun statementParameterValue(statementParameter : StatementParameter,
                                        programParameters : Parameters,
                                        bindings : Map<String,EngineValue>,
                                        programId : ProgramId,
                                        sheetContext : SheetContext) : AppEff<EngineValue> =
        when (statementParameter)
        {
            is StatementParameterBindingName ->
            {
                val bindingName = statementParameter.bindingName.value
                note(bindings[bindingName],
                     AppEvalError(BindingDoesNotExist(bindingName, programId)))
            }
            is StatementParameterProgramParameter ->
            {
                val parameterIndex = statementParameter.index.value
                val parameter = programParameters.atIndex(parameterIndex)
                when (parameter) {
                    is Just    -> effValue<AppError,EngineValue>(parameter.value)
                    is Nothing -> effError<AppError,EngineValue>(AppEvalError(
                                        ProgramParameterDoesNotExist(parameterIndex, programId)))
                }
            }
            is StatementParameterReference ->
            {
                SheetData.referenceEngineValue(statementParameter.reference, sheetContext)
            }
        }


    private fun functionValue(function : Function, parameters : Parameters) : AppEff<EngineValue>
    {
        val tuple = function.tupleWithParameters(parameters)

        if (tuple != null)
            return effValue(tuple.result())
        else
            return effError(AppEvalError(
                    FunctionNotDefinedForParameters(function.functionId(), parameters)))
    }



}



data class Parameters(val parameter1 : EngineValue,
                      val parameter2 : Maybe<EngineValue>,
                      val parameter3 : Maybe<EngineValue>,
                      val parameter4 : Maybe<EngineValue>,
                      val parameter5 : Maybe<EngineValue>) : Serializable
{

        fun atIndex(index : Int) : Maybe<EngineValue> =
            when (index)
            {
                1    -> Just(parameter1)
                2    -> parameter2
                3    -> parameter3
                4    -> parameter4
                5    -> parameter5
                else -> Nothing()
            }

    }


