package com.clawmarks.loggy.context

import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.clawmarks.loggy.LoggyContextComponent
import com.clawmarks.loggy.prefs.LoggyPrefs

class LoggyContextImpl(override var prefs: LoggyPrefs) : LoggyContext {

    private val components = HashMap<String, LoggyContextComponent>()

    override fun register(component: LoggyContextComponent) {
        components[component.componentName] = component
    }

    override var logLevel: Int = 2
        set(value) {
            isAnalyticsEnabled = value >= 1
            isLogcatCrashEnabled = value >= 2
            isLogcatAppEnabled = value >= 3
            isLogcatFullEnabled = value == 80
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
            isSendingUrgent = value == 80
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
    override var hasSendingPermission: Boolean = false

    private val minAvailableMemoryBytes: Int
        get() = prefs.minAvailableMemoryMb * 1024 * 1024

    private var hasNoEnoughMemory: Boolean = false

    override fun updatePrefs() {
        logLevel = prefs.logLevel
        sendingLevel = prefs.sendingLevel
        components.forEach {
            it.value.onPrefsUpdated()
        }
    }



}