package com.clawsmark.logtracker.data

import com.clawsmark.logtracker.utils.currentLogMessageTime
import java.text.SimpleDateFormat
import java.util.*

class TrackMessage(val tag: String, val message: String, val level:Level) {
    val time = currentLogMessageTime()
    override fun toString(): String {
        return "$time ${level.value}/$tag: $message"
    }
}