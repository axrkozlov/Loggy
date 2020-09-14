package com.clawmarks.loggy.prefs

import com.clawmarks.loggy.LoggyComponent

interface LoggyPrefs : LoggyComponent {

    /** Buffer line count start saving to file    */
    val bufferSize: Int

    /** Buffer line count stop buffering.
     * If buffer length is larger this value, that mean buffer was not written into file*/
    val bufferOverflowSize: Int

    /**
     * Log level
     * 0 - disabled
     * 1 - analytics
     * 2 - logcat on crash
     * 3 - logcat of app
     * 80 - full logcat */
    val logLevel: Int

    /**
     * Sending level
     * 0 - disabled
     * 1 - by server request
     * 2 - deferred
     * 80 - urgent */
    val sendingLevel: Int

    /**
    Period between files start sending if sending is enabled.
    If value is 0 files will be sending every time when new file has been added.
     */
    val sendingIntervalMin: Long

    /**
    Period between files start sending again when a sending error occurred if sending is enabled.
    Uses only with sendingIntervalMin = 0, otherwise will be ignored.
    If value is 0 files will be sending every time when new file has been added.
     */
    val sendingRetryIntervalMin: Long

    /**
     * Time without user activity log sending starts
     */
    val timeForIsIdleMin: Long

    /**
     * Time while sending is permitted
     * value = 0 - permission stops manual with Loggy.stopSendingPermission
     */
    val timeIsSendingPermittedMin: Long

    /**
    Period between a file will be sending after the last one
     */
    val pauseBetweenFileSendingSec: Long

    /**
    Minimal available phone memory for log files.
    If free memory size less than that value buffer won't be saved to file
     */
    val minAvailableMemoryMb: Int

    /**
    Size of logcat buffer must be between 100-8192
     */
    val logcatBufferSizeKb: Int

    /**
    Size of log file.
    Real file can be a bit larger
    cause of additional report info and Outputstreamwriter default buffer
    is 8kb (every 8 kb file flushes to disk)
     */
    val fileSizeKb: Int
    val fileSizeBytes: Int
        get() = fileSizeKb * 1024

    /**
    Size of log directory.
    There is 2 directory: analytics and logcat. For each of them value is the same.
    When new file has been written, eldest file will be removed.
     */
    val dirSizeMb: Int
    val dirSizeBytes: Int
        get() = dirSizeMb * 1024 * 1024

    /** Max log directory size for logcat and analytics, when writing will stopped*/
    val maxDirSizeMb: Int
    val maxDirSizeBytes: Int
        get() = maxDirSizeMb * 1024 * 1024

    /** Path for loggy */
    val loggyPath: String

    /** Path for logcat files*/
    val logcatPath: String
        get() = "$loggyPath/logcat"

    /** Path for analytics files*/
    val analyticsPath: String
        get() = "$loggyPath/analytics"

    /** Extra info fields inside report */
    val extra: Map<String, Any>

    val deviceId: String
    val userId: String

    /**
     * Version must be updated after log file structure changed
     */
    val reportVersion: Int

    /**
     * Override to disable zlib compression
     */
    val isCompressionEnabled: Boolean
        get() = true

    /**
     * Show analytics messages in logcat
     */
    val isDebugMode
        get() = false


}