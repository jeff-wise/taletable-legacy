
package com.kispoko.tome.rts.entity


import android.graphics.Color
import com.kispoko.tome.R.string.engine
import com.kispoko.tome.app.*
import com.kispoko.tome.model.game.engine.Engine
import com.kispoko.tome.model.game.engine.EngineValue
import com.kispoko.tome.model.game.engine.EngineValueType
import com.kispoko.tome.model.game.engine.function.Function
import com.kispoko.tome.model.game.engine.function.FunctionId
import com.kispoko.tome.model.game.engine.mechanic.Mechanic
import com.kispoko.tome.model.game.engine.mechanic.MechanicCategory
import com.kispoko.tome.model.game.engine.mechanic.MechanicCategoryId
import com.kispoko.tome.model.game.engine.mechanic.MechanicId
import com.kispoko.tome.model.game.engine.procedure.Procedure
import com.kispoko.tome.model.game.engine.procedure.ProcedureId
import com.kispoko.tome.model.game.engine.program.Program
import com.kispoko.tome.model.game.engine.program.ProgramId
import com.kispoko.tome.model.game.engine.summation.Summation
import com.kispoko.tome.model.game.engine.summation.SummationId
import com.kispoko.tome.model.game.engine.value.*
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.engine.EngineState
import com.kispoko.tome.rts.entity.engine.TextReferenceIsNull
import com.kispoko.tome.rts.entity.sheet.SheetContext
import com.kispoko.tome.rts.entity.sheet.SheetData
import com.kispoko.tome.rts.entity.sheet.VariableDoesNotExist
import com.kispoko.tome.rts.entity.sheet.VariableIsOfUnexpectedType
import com.kispoko.tome.rts.entity.theme.ThemeManager
import effect.*
import maybe.Maybe
import maybe.Nothing


// ---------------------------------------------------------------------------------------------
// STATE
// ---------------------------------------------------------------------------------------------

private var stateById : MutableMap<EntityId,EntityState> = mutableMapOf()


// ---------------------------------------------------------------------------------------------
// API
// ---------------------------------------------------------------------------------------------

/**
 * Entity State
 */
fun entityState(entityId : EntityId) : AppEff<EntityState> =
    if (stateById.containsKey(entityId))
        effValue(stateById[entityId]!!)
    else
        effError(AppEntityError(EntityDoesNotExist(entityId)))


// Engine
// ---------------------------------------------------------------------------------------------

/**
 * Engine
 */
fun entityEngines(entityId : EntityId) : AppEff<List<Engine>> =
        entityState(entityId).apply { effValue<AppError,List<Engine>>(it.engines) }


// Engine > Function
// ---------------------------------------------------------------------------------------------

fun function(functionId : FunctionId, entityId : EntityId) : AppEff<Function>
{
    val engines = entityEngines(entityId)
    when (engines) {
        is Val -> {
            engines.value.forEach {
                val engineFunction = it.function(functionId)
                when (engineFunction) {
                    is Val -> return engineFunction
                }
            }
            return effError(AppEntityError(EntityDoesNotHaveFunction(entityId, functionId)))
        }
        is Err -> return engines as AppEff<Function>
    }
}


// Engine > Mechanic
// ---------------------------------------------------------------------------------------------

fun mechanic(mechanicId : MechanicId, entityId : EntityId) : AppEff<Mechanic>
{
    val engines = entityEngines(entityId)
    when (engines) {
        is Val -> {
            engines.value.forEach {
                val engineMechanic = it.mechanic(mechanicId)
                when (engineMechanic) {
                    is Val -> return engineMechanic
                }
            }
            return effError(AppEntityError(EntityDoesNotHaveMechanic(entityId, mechanicId)))
        }
        is Err -> return engines as AppEff<Mechanic>
    }
}


fun mechanicsInCategory(mechanicCategoryId: MechanicCategoryId,
                        entityId : EntityId) : AppEff<Set<Mechanic>>
{
    val mechanics : MutableSet<Mechanic> = mutableSetOf()
    val engines = entityEngines(entityId)
    when (engines) {
        is Val -> {
            engines.value.forEach {
                mechanics.addAll(it.mechanicsInCategory(mechanicCategoryId))
            }
        }
        is Err -> return engines as AppEff<Set<Mechanic>>
    }

    return effValue(mechanics)
}


// Engine > Mechanic Category
// ---------------------------------------------------------------------------------------------

