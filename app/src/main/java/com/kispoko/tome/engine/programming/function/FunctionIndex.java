
package com.kispoko.tome.engine.programming.function;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Function Index
 */
public class FunctionIndex implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private CollectionValue<Function> functions;


    // > Internal
    private Map<String,Function> functionByName;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public FunctionIndex()
    {
        this.id        = null;

        List<Class<? extends Function>> functionClasses = new ArrayList<>();
        functionClasses.add(Function.class);
        this.functions = CollectionValue.empty(functionClasses);

        this.functionByName = new HashMap<>();
    }


    public FunctionIndex(UUID id)
    {
        this.id = id;

        List<Class<? extends Function>> functionClasses = new ArrayList<>();
        functionClasses.add(Function.class);
        this.functions = CollectionValue.full(new ArrayList<Function>(), functionClasses);

        this.functionByName = new HashMap<>();
    }


    public static FunctionIndex fromYaml(Yaml yaml)
                  throws YamlException
    {
        final FunctionIndex functionIndex = new FunctionIndex(UUID.randomUUID());

        List<Function> functions = yaml.forEach(new Yaml.ForEach<Function>() {
            @Override
            public Function forEach(Yaml yaml, int index) throws YamlException {
                Function function = null;
                try {
                    function = Function.fromYaml(yaml);
                } catch (InvalidFunctionException e) {
                    ApplicationFailure.invalidFunction(e);
                }
                return function;
            }
        });

        for (Function function : functions) {
            functionIndex.addFunction(function);
        }

        return functionIndex;
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     * @param id The new model UUID.
     */
    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Function Index is completely loaded for the first time.
     */
    public void onLoad()
    {
        // Functions are loaded into collection, but not indexed. Index functions when all of them
        // are loaded.
        for (Function function : this.functions.getValue()) {
            this.addFunction(function);
        }
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Functions
    // ------------------------------------------------------------------------------------------

    /**
     * Add a new Function to the index. If a function with the same name exists, it will
     * not be added.
     * @param function The function to add.
     */
    public void addFunction(Function function)
    {
        this.functionByName.put(function.getName(), function);
        this.functions.getValue().add(function);
    }


    /**
     * Get the function from the index with the given name.
     * @param functionName The function name.
     * @return The function with the given name, or null if no function with that name exists.
     */
    public Function functionWithName(String functionName)
    {
        return this.functionByName.get(functionName);
    }


    /**
     * Returns true if the index contains the function with the given name.
     * @param functionName The name of the function.
     * @return True if the function with the given name exists in the index, false otherwise.
     */
    public boolean hasFunction(String functionName)
    {
        return this.functionByName.containsKey(functionName);
    }

}