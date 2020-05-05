package com.clawmarks.loggy.context

import com.clawmarks.loggy.LoggyComponent
import com.clawmarks.loggy.prefs.LoggyPrefs

interface LoggyContext {
    fun updatePrefs()
    var prefs: LoggyPrefs
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

}