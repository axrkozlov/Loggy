package com.clawsmark.logtracker.data.context

import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.clawsmark.logtracker.data.LoggyComponent
import com.clawsmark.logtracker.data.prefs.LoggyPrefs

class LoggyContextImpl(override val prefs: LoggyPrefs) : LoggyContext {


    private val components = HashMap<String, LoggyComponent>()

    private fun hasFreeSpace() = StatFs(Environment.getExternalStorageDirectory().path).availableBytes < minAvailableMemoryBytes
    override fun register(component: LoggyComponent) {
        components[component.componentName] = component
    }

    /**
     * level
     * 0 - disabled
     * 1 - analytics
     * 2 - logcat on crash
     * 3 - logcat of app
     * 4 - full logcat */
    override var logLevel: Int = 2
        set(value) {
            isAnalyticsEnabled = value >= 1
            isLogcatCrashEnabled = value >= 2
            isLogcatAppEnabled = value >= 3
            isLogcatFullEnabled = value ==80
            field = value
        }
    override var isAnalyticsEnabled: Boolean = false
    override var isLogcatCrashEnabled: Boolean = false
    override var isLogcatAppEnabled: Boolean = false
    override var isLogcatFullEnabled: Boolean = false

    override var sendingLevel: Int = 2
        set(value) {
            isSendingEnabled = value >= 1
            isSendingByServerRequest = value == 1
            isSendingDeferred = value == 2
            isSendingUrgent = value ==80
            field = value
        }

    override var isSendingEnabled: Boolean = false
    override var isSendingByServerRequest: Boolean = false
    override var isSendingDeferred: Boolean = false
    override var isSendingUrgent: Boolean = false

    override val hasEnoughMemory: Boolean
        get() {
            val value = StatFs(Environment.getExternalStorageDirectory().path).availableBytes > minAvailableMemoryBytes
            if (!hasNoEnoughMemory && !value) Log.i("LoggyContextImpl", "There is no enough memory for Loggy :(")
            hasNoEnoughMemory = !value
            return value
        }
    private val minAvailableMemoryBytes: Int
        get() = prefs.minAvialableMemoryMb * 1024 * 1024

    private var hasNoEnoughMemory: Boolean = false

    override fun updatePrefs() {
        logLevel = prefs.logLevel
        sendingLevel =prefs.sendingLevel
        components.forEach {
            it.value.onPrefsUpdated()
        }
    }

    override val serialNumber: String
        get() = "1234567890"
    override val terminalId: String
        get() = "31007579"


}