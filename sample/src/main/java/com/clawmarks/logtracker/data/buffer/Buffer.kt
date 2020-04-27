package com.clawmarks.logtracker.data.buffer

import android.util.Log
import com.clawmarks.logtracker.data.writer.BufferWriter
import com.clawmarks.logtracker.data.message.Message

abstract class Buffer {
    abstract val bufferWriter: BufferWriter
    abstract val size: Int
    abstract val blockSize: Int
    abstract val maxSize: Int
    abstract fun push(message: Message)
    abstract fun pull(): Message?
    val isBufferAvailable: Boolean
        get() {
            if (size > blockSize) save()
            isBufferOverflow = size > maxSize
            return !isBufferOverflow
        }

    fun save() {
        bufferWriter.saveBuffer(this)
    }

    fun save(throwable: Throwable, isFatal: Boolean = false) {
        bufferWriter.saveBuffer(this, throwable, isFatal)
    }

    var isBufferOverflow = false
        set(value) {
            if (value && field != value) onBufferOverflow()
            field = value
        }

    fun onBufferOverflow() {
        Log.i("Buffer", "onBufferOverflow: ")

    }

}