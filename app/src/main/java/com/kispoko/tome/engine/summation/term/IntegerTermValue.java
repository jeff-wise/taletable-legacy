
package com.kispoko.tome.engine.summation.term;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.engine.variable.NullVariableException;
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
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.util.tuple.Tuple2;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;



/**
 * Term Value: Integer
 */
public class IntegerTermValue extends Model
                              implements Serializable
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

    private PrimitiveFunctor<String>        name;

    private PrimitiveFunctor<Kind>          kind;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public IntegerTermValue()
    {
        this.id                 = null;

        this.integerValue       = new PrimitiveFunctor<>(null, Integer.class);
        this.variableReference  = ModelFunctor.empty(VariableReference.class);

        this.name               = new PrimitiveFunctor<>(null, String.class);

        this.kind               = new PrimitiveFunctor<>(null, Kind.class);
    }


    private IntegerTermValue(UUID id, Object value, Kind kind, String name)
    {
        this.id                 = id;

        this.integerValue       = new PrimitiveFunctor<>(null, Integer.class);
        this.variableReference  = ModelFunctor.full(null, VariableReference.class);

        this.name               = new PrimitiveFunctor<>(name, String.class);

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


    /**
     * Create a "literal" integer term value.
     * @param id The model id.
     * @param value The integer value.
     * @param name The term name.
     * @return The Integer Term Value.
     */
    private static IntegerTermValue asLiteral(UUID id, Integer value, String name)
    {
        return new IntegerTermValue(id, value, Kind.LITERAL, name);
    }


    /**
     * Create a "variable" integer term value.
     * @param id The model id.
     * @param variableReference The variable reference.
     * @return The Integer Term Value.
     */
    private static IntegerTermValue asVariable(UUID id, VariableReference variableReference)
    {
        return new IntegerTermValue(id, variableReference, Kind.VARIABLE, null);
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
                String  name  = yaml.atMaybeKey("name").getString();
                return IntegerTermValue.asLiteral(id, value, name);
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

    // ** Kind
    // ------------------------------------------------------------------------------------------

    /**
     * The integer term value kind.
     * @return The kind.
     */
    private Kind kind()
    {
        return this.kind.getValue();
    }


    // ** Integer Value
    // ------------------------------------------------------------------------------------------

    /**
     * The "literal" case.
     * @return
     */
    public Integer literal()
    {
        return this.integerValue.getValue();
    }


    // ** Variable Reference
    // ------------------------------------------------------------------------------------------

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


    // ** Name
    // ------------------------------------------------------------------------------------------

    /**
     * The term value name (if a literal value). Variable's should have their own name.
     * @return The literal value name.
     */
    public String name()
    {
        switch (this.kind())
        {
            case LITERAL:
                return this.name.getValue();
            case VARIABLE:
                return this.variableReference().variable().variable().label();
        }

        return "";
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


    // > Components
    // ------------------------------------------------------------------------------------------

    public List<Tuple2<String,Integer>> components()
    {
        switch (this.kind())
        {
            case LITERAL:
                List<Tuple2<String,Integer>> components = new ArrayList<>();
                String name = this.name() != null ? this.name() : "";
                components.add(new Tuple2<>(name, this.literal()));
                return components;
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


            Integer variableValue = 0;
            try {
                variableValue = variableUnion.numberVariable().value();
            }
            catch (NullVariableException exception) {
                ApplicationFailure.nullVariable(exception);
            }

            total += variableValue;
        }

        return total;
    }


    private List<Tuple2<String,Integer>> variableSummaries()
    {
        List<Tuple2<String,Integer>> summaries = new ArrayList<>();

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

            try {
                Integer value = variable.value();
                summaries.add(new Tuple2<>(variable.label(), value));
            }
            catch (NullVariableException exception) {
                ApplicationFailure.nullVariable(exception);
            }
        }

        // > Sort the summaries
        Collections.sort(summaries, new Comparator<Tuple2<String,Integer>>()
        {
            @Override
            public int compare(Tuple2<String,Integer> summary1, Tuple2<String,Integer> summary2)
            {
                int summary1Value = summary1.getItem2();
                int summary2Value = summary2.getItem2();

                if (summary1Value > summary2Value)
                    return -1;
                if (summary2Value < summary1Value)
                    return 1;
                return 0;
            }
        });


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
                        new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
            }
        }

    }


}
