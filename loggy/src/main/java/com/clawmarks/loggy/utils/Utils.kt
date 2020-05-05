package com.clawmarks.loggy.utils

import com.clawmarks.loggy.Loggy
import java.text.SimpleDateFormat
import java.util.*

fun loggy(tag: String, message: String){
    Loggy.i(tag,message)
}

fun loggyw(tag: String, message: String){
    Loggy.w(tag,message)
}

fun loggye(tag: String, message: String){
    Loggy.e(tag,message)
}

internal fun currentLogFinalTime():String{
    val format = SimpleDateFormat("ddMMyy_HHmmssSSS", Locale.getDefault())
    return format.format(Date(System.currentTimeMillis()))
}

internal fun currentLogMessageTime():String{
    val format = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault())
    return format.format(Date(System.currentTimeMillis()))
}