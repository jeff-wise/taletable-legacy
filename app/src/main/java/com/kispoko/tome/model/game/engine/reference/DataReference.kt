
package com.kispoko.tome.model.game.engine.reference


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.SumType
import com.kispoko.tome.lib.orm.schema.SumValue
import com.kispoko.tome.model.game.engine.variable.VariableReference
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.*
import java.io.Serializable



/**
 * Value Reference
 */
@Suppress("UNCHECKED_CAST")
sealed class DataReference : ToDocument, SumType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DataReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DataReference> =
            when (doc.case())
            {
                "data_reference_boolean"   -> DataReferenceBoolean.fromDocument(doc.nextCase())
                                                as ValueParser<DataReference>
                "data_reference_dice_roll" -> DataReferenceDiceRoll.fromDocument(doc.nextCase())
                                                as ValueParser<DataReference>
                "data_reference_number"    -> DataReferenceNumber.fromDocument(doc.nextCase())
                                                as ValueParser<DataReference>
                else                       -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    abstract fun dependencies(): Set<VariableReference>

}


/**
 * Boolean Value Reference
 */
data class DataReferenceBoolean(val reference : BooleanReference) : DataReference()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DataReferenceBoolean>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DataReferenceBoolean> = when (doc)
        {
            is DocDict -> BooleanReference.fromDocument(doc) ap {
                              effValue<ValueError, DataReferenceBoolean>(
                                      DataReferenceBoolean(it))
                          }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.reference.toDocument()
                                    .withCase("data_reference_boolean")


    // -----------------------------------------------------------------------------------------
    // SUM TYPE
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = SumValue(this.reference)


    override fun case() = "boolean"


    override val sumModelObject = this.reference


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> = this.reference.dependencies()

}


/**
 * Dice Roll Value Reference
 */
data class DataReferenceDiceRoll(val reference : DiceRollReference) : DataReference()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DataReferenceDiceRoll>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DataReferenceDiceRoll> = when (doc)
        {
            is DocDict -> DiceRollReference.fromDocument(doc) ap {
                              effValue<ValueError, DataReferenceDiceRoll>(
                                      DataReferenceDiceRoll(it))
                          }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.reference.toDocument()
                                    .withCase("data_reference_dice_roll")


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = SumValue(this.reference)


    override fun case() = "dice_roll"


    override val sumModelObject = this.reference


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> = this.reference.dependencies()

}


/**
 * Number Value Reference
 */
data class DataReferenceNumber(val reference : NumberReference) : DataReference()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DataReferenceNumber>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DataReferenceNumber> =
                effApply(::DataReferenceNumber, NumberReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.reference.toDocument()
                                    .withCase("data_reference_number")


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> = this.reference.dependencies()


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = SumValue(this.reference)


    override fun case() = "number"


    override val sumModelObject = this.reference

}
