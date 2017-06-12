
package com.kispoko.tome.rts.sheet


import android.util.Log
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.model.sheet.Sheet
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
class SheetState(val sheet : Sheet) : State
{

    // Variable Indexes
    // -----------------------------------------------------------------------------------------

    private val variableById : MutableMap<VariableId,Variable> = mutableMapOf()
    private val variablesByTag : MutableMap<VariableTag,MutableSet<Variable>> = mutableMapOf()

    private val listenersById : MutableMap<VariableId,MutableSet<Variable>> = mutableMapOf()
    private val listenersByTag : MutableMap<VariableTag,MutableSet<Variable>> = mutableMapOf()

    // Search Indexes
    // -----------------------------------------------------------------------------------------

    private val activeVariableIdTrie : PatriciaTrie<Variable> = PatriciaTrie()
    private val activeVariableLabelTrie : PatriciaTrie<Variable> = PatriciaTrie()


    // VARIABLES
    // -----------------------------------------------------------------------------------------

    /**
     * Add a variable to the index. If a variable with the same name already exists in the index,
     * then the new variable replaces the old variable.
     * @param variable The variable to add.
    */
    override fun addVariable(variable : Variable)
    {
        Log.d("***SHEET_STATE", "add variable " + variable.variableId.value)
        val variableId = variable.variableId.value
        val variableLabel = variable.label.value

        // (1) Index variable by name
        // -------------------------------------------------------------------------------------

        variableById.put(variableId, variable)

        // (2) Index variable for search
        // -------------------------------------------------------------------------------------

        activeVariableIdTrie.put(variableId.toString(), variable)

        if (variableLabel != null)
            activeVariableLabelTrie.put(variableLabel.value, variable)

        // (3) Index the variable by tag
        // -------------------------------------------------------------------------------------

        for (tag in variable.tags.value)
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
                is VariableReferenceId ->
                {
                    if (listenersById.containsKey(variableRef.id))
                        listenersById[variableRef.id]!!.add(variable)
                    else
                        listenersById.put(variableRef.id, mutableSetOf(variable))
                }
                is VariableReferenceTag ->
                {
                    if (listenersByTag.containsKey(variableRef.tag))
                        listenersByTag[variableRef.tag]!!.add(variable)
                    else
                        listenersByTag.put(variableRef.tag, mutableSetOf(variable))
                }
            }
        }

        // (5) Notify all current listeners of this variable
        // -------------------------------------------------------------------------------------

        this.updateListeners(variable)

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

        for (tag in variable.tags.value)
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


    fun textVariableWithId(variableId : VariableId) : TextVariable?
    {
        Log.d("***SHEET_STATE", "lookup text var " + variableId)
        val variable = this.variableById[variableId]

        when (variable)
        {
            is TextVariable -> return variable
            else            -> return null
        }
    }


}


//public class State
//{
//
//    // PROPERTIES
//    // ------------------------------------------------------------------------------------------
//
//    private static Map<String,VariableUnion>      variableByName          = new HashMap<>();
//
//    private static Map<String,Set<Variable>>      variableNameToListeners = new HashMap<>();
//    private static Map<String,Set<Variable>>      variableTagToListeners  = new HashMap<>();
//
//    private static Map<String,Set<VariableUnion>> tagIndex                = new HashMap<>();
//
//    private static boolean                        mechanicIndexReady      = false;
//
//
//    // > Search Indexes
//    // ------------------------------------------------------------------------------------------
//
//    private static PatriciaTrie<VariableUnion> activeVariableNameTrie;
//    private static PatriciaTrie<VariableUnion> activeVariableLabelTrie;
//
//
//    // API
//    // ------------------------------------------------------------------------------------------
//
//    // > Initialize
//    // ------------------------------------------------------------------------------------------
//
//    public static void initialize()
//    {
//        activeVariableNameTrie  = new PatriciaTrie<>();
//        activeVariableLabelTrie = new PatriciaTrie<>();
//
//        State.initializeMechanics();
//    }
//
//
//    // > Engine
//    // ------------------------------------------------------------------------------------------
//
//    public static void addVariable(Variable variable)
//    {
//        if (variable instanceof TextVariable) {
//            addVariable(VariableUnion.asText((TextVariable) variable));
//        }
//        else if (variable instanceof NumberVariable) {
//            addVariable(VariableUnion.asNumber((NumberVariable) variable));
//        }
//        else if (variable instanceof BooleanVariable) {
//            addVariable(VariableUnion.asBoolean((BooleanVariable) variable));
//        }
//        else if (variable instanceof DiceVariable) {
//            addVariable(VariableUnion.asDice((DiceVariable) variable));
//        }
//    }
//
//
//    /**
//     * Get the variable from the state that has the given name.
//     * @param name The variable name.
//     * @return The variable union with the given name.
//     */
//    public static VariableUnion variableWithName(String name)
//    {
//        return variableByName.get(name);
//    }
//
//
//    @Nullable
//    public static NumberVariable numberVariableWithName(String name)
//                  throws VariableException
//    {
//        if (name == null)
//            throw VariableException.undefinedVariable(new UndefinedVariableError("__NULL__"));
//
//        VariableUnion variableUnion = State.variableWithName(name);
//
//        if (variableUnion == null)
//            throw VariableException.undefinedVariable(new UndefinedVariableError(name));
//
//        if (variableUnion.type() != VariableType.NUMBER) {
//            throw VariableException.unexpectedVariableType(
//                    new UnexpectedVariableTypeError(name,
//                                                    VariableType.NUMBER,
//                                                    variableUnion.type()));
//        }
//
//        return variableUnion.numberVariable();
//    }
//
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
//
//    /**
//     * Returns true if the state contains the variable with the given name.
//     * @param variableName The variable name.
//     * @return True if the state contains the variable, False otherwise.
//     */
//    public static boolean hasVariable(String variableName)
//    {
//        return variableByName.containsKey(variableName);
//    }
//
//
//    public static Set<VariableUnion> variablesWithTag(String tag)
//    {
//        if (tagIndex.containsKey(tag))
//            return tagIndex.get(tag);
//        else
//            return new HashSet<>();
//    }
//
//
//    public static void initializeMechanics()
//    {
//        mechanicIndexReady = true;
//
//        // TODO casues concurrent mod error
//        // whole system needs to be more understandable
//        MechanicIndex mechanicIndex = SheetManagerOld.currentSheet().engine().mechanicIndex();
//        for (VariableUnion variableUnion : variableByName.values()) {
//            mechanicIndex.onVariableUpdate(variableUnion.variable().name());
//        }
//    }
//
//
//    public static void updateMechanics(String variableName)
//    {
//        MechanicIndex mechanicIndex = SheetManagerOld.currentSheet().engine().mechanicIndex();
//        mechanicIndex.onVariableUpdate(variableName);
//    }
//
//
//    /**
//     * Get a variable tuple that consists of the variable name and its string value. This method
//     * returns null if the variable does not exist or another error occurs.
//     * @param variableName The variable id.
//     * @return The variable (name, value) tuple.
//     */
//    public static Tuple2<String,String> variableTuple(String variableName)
//    {
//        Tuple2<String,String> tuple = null;
//
//        VariableUnion variableUnion = State.variableWithName(variableName);
//
//        if (variableUnion != null)
//        {
//            try {
//                tuple = new Tuple2<>(variableUnion.variable().label(),
//                                     variableUnion.variable().valueString());
//            }
//            catch (NullVariableException exception) {
//                ApplicationFailure.nullVariable(exception);
//            }
//        }
//
//        return tuple;
//    }
//
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
