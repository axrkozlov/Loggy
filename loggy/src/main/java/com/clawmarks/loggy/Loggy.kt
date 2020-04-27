package com.clawmarks.loggy

import com.clawmarks.loggy.message.MessageLevel
import com.clawmarks.loggy.message.AnalyticsMessage
import com.clawmarks.loggy.buffer.AnalyticsBuffer
import com.clawmarks.loggy.buffer.LogcatBuffer
import com.clawmarks.loggy.context.LoggyContext
import com.clawmarks.loggy.prefs.LoggyDefaultPrefs
import com.clawmarks.loggy.prefs.LoggyPrefs
import com.clawmarks.loggy.sender.LoggySender
import com.clawmarks.loggy.uploader.LoggyDefaultUploader
import com.clawmarks.loggy.uploader.LoggyUploader
import org.koin.core.KoinComponent
import org.koin.core.get

object Loggy : KoinComponent {
    internal var loggyUploader: LoggyUploader = get()
    internal var loggyPrefs: LoggyPrefs = get()
    private var logcatBuffer: LogcatBuffer = get()
    val context: LoggyContext = get()
    private var analyticsBuffer: AnalyticsBuffer = get()
    private val loggySender: LoggySender = get()



    fun setComponents(vararg components: KoinComponent) {
        components.forEach {
            when (it) {
                is LoggyPrefs -> loggyPrefs = it
                is LoggyUploader -> loggyUploader = it
            }
        }
    }

    fun setUploader() {

    }

    fun setPrefs() {

    }

    fun log(tag: String, message: String, messageLevel: MessageLevel) {
        if (context.isAnalyticsEnabled) analyticsBuffer.push(AnalyticsMessage(tag, message, messageLevel))
    }

    fun i(tag: String, message: String) {
        if (context.isAnalyticsEnabled) analyticsBuffer.push(AnalyticsMessage(tag, message, MessageLevel.INFO))
    }

    fun w(tag: String, message: String) {
        if (context.isAnalyticsEnabled) analyticsBuffer.push(AnalyticsMessage(tag, message, MessageLevel.WARNING))
    }

    fun e(tag: String, message: String) {
        if (context.isAnalyticsEnabled) analyticsBuffer.push(AnalyticsMessage(tag, message, MessageLevel.ERROR))
    }

    fun dump(throwable: Throwable, isFatal: Boolean = false) {
        if (context.isAnalyticsEnabled) analyticsBuffer.save(throwable, isFatal)
        if (context.isLogcatCrashEnabled) logcatBuffer.save(throwable, isFatal)
    }

    fun updatePrefs() = context.updatePrefs()

    fun startSending(isUrgentlySendingRequest: Boolean = false) {
        if (context.isSendingEnabled) {
            loggySender.startSending(isUrgentlySendingRequest)
        }
    }

    fun stopSending(isForce: Boolean = false) {
        loggySender.stopSending(isForce)
    }

}