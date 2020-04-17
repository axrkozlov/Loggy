package com.clawsmark.logtracker.data.services.prefs

import android.content.Context
import android.content.SharedPreferences
import java.text.Format
import kotlin.math.max
import kotlin.math.min

class LoggyPrefsImpl(val preferences: SharedPreferences) : LoggyPrefs {
    override val bufferBlockSize: Int
        get() = 256
    override val maxBufferSize: Int
        get() = 10_000


    override val timeBeforeStartSendingSeconds: Int
        get() = 1
    override val minAvialableMemoryMb: Int
        get() = 200

    override val logcatBufferSizeKb: Int
        get() {
            val value = 1000
            var size = min(value, logcatMinBufferSizeKb)
            size = max(size, logcatMaxBufferSizeKb)
            return size
        }
    override val logcatMinBufferSizeKb: Int
        get() = 100



    override val logcatMaxBufferSizeKb: Int
        get() = 8192

    override val maxFileSizeKb: Int
        get() = 1024

    override val logcatPath: String
        get() = "logger/logcat"
    override val analyticsPath: String
        get() = "logger/analytics"
    override val analyticsFileNameFormat: String
        get() = "%s"
    override val logcatFileNameFormat: String
        get() = "%s"
    override val serialNumber: String
        get() = "1234567890"
    override val terminalId: String
        get() = "31007579"
//    var level = 1
//        set(value) {
//            if (value >= 2) isLogcatEnabled = true
//            if (value >= 3) isLogcatEnabled = true
//            if (value >= 4) isFullLogcatEnabled = true
//
//            field = value
//        }
//    internal var isLogcatCrashEnabled = false
//    internal var isLogcatEnabled = false
//    internal var isFullLogcatEnabled = false

}