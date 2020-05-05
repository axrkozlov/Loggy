package com.clawmarks.loggy.buffer

import com.clawmarks.loggy.writer.BufferWriter
import com.clawmarks.loggy.message.Message
import com.clawmarks.loggy.LoggyComponent
import com.clawmarks.loggy.context.LoggyContext
import java.util.concurrent.ConcurrentLinkedQueue

class AnalyticsBuffer(override val context: LoggyContext, override val bufferWriter: BufferWriter) : Buffer(), LoggyComponent {
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
        if (isBufferAvailable) localBuffer.add(message)
    }

    override fun pull(): Message? = localBuffer.poll()

    override fun onPrefsUpdated() {

    }


}