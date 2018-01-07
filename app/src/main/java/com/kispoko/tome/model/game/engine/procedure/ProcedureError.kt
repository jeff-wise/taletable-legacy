
package com.kispoko.tome.model.game.engine.procedure


import com.kispoko.tome.app.ApplicationError
import com.kispoko.tome.model.game.engine.program.ProgramId
import com.kispoko.tome.rts.game.engine.interpreter.EvalError



/**
 * Procedure Error
 */
sealed class ProcedureError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


class ErrorRunningProcedure(val procedureId : ProcedureId, val errorString : String) : ProcedureError()
{
    override fun debugMessage(): String =
            """
            Procedure Error: Error Running Procedure
                Procedure Id: $procedureId
                Error: $errorString
            """

    override fun logMessage(): String = userMessage()
}

