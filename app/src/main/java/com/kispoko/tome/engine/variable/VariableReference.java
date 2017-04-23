
package com.kispoko.tome.engine.variable;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.lib.functor.OptionFunctor;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;



/**
 * Variable Reference
 */
public class VariableReference extends Model
                               implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                    id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>                name;
    private PrimitiveFunctor<String>                tag;

    private OptionFunctor<VariableReferenceType>    type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public VariableReference()
    {
        this.id     = null;

        this.name   = new PrimitiveFunctor<>(null, String.class);
        this.tag    = new PrimitiveFunctor<>(null, String.class);

        this.type   = new OptionFunctor<>(null, VariableReferenceType.class);

        this.initializeFunctors();
    }


    private VariableReference(UUID id, Object value, VariableReferenceType type)
    {
        this.id     = id;

        this.name   = new PrimitiveFunctor<>(null, String.class);
        this.tag    = new PrimitiveFunctor<>(null, String.class);

        this.type   = new OptionFunctor<>(type, VariableReferenceType.class);

        // > Set the value depending on the case
        switch (type)
        {
            case NAME:
                this.name.setValue((String) value);
                break;
            case TAG:
                this.tag.setValue((String) value);
                break;
        }

        this.initializeFunctors();
    }


    // > Variants
    // ------------------------------------------------------------------------------------------

    /**
     * Create the "by name" case. Reference a variable by its name.
     * @param id The model id.
     * @param variableName The variable name.
     * @return The Variable Reference "by name".
     */
    public static VariableReference asByName(UUID id, String variableName)
    {
        return new VariableReference(id, variableName, VariableReferenceType.NAME);
    }


    /**
     * Create the "by name" case, but not as a model.
     * @param variableName The variable name.
     * @return The Variable Reference "by name".
     */
    public static VariableReference asByName(String variableName)
    {
        return new VariableReference(null, variableName, VariableReferenceType.NAME);
    }


    /**
     * Create the "by tag" case. Reference all variables that have the given tag.
     * @param id The model id.
     * @param tag The variable tag
     * @return The Variable Reference "by tag".
     */
    public static VariableReference asByTag(UUID id, String tag)
    {
        return new VariableReference(id, tag, VariableReferenceType.TAG);
    }


    /**
     * Create the "by tag" case, but not as a model.
     * @param tag The variable tag.
     * @return The Variable Reference "by tag".
     */
    public static VariableReference asByTag(String tag)
    {
        return new VariableReference(null, tag, VariableReferenceType.TAG);
    }


    // > Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * Create a Variable Reference from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Variable Reference.
     * @throws YamlParseException
     */
    public static VariableReference fromYaml(YamlParser yaml)
            throws YamlParseException
    {
        UUID                  id   = UUID.randomUUID();

        VariableReferenceType type = VariableReferenceType.fromYaml(yaml.atKey("type"));

        switch (type)
        {
            case NAME:
                String variableName = yaml.atKey("name").getString();
                return VariableReference.asByName(id, variableName);
            case TAG:
                String tag = yaml.atKey("tag").getString();
                return VariableReference.asByTag(id, tag);
        }

        return null;
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
     * This method is called when the Column Union is completely loaded for the first time.
     */
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the kind of variable reference.
     * @return The reference kind.
     */
    public VariableReferenceType type()
    {
        return this.type.getValue();
    }


    /**
     * The variable name case.
     * @return The variable name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    /**
     * The tag case.
     * @return The tag.
     */
    public String tag()
    {
        return this.tag.getValue();
    }


    // > Variables
    // ------------------------------------------------------------------------------------------

    /**
     * Lookup the referenced variables.
     * @return The Variable List.
     */
    public Set<VariableUnion> variables()
    {
        switch (this.type())
        {
            case NAME:
                Set<VariableUnion> variables = new HashSet<>();
                VariableUnion variableUnion = State.variableWithName(this.name());
                if (variableUnion != null)
                    variables.add(variableUnion);
                return variables;
            case TAG:
                return State.variablesWithTag(this.tag());
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(VariableReferenceType.class.getName())));
                return new HashSet<>();
        }
    }


    /**
     * Special function when we assume that there are no references by tag being used, or there
     * will only every be one variable with that tag.
     * @return The referenced variable.
     */
    public VariableUnion variable()
    {
        switch (this.type())
        {
            case NAME:
                return State.variableWithName(this.name());
            case TAG:
                Set<VariableUnion> variables = State.variablesWithTag(this.tag());

                if (variables.size() == 0)
                    return null;

                List<VariableUnion> variableList = new ArrayList<>(variables);
                return variableList.get(0);
        }

        return null;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Initialize
    // ------------------------------------------------------------------------------------------

    private void initializeFunctors()
    {
        // Type
        this.type.setName("type");
        this.type.setLabelId(R.string.activity_variable_reference_field_type_label);
        this.type.setDescriptionId(R.string.activity_variable_reference_field_type_description);

        // Name
        this.name.setName("type_name");
        this.name.setLabelId(R.string.activity_variable_reference_field_name_label);
        this.name.setDescriptionId(R.string.activity_variable_reference_field_name_label);
        this.name.caseOf("type", "name");

        // Tag
        this.tag.setName("type_tag");
        this.tag.setLabelId(R.string.activity_variable_reference_field_tag_label);
        this.tag.setDescriptionId(R.string.activity_variable_reference_field_tag_description);
        this.tag.caseOf("type", "tag");
    }

}
