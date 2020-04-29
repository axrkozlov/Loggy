package com.clawmarks.loggy

import android.content.Context
import com.clawmarks.loggy.message.MessageLevel
import com.clawmarks.loggy.message.AnalyticsMessage
import com.clawmarks.loggy.buffer.AnalyticsBuffer
import com.clawmarks.loggy.buffer.LogcatBuffer
import com.clawmarks.loggy.context.LoggyContext
import com.clawmarks.loggy.di.loggyModule
import com.clawmarks.loggy.prefs.LoggyPrefs
import com.clawmarks.loggy.sender.LoggySender
import com.clawmarks.loggy.uploader.LoggyUploader
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.*
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.dsl.koinApplication
import java.lang.Exception

class Loggy internal constructor(): LoggyKoinComponent {


    private val context: LoggyContext by inject()
    private val loggySender: LoggySender by inject()
    private val analyticsBuffer: AnalyticsBuffer by inject()
    private val logcatBuffer: LogcatBuffer by inject()




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
        if (context.isSendingEnabled) loggySender.startSending(isUrgentlySendingRequest)
    }

    fun stopSending(isForce: Boolean = false) {
        loggySender.stopSending(isForce)
    }

    private object HOLDER : KoinComponent {
        var isInitialied = false
        val INSTANCE: Loggy = Loggy()
    }

    companion object {

        fun start( vararg components: KoinComponent) {


            components.forEach {
                when (it) {
                    is LoggyPrefs -> instance.context.prefs = it
                    is LoggyUploader -> instance.loggySender.loggyUploader = it
                }
            }
            HOLDER.isInitialied = true
        }

        private val instance: Loggy by lazy { HOLDER.INSTANCE }

        fun log(tag: String, message: String, messageLevel: MessageLevel) {
            instance.log(tag, message, messageLevel)
        }

        fun i(tag: String, message: String) {
            instance.log(tag, message, MessageLevel.INFO)
        }

        fun w(tag: String, message: String) {
            instance.log(tag, message, MessageLevel.WARNING)
        }

        fun e(tag: String, message: String) {
            instance.log(tag, message, MessageLevel.ERROR)
        }

        fun dump(throwable: Throwable, isFatal: Boolean = false) {
            instance.dump(throwable,isFatal)
        }

        fun updatePrefs() = instance.updatePrefs()

        fun startSending(isUrgentlySendingRequest: Boolean = false) {
            instance.startSending(isUrgentlySendingRequest)
        }

        fun stopSending(isForce: Boolean = false) {
            instance.stopSending(isForce)
        }
    }
}
object MyKoinContext {
    val koinApp : KoinApplication = koinApplication {
        modules(loggyModule)
    }
}
interface LoggyKoinComponent : KoinComponent {
    // Override default Koin instance, intially target on GlobalContext to yours
    override fun getKoin(): Koin = MyKoinContext.koinApp.koin
}