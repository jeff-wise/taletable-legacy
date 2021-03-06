
package com.taletable.android.rts.entity


import android.graphics.Color
import android.util.Log
import com.kispoko.culebra.*
import com.taletable.android.app.*
import com.taletable.android.lib.Factory
import com.taletable.android.model.book.Book
import com.taletable.android.model.campaign.Campaign
import com.taletable.android.model.game.Game
import com.taletable.android.model.engine.Engine
import com.taletable.android.model.engine.EngineValue
import com.taletable.android.model.engine.function.Function
import com.taletable.android.model.engine.function.FunctionId
import com.taletable.android.model.engine.mechanic.Mechanic
import com.taletable.android.model.engine.mechanic.MechanicCategory
import com.taletable.android.model.engine.mechanic.MechanicCategoryReference
import com.taletable.android.model.engine.mechanic.MechanicId
import com.taletable.android.model.engine.procedure.Procedure
import com.taletable.android.model.engine.procedure.ProcedureId
import com.taletable.android.model.engine.program.Program
import com.taletable.android.model.engine.program.ProgramId
import com.taletable.android.model.engine.reference.TextReference
import com.taletable.android.model.engine.summation.Summation
import com.taletable.android.model.engine.summation.SummationId
import com.taletable.android.model.engine.tag.TagQuery
import com.taletable.android.model.engine.task.Task
import com.taletable.android.model.engine.value.*
import com.taletable.android.model.engine.variable.*
import com.taletable.android.model.feed.Feed
import com.taletable.android.model.sheet.Sheet
import com.taletable.android.model.sheet.group.*
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.Theme
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.model.theme.official.officialAppThemeLight
import com.taletable.android.rts.entity.engine.TextReferenceIsNull
import com.taletable.android.rts.entity.sheet.SheetData
import com.taletable.android.rts.entity.theme.ThemeManager
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
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


fun entityType(entityId : EntityId) : AppEff<EntityType> =
        apply({it.entityType}, entityRecord(entityId))


//fun entitySheetRecord(sheetId : SheetId) : AppEff<EntitySheetRecord> =
//    entityRecord(EntitySheetId(sheetId)) apply {
//        when (it) {
//            is EntitySheetRecord -> effValue(it)
//            else                 -> {
//                effError<AppError,EntitySheetRecord>(AppEntityError(
//                        EntityIsUnexpectedType(EntitySheetId(sheetId),
//                                               EntityTypeSheet,
//                                               it.entityType)))
//            }
//        }
//    }


// ---------------------------------------------------------------------------------------------
// GET
// ---------------------------------------------------------------------------------------------

// GET > Sheet
// ---------------------------------------------------------------------------------------------

fun sheet(entityId : EntityId) : Maybe<Sheet>
{
    val entityState = entityRecordById[entityId]
    return when (entityState) {
        is EntitySheetRecord -> {
            Just(entityState.sheet)
        }
        else -> Nothing()
    }
}


fun sheetCampaign(sheetId : EntityId) : Maybe<Campaign> =
    sheet(sheetId).ap { campaign(it.campaignId()) }


fun sheetGame(sheetId : EntityId) : Maybe<Game> =
    sheet(sheetId)            ap {
    campaign(it.campaignId()) ap {
        game(it.gameId())
    } }


//sheet(record.sheet.entityId()) ap {
//                    campaign(it.campaignId()) ap {
//                        game(it.gameId()) ap {
//                            it.groupIndex().groupSetWithId(groupSetId)
//                        }
//                    }
//                }


