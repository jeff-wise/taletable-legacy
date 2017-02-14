
package com.kispoko.tome.engine.value;


import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Text Value
 */
public class TextValue implements Value, Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                             id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>         name;
    private PrimitiveFunctor<String>         value;
    private PrimitiveFunctor<String>         summary;
    private CollectionFunctor<VariableUnion> variables;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextValue()
    {
        this.id         = null;

        this.name       = new PrimitiveFunctor<>(null, String.class);
        this.value      = new PrimitiveFunctor<>(null, String.class);
        this.summary    = new PrimitiveFunctor<>(null, String.class);

        List<Class<? extends VariableUnion>> variableClasses = new ArrayList<>();
        variableClasses.add(VariableUnion.class);
        this.variables  = CollectionFunctor.empty(variableClasses);
    }


    public TextValue(UUID id,
                     String name,
                     String value,
                     String summary,
                     List<VariableUnion> variables)
    {
        this.id         = id;

        this.name       = new PrimitiveFunctor<>(name, String.class);
        this.value      = new PrimitiveFunctor<>(value, String.class);
        this.summary    = new PrimitiveFunctor<>(summary, String.class);

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
     * Create a Text Value from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Text Value.
     * @throws YamlParseException
     */
    public static TextValue fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID                id        = UUID.randomUUID();

        String              name      = yaml.atMaybeKey("name").getTrimmedString();
        String              value     = yaml.atKey("value").getTrimmedString();
        String              summary   = yaml.atMaybeKey("summary").getTrimmedString();

        List<VariableUnion> variables = yaml.atMaybeKey("variables")
                                            .forEach(new YamlParser.ForEach<VariableUnion>() {
            @Override
            public VariableUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return VariableUnion.fromYaml(yaml);
            }
        }, true);

        return new TextValue(id, name, value, summary, variables);
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


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Text Value's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putString("value", this.value())
                .putString("summary", this.summary())
                .putList("variables", this.variables());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The text value's name.
     * @return The name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    /**
     * The text value.
     * @return The value.
     */
    public String value()
    {
        return this.value.getValue();
    }


    /**
     * The text value's summary (a short description of what it represents).
     */
    public String summary()
    {
        return this.summary.getValue();
    }


    // > Variables
    // ------------------------------------------------------------------------------------------

    /**
     * The text value's variables.
     * @return The list of variables.
     */
    private List<VariableUnion> variables()
    {
        return this.variables.getValue();
    }


    public void addToState()
    {
        for (VariableUnion variableUnion : this.variables()) {
            State.addVariable(variableUnion);
        }
    }


    public void removeFromState()
    {
        for (VariableUnion variableUnion : this.variables()) {
            State.removeVariable(variableUnion.variable().name());
        }
    }
}
