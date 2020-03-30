package com.clawsmark.logtracker.data.services.logfilemanager

import com.clawsmark.logtracker.data.Buffer

interface BufferWritable {
    val successCallback: () -> Unit
    fun write(buffer: Buffer<*>)
    fun notifySuccess()
}