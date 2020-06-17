package com.clawmarks.loggy.buffer

import android.util.Log
import com.clawmarks.loggy.writer.BufferWriter
import com.clawmarks.loggy.message.Message
import com.clawmarks.loggy.LoggyContextComponent
import com.clawmarks.loggy.context.LoggyContext
import java.util.concurrent.ConcurrentLinkedQueue

class AnalyticsBuffer(override val context: LoggyContext, override val bufferWriter: BufferWriter) : Buffer(), LoggyContextComponent {
    init {
        register()
    }
    private val localBuffer = ConcurrentLinkedQueue<Message>()

    override val size: Int
        get() = localBuffer.size
    override val bufferSize: Int
        get() = prefs.bufferSize
    override val maxSize: Int
        get() = prefs.bufferOverflowSize

    override fun push(message: Message) {
        if (isBufferAvailable) {
            localBuffer.add(message)
            if (prefs.isDebugMode) Log.i("AnalyticsBuffer", "push: ${message.content}")
        }
    }

    override fun pull(): Message? = localBuffer.poll()

    override fun onPrefsUpdated() {

    }


}