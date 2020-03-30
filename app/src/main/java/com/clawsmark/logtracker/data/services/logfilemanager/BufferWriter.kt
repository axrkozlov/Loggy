package com.clawsmark.logtracker.data.services.logfilemanager

import com.clawsmark.logtracker.data.Buffer
import com.clawsmark.logtracker.data.ReportType
import com.clawsmark.logtracker.utils.currentLogFileNameTime
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class BufferWriter(
        private val dir: File,
        private val serialNumber: String,
        private val reportType: ReportType,
        override val successCallback: () -> Unit) : BufferWritable {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    override fun write(buffer: Buffer<*>) {
        coroutineScope.launch {
            try {
                val currentTime = currentLogFileNameTime()
                val logFile = File(dir, "${serialNumber}_${currentTime}_${reportType}.log")

                val fOut = FileOutputStream(logFile)
                val osw = OutputStreamWriter(fOut)
                for (item in buffer) {
                    osw.write("$item\n")
                }
                osw.flush()
                osw.close()
                notifySuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun notifySuccess() {
        successCallback.invoke()
    }

}