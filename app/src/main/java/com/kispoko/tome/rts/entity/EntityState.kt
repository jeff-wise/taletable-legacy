
package com.kispoko.tome.rts.entity


import android.util.Log
import com.kispoko.tome.app.*
import com.kispoko.tome.model.engine.EngineValue
import com.kispoko.tome.model.engine.EngineValueBoolean
import com.kispoko.tome.model.engine.EngineValueNumber
import com.kispoko.tome.model.engine.EngineValueText
import com.kispoko.tome.model.engine.mechanic.*
import com.kispoko.tome.model.engine.task.Task
import com.kispoko.tome.model.engine.variable.*
import com.kispoko.tome.rts.entity.sheet.*
import effect.*
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import org.apache.commons.collections4.trie.PatriciaTrie
import java.util.*



/**
 * The state interface.
 */
interface State
{
    fun addVariable(variable : Variable, parentVariable : Variable? = null)
}


data class RelationListener(val tag : VariableTag, val variableId : VariableId)


/**
 * State
 *
 * Game data for a sheet.
 */
class EntityState(val entityId : EntityId,
                  val mechanics : MutableList<Mechanic>,
                  val tasks : MutableList<Task>) : State, MechanicStateMachine
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // Variable Indexes
    // -----------------------------------------------------------------------------------------

    private val variableById : MutableMap<VariableId,Variable> = mutableMapOf()
    private val variablesByTag : MutableMap<VariableTag,MutableSet<Variable>> = mutableMapOf()

    private val companionVariableIdsByVariableId : MutableMap<VariableId,MutableSet<VariableId>> = mutableMapOf()

    private val listenersById : MutableMap<VariableId,MutableSet<Variable>> = mutableMapOf()
    private val listenersByTag : MutableMap<VariableTag,MutableSet<Variable>> = mutableMapOf()
    private val listenerTagsByRelation : MutableMap<VariableRelation,MutableSet<RelationListener>> = mutableMapOf()

    private val onChangeListenersById : MutableMap<VariableId,MutableSet<VariableChangeListener>> = mutableMapOf()


    // Mechanic Indexes
    // -----------------------------------------------------------------------------------------

    private val mechanicsByReqVariableId : MutableMap<VariableId,MutableSet<MechanicState>> =
            mutableMapOf()

    private val activeMechanicsByCateogryId : MutableMap<MechanicCategoryReference,MutableSet<Mechanic>> = mutableMapOf()

    private val activeMechanicsByCateogryTag : MutableMap<MechanicCategoryTag,MutableSet<Mechanic>> = mutableMapOf()


    // Task Indexes
    // -----------------------------------------------------------------------------------------

    private val tasksByVariableId : MutableMap<VariableId,MutableSet<Task>> = mutableMapOf()

    private val activeTasks : MutableSet<Task> = mutableSetOf()


    // Search Indexes
    // -----------------------------------------------------------------------------------------

    private val activeVariableIdTrie : PatriciaTrie<Variable> = PatriciaTrie()
    private val activeVariableLabelTrie : PatriciaTrie<Variable> = PatriciaTrie()


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.indexMechanics(mechanics)

        this.indexTasks()
    }


    fun setMechanics(mechanics : List<Mechanic>)
    {
        this.indexMechanics(mechanics)
    }


    fun setTasks(tasks : List<Task>)
    {
        this.tasks.clear()
        this.tasks.addAll(tasks)
        this.indexTasks()
    }


    private fun indexMechanics(mechanics : List<Mechanic>)
    {
        // Index Mechanics
        for (mechanic in mechanics)
        {
            if (mechanic.requirements().isEmpty())
            {
                this.addMechanic(mechanic)
            }
            else
            {
                for (variableId in mechanic.requirements())
                {
                    if (!this.mechanicsByReqVariableId.containsKey(variableId))
                        this.mechanicsByReqVariableId.put(variableId, mutableSetOf())

                    val mechanicState = MechanicState(mechanic)
                    this.mechanicsByReqVariableId[variableId]!!.add(mechanicState)
                }
            }
        }
    }


    private fun indexTasks()
    {
        Log.d("****ENTITY STATE", "index tasks")
        this.tasks.forEach { task ->

            task.trigger.variableReferences().forEach {
                when (it) {
                    is VariableId -> {
                        Log.d("****ENTITY STATE", "index task at var id: $it")
                        if (!this.tasksByVariableId.containsKey(it))
                            this.tasksByVariableId.put(it, mutableSetOf())

                        this.tasksByVariableId[it]!!.add(task)
                    }
                }
            }

        }

    }


    // -----------------------------------------------------------------------------------------
    // VARIABLES
    // -----------------------------------------------------------------------------------------


    /**
     * Add a variable to the index. If a variable with the same name already exists in the index,
     * then the new variable replaces the old variable.
     * @param variable The variable to add.
    */
    override fun addVariable(variable : Variable, parentVariable : Variable?)
    {
        // TODO add log event for if variable with name already exist
        // TODO make sure variable does not depend on itself.
        val variableId = variable.variableId()

        if (this.variableById.containsKey(variableId))
            return

        // (1) Index variable by name
        // -------------------------------------------------------------------------------------

        variableById.put(variableId, variable)

        // (2) Index variable for search
        // -------------------------------------------------------------------------------------

        activeVariableIdTrie.put(variableId.toString(), variable)
        activeVariableLabelTrie.put(variable.label().value, variable)

        // (3) Index the variable by tag
        // -------------------------------------------------------------------------------------

        for (tag in variable.tags())
        {

            ApplicationLog.event(AppStateEvent(VariableTagAdded(variableId, tag)))

            if (variablesByTag.containsKey(tag))
                variablesByTag[tag]!!.add(variable)
            else
                variablesByTag.put(tag, mutableSetOf(variable))
        }

        // (4) Index the variable's dependencies
        //     For each dependency, we add this variable as a listener. That is, in the state
        //     graph we add a forward edge from each dependency variable to this one, so that
        //     when the dependency variable is updated it can update this variable as well.
        // -------------------------------------------------------------------------------------

        for (variableRef in variable.dependencies(this.entityId))
        {
            when (variableRef)
            {
                is VariableId ->
                {
                    if (listenersById.containsKey(variableRef))
                        listenersById[variableRef]!!.add(variable)
                    else
                        listenersById.put(variableRef, mutableSetOf(variable))
                }
                is VariableTag ->
                {
                    if (listenersByTag.containsKey(variableRef))
                        listenersByTag[variableRef]!!.add(variable)
                    else
                        listenersByTag.put(variableRef, mutableSetOf(variable))
                }
                is RelatedVariableSet ->
                {
                    val tag = variableRef.tag
                    val relation = variableRef.relation
                    if (!listenerTagsByRelation.containsKey(relation))
                        listenerTagsByRelation.put(relation, mutableSetOf())

                    val listenerSet = listenerTagsByRelation[relation]
                    listenerSet!!.add(RelationListener(tag, variable.variableId()))
                }
            }
        }

        // 4 (B) Also add parent as dependency
        // -------------------------------------------------------------------------------------
//
//        if (parentVariable != null) {
//            val parentVarId = parentVariable.variableId()
//            if (listenersById.containsKey(parentVarId)))
//                listenersById[parentVarId]!!.add(variable)
//            else
//                listenersById.put(variableRef, mutableSetOf(variable))
//        }


        // (5) Update any mechanics which are dependent on this variable
        // -------------------------------------------------------------------------------------

        when (variable) {
            is BooleanVariable ->
                mechanicsByReqVariableId[variableId]?.forEach { it.update(variable, this) }
        }

        // (5.5) Call variable on add to state
        // -------------------------------------------------------------------------------------

        variable.onAddToState(this.entityId, parentVariable)


        // (6) Add companion variables to the state
        // -------------------------------------------------------------------------------------

        val companionVariables = variable.companionVariables(this.entityId)
        when (companionVariables)
        {
            is Val -> {

                val companionVariableIds : MutableSet<VariableId> = mutableSetOf()

                companionVariables.value.forEach {
                    this.addVariable(it, variable)
                    companionVariableIds.add(it.variableId())
                }

                companionVariableIdsByVariableId.put(variableId, companionVariableIds)
            }
            is Err -> ApplicationLog.error(companionVariables.error)
        }

        // (7) Create history variable
        // -------------------------------------------------------------------------------------

        when (variable)
        {
            is NumberVariable ->
            {
                this.addVariable(variable.historyVariable())
            }
        }


        // (8) Notify all current listeners of this variable
        // -------------------------------------------------------------------------------------

        this.updateListeners(variable, UUID.randomUUID())

        ApplicationLog.event(AppStateEvent(VariableAdded(variableId)))

    }


    fun removeVariable(variableId : VariableId)
    {
        if (!this.variableById.containsKey(variableId))
            return

        val variable = this.variableById[variableId]

        // (1) Notify listeners (before they are removed)
        // -------------------------------------------------------------------------------------


        // (2) Remove from Id index
        // -------------------------------------------------------------------------------------

        variableById.remove(variableId)


        // (3) Remove from serach index
        // -------------------------------------------------------------------------------------

        activeVariableIdTrie.remove(variableId.toString())
        activeVariableLabelTrie.remove(variable!!.label().value)

        // (4) Un-index tags
        // -------------------------------------------------------------------------------------

        for (tag in variable.tags())
        {
            if (variablesByTag.containsKey(tag))
            {
                val variableSet = variablesByTag[tag]
                variableSet?.remove(variable)
            }
        }


        this.onRemoveUpdateListeners(variable!!, UUID.randomUUID())

        // (5) Unindex dependencies
        // -------------------------------------------------------------------------------------

        for (variableRef in variable.dependencies(this.entityId))
        {
            when (variableRef)
            {
                is VariableId ->
                {
                    listenersById[variableRef]?.remove(variable)
                }
                is VariableTag ->
                {
                    listenersByTag[variableRef]?.remove(variable)
                }
                is RelatedVariableSet ->
                {
//                    val relation = variableRef.relation
//                    val tag = variableRef.tag
//                    if (listenerTagsByRelation.containsKey(relation))
//                    {
//                        val tagMap = listenerTagsByRelation[relation]!!
//                        if (tagMap.containsKey(tag))
//                        {
//                            val currentCount = tagMap[tag]!!
//                            if (currentCount == 1)
//                                tagMap.remove(tag)
//                            else
//                                tagMap.put(tag, currentCount - 1)
//                        }
//                    }
                }
            }
        }

        // (6) Update any mechanics which are dependent on this variable
        // -------------------------------------------------------------------------------------

        when (variable)
        {
            is BooleanVariable ->
            {
//                mechanicsByReqVariableId[variableId]?.forEach {
//                    val isReady = it.update(variable)
//                    if (isReady) this.addMechanic(it.mechanic)
//                }
            }
        }

        ApplicationLog.event(AppStateEvent(VariableRemoved(variableId)))
    }


    fun updateVariableId(currentVariableId : VariableId, newVariableId : VariableId)
    {
        // TODO update history variable as well

        // (1) Get Variable
        val currentVariable = this.variableById[currentVariableId]

        if (currentVariable == null || this.variableById.containsKey(newVariableId))
            return

        // (3) Remove variable with old id
        this.removeVariable(currentVariableId)

        currentVariable.setVariableId(newVariableId)

        // (3) Add variable back with new id
        this.addVariable(currentVariable)

        ApplicationLog.event(AppStateEvent(VariableRenamed(currentVariableId, newVariableId)))
    }


    override fun addMechanic(mechanic : Mechanic)
    {
        mechanic.variables().forEach { this.addVariable(it) }

        val mechanicAddedEvent =
                MechanicAdded(mechanic.mechanicId(),
                        mechanic.variables().map { it.variableId() }.toSet())
        val event = AppStateEvent(mechanicAddedEvent)

        ApplicationLog.event(event)

        val categoryId = mechanic.categoryId()
        if (!this.activeMechanicsByCateogryId.containsKey(mechanic.categoryId()))
            this.activeMechanicsByCateogryId.put(categoryId, mutableSetOf())

        val activeMechanics = this.activeMechanicsByCateogryId[categoryId]
        activeMechanics?.add(mechanic)

        // look up category, get tags, index mechanic by each tag.
        // Index by category tag
        val _mechanicCategory = mechanicCategory(mechanic.categoryId(), entityId)
        when (_mechanicCategory) {
            is Val -> {
                _mechanicCategory.value.tags().forEach { tag ->
                    if (!this.activeMechanicsByCateogryTag.containsKey(tag))
                        this.activeMechanicsByCateogryTag.put(tag, mutableSetOf())

                    val activeMechanicSet = this.activeMechanicsByCateogryTag[tag]
                    activeMechanicSet?.add(mechanic)

                }
            }
        }

    }


    override fun removeMechanic(mechanic : Mechanic)
    {
        when (mechanic.mechanicType()) {
            is MechanicType.Auto -> {
                mechanic.variables().forEach { this.removeVariable(it.variableId()) }
                val event = MechanicRemoved(mechanic.mechanicId(),
                        mechanic.variables().map { it.variableId() }.toSet())
                ApplicationLog.event(AppStateEvent(event))
            }
            is MechanicType.OptionSelected -> {
                mechanic.variables().forEach { this.removeVariable(it.variableId()) }
                val event = MechanicRemoved(mechanic.mechanicId(),
                        mechanic.variables().map { it.variableId() }.toSet())
                ApplicationLog.event(AppStateEvent(event))
            }
            else -> {
                val event = MechanicRemoved(mechanic.mechanicId(), setOf())
                ApplicationLog.event(AppStateEvent(event))
            }
        }


        // Unindex mechanic by category id
        val categoryId = mechanic.categoryId()
        if (this.activeMechanicsByCateogryId.containsKey(categoryId)) {
            val activeMechanics = this.activeMechanicsByCateogryId[categoryId]
            activeMechanics?.remove(mechanic)
        }

        // Unindex mechanic by category tag
        val _mechanicCategory = mechanicCategory(mechanic.categoryId(), entityId)
        when (_mechanicCategory) {
            is Val -> {
                _mechanicCategory.value.tags().forEach { tag ->
                    if (this.activeMechanicsByCateogryTag.containsKey(tag))
                    {
                        val activeMechanicSet = this.activeMechanicsByCateogryTag[tag]
                        activeMechanicSet?.remove(mechanic)
                    }
                }
            }
        }
    }


    override fun activeMechanicsInCategory(categoryReference: MechanicCategoryReference) : Set<Mechanic> =
        when (categoryReference)
        {
            is MechanicCategoryId ->
            {
                if (this.activeMechanicsByCateogryId.containsKey(categoryReference))
                    this.activeMechanicsByCateogryId[categoryReference]!!
                else
                    setOf()
            }
            is MechanicCategoryTag ->
            {
                if (this.activeMechanicsByCateogryTag.containsKey(categoryReference))
                    this.activeMechanicsByCateogryTag[categoryReference]!!
                else
                    setOf()
            }
        }


    fun activeTasks() : Set<Task> = this.activeTasks


    fun onVariableUpdate(variable : Variable)
    {

    //    ApplicationLog.event(AppStateEvent(VariableUpdated(variable.variableId())))

        // TODO redunant?
//        when (variable) {
//            is BooleanVariable -> {
//                mechanicsByReqVariableId[variable.variableId()]?.forEach {
//                    it.update(variable, this)
//                }
//            }
//        }

        // Remove previous companion variables
        val variableId = variable.variableId()

        if (companionVariableIdsByVariableId.containsKey(variableId))
        {
            companionVariableIdsByVariableId[variable.variableId()]?.forEach {
                removeVariable(it)
            }

            companionVariableIdsByVariableId.put(variableId, mutableSetOf())
        }


        // (6) Add companion variables to the state
        // -------------------------------------------------------------------------------------

        val companionVariables = variable.companionVariables(this.entityId)
        when (companionVariables)
        {
            is Val -> {

                val companionVariableIds : MutableSet<VariableId> = mutableSetOf()

                companionVariables.value.forEach {
                    this.addVariable(it, variable)
                    companionVariableIds.add(it.variableId())
                }

                companionVariableIdsByVariableId.put(variableId, companionVariableIds)
            }
            is Err -> ApplicationLog.error(companionVariables.error)
        }


//
//        val companionVariables = variable.companionVariables(this.entityId)
//        when (companionVariables)
//        {
//            is Val -> companionVariables.value.forEach { this.addVariable(it, variable)
//            }
//            is Err -> ApplicationLog.error(companionVariables.error)
//        }
//

        this.updateListeners(variable, UUID.randomUUID())

        this.onVariableChange(variableId)
    }


    fun onVariableChange(variableId : VariableId)
    {
        if (onChangeListenersById.containsKey(variableId))
        {
            val listeners = onChangeListenersById[variableId]
            listeners?.forEach { it.onRemove() }
        }
    }


    fun onRemoveUpdateListeners(variable : Variable, updateId : UUID)
    {

        // (1) Update listeners of variable id
        // -------------------------------------------------------------------------------------

        val variableId = variable.variableId()
        if (listenersById.containsKey(variableId))
        {
            for (listener in listenersById[variableId]!!)
            {
                if (listener.lastUpdateId != updateId)
                {
                    onVariableChange(listener.variableId())
                    listener.lastUpdateId = updateId
                    this.updateListeners(listener, updateId)
                }
            }

        }

        // (2) Update listeners of variable tag
        // -------------------------------------------------------------------------------------

        for (tag in variable.tags())
        {
            if (listenersByTag.containsKey(tag))
            {
                for (listener in listenersByTag[tag]!!)
                {
                    if (listener.lastUpdateId != updateId)
                    {
//                        listener.onUpdate()
                        onVariableChange(listener.variableId())
                        listener.lastUpdateId = updateId
                        this.updateListeners(listener, updateId)
                    }
                }
            }
        }

        // (3) Update any mechanics
        // -------------------------------------------------------------------------------------

        when (variable)
        {
            is BooleanVariable ->
                mechanicsByReqVariableId[variableId]?.forEach {
                    it.updateOnVariableRemoved(variable.variableId(), this)
                }
        }


        // (3) Update listeners of variable relation
        // -------------------------------------------------------------------------------------

//        val relation = variable.relation()
//        when (relation)
//        {
//            is Just ->
//            {
//                if (listenerTagsByRelation.containsKey(relation.value))
//                {
//                    val tagMap = listenerTagsByRelation[relation.value]
//                    val tags = tagMap!!.keys
//                    tags.forEach {
//                        this.variables
//
//                    }
//                }
//            }
//        }

        // (4) Update on change listeners
        // -------------------------------------------------------------------------------------

        if (onChangeListenersById.containsKey(variableId))
        {
            val listeners = onChangeListenersById[variableId]
            listeners?.forEach { it.onRemove() }
        }
    }


    fun updateVariable(variableId : VariableId,
                       engineValue : EngineValue,
                       entityId : EntityId)
    {
        when (engineValue)
        {
            is EngineValueBoolean ->
            {
                this.booleanVariable(variableId) apDo { booleanVariable ->
                    booleanVariable.updateValue(engineValue.value, entityId)

                    // calling this in Variable.kt to ensure onUpdate is called afterwards
                    //this.onVariableUpdate(booleanVariable)
                }

//                val variableEff = this.variableWithId(variableId)
//                when (variableEff) {
//                    is Val -> {
//                        val variable = variableEff.value
//                        when (variable) {
//                            is BooleanVariable ->
//                                mechanicsByReqVariableId[variableId]?.forEach { it.update(variable, this) }
//                        }
//                    }
//                    is Err -> ApplicationLog.error(variableEff.error)
//                }
            }
            is EngineValueNumber ->
            {
                this.numberVariable(variableId) apDo { numberVariable ->
                    numberVariable.updateValue(engineValue.value, entityId)
                }
            }
            is EngineValueText ->
            {
                this.textVariable(variableId) apDo { textVar ->
                    textVar.updateValue(engineValue.value, entityId)
                }
            }
        }
    }


    /**
     * Update all listeners that the variable has been changed.
     */
    fun updateListeners(variable : Variable, updateId : UUID)
    {
        ApplicationLog.event(AppStateEvent(VariableUpdated(variable.variableId())))

        // (1) Update listeners of variable id
        // -------------------------------------------------------------------------------------

        val variableId = variable.variableId()
        if (listenersById.containsKey(variableId))
        {
            for (listener in listenersById[variableId]!!)
            {
                if (listener.lastUpdateId != updateId)
                {
                    Log.d("****WIDGET", "updating variable: ${listener.variableId()}")
//                    listener.onUpdate()
                    onVariableChange(listener.variableId())
                    listener.lastUpdateId = updateId
                    this.updateListeners(listener, updateId)
                }
            }

        }

        // (2) Update listeners of variable tag
        // -------------------------------------------------------------------------------------

        for (tag in variable.tags())
        {
            if (listenersByTag.containsKey(tag))
            {
                for (listener in listenersByTag[tag]!!)
                {
                    if (listener.lastUpdateId != updateId)
                    {
//                        listener.onUpdate()
                        onVariableChange(listener.variableId())
                        listener.lastUpdateId = updateId
                        this.updateListeners(listener, updateId)
                    }
                }
            }
        }

        // (3) Update any relation listeners
        // -------------------------------------------------------------------------------------

        val mRelation = variable.relation()
        when (mRelation)
        {
            is Just ->
            {
                val relation = mRelation.value
                if (listenerTagsByRelation.containsKey(relation))
                {
                    val listeners = listenerTagsByRelation[relation]!!
                    listeners.forEach { listener ->
                        val parentWithTag = variable.relatedParents().any { parentVarId ->
                            val parentVar = this.variable(parentVarId)
                            when (parentVar) {
                                is Val -> parentVar.value.tags().contains(listener.tag)
                                is Err -> false
                            }
                        }
                        if (parentWithTag)
                        {
                            this.variable(listener.variableId) apDo { listenerVar ->
                                if (listenerVar.lastUpdateId != updateId)
                                {
//                                    listenerVar.onUpdate()
                                    onVariableChange(listenerVar.variableId())
                                    listenerVar.lastUpdateId = updateId
                                    this.updateListeners(listenerVar, updateId)
                                }
                            }
                        }
                    }
                }
            }
        }

        // (3) Update any mechanics
        // -------------------------------------------------------------------------------------

        when (variable)
        {
            is BooleanVariable ->
                mechanicsByReqVariableId[variableId]?.forEach { it.update(variable, this) }
        }


        // (4) Update any tasks
        // -------------------------------------------------------------------------------------

        if (this.tasksByVariableId.containsKey(variableId))
        {
            // TODO avoid duplicate checks
            this.tasksByVariableId[variableId]?.forEach { task ->
                Log.d("****ENTITY STATE", "updating task for: ${variableId}")
                if (task.trigger().isActive(entityId)) {
                    Log.d("****ENTITY STATE", "is active for: ${variableId}")
                    this.activeTasks.add(task)
                }
            }
        }


        // (5) Update on change listeners
        // -------------------------------------------------------------------------------------

        if (onChangeListenersById.containsKey(variableId))
        {
            val listeners = onChangeListenersById[variableId]
            listeners?.forEach { it.onUpdate(variable) }
        }

    }


    // -----------------------------------------------------------------------------------------
    // ON CHANGE LISTENERS
    // -----------------------------------------------------------------------------------------

    fun addVariableOnChangeListener(variableId : VariableId, listener : VariableChangeListener)
    {
        if (!this.onChangeListenersById.containsKey(variableId))
            this.onChangeListenersById.put(variableId, mutableSetOf())

        val onChangeListeners = this.onChangeListenersById[variableId]
        onChangeListeners?.add(listener)
    }


    // -----------------------------------------------------------------------------------------
    // LOOKUP
    // -----------------------------------------------------------------------------------------

    // Variable
    // -----------------------------------------------------------------------------------------

    fun variable(variableId : VariableId) : AppEff<Variable> =
            note(this.variableById[variableId],
                 AppStateError(VariableWithIdDoesNotExist(entityId, variableId)))


    fun variable(variableReference : VariableReference,
                 context : Maybe<VariableNamespace> = Nothing()) : AppEff<Variable>
    {
        fun firstVariable(variableSet : Set<Variable>) : AppEff<Variable> =
                note(variableSet.firstOrNull(),
                        AppStateError(VariableDoesNotExist(entityId, variableReference)))

        return this.variables(variableReference, context).apply(::firstVariable)
    }


    // Variables
    // -----------------------------------------------------------------------------------------

    fun variables() : Collection<Variable> = this.variableById.values


    fun variables(variableReference : VariableReference,
                  context : Maybe<VariableNamespace> = Nothing()) : AppEff<Set<Variable>> =
        when (variableReference)
        {
            is VariableId      -> effApply(::setOf, this.variable(variableReference))
            is VariableTag     -> this.variablesWithTag(variableReference)
            is VariableContext -> {
                when (context) {
                    is Just -> {
                        val contextId = VariableId(context.value, VariableName(variableReference.value))
                        effApply(::setOf, this.variable(contextId))
                    }
                    else    -> effError<AppError,Set<Variable>>(AppStateError(NoContext(variableReference)))
                }

            }
            is RelatedVariable  -> {
                val variableId = VariableId(variableReference.name.value)
                val relation = variableReference.relation
                this.variable(variableId)
                    .apply { effValue<AppError,Maybe<VariableId>>(it.relatedVariableId(relation)) }
                    .apply { note<AppError,VariableId>(it.toNullable(), AppStateError(VariableDoesNotHaveRelation(variableId, relation))) }
                    .apply { this.variable(it) }
                    .apply { effValue<AppError,Set<Variable>>(setOf(it)) }
            }
            is RelatedVariableSet -> {
                val relation = variableReference.relation
                val variableTag = variableReference.tag
                fun relatedVariables(variableSet : Set<Variable>) : Set<Variable> {
                    val relatedVariableSet : MutableSet<Variable> = mutableSetOf()
                    variableSet.forEach {
                        val relatedVarId = it.relatedVariableId(relation)
                        when (relatedVarId) {
                             is Just -> {
                                 val relatedVar = this.variable(relatedVarId.value)
                                 when (relatedVar) {
                                     is Val -> relatedVariableSet.add(relatedVar.value)
                             }
                        } }
                    }
                    return relatedVariableSet
                }
                this.variablesWithTag(variableTag)
                     .apply { effValue<AppError,Set<Variable>>(relatedVariables(it)) }
            }

        }


    fun variablesWithTag(variableTag : VariableTag) : AppEff<Set<Variable>> =
        note(this.variablesByTag.get(variableTag),
             AppStateError(VariableWithTagDoesNotExist(entityId, variableTag)))




    // Variable > Boolean
    // -----------------------------------------------------------------------------------------

    fun booleanVariable(variableReference : VariableReference) : AppEff<BooleanVariable> =
        variable(variableReference).apply { it.booleanVariable(this.entityId) }


    // Variable > Dice Roll
    // -----------------------------------------------------------------------------------------

    fun diceRollVariable(variableReference : VariableReference) : AppEff<DiceRollVariable> =
        variable(variableReference).apply { it.diceRollVariable(this.entityId) }


    // Variable > Number
    // -----------------------------------------------------------------------------------------

    fun numberVariable(variableReference : VariableReference) : AppEff<NumberVariable> =
        variable(variableReference).apply { it.numberVariable(this.entityId) }


    fun numberVariables(variableReference : VariableReference,
                        context : Maybe<VariableNamespace> = Nothing()) : AppEff<Set<NumberVariable>>
    {
        return this.variables(variableReference, context) ap {
            val numberVariableSet : MutableSet<NumberVariable> = mutableSetOf()
            it.forEach { variable ->
                when (variable) {
                    is NumberVariable -> numberVariableSet.add(variable)
                }
            }
            effValue<AppError,Set<NumberVariable>>(numberVariableSet)
        }
    }


    // Variable > Text
    // -----------------------------------------------------------------------------------------

    fun textVariable(variableReference : VariableReference) : AppEff<TextVariable> =
        variable(variableReference).apply { it.textVariable(this.entityId) }


    // Variable > Text List
    // -----------------------------------------------------------------------------------------

    fun textListVariable(variableReference : VariableReference) : AppEff<TextListVariable> =
        variable(variableReference).apply { it.textListVariable(this.entityId) }



    // -----------------------------------------------------------------------------------------
    // LISTEN
    // -----------------------------------------------------------------------------------------

    val onChangeListenerByVariableId : MutableMap<VariableId, VariableChangeListener> = mutableMapOf()

    // TODO optimize this with indexes
    val triggersRelatedVariableSet : MutableSet<RelatedVariableSet> = mutableSetOf()


    fun listen(variableReference : VariableReference,
               listener : VariableChangeListener) = when (variableReference)
    {
        is RelatedVariableSet -> {
            this.triggersRelatedVariableSet.add(variableReference)
        }
        else -> { }
    }


}


