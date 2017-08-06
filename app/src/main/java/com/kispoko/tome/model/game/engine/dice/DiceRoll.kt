
package com.kispoko.tome.model.game.engine.dice


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Conj
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.functor.getMaybePrim
import com.kispoko.tome.lib.functor.maybeLiftPrim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.*
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Dice Roll
 */
data class DiceRoll(override val id : UUID,
                    val quantities : Conj<DiceQuantity>,
                    val modifiers : Conj<RollModifier>,
                    val rollName : Maybe<Prim<DiceRollName>>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(quantities : MutableSet<DiceQuantity>,
                modifiers : MutableSet<RollModifier>)
        : this(UUID.randomUUID(),
               Conj(quantities),
               Conj(modifiers),
               Nothing())


    constructor(quantities : MutableSet<DiceQuantity>,
                modifiers : MutableSet<RollModifier>,
                rollName : Maybe<DiceRollName>)
        : this(UUID.randomUUID(),
               Conj(quantities),
               Conj(modifiers),
               maybeLiftPrim(rollName))


    companion object : Factory<DiceRoll>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<DiceRoll> = when (doc)
        {
            is DocDict ->
            {
                effApply(::DiceRoll,
                         // Quantity
                         doc.list("quantities") ap { docList ->
                             docList.mapSetMut { DiceQuantity.fromDocument(it) }
                         },
                         // Modifier
                         split(doc.maybeList("modiiers"),
                               effValue(mutableSetOf<RollModifier>()),
                              { it.mapSetMut { RollModifier.fromDocument(it) } }),
                         // Name
                         split(doc.maybeAt("name"),
                               effValue<ValueError,Maybe<DiceRollName>>(Nothing()),
                               { effApply(::Just, DiceRollName.fromDocument(it))}
                         ))
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun quantities() : Set<DiceQuantity> = this.quantities.set

    fun modifiers() : Set<RollModifier> = this.modifiers.set

    fun rollName() : String? = getMaybePrim(this.rollName)?.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "dice_roll"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun roll() : Int = this.quantities().map({ it.roll() }).sum() +
                       this.modifierValues().sum()


    fun modifierValues() : List<Int> = this.modifiers().map { it.valueInt() }


    fun rollSummary() : RollSummary
    {
        val quantitySummaries = this.quantities()
                                    .sortedByDescending { it.quantityInt() }
                                    .map { RollPartSummary(it.roll(), it.toString(), "") }

        val modifierSummaries = this.modifiers()
                                    .sortedByDescending { it.valueInt() }
                                    .map { RollPartSummary(it.valueInt(), "", it.nameString() ?: "") }

        val total = quantitySummaries.map { it.value }.sum() +
                    modifierSummaries.map { it.value }.sum()


        return RollSummary(total, quantitySummaries.plus(modifierSummaries))
    }


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString() : String
    {
        val diceString = this.quantities().sortedBy { it.sidesInt() }
                                          .map { it.toString() }
                                          .joinToString(" + ")

        val modifierSum = this.modifierValues().sum()
        var modifierString = ""
        if (modifierSum != 0)
            modifierString = " + " + modifierSum.toString()

        return diceString + modifierString
    }

}


/**
 * Dice Quantity
 */
data class DiceQuantity(override val id : UUID,
                        val sides : Prim<DiceSides>,
                        val quantity : Prim<DiceRollQuantity>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(sides : DiceSides, quantity : DiceRollQuantity)
        : this(UUID.randomUUID(), Prim(sides), Prim(quantity))

    companion object : Factory<DiceQuantity>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<DiceQuantity> = when (doc)
        {
            is DocDict -> effApply(::DiceQuantity,
                                   // Sides
                                   doc.at("sides") ap { DiceSides.fromDocument(it) },
                                   // Quantity
                                   doc.at("quantity") ap { DiceRollQuantity.fromDocument(it) }
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun sidesInt() : Int = this.sides.value.value

    fun quantityInt() : Int = this.quantity.value.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "dice_quantity"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // ROLL
    // -----------------------------------------------------------------------------------------

    fun roll() : Int = this.rollValues().sum()


    fun rollValues() : List<Int>
    {
        val random = Random()
        return  (1..quantityInt()).map { random.nextInt(sidesInt()) + 1 }
    }


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString() : String =
            this.quantityInt().toString() + "d" + this.sidesInt().toString()
}


/**
 * Dice Roll Name
 */
data class DiceRollName(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceRollName>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<DiceRollName> = when (doc)
        {
            is DocText -> effValue(DiceRollName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Dice Sides
 */
data class DiceSides(val value : Int) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceSides>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<DiceSides> = when (doc)
        {
            is DocNumber -> effValue(DiceSides(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({this.value})

}


/**
 * Dice Roll Quantity
 */
data class DiceRollQuantity(val value : Int) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceRollQuantity>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<DiceRollQuantity> = when (doc)
        {
            is DocNumber -> effValue(DiceRollQuantity(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({this.value})

}


/**
 * Dice Modifier
 */
data class RollModifier(override val id : UUID,
                        val value : Prim<RollModifierValue>,
                        val modifierName : Maybe<Prim<RollModifierName>>)
                         : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(value : RollModifierValue,
                name : Maybe<RollModifierName>)
        : this(UUID.randomUUID(),
               Prim(value),
               maybeLiftPrim(name))


    companion object : Factory<RollModifier>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<RollModifier> = when (doc)
        {
            is DocDict -> effApply(::RollModifier,
                                   // Value
                                   doc.at("value") ap { RollModifierValue.fromDocument(it) },
                                   // Name
                                   split(doc.maybeAt("name"),
                                         effValue<ValueError,Maybe<RollModifierName>>(Nothing()),
                                         { effApply(::Just, RollModifierName.fromDocument(it)) } )
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun valueInt() : Int = this.value.value.value.toInt()

    fun nameString() : String? = getMaybePrim(this.modifierName)?.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "roll_modifier"

    override val modelObject = this

}


/**
 * Roll Modifier Value
 */
data class RollModifierValue(val value : Double) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RollModifierValue>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<RollModifierValue> = when (doc)
        {
            is DocNumber -> effValue(RollModifierValue(doc.number))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value})

}


/**
 * Roll Modifier Name
 */
data class RollModifierName(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RollModifierName>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<RollModifierName> = when (doc)
        {
            is DocText -> effValue(RollModifierName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


data class RollSummary(val value : Int, val parts : List<RollPartSummary>)


data class RollPartSummary(val value : Int, val dice : String, val tag : String)


/**
 * Dice Roll Modifier Function
 */
sealed class DiceRollModifierFunction : Serializable
{
    object DropHighest : DiceRollModifierFunction()
    {
        override fun toString() = "Drop the Highest Roll"
    }

    object DropLowest : DiceRollModifierFunction()
    {
        override fun toString() = "Drop the Lowest Roll"
    }
}



//    // > Description
//    // ------------------------------------------------------------------------------------------
//
//    public String description()
//    {
//        String description = "";
//
//        description += "Roll a 1d";
//        description += Integer.toString(this.diceSides());
//        description += " ";
//        description += Integer.toString(this.quantity());
//        description += " times";
//
//        return description;
//    }
//
//    // > To String
//    // ------------------------------------------------------------------------------------------
//
//    @Override
//    public String toString()
//    {
//        StringBuilder quantity = new StringBuilder();
//
//        quantity.append(Integer.toString(this.quantity()));
//        quantity.append("d");
//        quantity.append(Integer.toString(this.diceSides()));
//
//        return quantity.toString();
//    }
//
//
//    // > Roll
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Roll the dice and return the total value.
//     * @return The sum of the random dice values.
//     */
//    public Integer roll()
//    {
//        return this.rollAsSummary().rollValue();
//    }
//
//
//    /**
//     * Roll the dice and return a summary of the values of each die in the roll.
//     * @return The randomly generated roll summary.
//     */
//    public RollPartSummary rollAsSummary()
//    {
//        List<DieRollResult> results = new ArrayList<>();
//
//        for (int i = 0; i < this.quantity(); i++) {
//            results.add(DieRollResult.generate(this.diceSides()));
//        }
//
//        return new RollPartSummary(results);
//    }
//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Initialize
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeFunctors()
//    {
//        // Dice Sides
//        this.diceSides.setName("dice_sides");
//        this.diceSides.setLabelId(R.string.dice_quantity_field_dice_sides_label);
//        this.diceSides.setDescriptionId(R.string.dice_quantity_field_dice_sides_description);
//
//        // Quantity
//        this.quantity.setName("quantity");
//        this.quantity.setLabelId(R.string.dice_quantity_field_quantity_label);
//        this.quantity.setDescriptionId(R.string.dice_quantity_field_quantity_description);
//    }
//
//}


