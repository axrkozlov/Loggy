package com.clawsmark.logtracker.data.writer

import com.clawsmark.logtracker.data.buffer.Buffer

interface BufferWriter {
    fun saveBuffer(buffer: Buffer, causeThrowable: Throwable? = null, isFatal : Boolean = false)
}