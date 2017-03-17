
package com.kispoko.tome.engine.summation.term;


import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.variable.VariableException;
import com.kispoko.tome.engine.variable.VariableReference;
import com.kispoko.tome.engine.variable.error.UndefinedVariableError;
import com.kispoko.tome.engine.variable.error.UnexpectedVariableTypeError;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.engine.variable.VariableType;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;

import java.io.Serializable;
import java.util.UUID;



/**
 * Term Value: Boolean
 */
public class BooleanTermValue implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<Boolean>   booleanValue;
    private PrimitiveFunctor<String>    variableName;

    private PrimitiveFunctor<Kind>      kind;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BooleanTermValue()
    {
        this.id           = null;

        this.booleanValue = new PrimitiveFunctor<>(null, Boolean.class);
        this.variableName = new PrimitiveFunctor<>(null, String.class);

        this.kind         = new PrimitiveFunctor<>(null, Kind.class);
    }


    private BooleanTermValue(UUID id, Object value, Kind kind)
    {
        this.id           = id;

        this.booleanValue = new PrimitiveFunctor<>(null, Boolean.class);
        this.variableName = new PrimitiveFunctor<>(null, String.class);

        this.kind         = new PrimitiveFunctor<>(kind, Kind.class);

        // > Set the value depending on the case
        switch (kind)
        {
            case LITERAL:
                this.booleanValue.setValue((Boolean) value);
                break;
            case VARIABLE:
                this.variableName.setValue((String) value);
                break;
        }
    }


    private static BooleanTermValue asLiteral(UUID id, Boolean value)
    {
        return new BooleanTermValue(id, value, Kind.LITERAL);
    }


    private static BooleanTermValue asVariable(UUID id, String variableName)
    {
        return new BooleanTermValue(id, variableName, Kind.VARIABLE);
    }


    /**
     * Create a Boolean Term Value from its Yaml representation.
     * @param yaml The yaml parser.
     * @return A new Boolean Term Value.
     * @throws YamlParseException
     */
    public static BooleanTermValue fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID id   = UUID.randomUUID();

        Kind kind = Kind.fromYaml(yaml.atKey("type"));

        switch (kind)
        {
            case LITERAL:
                Boolean value = yaml.atKey("value").getBoolean();
                return BooleanTermValue.asLiteral(id, value);
            case VARIABLE:
                String variableName = yaml.atKey("variable").getString();
                return BooleanTermValue.asVariable(id, variableName);
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
     * The variable name case.
     * @return The variable name.
     */
    private String variableName()
    {
        return this.variableName.getValue();
    }


    /**
     * Get the value of the boolean term. It may be a static boolean value, or the value of a
     * boolean variable.
     * @return The boolean value.
     * @throws VariableException
     */
    public Boolean value()
           throws VariableException
    {
        switch (this.kind.getValue())
        {
            case LITERAL:
                return this.booleanValue.getValue();
            case VARIABLE:
                return this.variableValue(this.variableName.getValue());
        }

        return null;
    }


    /**
     * Get the name of the boolean variable of the term. If the term is not a variable, then
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
                return VariableReference.asByName(this.variableName());
        }

        return null;
    }


    /**
     * Get the value of the term's boolean variable.
     * @param variableName The boolean variable name.
     * @return The boolean variable's value.
     */
    private Boolean variableValue(String variableName)
            throws VariableException
    {
        // > If variable does not exist, throw exception
        if (!State.hasVariable(variableName)) {
            throw VariableException.undefinedVariable(
                    new UndefinedVariableError(variableName));
        }

        // [1] Get the variable
        VariableUnion variableUnion = State.variableWithName(variableName);

        // > If variable is not a number, throw exception
        if (!variableUnion.type().equals(VariableType.BOOLEAN)) {
            throw VariableException.unexpectedVariableType(
                    new UnexpectedVariableTypeError(variableName,
                                                    VariableType.BOOLEAN,
                                                    variableUnion.type()));
        }

        return variableUnion.booleanVariable().value();
    }


    // KIND
    // ------------------------------------------------------------------------------------------

    public enum Kind
    {

        LITERAL,
        VARIABLE;


        public static Kind fromString(String typeString)
                      throws InvalidDataException
        {
            return EnumUtils.fromString(Kind.class, typeString);
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
