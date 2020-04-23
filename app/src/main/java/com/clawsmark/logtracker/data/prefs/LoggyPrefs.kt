package com.clawsmark.logtracker.data.prefs

interface LoggyPrefs {

    val bufferBlockSize: Int
    val maxBufferSize: Int

    val logLevel: Int
    val sendingLevel: Int

    /**
     Period between files start sending if sending is enabled.
     If value is 0 files will be sending every time when new file has been added.
     */
    val sendingIntervalMin:Long
    val pauseBetweenFileSendingSec :Long

    val timeBeforeStartSendingSeconds: Int
    val minAvialableMemoryMb: Int

    val logcatBufferSizeKb: Int
    val logcatMinBufferSizeKb: Int
    val logcatMaxBufferSizeKb: Int

    val maxFileSizeKb: Int
    val maxFileSizeBytes: Int
        get() = maxFileSizeKb * 1024

    val dirSizeMb: Int
    val dirSizeBytes: Int
        get() = dirSizeMb * 1024 * 1024

    /**
    Max log dir size for logcat and analytics, when writing will be stopped
     */
    val maxDirSizeMb: Int
    val maxDirSizeBytes: Int
        get() = maxDirSizeMb * 1024 * 1024


    val logcatPath: String
    val analyticsPath: String
    val loggyPath: String


}