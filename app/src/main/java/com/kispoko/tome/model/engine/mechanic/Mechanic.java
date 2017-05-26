
package com.kispoko.tome.model.engine.mechanic;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.model.engine.mechanic.error.NonBooleanRequirementError;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



/**
 * Mechanic
 */
public class Mechanic extends Model
                      implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>            name;
    private PrimitiveFunctor<String>            label;

    /**
     * A short description of the mechanic. Displayed on the mechanic widget.
     */
    private PrimitiveFunctor<String>            summary;

    /**
     * A long, complete description of the mechanic.
     */
    private PrimitiveFunctor<String>            description;

    // TODO seperate name vs label
    private PrimitiveFunctor<String>            category;

    /**
     * A requirement is the name of a boolean variable. If all the requiremnet variable values are
     * true, then the mechanic is set to active ie. added to the state.
     */
    private PrimitiveFunctor<String[]>          requirements;

    private CollectionFunctor<VariableUnion>    variables;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private boolean                             active;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Mechanic()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.label          = new PrimitiveFunctor<>(null, String.class);
        this.summary        = new PrimitiveFunctor<>(null, String.class);
        this.description    = new PrimitiveFunctor<>(null, String.class);
        this.category       = new PrimitiveFunctor<>(null, String.class);
        this.requirements   = new PrimitiveFunctor<>(null, String[].class);

        this.variables      = CollectionFunctor.empty(VariableUnion.class);

        this.initializeFunctors();
    }


    public Mechanic(UUID id,
                    String name,
                    String label,
                    String summary,
                    String description,
                    String category,
                    List<String> requirements,
                    List<VariableUnion> variables)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);
        this.label          = new PrimitiveFunctor<>(label, String.class);
        this.summary        = new PrimitiveFunctor<>(summary, String.class);
        this.description    = new PrimitiveFunctor<>(description, String.class);
        this.category       = new PrimitiveFunctor<>(category, String.class);

        String[] requirementsArray = new String[requirements.size()];
        requirements.toArray(requirementsArray);
        this.requirements = new PrimitiveFunctor<>(requirementsArray, String[].class);

        this.variables    = CollectionFunctor.full(variables, VariableUnion.class);

        this.initializeFunctors();
    }


    /**
     * Create a Mechanic from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Mechanic.
     */
    public static Mechanic fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID                id           = UUID.randomUUID();

        String              name         = yaml.atKey("name").getString();

        // TODO make sure trim is not used on maybe key
        String              label        = yaml.atKey("label").getTrimmedString();

        String              summary      = yaml.atMaybeKey("summary").getTrimmedString();

        String              description  = yaml.atMaybeKey("description").getTrimmedString();

        String              type         = yaml.atMaybeKey("type").getTrimmedString();

        List<String>        requirements = yaml.atMaybeKey("requirements").getStringList();

        List<VariableUnion> variables    = yaml.atMaybeKey("variables").forEach(
                                                        new YamlParser.ForEach<VariableUnion>()
        {
            @Override
            public VariableUnion forEach(YamlParser yaml, int index) throws YamlParseException {
                return VariableUnion.fromYaml(yaml);
            }
        }, true);

        return new Mechanic(id, name, label, summary, description, type, requirements, variables);
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
     * This method is called when the RulesEngine is completely loaded for the first time.
     */
    public void onLoad()
    {
        this.initialize();
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The mechanic's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putString("label", this.label())
                .putString("summary", this.summary())
                .putString("description", this.description())
                .putString("type", this.type())
                .putStringList("requirements", this.requirements())
                .putList("variables", this.variables());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the mechanic's name.
     * @return The mechanic name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    /**
     * The mechanic's label.
     * @return The label.
     */
    public String label()
    {
        return this.label.getValue();
    }


    /**
     * The mechanic summary.
     * @return The mechanic summary.
     */
    public String summary()
    {
        return this.summary.getValue();
    }


    /**
     * The mechanic's description.
     * @return The mechanic's description.
     */
    public String description()
    {
        return this.description.getValue();
    }


    /**
     * The mechanic's type.
     * @return The type.
     */
    public String type()
    {
        return this.category.getValue();
    }


    // ** Requirements
    // ------------------------------------------------------------------------------------------

    /**
     * Get the mechanic's requirements. Each requirement is the name of a boolean variable.
     * @return The requiement array.
     */
    public List<String> requirements()
    {
        return Arrays.asList(this.requirements.getValue());
    }


    // ** Variables
    // ------------------------------------------------------------------------------------------

    /**
     * Get the mechanic's variables.
     * @return The Variable list.
     */
    public List<VariableUnion> variables()
    {
        return this.variables.getValue();
    }


    public List<String> variableNames()
    {
        List<String> names = new ArrayList<>();

        for (VariableUnion variableUnion : this.variables())
            names.add(variableUnion.variable().name());

        return names;
    }


    // > Active
    // ------------------------------------------------------------------------------------------

    /**
     * True if the mechanic is active.
     * @return
     */
    public Boolean active()
    {
        return this.active;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    /**
     * Initialize the mechanic.
     */
    private void initialize()
    {
        this.active = false;

        validateRequirements();

        onRequirementUpdate();
    }


    private void initializeFunctors()
    {

        // Name
        this.name.setName("name");
        this.name.setLabelId(R.string.mechanic_field_name_label);
        this.name.setDescriptionId(R.string.mechanic_field_name_description);

        // Label
        this.label.setName("label");
        this.label.setLabelId(R.string.mechanic_field_label_label);
        this.label.setDescriptionId(R.string.mechanic_field_label_description);

        // Summary
        this.summary.setName("summary");
        this.summary.setLabelId(R.string.mechanic_field_summary_label);
        this.summary.setDescriptionId(R.string.mechanic_field_summary_description);

        // Description
        this.description.setName("description");
        this.description.setLabelId(R.string.mechanic_field_description_label);
        this.description.setDescriptionId(R.string.mechanic_field_description_description);

        // Category
        this.category.setName("category");
        this.category.setLabelId(R.string.mechanic_field_category_label);
        this.category.setDescriptionId(R.string.mechanic_field_category_description);

        // Requirements
        this.requirements.setName("requirements");
        this.requirements.setLabelId(R.string.mechanic_field_reqs_label);
        this.requirements.setDescriptionId(R.string.mechanic_field_reqs_description);

        // Variables
        this.variables.setName("variables");
        this.variables.setLabelId(R.string.mechanic_field_variables_label);
        this.variables.setDescriptionId(R.string.mechanic_field_variables_description);
    }


    /**
     * Called when there is an update to one of the mechanic's requirement variables. Checks to
     * see if the active status of the mechanic has changed.
     */
    public UpdateStatus onRequirementUpdate()
    {
        boolean isActive = true;

        for (String requirement : this.requirements())
        {
            if (State.hasVariable(requirement))
            {
                VariableUnion variableUnion = State.variableWithName(requirement);

                if (variableUnion.type() != VariableType.BOOLEAN) {
                    ApplicationFailure.mechanic(
                            MechanicException.nonBooleanRequirement(
                                    new NonBooleanRequirementError(this.name(), requirement)));
                    // TODO add to user programming errors
                    continue;
                }

                if (!variableUnion.booleanVariable().value()) {
                    isActive = false;
                    break;
                }
            }
            else
            {
                isActive = false;
                break;
            }

        }

        // If was active and is now inactive
        if (this.active && !isActive)
        {
            this.removeFromState();
            return UpdateStatus.REMOVED_FROM_STATE;
        }
        // If was not active and is now active
        else if (!this.active && isActive)
        {
            this.addToState();
            return UpdateStatus.ADDED_TO_STATE;
        }

        return UpdateStatus.NO_CHANGE;
    }


    /**
     * Add every variable in the mechanic to the state (if the mechanic is active).
     */
    private void addToState()
    {
        this.active = true;

        for (VariableUnion variableUnion : this.variables()) {
            State.addVariable(variableUnion);
        }
    }


    /**
     * Remove the mechanic from the state. Remove each mechanic variable from the state.
     */
    private void removeFromState()
    {
        this.active = false;

        for (VariableUnion variableUnion : this.variables()) {
            State.removeVariable(variableUnion.variable().name());
        }
    }


    /**
     * Check the requirement variables to make sure they are valid.
     */
    private void validateRequirements()
    {
        // TODO need way to analyze variable before added to state.
    }


    // UPDATE STATUS
    // ------------------------------------------------------------------------------------------

    public enum UpdateStatus
    {
        ADDED_TO_STATE,
        REMOVED_FROM_STATE,
        NO_CHANGE
    }

}