
package com.kispoko.tome.engine.program;


import com.kispoko.tome.engine.program.statement.Statement;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



/**
 * Program
 */
public class Program implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                    id;


    // > Functor
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>                name;
    private PrimitiveFunctor<String>                label;
    private PrimitiveFunctor<String>                description;

    private PrimitiveFunctor<ProgramValueType[]>    parameterTypes;
    private PrimitiveFunctor<ProgramValueType>      resultType;

    private CollectionFunctor<Statement>            statements;
    private ModelFunctor<Statement>                 resultStatement;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Program()
    {
        this.id             = null;

        // ** Name
        this.name            = new PrimitiveFunctor<>(null, String.class);

        // ** Label
        this.label           = new PrimitiveFunctor<>(null, String.class);

        // ** Description
        this.description     = new PrimitiveFunctor<>(null, String.class);

        // ** Parameter Types
        this.parameterTypes  = new PrimitiveFunctor<>(null, ProgramValueType[].class);

        // ** Result ErrorType
        this.resultType      = new PrimitiveFunctor<>(null, ProgramValueType.class);

        // **  Statements
        List<Class<? extends Statement>> statementClasses = new ArrayList<>();
        statementClasses.add(Statement.class);
        this.statements      = CollectionFunctor.empty(statementClasses);

        // **  Result Statement
        this.resultStatement = ModelFunctor.empty(Statement.class);
    }


    public Program(UUID id,
                   String name,
                   String label,
                   String description,
                   List<ProgramValueType> parameterTypes,
                   ProgramValueType resultType,
                   List<Statement> statements,
                   Statement resultStatement)
    {
        this.id              = id;

        // ** Name
        this.name            = new PrimitiveFunctor<>(name, String.class);

        // ** Label
        this.label           = new PrimitiveFunctor<>(label, String.class);

        // ** Description
        this.description     = new PrimitiveFunctor<>(description, String.class);

        // ** Parameter Types
        ProgramValueType[] parameterTypeArray = parameterTypes.toArray(
                                                    new ProgramValueType[parameterTypes.size()]);
        this.parameterTypes  = new PrimitiveFunctor<>(parameterTypeArray, ProgramValueType[].class);

        // ** Result ErrorType
        this.resultType      = new PrimitiveFunctor<>(resultType, ProgramValueType.class);

        // **  Statements
        List<Class<? extends Statement>> statementClasses = new ArrayList<>();
        statementClasses.add(Statement.class);
        this.statements      = CollectionFunctor.full(statements, statementClasses);

        // **  Result Statement
        this.resultStatement = ModelFunctor.full(resultStatement, Statement.class);
    }


    /**
     * Create a Program from its Yaml representation.
     * @param yaml The Yaml parser object.
     * @return The parsed Program object.
     * @throws YamlParseException
     */
    public static Program fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID     id             = UUID.randomUUID();

        // ** Name
        String   name           = yaml.atKey("name").getString().trim();

        // ** Label
        String   label          = yaml.atMaybeKey("label").getString();
        if (label != null)  label = label.trim();

        // ** Description
        String   description    = yaml.atMaybeKey("description").getString();
        if (description != null)  description = description.trim();

        // ** Parameter Types
        List<ProgramValueType> parameterTypes
                = yaml.atKey("parameter_types").forEach(new YamlParser.ForEach<ProgramValueType>() {
            @Override
            public ProgramValueType forEach(YamlParser yaml, int index) throws YamlParseException {
                return ProgramValueType.fromYaml(yaml);
            }
        });

        // ** Result ErrorType
        ProgramValueType resultType = ProgramValueType.fromYaml(yaml.atKey("result_type"));

        // ** Statements
        List<Statement>  statements =
                yaml.atMaybeKey("statements").forEach(new YamlParser.ForEach<Statement>() {
            @Override
            public Statement forEach(YamlParser yaml, int index) throws YamlParseException {
                return Statement.fromYaml(yaml);
            }
        }, true);

        // ** Result Statement
        Statement resultStatement = Statement.fromYaml(yaml.atKey("result_statement"));

        return new Program(id, name, label, description, parameterTypes, resultType,
                           statements, resultStatement);
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


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Program's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putList("parameter_types", this.parameterTypes())
                .putYaml("result_type", this.resultType())
                .putList("statements", this.statements())
                .putYaml("result_statement", this.resultStatement());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Name
    // ------------------------------------------------------------------------------------------

    /**
     * Get the unique name of the program.
     * @return The program's name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    // ** Label
    // ------------------------------------------------------------------------------------------

    /**
     * The Program's label (human-friendly name).
     * @return The label.
     */
    public String label()
    {
        return this.label.getValue();
    }


    // ** Description
    // ------------------------------------------------------------------------------------------

    /**
     * A description of what the program is/does.
     * @return The description.
     */
    public String description()
    {
        return this.description.getValue();
    }


    // ** Parameter Types
    // ------------------------------------------------------------------------------------------

    /**
     * Get the parameter types of the program.
     * @return The program's parameter type list.
     */
    public List<ProgramValueType> parameterTypes()
    {
        return Arrays.asList(this.parameterTypes.getValue());
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
    public ProgramValueType resultType()
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
    public List<Statement> statements()
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
    public Statement resultStatement()
    {
        return this.resultStatement.getValue();
    }


    // > Arity
    // ------------------------------------------------------------------------------------------

    /**
     * The number of parameters the program accepts.
     * @return The program arity.
     */
    public int arity()
    {
        return this.parameterTypes().size();
    }


}
