
package com.kispoko.tome.engine.programming.variable;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Variable Union
 */
public class VariableUnion implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelValue<BooleanVariable>  booleanVariable;
    private ModelValue<DiceVariable>     diceVariable;
    private ModelValue<NumberVariable>   numberVariable;
    private ModelValue<TextVariable>     textVariable;

    private PrimitiveValue<VariableType> type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public VariableUnion()
    {
        this.id = null;

        this.booleanVariable = ModelValue.empty(BooleanVariable.class);
        this.diceVariable    = ModelValue.empty(DiceVariable.class);
        this.numberVariable  = ModelValue.empty(NumberVariable.class);
        this.textVariable    = ModelValue.empty(TextVariable.class);

        this.type            = new PrimitiveValue<>(null, VariableType.class);
    }


    private VariableUnion(UUID id, Object variable, VariableType type)
    {
        this.id              = id;

        this.booleanVariable = ModelValue.full(null, BooleanVariable.class);
        this.diceVariable    = ModelValue.full(null, DiceVariable.class);
        this.numberVariable  = ModelValue.full(null, NumberVariable.class);
        this.textVariable    = ModelValue.full(null, TextVariable.class);

        this.type            = new PrimitiveValue<>(type, VariableType.class);

        switch (type)
        {
            case TEXT:
                this.textVariable.setValue((TextVariable) variable);
                break;
            case NUMBER:
                this.numberVariable.setValue((NumberVariable) variable);
                break;
            case BOOLEAN:
                this.booleanVariable.setValue((BooleanVariable) variable);
                break;
            case DICE:
                this.diceVariable.setValue((DiceVariable) variable);
                break;
        }
    }


    // > Variants
    // ------------------------------------------------------------------------------------------

    /**
     * Create the "text" variant.
     * @param textVariable The text variable.
     * @return The new Variable Union as the "text" case.
     */
    public static VariableUnion asText(UUID id, TextVariable textVariable)
    {
        return new VariableUnion(id, textVariable, VariableType.TEXT);
    }


    /**
     * Create the "text" variant, as a non-persistent value.
     * @param textVariable The text variable.
     * @return The "text" Variable Union.
     */
    public static VariableUnion asText(TextVariable textVariable)
    {
        return new VariableUnion(null, textVariable, VariableType.TEXT);
    }


    /**
     * Create the "number" variant.
     * @param numberVariable The number variable.
     * @return The new Variable Union as the "number" case.
     */
    public static VariableUnion asNumber(UUID id, NumberVariable numberVariable)
    {
        return new VariableUnion(id, numberVariable, VariableType.NUMBER);
    }


    /**
     * Create the "number" variant, as a non-persistent value.
     * @param numberVariable The number variable.
     * @return The "number" Variable Union.
     */
    public static VariableUnion asNumber(NumberVariable numberVariable)
    {
        return new VariableUnion(null, numberVariable, VariableType.NUMBER);
    }


    /**
     * Create the "boolean" variant.
     * @param booleanVariable The boolean variable.
     * @return The new Variable Union as the "boolean" case.
     */
    public static VariableUnion asBoolean(UUID id, BooleanVariable booleanVariable)
    {
        return new VariableUnion(id, booleanVariable, VariableType.BOOLEAN);
    }


    /**
     * Create the "boolean" variant, as a non-persistent value.
     * @param booleanVariable The boolean variable.
     * @return The "boolean" Variable Union.
     */
    public static VariableUnion asBoolean(BooleanVariable booleanVariable)
    {
        return new VariableUnion(null, booleanVariable, VariableType.BOOLEAN);
    }


    /**
     * Create the "dice" variant.
     * @param diceVariable The dice variable.
     * @return The "dice" Variable Union.
     */
    public static VariableUnion asDice(UUID id, DiceVariable diceVariable)
    {
        return new VariableUnion(id, diceVariable, VariableType.DICE);
    }


    /**
     * Create the "dice" variant, as a non-psersistent value.
     * @param diceVariable The dice variable.
     * @return The "dice" Variable Union.
     */
    public static VariableUnion asDice(DiceVariable diceVariable)
    {
        return new VariableUnion(null, diceVariable, VariableType.DICE);
    }


    /**
     * Create a Variable Union from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Variable Union.
     * @throws YamlException
     */
    public static VariableUnion fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID         id   = UUID.randomUUID();

        VariableType type = VariableType.fromYaml(yaml.atKey("type"));

        switch (type)
        {
            case TEXT:
                TextVariable textVariable = TextVariable.fromYaml(yaml.atKey("value"));
                return VariableUnion.asText(id, textVariable);
            case NUMBER:
                NumberVariable numberVariable = NumberVariable.fromYaml(yaml.atKey("value"));
                return VariableUnion.asNumber(id, numberVariable);
            case BOOLEAN:
                BooleanVariable booleanVariable = BooleanVariable.fromYaml(yaml.atKey("value"));
                return VariableUnion.asBoolean(id, booleanVariable);
            case DICE:
                DiceVariable diceVariable = DiceVariable.fromYaml(yaml.atKey("value"));
                return VariableUnion.asDice(id, diceVariable);
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
     * This method is called when the RulesEngine is completely loaded for the first time.
     */
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Type
    // ------------------------------------------------------------------------------------------

    /**
     * Get the variant type of the variable union.
     * @return The Variable ErrorType.
     */
    public VariableType type()
    {
        return this.type.getValue();
    }


    // ** Variables
    // ------------------------------------------------------------------------------------------

    /**
     * Get the variable.
     * @return The variable.
     */
    public Variable variable()
    {
        switch (this.type())
        {
            case TEXT:
                return this.textVariable();
            case NUMBER:
                return this.numberVariable();
            case BOOLEAN:
                return this.booleanVariable();
            case DICE:
                return this.diceVariable();
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(VariableType.class.getName())));
        }

        return null;
    }


    /**
     * Get the text case of the union.
     * @return The Text Variable.
     */
    public TextVariable textVariable()
    {
        if (this.type() != VariableType.TEXT) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("text", this.type.toString())));
        }
        return this.textVariable.getValue();
    }


    /**
     * Get the number case of the union.
     * @return The Number Variable.
     */
    public NumberVariable numberVariable()
    {
        if (this.type() != VariableType.NUMBER) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("number", this.type.toString())));
        }
        return this.numberVariable.getValue();
    }


    /**
     * Get the boolean case of the union.
     * @return The Boolean Variable.
     */
    public BooleanVariable booleanVariable()
    {
        if (this.type() != VariableType.BOOLEAN) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("boolean", this.type.toString())));
        }
        return this.booleanVariable.getValue();
    }


    /**
     * Get the dice variable case of the union.
     * @return The Dice Variable.
     */
    public DiceVariable diceVariable()
    {
        if (this.type() != VariableType.DICE) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("dice", this.type.toString())));
        }
        return this.diceVariable.getValue();
    }


    // ** Is Null
    // ------------------------------------------------------------------------------------------

    public boolean isNull()
    {
        switch (this.type())
        {
            case TEXT:
                return this.textVariable.isNull();
            case NUMBER:
                return this.numberVariable.isNull();
            case BOOLEAN:
                return this.booleanVariable.isNull();
            case DICE:
                return this.diceVariable.isNull();
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(VariableType.class.getName())));
        }

        return true;
    }

}
