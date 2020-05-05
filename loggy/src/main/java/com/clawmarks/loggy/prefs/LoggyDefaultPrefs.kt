package com.clawmarks.loggy.prefs

import android.os.Environment
import java.io.File
import kotlin.math.max
import kotlin.math.min

class LoggyDefaultPrefs : LoggyPrefs {
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

    override val logcatBufferSizeKb: Int
        get() {
            val value = 1000
            var size = max(value, 100)
            size = min(size, 8192)
            return size
        }

    override val fileSizeKb: Int
        get() = 64
    override val dirSizeMb: Int
        get() = 4
    override val maxDirSizeMb: Int
        get() = 8
    private val sdCard: File = Environment.getExternalStorageDirectory()
    override val loggyPath = "${sdCard.absolutePath}/loggy"
    override val extra: Map<String, String>
        get() = emptyMap()
    override val deviceId: String
        get() = "1234567890"
    override val userId: String
        get() = "9876543210"
    override val reportVersion: Int
        get() = 1


}