
package com.kispoko.tome.rts.entity.sheet


import com.kispoko.tome.model.engine.procedure.ProcedureInvocation
import com.kispoko.tome.model.entity.EntityUpdateSheet
import java.io.Serializable




sealed class MessageSheet : Serializable


data class MessageSheetUpdate(val update : EntityUpdateSheet) : MessageSheet()


sealed class MessageSheetAction : MessageSheet()


data class MessageSheetActionRunProcedure(
            val procedureInvocation: ProcedureInvocation) : MessageSheetAction()

