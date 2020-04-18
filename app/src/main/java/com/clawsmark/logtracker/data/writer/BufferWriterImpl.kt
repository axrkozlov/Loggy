package com.clawsmark.logtracker.data.writer

import android.os.Environment
import android.os.StatFs
import android.util.Log
import com.clawsmark.logtracker.data.ReportInfo
import com.clawsmark.logtracker.data.CauseExceptionInfo
import com.clawsmark.logtracker.data.ReportType
import com.clawsmark.logtracker.data.buffer.Buffer
import com.clawsmark.logtracker.loggy.LoggyComponent
import com.clawsmark.logtracker.loggy.LoggyContext
import com.clawsmark.logtracker.utils.currentLogFinalTime
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
    private val tempFileName = "~temp.txt"

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

    init {
        register()
        createDir()
    }

    private fun createDir(){
        try {
            val fDir =File(dir)
            if (!fDir.exists()) fDir.mkdirs()
        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun saveBuffer(buffer: Buffer, causeException: Exception?, isFatal: Boolean) {
        this.causeException = causeException
        this.isFatal = isFatal
        if (hasEnoughMemory) writeBuffer(buffer)
        else context.hasNoEnoughMemory
    }

    private var isWritingInProgress = false
    private fun writeBuffer(buffer: Buffer) {
        if (isWritingInProgress) return
        Log.i("BufferWriterImpl", "writeBuffer: ")
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
        openFile()
        writeMessages(buffer)
        if (causeException != null) finalizeFile()
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

    private var tempFile: File = File(dir, tempFileName)
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
        causeException?.let {
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
        closeFile()
        renameFile()
    }


    private fun renameFile() {
        val name = String.format("%1$2s_%2$2s_%3$2s.log", serialNumber, endTime, reportType)
        val file = File(dir, name)
        tempFile.renameTo(file)
    }

    override fun onPrefsUpdated() {

    }


}