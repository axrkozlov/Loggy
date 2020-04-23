package com.clawsmark.logtracker.data.report

import com.google.gson.Gson
import java.util.*

data class ReportInfo (
        val reportType: ReportType,
        val serialNumber:String,
        val terminalId:String,
        val time:String,
        val causeExceptionInfo: CauseExceptionInfo? = null
){
    fun toJson(): String = Gson().toJson(this)
}

data class CauseExceptionInfo(
    val exception:String,
    val id: UUID,
    val isFatal:Boolean
)