
package com.kispoko.tome.app


import android.util.Log
import com.kispoko.tome.rts.game.GameError
import com.kispoko.tome.rts.game.engine.EngineError
import com.kispoko.tome.rts.sheet.SheetError
import com.kispoko.tome.rts.sheet.StateError
import effect.Eff
import effect.Identity



// ---------------------------------------------------------------------------------------------
// LOG
// ---------------------------------------------------------------------------------------------

object ApplicationLog
{

    fun error(error : ApplicationError)
    {
        Log.d("***TOME LOG", error.debugMessage())
    }

}


// ---------------------------------------------------------------------------------------------
// EFFECT
// ---------------------------------------------------------------------------------------------

typealias AppEff<A> = Eff<AppError, Identity, A>


// ---------------------------------------------------------------------------------------------
// ERRORS
// ---------------------------------------------------------------------------------------------

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


class AppEngineError(val error : EngineError) : AppError()
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




