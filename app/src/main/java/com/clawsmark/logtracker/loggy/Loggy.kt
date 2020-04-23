package com.clawsmark.logtracker.loggy

import com.clawsmark.logtracker.data.message.MessageLevel
import com.clawsmark.logtracker.data.message.AnalyticsMessage
import com.clawsmark.logtracker.data.buffer.AnalyticsBuffer
import com.clawsmark.logtracker.data.buffer.LogcatBuffer
import com.clawsmark.logtracker.data.LoggyComponent
import com.clawsmark.logtracker.data.context.LoggyContext
import com.clawsmark.logtracker.data.sender.LoggySender
import com.clawsmark.logtracker.data.userinteraction.UserInteractionObserver
import org.koin.core.KoinComponent
import org.koin.core.get

object Loggy : LoggyComponent, KoinComponent, UserInteractionObserver {
    private var logcatBuffer: LogcatBuffer = get()
    override val context: LoggyContext = get()
    private var analyticsBuffer: AnalyticsBuffer = get()
    private val loggySender: LoggySender = get()

    override fun onInteraction() {
        stopSending()
    }

    override fun onIdle() {
        startSending()
    }

    init {
        register()
    }

    override fun onPrefsUpdated() {
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

    fun log(throwable: Throwable, isFatal: Boolean = false) {
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