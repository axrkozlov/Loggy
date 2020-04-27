package com.clawmarks.logtracker.data.writer

import com.clawmarks.logtracker.data.buffer.Buffer

interface BufferWriter {
    fun saveBuffer(buffer: Buffer, causeThrowable: Throwable? = null, isFatal : Boolean = false)
}