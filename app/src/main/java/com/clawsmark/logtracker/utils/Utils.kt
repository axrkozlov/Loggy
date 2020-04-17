package com.clawsmark.logtracker.utils

import com.clawsmark.logtracker.loggy.Loggy
import java.text.SimpleDateFormat
import java.util.*

fun trace(tag: String, message: String){
    Loggy.i(tag,message)
}

fun traceWarning(tag: String, message: String){
    Loggy.w(tag,message)
}

fun traceError(tag: String, message: String){
    Loggy.e(tag,message)
}

fun currentLogFileNameTime():String{
    val format = SimpleDateFormat("ddMMyy_HHmmss", Locale.getDefault())
    return format.format(Date(System.currentTimeMillis()))
}

fun currentLogMessageTime():String{
    val format = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault())
    return format.format(Date(System.currentTimeMillis()))
}