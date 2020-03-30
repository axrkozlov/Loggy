package com.clawsmark.logtracker.data.services.logfilemanager

import android.os.Environment
import android.util.Log
import com.clawsmark.logtracker.data.Buffer
import com.clawsmark.logtracker.data.LogcatBuffer
import com.clawsmark.logtracker.data.ReportType
import com.clawsmark.logtracker.data.TrackerBuffer
import com.clawsmark.logtracker.locator.LogTrackerServiceLocator
import java.io.*
import java.lang.Exception

object LogFileManager : LogFileDao {



    private val serialNumber: String ="1234567890"

    private val maxBufferStringCount = 1_00
    private val prefs = LogTrackerServiceLocator.trackerPrefs


//    private val job = Job()
//    override val coroutineContext: CoroutineContext
//        get() = job + Dispatchers.IO

    private val sdCard: File = Environment.getExternalStorageDirectory()
    private val trackerLogDir = File(sdCard.absolutePath.toString() + "/logs")
    private val logcatLogDir = File(sdCard.absolutePath.toString() + "/logcat")

    override val trackerLogs = LogFileList(trackerLogDir, 20)
    override val logcatLogs = LogFileList(logcatLogDir, 20)

    private val trackerWriter: BufferWritable = BufferWriter(trackerLogDir, serialNumber, ReportType.ANALYTIC) {
        this.trackerLogs.update()
    }

    private val logcatWriter: BufferWritable = BufferWriter(logcatLogDir, serialNumber, ReportType.REGULAR) {
        this.logcatLogs.update()
    }
    init {
        Log.i("LogFileManager", "LogFileManager: $trackerLogs")

    }
    override fun saveBuffer(buffer: Buffer<*>) {
        when (buffer) {
            is LogcatBuffer -> logcatWriter.write(buffer)
            is TrackerBuffer -> trackerWriter.write(buffer)
        }

    }


}