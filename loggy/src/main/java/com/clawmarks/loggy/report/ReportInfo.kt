package com.clawmarks.loggy.report

import android.hardware.SensorAdditionalInfo
import com.google.gson.Gson
import java.util.*

data class ReportInfo(
        val reportVersion: Int = 1,
        val reportType: ReportType,
        val deviceId: String,
        val userId: String,
        val time: String,
        val causeExceptionInfo: CauseExceptionInfo? = null,
        val extra: Map<String,String>,
        val reportId: UUID=UUID.randomUUID()
) {
    fun toJson(): String = Gson().toJson(this)
}

data class CauseExceptionInfo(
        val exceptionId: UUID,
        val exception: String,
        val stacktrace: String,
        val isFatal: Boolean
)