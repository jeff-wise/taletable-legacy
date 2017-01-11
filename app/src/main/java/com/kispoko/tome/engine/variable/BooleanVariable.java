
package com.kispoko.tome.engine.variable;


import com.kispoko.tome.engine.State;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.engine.programming.program.invocation.Invocation;
import com.kispoko.tome.engine.refinement.RefinementId;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



/**
 * Boolean Variable
 */
public class BooleanVariable extends Variable
                             implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>    name;

    private PrimitiveFunctor<Boolean>   booleanValue;
    private ModelFunctor<Invocation>    invocationValue;

    private PrimitiveFunctor<Kind>      kind;
    private ModelFunctor<RefinementId>  refinementId;

    private PrimitiveFunctor<Boolean>   isNamespaced;

    private PrimitiveFunctor<String[]>  tags;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private ReactiveValue<Boolean>      reactiveValue;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BooleanVariable()
    {
        super();

        this.id                     = null;

        this.name                   = new PrimitiveFunctor<>(null, String.class);

        this.booleanValue           = new PrimitiveFunctor<>(null, Boolean.class);
        this.invocationValue        = ModelFunctor.empty(Invocation.class);

        this.kind                   = new PrimitiveFunctor<>(null, Kind.class);

        this.refinementId           = ModelFunctor.empty(RefinementId.class);

        this.isNamespaced           = new PrimitiveFunctor<>(null, Boolean.class);

        this.tags                   = new PrimitiveFunctor<>(null, String[].class);

        this.reactiveValue          = null;
    }


    /**
     * Create a Variable. This constructor is private to enforce use of the case specific
     * constructors, so only valid value/type associations can be used.
     * @param id The Model id.
     * @param value The Variable value.
     * @param kind The Variable kind.
     */
    private BooleanVariable(UUID id,
                            String name,
                            Object value,
                            Kind kind,
                            RefinementId refinementId,
                            Boolean isNamespaced,
                            List<String> tags)
    {
        super();

        this.id                     = id;

        this.name                   = new PrimitiveFunctor<>(name, String.class);

        this.booleanValue           = new PrimitiveFunctor<>(null, Boolean.class);
        this.invocationValue        = ModelFunctor.full(null, Invocation.class);

        this.kind                   = new PrimitiveFunctor<>(kind, Kind.class);

        this.refinementId           = ModelFunctor.full(refinementId, RefinementId.class);

        if (isNamespaced == null) isNamespaced = false;

        this.isNamespaced           = new PrimitiveFunctor<>(isNamespaced, Boolean.class);

        if (tags != null) {
            String[] tagsArray = new String[tags.size()];
            tags.toArray(tagsArray);
            this.tags               = new PrimitiveFunctor<>(tagsArray, String[].class);
        }
        else {
            this.tags               = new PrimitiveFunctor<>(new String[0], String[].class);
        }

        // Set value according to variable type
        switch (kind)
        {
            case LITERAL:
                this.booleanValue.setValue((Boolean) value);
                break;
            case PROGRAM:
                this.invocationValue.setValue((Invocation) value);
                break;
        }

        initialize();
    }


    /**
     * Create a "boolean" valued variable.
     * @param id The Model id.
     * @param name The variable name.
     * @param booleanValue The Boolean value.
     * @param refinementId The id of the variable's refinement.
     * @param tags The variable's tags.
     * @return A new "boolean" variable.
     */
    public static BooleanVariable asBoolean(UUID id,
                                            String name,
                                            Boolean booleanValue,
                                            RefinementId refinementId,
                                            Boolean isNamespaced,
                                            List<String> tags)
    {
        return new BooleanVariable(id, name, booleanValue, Kind.LITERAL, refinementId,
                                   isNamespaced, tags);
    }


    /**
     * Create a "boolean" valued variable.
     * @param id The Model id.
     * @param booleanValue The Boolean value.
     * @return A new "boolean" variable.
     */
    public static BooleanVariable asBoolean(UUID id,
                                            Boolean booleanValue)
    {
        return new BooleanVariable(id, null, booleanValue, Kind.LITERAL, null, null, null);
    }


    /**
     * Create a "program" valued variable.
     * @param id The Model id.
     * @param invocation The Invocation value.
     * @return A new "program" variable.
     */
    public static BooleanVariable asProgram(UUID id,
                                            String name,
                                            Invocation invocation,
                                            RefinementId refinementId,
                                            Boolean isNamespaced,
                                            List<String> tags)
    {
        return new BooleanVariable(id, name, invocation, Kind.PROGRAM, refinementId,
                                   isNamespaced, tags);
    }


    /**
     * Create a new Variable from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The new Variable.
     * @throws YamlParseException
     */
    public static BooleanVariable fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return null;

        UUID         id                 = UUID.randomUUID();
        String       name               = yaml.atMaybeKey("name").getString();
        Kind         kind               = Kind.fromYaml(yaml.atKey("type"));
        RefinementId refinementId       = RefinementId.fromYaml(yaml.atMaybeKey("refinement"));
        Boolean      isNamespaced       = yaml.atMaybeKey("namespaced").getBoolean();
        List<String> tags               = yaml.atMaybeKey("tags").getStringList();

        switch (kind)
        {
            case LITERAL:
                Boolean booleanValue  = yaml.atKey("value").getBoolean();
                return BooleanVariable.asBoolean(id, name, booleanValue, refinementId,
                                                 isNamespaced, tags);
            case PROGRAM:
                Invocation invocation = Invocation.fromYaml(yaml.atKey("value"));
                return BooleanVariable.asProgram(id, name, invocation, refinementId,
                                                 isNamespaced, tags);
        }

        // CANNOT REACH HERE. If VariableKind is null, an InvalidEnum exception would be thrown.
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

    public void onLoad()
    {
        initialize();
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Boolean Variable's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putYaml("type", this.kind())
                .putYaml("refinement", this.refinementId())
                .putBoolean("namespaced", this.isNamespaced())
                .putStringList("tags", this.tags());
    }


    // > Variable
    // ------------------------------------------------------------------------------------------

    @Override
    public String name()
    {
        return this.name.getValue();
    }


    @Override
    public void setName(String name)
    {
        // > Set the name
        String oldName = this.name();
        this.name.setValue(name);

        // > Reindex variable
        State.removeVariable(oldName);
        State.addVariable(this);
    }


    @Override
    public boolean isNamespaced()
    {
        return this.isNamespaced.getValue();
    }


    @Override
    public List<VariableReference> dependencies()
    {
        List<VariableReference> variableDependencies = new ArrayList<>();

        if (this.kind.getValue() == Kind.PROGRAM) {
            variableDependencies = this.invocation().variableDependencies();
        }

        return variableDependencies;
    }


    @Override
    public List<String> tags()
    {
        return Arrays.asList(this.tags.getValue());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The program invocation case.
     * @return The invocation.
     */
    private Invocation invocation()
    {
        return this.invocationValue.getValue();
    }


    // ** Kind
    // ------------------------------------------------------------------------------------------

    /**
     * The Boolean Variable kind.
     * @return The Kind.
     */
    public Kind kind()
    {
        return this.kind.getValue();
    }

    // ** Value
    // ------------------------------------------------------------------------------------------

    /**
     * Set the boolean variable integer. value
     * @param newValue The boolean value.
     */
    public void setValue(Boolean newValue)
    {
        switch (this.kind.getValue())
        {
            case LITERAL:
                this.booleanValue.setValue(newValue);
                this.onUpdate();
                break;
            case PROGRAM:
                break;
        }
    }


    /**
     * Get the boolean variable's integer value.
     * @return The boolean value.
     */
    public Boolean value()
    {
        switch (this.kind.getValue())
        {
            case LITERAL:
                return this.booleanValue.getValue();
            case PROGRAM:
                return this.reactiveValue.value();
        }

        return null;
    }


    // ** Refinement
    // ------------------------------------------------------------------------------------------


    /**
     * Returns true if the variable has a refinement.
     * @return True if the variable has a refinement.
     */
    public boolean hasRefinement()
    {
        return this.refinementId != null;
    }


    /**
     * Get the refinement identifier for this variable.
     * @return The variable's refinement id, or null if there is none.
     */
    public RefinementId refinementId()
    {
        return this.refinementId.getValue();
    }


    // > Initialize
    // ------------------------------------------------------------------------------------------

    public void initialize()
    {
        // [1] Create reaction value (if program variable)
        // --------------------------------------------------------------------------------------

        if (this.kind.getValue() == Kind.PROGRAM) {
            this.reactiveValue = new ReactiveValue<>(this.invocationValue.getValue(),
                                                     VariableType.NUMBER);
        }
        else {
            this.reactiveValue = null;
        }

        // [2] Add any variables associated with the value to the state
        // --------------------------------------------------------------------------------------

        this.addToState();
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // ** Variable State
    // ------------------------------------------------------------------------------------------

    /**
     * Add any variables associated with the current value to the state.
     */
    private void addToState()
    {
    }


    private void removeFromState()
    {
    }


    // KIND
    // ------------------------------------------------------------------------------------------

    public enum Kind implements ToYaml
    {

        // VALUES
        // ------------------------------------------------------------------------------------------

        LITERAL,
        PROGRAM;


        // CONSTRUCTORS
        // ------------------------------------------------------------------------------------------

        public static Kind fromString(String kindString)
                      throws InvalidDataException
        {
            return EnumUtils.fromString(Kind.class, kindString);
        }


        public static Kind fromYaml(YamlParser yaml)
                      throws YamlParseException
        {
            String kindString = yaml.getString();
            try {
                return Kind.fromString(kindString);
            } catch (InvalidDataException e) {
                throw YamlParseException.invalidEnum(new InvalidEnumError(kindString));
            }
        }


        public static Kind fromSQLValue(SQLValue sqlValue)
                      throws DatabaseException
        {
            String enumString = "";
            try {
                enumString = sqlValue.getText();
                Kind kind = Kind.fromString(enumString);
                return kind;
            } catch (InvalidDataException e) {
                throw DatabaseException.invalidEnum(
                        new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
            }
        }


        // TO YAML
        // ------------------------------------------------------------------------------------------

        public YamlBuilder toYaml()
        {
            return YamlBuilder.string(this.name().toLowerCase());
        }

    }

}
