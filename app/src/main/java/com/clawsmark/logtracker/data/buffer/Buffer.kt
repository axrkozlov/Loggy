package com.clawsmark.logtracker.data.buffer

import com.clawsmark.logtracker.data.writer.BufferWriter
import com.clawsmark.logtracker.data.Message

abstract class Buffer {
    abstract val bufferWriter: BufferWriter
    abstract val size : Int
    abstract val blockSize : Int
    abstract val maxSize:Int
    abstract fun push(message: Message)
    abstract fun pull(): Message?
    val isBufferAvailable : Boolean
    get(){
        if (size > blockSize) save()
        isBufferOverflow = size > maxSize
        return !isBufferOverflow
    }
    fun save(){
        bufferWriter.saveBuffer(this)
    }
    var isBufferOverflow = false
    set (value)  {
        if (value && field!=value) onBufferOverflow()
        field = value
    }
    fun onBufferOverflow(){
//        timber.log.Timber.i("onBufferOverflow")
    }

}