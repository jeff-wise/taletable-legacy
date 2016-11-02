
package com.kispoko.tome.rules;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Program
 */
public class Program
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String name;
    private List<FunctionValueType> parameterTypes;
    private FunctionValueType resultType;
    private String resultVariableName;
    private List<Statement> statements;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Program(String name, List<FunctionValueType> parameterTypes,
                   FunctionValueType resultType, String resultVariableName,
                   List<Statement> statements)
    {
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.resultType = resultType;
        this.resultVariableName = resultVariableName;
        this.statements = statements;
    }


    @SuppressWarnings("unchecked")
    public static Program fromYaml(Map<String,Object> programYaml)
    {
        // VALUES TO PARSE
        // --------------------------------------------------------------------------------------
        String name = null;
        List<FunctionValueType> parameterTypes = new ArrayList<>();
        FunctionValueType resultType = null;
        String resultVariable = null;
        List<Statement> statements = new ArrayList<>();

        // PARSE VALUES
        // --------------------------------------------------------------------------------------

        // > Top Level
        // --------------------------------------------------------------------------------------

        // ** Name
        if (programYaml.containsKey("name"))
            name = (String) programYaml.get("name");

        // >> Definition
        // --------------------------------------------------------------------------------------
        Map<String,Object> definitionYaml = (Map<String,Object>) programYaml.get("definition");

        if (definitionYaml != null)
        {
            // ** Parameter Types
            if (definitionYaml.containsKey("parameter_types")) {
                List<String> parameterTypeStrings =
                        (List<String>) definitionYaml.get("parameter_types");
                for (String parameterTypeString : parameterTypeStrings) {
                    parameterTypes.add(FunctionValueType.fromString(parameterTypeString));
                }
            }

            // ** Result Type
            if (definitionYaml.containsKey("result_type")) {
                resultType = FunctionValueType.fromString(
                                (String) definitionYaml.get("result_type"));
            }

            // ** Result Variable
            if (definitionYaml.containsKey("result_variable"))
                resultVariable = (String) definitionYaml.get("result_variable");

            // ** Statements
            if (definitionYaml.containsKey("statements")) {
                List<Map<String,Object>> statementsYaml =
                        (List<Map<String,Object>>) definitionYaml.get("statements");
                for (Map<String,Object> statementYaml : statementsYaml) {
                    statements.add(Statement.fromYaml(statementYaml));
                }
            }
        }

        return new Program(name, parameterTypes, resultType, resultVariable, statements);
    }


    // API
    // ------------------------------------------------------------------------------------------


    public List<Statement> getStatements() {
        return this.statements;
    }


    public String getResultVariableName() {
        return this.resultVariableName;
    }


    // NESTED CLASSES
    // ------------------------------------------------------------------------------------------

}
