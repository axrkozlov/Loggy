package com.clawmarks.loggy.writer

import com.clawmarks.loggy.context.LoggyContext
import com.clawmarks.loggy.filelist.LoggyFileListState
import com.clawmarks.loggy.report.ReportType

class AnalyticsBufferWriter(context: LoggyContext, loggyFileListState: LoggyFileListState): BufferWriter(context,loggyFileListState) {

    override val reportType: ReportType
        get() = ReportType.ANALYTIC

    override val path: String
        get() = prefs.analyticsPath

    init {
        register()
    }
}