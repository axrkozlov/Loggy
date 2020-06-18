package com.clawmarks.loggy.context

import com.clawmarks.loggy.LoggyComponent
import com.clawmarks.loggy.LoggyContextComponent
import com.clawmarks.loggy.prefs.LoggyPrefs

interface LoggyContext:LoggyComponent {
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

    var hasSendingPermission: Boolean

    fun register(component: LoggyContextComponent)

    val fileExtension :String
    get() = if (prefs.isCompressionEnabled) "zlib"
    else "log"

}