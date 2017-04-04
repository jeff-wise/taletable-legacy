
package com.kispoko.tome.engine.value;


import com.kispoko.tome.R;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Text Value
 */
public class TextValue extends Model
                       implements Value, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>            name;
    private PrimitiveFunctor<String>            value;
    private PrimitiveFunctor<String>            description;
    private CollectionFunctor<VariableUnion>    variables;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextValue()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.value          = new PrimitiveFunctor<>(null, String.class);
        this.description    = new PrimitiveFunctor<>(null, String.class);

        this.variables      = CollectionFunctor.empty(VariableUnion.class);

        this.initializeFunctors();
    }


    public TextValue(UUID id,
                     String name,
                     String value,
                     String description,
                     List<VariableUnion> variables)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);
        this.value          = new PrimitiveFunctor<>(value, String.class);
        this.description    = new PrimitiveFunctor<>(description, String.class);

        if (variables != null) {
            this.variables  = CollectionFunctor.full(variables, VariableUnion.class);
        }
        else {
            this.variables  = CollectionFunctor.full(new ArrayList<VariableUnion>(),
                                                    VariableUnion.class);
        }

        this.initializeFunctors();
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
                .putString("description", this.description())
                .putList("variables", this.variables());
    }


    // > Value
    // ------------------------------------------------------------------------------------------

    /**
     * The text value's summary (a short description of what it represents).
     */
    public String description()
    {
        return this.description.getValue();
    }


    @Override
    public String valueString()
    {
        return this.value();
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


    // > Variables
    // ------------------------------------------------------------------------------------------

    /**
     * The text value's variables.
     * @return The list of variables.
     */
    public List<VariableUnion> variables()
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


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initializeFunctors()
    {
        // Name
        this.name.setName("name");
        this.name.setLabelId(R.string.value_field_name_label);
        this.name.setDescriptionId(R.string.value_field_name_description);

        // Value
        this.value.setName("value");
        this.value.setLabelId(R.string.value_field_value_label);
        this.value.setDescriptionId(R.string.value_field_value_description);
        this.value.setIsRequired(true);

        // Description
        this.description.setName("description");
        this.description.setLabelId(R.string.value_field_description_label);
        this.description.setDescriptionId(R.string.value_field_description_description);

        // Variables
        this.variables.setName("variables");
        this.variables.setLabelId(R.string.value_field_variables_label);
        this.variables.setDescriptionId(R.string.value_field_variables_description);
    }

}
