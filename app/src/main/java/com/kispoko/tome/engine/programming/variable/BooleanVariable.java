
package com.kispoko.tome.engine.programming.variable;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.engine.programming.program.invocation.Invocation;
import com.kispoko.tome.engine.refinement.RefinementId;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Boolean Variable
 */
public class BooleanVariable extends Variable implements Model, Serializable
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
        this.booleanValue           = new PrimitiveFunctor<>(null, Boolean.class);
        this.invocationValue        = ModelFunctor.empty(Invocation.class);

        this.kind                   = new PrimitiveFunctor<>(null, Kind.class);
        this.refinementId           = ModelFunctor.empty(RefinementId.class);
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
                            List<String> tags)
    {
        super();

        this.id                     = id;

        this.name                   = new PrimitiveFunctor<>(name, String.class);

        this.booleanValue           = new PrimitiveFunctor<>(null, Boolean.class);
        this.invocationValue = ModelFunctor.full(null, Invocation.class);

        this.kind                   = new PrimitiveFunctor<>(kind, Kind.class);
        this.refinementId           = ModelFunctor.full(refinementId, RefinementId.class);

        String[] tagsArray = new String[tags.size()];
        tags.toArray(tagsArray);
        this.tags                   = new PrimitiveFunctor<>(tagsArray, String[].class);

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
     * @param booleanValue The Boolean value.
     * @return A new "boolean" variable.
     */
    public static BooleanVariable asBoolean(UUID id,
                                            String name,
                                            Boolean booleanValue,
                                            RefinementId refinementId,
                                            List<String> tags)
    {
        return new BooleanVariable(id, name, booleanValue, Kind.LITERAL, refinementId, tags);
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
                                            List<String> tags)
    {
        return new BooleanVariable(id, name, invocation, Kind.PROGRAM, refinementId, tags);
    }


    /**
     * Create a new Variable from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The new Variable.
     * @throws YamlException
     */
    public static BooleanVariable fromYaml(Yaml yaml)
                  throws YamlException
    {
        if (yaml.isNull())
            return null;

        UUID         id           = UUID.randomUUID();
        String       name         = yaml.atMaybeKey("name").getString();
        Kind         kind         = Kind.fromYaml(yaml.atKey("type"));
        RefinementId refinementId = RefinementId.fromYaml(yaml.atMaybeKey("refinement"));
        List<String> tags         = yaml.atMaybeKey("tags").getStringList();

        switch (kind)
        {
            case LITERAL:
                Boolean booleanValue  = yaml.atKey("value").getBoolean();
                return BooleanVariable.asBoolean(id, name, booleanValue, refinementId, tags);
            case PROGRAM:
                Invocation invocation = Invocation.fromYaml(yaml.atKey("value"));
                return BooleanVariable.asProgram(id, name, invocation, refinementId, tags);
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


    // > Variable
    // ------------------------------------------------------------------------------------------

    public String name()
    {
        return this.name.getValue();
    }


    public List<String> dependencies()
    {
        List<String> variableDependencies = new ArrayList<>();

        if (this.kind.getValue() == Kind.PROGRAM) {
            variableDependencies = this.invocationValue.getValue().variableDependencies();
        }

        return variableDependencies;
    }


    // > State
    // ------------------------------------------------------------------------------------------

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
    public RefinementId getRefinementId()
    {
        return this.refinementId.getValue();
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initialize()
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
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    public enum Kind
    {
        LITERAL,
        PROGRAM;


        public static Kind fromString(String kindString)
                      throws InvalidDataException
        {
            return EnumUtils.fromString(Kind.class, kindString);
        }


        public static Kind fromYaml(Yaml yaml)
                      throws YamlException
        {
            String kindString = yaml.getString();
            try {
                return Kind.fromString(kindString);
            } catch (InvalidDataException e) {
                throw YamlException.invalidEnum(new InvalidEnumError(kindString));
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

    }

}
