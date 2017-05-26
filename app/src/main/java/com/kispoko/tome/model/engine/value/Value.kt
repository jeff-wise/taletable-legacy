
package com.kispoko.tome.model.engine.value


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import effect.Err
import effect.effApply
import effect.effApply3
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Value Reference
 */
data class ValueReference(override val id : UUID,
                          val valueSetName : Func<ValueSetName>,
                          val valueName : Func<ValueName>) : Model
{

    companion object : Factory<ValueReference>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ValueReference> = when (doc)
        {
            is DocDict -> effApply3(::ValueReference,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // ValueSet Name
                                    doc.at("value_set_name") ap {
                                        effApply(::Prim, ValueSetName.fromDocument(it))
                                    },
                                    // Value Name
                                    doc.at("value_name") ap {
                                        effApply(::Prim, ValueName.fromDocument(it))
                                    })
            else       -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }
}


/**
 * ValueSet Name
 */
data class ValueSetName(val value : String)
{

    companion object : Factory<ValueSetName>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ValueSetName> = when (doc)
        {
            is DocText -> valueResult(ValueSetName(doc.text))
            else       -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}


/**
 * Value Name
 */
data class ValueName(val value : String)
{

    companion object : Factory<ValueName>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ValueName> = when (doc)
        {
            is DocText -> valueResult(ValueName(doc.text))
            else       -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}