interface MechanicStateMachine
{
    fun addMechanic(mechanic : Mechanic)
    fun removeMechanic(mechanic : Mechanic)

    fun activeMechanicsInCategory(categoryId : MechanicCategoryReference) : Set<Mechanic>
}


data class MechanicState(val state : MutableMap<VariableId,Boolean>,
                         val mechanic : Mechanic)
{

    // if variable doesn't exist set to false

    var isActive : Boolean = false

    constructor(mechanic : Mechanic)
        : this(mechanic.requirements().associate { Pair(it, false) }.toMutableMap(),
               mechanic)


    fun update(variable : BooleanVariable,
               mechanicStateMachine : MechanicStateMachine)
    {
        val variableId = variable.variableId()

        if (state.containsKey(variableId))
        {
            val value = variable.value()

            when (value)
            {
                is Val ->
                {
                    state[variableId] = value.value
                    if (this.isReady()) {
                        this.isActive = true
                        mechanicStateMachine.addMechanic(mechanic)
                    }
                    else if (!this.isReady() && this.isActive) {
                        this.isActive = false
                        mechanicStateMachine.removeMechanic(mechanic)
                    }
                }
                is Err -> ApplicationLog.error(value.error)
            }
        }
    }


    fun updateOnVariableRemoved(variableId : VariableId,
                                mechanicStateMachine : MechanicStateMachine)
    {
        if (state.containsKey(variableId))
        {
            state[variableId] = false

            if (!this.isReady() && this.isActive) {
                this.isActive = false
                mechanicStateMachine.removeMechanic(mechanic)
            }
        }
    }



    private fun isReady() = this.state.values.all { it }


}


// ---------------------------------------------------------------------------------------------
// ON CHANGE LISTERER
// ---------------------------------------------------------------------------------------------

data class VariableChangeListener(val onUpdate : (Variable) -> Unit,
                                  val onRemove : () -> Unit)

