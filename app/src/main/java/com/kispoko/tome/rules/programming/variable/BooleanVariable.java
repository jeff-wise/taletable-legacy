
package com.kispoko.tome.rules.programming.variable;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.rules.programming.program.ProgramInvocation;
import com.kispoko.tome.rules.refinement.RefinementId;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import java.io.Serializable;
import java.util.UUID;

import static android.R.attr.type;


/**
 * Boolean Variable
 */
public class BooleanVariable implements Model, Variable, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private PrimitiveValue<String>        name;


    private PrimitiveValue<Boolean>       booleanValue;
    private ModelValue<ProgramInvocation> programInvocationValue;

    private PrimitiveValue<Kind>  kind;

    private ModelValue<RefinementId>      refinementId;

    private ReactiveValue<Boolean>        reactiveValue;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BooleanVariable()
    {
        this.id                     = null;

        this.name                   = new PrimitiveValue<>(null, String.class);

        this.booleanValue           = new PrimitiveValue<>(null, Boolean.class);
        this.booleanValue           = new PrimitiveValue<>(null, Boolean.class);
        this.programInvocationValue = ModelValue.empty(ProgramInvocation.class);

        this.kind                   = new PrimitiveValue<>(null, Kind.class);

        this.refinementId           = ModelValue.empty(RefinementId.class);

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
                            RefinementId refinementId)
    {
        this.id                     = id;

        this.name                   = new PrimitiveValue<>(name, String.class);

        this.booleanValue           = new PrimitiveValue<>(null, Boolean.class);
        this.programInvocationValue = ModelValue.full(null, ProgramInvocation.class);

        this.kind                   = new PrimitiveValue<>(kind, Kind.class);

        this.refinementId           = ModelValue.full(refinementId, RefinementId.class);

        // Set value according to variable type
        switch (kind)
        {
            case LITERAL:
                this.booleanValue.setValue((Boolean) value);
                break;
            case PROGRAM:
                this.programInvocationValue.setValue((ProgramInvocation) value);
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
                                            RefinementId refinementId)
    {
        return new BooleanVariable(id, name, booleanValue, Kind.LITERAL, refinementId);
    }


    /**
     * Create a "program" valued variable.
     * @param id The Model id.
     * @param programInvocation The ProgramInvocation value.
     * @return A new "program" variable.
     */
    public static BooleanVariable asProgram(UUID id,
                                            String name,
                                            ProgramInvocation programInvocation,
                                            RefinementId refinementId)
    {
        return new BooleanVariable(id, name, programInvocation, Kind.PROGRAM, refinementId);
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

        switch (kind)
        {
            case LITERAL:
                Boolean booleanValue  = yaml.atKey("value").getBoolean();
                return BooleanVariable.asBoolean(id, name, booleanValue, refinementId);
            case PROGRAM:
                ProgramInvocation invocation = ProgramInvocation.fromYaml(yaml.atKey("value"));
                return BooleanVariable.asProgram(id, name, invocation, refinementId);
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

    public String getName()
    {
        return this.name.getValue();
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
        if (!this.name.isNull())
            SheetManager.registerVariable(this);

        // ** Reaction Value (if program variable)
        if (this.kind.getValue() == Kind.PROGRAM) {
            this.reactiveValue = new ReactiveValue<>(this.programInvocationValue.getValue(),
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
