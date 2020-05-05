package com.clawmarks.logtracker.prefs

import android.content.SharedPreferences
import android.os.Environment
import com.clawmarks.loggy.prefs.LoggyPrefs
import java.io.File
import kotlin.math.max
import kotlin.math.min

class LoggyPrefsImpl() : LoggyPrefs {
    override val bufferSize: Int
        get() = 256
    override val bufferOverflowSize: Int
        get() = 10_000
    override val logLevel: Int
        get() = 3
    override val sendingLevel: Int
        get() = 2
    override val sendingIntervalMin: Long
        get() = 0
    override val pauseBetweenFileSendingSec: Long
        get() = 1

    override val minAvailableMemoryMb: Int
        get() = 200

    override val logcatBufferSizeKb: Int = 8192

    override val fileSizeKb: Int
        get() = 1024
    override val dirSizeMb: Int
        get() = 4
    override val maxDirSizeMb: Int
        get() = 8
    private val sdCard: File = Environment.getExternalStorageDirectory()
    override val loggyPath = "${sdCard.absolutePath}/loggy"



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