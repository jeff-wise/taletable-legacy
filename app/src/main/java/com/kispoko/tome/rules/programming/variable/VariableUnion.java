
package com.kispoko.tome.rules.programming.variable;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.rules.programming.program.Program;

/**
 * Variable Union
 */
public class VariableUnion
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object       variable;
    private VariableType type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    private VariableUnion(Object variable, VariableType type)
    {
        this.variable = variable;
        this.type     = type;
    }


    /**
     * Create the "text" variant.
     * @param textVariable The text variable.
     * @return The new Variable Union as the "text" case.
     */
    public static VariableUnion asText(TextVariable textVariable)
    {
        return new VariableUnion(textVariable, VariableType.TEXT);
    }


    /**
     * Create the "number" variant.
     * @param numberVariable The number variable.
     * @return The new Variable Union as the "number" case.
     */
    public static VariableUnion asNumber(NumberVariable numberVariable)
    {
        return new VariableUnion(numberVariable, VariableType.NUMBER);
    }


    /**
     * Create the "boolean" variant.
     * @param booleanVariable The boolean variable.
     * @return The new Variable Union as the "boolean" case.
     */
    public static VariableUnion asBoolean(BooleanVariable booleanVariable)
    {
        return new VariableUnion(booleanVariable, VariableType.BOOLEAN);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // ** Name
    // ------------------------------------------------------------------------------------------

    /**
     * Get the name of the variable.
     * @return The variable name.
     */
    public String getName()
    {
        return ((Variable) this.variable).getName();
    }


    // ** Type
    // ------------------------------------------------------------------------------------------

    /**
     * Get the variant type of the variable union.
     * @return The Variable Type.
     */
    public VariableType getType()
    {
        return this.type;
    }


    // ** Text Variable
    // ------------------------------------------------------------------------------------------

    // TODO invalid case exception

    /**
     * Get the text case of the union.
     * @return The Text Variable.
     */
    public TextVariable getText()
    {
        if (this.type != VariableType.TEXT) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("text", this.type.toString())));
        }
        return (TextVariable) variable;
    }


    /**
     * Get the number case of the union.
     * @return The Number Variable.
     */
    public NumberVariable getNumber()
    {
        if (this.type != VariableType.NUMBER) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("number", this.type.toString())));
        }
        return (NumberVariable) variable;
    }


    /**
     * Get the boolean case of the union.
     * @return The Boolean Variable.
     */
    public BooleanVariable getBoolean()
    {
        if (this.type != VariableType.BOOLEAN) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("boolean", this.type.toString())));
        }
        return (BooleanVariable) variable;
    }


}
