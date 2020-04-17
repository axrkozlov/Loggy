package com.clawsmark.logtracker.loggy

import com.clawsmark.logtracker.data.services.prefs.LoggyPrefs
import com.clawsmark.logtracker.events.Event

interface LoggyContext {
    fun updatePrefs()
    val minAvailableMemoryBytes: Int
    val prefs:LoggyPrefs
    var logLevel: Int
    var isAnalyticsEnabled:Boolean
    var isLogcatCrashEnabled:Boolean
    var isLogcatAppEnabled:Boolean
    var isLogcatFullEnabled:Boolean
    var hasNoEnoughMemory:Boolean

    fun onNewEvent(event:Event)
    fun onComponentFail(component: LoggyComponent)

    fun hasFreeSpace(): Boolean
    fun register(component: LoggyComponent)

}