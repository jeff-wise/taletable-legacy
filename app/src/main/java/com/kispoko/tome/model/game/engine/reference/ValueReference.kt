
package com.kispoko.tome.model.game.engine.reference


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import effect.Err
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType



/**
 * Value Reference
 */
@Suppress("UNCHECKED_CAST")
sealed class ValueReference
{

    companion object : Factory<ValueReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ValueReference> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "boolean"   -> ValueReferenceBoolean.fromDocument(doc)
                                    as ValueParser<ValueReference>
                    "dice_roll" -> ValueReferenceDiceRoll.fromDocument(doc)
                                    as ValueParser<ValueReference>
                    "number"    -> ValueReferenceNumber.fromDocument(doc)
                                    as ValueParser<ValueReference>
                    else        -> Err<ValueError, DocPath,ValueReference>(
                                            UnknownCase(doc.case()), doc.path)
                }
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

}


/**
 * Boolean Value Reference
 */
data class ValueReferenceBoolean(val reference : Func<BooleanReference>) : ValueReference()
{

    companion object : Factory<ValueReferenceBoolean>
    {
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<ValueReferenceBoolean> = when (doc)
        {
            is DocDict -> BooleanReference.fromDocument(doc) ap {
                              valueResult(ValueReferenceBoolean(Prim(it)))
                          }
            else       -> Err(lulo.value.UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

}


/**
 * Dice Roll Value Reference
 */
data class ValueReferenceDiceRoll(val reference : Func<DiceRollReference>) : ValueReference()
{

    companion object : Factory<ValueReferenceDiceRoll>
    {
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<ValueReferenceDiceRoll> = when (doc)
        {
            is DocDict -> DiceRollReference.fromDocument(doc) ap {
                              valueResult(ValueReferenceDiceRoll(Prim(it)))
                          }
            else       -> Err(lulo.value.UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

}


/**
 * Number Value Reference
 */
data class ValueReferenceNumber(val reference : Func<NumberReference>) : ValueReference()
{

    companion object : Factory<ValueReferenceNumber>
    {
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<ValueReferenceNumber> = when (doc)
        {
            is DocDict -> NumberReference.fromDocument(doc) ap {
                              valueResult(ValueReferenceNumber(Prim(it)))
                          }
            else       -> Err(lulo.value.UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

}
