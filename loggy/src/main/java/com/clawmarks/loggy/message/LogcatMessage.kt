package com.clawmarks.loggy.message

class LogcatMessage(private val logcatLine: String) : Message {
    override val content: String
        get() = "\"$logcatLine\""
}
