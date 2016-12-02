
package com.kispoko.tome.rules.programming.program;


import com.kispoko.tome.rules.programming.program.statement.Statement;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Program
 */
public class Program implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private PrimitiveValue<String>              name;

    private PrimitiveValue<ProgramValueType[]> parameterTypes;
    private PrimitiveValue<ProgramValueType>   resultType;

    private CollectionValue<Statement>          statements;
    private ModelValue<Statement>               resultStatement;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Program()
    {
         this.id             = null;

        // ** Name
        this.name            = new PrimitiveValue<>(null, String.class);

        // ** Parameter Types
        this.parameterTypes  = new PrimitiveValue<>(null, ProgramValueType[].class);

        // ** Result Type
        this.resultType      = new PrimitiveValue<>(null, ProgramValueType.class);

        // **  Statements
        List<Class<? extends Statement>> statementClasses = new ArrayList<>();
        statementClasses.add(Statement.class);
        this.statements      = CollectionValue.empty(statementClasses);

        // **  Result Statement
        this.resultStatement = ModelValue.empty(Statement.class);
    }


    public Program(UUID id,
                   String name,
                   List<ProgramValueType> parameterTypes,
                   ProgramValueType resultType,
                   List<Statement> statements,
                   Statement resultStatement)
    {
        this.id              = id;

        // ** Name
        this.name            = new PrimitiveValue<>(name, String.class);

        // ** Parameter Types
        ProgramValueType[] parameterTypeArray = parameterTypes.toArray(
                                                    new ProgramValueType[parameterTypes.size()]);
        this.parameterTypes  = new PrimitiveValue<>(parameterTypeArray, ProgramValueType[].class);

        // ** Result Type
        this.resultType      = new PrimitiveValue<>(resultType, ProgramValueType.class);

        // **  Statements
        List<Class<? extends Statement>> statementClasses = new ArrayList<>();
        statementClasses.add(Statement.class);
        this.statements      = CollectionValue.full(statements, statementClasses);

        // **  Result Statement
        this.resultStatement = ModelValue.full(resultStatement, Statement.class);
    }


    /**
     * Create a Program from its Yaml representation.
     * @param yaml The Yaml parser object.
     * @return The parsed Program object.
     * @throws YamlException
     */
    public static Program fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID     id             = UUID.randomUUID();

        // ** Name
        String   name           = yaml.atKey("name").getString();

        // ** Parameter Types
        List<ProgramValueType> parameterTypes
                = yaml.atKey("parameter_types").forEach(new Yaml.ForEach<ProgramValueType>() {
            @Override
            public ProgramValueType forEach(Yaml yaml, int index) throws YamlException {
                return ProgramValueType.fromYaml(yaml);
            }
        });

        // ** Result Type
        ProgramValueType resultType = ProgramValueType.fromYaml(yaml.atKey("result_type"));

        // ** Statements
        List<Statement>  statements =
                yaml.atKey("statements").forEach(new Yaml.ForEach<Statement>() {
            @Override
            public Statement forEach(Yaml yaml, int index) throws YamlException {
                return Statement.fromYaml(yaml);
            }
        });

        // ** Result Statement
        Statement resultStatement = Statement.fromYaml(yaml.atKey("result_statement"));

        return new Program(id, name, parameterTypes, resultType, statements, resultStatement);
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

    /**
     * This method is called when the Program is completely loaded for the first time.
     */
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Name
    // ------------------------------------------------------------------------------------------

    /**
     * Get the unique name of the program.
     * @return The program's name.
     */
    public String getName() {
        return this.name.getValue();
    }


    // ** Parameter Types
    // ------------------------------------------------------------------------------------------

    /**
     * Get the parameter types of the program.
     * @return The program's parameter type list.
     */
    public ProgramValueType[] getParameterTypes()
    {
        return this.parameterTypes.getValue();
    }


    /**
     * Set the parameter types of the program.
     * @param parameterTypes The types of the parameters that are passed to the program.
     */
    public void setParameterTypes(ProgramValueType[] parameterTypes)
    {
        this.parameterTypes.setValue(parameterTypes);
    }


    // ** Result Type
    // ------------------------------------------------------------------------------------------

    /**
     * Get the result type of the program.
     * @return The program's result type.
     */
    public ProgramValueType getResultType()
    {
        return this.resultType.getValue();
    }


    /**
     * Set the result type of the program.
     * @param resultType The type of the value that the program computes.
     */
    public void setResultType(ProgramValueType resultType)
    {
        this.resultType.setValue(resultType);
    }


    // ** Statements
    // ------------------------------------------------------------------------------------------

    /**
     * Get all of the statements in the program.
     * @return The program's statements.
     */
    public List<Statement> getStatements()
    {
        return this.statements.getValue();
    }


    /**
     * Set the program's statements.
     * @param statements The statements that the program will run.
     */
    public void setStatements(List<Statement> statements)
    {
        this.statements.setValue(statements);
    }


    // ** Result Statement
    // ------------------------------------------------------------------------------------------


    /**
     * Get the program's result statement. The result of the result statement is the result
     * of the program.
     * @return The result Statement.
     */
    public Statement getResultStatement()
    {
        return this.resultStatement.getValue();
    }


}
