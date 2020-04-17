package com.clawsmark.logtracker.loggy.analytics

import com.clawsmark.logtracker.data.AnalyticsMessage
import com.clawsmark.logtracker.data.writer.LoggyAnalyticsWriter
import com.clawsmark.logtracker.loggy.LoggyComponent
import com.clawsmark.logtracker.loggy.LoggyContext

class LoggyAnalytics(override val context: LoggyContext,private val writer: LoggyAnalyticsWriter) : LoggyComponent {

    private var isEnabled = context.isAnalyticsEnabled

    fun logMessage(message: AnalyticsMessage) {
        writer.write(message)
    }

    override fun onPrefsUpdated() {
        isEnabled = context.isAnalyticsEnabled
        if (isEnabled) writer.start()
        else writer.stop()
    }

}