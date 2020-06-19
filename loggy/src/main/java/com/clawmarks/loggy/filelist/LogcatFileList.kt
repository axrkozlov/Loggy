package com.clawmarks.loggy.filelist

import com.clawmarks.loggy.context.LoggyContext
import com.clawmarks.loggy.report.ReportType

class LogcatFileList(context: LoggyContext):LoggyFileList(context) {
    override val reportType: ReportType
        get() = ReportType.REGULAR
    override val path: String
        get() = prefs.logcatPath

    init {
        register()
    }

}