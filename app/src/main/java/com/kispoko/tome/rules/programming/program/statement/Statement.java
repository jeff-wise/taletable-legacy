
package com.kispoko.tome.rules.programming.program.statement;


import com.kispoko.tome.sheet.Page;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Statement
 */
public class Statement implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                       id;

    private PrimitiveValue<String>     variableName;
    private PrimitiveValue<String>     functionName;
    private CollectionValue<Parameter> parameters;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Statement(String variableName, String functionName, List<Parameter> parameters)
    {
        this.variableName = new PrimitiveValue<>(variableName, this, String.class);
        this.functionName = new PrimitiveValue<>(functionName, this, String.class);

        List<Class<Parameter>> parameterClasses = Arrays.asList(Parameter.class);
        this.parameters   = new CollectionValue<>(parameters, this, parameterClasses);
    }


    /**
     * Create a new Statement from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return A new Statement.
     * @throws YamlException
     */
    public static Statement fromYaml(Yaml yaml)
                  throws YamlException
    {
        String variableName = yaml.atKey("let").getString();
        String functionName = yaml.atKey("function").getString();

        List<Parameter> parameters = yaml.atKey("parameters")
                                         .forEach(new Yaml.ForEach<Parameter>() {
            @Override
            public Parameter forEach(Yaml yaml, int index) throws YamlException {
                return Parameter.fromYaml(yaml);
            }
        });

        return new Statement(variableName, functionName, parameters);
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


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onModelUpdate(String valueName) { }


}

