
package com.taletable.android.model.engine.dice


import com.taletable.android.app.ApplicationLog
import com.taletable.android.db.*
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.RowValue2
import com.taletable.android.lib.orm.schema.MaybePrimValue
import com.taletable.android.lib.orm.schema.PrimValue
import com.taletable.android.lib.orm.sql.*
import com.taletable.android.model.engine.FormulaModifier
import com.taletable.android.model.engine.FormulaModifierMultiplyDice
import com.taletable.android.model.engine.reference.DiceRollReference
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.sheet.SheetData
import com.taletable.android.util.Util
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import maybe.Just
import maybe.Nothing
import maybe.Maybe
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*



/**
 * Dice Roll
 */
data class DiceRoll(val quantities : MutableList<DiceQuantity>,
                    val modifiers : MutableList<RollModifier>,
                    val formulaModifiers : MutableList<FormulaModifier>,
                    val rollName : Maybe<DiceRollName>)
                     : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(quantities : List<DiceQuantity>,
                modifiers : List<RollModifier>,
                formulaModifiers : List<FormulaModifier>)
        : this(quantities.toMutableList(),
               modifiers.toMutableList(),
               formulaModifiers.toMutableList(),
               Nothing())


    constructor() : this(mutableListOf(),
                         mutableListOf(),
                         mutableListOf(),
                         Nothing())


    companion object : Factory<DiceRoll>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<DiceRoll> = when (doc)
        {
            is DocDict ->
            {
                apply(::DiceRoll,
                      // Quantity
                      doc.list("quantities") ap {
                          it.mapMut { DiceQuantity.fromDocument(it) }
                      },
                      // Modifiers
                      split(doc.maybeList("modifiers"),
                            effValue(mutableListOf()),
                           { it.mapMut { RollModifier.fromDocument(it) } }),
                      // Formula Modifiers
                      split(doc.maybeList("formula_modifiers"),
                            effValue(mutableListOf()),
                            { it.mapMut { FormulaModifier.fromDocument(it) } }),
                      // Name
                      split(doc.maybeAt("name"),
                           effValue<ValueError, Maybe<DiceRollName>>(Nothing()),
                           { apply(::Just, DiceRollName.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "quantities" to DocList(this.quantities.map { it.toDocument() }),
        "modifiers" to DocList(this.modifiers.map { it.toDocument() })
    ))
    .maybeMerge(this.rollName.apply {
        Just(Pair("name", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun quantities() : List<DiceQuantity> = this.quantities


    fun modifiers() : List<RollModifier> = this.modifiers


    fun formulaModifiers() : List<FormulaModifier> = this.formulaModifiers


    fun rollName() : Maybe<DiceRollName> = this.rollName


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_DiceRollValue =
//        RowValue3(diceRollTable, PrimValue(DiceQuantitySet(this.quantities)),
//                                 CollValue(this.modifiers),
//                                 MaybePrimValue(this.rollName))


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------




    fun roll() : Int
    {
        val modRoll = this.withFormulaModifiers()

        return modRoll.quantities().map({ it.roll() }).sum() +
                       modRoll.modifierValues().sum()
    }


    fun modifierValues() : List<Int> = this.modifiers().map { it.valueInt() }


    fun rollSummary() : RollSummary
    {
        val modRoll = this.withFormulaModifiers()

        val quantitySummaries = modRoll.quantities()
                                    .sortedByDescending { it.quantityInt() }
                                    .map { RollPartSummary(it.roll(), it.toString(), "") }

        val modifierSummaries = modRoll.modifiers()
                                    .filter { it.valueInt() != 0 }
                                    .sortedByDescending { it.valueInt() }
                                    .map { RollPartSummary(it.valueInt(), "", it.name().toNullable()?.value ?: "") }

        val total = quantitySummaries.map { it.value }.sum() +
                    modifierSummaries.map { it.value }.sum()

        val rollName = this.rollName.toNullable()?.value ?: ""

        return RollSummary(rollName, total, quantitySummaries.plus(modifierSummaries))
    }


    fun add(diceRoll : DiceRoll) : DiceRoll
    {
        val quantities = this.quantities().plus(diceRoll.quantities())
        val modifiers = this.modifiers().plus(diceRoll.modifiers())
        val formulaModifiers = this.formulaModifiers().plus(diceRoll.formulaModifiers())

        return DiceRoll(quantities, modifiers, formulaModifiers)
    }


    fun addModifier(modifier : RollModifier) : DiceRoll
    {
//        val modifiers = this.modifiers().plusElement(modifier)
//
//        return DiceRoll(this.quantities(), modifiers)

        this.modifiers.add(modifier)
        return this
    }


    fun rangeString(base : Double) : String
    {
        var min = base
        var max = base

        this.quantities().forEach {
            min += it.quantityInt()
            max += (it.sidesInt() * it.quantityInt())
        }

        this.modifiers().forEach {
            min += it.valueInt()
            max += it.valueInt()
        }

        return "${Util.doubleString(min)} - ${Util.doubleString(max)}"
    }


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString() : String
    {
        val modRoll = this.withFormulaModifiers()

        val diceString = modRoll.quantities().sortedBy { it.sidesInt() }
                                          .map { it.toString() }
                                          .joinToString(" + ")

        val modifierSum = modRoll.modifierValues().sum()
        var modifierString = ""
        if (modifierSum > 0)
            modifierString = " + " + modifierSum.toString()
        else if (modifierSum < 0)
            modifierString = " - " + Math.abs(modifierSum).toString()

        return diceString + modifierString
    }


    fun modifierString() : String
    {
        val modRoll = this.withFormulaModifiers()

        val modifierSum = modRoll.modifierValues().sum()

        var modifierString = ""
        if (modifierSum > 0)
            modifierString = "+" + modifierSum.toString()
        else if (modifierSum < 0)
            modifierString = "-" + Math.abs(modifierSum).toString()
        else
            modifierString = "+0"

        return modifierString
    }


    // -----------------------------------------------------------------------------------------
    // FORMULA MODIFIERS
    // -----------------------------------------------------------------------------------------

    /**
     * Returns this dice roll after being modified by the formula modifiers.
     */
    private fun withFormulaModifiers() : DiceRoll
    {
        var currentDiceRoll : DiceRoll = this

        this.formulaModifiers.forEach {
            currentDiceRoll = currentDiceRoll.withFormulaModifier(it)
        }

        return currentDiceRoll
    }


    private fun withFormulaModifier(formulaModifier : FormulaModifier) : DiceRoll =
        when (formulaModifier) {
            is FormulaModifierMultiplyDice -> formulaModifier.apply(this)
        }


}


/**
 * Dice Quantity
 */
data class DiceQuantity(val sides : DiceSides,
                        val quantity : DiceRollQuantity)
                         : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceQuantity>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DiceQuantity> = when (doc)
        {
            is DocDict -> apply(::DiceQuantity,
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

    fun sides() : DiceSides = this.sides


    fun sidesInt() : Int = this.sides.value


    fun quantity() : DiceRollQuantity = this.quantity


    fun quantityInt() : Int = this.quantity.value


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


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ "${sidesInt()},${quantityInt()}" })

}


/**
 * Dice Quantity Set
 */
data class DiceQuantitySet(val quantities : List<DiceQuantity>) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceQuantitySet>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<DiceQuantitySet> = when (doc)
        {
            is DocList -> apply(::DiceQuantitySet, doc.map { DiceQuantity.fromDocument(it) })
            else       -> effError(lulo.value.UnexpectedType(DocType.LIST, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ quantities.joinToString(",") })

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

    override fun asSQLValue() = this.value.asSQLValue()

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

    override fun asSQLValue() = this.value.asSQLValue()

}


/**
 * Dice Modifier
 */
data class RollModifier(override val id : UUID,
                        val value : RollModifierValue,
                        val modifierName : Maybe<RollModifierName>)
                         : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(modifier : Double)
        : this(UUID.randomUUID(),
               RollModifierValue(modifier),
               Nothing())


    constructor(value : RollModifierValue,
                name : Maybe<RollModifierName>)
        : this(UUID.randomUUID(),
               value,
               name)


    companion object : Factory<RollModifier>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<RollModifier> = when (doc)
        {
            is DocDict ->
            {
                apply(::RollModifier,
                      // Value
                      doc.at("value") ap { RollModifierValue.fromDocument(it) },
                      // Name
                      split(doc.maybeAt("name"),
                            effValue<ValueError,Maybe<RollModifierName>>(Nothing()),
                            { effApply(::Just, RollModifierName.fromDocument(it)) } )
                      )
            }
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

    fun value() : RollModifierValue = this.value


    fun valueInt() : Int = this.value.value.toInt()


    fun name() : Maybe<RollModifierName> = this.modifierName


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_RollModifierValue =
        RowValue2(rollModifierTable, PrimValue(this.value),
                                     MaybePrimValue(this.modifierName))

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



/**
 * Dice Roll Group
 */
data class DiceRollGroup(override val id : UUID,
                         val rollReferences : List<DiceRollReference>,
                         val groupName : Maybe<DiceRollGroupName>)
                          : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(rollReferences : List<DiceRollReference>,
                groupName : Maybe<DiceRollGroupName>)
        : this(UUID.randomUUID(),
               rollReferences,
               groupName)


    companion object : Factory<DiceRollGroup>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<DiceRollGroup> = when (doc)
        {
            is DocDict ->
            {
                apply(::DiceRollGroup,
                      // Rolls
                      doc.list("roll_references") ap {
                          it.map { DiceRollReference.fromDocument(it) }
                      },
                      // Name
                      split(doc.maybeAt("name"),
                           effValue<ValueError, Maybe<DiceRollGroupName>>(Nothing()),
                           { apply(::Just, DiceRollGroupName.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "roll_references" to DocList(this.rollReferences.map { it.toDocument() })
    ))
    .maybeMerge(this.groupName.apply {
        Just(Pair("group_name", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun rollReferences() : List<DiceRollReference> = this.rollReferences


    fun groupName() : Maybe<DiceRollGroupName> = this.groupName


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_DiceRollGroupValue =
        RowValue2(diceRollGroupTable,
                  PrimValue(DiceRollReferences(this.rollReferences)),
                  MaybePrimValue(this.groupName))


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------


    fun diceRolls(entityId : EntityId) : List<DiceRoll>
    {
        val rollsEff = this.rollReferences.mapM { SheetData.diceRoll(it, entityId) }

        return when (rollsEff)
        {
            is Val -> rollsEff.value
            is Err -> {
                ApplicationLog.error(rollsEff.error)
                listOf()
            }
        }
    }

}


/**
 * Dice Roll Group Name
 */
data class DiceRollGroupName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceRollGroupName>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<DiceRollGroupName> = when (doc)
        {
            is DocText -> effValue(DiceRollGroupName(doc.text))
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
 * Dice Roll References
 */
data class DiceRollReferences(val variables : List<DiceRollReference>) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}



data class RollSummary(val name : String, val value : Int, val parts : List<RollPartSummary>)


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






