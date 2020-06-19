package com.clawmarks.loggy.filelist

import com.clawmarks.loggy.context.LoggyContext
import com.clawmarks.loggy.report.ReportType

class AnalyticsFileList(context: LoggyContext):LoggyFileList(context) {
    override val reportType: ReportType
        get() = ReportType.ANALYTIC
    override val path: String
        get() = prefs.analyticsPath

    init {
        register()
    }

}