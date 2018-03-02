
package com.kispoko.tome.model.game.engine.procedure


import com.kispoko.tome.app.ApplicationError


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

