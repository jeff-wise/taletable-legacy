
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
class SheetState(val sheetContext : SheetContext, mechanics : Set<Mechanic>) : State
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
            for (variableId in mechanic.requirements())
            {
                if (!this.mechanicsByReqVariableId.containsKey(variableId))
                    this.mechanicsByReqVariableId.put(variableId, mutableSetOf())

                val mechanicState = MechanicState(mechanic)
                this.mechanicsByReqVariableId[variableId]!!.add(mechanicState)
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
        val variableId = variable.variableId.value

        // (1) Index variable by name
        // -------------------------------------------------------------------------------------

        variableById.put(variableId, variable)

        // (2) Index variable for search
        // -------------------------------------------------------------------------------------

        activeVariableIdTrie.put(variableId.toString(), variable)
        activeVariableLabelTrie.put(variable.label(), variable)

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

        for (variableRef in variable.dependencies())
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

        when (variable)
        {
            is BooleanVariable ->
            {
                mechanicsByReqVariableId[variableId]?.forEach {
                    val isReady = it.update(variable)
                    if (isReady) this.addMechanic(it.mechanic)
                }
            }
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


    fun addMechanic(mechanic : Mechanic)
    {
        mechanic.variables().forEach { this.addVariable(it) }

        val mechanicAddedEvent =
                MechanicAdded(mechanic.mechanicId(),
                              mechanic.variables().map { it.variableId() }.toSet() )
        val event = AppStateEvent(mechanicAddedEvent)

        ApplicationLog.event(event)
    }


    /**
     * Update all listeners that the variable has been changed.
     */
    fun updateListeners(variable : Variable)
    {
        // (1) Update listeners of variable id
        // -------------------------------------------------------------------------------------

        val variableId = variable.variableId.value
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
            note(this.variablesByTag[variableTag],
                 AppStateError(VariableWithTagDoesNotExist(sheetContext.sheetId, variableTag)))


    fun variables(variableReference : VariableReference) : AppEff<Set<Variable>> =
        when (variableReference)
        {
            is VariableId  -> effApply(::setOf, this.variableWithId(variableReference))
            is VariableTag -> this.variablesWithTag(variableReference)
        }


    fun variable(variableReference : VariableReference) : AppEff<Variable>
    {
        fun firstVariable(variableSet : Set<Variable>) : AppEff<Variable> =
                note(variableSet.firstOrNull(),
                        AppStateError(VariableDoesNotExist(sheetContext.sheetId, variableReference)))

        return this.variables(variableReference).apply(::firstVariable)
    }


    // Variable > Boolean
    // -----------------------------------------------------------------------------------------

    fun booleanVariable(variableReference : VariableReference) : AppEff<BooleanVariable> =
        this.variable(variableReference)
            .apply({it.booleanVariable(sheetContext.sheetId)})


    // Variable > Dice Roll
    // -----------------------------------------------------------------------------------------

    fun diceRollVariable(variableReference : VariableReference) : AppEff<DiceRollVariable> =
        this.variable(variableReference)
            .apply({it.diceRollVariable(sheetContext.sheetId)})


    // Variable > Number
    // -----------------------------------------------------------------------------------------

    fun numberVariable(variableReference : VariableReference) : AppEff<NumberVariable> =
        this.variable(variableReference)
            .apply({it.numberVariable(sheetContext.sheetId)})


    fun numberVariables(variableReference : VariableReference) : AppEff<Set<NumberVariable>>
    {

        val vars = this.variables(variableReference) ap {
//            Log.d("***SHEETSTATE", "number variables: " + it.toString())
            it.mapM { it.numberVariable(sheetContext.sheetId) }
        }
//        Log.d("***SHEETSTATE", "vars: " + vars.toString())

        return vars
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


}




data class MechanicState(val state : MutableMap<VariableId,Boolean>,
                         val mechanic : Mechanic)
{

    constructor(mechanic : Mechanic)
        : this(mechanic.requirements().associate { Pair(it, false) }.toMutableMap(),
               mechanic)


    fun update(variable : BooleanVariable) : Boolean
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
                    return this.isReady()
                }
                is Err ->
                {
                    ApplicationLog.error(value.error)
                    return false
                }
            }
        }

        return false
    }


    private fun isReady() = this.state.values.all { it }

}



