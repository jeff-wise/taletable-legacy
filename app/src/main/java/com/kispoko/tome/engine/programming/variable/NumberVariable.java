
package com.kispoko.tome.engine.programming.variable;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.engine.programming.program.invocation.Invocation;
import com.kispoko.tome.engine.programming.summation.Summation;
import com.kispoko.tome.engine.programming.summation.SummationException;
import com.kispoko.tome.engine.refinement.RefinementId;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



/**
 * Number Variable
 */
public class NumberVariable extends Variable implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>    name;

    private PrimitiveFunctor<Integer>   integerValue;
    private ModelFunctor<Invocation>    invocationValue;
    private ModelFunctor<Summation>     summation;

    private PrimitiveFunctor<Kind>      kind;
    private ModelFunctor<RefinementId>  refinementId;
    private PrimitiveFunctor<String[]>  tags;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private ReactiveValue<Integer>      reactiveValue;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberVariable()
    {
        super();

        this.id              = null;

        this.name            = new PrimitiveFunctor<>(null, String.class);

        this.integerValue    = new PrimitiveFunctor<>(null, Integer.class);
        this.invocationValue = ModelFunctor.empty(Invocation.class);
        this.summation       = ModelFunctor.empty(Summation.class);

        this.kind            = new PrimitiveFunctor<>(null, Kind.class);
        this.refinementId    = ModelFunctor.empty(RefinementId.class);
        this.tags            = new PrimitiveFunctor<>(null, String[].class);

        this.reactiveValue   = null;
    }


    /**
     * Create a Variable. This constructor is private to enforce use of the case specific
     * constructors, so only valid value/type associations can be used.
     * @param id The Model id.
     * @param value The Variable value.
     * @param kind The Number Variable kind.
     */
    private NumberVariable(UUID id,
                           String name,
                           Object value,
                           Kind kind,
                           RefinementId refinementId,
                           List<String> tags)
    {
        super();

        this.id                     = id;

        this.name                   = new PrimitiveFunctor<>(name, String.class);

        this.integerValue           = new PrimitiveFunctor<>(null, Integer.class);
        this.invocationValue = ModelFunctor.full(null, Invocation.class);
        this.summation              = ModelFunctor.full(null, Summation.class);

        this.kind                   = new PrimitiveFunctor<>(kind, Kind.class);
        this.refinementId           = ModelFunctor.full(refinementId, RefinementId.class);

        String[] tagsArray = new String[tags.size()];
        tags.toArray(tagsArray);
        this.tags                   = new PrimitiveFunctor<>(tagsArray, String[].class);

        // Set value according to variable type
        switch (kind)
        {
            case LITERAL:
                this.integerValue.setValue((Integer) value);
                break;
            case PROGRAM:
                this.invocationValue.setValue((Invocation) value);
                break;
            case SUMMATION:
                this.summation.setValue((Summation) value);
                break;
        }

        initialize();
    }


    /**
     * Create a "literal" number variable that contains a value of type Integer.
     * @param id The Model id.
     * @param integerValue The Integer value.
     * @return A new "literal" Integer Variable.
     */
    public static NumberVariable asInteger(UUID id,
                                           String name,
                                           Integer integerValue,
                                           RefinementId refinementId,
                                           List<String> tags)
    {
        return new NumberVariable(id, name, integerValue, Kind.LITERAL, refinementId, tags);
    }


    /**
     * Create a "program" valued variable.
     * @param id The Model id.
     * @param invocation The Invocation value.
     * @return A new "program" variable.
     */
    public static NumberVariable asProgram(UUID id,
                                           String name,
                                           Invocation invocation,
                                           RefinementId refinementId,
                                           List<String> tags)
    {
        return new NumberVariable(id, name, invocation, Kind.PROGRAM, refinementId, tags);
    }


    /**
     * Create the "summation" case.
     * @param id The Model id.
     * @param summation The summation.
     * @return The new number variable as a summation.
     */
    public static NumberVariable asSummation(UUID id,
                                             String name,
                                             Summation summation,
                                             RefinementId refinementId,
                                             List<String> tags)
    {
        return new NumberVariable(id, name, summation, Kind.SUMMATION, refinementId, tags);
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
        if (yaml.isNull())
            return null;

        UUID         id           = UUID.randomUUID();
        String       name         = yaml.atMaybeKey("name").getString();
        Kind         kind         = Kind.fromYaml(yaml.atKey("type"));
        RefinementId refinementId = RefinementId.fromYaml(yaml.atMaybeKey("refinement"));
        List<String> tags         = yaml.atMaybeKey("tags").getStringList();

        switch (kind)
        {
            case LITERAL:
                Integer integerValue  = yaml.atKey("value").getInteger();
                return NumberVariable.asInteger(id, name, integerValue, refinementId, tags);
            case PROGRAM:
                Invocation invocation = Invocation.fromYaml(yaml.atKey("value"));
                return NumberVariable.asProgram(id, name, invocation, refinementId, tags);
            case SUMMATION:
                Summation summation = Summation.fromYaml(yaml.atKey("value"));
                return NumberVariable.asSummation(id, name, summation, refinementId, tags);
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
        initialize();
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The invocation case.
     * @return The invocation.
     */
    public Invocation invocation()
    {
        if (this.kind() != Kind.PROGRAM) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("program", this.kind.name())));
        }

        return this.invocationValue.getValue();
    }

    /**
     * The summation case.
     * @return The summation.
     */
    public Summation summation()
    {
        if (this.kind() != Kind.SUMMATION) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("summation", this.kind.name())));
        }

        return this.summation.getValue();
    }



    // > Variable
    // ------------------------------------------------------------------------------------------

    /**
     * Get the variable name which is a unique identifier.
     * @return The variable name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    public List<VariableReference> dependencies()
    {
        List<VariableReference> variableDependencies = new ArrayList<>();

        switch (this.kind.getValue())
        {
            case LITERAL:
                break;
            case PROGRAM:
                variableDependencies = this.invocation().variableDependencies();
                break;
            case SUMMATION:
                variableDependencies = this.summation().variableDependencies();
                break;
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(Kind.class.getName())));
        }

        return variableDependencies;
    }


    /**
     * The variable's tags.
     * @return The tag list.
     */
    public List<String> tags()
    {
        return Arrays.asList(this.tags.getValue());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Kind
    // ------------------------------------------------------------------------------------------

    /**
     * Get the kind of number variable.
     * @return The number variable kind.
     */
    private Kind kind()
    {
        return this.kind.getValue();
    }



    // ** Value
    // ------------------------------------------------------------------------------------------

    /**
     * Set the number variable integer. value
     * @param newValue The integer value.
     */
    public void setValue(Integer newValue)
    {
        switch (this.kind.getValue())
        {
            case LITERAL:
                this.integerValue.setValue(newValue);
                this.onUpdate();
                break;
            case PROGRAM:
                // Do Nothing?
                //this.reactiveValue.setValue(newValue);
                break;
            case SUMMATION:
                // Do Nothing?
                break;
        }
    }


    /**
     * Get the number variable's integer value.
     * @return The integer value.
     */
    public Integer value()
           throws VariableException
    {
        switch (this.kind.getValue())
        {
            case LITERAL:
                return this.integerValue.getValue();
            case PROGRAM:
                return this.reactiveValue.value();
            case SUMMATION:
                return this.summation.getValue().value();
        }

        return null;
    }


    /**
     * Get the value string representation. If the value contains any dice rolls, then it appears
     * as a formula, otherwise it is just an integer string.
     * @return The value string.
     * @throws SummationException
     */
    public String valueString()
           throws VariableException
    {
        switch (this.kind())
        {
            case LITERAL:
                return this.value().toString();
            case PROGRAM:
                return this.value().toString();
            case SUMMATION:
                return this.summation.getValue().valueString();
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(Kind.class.getName())));
        }

        return "";
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


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private void initialize()
    {
        // [1] Create reaction value (if program variable)
        // --------------------------------------------------------------------------------------

        if (this.kind.getValue() == Kind.PROGRAM) {
            this.reactiveValue = new ReactiveValue<>(this.invocationValue.getValue(),
                                                     VariableType.NUMBER);
        }
        else {
            this.reactiveValue = null;
        }
    }


    // KIND
    // ------------------------------------------------------------------------------------------

    public enum Kind
    {
        LITERAL,
        PROGRAM,
        SUMMATION;


        public static Kind fromString(String kindString)
                      throws InvalidDataException
        {
            return EnumUtils.fromString(Kind.class, kindString);
        }


        public static Kind fromYaml(Yaml yaml)
                      throws YamlException
        {
            String kindString = yaml.getString();
            try {
                return Kind.fromString(kindString);
            } catch (InvalidDataException e) {
                throw YamlException.invalidEnum(new InvalidEnumError(kindString));
            }
        }


        public static Kind fromSQLValue(SQLValue sqlValue)
                      throws DatabaseException
        {
            String enumString = "";
            try {
                enumString = sqlValue.getText();
                Kind kind = Kind.fromString(enumString);
                return kind;
            } catch (InvalidDataException e) {
                throw DatabaseException.invalidEnum(
                        new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
            }
        }

    }



}
