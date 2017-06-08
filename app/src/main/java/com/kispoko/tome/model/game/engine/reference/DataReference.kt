
package com.kispoko.tome.model.game.engine.reference


import com.kispoko.tome.lib.Factory
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
sealed class DataReference
{

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


    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    abstract fun dependencies(): Set<VariableReference>

}


/**
 * Boolean Value Reference
 */
data class DataReferenceBoolean(val reference : BooleanReference) : DataReference()
{

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


    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> = this.reference.dependencies()
}


/**
 * Dice Roll Value Reference
 */
data class DataReferenceDiceRoll(val reference : DiceRollReference) : DataReference()
{

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


    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> = this.reference.dependencies()

}


/**
 * Number Value Reference
 */
data class DataReferenceNumber(val reference : NumberReference) : DataReference()
{

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


    override fun dependencies(): Set<VariableReference> = this.reference.dependencies()

}
