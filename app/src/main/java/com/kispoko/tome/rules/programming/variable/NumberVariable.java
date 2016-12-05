
package com.kispoko.tome.rules.programming.variable;


import com.kispoko.tome.rules.programming.program.ProgramInvocation;
import com.kispoko.tome.rules.refinement.RefinementId;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;

import static com.kispoko.tome.rules.programming.variable.VariableKind.PROGRAM;


/**
 * Number Variable
 */
public class NumberVariable implements Model, Variable, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private PrimitiveValue<String>        name;

    private PrimitiveValue<Integer>       integerValue;
    private ModelValue<ProgramInvocation> programInvocationValue;

    private PrimitiveValue<VariableKind>  kind;

    private ModelValue<RefinementId>      refinementId;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private ReactiveValue<Integer>        reactiveValue;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberVariable()
    {
        this.id                     = null;

        this.name                   = new PrimitiveValue<>(null, String.class);

        this.integerValue           = new PrimitiveValue<>(null, Integer.class);
        this.programInvocationValue = ModelValue.empty(ProgramInvocation.class);

        this.kind                   = new PrimitiveValue<>(null, VariableKind.class);

        this.refinementId           = ModelValue.empty(RefinementId.class);

        this.reactiveValue          = null;
    }


    /**
     * Create a Variable. This constructor is private to enforce use of the case specific
     * constructors, so only valid value/type associations can be used.
     * @param id The Model id.
     * @param value The Variable value.
     * @param type The Variable type.
     */
    private NumberVariable(UUID id,
                           String name,
                           Object value,
                           VariableKind type,
                           RefinementId refinementId)
    {
        this.id                     = id;

        this.name                   = new PrimitiveValue<>(name, String.class);

        this.integerValue           = new PrimitiveValue<>(null, Integer.class);
        this.programInvocationValue = ModelValue.full(null, ProgramInvocation.class);

        this.kind                   = new PrimitiveValue<>(type, VariableKind.class);

        this.refinementId           = ModelValue.full(refinementId, RefinementId.class);

        // Set value according to variable type
        switch (type)
        {
            case LITERAL:
                this.integerValue.setValue((Integer) value);
                break;
            case PROGRAM:
                this.programInvocationValue.setValue((ProgramInvocation) value);
                break;
        }

        initialize();
    }


    /**
     * Create a "literal" number variable that contains a value of type Integer.
     * @param id The Model id.
     * @param integerValue The Integer value.
     * @return A new "literal" Integer Variable.
     */
    public static NumberVariable asInteger(UUID id,
                                           String name,
                                           Integer integerValue,
                                           RefinementId refinementId)
    {
        return new NumberVariable(id, name, integerValue, VariableKind.LITERAL, refinementId);
    }


    /**
     * Create a "program" valued variable.
     * @param id The Model id.
     * @param programInvocation The ProgramInvocation value.
     * @return A new "program" variable.
     */
    public static NumberVariable asProgram(UUID id,
                                           String name,
                                           ProgramInvocation programInvocation,
                                           RefinementId refinementId)
    {
        return new NumberVariable(id, name, programInvocation, PROGRAM, refinementId);
    }


    /**
     * Create a new Variable from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The new Variable.
     * @throws YamlException
     */
    public static NumberVariable fromYaml(Yaml yaml)
                  throws YamlException
    {
        if (yaml.isNull())
            return null;

        UUID         id           = UUID.randomUUID();
        String       name         = yaml.atMaybeKey("name").getString();
        VariableKind type         = VariableKind.fromYaml(yaml.atKey("type"));
        RefinementId refinementId = RefinementId.fromYaml(yaml.atMaybeKey("refinement"));

        switch (type)
        {
            case LITERAL:
                Integer integerValue  = yaml.atKey("value").getInteger();
                return NumberVariable.asInteger(id, name, integerValue, refinementId);
            case PROGRAM:
                ProgramInvocation invocation = ProgramInvocation.fromYaml(yaml.atKey("value"));
                return NumberVariable.asProgram(id, name, invocation, refinementId);
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

    /**
     * Get the variable name which is a unique identifier.
     * @return The variable name.
     */
    public String getName()
    {
        return this.name.getValue();
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Kind
    // ------------------------------------------------------------------------------------------

    public VariableKind getKind()
    {
        return this.kind.getValue();
    }


    // ** Value
    // ------------------------------------------------------------------------------------------

    /**
     * Set the number variable integer. value
     * @param newValue The integer value.
     */
    public void setValue(Integer newValue)
    {
        switch (this.getKind())
        {
            case LITERAL:
                this.integerValue.setValue(newValue);
                break;
            case PROGRAM:
                this.reactiveValue.setValue(newValue);
                break;
        }
    }


    /**
     * Get the number variable's integer value.
     * @return The integer value.
     */
    public Integer getValue()
    {
        switch (this.getKind())
        {
            case LITERAL:
                return this.integerValue.getValue();
            case PROGRAM:
                return this.reactiveValue.getValue();
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
        if (this.getKind() == VariableKind.PROGRAM) {
            this.reactiveValue = new ReactiveValue<>(this.programInvocationValue.getValue(),
                                                     VariableType.NUMBER);
        }
        else {
            this.reactiveValue = null;
        }
    }

}
