
package com.kispoko.tome.app


import com.kispoko.tome.rts.game.GameError
import com.kispoko.tome.rts.game.engine.EngineError
import com.kispoko.tome.rts.game.engine.interpreter.EvalError

import com.kispoko.tome.rts.sheet.SheetError
import com.kispoko.tome.rts.sheet.StateError
import com.kispoko.tome.rts.theme.ThemeError


/**
 * Application Error
 */
interface ApplicationError
{
    fun userMessage() : String
    fun debugMessage() : String
    fun logMessage() : String
}


/**
 * App Error
 */
sealed class AppError : ApplicationError
{
    override fun userMessage(): String = "Something went wrong."
}


class AppSheetError(val error : SheetError) : AppError()
{
    override fun debugMessage(): String = error.debugMessage()

    override fun logMessage(): String = error.userMessage()
}


class AppThemeError(val error : ThemeError) : AppError()
{
    override fun debugMessage(): String = "Theme Error: ${error.debugMessage()}"

    override fun logMessage(): String = "Theme Error: ${error.userMessage()}"
}


class AppEngineError(val error : EngineError) : AppError()
{
    override fun debugMessage(): String = error.debugMessage()

    override fun logMessage(): String = error.userMessage()
}


class AppEvalError(val error : EvalError) : AppError()
{
    override fun debugMessage(): String = error.debugMessage()

    override fun logMessage(): String = error.userMessage()
}


class AppGameError(val error : GameError) : AppError()
{
    override fun debugMessage(): String = error.debugMessage()

    override fun logMessage(): String = error.userMessage()
}


class AppStateError(val error : StateError) : AppError()
{
    override fun debugMessage(): String = error.debugMessage()

    override fun logMessage(): String = error.userMessage()
}




