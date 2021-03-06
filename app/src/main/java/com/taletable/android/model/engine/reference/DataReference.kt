
package com.taletable.android.model.engine.reference


import com.taletable.android.lib.Factory
import com.taletable.android.model.engine.variable.VariableReference
import com.taletable.android.rts.entity.EntityId
import effect.apply
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
sealed class DataReference : ToDocument, Serializable
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
                "data_reference_text"      -> DataReferenceText.fromDocument(doc.nextCase())
                                                as ValueParser<DataReference>
                else                       -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    abstract fun dependencies(entityId : EntityId): Set<VariableReference>

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
        override fun fromDocument(doc : SchemaDoc) : ValueParser<DataReferenceBoolean> =
                effApply(::DataReferenceBoolean, BooleanReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.reference.toDocument()
                                    .withCase("data_reference_boolean")


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId): Set<VariableReference> =
            this.reference.dependencies()


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

//    override fun columnValue() = SumValue(this.reference)
//
//
//    override fun case() = "boolean"
//
//
//    override val sumModelObject = this.reference

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
//
//    override fun columnValue() = SumValue(this.reference)
//
//
//    override fun case() = "dice_roll"
//
//
//    override val sumModelObject = this.reference
//

    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId): Set<VariableReference>
            = this.reference.dependencies(entityId)

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

    override fun dependencies(entityId : EntityId): Set<VariableReference> = this.reference.dependencies()


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

//    override fun columnValue() = SumValue(this.reference)
//
//
//    override fun case() = "number"
//
//
//    override val sumModelObject = this.reference

}


/**
 * Text Value Reference
 */
data class DataReferenceText(val reference : TextReference) : DataReference()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DataReferenceText>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<DataReferenceText> =
                apply(::DataReferenceText, TextReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.reference.toDocument()
                                    .withCase("data_reference_text")


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId): Set<VariableReference> =
            this.reference.dependencies(entityId)


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

//    override fun columnValue() = SumValue(this.reference)
//
//
//    override fun case() = "text"
//
//
//    override val sumModelObject = this.reference

}
