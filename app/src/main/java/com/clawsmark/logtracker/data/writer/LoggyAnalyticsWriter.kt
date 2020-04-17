package com.clawsmark.logtracker.data.writer

import android.os.Environment
import android.os.StatFs
import com.clawsmark.logtracker.data.AnalyticsMessage
import com.clawsmark.logtracker.data.buffer.Buffer
import com.clawsmark.logtracker.loggy.LoggyComponent
import com.clawsmark.logtracker.loggy.LoggyContext
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception

class LoggyAnalyticsWriter(override val context: LoggyContext) : LoggyComponent, BufferWriter {

    private val dir: String = context.prefs.analyticsPath
    private val serialNumber: String = context.prefs.serialNumber
    val fileNameFormat: String = context.prefs.analyticsFileNameFormat
    private val tempFileName = "~temp.log"

    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())


    private val hasEnoughMemory: Boolean
        get() = StatFs(Environment.getExternalStorageDirectory().path).availableBytes > context.minAvailableMemoryBytes


    override fun saveBuffer(buffer: Buffer) {
        if (hasEnoughMemory) writeBuffer(buffer)
        else context.hasNoEnoughMemory
    }

    private var isWritingInProgress = false
    private fun writeBuffer(buffer: Buffer) {
        if (isWritingInProgress) return
        isWritingInProgress = true
        val coroutineScope = CoroutineScope(Job())
        coroutineScope.launch(Dispatchers.IO) {
            try {
                checkTempFile()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                delay(5000)
                isWritingInProgress = false
            }
        }
    }

    fun checkTempFile() {
        if (tempFile == null) {
            createTempFile()
            return
        }
        val size = tempFile!!.length()
    }

    private var tempFile: File? = null
    private var fOut: FileOutputStream? = null
    private var osw: OutputStreamWriter? = null

    fun createTempFile() {
        tempFile = File(dir, tempFileName)
        tempFile!!.createNewFile()
    }

    fun openFile() {
        fOut = FileOutputStream(tempFile)
        osw = OutputStreamWriter(fOut)
    }

    fun closeFile() {
        osw?.flush()
        osw?.close()
    }

    private fun writeMessages(buffer: Buffer) {
        var hasMessages = true
        while (hasMessages) {
            val message = buffer.pull()
            if (message != null) osw?.write("${message.content}\n")
            else hasMessages = false
        }
    }


    fun saveFile() {

    }


    fun renameFile() {

    }

    fun write(message: AnalyticsMessage) {
//        if (isActive) buffer.push(message)
//
//        coroutineScope.launch {
//            try {
//                val currentTime = currentLogFileNameTime()
//                val logFile = File(dir, "${serialNumber}_${currentTime}_${reportType}.log")
//
//                val fOut = FileOutputStream(logFile)
//                val osw = OutputStreamWriter(fOut)
//                for (item in buffer) {
//                    osw.write("$item\n")
//                }
//                osw.flush()
//                osw.close()
//                notifySuccess()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }

    var writeJob: Job? = null
    val isActive: Boolean = writeJob?.isActive ?: false

    private fun writeContinuously() {
        writeJob = coroutineScope.launch {
            while (isActive) {
                writeBufferToFile()
            }
        }
    }

    fun save() {

    }

    fun start() {
        writeContinuously()
    }

    fun stop() {
        writeJob?.cancel()
    }


    fun writeBufferToFile() {
//        checkTempFile()
//        if (buffer.isEmpty()) return
//        try {
//            val string = buffer.poll()
//            osw!!.write("$string\n")
//        } catch (e: Exception) {
//            e.printStackTrace()
//            onFail()
//        }
    }


    override fun onPrefsUpdated() {

    }


}