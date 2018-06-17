
package com.kispoko.tome.rts.entity


import android.graphics.Color
import com.kispoko.culebra.*
import com.kispoko.tome.app.*
import com.kispoko.tome.model.book.Book
import com.kispoko.tome.model.book.BookId
import com.kispoko.tome.model.campaign.Campaign
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.Game
import com.kispoko.tome.model.game.GameId
import com.kispoko.tome.model.engine.Engine
import com.kispoko.tome.model.engine.EngineValue
import com.kispoko.tome.model.engine.function.Function
import com.kispoko.tome.model.engine.function.FunctionId
import com.kispoko.tome.model.engine.mechanic.Mechanic
import com.kispoko.tome.model.engine.mechanic.MechanicCategory
import com.kispoko.tome.model.engine.mechanic.MechanicCategoryReference
import com.kispoko.tome.model.engine.mechanic.MechanicId
import com.kispoko.tome.model.engine.procedure.Procedure
import com.kispoko.tome.model.engine.procedure.ProcedureId
import com.kispoko.tome.model.engine.program.Program
import com.kispoko.tome.model.engine.program.ProgramId
import com.kispoko.tome.model.engine.reference.TextReference
import com.kispoko.tome.model.engine.summation.Summation
import com.kispoko.tome.model.engine.summation.SummationId
import com.kispoko.tome.model.engine.tag.TagQuery
import com.kispoko.tome.model.engine.task.Task
import com.kispoko.tome.model.engine.value.*
import com.kispoko.tome.model.engine.variable.*
import com.kispoko.tome.model.feed.Feed
import com.kispoko.tome.model.sheet.Sheet
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.group.*
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
import java.util.*



// ---------------------------------------------------------------------------------------------
// STATE
// ---------------------------------------------------------------------------------------------

private var entityRecordById : MutableMap<EntityId,EntityRecord> = mutableMapOf()


// ---------------------------------------------------------------------------------------------
// API
// ---------------------------------------------------------------------------------------------

/**
 * Entity Record
 */
fun entityRecord(entityId : EntityId) : AppEff<EntityRecord> =
    if (entityRecordById.containsKey(entityId))
        effValue(entityRecordById[entityId]!!)
    else
        effError(AppEntityError(EntityDoesNotExist(entityId)))


fun entitySheetRecord(sheetId : SheetId) : AppEff<EntitySheetRecord> =
    entityRecord(EntitySheetId(sheetId)) apply {
        when (it) {
            is EntitySheetRecord -> effValue(it)
            else                 -> {
                effError<AppError,EntitySheetRecord>(AppEntityError(
                        EntityIsUnexpectedType(EntitySheetId(sheetId),
                                               EntityTypeSheet,
                                               it.entityType)))
            }
        }
    }


// ---------------------------------------------------------------------------------------------
// GET
// ---------------------------------------------------------------------------------------------

// GET > Sheet
// ---------------------------------------------------------------------------------------------

fun sheet(sheetId : SheetId) : Maybe<Sheet>
{
    val entityState = entityRecordById.get(EntitySheetId(sheetId))
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
    val record = entityRecordById.get(entityId)
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
    val entityState = entityRecordById.get(EntityCampaignId(campaignId))
    return when (entityState) {
        is EntityCampaignRecord -> {
            Just(entityState.campaign)
        }
        else -> Nothing()
    }
}


fun game(gameId : GameId) : Maybe<Game>
{
    val entityState = entityRecordById.get(EntityGameId(gameId))
    return when (entityState) {
        is EntityGameRecord -> {
            Just(entityState.game)
        }
        else -> Nothing()
    }
}


fun game(campaignId : CampaignId) : Maybe<Game> =
    campaign(campaignId).apply { game(it.gameId) }


fun book(bookId : BookId) : Maybe<Book>
{
    val entityState = entityRecordById.get(EntityBookId(bookId))
    return when (entityState) {
        is EntityBookRecord -> {
            Just(entityState.book)
        }
        else -> Nothing()
    }
}


fun feed(feedId : EntityFeedId) : Maybe<Feed>
{
    val record = entityRecordById[feedId]
    return when (record) {
        is EntityFeedRecord -> {
            Just(record.feed)
        }
        else -> Nothing()
    }
}


// Add
// ---------------------------------------------------------------------------------------------

fun addSheet(sheet : Sheet)
{
    val entityId = EntitySheetId(sheet.sheetId())
    val engineState = EntityState(entityId, mutableListOf(), mutableListOf())
    val sheetRecord = EntitySheetRecord(sheet, engineState, Just(sheet.settings().themeId()))

    entityRecordById.put(entityId, sheetRecord)
}