fun mechanicCategory(mechanicCategoryId : MechanicCategoryId,
                     entityId : EntityId) : AppEff<MechanicCategory>
{
    val engines = entityEngines(entityId)
    when (engines) {
        is Val -> {
            engines.value.forEach {
                val engineMechanicCategory = it.mechanicCategory(mechanicCategoryId)
                when (engineMechanicCategory) {
                    is Val -> return engineMechanicCategory
                }
            }
            return effError(AppEntityError(EntityDoesNotHaveMechanicCategory(entityId, mechanicCategoryId)))
        }
        is Err -> return engines as AppEff<MechanicCategory>
    }
}


// Engine > Procedure
// ---------------------------------------------------------------------------------------------

fun procedure(procedureId : ProcedureId, entityId : EntityId) : AppEff<Procedure>
{
    val engines = entityEngines(entityId)
    when (engines) {
        is Val -> {
            engines.value.forEach {
                val engineProcedure = it.procedure(procedureId)
                when (engineProcedure) {
                    is Val -> return engineProcedure
                }
            }
            return effError(AppEntityError(EntityDoesNotHaveProcedure(entityId, procedureId)))
        }
        is Err -> return engines as AppEff<Procedure>
    }
}



// Engine > Program
// ---------------------------------------------------------------------------------------------

fun program(programId : ProgramId, entityId : EntityId) : AppEff<Program>
{
    val engines = entityEngines(entityId)
    when (engines) {
        is Val -> {
            engines.value.forEach {
                val engineProgram = it.program(programId)
                when (engineProgram) {
                    is Val -> return engineProgram
                }
            }
            return effError(AppEntityError(EntityDoesNotHaveProgram(entityId, programId)))
        }
        is Err -> return engines as AppEff<Program>
    }
}


// Engine > Summation
// ---------------------------------------------------------------------------------------------

fun summation(summationId : SummationId, entityId : EntityId) : AppEff<Summation>
{
    val engines = entityEngines(entityId)
    when (engines) {
        is Val -> {
            engines.value.forEach {
                val engineSummation = it.summation(summationId)
                when (engineSummation) {
                    is Val -> return engineSummation
                }
            }
            return effError(AppEntityError(EntityDoesNotHaveSummation(entityId, summationId)))
        }
        is Err -> return engines as AppEff<Summation>
    }
}


// Engine > Value Set
// ---------------------------------------------------------------------------------------------

fun valueSet(valueSetId : ValueSetId, entityId : EntityId) : AppEff<ValueSet>
{
    val engines = entityEngines(entityId)
    when (engines) {
        is Val -> {
            engines.value.forEach {
                val engineValueSet = it.valueSet(valueSetId)
                when (engineValueSet) {
                    is Val -> return engineValueSet
                }
            }
            return effError(AppEntityError(EntityDoesNotHaveValueSet(entityId, valueSetId)))
        }
        is Err -> return engines as AppEff<ValueSet>
    }
}


// Engine > Value Set > Value
// ---------------------------------------------------------------------------------------------

fun value(valueReference : ValueReference, entityId : EntityId) : AppEff<Value>
{
    val engines = entityEngines(entityId)
    when (engines) {
        is Val -> {
            engines.value.forEach {
                val engineValue = it.value(valueReference, entityId)
                when (engineValue) {
                    is Val -> return engineValue
                }
            }
            return effError(AppEntityError(EntityDoesNotHaveValue(entityId, valueReference)))
        }
        is Err -> return engines as AppEff<Value>
    }
}


fun numberValue(valueReference : ValueReference, entityId : EntityId) : AppEff<ValueNumber>
{
    val engines = entityEngines(entityId)
    when (engines) {
        is Val -> {
            engines.value.forEach {
                val engineValue = it.numberValue(valueReference, entityId)
                when (engineValue) {
                    is Val -> return engineValue
                }
            }
            return effError(AppEntityError(EntityDoesNotHaveValue(entityId, valueReference)))
        }
        is Err -> return engines as AppEff<ValueNumber>
    }
}


fun textValue(valueReference : ValueReference, entityId : EntityId) : AppEff<ValueText>
{
    val engines = entityEngines(entityId)
    when (engines) {
        is Val -> {
            engines.value.forEach {
                val engineValue = it.textValue(valueReference, entityId)
                when (engineValue) {
                    is Val -> return engineValue
                }
            }
            return effError(AppEntityError(EntityDoesNotHaveValue(entityId, valueReference)))
        }
        is Err -> return engines as AppEff<ValueText>
    }
}


