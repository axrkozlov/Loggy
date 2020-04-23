package com.clawsmark.logtracker.data.buffer

import com.clawsmark.logtracker.data.writer.BufferWriter
import com.clawsmark.logtracker.data.message.Message
import com.clawsmark.logtracker.data.LoggyComponent
import com.clawsmark.logtracker.data.context.LoggyContext
import java.util.concurrent.ConcurrentLinkedQueue

class AnalyticsBuffer(override val context: LoggyContext, override val bufferWriter: BufferWriter) : Buffer(), LoggyComponent {
    init {
        register()
    }
    private val localBuffer = ConcurrentLinkedQueue<Message>()

    override val size: Int
        get() = localBuffer.size
    override val blockSize: Int
        get() = prefs.bufferBlockSize
    override val maxSize: Int
        get() = prefs.maxBufferSize

    override fun push(message: Message) {
        if (isBufferAvailable) localBuffer.add(message)
    }

    override fun pull(): Message? = localBuffer.poll()

    override fun onPrefsUpdated() {

    }


}