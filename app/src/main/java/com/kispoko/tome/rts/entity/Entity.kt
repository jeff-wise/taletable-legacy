
package com.kispoko.tome.rts.entity


import android.graphics.Color
import com.kispoko.tome.app.*
import com.kispoko.tome.model.book.Book
import com.kispoko.tome.model.book.BookId
import com.kispoko.tome.model.campaign.Campaign
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.Game
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.game.engine.Engine
import com.kispoko.tome.model.game.engine.EngineValue
import com.kispoko.tome.model.game.engine.function.Function
import com.kispoko.tome.model.game.engine.function.FunctionId
import com.kispoko.tome.model.game.engine.mechanic.Mechanic
import com.kispoko.tome.model.game.engine.mechanic.MechanicCategory
import com.kispoko.tome.model.game.engine.mechanic.MechanicCategoryReference
import com.kispoko.tome.model.game.engine.mechanic.MechanicId
import com.kispoko.tome.model.game.engine.procedure.Procedure
import com.kispoko.tome.model.game.engine.procedure.ProcedureId
import com.kispoko.tome.model.game.engine.program.Program
import com.kispoko.tome.model.game.engine.program.ProgramId
import com.kispoko.tome.model.game.engine.reference.TextReference
import com.kispoko.tome.model.game.engine.summation.Summation
import com.kispoko.tome.model.game.engine.summation.SummationId
import com.kispoko.tome.model.game.engine.value.*
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.model.sheet.Sheet
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeId
import com.kispoko.tome.rts.entity.engine.TextReferenceIsNull
import com.kispoko.tome.rts.entity.sheet.SheetData
import com.kispoko.tome.rts.entity.theme.ThemeManager
import effect.*
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



// ---------------------------------------------------------------------------------------------
// STATE
// ---------------------------------------------------------------------------------------------

private var stateById : MutableMap<EntityId, EntityRecord> = mutableMapOf()


// ---------------------------------------------------------------------------------------------
// API
// ---------------------------------------------------------------------------------------------

/**
 * Entity State
 */
fun entityState(entityId : EntityId) : AppEff<EntityRecord> =
    if (stateById.containsKey(entityId))
        effValue(stateById[entityId]!!)
    else
        effError(AppEntityError(EntityDoesNotExist(entityId)))


// ---------------------------------------------------------------------------------------------
// GET
// ---------------------------------------------------------------------------------------------

// GET > Sheet
// ---------------------------------------------------------------------------------------------

fun sheet(sheetId : SheetId) : Maybe<Sheet>
{
    val entityState = stateById.get(EntitySheetId(sheetId))
    return when (entityState) {
        is EntitySheetRecord -> {
            Just(entityState.sheet)
        }
        else -> Nothing()
    }
}


fun sheetOrError(sheetId : SheetId) : AppEff<Sheet> =
        sheetOrError(EntitySheetId(sheetId))


fun sheetOrError(entityId : EntityId) : AppEff<Sheet>
{
    val record = stateById.get(entityId)
    return if (record != null) {
        when (record) {
            is EntitySheetRecord -> effValue(record.sheet)
            else                 ->
                effError<AppError,Sheet>(AppEntityError(EntityIsUnexpectedType(entityId,
                                                        EntityTypeSheet,
                                                        record.entityType)))
        }
    }
    else
    {
        effError(AppEntityError(EntityDoesNotExist(entityId)))
    }
}


// GET > Campaign
// ---------------------------------------------------------------------------------------------

fun campaign(campaignId : CampaignId) : Maybe<Campaign>
{
    val entityState = stateById.get(EntityCampaignId(campaignId))
    return when (entityState) {
        is EntityCampaignRecord -> {
            Just(entityState.campaign)
        }
        else -> Nothing()
    }
}


fun game(gameId : GameId) : Maybe<Game>
{
    val entityState = stateById.get(EntityGameId(gameId))
    return when (entityState) {
        is EntityGameRecord -> {
            Just(entityState.game)
        }
        else -> Nothing()
    }
}


fun book(bookId : BookId) : Maybe<Book>
{
    val entityState = stateById.get(EntityBookId(bookId))
    return when (entityState) {
        is EntityBookRecord -> {
            Just(entityState.book)
        }
        else -> Nothing()
    }
}


// Add
// ---------------------------------------------------------------------------------------------

