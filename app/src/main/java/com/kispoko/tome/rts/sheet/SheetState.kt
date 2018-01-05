
package com.kispoko.tome.rts.sheet


import android.util.Log
import com.kispoko.tome.app.*
import com.kispoko.tome.model.game.engine.mechanic.Mechanic
import com.kispoko.tome.model.game.engine.variable.*
import effect.*
import org.apache.commons.collections4.trie.PatriciaTrie



/**
 * The state interface.
 */
interface State
{
    fun addVariable(variable : Variable)
}


/**
 * State
 *
 * Game data for a sheet.
 */
class SheetState(val sheetContext : SheetContext,
                 mechanics : List<Mechanic>) : State, MechanicStateMachine
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // Variable Indexes
    // -----------------------------------------------------------------------------------------

    private val variableById : MutableMap<VariableId,Variable> = mutableMapOf()
    private val variablesByTag : MutableMap<VariableTag,MutableSet<Variable>> = mutableMapOf()

    private val listenersById : MutableMap<VariableId,MutableSet<Variable>> = mutableMapOf()
    private val listenersByTag : MutableMap<VariableTag,MutableSet<Variable>> = mutableMapOf()

    private val onChangeListenersById : MutableMap<VariableId,MutableSet<OnVariableChangeListener>> = mutableMapOf()


    // Mechanic Indexes
    // -----------------------------------------------------------------------------------------

    private val mechanicsByReqVariableId : MutableMap<VariableId,MutableSet<MechanicState>> =
            mutableMapOf()

    // Search Indexes
    // -----------------------------------------------------------------------------------------

    private val activeVariableIdTrie : PatriciaTrie<Variable> = PatriciaTrie()
    private val activeVariableLabelTrie : PatriciaTrie<Variable> = PatriciaTrie()


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
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


    // -----------------------------------------------------------------------------------------
    // VARIABLES
    // -----------------------------------------------------------------------------------------

    /**
     * Add a variable to the index. If a variable with the same name already exists in the index,
     * then the new variable replaces the old variable.
     * @param variable The variable to add.
    */
    override fun addVariable(variable : Variable)
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

        for (variableRef in variable.dependencies(this.sheetContext))
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
            }
        }

        // (5) Update any mechanics which are dependent on this variable
        // -------------------------------------------------------------------------------------

        when (variable) {
            is BooleanVariable ->
                mechanicsByReqVariableId[variableId]?.forEach { it.update(variable, this) }
        }

        // (6) Add companion variables to the state
        // -------------------------------------------------------------------------------------

        val companionVariables = variable.companionVariables(sheetContext)
        when (companionVariables)
        {
            is Val -> companionVariables.value.forEach { this.addVariable(it) }
            is Err -> ApplicationLog.error(companionVariables.error)
        }


        // (7) Notify all current listeners of this variable
        // -------------------------------------------------------------------------------------

        this.updateListeners(variable)

        ApplicationLog.event(AppStateEvent(VariableAdded(variableId)))
    }


    fun removeVariable(variableId : VariableId)
    {
        if (!this.variableById.containsKey(variableId))
            return

        val variable = this.variableById[variableId]

        // (1) Notify listeners (before they are removed)
        // -------------------------------------------------------------------------------------

        this.onRemoveUpdateListeners(variable!!)

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

        // (5) Unindex dependencies
        // -------------------------------------------------------------------------------------

        for (variableRef in variable.dependencies(this.sheetContext))
        {
            when (variableRef)
            {
                is VariableId -> listenersById[variableRef]?.remove(variable)
                is VariableTag -> listenersByTag[variableRef]?.remove(variable)
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
                              mechanic.variables().map { it.variableId() }.toSet() )
        val event = AppStateEvent(mechanicAddedEvent)

        ApplicationLog.event(event)
    }


    override fun removeMechanic(mechanic : Mechanic)
    {
        mechanic.variables().forEach { this.removeVariable(it.variableId()) }

        val event = MechanicRemoved(mechanic.mechanicId(),
                                    mechanic.variables().map { it.variableId() }.toSet() )

        ApplicationLog.event(AppStateEvent(event))
    }


    fun onVariableUpdate(variable : Variable)
    {
    //    ApplicationLog.event(AppStateEvent(VariableUpdated(variable.variableId())))
        this.updateListeners(variable)
    }


    fun onRemoveUpdateListeners(variable : Variable)
    {
        // (1) Update listeners of variable id
        // -------------------------------------------------------------------------------------

        val variableId = variable.variableId()
        if (listenersById.containsKey(variableId))
        {
            for (listener in listenersById[variableId]!!)
            {
                listener.onUpdate()
                this.updateListeners(listener)
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
                    listener.onUpdate()
                    this.updateListeners(listener)
                }
            }
        }

        // (3) Update on change listeners
        // -------------------------------------------------------------------------------------

        if (onChangeListenersById.containsKey(variableId))
        {
            val listeners = onChangeListenersById[variableId]
            listeners?.forEach { it.onRemove() }
        }
    }


    /**
     * Update all listeners that the variable has been changed.
     */
    fun updateListeners(variable : Variable)
    {
        ApplicationLog.event(AppStateEvent(VariableUpdated(variable.variableId())))
//        Log.d("***SHEETSTATE", "UPDATE LISTENERS -------------------------------------")
//        Log.d("***SHEETSTATE", this.listenersById[VariableId("acrobatics", "is_proficient")].toString())

        // (1) Update listeners of variable id
        // -------------------------------------------------------------------------------------

        val variableId = variable.variableId()
        if (listenersById.containsKey(variableId))
        {
            Log.d("***SHEETSTATE", "update listeners for: ${variable.variableId()}")
            for (listener in listenersById[variableId]!!)
            {
                Log.d("***SHEETSTATE", "found listener: ${listener.variableId()}")
                listener.onUpdate()
                this.updateListeners(listener)
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
                    listener.onUpdate()
                    this.updateListeners(listener)
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

        // (4) Update on change listeners
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

    fun addVariableOnChangeListener(variableId : VariableId, listener : OnVariableChangeListener)
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

    fun variableWithId(variableId : VariableId) : AppEff<Variable> =
            note(this.variableById[variableId],
                 AppStateError(VariableWithIdDoesNotExist(sheetContext.sheetId, variableId)))


    fun variablesWithTag(variableTag : VariableTag) : AppEff<Set<Variable>> =
            note(this.variablesByTag.get(variableTag),
                 AppStateError(VariableWithTagDoesNotExist(sheetContext.sheetId, variableTag)))


    fun variables() : Collection<Variable> = this.variableById.values


    fun variables(variableReference : VariableReference,
                  context : Maybe<VariableNamespace> = Nothing()) : AppEff<Set<Variable>> =
        when (variableReference)
        {
            is VariableId      -> effApply(::setOf, this.variableWithId(variableReference))
            is VariableTag     -> this.variablesWithTag(variableReference)
            is VariableContext -> {
                when (context) {
                    is Just -> {
                        val contextId = VariableId(context.value, VariableName(variableReference.value))
                        effApply(::setOf, this.variableWithId(contextId))
                    }
                    else    -> effError<AppError,Set<Variable>>(AppStateError(NoContext(variableReference)))
                }

            }
        }


    fun variable(variableReference : VariableReference,
                 context : Maybe<VariableNamespace> = Nothing()) : AppEff<Variable>
    {
        fun firstVariable(variableSet : Set<Variable>) : AppEff<Variable> =
                note(variableSet.firstOrNull(),
                        AppStateError(VariableDoesNotExist(sheetContext.sheetId, variableReference)))

        return this.variables(variableReference, context).apply(::firstVariable)
    }


    // Variable > Boolean
    // -----------------------------------------------------------------------------------------

    fun booleanVariable(variableReference : VariableReference) : AppEff<BooleanVariable> =
        this.variable(variableReference)
            .apply({it.booleanVariable(sheetContext.sheetId)})


    fun booleanVariableWithId(variableId : VariableId) : AppEff<BooleanVariable>
    {
        fun boolVariableEff(variable : Variable) : AppEff<BooleanVariable> = when (variable)
        {
            is BooleanVariable -> effValue(variable)
            else               -> effError(AppStateError(
                                       VariableIsOfUnexpectedType(sheetContext.sheetId,
                                                                  variableId,
                                                                  VariableType.TEXT,
                                                                  variable.type())))
        }

        return variableWithId(variableId)
                  .apply(::boolVariableEff)
    }


    // Variable > Dice Roll
    // -----------------------------------------------------------------------------------------

    fun diceRollVariable(variableReference : VariableReference) : AppEff<DiceRollVariable> =
        this.variable(variableReference)
            .apply({it.diceRollVariable(sheetContext.sheetId)})


    // Variable > Number
    // -----------------------------------------------------------------------------------------

    fun numberVariable(variableReference : VariableReference,
                       context : Maybe<VariableNamespace> = Nothing()) : AppEff<NumberVariable> =
        this.variable(variableReference, context)
                .apply({it.numberVariable(sheetContext.sheetId)})


    fun numberVariables(variableReference : VariableReference,
                        context : Maybe<VariableNamespace> = Nothing()) : AppEff<Set<NumberVariable>>
    {

        return this.variables(variableReference, context) ap {
            if (it.isNotEmpty())
                it.mapM { variable -> variable.numberVariable(sheetContext.sheetId) }
            else
                effValue(setOf())
        }
    }


    fun numberVariableWithId(variableId : VariableId) : AppEff<NumberVariable>
    {
        fun numVariableEff(variable : Variable) : AppEff<NumberVariable> = when (variable)
        {
            is NumberVariable -> effValue(variable)
            else              -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(sheetContext.sheetId,
                                                               variableId,
                                                               VariableType.TEXT,
                                                               variable.type())))
        }

        return variableWithId(variableId)
                  .apply(::numVariableEff)
    }


    // Variable > Text
    // -----------------------------------------------------------------------------------------

    fun textVariable(variableReference : VariableReference) : AppEff<TextVariable> =
            this.variable(variableReference)
                .apply({it.textVariable(sheetContext.sheetId)})


    fun textVariableWithId(variableId : VariableId) : AppEff<TextVariable>
    {
        fun textVariableEff(variable : Variable) : AppEff<TextVariable> = when (variable)
        {
            is TextVariable -> effValue(variable)
            else            -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(sheetContext.sheetId,
                                                               variableId,
                                                               VariableType.TEXT,
                                                               variable.type())))
        }

        return variableWithId(variableId)
                  .apply(::textVariableEff)
    }


    // Variable > Text List
    // -----------------------------------------------------------------------------------------

    fun textListVariable(variableReference : VariableReference) : AppEff<TextListVariable> =
            this.variable(variableReference)
                .apply({it.textListVariable(sheetContext.sheetId)})


    fun textListVariableWithId(variableId : VariableId) : AppEff<TextListVariable>
    {
        fun textVariableEff(variable : Variable) : AppEff<TextListVariable> = when (variable)
        {
            is TextListVariable -> effValue(variable)
            else                -> effError(AppStateError(
                                        VariableIsOfUnexpectedType(sheetContext.sheetId,
                                                                   variableId,
                                                                   VariableType.TEXT,
                                                                   variable.type())))
        }

        return variableWithId(variableId)
                  .apply(::textVariableEff)
    }

}


interface MechanicStateMachine
{
    fun addMechanic(mechanic : Mechanic)
    fun removeMechanic(mechanic : Mechanic)
}


data class MechanicState(val state : MutableMap<VariableId,Boolean>,
                         val mechanic : Mechanic)
{

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


    private fun isReady() = this.state.values.all { it }


}


// ---------------------------------------------------------------------------------------------
// ON CHANGE LISTERER
// ---------------------------------------------------------------------------------------------

data class OnVariableChangeListener(val onUpdate : (Variable) -> Unit,
                                    val onRemove : () -> Unit)

