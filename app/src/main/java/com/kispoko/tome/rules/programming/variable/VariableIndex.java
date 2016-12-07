
package com.kispoko.tome.rules.programming.variable;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Variable Index
 */
public class VariableIndex implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Map<String,VariableUnion> variableByName;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public VariableIndex()
    {
        this.variableByName = new HashMap<>();
    }


    /**
     * Parse the free variables, which add themselves to the index along with the variables from
     * the other application components.
     * @param yaml The yaml parser
     * @throws YamlException
     */
    public static void fromYaml(Yaml yaml)
                  throws YamlException
    {
        yaml.forEach(new Yaml.ForEach<Void>() {
            @Override
            public Void forEach(Yaml yaml, int index) throws YamlException {
                VariableUnion.fromYaml(yaml);
                return null;
            }
        }, true);
    }


    // API
    // ------------------------------------------------------------------------------------------


    /**
     * Add a variable to the index. If a variable with the same name already exists in the index,
     * then the new variable replaces the old variable.
     * @param variable The variable to add.
     */
    public void addVariable(Variable variable)
    {
        VariableUnion variableUnion = null;

        if (variable instanceof TextVariable) {
            variableUnion = VariableUnion.asText((TextVariable) variable);
        }
        else if (variable instanceof NumberVariable) {
            variableUnion = VariableUnion.asNumber((NumberVariable) variable);
        }
        else if (variable instanceof BooleanVariable) {
            variableUnion = VariableUnion.asBoolean((BooleanVariable) variable);
        }
        else {
            ApplicationFailure.union(
                UnionException.unknownVariant(
                    new UnknownVariantError(VariableUnion.class.getName())));
        }

        this.variableByName.put(variableUnion.getName(), variableUnion);
    }


    public VariableUnion variableWithName(String name)
    {
        return this.variableByName.get(name);
    }


    public boolean hasVariable(String variableName)
    {
        return this.variableByName.containsKey(variableName);
    }

}
