
package com.kispoko.tome.model.game.engine.reference


import android.util.Log
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Sum
import com.kispoko.tome.lib.model.SumModel
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
sealed class DataReference : SumModel, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DataReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<DataReference> =
            when (doc.case())
            {
                "data_reference_boolean"   -> DataReferenceBoolean.fromDocument(doc.nextCase())
                                                as ValueParser<DataReference>
                "data_reference_dice_roll" -> DataReferenceDiceRoll.fromDocument(doc.nextCase())
                                                as ValueParser<DataReference>
                "data_reference_number"    -> DataReferenceNumber.fromDocument(doc.nextCase())
                                                as ValueParser<DataReference>
                else                        -> {
                    Log.d("***DATAREF", doc.toString())
                    effError<ValueError, DataReference>(
                            UnknownCase(doc.case(), doc.path))
                }
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
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<DataReferenceBoolean> = when (doc)
        {
            is DocDict -> BooleanReference.fromDocument(doc) ap {
                              effValue<ValueError, DataReferenceBoolean>(
                                      DataReferenceBoolean(it))
                          }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Sum(this.reference, "boolean")

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
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<DataReferenceDiceRoll> = when (doc)
        {
            is DocDict -> DiceRollReference.fromDocument(doc) ap {
                              effValue<ValueError, DataReferenceDiceRoll>(
                                      DataReferenceDiceRoll(it))
                          }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Sum(this.reference, "dice_roll")

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
        override fun fromDocument(doc : SpecDoc) : ValueParser<DataReferenceNumber> =
                effApply(::DataReferenceNumber, NumberReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> = this.reference.dependencies()


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Sum(this.reference, "number")

    override val sumModelObject = this.reference

}

//
//fun liftDataReference(dataReference : DataReference) : Func<DataReference>
//    = when (dataReference)
//    {
//        is DataReferenceBoolean ->
//        {
//            val booleanRefernece = dataReference.reference
//            when (booleanRefernece)
//            {
//                is BooleanReferenceLiteral  -> Prim(dataReference, "boolean_literal")
//                is BooleanReferenceVariable -> Prim(dataReference, "boolean_variable")
//            }
//        }
//        is DataReferenceDiceRoll ->
//        {
//            val diceRollReference = dataReference.reference
//            when (booleanRefernece)
//            {
//                is BooleanReferenceLiteral  -> Prim(dataReference, "boolean_literal")
//                is BooleanReferenceVariable -> Prim(dataReference, "boolean_variable")
//            }
//        }
//
//
//    }
