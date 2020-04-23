package com.clawsmark.logtracker.data.message

class LogcatMessage(private val logcatLine: String) : Message {
    override val content: String
        get() = "\"$logcatLine\""
}
