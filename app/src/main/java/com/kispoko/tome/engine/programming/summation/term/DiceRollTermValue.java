
package com.kispoko.tome.engine.programming.summation.term;


import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.programming.summation.SummationException;
import com.kispoko.tome.engine.programming.summation.error.UndefinedVariableError;
import com.kispoko.tome.engine.programming.summation.error.VariableNotNumberError;
import com.kispoko.tome.engine.programming.variable.VariableType;
import com.kispoko.tome.engine.programming.variable.VariableUnion;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.mechanic.dice.DiceRoll;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import java.io.Serializable;
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

    private UUID                   id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelValue<DiceRoll>   diceRoll;
    private PrimitiveValue<String> variableName;

    private PrimitiveValue<Kind>   kind;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public DiceRollTermValue()
    {
        this.id           = null;

        this.diceRoll     = ModelValue.empty(DiceRoll.class);
        this.variableName = new PrimitiveValue<>(null, String.class);

        this.kind         = new PrimitiveValue<>(null, Kind.class);
    }


    private DiceRollTermValue(UUID id, Object value, Kind kind)
    {
        this.id           = id;

        this.diceRoll     = ModelValue.full(null, DiceRoll.class);
        this.variableName = new PrimitiveValue<>(null, String.class);

        this.kind         = new PrimitiveValue<>(kind, Kind.class);

        switch (kind)
        {
            case LITERAL:
                this.diceRoll.setValue((DiceRoll) value);
                break;
            case VARIABLE:
                this.variableName.setValue((String) value);
                break;
        }
    }


    /**
     * Create the "dice roll" case.
     * @param id The Model id.
     * @param diceRoll The dice roll.
     * @return The "dice roll" variant.
     */
    public static DiceRollTermValue asDiceRoll(UUID id, DiceRoll diceRoll)
    {
        return new DiceRollTermValue(id, diceRoll, Kind.LITERAL);
    }


    /**
     * Create the "variable" case.
     * @param id The Model id.
     * @param variableName The variable name.
     * @return The "variable" variant.
     */
    public static DiceRollTermValue asVariable(UUID id, String variableName)
    {
        return new DiceRollTermValue(id, variableName, Kind.VARIABLE);
    }


    /**
     * Create a Dice Roll Term Value from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The Dice Roll Term Value.
     * @throws YamlException
     */
    public static DiceRollTermValue fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID id   = UUID.randomUUID();

        Kind kind = Kind.fromYaml(yaml.atKey("type"));

        switch (kind)
        {
            case LITERAL:
                DiceRoll diceRoll = DiceRoll.fromYaml(yaml.atKey("value"));
                return DiceRollTermValue.asDiceRoll(id, diceRoll);
            case VARIABLE:
                String variableName = yaml.atKey("variable").getString();
                return DiceRollTermValue.asVariable(id, variableName);
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

    /**
     * Get the dice roll.
     * @return The Dice Roll.
     */
    public DiceRoll diceRoll()
    {
        return this.diceRoll.getValue();
    }


    // > Value
    // ------------------------------------------------------------------------------------------

    /**
     * Get the value of the dice roll term. It is either a literal dice roll, or the value of an
     * dice roll variable.
     * @return The Dice Roll value.
     * @throws SummationException
     */
    public Integer value()
           throws SummationException
    {
        switch (this.kind.getValue())
        {
            case LITERAL:
                return this.diceRoll().roll();
            case VARIABLE:
                return this.variableValue(this.variableName.getValue());
        }

        return null;
    }


    /**
     * Get the name of the integer variable of the term. If the term is not a variable, then
     * null is returned.
     * @return The variable name, or null.
     */
    public String variableName()
    {
        switch (this.kind.getValue())
        {
            case LITERAL:
                return null;
            case VARIABLE:
                return this.variableName.getValue();
        }

        return null;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private Integer variableValue(String variableName)
            throws SummationException
    {
        // > If variable does not exist, throw exception
        if (!State.hasVariable(variableName)) {
            throw SummationException.undefinedVariable(
                    new UndefinedVariableError(variableName));
        }

        // [1] Get the variable
        VariableUnion variableUnion = State.variableWithName(variableName);

        // > If variable is not a dice roll, throw exception
        if (!variableUnion.type().equals(VariableType.DICE)) {
            throw SummationException.variableNotNumber(
                    new VariableNotNumberError(variableName));
        }

        Integer variableValue = variableUnion.diceVariable().rollValue();

        return variableValue;
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
