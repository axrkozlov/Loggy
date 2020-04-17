package com.clawsmark.logtracker.data.writer

import android.os.Environment
import android.os.StatFs
import com.clawsmark.logtracker.data.ReportInfo
import com.clawsmark.logtracker.data.CauseExceptionInfo
import com.clawsmark.logtracker.data.ReportType
import com.clawsmark.logtracker.data.buffer.Buffer
import com.clawsmark.logtracker.loggy.LoggyComponent
import com.clawsmark.logtracker.loggy.LoggyContext
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.lang.Exception
import java.util.*

class BufferWriterImpl(override val context: LoggyContext, private val reportType: ReportType) : LoggyComponent, BufferWriter {

    private val dir: String = if (reportType==ReportType.ANALYTIC) prefs.analyticsPath else prefs.logcatPath
    private val serialNumber: String = prefs.serialNumber
    private val terminalId: String = prefs.terminalId
    private val tempFileName = "~temp.log"

    private var causeException: Exception? = null
        set(value) {
            exceptionId = if (value != null) UUID.randomUUID()
            else null
            field = value
        }
    private var exceptionId: UUID? = null
    private var isFatal: Boolean = false

    private val hasEnoughMemory: Boolean
        get() = StatFs(Environment.getExternalStorageDirectory().path).availableBytes > context.minAvailableMemoryBytes


    override fun saveBuffer(buffer: Buffer, causeException: Exception?, isFatal: Boolean) {
        this.causeException = causeException
        this.isFatal = isFatal
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
                writeBufferToFile(buffer)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                delay(5000)
                isWritingInProgress = false
            }
        }
    }


    fun writeBufferToFile(buffer: Buffer) {
        if (tempFile == null || !tempFile!!.exists()) {
            createTempFile()
        }
        openFile()
        if (isFileJustCreated) writeStartBlock()
        writeMessages(buffer)
        if (causeException != null) finalizeFile()
        closeFile()
    }

    private fun writeStartBlock() {
        val string = "{\n  \"report\": ["
        osw!!.write(string)
        isFileJustCreated = false
    }

    private fun writeMessages(buffer: Buffer) {
        var hasMessages = true
        while (hasMessages) {
            val message = buffer.pull()
            if (message != null) osw?.write("${message.content},\n")
            else hasMessages = false
            checkFileSize()
        }
    }

    private var endTime: String = ""

    private fun writeEndBlock() {
        osw!!.write("\"\"],\n")
        var causeExceptionInfo: CauseExceptionInfo?
        causeException.let {
            causeExceptionInfo=CauseExceptionInfo(
                    causeException!!.toString(),
                    exceptionId!!,
                    isFatal)
        }
        val reportInfo = ReportInfo(
                reportType,
                serialNumber,
                terminalId,
                endTime,
                causeExceptionInfo
        )
        osw!!.write(reportInfo.toJson())
        osw!!.write("}")
//        string.append("\"serialNumber\": \"$serialNumber\"\n")
//        string.append("\"terminalId\": \"$terminalId\"\n")
//        endTime = currentLogFileNameTime()
//        string.append("\"time\": \"$endTime\"\n")
//        string.append("\"causeException\": {\n")
//        string.append("\"exception\": \"$causeException\",\n")
//        string.append("\"id\": \"$exceptionId\",\n")
//        string.append("\"isFatal\": \"$isFatal\"}\n")

    }

    private var tempFile: File? = null
    private var fOut: FileOutputStream? = null
    private var osw: OutputStreamWriter? = null
    private var isFileJustCreated = false

    fun createTempFile() {
        tempFile = File(dir, tempFileName)
        tempFile!!.createNewFile()
    }

    fun openFile() {
        fOut = FileOutputStream(tempFile)
        osw = OutputStreamWriter(fOut)
    }

    fun closeFile() {
        if (osw == null) return
        osw?.flush()
        osw?.close()
        osw = null
    }


    private fun checkFileSize() {
        if (tempFile!!.length() > prefs.maxFileSizeKb) {
            finalizeFile()
        }
    }

    private fun finalizeFile() {
        writeEndBlock()
        closeFile()
        renameFile()
        createTempFile()
    }


    private fun renameFile() {
        val name = String.format("%1$2s_%2$2s_%3$2s.log", serialNumber, endTime, reportType)
        val file = File(dir, name)
        tempFile?.renameTo(file)
    }

    override fun onPrefsUpdated() {

    }


}