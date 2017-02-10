
package com.kispoko.tome.engine.summation.term;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.engine.variable.DiceVariable;
import com.kispoko.tome.engine.variable.VariableException;
import com.kispoko.tome.engine.variable.VariableReference;
import com.kispoko.tome.engine.variable.VariableType;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.engine.variable.error.UnexpectedVariableTypeError;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.mechanic.dice.DiceRoll;
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
 * Dice Roll Term Value
 */
public class DiceRollTermValue implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<DiceRoll>          diceRoll;
    private ModelFunctor<VariableReference> variableReference;

    private PrimitiveFunctor<String>        name;

    private PrimitiveFunctor<Kind>          kind;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public DiceRollTermValue()
    {
        this.id                 = null;

        this.diceRoll           = ModelFunctor.empty(DiceRoll.class);
        this.variableReference  = ModelFunctor.empty(VariableReference.class);

        this.name               = new PrimitiveFunctor<>(null, String.class);

        this.kind               = new PrimitiveFunctor<>(null, Kind.class);
    }


    private DiceRollTermValue(UUID id, Object value, Kind kind, String name)
    {
        this.id                 = id;

        this.diceRoll           = ModelFunctor.full(null, DiceRoll.class);
        this.variableReference  = ModelFunctor.full(null, VariableReference.class);

        this.name               = new PrimitiveFunctor<>(name, String.class);

        this.kind               = new PrimitiveFunctor<>(kind, Kind.class);

        switch (kind)
        {
            case LITERAL:
                this.diceRoll.setValue((DiceRoll) value);
                break;
            case VARIABLE:
                this.variableReference.setValue((VariableReference) value);
                break;
        }
    }


    /**
     * Create the "dice roll" case.
     * @param id The Model id.
     * @param diceRoll The dice roll.
     * @param name The term value name.
     * @return The "dice roll" variant.
     */
    public static DiceRollTermValue asDiceRoll(UUID id, DiceRoll diceRoll, String name)
    {
        return new DiceRollTermValue(id, diceRoll, Kind.LITERAL, name);
    }


    /**
     * Create the "variable" case.
     * @param id The Model id.
     * @param variableReference The variable reference.
     * @return The "variable" variant.
     */
    public static DiceRollTermValue asVariable(UUID id, VariableReference variableReference)
    {
        return new DiceRollTermValue(id, variableReference, Kind.VARIABLE, null);
    }


    /**
     * Create a Dice Roll Term Value from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The Dice Roll Term Value.
     * @throws YamlParseException
     */
    public static DiceRollTermValue fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID id   = UUID.randomUUID();

        Kind kind = Kind.fromYaml(yaml.atKey("type"));

        switch (kind)
        {
            case LITERAL:
                DiceRoll diceRoll = DiceRoll.fromYaml(yaml.atKey("value"));
                String   name     = yaml.atMaybeKey("name").getString();
                return DiceRollTermValue.asDiceRoll(id, diceRoll, name);
            case VARIABLE:
                VariableReference variableReference =
                        VariableReference.fromYaml(yaml.atKey("variable"));
                return DiceRollTermValue.asVariable(id, variableReference);
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
     * The term value kind.
     * @return The term value kind.
     */
    public Kind kind()
    {
        return this.kind.getValue();
    }


    // ** CASE: Literal
    // ------------------------------------------------------------------------------------------

    /**
     * The "literal" case.
     * @return The dice roll.
     */
    public DiceRoll literal()
    {
        return this.diceRoll.getValue();
    }


    // ** CASE: Variable
    // ------------------------------------------------------------------------------------------

    /**
     * The variable case.
     * @return The variable reference.
     */
    public VariableReference variable()
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
     * The term value name. Only applicable for literal values.
     * @return The name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    // > Components
    // ------------------------------------------------------------------------------------------

    public List<Tuple2<String,String>> components()
           throws VariableException
    {
        switch (this.kind())
        {
            case LITERAL:
                List<Tuple2<String,String>> components = new ArrayList<>();
                String name = this.name() != null ? this.name() : "";
                components.add(new Tuple2<>(name, this.literal().toString()));
                return components;
            case VARIABLE:
                return this.variableSummaries();
        }

        return new ArrayList<>();
    }


    // > Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the value of the dice roll term. It is either a literal dice roll, or the value of an
     * dice roll variable.
     * @return The Dice Roll value.
     * @throws VariableException
     */
    public Integer value()
           throws VariableException
    {
        switch (this.kind())
        {
            case LITERAL:
                return this.diceRoll().roll();
            case VARIABLE:
                DiceRoll roll = this.variableValue();
                if (roll == null)
                    return null;
                return roll.roll();
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(DiceRollTermValue.Kind.class.getName())));
        }

        return null;
    }


    /**
     * Get the dice roll.
     * @return The Dice Roll.
     */
    public DiceRoll diceRoll()
           throws VariableException
    {
        switch (this.kind())
        {
            case LITERAL:
                return this.literal();
            case VARIABLE:
                return this.variableValue();
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(DiceRollTermValue.Kind.class.getName())));
        }

        return null;
    }



    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private DiceRoll variableValue()
            throws VariableException
    {
        VariableUnion variableUnion = this.variable().variable();

        if (variableUnion == null)
            return null;

        // > If variable is not a dice roll, throw exception
        if (!variableUnion.type().equals(VariableType.DICE)) {
            throw VariableException.unexpectedVariableType(
                    new UnexpectedVariableTypeError(this.variable().name(),
                                                    VariableType.DICE,
                                                    variableUnion.type()));
        }

        DiceRoll variableValue = variableUnion.diceVariable().diceRoll();

        return variableValue;
    }



    private List<Tuple2<String,String>> variableSummaries()
    {
        List<Tuple2<String,String>> summaries = new ArrayList<>();

        for (VariableUnion variableUnion : this.variable().variables())
        {
            // [1] If variable is not a number, throw exception
            // ----------------------------------------------------------------------------------
            if (variableUnion.type() != VariableType.DICE) {
                ApplicationFailure.variable(
                        VariableException.unexpectedVariableType(
                                new UnexpectedVariableTypeError(variableUnion.variable().name(),
                                        VariableType.DICE,
                                        variableUnion.type())));
                continue;
            }

            DiceVariable variable = variableUnion.diceVariable();

            summaries.add(new Tuple2<>(variable.label(), variable.diceRoll().toString()));
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
