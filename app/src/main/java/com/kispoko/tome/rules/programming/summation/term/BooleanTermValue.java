
package com.kispoko.tome.rules.programming.summation.term;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.rules.programming.summation.SummationException;
import com.kispoko.tome.rules.programming.summation.error.UndefinedVariableError;
import com.kispoko.tome.rules.programming.summation.error.VariableNotNumberError;
import com.kispoko.tome.rules.programming.variable.VariableIndex;
import com.kispoko.tome.rules.programming.variable.VariableType;
import com.kispoko.tome.rules.programming.variable.VariableUnion;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import java.util.UUID;



/**
 * Term Value: Boolean
 */
public class BooleanTermValue implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveValue<Boolean> booleanValue;
    private PrimitiveValue<String>  variableName;

    private PrimitiveValue<Kind>    kind;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public BooleanTermValue()
    {
        this.id           = null;

        this.booleanValue = new PrimitiveValue<>(null, Boolean.class);
        this.variableName = new PrimitiveValue<>(null, String.class);

        this.kind         = new PrimitiveValue<>(null, Kind.class);
    }


    private BooleanTermValue(UUID id, Object value, Kind kind)
    {
        this.id           = id;

        this.booleanValue = new PrimitiveValue<>(null, Boolean.class);
        this.variableName = new PrimitiveValue<>(null, String.class);

        this.kind         = new PrimitiveValue<>(kind, Kind.class);

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
     * @throws YamlException
     */
    public static BooleanTermValue fromYaml(Yaml yaml)
                  throws YamlException
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

    public Boolean value()
           throws SummationException
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


    private Boolean variableValue(String variableName)
            throws SummationException
    {
        VariableIndex variableIndex = SheetManager.currentSheet()
                                                  .getRulesEngine().getVariableIndex();

        // > If variable does not exist, throw exception
        if (!variableIndex.hasVariable(variableName)) {
            throw SummationException.undefinedVariable(
                    new UndefinedVariableError(variableName));
        }

        // [1] Get the variable
        VariableUnion variableUnion = variableIndex.variableWithName(variableName);

        // > If variable is not a number, throw exception
        if (!variableUnion.getType().equals(VariableType.BOOLEAN)) {
            throw SummationException.variableNotNumber(
                    new VariableNotNumberError(variableName));
        }

        Boolean variableValue = variableUnion.getBoolean().value();

        return variableValue;
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
