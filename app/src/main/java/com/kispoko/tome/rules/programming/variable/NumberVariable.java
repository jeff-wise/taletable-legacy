
package com.kispoko.tome.rules.programming.variable;


import com.kispoko.tome.rules.programming.program.ProgramInvocation;
import com.kispoko.tome.rules.refinement.RefinementId;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;


/**
 * Number Variable
 */
public class NumberVariable implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private PrimitiveValue<Integer>       integerValue;
    private ModelValue<ProgramInvocation> programInvocationValue;

    private PrimitiveValue<VariableType>  type;

    private ModelValue<RefinementId>      refinementId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberVariable() { }


    /**
     * Create a Variable. This constructor is private to enforce use of the case specific
     * constructors, so only valid value/type associations can be used.
     * @param id The Model id.
     * @param value The Variable value.
     * @param type The Variable type.
     */
    private NumberVariable(UUID id, Object value, VariableType type, RefinementId refinementId)
    {
        this.id                     = id;

        this.integerValue           = new PrimitiveValue<>(null, this, Integer.class);
        this.programInvocationValue = new ModelValue<>(null, this, ProgramInvocation.class);

        this.type                   = new PrimitiveValue<>(type, this, VariableType.class);

        this.refinementId           = new ModelValue<>(refinementId, this, RefinementId.class);

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
    }


    /**
     * Create a "literal" number variable that contains a value of type Integer.
     * @param id The Model id.
     * @param integerValue The Integer value.
     * @return A new "literal" Integer Variable.
     */
    public static NumberVariable asInteger(UUID id, Integer integerValue, RefinementId refinementId)
    {
        return new NumberVariable(id, integerValue, VariableType.LITERAL, refinementId);
    }


    /**
     * Create a "program" valued variable.
     * @param id The Model id.
     * @param programInvocation The ProgramInvocation value.
     * @return A new "program" variable.
     */
    public static NumberVariable asProgram(UUID id,
                                           ProgramInvocation programInvocation,
                                           RefinementId refinementId)
    {
        return new NumberVariable(id, programInvocation, VariableType.PROGRAM, refinementId);
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
        UUID         id           = UUID.randomUUID();
        VariableType type         = VariableType.fromYaml(yaml.atKey("type"));
        RefinementId refinementId = RefinementId.fromYaml(yaml.atMaybeKey("refinement"));

        switch (type)
        {
            case LITERAL:
                Integer integerValue  = yaml.atKey("value").getInteger();
                return NumberVariable.asInteger(id, integerValue, refinementId);
            case PROGRAM:
                ProgramInvocation invocation = ProgramInvocation.fromYaml(yaml.atKey("value"));
                return NumberVariable.asProgram(id, invocation, refinementId);
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

    public void onModelUpdate(String valueName) { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Type
    // ------------------------------------------------------------------------------------------

    public VariableType getType()
    {
        return this.type.getValue();
    }


    // ** Number Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the number value.
     * @return The variable's Integer value. Throws an InvalidCase exception if the variable
     *         is not a Number.
     */
    public Integer getInteger()
    {
        return this.integerValue.getValue();
    }


    /**
     * Set the number value. Throws an InvalidCase exception if the variable is not a Number.
     * @param integerValue The Boolean value.
     */
    public void setInteger(Integer integerValue)
    {
        this.integerValue.setValue(integerValue);
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
