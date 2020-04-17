package com.clawsmark.logtracker.data

import com.google.gson.Gson
import java.lang.Exception
import java.util.*

class ReportInfo (
        reportType: ReportType,
        serialNumber:String,
        terminalId:String,
        time:String,
        causeExceptionInfo:CauseExceptionInfo? = null
){
    fun toJson(): String = Gson().toJson(this)
}

class CauseExceptionInfo(
    val exception:String,
    val id: UUID,
    val isFatal:Boolean
)