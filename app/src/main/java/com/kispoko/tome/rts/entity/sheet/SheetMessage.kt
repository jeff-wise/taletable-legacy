
package com.kispoko.tome.rts.entity.sheet


import com.kispoko.tome.model.game.engine.procedure.ProcedureInvocation
import java.io.Serializable




sealed class MessageSheet : Serializable


data class MessageSheetUpdate(val update : SheetUpdate) : MessageSheet()


sealed class MessageSheetAction : MessageSheet()


data class MessageSheetActionRunProcedure(
            val procedureInvocation: ProcedureInvocation) : MessageSheetAction()

