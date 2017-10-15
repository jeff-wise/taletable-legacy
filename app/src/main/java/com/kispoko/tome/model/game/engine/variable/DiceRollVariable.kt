
package com.kispoko.tome.model.game.engine.variable


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.rts.sheet.SheetContext
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Dice Variable Value
 */
sealed class DiceRollVariableValue : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // COSNTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceRollVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DiceRollVariableValue> = when (doc)
        {
            is DocDict -> when (doc.case())
            {
                "literal" -> DiceRollVariableLiteralValue.fromDocument(doc)
                else      -> effError<ValueError, DiceRollVariableValue>(
                                    UnknownCase(doc.case(), doc.path))
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    open fun dependencies() : Set<VariableReference> = setOf()

    abstract fun value() : DiceRoll

    abstract fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>>

}


/**
 * Literal Value
 */
data class DiceRollVariableLiteralValue(val diceRoll : DiceRoll)
            : DiceRollVariableValue(), Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceRollVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DiceRollVariableValue> =
                effApply(::DiceRollVariableLiteralValue, DiceRoll.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.diceRoll.toDocument().withCase("literal")


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value() : DiceRoll = this.diceRoll

    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() = this.diceRoll.onLoad()

    override val id = this.diceRoll.id

    override val name = this.diceRoll.name

    override val modelObject : Model = this.diceRoll

}


fun liftDiceRollVariableValue(varValue : DiceRollVariableValue) : Func<DiceRollVariableValue>
    = when (varValue)
    {
        is DiceRollVariableLiteralValue -> Comp(varValue, "literal")
    }


//
//
//
//    private void initializeFunctors()
//    {
//        // Name
//        this.name.setName("name");
//        this.name.setLabelId(R.string.variable_field_name_label);
//        this.name.setDescriptionId(R.string.variable_field_name_description);
//
//        // Label
//        this.label.setName("label");
//        this.label.setLabelId(R.string.variable_field_label_label);
//        this.label.setDescriptionId(R.string.variable_field_label_description);
//
//        // Description
//        this.description.setName("description");
//        this.description.setLabelId(R.string.variable_field_description_label);
//        this.description.setDescriptionId(R.string.variable_field_description_description);
//
//        // Dice Roll
//        this.diceRoll.setName("dice_roll");
//        this.diceRoll.setLabelId(R.string.dice_roll_variable_field_value_dice_label);
//        this.diceRoll.setDescriptionId(R.string.dice_roll_variable_field_value_dice_description);
//
//        // Is Namespaced?
//        this.isNamespaced.setName("is_namespaced");
//        this.isNamespaced.setLabelId(R.string.variable_field_is_namespaced_label);
//        this.isNamespaced.setDescriptionId(R.string.variable_field_is_namespaced_description);
//    }
//
//
//    // > To Yaml
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The Dice Variable's yaml representation.
//     * @return
//     */
//    public YamlBuilder toYaml()
//    {
//        return YamlBuilder.map()
//                .putString("name", this.name())
//                .putString("label", this.label())
//                .putYaml("dice", this.diceRoll())
//                .putBoolean("namespaced", this.isNamespaced());
//    }
//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    // ** Dice Roll
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the dice roll.
//     * @return The dice roll.
//     */
//    public DiceRoll diceRoll()
//    {
//        return this.diceRoll.getValue();
//    }
//
//
//    public Integer rollValue()
//    {
//        return this.diceRoll().roll();

//    }

