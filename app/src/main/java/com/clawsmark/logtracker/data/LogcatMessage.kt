package com.clawsmark.logtracker.data

import com.clawsmark.logtracker.data.Message

class LogcatMessage(private val logcatLine: String) : Message{
    override val content: String
        get() = "\"$logcatLine\""
}
