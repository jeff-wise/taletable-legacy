
package com.taletable.android.router


import java.util.*



/**
 * Message
 */
sealed class Message


data class MessageUpdateSummationNumberTerm(val id : UUID, val newValue : Double) : Message()
