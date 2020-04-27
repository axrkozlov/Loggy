package com.clawmarks.logtracker

import android.content.SharedPreferences
import android.os.Environment
import com.clawmarks.loggy.prefs.LoggyPrefs
import java.io.File
import kotlin.math.max
import kotlin.math.min

class LoggyPrefsImpl() : LoggyPrefs {
    override val bufferBlockSize: Int
        get() = 256
    override val maxBufferSize: Int
        get() = 10_000
    override val logLevel: Int
        get() = 3
    override val sendingLevel: Int
        get() = 2
    override val sendingIntervalMin: Long
        get() = 0
    override val pauseBetweenFileSendingSec: Long
        get() = 1


    override val timeBeforeStartSendingSeconds: Int
        get() = 1
    override val minAvialableMemoryMb: Int
        get() = 200

    override val logcatBufferSizeKb: Int
        get() {
            val value = 1000
            var size = max(value, logcatMinBufferSizeKb)
            size = min(size, logcatMaxBufferSizeKb)
            return size
        }
    override val logcatMinBufferSizeKb: Int
        get() = 100



    override val logcatMaxBufferSizeKb: Int
        get() = 8192

    override val maxFileSizeKb: Int
        get() = 64
    override val dirSizeMb: Int
        get() = 4
    override val maxDirSizeMb: Int
        get() = 8
    private val sdCard: File = Environment.getExternalStorageDirectory()
    override val loggyPath = "${sdCard.absolutePath}/loggy"
    override val logcatPath: String= "$loggyPath/logcat"
    override val analyticsPath: String = "$loggyPath/analytics"



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