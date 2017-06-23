
package com.kispoko.tome.model.game.engine.program


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.functor.Sum
import com.kispoko.tome.lib.functor.maybeLiftSum
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.model.SumModel
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.EngineValueType
import com.kispoko.tome.model.game.engine.function.FunctionId
import com.kispoko.tome.model.game.engine.reference.*
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Statement
 */
data class Statement(override val id : UUID,
                     val bindingName : Prim<StatementBinding>,
                     val functionId : Prim<FunctionId>,
                     val parameter1 : Sum<StatementParameter>,
                     val parameter2 : Maybe<Sum<StatementParameter>>,
                     val parameter3 : Maybe<Sum<StatementParameter>>,
                     val parameter4 : Maybe<Sum<StatementParameter>>,
                     val parameter5 : Maybe<Sum<StatementParameter>>) : Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(bindingName : StatementBinding,
                functionId : FunctionId,
                parameter1 : StatementParameter,
                parameter2 : Maybe<StatementParameter>,
                parameter3 : Maybe<StatementParameter>,
                parameter4 : Maybe<StatementParameter>,
                parameter5 : Maybe<StatementParameter>)
        : this(UUID.randomUUID(),
               Prim(bindingName),
               Prim(functionId),
               Sum(parameter1),
               maybeLiftSum(parameter2),
               maybeLiftSum(parameter3),
               maybeLiftSum(parameter4),
               maybeLiftSum(parameter5))


    companion object : Factory<Statement>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Statement>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::Statement,
                         // Binding
                         doc.at("binding") ap { StatementBinding.fromDocument(it) },
                         // Function Id
                         doc.at("function_id") ap { FunctionId.fromDocument(it) },
                         // Parameter 1
                         doc.at("parameter_1") ap { StatementParameter.fromDocument(it) },
                         // Parameter 2
                         split(doc.maybeAt("parameter_2"),
                               effValue<ValueError,Maybe<StatementParameter>>(Nothing()),
                               { effApply(::Just, StatementParameter.fromDocument(it)) }),
                         // Parameter 3
                         split(doc.maybeAt("parameter_3"),
                               effValue<ValueError,Maybe<StatementParameter>>(Nothing()),
                               { effApply(::Just, StatementParameter.fromDocument(it)) }),
                         // Parameter 4
                         split(doc.maybeAt("parameter_4"),
                               effValue<ValueError,Maybe<StatementParameter>>(Nothing()),
                               { effApply(::Just, StatementParameter.fromDocument(it)) }),
                         // Parameter 5
                         split(doc.maybeAt("parameter_5"),
                               effValue<ValueError,Maybe<StatementParameter>>(Nothing()),
                               { effApply(::Just, StatementParameter.fromDocument(it)) })
                         )
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // PROD MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "statement"

    override val modelObject = this

}


/**
 * Statement Binding
 */
data class StatementBinding(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<StatementBinding>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<StatementBinding> = when (doc)
        {
            is DocText -> effValue(StatementBinding(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLText({this.value})
}


/**
 * Statement Parameter
 */
@Suppress("UNCHECKED_CAST")
sealed class StatementParameter : SumModel
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
data class StatementParameterBinding(val binding : StatementBinding)
            : StatementParameter(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<StatementParameterBinding>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<StatementParameterBinding> =
                effApply(::StatementParameterBinding, StatementBinding.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "binding")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.binding.asSQLValue()

}


/**
 * Reference Parameter
 */
data class StatementParameterReference(val reference : DataReference) : StatementParameter()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<StatementParameterReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<StatementParameterReference> =
                effApply(::StatementParameterReference, DataReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Sum(this.reference, "data_reference")

    override val sumModelObject = this.reference

}