fun addSheet(sheet : Sheet)
{
    val entityId = EntitySheetId(sheet.sheetId())
    val engineState = EntityState(entityId, listOf())
    val sheetRecord = EntitySheetRecord(sheet, engineState, Just(sheet.settings().themeId()))

    stateById.put(entityId, sheetRecord)
}


fun addCampaign(campaign : Campaign)
{
    val entityId = EntityCampaignId(campaign.campaignId())
    val engineState = EntityState(entityId, listOf())
    val campaignRecord = EntityCampaignRecord(campaign, engineState, Nothing())

    stateById.put(entityId, campaignRecord)
}


fun addGame(game : Game)
{
    val entityId = EntityGameId(game.gameId())
    val engineState = EntityState(entityId, listOf())
    val gameRecord = EntityGameRecord(game, engineState, Nothing())

    stateById.put(entityId, gameRecord)
}


fun addBook(book : Book)
{
    val entityId = EntityBookId(book.bookId())
    val engineState = EntityState(entityId, listOf())
    val bookRecord = EntityBookRecord(book, engineState, Just(book.settings().themeId()))

    stateById.put(entityId, bookRecord)
}


// ---------------------------------------------------------------------------------------------
// INITIALIZE
// ---------------------------------------------------------------------------------------------

fun initialize(entityId : EntityId) = when (entityId)
{
    is EntitySheetId -> initializeSheet(entityId.sheetId)
    else -> { }
}


fun initializeSheet(sheetId : SheetId)
{
    val entityId = EntitySheetId(sheetId)
    entityEngineState(entityId) apDo { entityState ->
    mechanics(entityId)         apDo { mechanicSet ->
        entityState.setMechanics(mechanicSet.toList())
    } }
}


// ---------------------------------------------------------------------------------------------
// ENGINE
// ---------------------------------------------------------------------------------------------

/**
 * Engine
 */
fun entityEngines(entityId : EntityId) : AppEff<List<Engine>> = when (entityId)
{
    is EntitySheetId ->
    {
        val engines : MutableList<Engine> = mutableListOf()
        val maybeSheet = sheet(entityId.sheetId)
        when (maybeSheet) {
            is Just -> {
                val sheet = maybeSheet.value
                engines.add(sheet.engine())
                val maybeCampaign = campaign(sheet.campaignId())
                when (maybeCampaign) {
                    is Just -> {
                        val campaign = maybeCampaign.value
                        engines.add(campaign.engine())
                        val maybeGame = game(campaign.gameId())
                        when (maybeGame) {
                            is Just -> engines.add(maybeGame.value.engine())
                        }
                    }
                }
            }
        }

        effValue(engines)
    }
    else -> effValue(listOf())
}



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


fun mechanics(entityId : EntityId) : AppEff<Set<Mechanic>>
{
    val engines = entityEngines(entityId)
    val _mechanics : MutableSet<Mechanic> = mutableSetOf()

    return when (engines)
    {
        is Val -> {
            engines.value.forEach {
                _mechanics.addAll(it.mechanics())
            }
            return effValue(_mechanics)
        }
        is Err -> return engines as AppEff<Set<Mechanic>>
    }
}