fun addCampaign(campaign : Campaign)
{
    val entityId = EntityCampaignId(campaign.campaignId())
    val engineState = EntityState(entityId, mutableListOf(), mutableListOf())
    val campaignRecord = EntityCampaignRecord(campaign, engineState, Nothing())

    entityRecordById.put(entityId, campaignRecord)
}


fun addGame(game : Game)
{
    val entityId = EntityGameId(game.gameId())
    val engineState = EntityState(entityId, mutableListOf(), mutableListOf())
    val gameRecord = EntityGameRecord(game, engineState, Nothing())

    entityRecordById.put(entityId, gameRecord)
}


fun addBook(book : Book)
{
    val entityId = EntityBookId(book.bookId())
    val entityState = EntityState(entityId, mutableListOf(), mutableListOf())
    val bookRecord = EntityBookRecord(book, entityState, Just(book.settings().themeId()))

    book.variables().forEach {
        entityState.addVariable(it)
    }

    entityRecordById.put(entityId, bookRecord)
}


fun addFeed(feed : Feed)
{
    val entityState = EntityState(feed.entityId(), mutableListOf(), mutableListOf())
    val bookRecord = EntityFeedRecord(feed, entityState, Just(feed.settings().themeId()))

    feed.variables().forEach {
        entityState.addVariable(it)
    }

    entityRecordById[feed.entityId()] = bookRecord
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
        mechanics(entityId) apDo { mechanicSet ->
            entityState.setMechanics(mechanicSet.toList())
        }
        tasks(entityId)     apDo { taskSet ->
            entityState.setTasks(taskSet.toList())
        }
    }
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


/**
 * Group With Id
 */
fun groupWithId(groupId : GroupId, entityId : EntityId) : Maybe<Group> = when (entityId)
{
    is EntitySheetId ->
    {
        sheet(entityId.sheetId)   ap {
        campaign(it.campaignId()) ap {
        game(it.gameId())         ap {
            it.groupWithId(groupId)
        } } }
    }
    is EntityFeedId ->
    {
        feed(entityId) ap {
            it.groupWithId(groupId)
        }
    }
    else -> Nothing()
}


/**
 * Group By Tag
 */
fun groups(tagQuery : TagQuery, entityId : EntityId) : List<Group> = when (entityId)
{
    is EntitySheetId ->
    {
        val game = sheet(entityId.sheetId)
                     .apply { campaign(it.campaignId()) }
                     .apply { game(it.gameId()) }
        when (game) {
            is Just    -> game.value.groups(tagQuery)
            is Nothing -> listOf()
        }
    }
    else -> listOf()
}


fun groups(groupReferences : List<GroupReference>, entityId : EntityId) : List<Group>
{
    val groups : MutableList<Group> = mutableListOf()

    groupReferences.forEach {
        when (it) {
            is GroupReferenceLiteral -> groups.add(it.group)
            is GroupReferenceId -> {
                groupWithId(it.groupId, entityId).doMaybe {
                    groups.add(it)
                }
            }
        }
    }

    return groups
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


// Engine > Task
// ---------------------------------------------------------------------------------------------

fun tasks(entityId : EntityId) : AppEff<Set<Task>>
{
    val engines = entityEngines(entityId)
    val _tasks : MutableSet<Task> = mutableSetOf()

    return when (engines)
    {
        is Val -> {
            engines.value.forEach {
                _tasks.addAll(it.tasks())
            }
            return effValue(_tasks)
        }
        is Err -> return engines as AppEff<Set<Task>>
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
        entityRecord(entityId).apply { effValue<AppError, EntityState>(it.engineState) }


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


fun addVariableChangeListener(variableId : VariableId,
                              onChangeListener : VariableChangeListener,
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
    entityRecord(entityId).apply {
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
{
    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityId> = when (yamlValue)
        {
            is YamlDict ->
            {
                yamlValue.text("type") apply {
                    when (it) {
                        "sheet"    -> yamlValue.at("sheet").apply(EntitySheetId.Companion::fromYaml) as YamlParser<EntityId>
                        "campaign" -> yamlValue.at("campaign").apply(EntityCampaignId.Companion::fromYaml) as YamlParser<EntityId>
                        "game"     -> yamlValue.at("game").apply(EntityGameId.Companion::fromYaml) as YamlParser<EntityId>
                        "book"     -> yamlValue.at("book").apply(EntityBookId.Companion::fromYaml) as YamlParser<EntityId>
                        else       -> effError<YamlParseError,EntityId>(
                                        UnexpectedStringValue(it, yamlValue.path))
                    }
                }
            }
            else        -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
        }
    }

}


data class EntitySheetId(val sheetId : SheetId) : EntityId()
{
    override fun toString() = sheetId.value

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntitySheetId> =
            apply(::EntitySheetId, SheetId.fromYaml(yamlValue))
    }

}


data class EntityCampaignId(val campaignId : CampaignId) : EntityId()
{
    override fun toString() = campaignId.value

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityCampaignId> =
            apply(::EntityCampaignId, CampaignId.fromYaml(yamlValue))
    }

}

data class EntityGameId(val gameId : GameId) : EntityId()
{
    override fun toString() = gameId.value

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityGameId> =
            apply(::EntityGameId, GameId.fromYaml(yamlValue))
    }

}

data class EntityThemeId(val themeId : ThemeId) : EntityId()
{
    override fun toString() = themeId.toString()

//    companion object
//    {
//        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityThemeId> =
//            apply(::EntityThemeId, ThemeId.fromYaml(yamlValue))
//    }

}

data class EntityBookId(val bookId : BookId) : EntityId()
{
    override fun toString() = bookId.value

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityBookId> =
            apply(::EntityBookId, BookId.fromYaml(yamlValue))
    }

}

