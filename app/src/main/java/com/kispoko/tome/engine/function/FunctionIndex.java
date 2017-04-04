
package com.kispoko.tome.engine.function;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Function Index
 */
public class FunctionIndex extends Model
                           implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private CollectionFunctor<Function> functions;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Map<String,Function>        functionByName;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public FunctionIndex()
    {
        this.id        = null;

        this.functions = CollectionFunctor.empty(Function.class);

        this.functionByName = new HashMap<>();
    }


    public FunctionIndex(UUID id)
    {
        this.id = id;

        this.functions = CollectionFunctor.full(new ArrayList<Function>(), Function.class);

        this.functionByName = new HashMap<>();

        initialize();
    }


    public static FunctionIndex fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        final FunctionIndex functionIndex = new FunctionIndex(UUID.randomUUID());

        List<Function> functions = yaml.forEach(new YamlParser.ForEach<Function>() {
            @Override
            public Function forEach(YamlParser yaml, int index) throws YamlParseException {
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
        initialize();
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.list(this.functions());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Functions
    // ------------------------------------------------------------------------------------------

    /**
     * The functions in the index.
     * @return The Function List.
     */
    public List<Function> functions()
    {
        return this.functions.getValue();
    }


    /**
     * Add a new Function to the index. If a function with the same name exists, it will
     * not be added.
     * @param function The function to add.
     */
    public void addFunction(Function function)
    {
        this.functionByName.put(function.name(), function);
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


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initialize()
    {
        for (Function function : this.functions.getValue()) {
            this.functionByName.put(function.name(), function);
        }
    }
}
