package com.clawmarks.logtracker.data.context

import com.clawmarks.logtracker.data.LoggyComponent
import com.clawmarks.logtracker.data.prefs.LoggyPrefs

interface LoggyContext {
    fun updatePrefs()
    val prefs: LoggyPrefs
    var logLevel: Int
    var isAnalyticsEnabled: Boolean
    var isLogcatCrashEnabled: Boolean
    var isLogcatAppEnabled: Boolean
    var isLogcatFullEnabled: Boolean

    var sendingLevel: Int
    var isSendingEnabled: Boolean
    var isSendingByServerRequest: Boolean
    var isSendingDeferred: Boolean
    var isSendingUrgent: Boolean

    val hasEnoughMemory: Boolean

    fun register(component: LoggyComponent)

    val serialNumber: String
    val terminalId: String
}