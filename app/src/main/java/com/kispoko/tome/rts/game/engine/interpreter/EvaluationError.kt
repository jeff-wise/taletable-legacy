
package com.kispoko.tome.rts.game.engine.interpreter


import com.kispoko.tome.app.ApplicationError
import com.kispoko.tome.model.game.engine.EngineValueType
import com.kispoko.tome.model.game.engine.function.FunctionId
import com.kispoko.tome.model.game.engine.function.FunctionParameters
import com.kispoko.tome.model.game.engine.program.ProgramId



/**
 * Evaluation Error
 */
sealed class EvalError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


class ProgramDoesNotExist(val programId : ProgramId) : EvalError()
{
    override fun debugMessage(): String =
            """
            Eval Error: Program Does Not Exist
                Program Id: $programId
            """

    override fun logMessage(): String = userMessage()
}


class PlatformFunctionDoesNotExist(val functionId : FunctionId) : EvalError()
{
    override fun debugMessage(): String =
            """
            Eval Error: Platform Function Does Not Exist
                Function Id: $functionId
            """

    override fun logMessage(): String = userMessage()
}


class BindingDoesNotExist(val bindingName : String, val programId : ProgramId) : EvalError()
{
    override fun debugMessage(): String =
            """
            Eval Error: Binding Does Not Exist
                Binding Name: $bindingName
                Program Id: $programId
            """

    override fun logMessage(): String = userMessage()
}


class ProgramParameterDoesNotExist(val parameterIndex : Int,
                                   val programId : ProgramId) : EvalError()
{
    override fun debugMessage(): String =
            """
            Eval Error: Program Parameter Does Not Exist (at index)
                Parameter Index: $parameterIndex
                Program Id: $programId
            """

    override fun logMessage(): String = userMessage()
}


class FunctionNotDefinedForParameters(val functionId : FunctionId,
                                      val parameters : FunctionParameters) : EvalError()
{
    override fun debugMessage(): String =
            """
            Eval Error: Function Is Not Defined For Parameters
                Function Id: $functionId
                Parameters: $parameters
            """

    override fun logMessage(): String = userMessage()
}


class ResultBindingDoesNotExist(val bindingName : String,
                                val programId : ProgramId) : EvalError()
{
    override fun debugMessage(): String =
            """
            Eval Error: Result Binding Does Not Exist
                Binding Name: $bindingName
                Program Id: $programId
            """

    override fun logMessage(): String = userMessage()
}


class UnexpectedProgramResultType(val programId : ProgramId,
                                  val actualType : EngineValueType,
                                  val expectedtype : EngineValueType) : EvalError()
{
    override fun debugMessage(): String =
            """
            Eval Error: Unexpected Program Result Type
                Program Id: $programId
                Actual Type: $actualType
                Expected Type: $expectedtype
            """

    override fun logMessage(): String = userMessage()
}
