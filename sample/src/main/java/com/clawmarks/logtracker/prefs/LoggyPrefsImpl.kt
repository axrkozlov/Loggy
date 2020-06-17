package com.clawmarks.logtracker.prefs

import android.os.Environment
import com.clawmarks.loggy.prefs.LoggyPrefs
import java.io.File

class LoggyPrefsImpl : LoggyPrefs {

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
    override val sendingRetryIntervalMin: Long
        get() = 1

    override val timeForIsIdleMin: Long
        get() = 1
    override val timeIsSendingPermittedMin: Long
        get() = 10

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

    override val extra: Map<String, Any>
        get() = mapOf("someField" to "somevalue")

    override val deviceId: String
        get() = "12341234"

    override val userId: String
        get() = "87658765"

    override val reportVersion: Int
        get() = 1

}