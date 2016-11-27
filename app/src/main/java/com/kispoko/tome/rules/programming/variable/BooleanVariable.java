
package com.kispoko.tome.rules.programming.variable;


import com.kispoko.tome.rules.programming.program.ProgramInvocation;
import com.kispoko.tome.rules.refinement.RefinementId;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;

import static com.kispoko.tome.rules.programming.variable.VariableType.PROGRAM;



/**
 * Boolean Variable
 */
public class BooleanVariable implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private PrimitiveValue<Boolean>       booleanValue;
    private ModelValue<ProgramInvocation> programInvocationValue;

    private PrimitiveValue<VariableType>  type;

    private ModelValue<RefinementId>      refinementId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BooleanVariable()
    {
        this.id                     = null;

        this.booleanValue           = new PrimitiveValue<>(null, Boolean.class);
        this.programInvocationValue = new ModelValue<>(null, ProgramInvocation.class);

        this.type                   = new PrimitiveValue<>(null, VariableType.class);

        this.refinementId           = new ModelValue<>(null, RefinementId.class);
    }


    /**
     * Create a Variable. This constructor is private to enforce use of the case specific
     * constructors, so only valid value/type associations can be used.
     * @param id The Model id.
     * @param value The Variable value.
     * @param type The Variable type.
     */
    private BooleanVariable(UUID id, Object value, VariableType type, RefinementId refinementId)
    {
        this.id                     = id;

        this.booleanValue           = new PrimitiveValue<>(null, Boolean.class);
        this.programInvocationValue = new ModelValue<>(null, ProgramInvocation.class);

        this.type                   = new PrimitiveValue<>(type, VariableType.class);

        this.refinementId           = new ModelValue<>(refinementId, RefinementId.class);

        // Set value according to variable type
        switch (type)
        {
            case LITERAL:
                this.booleanValue.setValue((Boolean) value);
                break;
            case PROGRAM:
                this.programInvocationValue.setValue((ProgramInvocation) value);
                break;
        }
    }


    /**
     * Create a "boolean" valued variable.
     * @param id The Model id.
     * @param booleanValue The Boolean value.
     * @return A new "boolean" variable.
     */
    public static BooleanVariable asBoolean(UUID id,
                                            Boolean booleanValue,
                                            RefinementId refinementId)
    {
        return new BooleanVariable(id, booleanValue, VariableType.LITERAL, refinementId);
    }


    /**
     * Create a "program" valued variable.
     * @param id The Model id.
     * @param programInvocation The ProgramInvocation value.
     * @return A new "program" variable.
     */
    public static BooleanVariable asProgram(UUID id,
                                            ProgramInvocation programInvocation,
                                            RefinementId refinementId)
    {
        return new BooleanVariable(id, programInvocation, PROGRAM, refinementId);
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
        VariableType type         = VariableType.fromYaml(yaml.atKey("type"));
        RefinementId refinementId = RefinementId.fromYaml(yaml.atMaybeKey("refinement"));

        switch (type)
        {
            case LITERAL:
                Boolean booleanValue  = yaml.atKey("value").getBoolean();
                return BooleanVariable.asBoolean(id, booleanValue, refinementId);
            case PROGRAM:
                ProgramInvocation invocation = ProgramInvocation.fromYaml(yaml.atKey("value"));
                return BooleanVariable.asProgram(id, invocation, refinementId);
        }

        // CANNOT REACH HERE. If VariableType is null, an InvalidEnum exception would be thrown.
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


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onValueUpdate(String valueName) { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Type
    // ------------------------------------------------------------------------------------------

    public VariableType getType()
    {
        return this.type.getValue();
    }


    // ** Boolean Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the boolean value.
     * @return The variable's boolean value. Throws an InvalidCase exception if the variable
     *         is not a Boolean.
     */
    public Boolean getBoolean()
    {
        return this.booleanValue.getValue();
    }


    /**
     * Set the boolean value. Throws an InvalidCase exception if the variable is not a Boolean.
     * @param booleanValue The Boolean value.
     */
    public void setBoolean(Boolean booleanValue)
    {
        this.booleanValue.setValue(booleanValue);
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


}
