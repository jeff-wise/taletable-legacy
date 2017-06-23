
package com.kispoko.tome.model.game.engine.reference


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Sum
import com.kispoko.tome.lib.model.SumModel
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.model.game.engine.variable.VariableReference
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType



/**
 * Value Reference
 */
@Suppress("UNCHECKED_CAST")
sealed class DataReference : SumModel
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DataReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<DataReference> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "boolean"   -> DataReferenceBoolean.fromDocument(doc)
                                    as ValueParser<DataReference>
                    "dice_roll" -> DataReferenceDiceRoll.fromDocument(doc)
                                    as ValueParser<DataReference>
                    "number"    -> DataReferenceNumber.fromDocument(doc)
                                    as ValueParser<DataReference>
                    else        -> effError<ValueError, DataReference>(
                                            UnknownCase(doc.case(), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
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
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<DataReferenceNumber> = when (doc)
        {
            is DocDict -> NumberReference.fromDocument(doc) ap {
                              effValue<ValueError, DataReferenceNumber>(DataReferenceNumber(it))
                          }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
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
