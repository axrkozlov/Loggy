package com.clawsmark.logtracker.data.writer

import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.clawsmark.logtracker.data.report.ReportInfo
import com.clawsmark.logtracker.data.report.CauseExceptionInfo
import com.clawsmark.logtracker.data.report.ReportType
import com.clawsmark.logtracker.data.buffer.Buffer
import com.clawsmark.logtracker.data.filelist.LoggyFileListState
import com.clawsmark.logtracker.data.LoggyComponent
import com.clawsmark.logtracker.data.context.LoggyContext
import com.clawsmark.logtracker.utils.currentLogFinalTime
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception
import java.util.*

class BufferWriterImpl(override val context: LoggyContext, private val reportType: ReportType, val loggyFileListState: LoggyFileListState) : LoggyComponent, BufferWriter {

    override val componentName: String
        get() = super.componentName + reportType

    private val path: String = if (reportType== ReportType.ANALYTIC) prefs.analyticsPath else prefs.logcatPath
    private val dir = File(path)
    private val tempFileName = ".temp.txt"

    private var serialNumber: String = "12345678901"
    private var terminalId: String = "12345678902"

    private var causeThrowable: Throwable? = null
        set(value) {
            exceptionId = if (value != null) UUID.randomUUID()
            else null
            field = value
        }
    private var exceptionId: UUID? = null
    private var isFatal: Boolean = false

    private val hasEnoughMemory: Boolean
        get() = context.hasEnoughMemory

    init {
        register()
        createDir()
    }

    private fun createDir(){
        try {
            if (!dir.exists()) dir.mkdirs()
        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun saveBuffer(buffer: Buffer, causeThrowable: Throwable?, isFatal: Boolean) {
        causeThrowable.toString()
        this.causeThrowable = causeThrowable
        this.isFatal = isFatal
        if (hasEnoughMemory && loggyFileListState.isNotOverflown) writeBuffer(buffer)
    }

    var writingJob : Job? = null
    private var isWritingInProgress = false
    private fun writeBuffer(buffer: Buffer) {
        if (isFatal) {
            writeBufferOnExit(buffer)
            return
        }
        if (isWritingInProgress) return
        Log.i("BufferWriterImpl", "writeBuffer: ")
        isWritingInProgress = true
        val coroutineScope = CoroutineScope(Job())
        writingJob = coroutineScope.launch(Dispatchers.IO) {
            try {
                writeBufferToFile(buffer)
                loggyFileListState.update()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                delay(5000)
                isWritingInProgress = false
            }
        }
    }

    private var isWritingOnExit = false
    private fun writeBufferOnExit(buffer: Buffer){
        writingJob?.cancel()
        isWritingOnExit = true
        try {
            writeBufferToFile(buffer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun writeBufferToFile(buffer: Buffer) {
        closeFile()
        openFile()
        writeMessages(buffer)
        if (causeThrowable != null) finalizeFile()
        closeFile()
    }

    private fun writeStartBlock() {
        val string = "{\n  \"report\": ["
        osw!!.write(string)
    }

    private fun writeMessages(buffer: Buffer) {
        var hasMessages = true
        while (hasMessages) {
            val message = buffer.pull()
            if (message != null) osw?.write("${message.content},\n")
            else hasMessages = false
            checkTempFile()
        }
    }

    private var endTime: String = ""

    private var tempFile: File = File(path, tempFileName)
    private var fOut: FileOutputStream? = null
    private var osw: OutputStreamWriter? = null


    fun createTempFileIfNotExist():Boolean {
        var isFileJustCreated: Boolean =false
        if (!tempFile.exists()) {
            tempFile.createNewFile()
            isFileJustCreated = true
        }
        return isFileJustCreated
    }

    fun openFile() {
        val isFileJustCreated: Boolean = createTempFileIfNotExist()
        fOut = FileOutputStream(tempFile,true)
        osw = OutputStreamWriter(fOut)
        if (isFileJustCreated) writeStartBlock()
    }

    private fun closeFile() {
        if (osw == null) return
        osw!!.flush()
        osw!!.close()
        osw = null
    }

    private fun checkTempFile() {
        if (tempFile.length() > prefs.maxFileSizeBytes) {
            finalizeFile()
        }
        if (osw==null) openFile()
    }

    private fun finalizeFile() {
        endTime = currentLogFinalTime()
        osw!!.write("\"\"],\n")
        var causeExceptionInfo: CauseExceptionInfo? =null
        causeThrowable?.let {
            causeExceptionInfo= CauseExceptionInfo(
                    exceptionId!!,
                    it.toString(),
                    it.stackTrace.toString(),
                    isFatal)
        }
        val reportInfo = ReportInfo(
                reportType,
                serialNumber,
                terminalId,
                endTime,
                causeExceptionInfo
        )
        osw!!.write("\"reportInfo\":${reportInfo.toJson()}")
        osw!!.write("}\n")
        closeFile()
        renameFile()
    }


    private fun renameFile() {
        val name = String.format("%1$2s_%2$2s_%3$2s.log", serialNumber, endTime, reportType)
        val file = File(path, name)
        tempFile.renameTo(file)
    }

    override fun onPrefsUpdated() {
        serialNumber = context.serialNumber
        terminalId = context.terminalId
    }


}