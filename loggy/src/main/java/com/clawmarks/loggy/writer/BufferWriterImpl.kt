package com.clawmarks.loggy.writer

import android.util.Log
import com.clawmarks.loggy.LoggyContextComponent
import com.clawmarks.loggy.buffer.Buffer
import com.clawmarks.loggy.context.LoggyContext
import com.clawmarks.loggy.filelist.LoggyFileListState
import com.clawmarks.loggy.report.CauseExceptionInfo
import com.clawmarks.loggy.report.ReportInfo
import com.clawmarks.loggy.report.ReportType
import com.clawmarks.loggy.utils.currentLogFinalTime
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream

class BufferWriterImpl(override val context: LoggyContext, private val reportType: ReportType, val loggyFileListState: LoggyFileListState) : LoggyContextComponent, BufferWriter {

    override val componentName: String
        get() = super.componentName + reportType

    private val path: String = if (reportType == ReportType.ANALYTIC) prefs.analyticsPath else prefs.logcatPath
    private val dir = File(path)
    //TODO:remove.txt
    private val tempFileName = ".temp.txt"
    private val tempCompressFileName = ".tempzlib.txt"

    private var reportVersion: Int = 0
    private var deviceId: String = ""
    private var userId: String = ""

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

    private var endTime: String = ""

    private var tempFile: File = File(path, tempFileName)
    private var fOut: FileOutputStream? = null
    private var osw: OutputStreamWriter? = null

    init {
        register()
        createDir()
    }

    private fun createDir() {
        try {
            if (!dir.exists()) dir.mkdirs()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun saveBuffer(buffer: Buffer, causeThrowable: Throwable?, isFatal: Boolean) {
        causeThrowable.toString()
        this.causeThrowable = causeThrowable
        this.isFatal = isFatal
        if (hasEnoughMemory && loggyFileListState.isNotOverflown) writeBuffer(buffer)
    }

    var writingJob: Job? = null
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
    private fun writeBufferOnExit(buffer: Buffer) {
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

    private fun createTempFileIfNotExist(): Boolean {
        var isFileJustCreated = false
        if (!tempFile.exists()) {
            tempFile.createNewFile()
            isFileJustCreated = true
        }
        return isFileJustCreated
    }

    private fun openFile() {
        val isFileJustCreated: Boolean = createTempFileIfNotExist()
        fOut = FileOutputStream(tempFile, true)
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
        if (osw == null) openFile()
    }

    private fun finalizeFile() {
        endTime = currentLogFinalTime()
        osw!!.write("\"\"],\n")
        var causeExceptionInfo: CauseExceptionInfo? = null
        causeThrowable?.let {
            causeExceptionInfo = CauseExceptionInfo(
                    exceptionId!!,
                    it.toString(),
                    it.stackTrace.toString(),
                    isFatal)
        }
        val reportInfo = ReportInfo(
                reportVersion,
                reportType,
                deviceId,
                userId,
                endTime,
                causeExceptionInfo,
                prefs.extra
        )
        osw!!.write("\"reportInfo\":${reportInfo.toJson()}")
        osw!!.write("}\n")
        closeFile()
        val name = String.format("%1$2s_%2$2s_%3$2s.${context.fileExtension}", deviceId, endTime, reportType)
        if (prefs.isCompressionEnabled) {
            compressFile(tempFile,name)
        } else {
            nameFile(tempFile, name)
        }
    }

    private fun compressFile(finalTempFile: File, name: String){
        val fileMusBeCompressed = File(path, tempCompressFileName)
        finalTempFile.renameTo(fileMusBeCompressed)
        val compressedFile = File(path, name)
        val compresser = Deflater(Deflater.DEFAULT_COMPRESSION)
        val fOut = DeflaterOutputStream(FileOutputStream(compressedFile),compresser)
        val bytes = fileMusBeCompressed.readBytes()
        fOut.write(bytes,0,bytes.size)
        fOut.finish()
        fOut.close()
        compresser.end()
        fileMusBeCompressed.delete()
    }

    private fun nameFile(finalTempFile: File, name: String):File {
        val file = File(path, name)
        finalTempFile.renameTo(file)
        return file
    }

    override fun onPrefsUpdated() {
        deviceId = prefs.deviceId
        userId = prefs.userId
        reportVersion = prefs.reportVersion
    }


}