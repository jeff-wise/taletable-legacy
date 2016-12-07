
package com.kispoko.tome.rules.programming.variable;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.rules.programming.summation.term.BooleanTermValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;



/**
 * Variable Union
 */
public class VariableUnion implements Serializable
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


    public static VariableUnion fromYaml(Yaml yaml)
                  throws YamlException
    {
        VariableType type = VariableType.fromYaml(yaml.atKey("type"));

        switch (type)
        {
            case TEXT:
                TextVariable textVariable = TextVariable.fromYaml(yaml.atKey("value"));
                return VariableUnion.asText(textVariable);
            case NUMBER:
                NumberVariable numberVariable = NumberVariable.fromYaml(yaml.atKey("value"));
                return VariableUnion.asNumber(numberVariable);
            case BOOLEAN:
                BooleanVariable booleanVariable = BooleanVariable.fromYaml(yaml.atKey("value"));
                return VariableUnion.asBoolean(booleanVariable);
        }

        return null;
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


    // ** ErrorType
    // ------------------------------------------------------------------------------------------

    /**
     * Get the variant type of the variable union.
     * @return The Variable ErrorType.
     */
    public VariableType getType()
    {
        return this.type;
    }


    // ** Variables
    // ------------------------------------------------------------------------------------------

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


    // ** Is Null
    // ------------------------------------------------------------------------------------------

    public boolean isNull()
    {
        return this.variable == null;
    }

}
