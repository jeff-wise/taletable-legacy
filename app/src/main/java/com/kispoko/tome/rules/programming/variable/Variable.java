
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
 * Variable
 *
 * A variable is a piece of programmable state associated with a component. It could contain a
 * literal value, or a be a value that is generated dynamically from a script.
 */
public class Variable implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                          id;

    private PrimitiveValue<String>        stringValue;
    private PrimitiveValue<Integer>       integerValue;
    private PrimitiveValue<Boolean>       booleanValue;
    private ModelValue<ProgramInvocation> programInvocationValue;

    private PrimitiveValue<VariableType>  type;

    private ModelValue<RefinementId>      refinementId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    /**
     * Create a Variable. This constructor is private to enforce use of the case specific
     * constructors, so only valid value/type associations can be used.
     * @param id The Model id.
     * @param value The Variable value.
     * @param type The Variable type.
     */
    private Variable(UUID id, Object value, VariableType type, RefinementId refinementId)
    {
        this.id                     = id;

        this.stringValue            = new PrimitiveValue<>(null, this, String.class);
        this.integerValue           = new PrimitiveValue<>(null, this, Integer.class);
        this.booleanValue           = new PrimitiveValue<>(null, this, Boolean.class);
        this.programInvocationValue = new ModelValue<>(null, this, ProgramInvocation.class);

        this.type                   = new PrimitiveValue<>(type, this, VariableType.class);

        this.refinementId           = new ModelValue<>(refinementId, this, RefinementId.class);

        // Set the value, depending on the variable type
        switch (type)
        {
            case LITERAL_STRING:
                this.stringValue.setValue((String) value);
                break;
            case LITERAL_INTEGER:
                this.integerValue.setValue((Integer) value);
                break;
            case LITERAL_BOOLEAN:
                this.booleanValue.setValue((Boolean) value);
                break;
            case PROGRAM:
                this.programInvocationValue.setValue((ProgramInvocation) value);
                break;
        }
    }


    /**
     * Create a "string" valued variable.
     * @param id The Model id.
     * @param stringValue The string value.
     * @return A new "string" variable.
     */
    public static Variable asString(UUID id, String stringValue, RefinementId refinementId)
    {
        return new Variable(id, stringValue, VariableType.LITERAL_STRING, refinementId);
    }


    /**
     * Create an "integer" valued variable.
     * @param id The Model id.
     * @param integerValue The Integer value.
     * @return A new "integer" variable.
     */
    public static Variable asInteger(UUID id, Integer integerValue, RefinementId refinementId)
    {
        return new Variable(id, integerValue, VariableType.LITERAL_INTEGER, refinementId);
    }


    /**
     * Create a "boolean" valued variable.
     * @param id The Model id.
     * @param booleanValue The Boolean value.
     * @return A new "boolean" variable.
     */
    public static Variable asBoolean(UUID id, Boolean booleanValue, RefinementId refinementId)
    {
        return new Variable(id, booleanValue, VariableType.LITERAL_BOOLEAN, refinementId);
    }


    /**
     * Create a "program" valued variable.
     * @param id The Model id.
     * @param programInvocation The ProgramInvocation value.
     * @return A new "program" variable.
     */
    public static Variable asProgram(UUID id,
                                     ProgramInvocation programInvocation,
                                     RefinementId refinementId)
    {
        return new Variable(id, programInvocation, VariableType.PROGRAM, refinementId);
    }


    /**
     * Create a new Variable from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The new Variable.
     * @throws YamlException
     */
    public static Variable fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID         id           = UUID.randomUUID();
        VariableType type         = VariableType.fromYaml(yaml.atKey("type"));
        RefinementId refinementId = RefinementId.fromYaml(yaml.atKey("refinement"));

        switch (type)
        {
            case LITERAL_STRING:
                String stringValue = yaml.atKey("value").getString();
                return Variable.asString(id, stringValue, refinementId);
            case LITERAL_INTEGER:
                Integer integerValue = yaml.atKey("value").getInteger();
                return Variable.asInteger(id, integerValue, refinementId);
            case LITERAL_BOOLEAN:
                Boolean booleanValue  = yaml.atKey("value").getBoolean();
                return Variable.asBoolean(id, booleanValue, refinementId);
            case PROGRAM:
                ProgramInvocation invocation = ProgramInvocation.fromYaml(yaml.atKey("value"));
                return Variable.asProgram(id, invocation, refinementId);
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


    // ** String Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the string value.
     * @return The variable's string value. Throws an InvalidCase exception if the variable
     *         is not a String.
     */
    public String getString()
    {
        return this.stringValue.getValue();
    }


    /**
     * Set the string value. Throws an InvalidCase exception if the variable is not a String.
     * @param stringValue The String value.
     */
    public void setString(String stringValue)
    {
        this.stringValue.setValue(stringValue);
    }


    // ** Integer Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the integer value.
     * @return The variable's integer value. Throws an InvalidCase exception if the variable
     *         is not a Integer.
     */
    public Integer getInteger()
    {
        return this.integerValue.getValue();
    }


    /**
     * Set the integer value. Throws an InvalidCase exception if the variable is not a Integer.
     * @param integerValue The Integer value.
     */
    public void setInteger(Integer integerValue)
    {
        this.integerValue.setValue(integerValue);
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
