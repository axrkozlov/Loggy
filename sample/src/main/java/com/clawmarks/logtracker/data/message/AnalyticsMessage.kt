package com.clawmarks.logtracker.data.message

import com.clawmarks.logtracker.utils.currentLogMessageTime

class AnalyticsMessage(val tag: String, val message: String, val messageLevel: MessageLevel): Message {
    private val time = currentLogMessageTime()
    override val content: String
        get() = "\"$time ${messageLevel.value}/$tag: $message\""
    val sizeInBytes =content.length * 2
}