data class EntityFeedId(val id : UUID) : EntityId()
{
    override fun toString() = id.toString()
}

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


// Entity Kind
// ---------------------------------------------------------------------------------------------

data class EntityKind(val id : EntityKindId,
                      val name : String,
                      val namePlural : String,
                      val shortName : String,
                      val shortNamePlural : String,
                      val description : String) : Serializable
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityKind> = when (yamlValue)
        {
            is YamlDict ->
            {
                apply(::EntityKind,
                      // Id
                      yamlValue.at("id") ap { EntityKindId.fromYaml(it) },
                      // Name
                      yamlValue.text("name"),
                      // Name Plural
                      yamlValue.text("name_plural"),
                      // Short Name
                      yamlValue.text("short_name"),
                      // Short Name Plural
                      yamlValue.text("short_name_plural"),
                      // Description
                      yamlValue.text("description")
                      )
            }
            else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
        }
    }

}


data class EntityKindId(val value : String) : EntityId()
{

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityKindId> =
            when (yamlValue)
            {
                is YamlText -> effValue(EntityKindId(yamlValue.text))
                else        -> error(UnexpectedTypeFound(YamlType.TEXT,
                                                         yamlType(yamlValue),
                                                         yamlValue.path))
            }

    }

}



// Entity State
// ---------------------------------------------------------------------------------------------

sealed class EntityRecord(open val engineState : EntityState,
                          open val themeId : Maybe<ThemeId>)
{
    abstract val entityType : EntityType

    abstract fun entity() : Entity

}



data class EntitySheetRecord(val sheet : Sheet,
                             override val engineState : EntityState,
                             override val themeId : Maybe<ThemeId>)
                              : EntityRecord(engineState, themeId)
{

    override val entityType = EntityTypeSheet

    override fun entity() = sheet

}


data class EntityCampaignRecord(val campaign : Campaign,
                                override val engineState : EntityState,
                                override val themeId : Maybe<ThemeId>)
                                 : EntityRecord(engineState, themeId)
{

    override val entityType = EntityTypeCampaign

    override fun entity() = campaign

}


data class EntityGameRecord(val game : Game,
                            override val engineState : EntityState,
                            override val themeId : Maybe<ThemeId>)
                             : EntityRecord(engineState, themeId)
{

    override val entityType = EntityTypeGame

    override fun entity() = game

}


data class EntityBookRecord(val book : Book,
                            override val engineState : EntityState,
                            override val themeId : Maybe<ThemeId>)
                             : EntityRecord(engineState, themeId)
{

    override val entityType = EntityTypeBook

    override fun entity() = book

}


data class EntityFeedRecord(val feed : Feed,
                            override val engineState : EntityState,
                            override val themeId : Maybe<ThemeId>)
                             : EntityRecord(engineState, themeId)
{

    override val entityType = EntityTypeBook

    override fun entity() = this.feed

}


sealed class EntitySource


class EntitySourceOfficial : EntitySource()


data class EntitySourceLocal(val rowId : Long) : EntitySource()



interface Entity : Serializable
{
    val id : UUID
    fun entityId() : EntityId
    fun name() : String
    fun summary() : String
    fun entityLoader() : EntityLoader
}

