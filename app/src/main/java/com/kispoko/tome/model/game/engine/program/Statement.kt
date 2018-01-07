
package com.kispoko.tome.model.game.engine.program


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.AppEvalError
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue7
import com.kispoko.tome.lib.orm.SumType
import com.kispoko.tome.lib.orm.schema.*
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.model.game.engine.EngineValue
import com.kispoko.tome.model.game.engine.function.*
import com.kispoko.tome.model.game.engine.reference.*
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.game.engine.interpreter.BindingDoesNotExist
import com.kispoko.tome.rts.game.engine.interpreter.ProgramParameterDoesNotExist
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetData
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
                     val bindingName : StatementBindingName,
                     val functionId : FunctionId,
                     val parameter1 : Maybe<StatementParameter>,
                     val parameter2 : Maybe<StatementParameter>,
                     val parameter3 : Maybe<StatementParameter>,
                     val parameter4 : Maybe<StatementParameter>,
                     val parameter5 : Maybe<StatementParameter>)
                      : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(bindingName : StatementBindingName,
                functionId : FunctionId,
                parameter1 : Maybe<StatementParameter>,
                parameter2 : Maybe<StatementParameter>,
                parameter3 : Maybe<StatementParameter>,
                parameter4 : Maybe<StatementParameter>,
                parameter5 : Maybe<StatementParameter>)
        : this(UUID.randomUUID(),
               bindingName,
               functionId,
               parameter1,
               parameter2,
               parameter3,
               parameter4,
               parameter5)


    companion object : Factory<Statement>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Statement> = when (doc)
        {
            is DocDict ->
            {
                apply(::Statement,
                      // Binding
                      doc.at("binding_name") ap { StatementBindingName.fromDocument(it) },
                      // Function Id
                      doc.at("function_id") ap { FunctionId.fromDocument(it) },
                      // Parameter 1
                      split(doc.maybeAt("parameter1"),
                            effValue<ValueError,Maybe<StatementParameter>>(Nothing()),
                            { effApply(::Just, StatementParameter.fromDocument(it)) }),
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
        "function_id" to this.functionId().toDocument()
        ))
        .maybeMerge(this.parameter1().apply {
            Just(Pair("parameter1", it.toDocument())) })
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

    fun bindingName() : StatementBindingName = this.bindingName


    fun bindingNameString() : String = this.bindingName.value


    fun functionId() : FunctionId = this.functionId


    fun parameter1() : Maybe<StatementParameter> = this.parameter1


    fun parameter2() : Maybe<StatementParameter> = this.parameter2


    fun parameter3() : Maybe<StatementParameter> = this.parameter3


    fun parameter4() : Maybe<StatementParameter> = this.parameter4


    fun parameter5() : Maybe<StatementParameter> = this.parameter5


    // -----------------------------------------------------------------------------------------
    // PROD MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_StatementValue =
        RowValue7(statementTable, PrimValue(this.bindingName),
                                  PrimValue(this.functionId),
                                  MaybeSumValue(this.parameter1),
                                  MaybeSumValue(this.parameter2),
                                  MaybeSumValue(this.parameter3),
                                  MaybeSumValue(this.parameter4),
                                  MaybeSumValue(this.parameter5))


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value(programParameterValues : ProgramParameterValues,
              bindings : Map<String, EngineValue>,
              programId : ProgramId,
              sheetContext : SheetContext) : AppEff<EngineValue>
    {


        return if (isPlatformFunction(this.functionId))
        {
            val params = this.platformFunctionParameters(programParameterValues,
                                                         bindings,
                                                         programId,
                                                         sheetContext)
            params.apply {
                runPlatformFunction(this.functionId, it)
            }
        }
        else
        {
            val paramsEff = this.functionParameters(programParameterValues,
                                                    bindings,
                                                    programId,
                                                    sheetContext)

            GameManager.engine(sheetContext.gameId) ap { engine ->
            engine.function(this.functionId())      ap { function ->
            paramsEff                               ap { params ->
                function.value(params)
            } } }
        }
    }


    fun functionParameters(programParameterValues : ProgramParameterValues,
                           bindings : Map<String,EngineValue>,
                           programId : ProgramId,
                           sheetContext : SheetContext) : AppEff<FunctionParameters>
    {
        val statementParameter1 = this.parameter1()
        val parameter1 = when (statementParameter1) {
            is Just -> this.statementParameterValue(statementParameter1.value,
                                                    programParameterValues,
                                                    bindings,
                                                    programId,
                                                    sheetContext)
            else    -> effValue(Nothing())
        }

        val statementParameter2 = this.parameter2()
        val parameter2 = when (statementParameter2) {
            is Just -> this.statementParameterValue(statementParameter2.value,
                                                    programParameterValues,
                                                    bindings,
                                                    programId,
                                                    sheetContext)
            else    -> effValue(Nothing())
        }

        val statementParameter3 = this.parameter3()
        val parameter3 = when (statementParameter3) {
            is Just -> this.statementParameterValue(statementParameter3.value,
                                                       programParameterValues,
                                                       bindings,
                                                       programId,
                                                       sheetContext)
            else    -> effValue(Nothing())
        }

        val statementParameter4 = this.parameter4()
        val parameter4 = when (statementParameter4) {
            is Just -> this.statementParameterValue(statementParameter4.value,
                                                       programParameterValues,
                                                       bindings,
                                                       programId,
                                                       sheetContext)
            else    -> effValue(Nothing())
        }

        val statementParameter5 = this.parameter5()
        val parameter5 = when (statementParameter5) {
            is Just -> this.statementParameterValue(statementParameter5.value,
                                                       programParameterValues,
                                                       bindings,
                                                       programId,
                                                       sheetContext)
            else    -> effValue(Nothing())
        }

        return parameter1 ap { maybeParameter1 ->
            when (maybeParameter1)
            {
                is Just -> apply(::FunctionParameters,
                                 effValue(maybeParameter1.value),
                                 parameter2,
                                 parameter3,
                                 parameter4,
                                 parameter5)
                else -> effError<AppError,FunctionParameters>(
                                AppEvalError(ProgramParameterDoesNotExist(1, programId)))
            }
        }
    }


    private fun platformFunctionParameters(programParameterValues : ProgramParameterValues,
                                           bindings : Map<String,EngineValue>,
                                           programId : ProgramId,
                                           sheetContext : SheetContext)
                                            : AppEff<PlatformFunctionParameters>
    {
        val justParams = listOf(this.parameter1,
                                this.parameter2,
                                this.parameter3,
                                this.parameter4,
                                this.parameter5)
                                .filterJust()

        return justParams.mapM {
            this.statementParameterValue(it,
                                         programParameterValues,
                                         bindings,
                                         programId,
                                         sheetContext)
        }
        .apply { effValue<AppError,PlatformFunctionParameters>(PlatformFunctionParameters(it.filterJust())) }
    }


    private fun statementParameterValue(statementParameter : StatementParameter,
                                        programParameterValues : ProgramParameterValues,
                                        bindings : Map<String,EngineValue>,
                                        programId : ProgramId,
                                        sheetContext : SheetContext) : AppEff<Maybe<EngineValue>> =
        when (statementParameter)
        {
            is StatementParameterBindingName ->
            {
                val bindingName = statementParameter.bindingName.value
                if (bindings.containsKey(bindingName))
                    effValue<AppError,Maybe<EngineValue>>(Just(bindings.get(bindingName)!!))
                else
                    effError<AppError,Maybe<EngineValue>>(AppEvalError(BindingDoesNotExist(bindingName, programId)))
            }
            is StatementParameterProgramParameter ->
            {
                val parameterIndex = statementParameter.index.value
                val parameter = programParameterValues.atIndex(parameterIndex)
                when (parameter) {
                    is Just -> effValue<AppError,Maybe<EngineValue>>(Just(parameter.value))
                    else    -> effError<AppError,Maybe<EngineValue>>(AppEvalError(
                                        ProgramParameterDoesNotExist(parameterIndex, programId)))
                }
            }
            is StatementParameterReference ->
            {
                SheetData.referenceEngineValue(statementParameter.reference, sheetContext)
            }
        }


}


data class StatementBinding(val name : String, val value : EngineValue)


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
sealed class StatementParameter : ToDocument, SumType, Serializable
{

    companion object : Factory<StatementParameter>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<StatementParameter> =
            when (doc.case())
            {
                "statement_binding_name"  -> StatementParameterBindingName.fromDocument(doc.nextCase())
                                                as ValueParser<StatementParameter>
                "program_parameter_index" -> StatementParameterProgramParameter.fromDocument(doc.nextCase())
                                                as ValueParser<StatementParameter>
                "data_reference"          -> StatementParameterReference.fromDocument(doc.nextCase())
                                                as ValueParser<StatementParameter>
                else                      -> effError(UnknownCase(doc.case(), doc.path))
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

    override fun toDocument() = this.bindingName.toDocument().withCase("statement_binding_name")


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "binding_name"


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

    override fun columnValue() = PrimValue(this.index)


    override fun case() = "program_parameter"


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

    override fun columnValue() = SumValue(this.reference)


    override fun case() = "data_reference"


    override val sumModelObject = this.reference

}


