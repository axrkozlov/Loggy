package com.clawsmark.logtracker.data.services.prefs

import android.content.Context
import android.content.SharedPreferences
import java.text.Format

class LoggyPrefsImpl(val preferences: SharedPreferences) : LoggyPrefs {
    override val bufferBlockSize: Int
        get() = 256
    override val maxBufferSize: Int
        get() = 10_000

    override val timeBeforeStartSendingSeconds: Int
        get() = 1
    override val maxLogLinesInFile: Int
        get() = 10
    override val minAvialableMemoryMb: Int
        get() = 200
    override val logcatBufferSizeKb: Int
        get() = 1000
    override val logcatMaxBufferSizeKb: Int
        get() = 8024
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


}