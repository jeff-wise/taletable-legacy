
package com.kispoko.tome.model.game.engine.dice


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
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
                    val rollName : Maybe<Prim<DiceRollName>>)
                     : ToDocument, Model, Serializable
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


    constructor()
        : this(UUID.randomUUID(),
               Conj(mutableSetOf()),
               Conj(mutableSetOf()),
               Nothing())


    companion object : Factory<DiceRoll>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DiceRoll> = when (doc)
        {
            is DocDict ->
            {
                effApply(::DiceRoll,
                         // Quantity
                         doc.list("quantities") ap { docList ->
                             docList.mapSetMut { DiceQuantity.fromDocument(it) }
                         },
                         // Modifier
                         split(doc.maybeList("modifiers"),
                               effValue(mutableSetOf()),
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
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "quantities" to DocList(this.quantities().map { it.toDocument() }),
        "modifiers" to DocList(this.modifiers().map { it.toDocument() })
    ))
    .maybeMerge(this.rollName().apply {
        Just(Pair("name", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun quantities() : Set<DiceQuantity> = this.quantities.set

    fun modifiers() : Set<RollModifier> = this.modifiers.set

    fun rollName() : Maybe<DiceRollName> = _getMaybePrim(this.rollName)

    fun rollNameString() : String? = getMaybePrim(this.rollName)?.value


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


    fun add(diceRoll : DiceRoll) : DiceRoll
    {
        val quantities = this.quantities().plus(diceRoll.quantities()).toMutableSet()
        val modifiers = this.modifiers().plus(diceRoll.modifiers()).toMutableSet()

        return DiceRoll(quantities, modifiers)
    }


    fun addModifier(modifier : RollModifier) : DiceRoll
    {
        val modifiers = this.modifiers().plusElement(modifier)

        return DiceRoll(this.quantities().toMutableSet(), modifiers.toMutableSet())
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
                        val quantity : Prim<DiceRollQuantity>)
                         : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(sides : DiceSides, quantity : DiceRollQuantity)
        : this(UUID.randomUUID(), Prim(sides), Prim(quantity))

    companion object : Factory<DiceQuantity>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DiceQuantity> = when (doc)
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
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "sides" to this.sides().toDocument(),
        "quantity" to this.quantity().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun sides() : DiceSides = this.sides.value

    fun sidesInt() : Int = this.sides.value.value

    fun quantity() : DiceRollQuantity = this.quantity.value

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
data class DiceRollName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceRollName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DiceRollName> = when (doc)
        {
            is DocText -> effValue(DiceRollName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Dice Sides
 */
data class DiceSides(val value : Int) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceSides>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DiceSides> = when (doc)
        {
            is DocNumber -> effValue(DiceSides(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({this.value})

}


/**
 * Dice Roll Quantity
 */
data class DiceRollQuantity(val value : Int) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceRollQuantity>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DiceRollQuantity> = when (doc)
        {
            is DocNumber -> effValue(DiceRollQuantity(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


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
                         : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(modifier : Double)
        : this(UUID.randomUUID(),
               Prim(RollModifierValue(modifier)),
               Nothing())


    constructor(value : RollModifierValue,
                name : Maybe<RollModifierName>)
        : this(UUID.randomUUID(),
               Prim(value),
               maybeLiftPrim(name))


    companion object : Factory<RollModifier>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<RollModifier> = when (doc)
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
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "value" to this.value().toDocument()
    ))
    .maybeMerge(this.name().apply {
        Just(Pair("name", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun value() : RollModifierValue = this.value.value

    fun valueInt() : Int = this.value.value.value.toInt()

    fun name() : Maybe<RollModifierName> = _getMaybePrim(this.modifierName)

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
data class RollModifierValue(val value : Double) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RollModifierValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<RollModifierValue> = when (doc)
        {
            is DocNumber -> effValue(RollModifierValue(doc.number))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value})

}


/**
 * Roll Modifier Name
 */
data class RollModifierName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RollModifierName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<RollModifierName> = when (doc)
        {
            is DocText -> effValue(RollModifierName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


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


