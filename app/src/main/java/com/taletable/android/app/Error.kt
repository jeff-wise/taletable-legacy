
package com.taletable.android.app


import com.kispoko.culebra.YamlParseError
import com.taletable.android.db.DatabaseError
import com.taletable.android.official.OfficialError
import com.taletable.android.rts.entity.EntityError
import com.taletable.android.rts.entity.campaign.CampaignError
import com.taletable.android.rts.entity.game.GameError
import com.taletable.android.rts.entity.engine.EngineError
import com.taletable.android.rts.entity.engine.interpreter.EvalError

import com.taletable.android.rts.entity.sheet.SheetError
import com.taletable.android.rts.entity.sheet.StateError
import com.taletable.android.rts.entity.theme.ThemeError


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


class AppVoidError() : AppError()
{
    override fun debugMessage(): String = "void"

    override fun logMessage(): String = "void"
}


class AppDBError(val error : DatabaseError) : AppError()
{
    override fun debugMessage(): String = error.debugMessage()

    override fun logMessage(): String = error.userMessage()
}


class AppEntityError(val error : EntityError) : AppError()
{
    override fun debugMessage(): String = error.debugMessage()

    override fun logMessage(): String = error.userMessage()
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


class AppCampaignError(val error : CampaignError) : AppError()
{
    override fun debugMessage(): String = error.debugMessage()

    override fun logMessage(): String = error.userMessage()
}


class AppStateError(val error : StateError) : AppError()
{
    override fun debugMessage(): String = error.debugMessage()

    override fun logMessage(): String = error.userMessage()
}


class AppOfficialError(val error : OfficialError) : AppError()
{
    override fun debugMessage(): String = error.debugMessage()

    override fun logMessage(): String = error.userMessage()
}


class AppYamlError(val error : YamlParseError) : AppError()
{
    override fun debugMessage(): String = error.toString()

    override fun logMessage(): String = error.toString()
}



