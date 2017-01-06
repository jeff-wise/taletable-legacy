
package com.kispoko.tome.engine.value;


import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Number Value
 */
public class NumberValue implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>            name;
    private PrimitiveFunctor<Integer>           value;
    private CollectionFunctor<VariableUnion>    variables;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberValue()
    {
        this.id         = null;

        this.name       = new PrimitiveFunctor<>(null, String.class);
        this.value      = new PrimitiveFunctor<>(null, Integer.class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);
        this.variables  = CollectionFunctor.empty(variableClasses);
    }


    public NumberValue(UUID id, String name, Integer value, List<VariableUnion> variables)
    {
        this.id         = id;

        this.name       = new PrimitiveFunctor<>(name, String.class);
        this.value      = new PrimitiveFunctor<>(value, Integer.class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);

        if (variables != null) {
            this.variables = CollectionFunctor.full(variables, variableClasses);
        }
        else {
            this.variables = CollectionFunctor.full(new ArrayList<VariableUnion>(),
                                                    variableClasses);
        }
    }


    /**
     * Create a Number Value from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Number Value.
     * @throws YamlException
     */
    public static NumberValue fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID                id        = UUID.randomUUID();

        String              name      = yaml.atKey("name").getString();
        Integer             value     = yaml.atKey("value").getInteger();

        List<VariableUnion> variables = yaml.atMaybeKey("variables")
                                            .forEach(new Yaml.ForEach<VariableUnion>() {
            @Override
            public VariableUnion forEach(Yaml yaml, int index) throws YamlException {
                return VariableUnion.fromYaml(yaml);
            }
        }, true);

        return new NumberValue(id, name, value, variables);
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

    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------


    /**
     * The number value's name.
     * @return The name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    /**
     * The integer value.
     * @return The value.
     */
    public Integer value()
    {
        return this.value.getValue();
    }


    /**
     * The text value's variables.
     * @return The list of variables.
     */
    public List<VariableUnion> variables()
    {
        return this.variables.getValue();
    }


}