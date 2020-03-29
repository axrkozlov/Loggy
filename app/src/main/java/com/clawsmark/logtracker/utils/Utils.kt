package com.clawsmark.logtracker.utils

import com.clawsmark.logtracker.tracker.Tracker
import java.text.SimpleDateFormat
import java.util.*

fun trace(tag: String, message: String){
    Tracker.i(tag,message)
}

fun traceWarning(tag: String, message: String){
    Tracker.w(tag,message)
}

fun traceError(tag: String, message: String){
    Tracker.e(tag,message)
}

fun currentLogMessageTime():String{
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    return format.format(Date(System.currentTimeMillis()))
}