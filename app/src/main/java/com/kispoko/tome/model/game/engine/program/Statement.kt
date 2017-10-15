
package com.kispoko.tome.model.game.engine.program


import android.util.Log
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.functor.Sum
import com.kispoko.tome.lib.functor.getMaybeSum
import com.kispoko.tome.lib.functor.maybeLiftSum
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.model.SumModel
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
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
                     val bindingName : Prim<StatementBindingName>,
                     val functionId : Prim<FunctionId>,
                     val parameter1 : Sum<StatementParameter>,
                     val parameter2 : Maybe<Sum<StatementParameter>>,
                     val parameter3 : Maybe<Sum<StatementParameter>>,
                     val parameter4 : Maybe<Sum<StatementParameter>>,
                     val parameter5 : Maybe<Sum<StatementParameter>>)
                      : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(bindingName : StatementBindingName,
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
        override fun fromDocument(doc: SchemaDoc): ValueParser<Statement> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Statement,
                         // Binding
                         doc.at("binding_name") ap { StatementBindingName.fromDocument(it) },
                         // Function Id
                         doc.at("function_id") ap { FunctionId.fromDocument(it) },
                         // Parameter 1
                         doc.at("parameter1") ap { StatementParameter.fromDocument(it) },
                         // Parameter 2
                         split(doc.maybeAt("parameter2"),
                               effValue<ValueError,Maybe<StatementParameter>>(Nothing()),
                               { effApply(::Just, StatementParameter.fromDocument(it)) }),
                         // Parameter 3
                         split(doc.maybeAt("parameter3"),
                               effValue<ValueError,Maybe<StatementParameter>>(Nothing()),
                               { effApply(::Just, StatementParameter.fromDocument(it)) }),
                         // Parameter 4
                         split(doc.maybeAt("parameter4"),
                               effValue<ValueError,Maybe<StatementParameter>>(Nothing()),
                               { effApply(::Just, StatementParameter.fromDocument(it)) }),
                         // Parameter 5
                         split(doc.maybeAt("parameter5"),
                               effValue<ValueError,Maybe<StatementParameter>>(Nothing()),
                               { effApply(::Just, StatementParameter.fromDocument(it)) })
                         )
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "binding_name" to this.bindingName().toDocument(),
        "function_id" to this.functionId().toDocument(),
        "parameter_1" to this.parameter1().toDocument()
        ))
        .maybeMerge(this.parameter2().apply {
            Just(Pair("parameter2", it.toDocument())) })
        .maybeMerge(this.parameter3().apply {
            Just(Pair("parameter3", it.toDocument())) })
        .maybeMerge(this.parameter4().apply {
            Just(Pair("parameter4", it.toDocument())) })
        .maybeMerge(this.parameter5().apply {
            Just(Pair("parameter5", it.toDocument())) })

    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun bindingName() : StatementBindingName = this.bindingName.value

    fun bindingNameString() : String = this.bindingName.value.value

    fun functionId() : FunctionId = this.functionId.value

    fun parameter1() : StatementParameter = this.parameter1.value

    fun parameter2() : Maybe<StatementParameter> = getMaybeSum(this.parameter2)

    fun parameter3() : Maybe<StatementParameter> = getMaybeSum(this.parameter3)

    fun parameter4() : Maybe<StatementParameter> = getMaybeSum(this.parameter4)

    fun parameter5() : Maybe<StatementParameter> = getMaybeSum(this.parameter5)


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
data class StatementBindingName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<StatementBindingName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<StatementBindingName> = when (doc)
        {
            is DocText -> effValue(StatementBindingName(doc.text))
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

    override fun asSQLValue() = SQLText({this.value})
}


/**
 * Statement Parameter
 */
@Suppress("UNCHECKED_CAST")
sealed class StatementParameter : ToDocument, SumModel, Serializable
{

    companion object : Factory<StatementParameter>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<StatementParameter> =
            when (doc.case())
            {
                "statement_binding"       -> StatementParameterBindingName.fromDocument(doc.nextCase())
                                                as ValueParser<StatementParameter>
                "program_parameter_index" -> StatementParameterProgramParameter.fromDocument(doc.nextCase())
                                                as ValueParser<StatementParameter>
                "data_reference"          -> StatementParameterReference.fromDocument(doc.nextCase())
                                                as ValueParser<StatementParameter>
                else                      ->
                {

                    Log.d("***STATEMNET", doc.toString())
                    effError<ValueError, StatementParameter>(
                            UnknownCase(doc.case(), doc.path))
                }
            }
    }

}


/**
 * Binding Parameter
 */
data class StatementParameterBindingName(val bindingName : StatementBindingName)
            : StatementParameter(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<StatementParameterBindingName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<StatementParameterBindingName> =
                effApply(::StatementParameterBindingName, StatementBindingName.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.bindingName.toDocument().withCase("statement_binding")


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "bindingNameString")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.bindingName.asSQLValue()

}


/**
 * Program Parameter Reference
 */
data class StatementParameterProgramParameter(val index : ProgramParameterIndex)
    : StatementParameter()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<StatementParameterProgramParameter>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<StatementParameterProgramParameter> =
                effApply(::StatementParameterProgramParameter,
                           ProgramParameterIndex.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.index.toDocument().withCase("program_parameter_index")


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this.index)

    override val sumModelObject = this

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
        override fun fromDocument(doc: SchemaDoc): ValueParser<StatementParameterReference> =
                effApply(::StatementParameterReference, DataReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.reference.toDocument().withCase("data_reference")


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Sum(this.reference, "data_reference")

    override val sumModelObject = this.reference

}


