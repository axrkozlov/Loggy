package com.clawsmark.logtracker.loggy

import com.clawsmark.logtracker.data.MessageLevel
import com.clawsmark.logtracker.data.AnalyticsMessage
import com.clawsmark.logtracker.loggy.analytics.LoggyAnalytics
import org.koin.core.KoinComponent
import org.koin.core.inject

object Loggy : KoinComponent {
    private val LOGGY_ANALYTICS: LoggyAnalytics by inject()
    private val LOGGY_LOGCAT: LoggyLogcat by inject()
    private val context: LoggyContext by inject()

    fun log(tag: String, message: String, messageLevel: MessageLevel) {
        LOGGY_ANALYTICS.logMessage(AnalyticsMessage(tag, message, messageLevel))
    }

    fun i(tag: String, message: String) {
        LOGGY_ANALYTICS.logMessage(AnalyticsMessage(tag, message, MessageLevel.INFO))
    }

    fun w(tag: String, message: String) {
        LOGGY_ANALYTICS.logMessage(AnalyticsMessage(tag, message, MessageLevel.WARNING))
    }

    fun e(tag: String, message: String) {
        LOGGY_ANALYTICS.logMessage(AnalyticsMessage(tag, message, MessageLevel.ERROR))
    }

    fun updatePrefs() = context.updatePrefs()

}