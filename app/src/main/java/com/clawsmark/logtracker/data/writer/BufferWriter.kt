package com.clawsmark.logtracker.data.writer

import com.clawsmark.logtracker.data.buffer.Buffer

interface BufferWriter {
    fun saveBuffer(buffer: Buffer, causeException: Exception? = null, isFatal : Boolean = false)
}