fun mechanicsInCategory(mechanicCategoryId: MechanicCategoryReference,
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

fun mechanicCategory(mechanicCategoryId : MechanicCategoryReference,
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


fun valueSet(valueSetIdReference : TextReference, entityId : EntityId) : AppEff<ValueSet>
{
    val error : AppError = AppEngineError(TextReferenceIsNull(valueSetIdReference))
    val valueSetId = SheetData.text(valueSetIdReference, entityId) ap { mValueSetId ->
                       note(mValueSetId.toNullable(), error)
                     }

    return when (valueSetId) {
        is Val -> valueSet(ValueSetId(valueSetId.value), entityId)
        is Err -> effError(error)
    }
}


fun valueSets(entityId : EntityId) : AppEff<Set<ValueSet>>
{
    val engines = entityEngines(entityId)
    val _valueSets : MutableSet<ValueSet> = mutableSetOf()

    return when (engines)
    {
        is Val -> {
            engines.value.forEach {
                _valueSets.addAll(it.valueSets())
            }
            return effValue(_valueSets)
        }
        is Err -> return engines as AppEff<Set<ValueSet>>
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

fun entityEngineState(entityId : EntityId) : AppEff<EntityState> =
        entityState(entityId).apply { effValue<AppError, EntityState>(it.engineState) }


// STATE > Variable
// ---------------------------------------------------------------------------------------------

fun variable(variableReference : VariableReference,
             entityId : EntityId) : AppEff<Variable> =
    entityEngineState(entityId).apply { it.variable(variableReference)  }


fun variables(variableReference : VariableReference,
              entityId : EntityId) : AppEff<Set<Variable>> =
        entityEngineState(entityId).apply { it.variables(variableReference)  }


fun variablesWithTag(variableTag : VariableTag, entityId : EntityId) : AppEff<Set<Variable>> =
    entityEngineState(entityId).apply { it.variablesWithTag(variableTag) }


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
        it.updateVariable(variableId, engineValue, entityId)
    }
}


fun addOnVariableChangeListener(variableId : VariableId,
                                onChangeListener : OnVariableChangeListener,
                                entityId : EntityId)
{
    entityEngineState(entityId) apDo {
        it.addVariableOnChangeListener(variableId, onChangeListener)
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


// STATE > Mechanics
// ---------------------------------------------------------------------------------------------

fun activeMechanicsInCategory(categoryId : MechanicCategoryReference,
                              entityId : EntityId) : AppEff<Set<Mechanic>> =
    entityEngineState(entityId)
            .apply { effValue<AppError,Set<Mechanic>>(it.activeMechanicsInCategory(categoryId)) }


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


fun color(colorId : ColorId, entityId : EntityId) : AppEff<Int> =
    entityThemeId(entityId)                 ap { themeId ->
    ThemeManager.theme(themeId)             ap { theme ->
    theme.colorOrError(colorId)
    } }


fun colorOrBlack(colorTheme : ColorTheme, entityId : EntityId) : Int
{
    val c = color(colorTheme, entityId)
    return when (c) {
        is Val -> c.value
        is Err -> {
            ApplicationLog.error(c.error)
            Color.BLACK
        }
    }
}


fun colorOrBlack(colorId : ColorId, entityId : EntityId) : Int
{
    val c = color(colorId, entityId)
    return when (c) {
        is Val -> c.value
        is Err -> Color.BLACK
    }
}



// ---------------------------------------------------------------------------------------------
// DEFINITIONS
// ---------------------------------------------------------------------------------------------

// Entity Id
// ---------------------------------------------------------------------------------------------

sealed class EntityId : Serializable

data class EntitySheetId(val sheetId : SheetId) : EntityId()

data class EntityCampaignId(val campaignId : CampaignId) : EntityId()

data class EntityGameId(val gameId : GameId) : EntityId()

data class EntityThemeId(val themeId : ThemeId) : EntityId()

data class EntityBookId(val bookId : BookId) : EntityId()


// Entity Type
// ---------------------------------------------------------------------------------------------

sealed class EntityType

object EntityTypeSheet : EntityType()
{
    override fun toString() = "Sheet"
}

object EntityTypeCampaign : EntityType()
{
    override fun toString() = "Campaign"
}

object EntityTypeGame : EntityType()
{
    override fun toString() = "Game"
}

object EntityTypeTheme : EntityType()
{
    override fun toString() = "Theme"
}

object EntityTypeBook : EntityType()
{
    override fun toString() = "Book"
}


// Entity State
// ---------------------------------------------------------------------------------------------

sealed class EntityRecord(open val engineState : EntityState,
                          open val themeId : Maybe<ThemeId>)
{
    abstract val entityType : EntityType
}



data class EntitySheetRecord(val sheet : Sheet,
                             override val engineState : EntityState,
                             override val themeId : Maybe<ThemeId>)
                              : EntityRecord(engineState, themeId)
{
    override val entityType = EntityTypeSheet
}


data class EntityCampaignRecord(val campaign : Campaign,
                                override val engineState : EntityState,
                                override val themeId : Maybe<ThemeId>)
                                 : EntityRecord(engineState, themeId)
{
    override val entityType = EntityTypeCampaign
}


data class EntityGameRecord(val game : Game,
                            override val engineState : EntityState,
                            override val themeId : Maybe<ThemeId>)
                             : EntityRecord(engineState, themeId)
{
    override val entityType = EntityTypeGame
}


data class EntityBookRecord(val book : Book,
                            override val engineState : EntityState,
                            override val themeId : Maybe<ThemeId>)
                             : EntityRecord(engineState, themeId)
{
    override val entityType = EntityTypeBook
}


