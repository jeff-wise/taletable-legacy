
package com.kispoko.tome.engine.programming.summation.term;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.engine.variable.VariableException;
import com.kispoko.tome.engine.variable.VariableReference;
import com.kispoko.tome.engine.variable.error.UnexpectedVariableTypeError;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.engine.variable.VariableType;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.tuple.Tuple2;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Term Value: Integer
 */
public class IntegerTermValue implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<Integer>       integerValue;
    private ModelFunctor<VariableReference> variableReference;

    private PrimitiveFunctor<Kind>          kind;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public IntegerTermValue()
    {
        this.id                 = null;

        this.integerValue       = new PrimitiveFunctor<>(null, Integer.class);
        this.variableReference  = ModelFunctor.empty(VariableReference.class);

        this.kind               = new PrimitiveFunctor<>(null, Kind.class);
    }


    private IntegerTermValue(UUID id, Object value, Kind kind)
    {
        this.id                 = id;

        this.integerValue       = new PrimitiveFunctor<>(null, Integer.class);
        this.variableReference  = ModelFunctor.full(null, VariableReference.class);

        this.kind               = new PrimitiveFunctor<>(kind, Kind.class);

        // > Set the value depending on the case
        switch (kind)
        {
            case LITERAL:
                this.integerValue.setValue((Integer) value);
                break;
            case VARIABLE:
                this.variableReference.setValue((VariableReference) value);
                break;
        }
    }


    private static IntegerTermValue asLiteral(UUID id, Integer value)
    {
        return new IntegerTermValue(id, value, Kind.LITERAL);
    }


    private static IntegerTermValue asVariable(UUID id, VariableReference variableReference)
    {
        return new IntegerTermValue(id, variableReference, Kind.VARIABLE);
    }


    /**
     * Create an Integer Term Value from its Yaml representation.
     * @param yaml The yaml parser.
     * @return A new Integer Term Value.
     * @throws YamlParseException
     */
    public static IntegerTermValue fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID id   = UUID.randomUUID();

        Kind kind = Kind.fromYaml(yaml.atKey("type"));

        switch (kind)
        {
            case LITERAL:
                Integer value = yaml.atKey("value").getInteger();
                return IntegerTermValue.asLiteral(id, value);
            case VARIABLE:
                VariableReference varRef = VariableReference.fromYaml(yaml.atKey("variable"));
                return IntegerTermValue.asVariable(id, varRef);
        }

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

    /**
     * This method is called when the Column Union is completely loaded for the first time.
     */
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Variable Reference
    // ------------------------------------------------------------------------------------------

    /**
     * The integer term value kind.
     * @return The kind.
     */
    private Kind kind()
    {
        return this.kind.getValue();
    }


    /**
     * The variable case.
     * @return The variable reference.
     */
    public VariableReference variableReference()
    {
        if (this.kind() != Kind.VARIABLE) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("variable", this.kind.toString())));
        }

        return this.variableReference.getValue();
    }


    // > Value
    // ------------------------------------------------------------------------------------------


    /**
     * Get the value of the integer term. It is either a literal integer, or the value of an
     * integer variable.
     * @return The integer value.
     * @throws VariableException
     */
    public Integer value()
           throws VariableException
    {
        switch (this.kind.getValue())
        {
            case LITERAL:
                return this.integerValue.getValue();
            case VARIABLE:
                return this.variableValue(this.variableReference());
        }

        return null;
    }


    /**
     * Get the name of the integer variable of the term. If the term is not a variable, then
     * null is returned.
     * @return The variable name, or null.
     */
    public VariableReference variableDependency()
    {
        switch (this.kind.getValue())
        {
            case LITERAL:
                return null;
            case VARIABLE:
                return this.variableReference();
        }

        return null;
    }


    // > Label
    // ------------------------------------------------------------------------------------------

    public List<Tuple2<Integer,String>> summary()
    {
        switch (this.kind())
        {
            case LITERAL:
                return new ArrayList<>();
            case VARIABLE:
                return this.variableSummaries();
        }

        return new ArrayList<>();
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private Integer variableValue(VariableReference variableReference)
            throws VariableException
    {
        Integer total = 0;

        for (VariableUnion variableUnion : variableReference.variables())
        {
            // [1] If variable is not a number, throw exception
            // ----------------------------------------------------------------------------------
            if (variableUnion.type() != VariableType.NUMBER) {
                throw VariableException.unexpectedVariableType(
                        new UnexpectedVariableTypeError(variableUnion.variable().name(),
                                                        VariableType.NUMBER,
                                                        variableUnion.type()));
            }

            // [2] Add the variable's value to the sum.
            // ----------------------------------------------------------------------------------

            Integer variableValue = variableUnion.numberVariable().value();
            total += variableValue;
        }

        return total;
    }


    private List<Tuple2<Integer,String>> variableSummaries()
    {
        List<Tuple2<Integer,String>> summaries = new ArrayList<>();

        for (VariableUnion variableUnion : this.variableReference().variables())
        {
            // [1] If variable is not a number, throw exception
            // ----------------------------------------------------------------------------------
            if (variableUnion.type() != VariableType.NUMBER) {
                ApplicationFailure.variable(
                        VariableException.unexpectedVariableType(
                                new UnexpectedVariableTypeError(variableUnion.variable().name(),
                                        VariableType.NUMBER,
                                        variableUnion.type())));
                continue;
            }

            NumberVariable variable = variableUnion.numberVariable();

            Integer value = 0;
            try {
                value = variable.value();
            }
            catch (VariableException exception) {
                ApplicationFailure.variable(exception);
            }

            summaries.add(new Tuple2<>(value, variable.label()));
        }

        return summaries;
    }


    // KIND
    // ------------------------------------------------------------------------------------------

    public enum Kind
    {

        LITERAL,
        VARIABLE;


        public static Kind fromString(String kindString)
                      throws InvalidDataException
        {
            return EnumUtils.fromString(Kind.class, kindString);
        }


        public static Kind fromYaml(YamlParser yaml)
                      throws YamlParseException
        {
            String kindString = yaml.getString();
            try {
                return Kind.fromString(kindString);
            } catch (InvalidDataException e) {
                throw YamlParseException.invalidEnum(new InvalidEnumError(kindString));
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
