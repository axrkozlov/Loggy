package com.clawmarks.loggy.report

import com.google.gson.Gson
import java.util.*

data class ReportInfo(
        val reportType: ReportType,
        val serialNumber: String,
        val terminalId: String,
        val time: String,
        val causeExceptionInfo: CauseExceptionInfo? = null,
        val reportId: UUID=UUID.randomUUID(),
        val reportVersion: Int = 1
) {
    fun toJson(): String = Gson().toJson(this)
}

data class CauseExceptionInfo(
        val exceptionId: UUID,
        val exception: String,
        val stacktrace: String,
        val isFatal: Boolean
)