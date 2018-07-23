
package com.taletable.android.rts.entity.sheet


import com.taletable.android.model.engine.procedure.ProcedureInvocation
import com.taletable.android.model.entity.EntityUpdateSheet
import java.io.Serializable




sealed class MessageSheet : Serializable


data class MessageSheetUpdate(val update : EntityUpdateSheet) : MessageSheet()


sealed class MessageSheetAction : MessageSheet()


data class MessageSheetActionRunProcedure(
            val procedureInvocation: ProcedureInvocation) : MessageSheetAction()

