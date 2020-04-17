package com.clawsmark.logtracker.loggy

import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.clawsmark.logtracker.data.services.prefs.LoggyPrefs
import com.clawsmark.logtracker.events.Event

class LoggyContextImpl(override val prefs: LoggyPrefs) : LoggyContext {

    override val minAvailableMemoryBytes: Int
        get() = prefs.minAvialableMemoryMb * 1024 * 1024

    override fun hasFreeSpace() = StatFs(Environment.getExternalStorageDirectory().path).availableBytes < minAvailableMemoryBytes
    override fun register(component: LoggyComponent) {

    }

    /**
     * level
     * 0 - disabled
     * 1 - analytics
     * 2 - logcat on crash
     * 3 - logcat of app
     * 4 - full logcat */
    override var logLevel: Int = 1
    override var isAnalyticsEnabled: Boolean
        get() = true
        set(value) {}
    override var isLogcatCrashEnabled: Boolean
        get() = true
        set(value) {}
    override var isLogcatAppEnabled: Boolean
        get() = true
        set(value) {}
    override var isLogcatFullEnabled: Boolean
        get() = true
        set(value) {}
    override var hasNoEnoughMemory: Boolean = false
        set(value) {
            if (value and value != field) reportHasNoEnoughMemory()
            field = value
        }

    private fun reportHasNoEnoughMemory() {
        Log.i("LoggyContextImpl", "reportHasNoEnoughMemory: ")
    }

    override fun onNewEvent(event: Event) {

    }

    override fun onComponentFail(component: LoggyComponent) {
        when (component) {
            is LoggyComponent -> {

            }
        }
    }


    var level = 1
    override fun updatePrefs() {

    }

//        set(value) {
//            if (value >= 1) Loggy.isEnabled = true
//            LogCatLoggy.level = value
//            field = value
//        }


}