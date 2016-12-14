
package com.kispoko.tome.engine.programming.summation.term;


import com.kispoko.tome.engine.State;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.engine.programming.summation.SummationException;
import com.kispoko.tome.engine.programming.summation.error.UndefinedVariableError;
import com.kispoko.tome.engine.programming.summation.error.VariableNotNumberError;
import com.kispoko.tome.engine.programming.variable.VariableType;
import com.kispoko.tome.engine.programming.variable.VariableUnion;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import java.io.Serializable;
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

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveValue<Integer> integerValue;
    private PrimitiveValue<String>  variableName;

    private PrimitiveValue<Kind>    kind;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public IntegerTermValue()
    {
        this.id           = null;

        this.integerValue = new PrimitiveValue<>(null, Integer.class);
        this.variableName = new PrimitiveValue<>(null, String.class);

        this.kind         = new PrimitiveValue<>(null, Kind.class);
    }


    private IntegerTermValue(UUID id, Object value, Kind kind)
    {
        this.id           = id;

        this.integerValue = new PrimitiveValue<>(null, Integer.class);
        this.variableName = new PrimitiveValue<>(null, String.class);

        this.kind         = new PrimitiveValue<>(kind, Kind.class);

        // > Set the value depending on the case
        switch (kind)
        {
            case LITERAL:
                this.integerValue.setValue((Integer) value);
                break;
            case VARIABLE:
                this.variableName.setValue((String) value);
                break;
        }
    }


    private static IntegerTermValue asLiteral(UUID id, Integer value)
    {
        return new IntegerTermValue(id, value, Kind.LITERAL);
    }


    private static IntegerTermValue asVariable(UUID id, String variableName)
    {
        return new IntegerTermValue(id, variableName, Kind.VARIABLE);
    }


    /**
     * Create an Integer Term Value from its Yaml representation.
     * @param yaml The yaml parser.
     * @return A new Integer Term Value.
     * @throws YamlException
     */
    public static IntegerTermValue fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID id   = UUID.randomUUID();

        Kind kind = Kind.fromYaml(yaml.atKey("type"));

        switch (kind)
        {
            case LITERAL:
                Integer value = yaml.atKey("value").getInteger();
                return IntegerTermValue.asLiteral(id, value);
            case VARIABLE:
                String variableName = yaml.atKey("variable").getString();
                return IntegerTermValue.asVariable(id, variableName);
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


    // > Value
    // ------------------------------------------------------------------------------------------


    /**
     * Get the value of the integer term. It is either a literal integer, or the value of an
     * integer variable.
     * @return The integer value.
     * @throws SummationException
     */
    public Integer value()
           throws SummationException
    {
        switch (this.kind.getValue())
        {
            case LITERAL:
                return this.integerValue.getValue();
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

        // > If variable is not a number, throw exception
        if (!variableUnion.getType().equals(VariableType.NUMBER)) {
            throw SummationException.variableNotNumber(
                    new VariableNotNumberError(variableName));
        }

        Integer variableValue = variableUnion.getNumber().value();

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
