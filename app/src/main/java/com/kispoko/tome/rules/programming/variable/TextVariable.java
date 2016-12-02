
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

    private PrimitiveValue<String>        name;

    private PrimitiveValue<String>        stringValue;
    private ModelValue<ProgramInvocation> programInvocationValue;

    private PrimitiveValue<VariableKind>  type;

    private ModelValue<RefinementId>      refinementId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextVariable()
    {
        this.id                     = null;

        this.name                   = new PrimitiveValue<>(null, String.class);

        this.stringValue            = new PrimitiveValue<>(null, String.class);
        this.programInvocationValue = ModelValue.empty(ProgramInvocation.class);

        this.type                   = new PrimitiveValue<>(null, VariableKind.class);

        this.refinementId           = ModelValue.empty(RefinementId.class);
    }


    /**
     * Create a Variable. This constructor is private to enforce use of the case specific
     * constructors, so only valid value/type associations can be used.
     * @param id The Model id.
     * @param value The Variable value.
     * @param type The Variable type.
     */
    private TextVariable(UUID id,
                         String name,
                         Object value,
                         VariableKind type,
                         RefinementId refinementId)
    {
        this.id                     = id;

        this.name                   = new PrimitiveValue<>(name, String.class);

        this.stringValue            = new PrimitiveValue<>(null, String.class);
        this.programInvocationValue = ModelValue.full(null, ProgramInvocation.class);

        this.type                   = new PrimitiveValue<>(type, VariableKind.class);

        this.refinementId           = ModelValue.full(refinementId, RefinementId.class);

        // Set value according to variable type
        switch (type)
        {
            case LITERAL:
                this.stringValue.setValue((String) value);
                break;
            case PROGRAM:
                this.programInvocationValue.setValue((ProgramInvocation) value);
                break;
        }

        if (!this.name.isNull())
            SheetManager.registerVariable(this);
    }


    /**
     * Create a "literal" text variable, that contains a value of type String.
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
        if (!this.name.isNull())
            SheetManager.registerVariable(this);
    }


    // > State
    // ------------------------------------------------------------------------------------------

    public String getName()
    {
        return this.name.getValue();
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Type
    // ------------------------------------------------------------------------------------------

    public VariableKind getType()
    {
        return this.type.getValue();
    }


    // ** String Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the string value.
     * @return The variable's String value. Throws an InvalidCase exception if the variable
     *         is not a String.
     */
    public String getString()
    {
        return this.stringValue.getValue();
    }


    /**
     * Set the string value. Throws an InvalidCase exception if the variable is not a String.
     * @param stringValue The Boolean value.
     */
    public void setString(String stringValue)
    {
        this.stringValue.setValue(stringValue);
    }


    // ** Program Invocation Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the Program Invocation value.
     * @return The variable's program invocation value. Throws an InvalidCase exception if the
     *         variable is not a ProgramInvocation.
     */
    public ProgramInvocation getProgramInvocation()
    {
        return this.programInvocationValue.getValue();
    }


    /**
     * Set the Program Invocation value. Throws an InvalidCase exception if the variable is not
     * a ProgramInvocation.
     * @param programInvocationValue The ProgramInvocation value.
     */
    public void setProgramInvocation(ProgramInvocation programInvocationValue)
    {
        this.programInvocationValue.setValue(programInvocationValue);
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
        switch (getType())
        {
            case LITERAL:
                return this.stringValue == null;
            case PROGRAM:
                return this.programInvocationValue == null;
        }

        return true;
    }


}