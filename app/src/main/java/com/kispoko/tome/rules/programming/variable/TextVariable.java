
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



/**
 * Text Variable
 */
public class TextVariable implements Model, Variable, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveValue<String>        name;

    private PrimitiveValue<String>        stringValue;
    private ModelValue<ProgramInvocation> programInvocationValue;

    private PrimitiveValue<VariableKind>  kind;

    private ModelValue<RefinementId>      refinementId;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private ReactiveValue<String>         reactiveValue;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextVariable()
    {
        this.id                     = null;

        this.name                   = new PrimitiveValue<>(null, String.class);

        this.stringValue            = new PrimitiveValue<>(null, String.class);
        this.programInvocationValue = ModelValue.empty(ProgramInvocation.class);

        this.kind = new PrimitiveValue<>(null, VariableKind.class);

        this.refinementId           = ModelValue.empty(RefinementId.class);

        this.reactiveValue          = null;
    }


    /**
     * Create a Variable. This constructor is private to enforce use of the case specific
     * constructors, so only valid value/kind associations can be used.
     * @param id The Model id.
     * @param value The Variable value.
     * @param kind The Variable kind.
     */
    private TextVariable(UUID id,
                         String name,
                         Object value,
                         VariableKind kind,
                         RefinementId refinementId)
    {
        // ** Id
        this.id                     = id;

        // ** Name
        this.name                   = new PrimitiveValue<>(name, String.class);

        // ** Value Variants
        this.stringValue            = new PrimitiveValue<>(null, String.class);
        this.programInvocationValue = ModelValue.full(null, ProgramInvocation.class);

        // ** Kind (Literal or Program)
        this.kind = new PrimitiveValue<>(kind, VariableKind.class);

        // ** Refinement Id (if any)
        this.refinementId           = ModelValue.full(refinementId, RefinementId.class);

        // > Set the value according to variable kind
        switch (kind)
        {
            case LITERAL:
                this.stringValue.setValue((String) value);
                break;
            case PROGRAM:
                this.programInvocationValue.setValue((ProgramInvocation) value);
                break;
        }

        initialize();
    }


    /**
     * Create a "literal" text variable, that contains a value of kind String.
     * @param id The Model id.
     * @param stringValue The String value.
     * @return A new "literal" Text Variable.
     */
    public static TextVariable asText(UUID id,
                                      String name,
                                      String stringValue,
                                      RefinementId refinementId)
    {
        return new TextVariable(id, name, stringValue, VariableKind.LITERAL, refinementId);
    }


    /**
     * Create a "program" valued variable.
     * @param id The Model id.
     * @param programInvocation The ProgramInvocation value.
     * @return A new "program" variable.
     */
    public static TextVariable asProgram(UUID id,
                                         String name,
                                         ProgramInvocation programInvocation,
                                         RefinementId refinementId)
    {
        return new TextVariable(id, name, programInvocation, VariableKind.PROGRAM, refinementId);
    }


    /**
     * Create a new Variable from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The new Variable.
     * @throws YamlException
     */
    public static TextVariable fromYaml(Yaml yaml)
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
                String stringValue  = yaml.atKey("value").getString();
                return TextVariable.asText(id, name, stringValue, refinementId);
            case PROGRAM:
                ProgramInvocation invocation = ProgramInvocation.fromYaml(yaml.atKey("value"));
                return TextVariable.asProgram(id, name, invocation, refinementId);
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

    // ** Name
    // ------------------------------------------------------------------------------------------

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

    public void setValue(String newValue)
    {
        switch (this.getKind())
        {
            case LITERAL:
                this.stringValue.setValue(newValue);
                break;
            case PROGRAM:
                this.reactiveValue.setValue(newValue);
                break;
        }
    }


    public String getValue()
    {
        switch (this.getKind())
        {
            case LITERAL:
                return this.stringValue.getValue();
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


    // > Null
    // ------------------------------------------------------------------------------------------

    public boolean isNull()
    {
        switch (getKind())
        {
            case LITERAL:
                return this.stringValue == null;
            case PROGRAM:
                return this.programInvocationValue == null;
        }

        return true;
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
                                                     VariableType.TEXT);
        }
        else {
            this.reactiveValue = null;
        }
    }

}