//
//    /**
//     * Remove the variable with the given name from the state. Returns true if the variable was
//     * removed, and false if the variable did not exist in the state.
//     * @param variableName The variable name.
//     * @return True if the variable was removed, False if the variable did not exist.
//     */
//    public static boolean removeVariable(String variableName)
//    {
//        if (!variableByName.containsKey(variableName))
//            return false;
//
//        VariableUnion variableUnion = variableByName.get(variableName);
//
//        // [1] Remove the variable from the index
//        // --------------------------------------------------------------------------------------
//
//        // > Delete from MAIN index
//        variableByName.remove(variableName);
//
//        // > Delete from SEARCH indexes
//        activeVariableNameTrie.remove(variableName);
//        activeVariableLabelTrie.remove(variableName);
//
//        // [2] Un-Index the variable's dependencies
//        // --------------------------------------------------------------------------------------
//
//        for (VariableReference variableReference : variableUnion.variable().dependencies())
//        {
//            switch (variableReference.type())
//            {
//                // > Index variable names to listeners
//                case NAME:
//                    String name = variableReference.name();
//                    Set<Variable> nameListeners = variableNameToListeners.get(name);
//                    nameListeners.remove(variableUnion.variable());
//
//                    if (nameListeners.isEmpty())
//                        variableNameToListeners.remove(name);
//                    break;
//                case TAG:
//                    String tag = variableReference.tag();
//                    Set<Variable> tagListeners = variableTagToListeners.get(tag);
//                    tagListeners.remove(variableUnion.variable());
//
//                    if (tagListeners.isEmpty())
//                        variableTagToListeners.remove(tag);
//                    break;
//            }
//        }
//
//        // [3] Un-Index the variable's tags
//        // --------------------------------------------------------------------------------------
//
//        for (String tag : variableUnion.variable().tags())
//        {
//            Set<VariableUnion> variablesWithTag = tagIndex.get(tag);
//            variablesWithTag.remove(variableUnion);
//
//            if (variablesWithTag.isEmpty())
//                tagIndex.remove(tag);
//        }
//
//        // [4] Notify all current listeners of this variable
//        // --------------------------------------------------------------------------------------
//
//        updateVariableDependencies(variableUnion.variable());
//
//        return true;
//    }
//

//    // > Search
//    // ------------------------------------------------------------------------------------------
//
//    public static Collection<EngineActiveSearchResult> search(String query)
//    {
//        Map<String,ActiveVariableSearchResult> resultsByVariableName = new HashMap<>();
//
//        // Name Matches
//        Collection<VariableUnion> nameMatches = activeVariableNameTrie.prefixMap(query).values();
//        for (VariableUnion variableUnion : nameMatches)
//        {
//            String variableName = variableUnion.variable().name();
//            String variableLabel = variableUnion.variable().label();
//
//            ActiveVariableSearchResult result;
//            if (resultsByVariableName.containsKey(variableName))
//            {
//                result = resultsByVariableName.get(variableName);
//                result.addToRanking(1f);
//            }
//            else
//            {
//                result = new ActiveVariableSearchResult(variableName, variableLabel);
//                resultsByVariableName.put(variableName, result);
//            }
//
//            result.setNameIsMatched();
//        }
//
//        // Label Matches
//        Collection<VariableUnion> labelMatches = activeVariableLabelTrie.prefixMap(query).values();
//        for (VariableUnion variableUnion : labelMatches)
//        {
//            String variableName = variableUnion.variable().name();
//            String variableLabel = variableUnion.variable().label();
//
//            ActiveVariableSearchResult result;
//            if (resultsByVariableName.containsKey(variableName))
//            {
//                result = resultsByVariableName.get(variableName);
//                result.addToRanking(1f);
//            }
//            else
//            {
//                result = new ActiveVariableSearchResult(variableName, variableLabel);
//                resultsByVariableName.put(variableName, result);
//            }
//
//            result.setLabelIsMatched();
//        }
//
//        Collection<EngineActiveSearchResult> results = new ArrayList<>();
//        for (ActiveVariableSearchResult variableSearchResult : resultsByVariableName.values()) {
//            results.add(new EngineActiveSearchResult(variableSearchResult));
//        }
//
//        return results;
//    }
//
//}