// STATE
// ---------------------------------------------------------------------------------------------

fun entityEngineState(entityId : EntityId) : AppEff<EngineState> =
        entityState(entityId).apply { effValue<AppError,EngineState>(it.engineState) }


// STATE > Variable
// ---------------------------------------------------------------------------------------------

fun variable(variableReference : VariableReference,
             entityId : EntityId) : AppEff<Variable> =
    entityEngineState(entityId).apply { it.variable(variableReference)  }


fun variables(variableReference : VariableReference,
              entityId : EntityId) : AppEff<Set<Variable>> =
        entityEngineState(entityId).apply { it.variables(variableReference)  }


fun onVariableUpdate(variable : Variable, entityId : EntityId) {
    entityEngineState(entityId) apDo { it.onVariableUpdate(variable) }
}


fun addVariable(variable : Variable, entityId : EntityId) {
    entityEngineState(entityId) apDo { it.addVariable(variable) }
}


fun updateVariableId(currentVariableId : VariableId,
                     newVariableId : VariableId,
                     entityId : EntityId) {
    entityEngineState(entityId) apDo { it.updateVariableId(currentVariableId, newVariableId) }
}


fun updateVariable(variableId : VariableId,
                   engineValue : EngineValue,
                   entityId : EntityId)
{
    entityEngineState(entityId) apDo {
        updateVariable(variableId, engineValue, entityId)
    }
}


// STATE > Variable > Boolean
// ---------------------------------------------------------------------------------------------

fun booleanVariable(variableReference : VariableReference,
                    entityId : EntityId) : AppEff<BooleanVariable> =
        entityEngineState(entityId).apply { it.booleanVariable(variableReference)  }


// Engine State > Variable > Dice Roll
// ---------------------------------------------------------------------------------------------

fun diceRollVariable(variableReference : VariableReference,
                     entityId : EntityId) : AppEff<DiceRollVariable> =
        entityEngineState(entityId).apply { it.diceRollVariable(variableReference)  }


// Engine State > Variable > Number
// ---------------------------------------------------------------------------------------------

fun numberVariable(variableReference : VariableReference,
                   entityId : EntityId) : AppEff<NumberVariable> =
        entityEngineState(entityId).apply { it.numberVariable(variableReference)  }


fun numberVariables(variableReference : VariableReference,
                    entityId : EntityId) : AppEff<Set<NumberVariable>> =
        entityEngineState(entityId).apply { it.numberVariables(variableReference)  }


// Engine State > Variable > Text
// ---------------------------------------------------------------------------------------------

fun textVariable(variableReference : VariableReference,
                 entityId : EntityId) : AppEff<TextVariable> =
        entityEngineState(entityId).apply { it.textVariable(variableReference)  }


// Engine State > Variable > Text List
// ---------------------------------------------------------------------------------------------

fun textListVariable(variableReference : VariableReference,
                    entityId : EntityId) : AppEff<TextListVariable> =
    entityEngineState(entityId).apply { it.textListVariable(variableReference)  }


// THEME
// ---------------------------------------------------------------------------------------------

fun entityThemeId(entityId : EntityId) : AppEff<ThemeId> =
    entityState(entityId).apply {
        note<AppError,ThemeId>(it.themeId, AppEntityError(EntityDoesNotHaveTheme(entityId)))
    }


fun color(colorTheme : ColorTheme, entityId : EntityId) : AppEff<Int> =
    entityThemeId(entityId)                 ap { themeId ->
    ThemeManager.theme(themeId)             ap { theme ->
    colorTheme.themeColorIdOrError(themeId) ap { colorId ->
    theme.colorOrError(colorId)
    } } }


fun colorOrBlack(colorTheme : ColorTheme, entityId : EntityId) : Int
{
    val c = color(colorTheme, entityId)
    return when (c) {
        is Val -> c.value
        is Err -> Color.BLACK
    }
}


// ---------------------------------------------------------------------------------------------
// DEFINITIONS
// ---------------------------------------------------------------------------------------------

/**
 * Entity State
 */
data class EntityState(val engines : List<Engine>,
                       val engineState : EngineState,
                       val themeId : Maybe<ThemeId>)
{


}

