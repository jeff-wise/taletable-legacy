
package com.kispoko.tome.model.game.engine.program


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.function.FunctionId
import com.kispoko.tome.model.game.engine.reference.*
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import java.util.*



/**
 * Statement
 */
data class Statement(override val id : UUID,
                     val bindingName : Func<StatementBinding>,
                     val functionId : Func<FunctionId>,
                     val functionParameters : Func<List<StatementParameter>>) : Model
{

    companion object : Factory<Statement>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Statement>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::Statement,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Binding
                         doc.at("binding") ap {
                             effApply(::Prim, StatementBinding.fromDocument(it))
                         },
                         // Function Id
                         doc.at("function_id") ap {
                             effApply(::Prim, FunctionId.fromDocument(it))
                         },
                         // Function Parameters
                         doc.list("function_parameters") ap { docList ->
                             effApply(::Prim,
                                 docList.map { StatementParameter.fromDocument(it) })
                         })
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Statement Binding
 */
data class StatementBinding(val value : String)
{

    companion object : Factory<StatementBinding>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<StatementBinding> = when (doc)
        {
            is DocText -> effValue(StatementBinding(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Statement Parameter
 */
@Suppress("UNCHECKED_CAST")
sealed class StatementParameter
{

    companion object : Factory<StatementParameter>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<StatementParameter> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "binding"   -> StatementParameterBinding.fromDocument(doc)
                                    as ValueParser<StatementParameter>
                    "reference" -> StatementParameterReference.fromDocument(doc)
                                    as ValueParser<StatementParameter>
                    else        -> effError<ValueError,StatementParameter>(
                                            UnknownCase(doc.case(), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

}


/**
 * Binding Parameter
 */
data class StatementParameterBinding(val binding : Func<StatementBinding>) : StatementParameter()
{

    companion object : Factory<StatementParameterBinding>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<StatementParameterBinding> =
            StatementBinding.fromDocument(doc) ap {
                effValue<ValueError,StatementParameterBinding>(StatementParameterBinding(Prim(it)))
            }
    }

}


/**
 * Reference Parameter
 */
data class StatementParameterReference(val reference : Func<DataReference>) : StatementParameter()
{

    companion object : Factory<StatementParameterReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<StatementParameterReference> =
            DataReference.fromDocument(doc) ap {
                effValue<ValueError,StatementParameterReference>(StatementParameterReference(Prim(it)))
            }
    }

}
