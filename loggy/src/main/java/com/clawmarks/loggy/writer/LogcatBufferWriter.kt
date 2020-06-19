package com.clawmarks.loggy.writer

import com.clawmarks.loggy.context.LoggyContext
import com.clawmarks.loggy.filelist.LoggyFileListState
import com.clawmarks.loggy.report.ReportType

class LogcatBufferWriter(context: LoggyContext, loggyFileListState: LoggyFileListState): BufferWriter(context,loggyFileListState) {

    override val reportType: ReportType
        get() = ReportType.REGULAR

    override val path: String
        get() = prefs.logcatPath

    init {
        register()
    }
}