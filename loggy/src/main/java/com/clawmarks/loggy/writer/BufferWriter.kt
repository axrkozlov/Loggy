package com.clawmarks.loggy.writer

import com.clawmarks.loggy.buffer.Buffer

interface BufferWriter {
    fun saveBuffer(buffer: Buffer, causeThrowable: Throwable? = null, isFatal : Boolean = false)
}