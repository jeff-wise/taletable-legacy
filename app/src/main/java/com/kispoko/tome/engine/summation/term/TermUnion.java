
package com.kispoko.tome.engine.summation.term;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.engine.variable.VariableType;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Term Union
 */
public class TermUnion implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<IntegerTerm>       literalTerm;
    private ModelFunctor<DiceRollTerm>      diceRollTerm;
    private ModelFunctor<ConditionalTerm>   conditionalTerm;

    private PrimitiveFunctor<TermType>      type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TermUnion()
    {
        this.id              = null;

        this.literalTerm     = ModelFunctor.empty(IntegerTerm.class);
        this.diceRollTerm    = ModelFunctor.empty(DiceRollTerm.class);
        this.conditionalTerm = ModelFunctor.empty(ConditionalTerm.class);

        this.type            = new PrimitiveFunctor<>(null, TermType.class);
    }


    private TermUnion(UUID id, Object term, TermType type)
    {
        this.id              = id;

        this.literalTerm     = ModelFunctor.full(null, IntegerTerm.class);
        this.diceRollTerm    = ModelFunctor.full(null, DiceRollTerm.class);
        this.conditionalTerm = ModelFunctor.full(null, ConditionalTerm.class);

        this.type            = new PrimitiveFunctor<>(type, TermType.class);

        switch (type)
        {
            case INTEGER:
                this.literalTerm.setValue((IntegerTerm) term);
                break;
            case DICE_ROLL:
                this.diceRollTerm.setValue((DiceRollTerm) term);
                break;
            case CONDITIONAL:
                this.conditionalTerm.setValue((ConditionalTerm) term);
                break;
        }
    }


    // > Variants
    // ------------------------------------------------------------------------------------------

    /**
     * Create the "literal term" case.
     * @param integerTerm The literal term.
     * @return The "literal term" case Term Union.
     */
    public static TermUnion asInteger(UUID id, IntegerTerm integerTerm)
    {
        return new TermUnion(id, integerTerm, TermType.INTEGER);
    }


    /**
     * Create the "dice roll term" case.
     * @param diceRollTerm The dice roll term.
     * @return The "dice roll term" case Term Union.
     */
    public static TermUnion asDiceRoll(UUID id, DiceRollTerm diceRollTerm)
    {
        return new TermUnion(id, diceRollTerm, TermType.DICE_ROLL);
    }


    /**
     * Create the "conditional term" case.
     * @param conditionalTerm The conditional term.
     * @return The "conditional term" case Term Union.
     */
    public static TermUnion asConditional(UUID id, ConditionalTerm conditionalTerm)
    {
        return new TermUnion(id, conditionalTerm, TermType.CONDITIONAL);
    }


    /**
     * Create a Term Union from its Yaml representation.
     * @param yaml The yaml parser.
     * @return The parsed Term Union.
     * @throws YamlParseException
     */
    public static TermUnion fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID     id   = UUID.randomUUID();

        TermType type = TermType.fromYaml(yaml.atKey("type"));

        switch (type)
        {
            case INTEGER:
                IntegerTerm integerTerm = IntegerTerm.fromYaml(yaml.atKey("term"));
                return TermUnion.asInteger(id, integerTerm);
            case DICE_ROLL:
                DiceRollTerm diceRollTerm = DiceRollTerm.fromYaml(yaml.atKey("term"));
                return TermUnion.asDiceRoll(id, diceRollTerm);
            case CONDITIONAL:
                ConditionalTerm conditionalTerm = ConditionalTerm.fromYaml(yaml.atKey("term"));
                return TermUnion.asConditional(id, conditionalTerm);
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
     * Get the type (case) of the union.
     * @return The union type.
     */
    public TermType type()
    {
        return this.type.getValue();
    }


    // ** Term
    // ------------------------------------------------------------------------------------------

    /**
     * Get the term from the union.
     * @return The Term.
     */
    public Term term()
    {
        switch (this.type())
        {
            case INTEGER:
                return this.integerTerm();
            case DICE_ROLL:
                return this.diceRollTerm();
            case CONDITIONAL:
                return this.conditionalTerm();
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(VariableType.class.getName())));
        }

        return null;
    }


    /**
     * The "literal term" case. Throws an exception if the union is not a "literal term".
     * @return The Literal Term.
     */
    public IntegerTerm integerTerm()
    {
        if (this.type() != TermType.INTEGER) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("literal", this.type.toString())));
        }
        return this.literalTerm.getValue();
    }


    /**
     * The "dice roll term" case. Throws an exception if the union is not a "dice roll term".
     * @return The Dice Roll Term.
     */
    public DiceRollTerm diceRollTerm()
    {
        if (this.type() != TermType.DICE_ROLL) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("dice roll", this.type.toString())));
        }
        return this.diceRollTerm.getValue();
    }


    /**
     * The "conditional term" case. Throws an exception if the union is not a "conditional term".
     * @return The Conditional Term.
     */
    public ConditionalTerm conditionalTerm()
    {
        if (this.type() != TermType.CONDITIONAL) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("conditional", this.type.toString())));
        }
        return this.conditionalTerm.getValue();
    }


}