fun sheetOrError(entityId : EntityId) : AppEff<Sheet>
{
    val record = entityRecordById[entityId]
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

fun campaign(entityId : EntityId) : Maybe<Campaign>
{
    val entityState = entityRecordById[entityId]
    return when (entityState) {
        is EntityCampaignRecord -> {
            Just(entityState.campaign)
        }
        else -> Nothing()
    }
}


fun game(entityId : EntityId) : Maybe<Game>
{
    val entityState = entityRecordById[entityId]
    return when (entityState) {
        is EntityGameRecord -> {
            Just(entityState.game)
        }
        else -> Nothing()
    }
}


fun campaignGame(campaignId : EntityId) : Maybe<Game> =
    campaign(campaignId).apply { game(it.gameId()) }


fun book(bookId : EntityId) : Maybe<Book>
{
    val entityState = entityRecordById[bookId]
    return when (entityState) {
        is EntityBookRecord -> {
            Just(entityState.book)
        }
        else -> Nothing()
    }
}


fun feed(feedId : EntityId) : Maybe<Feed>
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

fun addEntity(entity : Entity)
{
    when (entity)
    {
        is Sheet    -> addSheet(entity)
        is Campaign -> addCampaign(entity)
        is Game     -> addGame(entity)
        is Book     -> addBook(entity)
        is Feed     -> addFeed(entity)
    }
}


fun addSheet(sheet : Sheet)
{
    val engineState = EntityState(sheet.entityId(), mutableListOf(), mutableListOf())
    val sheetRecord = EntitySheetRecord(sheet, engineState, Just(sheet.settings().themeId()))

    entityRecordById[sheet.entityId()] = sheetRecord

    Log.d("***ENTITY", "Added sheet: ${sheet.name()}")
}


fun addCampaign(campaign : Campaign)
{
    val engineState = EntityState(campaign.entityId(), mutableListOf(), mutableListOf())
    val campaignRecord = EntityCampaignRecord(campaign, engineState, Nothing())

    entityRecordById[campaign.entityId()] = campaignRecord

    Log.d("***ENTITY", "Added campaign: ${campaign.name()}")
}


fun addGame(game : Game)
{
    val engineState = EntityState(game.entityId(), mutableListOf(), mutableListOf())
    val gameRecord = EntityGameRecord(game, engineState, Nothing())

    entityRecordById[game.entityId()] = gameRecord

    Log.d("***ENTITY", "Added game: ${game.name()}")
}


fun addBook(book : Book)
{
    val entityState = EntityState(book.entityId(), mutableListOf(), mutableListOf())
    val bookRecord = EntityBookRecord(book, entityState, Just(book.settings().themeId()))

    book.variables().forEach {
        entityState.addVariable(it)
    }

    entityRecordById[book.entityId()] = bookRecord

    Log.d("***ENTITY", "Added book: ${book.name()}")
}


fun addFeed(feed : Feed)
{
    val entityState = EntityState(feed.entityId(), mutableListOf(), mutableListOf())
    val bookRecord = EntityFeedRecord(feed, entityState, Just(feed.settings().themeId()))

    feed.variables().forEach {
        entityState.addVariable(it)
    }

    entityRecordById[feed.entityId()] = bookRecord

    Log.d("***ENTITY", "Added feed: ${feed.name()}")
}



// ---------------------------------------------------------------------------------------------
// INITIALIZE
// ---------------------------------------------------------------------------------------------

fun initialize(entityId : EntityId)
{
    entityRecord(entityId) apDo { record ->
        when (record) {
            is EntitySheetRecord -> {
                Log.d("***ENTITY", "initialize sheet")
                initializeSheet(record.sheet)
            }
            is EntityGameRecord -> {
                Log.d("***ENTITY", "initialize game")
                initializeGame(record.game)
            }
            is EntityBookRecord -> {
                Log.d("***ENTITY", "initialize book")
                initializeBook(record.book)
            }
        }
    }
}


fun initializeSheet(sheet : Sheet)
{
    val entityId = sheet.entityId()

//    sheet.variables().forEach {
//        addVariable(it, entityId)
//    }

    entityEngineState(entityId) apDo { entityState ->


        mechanics(entityId) apDo { mechanicSet ->
            entityState.setMechanics(mechanicSet.toList())
        }
        tasks(entityId)     apDo { taskSet ->
            entityState.setTasks(taskSet.toList())
        }
    }
}


fun initializeGame(game : Game)
{
    game.variables().forEach { variable ->
        if (variable.isContextual()) {
            entityEngineState(game.entityId()).apDo {
                it.addContextualVariable(variable)
            }
        }
        else {
            addVariable(variable, game.entityId())
        }
    }
}


fun initializeBook(book : Book)
{
    book.variables().forEach { variable ->
        addVariable(variable, book.entityId())
//        if (variable.isContextual()) {
//            entityEngineState(game.entityId()).apDo {
//                it.addContextualVariable(variable)
//            }
//        }
        //else {
        //}
    }
}



// ---------------------------------------------------------------------------------------------
// ENGINE
// ---------------------------------------------------------------------------------------------

/**
 * Engine
 */
fun entityEngines(entityId : EntityId) : AppEff<List<Engine>>
{
    val engines : MutableList<Engine> = mutableListOf()

    sheet(entityId).doMaybe { sheet ->
        engines.add(sheet.engine())
        val maybeCampaign = campaign(sheet.campaignId())
        when (maybeCampaign) {
            is Just -> {
                val campaign = maybeCampaign.value
                engines.add(campaign.engine())
                val maybeGame = game(campaign.gameId())
                when (maybeGame) {
                    is Just -> {
                        engines.add(maybeGame.value.engine())
                    }
                }
            }
        }
    }

    book(entityId).doMaybe {
        engines.add(it.engine())
    }

    return effValue(engines)
}


/**
 * Entity States
 */
fun entityStates(entityId : EntityId) : List<EntityState>
{
    val states : MutableList<EntityState> = mutableListOf()

    // if SHEET
    sheet(entityId).doMaybe { sheet ->
        entityEngineState(entityId) apDo { states.add(it) }

        val maybeCampaign = campaign(sheet.campaignId())
        when (maybeCampaign) {
            is Just -> {
                val campaign = maybeCampaign.value
                entityEngineState(campaign.entityId()) apDo { states.add(it) }
                val maybeGame = game(campaign.gameId())
                when (maybeGame) {
                    is Just -> {
                        val game = maybeGame.value
                        entityEngineState(game.entityId()) apDo { states.add(it) }
                    }
                }
            }
        }
    }

    // if FEED
    feed(entityId).doMaybe {
        entityEngineState(entityId) apDo { states.add(it) }
    }

    // if BOOK
    book(entityId).doMaybe {
        entityEngineState(entityId) apDo { states.add(it) }
    }

    return states
}


/**
 * Group With Id
 */
fun groupWithId(groupId : GroupId, entityId : EntityId) : Maybe<Group>
{
    val recordEff = entityRecord(entityId)

    return when (recordEff)
    {
        is Val ->
        {
            val record = recordEff.value
            when (record)
            {
                is EntitySheetRecord -> {
                    sheet(record.sheet.entityId()) ap {
                        campaign(it.campaignId()) ap {
                            game(it.gameId()) ap {
                                it.groupIndex().groupWithId(groupId)
                            }
                        }
                    }
                }
                is EntityFeedRecord -> {
                    feed(record.feed.entityId()) ap {
                        it.groupWithId(groupId)
                    }
                }
                is EntityBookRecord -> {
                    book(record.book.entityId()) ap {
                        it.groupIndex().groupWithId(groupId)
                    }
                }
                else -> Nothing()
            }
        }
        is Err -> Nothing()
    }

}

/**
 * Group Set With Id
 */
fun groupSetWithId(groupSetId : GroupSetId, entityId : EntityId) : List<Group>
{
    val recordEff = entityRecord(entityId)

    var setGroups : List<Group> = listOf()

    recordEff apDo { record ->
        when (record)
        {
            is EntitySheetRecord -> {
                val sheetId = record.sheet.entityId()
//                val maybeSheet = sheet(sheetId)
//                val maybeCampaign = sheetCampaign(sheetId)

                // TODO search heirarchy

                sheetGame(sheetId).doMaybe {
                    it.groupIndex().groupSetWithId(groupSetId).doMaybe {
                        setGroups = it.groups(sheetId)
                    }
                }


            }
        }
    }

    return setGroups
}


/**
 * Group By Tag
 */
fun groups(tagQuery : TagQuery, entityId : EntityId) : List<Group>
{
    val recordEff = entityRecord(entityId)

    return when (recordEff)
    {
        is Val ->
        {
            val record = recordEff.value
            when (record)
            {
                is EntitySheetRecord ->
                {
                    val game = sheet(record.sheet.entityId())
                                 .apply { campaign(it.campaignId()) }
                                 .apply { game(it.gameId()) }
                    when (game) {
                        is Just    -> game.value.groupIndex().groups(tagQuery)
                        is Nothing -> listOf()
                    }
                }
                else -> listOf()
            }
        }
        is Err -> listOf()
    }
}



fun groups(groupReferences : List<GroupReference>,
           entityId : EntityId,
           groupContext : Maybe<GroupContext> = Nothing()) : List<GroupInvocation>
{
    val groups : MutableList<GroupInvocation> = mutableListOf()

    groupReferences.forEach { groupRef ->

        val groupRefValue = groupRef.value
        when (groupRefValue) {
            is GroupReferenceLiteral -> groups.add(GroupInvocation(groupRefValue.group, groupContext))
            is GroupReferenceId -> {
                groupWithId(groupRefValue.groupId, entityId).doMaybe { group ->
                    val mGroupContext = groupRef.groupContext
                    when (mGroupContext) {
                        is Just -> groups.add(GroupInvocation(group, mGroupContext))
                        is Nothing -> groups.add(GroupInvocation(group))
                    }
                }
            }
            is GroupReferenceSetId -> {
                val setGroups = groupSetWithId(groupRefValue.groupSetId, entityId)
                                 .map { GroupInvocation(it) }
                groups.addAll(setGroups)
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
            effValue(_tasks)
        }
        is Err -> engines as AppEff<Set<Task>>
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

//fun variable(variableReference : VariableReference,
//             entityId : EntityId) : AppEff<Variable>
//{
//    entityStates(entityId).forEachIndexed { i, v ->
//        val _variable = v.variable(variableReference)
//        when (_variable) {
//            is Val -> {
//                return _variable
//            }
//        }
//    }
//
//    return effError(AppEntityError(EntityDoesNotHaveVariable(entityId, variableReference)))
//}


fun variable(variableReference : VariableReference,
             entityId : EntityId,
             context : Maybe<VariableNamespace> = Nothing()) : AppEff<Variable>
{
    entityStates(entityId).forEachIndexed { i, v ->
        val _variable = v.variable(variableReference, context)
        when (_variable) {
            is Val -> {
                return _variable
            }
        }
    }

    return effError(AppEntityError(EntityDoesNotHaveVariable(entityId, variableReference)))
}



fun variables(variableReference : VariableReference,
              entityId : EntityId) : Set<Variable>
{
    val variableSet : MutableSet<Variable> = mutableSetOf()

    entityStates(entityId).forEach {
        val _variables = it.variables(variableReference)
        when (_variables) {
            is Val -> variableSet.addAll(_variables.value)
        }
    }

    return variableSet
}


fun variablesWithTag(variableTag : VariableTag, entityId : EntityId) : AppEff<Set<Variable>> =
    entityEngineState(entityId).apply {
        effValue<AppError,Set<Variable>>(it.variablesWithTag(variableTag))
    }


fun onVariableUpdate(variable : Variable, entityId : EntityId) {
    entityEngineState(entityId) apDo { it.onVariableUpdate(variable) }
}


fun addVariable(variable : Variable, entityId : EntityId) {
    entityEngineState(entityId) apDo { it.addVariable(variable) }
}



fun setContextualVariable(variableId : VariableId,
                          context : VariableContext,
                          entityId: EntityId)
{
    entityStates(entityId).forEach {
        it.setContextualVariable(variableId, context)
    }
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
        variable(variableReference, entityId).apply { it.booleanVariable(entityId)  }


// | STATE > Variable > Boolean
// ---------------------------------------------------------------------------------------------

fun contentReferenceVariable(variableReference : VariableReference,
                             entityId : EntityId,
                             context : Maybe<VariableNamespace> = Nothing()) : AppEff<ContentReferenceVariable> =
        variable(variableReference, entityId, context).apply { it.contentReferenceVariable(entityId)  }


// Engine State > Variable > Dice Roll
// ---------------------------------------------------------------------------------------------

fun diceRollVariable(variableReference : VariableReference,
                     entityId : EntityId) : AppEff<DiceRollVariable> =
        variable(variableReference, entityId).apply { it.diceRollVariable(entityId)  }


// Engine State > Variable > Number
// ---------------------------------------------------------------------------------------------

fun numberVariable(variableReference : VariableReference,
                   entityId : EntityId,
                   context : Maybe<VariableNamespace> = Nothing()) : AppEff<NumberVariable> =
        variable(variableReference, entityId, context).apply { it.numberVariable(entityId)  }


fun numberVariables(variableReference : VariableReference,
                    entityId : EntityId) : AppEff<Set<NumberVariable>> =
        entityEngineState(entityId).apply { it.numberVariables(variableReference)  }


// Engine State > Variable > Text
// ---------------------------------------------------------------------------------------------

fun textVariable(variableReference : VariableReference,
                 entityId : EntityId,
                 context : Maybe<VariableNamespace> = Nothing()) : AppEff<TextVariable> =
       variable(variableReference, entityId, context).apply { it.textVariable(entityId)  }

//        entityEngineState(entityId).apply { it.textVariable(variableReference)  }


// Engine State > Variable > Text List
// ---------------------------------------------------------------------------------------------

fun textListVariable(variableReference : VariableReference,
                     entityId : EntityId,
                     context : Maybe<VariableNamespace> = Nothing()) : AppEff<TextListVariable>
{
    Log.d("***ENTITY", "text list var context is $context")
    return variable(variableReference, entityId, context).apply { it.textListVariable(entityId)  }
}


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


fun theme(entityId : EntityId) : AppEff<Theme> =
    entityThemeId(entityId).apply(ThemeManager::theme)

fun themeOrDefault(entityId : EntityId) : Theme
{
    val entityTheme = entityThemeId(entityId).apply(ThemeManager::theme)
    return when (entityTheme) {
        is Val -> entityTheme.value
        is Err -> officialAppThemeLight
    }
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

//sealed class EntityId : Serializable
//{
//    companion object
//    {
//        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityId> = when (yamlValue)
//        {
//            is YamlDict ->
//            {
//                yamlValue.text("type") apply {
//                    when (it) {
//                        "sheet"    -> yamlValue.at("sheet").apply(EntitySheetId.Companion::fromYaml) as YamlParser<EntityId>
//                        "campaign" -> yamlValue.at("campaign").apply(EntityCampaignId.Companion::fromYaml) as YamlParser<EntityId>
//                        "game"     -> yamlValue.at("game").apply(EntityGameId.Companion::fromYaml) as YamlParser<EntityId>
//                        "book"     -> yamlValue.at("book").apply(EntityBookId.Companion::fromYaml) as YamlParser<EntityId>
//                        else       -> effError<YamlParseError,EntityId>(
//                                        UnexpectedStringValue(it, yamlValue.path))
//                    }
//                }
//            }
//            else        -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
//        }
//    }
//
//}
//
//
//data class EntitySheetId(val sheetId : SheetId) : EntityId()
//{
//    override fun toString() = sheetId.value
//
//    companion object
//    {
//        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntitySheetId> =
//            apply(::EntitySheetId, SheetId.fromYaml(yamlValue))
//    }
//
//}
//
//
//data class EntityCampaignId(val campaignId : CampaignId) : EntityId()
//{
//    override fun toString() = campaignId.value
//
//    companion object
//    {
//        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityCampaignId> =
//            apply(::EntityCampaignId, CampaignId.fromYaml(yamlValue))
//    }
//
//}
//
//data class EntityGameId(val gameId : GameId) : EntityId()
//{
//    override fun toString() = gameId.value
//
//    companion object
//    {
//        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityGameId> =
//            apply(::EntityGameId, GameId.fromYaml(yamlValue))
//    }
//
//}
//
//data class EntityThemeId(val themeId : ThemeId) : EntityId()
//{
//    override fun toString() = themeId.toString()
//
////    companion object
////    {
////        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityThemeId> =
////            apply(::EntityThemeId, ThemeId.fromYaml(yamlValue))
////    }
//
//}
//
//data class EntityBookId(val bookId : BookId) : EntityId()
//{
//    override fun toString() = bookId.value
//
//    companion object
//    {
//        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityBookId> =
//            apply(::EntityBookId, BookId.fromYaml(yamlValue))
//    }
//
//}
//
//data class EntityFeedId(val id : UUID) : EntityId()
//{
//    override fun toString() = id.toString()
//}


data class EntityId(val value : UUID) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<EntityId>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<EntityId> = when (doc)
        {
            is DocText -> {
                try {
                    effValue<ValueError,EntityId>(EntityId(UUID.fromString(doc.text)))
                }
                catch (e : IllegalArgumentException) {
                    effError<ValueError,EntityId>(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }

        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityId> =
            when (yamlValue)
            {
                is YamlText ->
                {
                    try {
                        effValue<YamlParseError,EntityId>(EntityId(UUID.fromString(yamlValue.text)))
                    }
                    catch (e : IllegalArgumentException) {
                        error(UnexpectedTypeFound(YamlType.TEXT, yamlType(yamlValue), yamlValue.path))
                    }
                }
                else        -> error(UnexpectedTypeFound(YamlType.TEXT,
                                                         yamlType(yamlValue),
                                                         yamlValue.path))
            }

        fun fromString(uuidString : String) : EntityId?
        {
            return try {
                EntityId(UUID.fromString(uuidString))
            }
            catch (e : IllegalArgumentException) {
                null
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value.toString())

}


// Entity Type
// ---------------------------------------------------------------------------------------------

sealed class EntityType
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {

        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityType> = when (yamlValue)
        {
            is YamlText ->
            {
                when (yamlValue.text)
                {
                    "sheet"     -> effValue<YamlParseError,EntityType>(EntityTypeSheet)
                    "campaign"  -> effValue<YamlParseError,EntityType>(EntityTypeCampaign)
                    "game"      -> effValue<YamlParseError,EntityType>(EntityTypeGame)
                    "theme"     -> effValue<YamlParseError,EntityType>(EntityTypeTheme)
                    "book"      -> effValue<YamlParseError,EntityType>(EntityTypeBook)
                    else        -> error(UnexpectedStringValue(yamlValue.text, yamlValue.path))
                }
            }
            else        -> error(UnexpectedTypeFound(YamlType.TEXT,
                                                     yamlType(yamlValue),
                                                     yamlValue.path))
        }


    }

}

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


data class EntityKindId(val value : String) : Serializable
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
//    val id : UUID
    fun entityId() : EntityId
    fun name() : String
    fun category() : String
    fun summary() : String
}




sealed class EntityIndexType
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityIndexType> = when (yamlValue)
        {
            is YamlText ->
            {
                when (yamlValue.text)
                {
                    "group"    -> effValue<YamlParseError,EntityIndexType>(EntityIndexTypeGroup())
                    "variable" -> effValue<YamlParseError,EntityIndexType>(EntityIndexTypeVariable())
                    else       -> error(UnexpectedStringValue(yamlValue.text, yamlValue.path))
                }
            }
            else        -> error(UnexpectedTypeFound(YamlType.TEXT,
                                                     yamlType(yamlValue),
                                                     yamlValue.path))
        }


    }

}


class EntityIndexTypeGroup : EntityIndexType()

class EntityIndexTypeVariable : EntityIndexType()
