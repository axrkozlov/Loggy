package com.clawsmark.logtracker.data.buffer

import com.clawsmark.logtracker.data.writer.BufferWriter
import com.clawsmark.logtracker.data.Message
import com.clawsmark.logtracker.loggy.LoggyComponent
import com.clawsmark.logtracker.loggy.LoggyContext
import java.util.concurrent.ConcurrentLinkedQueue

class AnalyticsBuffer(override val context: LoggyContext, override val bufferWriter: BufferWriter) : Buffer(), LoggyComponent {
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